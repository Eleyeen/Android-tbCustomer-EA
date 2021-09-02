package com.toolsbox.customer.view.activity.main.home;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jaeger.library.StatusBarUtil;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.toolsbox.customer.R;
import com.toolsbox.customer.common.Constant;
import com.toolsbox.customer.common.interFace.Notify;
import com.toolsbox.customer.common.model.AttachFileInfo;
import com.toolsbox.customer.common.model.AvailabilityDateInfo;
import com.toolsbox.customer.common.model.ContractorInfo;
import com.toolsbox.customer.common.model.FromToDateInfo;
import com.toolsbox.customer.common.model.JobInfo;
import com.toolsbox.customer.common.model.ServiceItem;
import com.toolsbox.customer.common.model.api.AttachData;
import com.toolsbox.customer.common.model.api.GeneralData;
import com.toolsbox.customer.common.utils.ApiUtils;
import com.toolsbox.customer.common.utils.AppPreferenceManager;
import com.toolsbox.customer.common.utils.FileUtils;
import com.toolsbox.customer.common.utils.GlobalUtils;
import com.toolsbox.customer.common.utils.MessageUtils;
import com.toolsbox.customer.common.utils.PreferenceHelper;
import com.toolsbox.customer.common.utils.StringHelper;
import com.toolsbox.customer.view.activity.basic.BaseActivity;
import com.toolsbox.customer.view.activity.login.EnterActivity;
import com.toolsbox.customer.view.activity.login.LoginActivity;
import com.toolsbox.customer.view.activity.main.HomeActivity;
import com.toolsbox.customer.view.adapter.JobPostAdapter;
import com.toolsbox.customer.view.adapter.PlaceArrayAdapter;
import com.toolsbox.customer.view.customUI.ConfirmDialog;
import com.toolsbox.customer.view.customUI.IconEditText;


import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static com.toolsbox.customer.common.Constant.DATE_FORMAT_AVAILABILITY;
import static com.toolsbox.customer.common.Constant.FRAG_JOBS;
import static com.toolsbox.customer.common.Constant.FROM_JOB_EDIT;
import static com.toolsbox.customer.common.Constant.FROM_JOB_HIRE;
import static com.toolsbox.customer.common.Constant.FROM_JOB_POST;
import static com.toolsbox.customer.common.Constant.gArrSpecialization;

public class PostJobActivity extends BaseActivity implements View.OnClickListener {
    private static String TAG = "PostJobActivity";
    private static final int REQUEST_CODE_AVAILABILITY_SELECT = 1;
    private static final int REQUEST_CODE_SELECT_PICTURE = 3;

    private Toolbar toolbar;
    private IconEditText etJobName, etAvailability;
    private EditText etDetails;
    private ArrayAdapter<String> projectAdapter;
    private PlaceArrayAdapter placeArrayAdapter;
    private AutoCompleteTextView etArea;
    private LinearLayout llServiceIcon;
    private RelativeLayout rlAvailability;
    private Button btnSubmit;
    private ImageView ivIcon;
    private TextView tvServiceTitle;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private int selectedIndustry;
    private LatLng selectedLocation;
    private JobPostAdapter adapter;
    private ArrayList<AvailabilityDateInfo> arrSelectedDates = new ArrayList<>();
    private ArrayList<AttachFileInfo> dataAttach = new ArrayList<>();
    private File rawImageFile;

    // from activity
    private int from;
    private ContractorInfo contractor;
    private JobInfo job;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_job);
        initVariable();
        initUI();
    }

    void initVariable(){
        selectedIndustry = 0;
        selectedLocation = null;
        etArea = findViewById(R.id.et_area);
        etArea.setThreshold(3);
        etArea.setOnItemClickListener(mAutocompleteClickListener);
        placeArrayAdapter = new PlaceArrayAdapter(this, android.R.layout.simple_list_item_1);
        etArea.setAdapter(placeArrayAdapter);
        projectAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_dropdown_item , gArrSpecialization);
        projectAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

        from = getIntent().getIntExtra("from", Constant.FROM_JOB_POST);
        switch (from){
            case FROM_JOB_POST:
                selectedIndustry = getIntent().getIntExtra("industry", 1);
                break;
            case FROM_JOB_EDIT:
                contractor = (ContractorInfo) getIntent().getSerializableExtra("contractor");
                job = (JobInfo) getIntent().getSerializableExtra("job");
                break;
            case FROM_JOB_HIRE:
                selectedIndustry = getIntent().getIntExtra("industry", 1);
                contractor = (ContractorInfo) getIntent().getSerializableExtra("contractor");
                break;
        }
    }

    void initUI(){
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorPrimary), 0);
        setupToolbar();
        btnSubmit = findViewById(R.id.btn_submit);
        etJobName = findViewById(R.id.et_job_name);
        ivIcon = findViewById(R.id.iv_icon);
        etAvailability = findViewById(R.id.et_availability);
        etDetails = findViewById(R.id.et_details);
        llServiceIcon = findViewById(R.id.ll_service_icon);
        rlAvailability = findViewById(R.id.rl_availability);
        tvServiceTitle = findViewById(R.id.tv_service_title);
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new JobPostAdapter(this, dataAttach);
        adapter.addListener(new JobPostAdapter.OnItemClickListener() {
            @Override
            public void onItemAddClick() {
                chooseAction();
            }

            @Override
            public void onItemRemoveClick(AttachFileInfo item) {
                MessageUtils.displayCancellableAlertWithHandler("Remove", "Are you sure you want to remove the image?", PostJobActivity.this, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dataAttach.remove(item);
                        adapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onItemOpen(AttachFileInfo item) {
                Intent intent = new Intent(PostJobActivity.this, ImagePreviewActivity.class);
                intent.putExtra("file", item.localUrl);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
        btnSubmit.setOnClickListener(this);
        rlAvailability.setOnClickListener(this);
        etAvailability.setEnabled(false);
        if (from == FROM_JOB_HIRE){
            btnSubmit.setText(R.string.hire_specialist);
            ivIcon.setImageDrawable(getDrawable(GlobalUtils.getAllService().get(selectedIndustry - 1).getIcon()));
            ivIcon.setColorFilter(ContextCompat.getColor(this, R.color.colorWhite));
            tvServiceTitle.setText(GlobalUtils.getAllService().get(selectedIndustry - 1).getTitle());
            llServiceIcon.setVisibility(View.GONE);
        } else if (from == FROM_JOB_EDIT){
            etJobName.setText(job.name);
            etArea.setText(job.area);
            selectedLocation = new LatLng(Double.valueOf(job.area_lati), Double.valueOf(job.area_longi));
            arrSelectedDates = GlobalUtils.convertAvailabilityDateModel(job.availability_dates);
            selectedIndustry = job.industry;
            etAvailability.setText("View Availability");
            etDetails.setText(job.description);
            if (!StringHelper.isEmpty(job.attachment)) {
                for (String item : Arrays.asList(job.attachment.split(","))) {
                    dataAttach.add(new AttachFileInfo(GlobalUtils.fetchDownloadFileName(item), item, "", false));
                }
                adapter.notifyDataSetChanged();
            }

        } else if (from == FROM_JOB_POST) {
            ivIcon.setImageDrawable(getDrawable(GlobalUtils.getAllService().get(selectedIndustry - 1).getIcon()));
            ivIcon.setColorFilter(ContextCompat.getColor(this, R.color.colorWhite));
            tvServiceTitle.setText(GlobalUtils.getAllService().get(selectedIndustry - 1).getTitle());
        }
    }


    void setupToolbar(){
        toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);

        } catch (Exception e) {
        }
    }

    boolean requiredFieldNotEmpty(){
        return !(StringHelper.isEmpty(etJobName.getText().toString()) || selectedIndustry == 0
                || selectedLocation == null || arrSelectedDates.size() == 0 || StringHelper.isEmpty(etDetails.getText().toString()));
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
            case R.id.rl_availability:
                Intent intent = new Intent(this, SeeAvailabilityActivity.class);
                Gson gson = new Gson();
                String strArrDate = gson.toJson(arrSelectedDates);
                intent.putExtra("arrDates", strArrDate);
                intent.putExtra("isEditable", true);
                startActivityForResult(intent, REQUEST_CODE_AVAILABILITY_SELECT);
                break;
            case R.id.btn_submit:
                if (requiredFieldNotEmpty()){
                    if (PreferenceHelper.isLoginIn()) {
                        switch (from){
                            case FROM_JOB_POST:
                                postJob();
                                break;
                            case FROM_JOB_EDIT:
                                editJob();
                                break;
                            case FROM_JOB_HIRE:
                                hireJob();
                                break;
                        }
                    } else {
                        MessageUtils.showCustomAlertDialogNoCancel(PostJobActivity.this, getResources().getString(R.string.note), getResources().getString(R.string.please_signup_to_post_job), new Notify() {
                            @Override
                            public void onSuccess(Object object) {
                                Intent intent = new Intent(PostJobActivity.this, EnterActivity.class);
                                startActivity(intent);
                            }

                            @Override
                            public void onFail() {

                            }
                        });
                    }

                } else {
                    Toast.makeText(PostJobActivity.this, R.string.please_fill_all_the_required_info, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    void chooseAction() {
        File dir = FileUtils.getDiskCacheDir(this, "temp");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String name = StringHelper.getDateRandomString() + ".png";
        rawImageFile = new File(dir, name);
        Intent captureImageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        captureImageIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(rawImageFile));

        Intent pickIntent = new Intent(Intent.ACTION_GET_CONTENT);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(pickIntent, getString(R.string.profile_photo));
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {captureImageIntent});

        startActivityForResult(chooserIntent, REQUEST_CODE_SELECT_PICTURE);
    }

    String fetchAttachment(){
        List<String> arrString = new ArrayList<>();
        for (AttachFileInfo item : dataAttach) {
            arrString.add(item.url);
        }
        return TextUtils.join(",", arrString);
    }

    String fetchAvailableDate(){
        // need to convert date with UTC timezone
        List<String> arrString = new ArrayList<>();
        for(AvailabilityDateInfo item : arrSelectedDates) {
            for (FromToDateInfo stamp : item.timeStamp) {
                String strFrom = GlobalUtils.convertDateToString(stamp.fromDate, DATE_FORMAT_AVAILABILITY, TimeZone.getTimeZone("UTC"));
                String strTo = GlobalUtils.convertDateToString(stamp.toDate, DATE_FORMAT_AVAILABILITY, TimeZone.getTimeZone("UTC"));
                arrString.add(strFrom + "-" + strTo);
            }
        }
        return TextUtils.join(",", arrString);
    }

    void postJob(){
        showProgressDialog();
        int posterId = AppPreferenceManager.getInt(Constant.PRE_ID, 0);
        String posterName = AppPreferenceManager.getString(Constant.PRE_NAME, "");

        ApiUtils.postJob(this, etJobName.getText().toString(), selectedIndustry, etArea.getText().toString(),
                String.valueOf(selectedLocation.latitude), String.valueOf(selectedLocation.longitude),
                etDetails.getText().toString(), 0, posterId, posterName,  fetchAvailableDate(), fetchAttachment(), new Notify() {
                    @Override
                    public void onSuccess(Object object) {
                        hideProgressDialog();
                        GeneralData data = (GeneralData) object;
                        if (data != null){
                            if (data.status == 0){
                                MessageUtils.showConfirmAlertDialog(PostJobActivity.this, getResources().getString(R.string.your_job_has_been_posted_successfully), new Notify() {
                                    @Override
                                    public void onSuccess(Object object) {
                                        finishWithResult();
                                    }

                                    @Override
                                    public void onFail() {

                                    }
                                });
                            } else {
                                Toast.makeText(PostJobActivity.this, R.string.failure, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(PostJobActivity.this, R.string.server_not_response, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFail() {
                        hideProgressDialog();
                        Toast.makeText(PostJobActivity.this, R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    void hireJob(){
        showProgressDialog();
        int posterId = AppPreferenceManager.getInt(Constant.PRE_ID, 0);
        String posterName = AppPreferenceManager.getString(Constant.PRE_NAME, "");
        int contractorId = contractor.id;

        ApiUtils.hireJob(this, etJobName.getText().toString(), selectedIndustry, etArea.getText().toString(),
                String.valueOf(selectedLocation.latitude), String.valueOf(selectedLocation.longitude),
                etDetails.getText().toString(), 0, posterId, posterName, fetchAvailableDate(), fetchAttachment(),  contractorId, new Notify() {
                    @Override
                    public void onSuccess(Object object) {
                        hideProgressDialog();
                        GeneralData data = (GeneralData) object;
                        if (data != null){
                            if (data.status == 0){
                                MessageUtils.showConfirmAlertDialog(PostJobActivity.this, getResources().getString(R.string.your_hiring_proposal_has_been), new Notify() {
                                    @Override
                                    public void onSuccess(Object object) {
                                        finishWithResult();
                                    }

                                    @Override
                                    public void onFail() {

                                    }
                                });
                            } else {
                                Toast.makeText(PostJobActivity.this, R.string.failure, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(PostJobActivity.this, R.string.server_not_response, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFail() {
                        hideProgressDialog();
                        Toast.makeText(PostJobActivity.this, R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    void editJob(){
        showProgressDialog();
        String token = AppPreferenceManager.getString(Constant.PRE_TOKEN, "");
        int jobId = job.id;

        ApiUtils.editJob(this, jobId,  etJobName.getText().toString(), selectedIndustry, etArea.getText().toString(),
                String.valueOf(selectedLocation.latitude), String.valueOf(selectedLocation.longitude),
                etDetails.getText().toString(),  fetchAvailableDate(), fetchAttachment(), token, new Notify() {
                    @Override
                    public void onSuccess(Object object) {
                        hideProgressDialog();
                        GeneralData data = (GeneralData) object;
                        if (data != null){
                            if (data.status == 0){
                                MessageUtils.showConfirmAlertDialog(PostJobActivity.this, getResources().getString(R.string.your_proposal_has_been_updated), new Notify() {
                                    @Override
                                    public void onSuccess(Object object) {
                                        finishWithResult();
                                    }

                                    @Override
                                    public void onFail() {

                                    }
                                });
                            } else {
                                Toast.makeText(PostJobActivity.this, R.string.failure, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(PostJobActivity.this, R.string.server_not_response, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFail() {
                        hideProgressDialog();
                        Toast.makeText(PostJobActivity.this, R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    void uploadAttachment(File file){
        showProgressDialog();
        ApiUtils.uploadAttachment(this, file, new Notify() {
                    @Override
                    public void onSuccess(Object object) {
                        hideProgressDialog();
                        AttachData data = (AttachData) object;
                        if (data != null){
                            if (data.status == 0){
                                dataAttach.add(new AttachFileInfo(file.getName(), data.info, file.getAbsolutePath(), false));
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(PostJobActivity.this, R.string.failure, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(PostJobActivity.this, R.string.server_not_response, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFail() {
                        hideProgressDialog();
                        Toast.makeText(PostJobActivity.this, R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceArrayAdapter.PlaceAutocomplete item = placeArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
            FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields).build();
            PlacesClient placesClient = com.google.android.libraries.places.api.Places.createClient(PostJobActivity.this);
            placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                @Override
                public void onSuccess(FetchPlaceResponse response) {
                    Place place = response.getPlace();
                    selectedLocation = place.getLatLng();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    if (exception instanceof ApiException) {
                        Toast.makeText(PostJobActivity.this, exception.getMessage() + "", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_AVAILABILITY_SELECT:
                    if (data != null) {
                        String strDate = data.getStringExtra("selectedDates");
                        Gson gson = new Gson();
                        arrSelectedDates = gson.fromJson(strDate, new TypeToken<ArrayList<AvailabilityDateInfo>>(){}.getType());
                        if (arrSelectedDates.size() > 0)
                            etAvailability.setText("View Availability");
                        break;
                    }
                case REQUEST_CODE_SELECT_PICTURE:
                    if (checkActionType(data)) { // Camera
                        Uri imageUri =  getPickImageResultUri(data);
                        File originFile = new File(imageUri.getPath());
                        File compressFile = FileUtils.compressImage(this, originFile);
                        uploadAttachment(compressFile);
                    } else {  // Gallery
                        if (data.getData() != null){
                            Uri uri = data.getData();
                            File originFile = FileUtils.getFile(this, uri);
                            File compressFile = FileUtils.compressImage(this, originFile);
                            uploadAttachment(compressFile);
                        }
                    }
                    break;
            }
        }
    }

    boolean checkActionType(Intent data) {
        boolean isCamera = true;
        if (data != null ) {
            String action = data.getAction();
            if ((data.getData() == null) && (data.getClipData() == null)) {
                isCamera = true;
            } else {
                isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
            }
        }
        return isCamera;
    }

    public Uri getPickImageResultUri(Intent  data) {
        boolean isCamera = true;
        if (data != null && data.getData() != null) {
            String action = data.getAction();
            isCamera = action != null  && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return isCamera ?  Uri.fromFile(rawImageFile) : data.getData();
    }


    void finishWithResult(){
        Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

}
