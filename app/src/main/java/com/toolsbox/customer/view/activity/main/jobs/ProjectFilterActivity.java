package com.toolsbox.customer.view.activity.main.jobs;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;

import com.toolsbox.customer.R;
import com.toolsbox.customer.view.activity.basic.BaseActivity;

import static com.toolsbox.customer.common.Constant.gArrSpecialization;

public class ProjectFilterActivity extends BaseActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private CardView cvFilterResult;
    private AppCompatSpinner spProjectType, spProjectStatus, spDuration;
    ArrayAdapter<String> projectTypeAdapter, projectStatusAdapter, durationAdapter;

    private  String[] arrProjectStatus = {"In bidding process", "Complete", "In progress"};
    private  String[] arrDuration = {"1 day - 1 week", "1 week - 1 month", "1 month - 3 months",
            "3 months - 6 months", "6 months - 1 year", "more than 1 year"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_filter);
        initVariable();
        initUI();
    }

    void initVariable(){

    }

    void initUI(){
        setupToolbar();
        projectTypeAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_dropdown_item , gArrSpecialization);
        projectTypeAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        projectStatusAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_dropdown_item ,arrProjectStatus);
        projectStatusAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        durationAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_dropdown_item ,arrDuration);
        durationAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

        spProjectType = findViewById(R.id.sp_project_type);
        spProjectType.setAdapter(projectTypeAdapter);
        spProjectStatus = findViewById(R.id.sp_project_status);
        spProjectStatus.setAdapter(projectStatusAdapter);
        spDuration = findViewById(R.id.sp_duration);
        spDuration.setAdapter(durationAdapter);

        cvFilterResult = findViewById(R.id.cv_filter_result);
        cvFilterResult.setOnClickListener(this);


    }

    void setupToolbar(){
        toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Filter Projects");
        } catch (Exception e) {
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.cv_filter_result:
                finish();
                break;
        }
    }
}
