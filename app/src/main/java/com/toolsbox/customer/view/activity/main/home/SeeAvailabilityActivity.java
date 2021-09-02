package com.toolsbox.customer.view.activity.main.home;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jaeger.library.StatusBarUtil;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.toolsbox.customer.R;
import com.toolsbox.customer.common.model.AvailabilityDateInfo;
import com.toolsbox.customer.common.model.FromToDateInfo;
import com.toolsbox.customer.common.utils.GlobalUtils;
import com.toolsbox.customer.common.utils.TimeHelper;
import com.toolsbox.customer.view.activity.basic.BaseActivity;
import com.toolsbox.customer.view.adapter.AvailabilityAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.toolsbox.customer.common.utils.TimeHelper.checkSameDate;
import static com.toolsbox.customer.common.utils.TimeHelper.checkSameDay;

public class SeeAvailabilityActivity extends BaseActivity {
    private static String TAG = "SeeAvailabilityActivity";

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private AvailabilityAdapter adapter;
    private Boolean isEditable;
    private ArrayList<AvailabilityDateInfo> arrDates = new ArrayList<>();
    private TextView tvDescription;
    private static final int REQUEST_CODE_CALENDAR_SELECT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_availability);
        initVariable();
        initUI();
    }

    void initVariable(){
        isEditable = getIntent().getBooleanExtra("isEditable", true);
        String strArrDate = getIntent().getStringExtra("arrDates");
        Gson gson = new Gson();
        arrDates = gson.fromJson(strArrDate, new TypeToken<ArrayList<AvailabilityDateInfo>>(){}.getType());
    }

    void initUI(){
        StatusBarUtil.setTranslucent(this, 0);
        setupToolbar();
        tvDescription = findViewById(R.id.tv_description);
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new AvailabilityAdapter(this, arrDates, isEditable);
        adapter.addListener(new AvailabilityAdapter.OnItemClickListener() {
            @Override
            public void onItemRemoveClick(AvailabilityDateInfo item) {
                arrDates.remove(item);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onItemAddClick() {
                if (arrDates.size() < 3) {
                    Intent intent = new Intent(SeeAvailabilityActivity.this, CalendarActivity.class);
                    Gson gson = new Gson();
                    String strSelectedDates = gson.toJson(arrDates);
                    intent.putExtra("selectedDates", strSelectedDates);
                    startActivityForResult(intent, REQUEST_CODE_CALENDAR_SELECT);
                }

            }
        });
        recyclerView.setAdapter(adapter);
        if (!isEditable) {
            tvDescription.setVisibility(View.GONE);
        }
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_save:
                if (arrDates.size() > 0 && isEditable) {
                    Gson gson = new Gson();
                    String strDates = gson.toJson(arrDates);
                    Intent intent = new Intent();
                    intent.putExtra("selectedDates", strDates);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                } else {
                    finish();
                }
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_availability, menu);
        if (!isEditable){
            final MenuItem saveItemView = menu.findItem(R.id.action_save);
            saveItemView.setVisible(false);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_CALENDAR_SELECT:
                    String tempDate = data.getStringExtra("selectedDate");
                    String tempTimes = data.getStringExtra("arrTime");
                    Gson gson = new Gson();
                    CalendarDay cDay = gson.fromJson(tempDate, new TypeToken<CalendarDay>(){}.getType());
                    List<String> arrTimes = gson.fromJson(tempTimes, new TypeToken<List<String>>(){}.getType());

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

                    boolean isSameDate = false;
                    for (AvailabilityDateInfo item : this.arrDates) {
                        if (checkSameDay(cDay.getDate(), item.timeStamp.get(0).fromDate)) {
                            if (!checkSameDate(arrStamp.get(0).fromDate, item.timeStamp.get(0).fromDate)) {
                                item.timeStamp.add(arrStamp.get(0));
                                isSameDate = true;
                            }
                        }
                    }
                    if (!isSameDate) {
                        arrDates.add(newDate);
                    }
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    }

}
