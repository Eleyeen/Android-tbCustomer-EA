package com.toolsbox.customer.view.activity.main.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jaeger.library.StatusBarUtil;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.toolsbox.customer.R;
import com.toolsbox.customer.common.model.AvailabilityDateInfo;
import com.toolsbox.customer.common.model.FromToDateInfo;
import com.toolsbox.customer.common.model.TimePreferItemInfo;
import com.toolsbox.customer.common.utils.GlobalUtils;
import com.toolsbox.customer.common.utils.TimeHelper;
import com.toolsbox.customer.view.activity.basic.BaseActivity;
import com.toolsbox.customer.view.adapter.TimeSlotAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.toolsbox.customer.common.Constant.DATE_FORMAT_DEFAULT;
import static com.toolsbox.customer.common.utils.TimeHelper.checkSameDate;
import static com.toolsbox.customer.common.utils.TimeHelper.checkSameDay;

public class CalendarActivity extends BaseActivity {
    private static String TAG = "CalendarActivity";

    private Toolbar toolbar;
    private MaterialCalendarView calendarView;
    private RecyclerView recyclerView;
    private GridLayoutManager layoutManager;
    private TimeSlotAdapter adapter;
    private List<CalendarDay> selectedDates;
    private ArrayList<TimePreferItemInfo> arrTimePrefer = new ArrayList<>();

    private ArrayList<AvailabilityDateInfo> arrDates = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        initVariable();
        initUI();
    }

    void initVariable(){
        String strSelectedDates = getIntent().getStringExtra("selectedDates");
        Gson gson = new Gson();
        arrDates = gson.fromJson(strSelectedDates, new TypeToken<ArrayList<AvailabilityDateInfo>>(){}.getType());
    }

    void initUI(){
        StatusBarUtil.setTranslucent(this, 0);
        setupToolbar();
        calendarView = findViewById(R.id.calendar_view);
        calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_SINGLE);
        // customize calendar
        calendarView.state().edit()
                .setMinimumDate(new Date())
                .commit();
        recyclerView = findViewById(R.id.recycler);
        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        arrTimePrefer.add(new TimePreferItemInfo("8:00 AM - 10:00 AM", false));
        arrTimePrefer.add(new TimePreferItemInfo("10:00 AM - 12:00 PM", false));
        arrTimePrefer.add(new TimePreferItemInfo("12:00 PM - 2:00 PM", false));
        arrTimePrefer.add(new TimePreferItemInfo("2:00 PM - 4:00 PM", false));
        arrTimePrefer.add(new TimePreferItemInfo("4:00 PM - 6:00 PM", false));
        arrTimePrefer.add(new TimePreferItemInfo("6:00 PM - 8:00 PM", false));
        arrTimePrefer.add(new TimePreferItemInfo("8:00 PM - 12:00 AM", false));
        arrTimePrefer.add(new TimePreferItemInfo("12:00 AM - 8:00 AM", false));

        adapter = new TimeSlotAdapter(this, arrTimePrefer);
        recyclerView.setAdapter(adapter);

    }

    void setupToolbar(){
        toolbar =  findViewById(R.id.toolbar);
        TextView tvToolbar = toolbar.findViewById(R.id.toolbar_title);
        tvToolbar.setText(R.string.availability);
        setSupportActionBar(toolbar);
        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        } catch (Exception e) {
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_select_industry, menu);
        return true;
    }

    List<String> getSelectedItems() {
        List<String> dates = new ArrayList<>();
        for (TimePreferItemInfo item : arrTimePrefer) {
            if (item.isSelected) {
                dates.add(item.title);
            }
        }
        return dates;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_done:
                selectedDates = calendarView.getSelectedDates();
                if (selectedDates.size() < 1 || getSelectedItems().size() < 1) {
                    Toast.makeText(this, "Please select an availability date and a time availability", Toast.LENGTH_LONG).show();
                } else {
                    if (isAvailableTimeStamp()){
                        Gson gson = new Gson();
                        String strDate = gson.toJson(selectedDates.get(0));
                        String strTimes = gson.toJson(getSelectedItems());
                        Intent intent = new Intent();
                        setResult(Activity.RESULT_OK, intent);
                        intent.putExtra("selectedDate", strDate);
                        intent.putExtra("arrTime", strTimes);
                        finish();
                    }
                }
        }
        return super.onOptionsItemSelected(item);
    }




    boolean isAvailableTimeStamp(){
        Gson gson = new Gson();
        CalendarDay cDay = selectedDates.get(0);
        List<String> arrTimes = getSelectedItems();

        ArrayList<FromToDateInfo> arrStamp = new ArrayList<>();
        for (String time : arrTimes) {
            int[] arrHour = GlobalUtils.getHourFromTimeStamp(time);
            Date startTime = TimeHelper.setTime(cDay.getDate(), arrHour[0], 0, 0);
            Date endTime;
            if (arrHour[1] == 24) {
                Date tomorrow = TimeHelper.getTomorrow(cDay.getDate());
                endTime = TimeHelper.setTime(tomorrow, 0, 0, 0);
            } else {
                endTime = TimeHelper.setTime(cDay.getDate(), arrHour[1], 0, 0);
            }
            arrStamp.add(new FromToDateInfo(startTime, endTime));
        }

        AvailabilityDateInfo newDate = new AvailabilityDateInfo(arrStamp);
        // Check if time availability is bigger than current time
        boolean isCompareCurrentTime = true;
        for (FromToDateInfo item : newDate.timeStamp){
            Calendar cal = Calendar.getInstance();
            cal.setTime(item.toDate);
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            // Check emergency timestamp
            if (hour == 0 || hour == 8) {
                if (new Date().after(item.toDate)){
                    isCompareCurrentTime = false;
                }
            } else {
                if (new Date().after(item.fromDate)){
                    isCompareCurrentTime = false;
                }
            }
        }
        if (!isCompareCurrentTime) {
            Toast.makeText(this, "Please select a future time availability!", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            for (AvailabilityDateInfo item : this.arrDates) {
                if (checkSameDay(cDay.getDate(), item.timeStamp.get(0).fromDate)) {
                    // Check if there are already 2 dates choose.
                    if (item.timeStamp.size() > 1) {
                        Toast.makeText(this, "You already chose 2 time availabilities for this date.", Toast.LENGTH_SHORT).show();
                        return false;
                    } else {
                        if (arrStamp.size() > 1) {
                            Toast.makeText(this, "You can select 1 time availability in this date", Toast.LENGTH_SHORT).show();
                            return false;
                        } else {
                            if (checkSameDate(arrStamp.get(0).fromDate, item.timeStamp.get(0).fromDate)) {
                                Toast.makeText(this, "This time availability already exists, Please choose another time", Toast.LENGTH_SHORT).show();
                                return false;
                            } else {
                                item.timeStamp.add(arrStamp.get(0));
                                return true;
                            }
                        }
                    }

                }
            }
            return true;
        }
    }
}
