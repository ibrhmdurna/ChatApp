package com.ibrhmdurna.chatapp.settings;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.chivorn.smartmaterialspinner.SmartMaterialSpinner;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.application.App;
import com.ibrhmdurna.chatapp.application.ViewComponentFactory;
import com.ibrhmdurna.chatapp.database.bridgeSelect.AccountContent;
import com.ibrhmdurna.chatapp.database.bridgeSelect.AccountInformationView;
import com.ibrhmdurna.chatapp.database.select.AccountEditInformation;
import com.ibrhmdurna.chatapp.databinding.ActivityEditAccountBinding;
import com.ibrhmdurna.chatapp.image.CameraActivity;
import com.ibrhmdurna.chatapp.image.GalleryActivity;
import com.ibrhmdurna.chatapp.util.Environment;
import com.ibrhmdurna.chatapp.util.dialog.ProfileBottomSheetDialog;
import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditAccountActivity extends AppCompatActivity implements ViewComponentFactory, View.OnClickListener, ProfileBottomSheetDialog.BottomSheetListener {

    private ActivityEditAccountBinding binding;

    private TextInputLayout nameInput, surnameInput, phoneInput;
    private SmartMaterialSpinner genderSpinner, locationSpinner;
    private TextView birthdayText, profileText;
    private CircleImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.Theme.getInstance().getTheme(this);
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_edit_account);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_account);

        toolsManagement();
    }

    private void getAccountInformation(){
        AccountContent content = new AccountInformationView(new AccountEditInformation(binding, profileImage, profileText, locationSpinner));
        content.getAccountInformation();
    }

    private void inputProcess(){
        nameInput.getEditText().addTextChangedListener(inputWatcher);
        surnameInput.getEditText().addTextChangedListener(inputWatcher);
        phoneInput.getEditText().addTextChangedListener(inputWatcher);
    }

    private TextWatcher inputWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            checkInput();
        }
    };

    private void checkInput(){
        String name = nameInput.getEditText().getText().toString();
        String surname = surnameInput.getEditText().getText().toString();
        String phone = phoneInput.getEditText().getText().toString();
    }

    private void buildBirthdaySpinner(){
        Date date = new Date();
        int day = Integer.parseInt(String.valueOf(DateFormat.format("dd", date.getTime())));
        int month = Integer.parseInt(String.valueOf(DateFormat.format("MM", date.getTime())));
        int year = Integer.parseInt(String.valueOf(DateFormat.format("yyyy", date.getTime())));

        new SpinnerDatePickerDialogBuilder()
                .context(EditAccountActivity.this)
                .spinnerTheme(R.style.NumberPickerStyle)
                .callback(new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        switch (monthOfYear){
                            case 0:
                                birthdayText.setText(dayOfMonth+" January "+year);
                                break;
                            case 1:
                                birthdayText.setText(dayOfMonth+" February "+year);
                                break;
                            case 2:
                                birthdayText.setText(dayOfMonth+" March "+year);
                                break;
                            case 3:
                                birthdayText.setText(dayOfMonth+" April "+year);
                                break;
                            case 4:
                                birthdayText.setText(dayOfMonth+" May "+year);
                                break;
                            case 5:
                                birthdayText.setText(dayOfMonth+" June "+year);
                                break;
                            case 6:
                                birthdayText.setText(dayOfMonth+" July "+year);
                                break;
                            case 7:
                                birthdayText.setText(dayOfMonth+" August "+year);
                                break;
                            case 8:
                                birthdayText.setText(dayOfMonth+" September "+year);
                                break;
                            case 9:
                                birthdayText.setText(dayOfMonth+" October "+year);
                                break;
                            case 10:
                                birthdayText.setText(dayOfMonth+" November "+year);
                                break;
                            case 11:
                                birthdayText.setText(dayOfMonth+" December "+year);
                                break;
                        }
                    }
                })
                .showTitle(true)
                .showDaySpinner(true)
                .defaultDate(year,month - 1,day)
                .maxDate(year,month - 1,day)
                .minDate(1900,1,1)
                .build()
                .show();
    }

    private void spinnerProcess(){
        // Gender spinner
        genderSpinner = findViewById(R.id.genderSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.gender, R.layout.spinner_text);
        adapter.setDropDownViewResource(R.layout.spinner_layout);
        genderSpinner.setAdapter(adapter);

        // Location spinner
        ArrayAdapter<CharSequence> locAdapter = ArrayAdapter.createFromResource(this, R.array.countries_array, R.layout.spinner_text);
        locAdapter.setDropDownViewResource(R.layout.spinner_layout);
        locationSpinner.setAdapter(locAdapter);
    }

    @Override
    public void buildView(){
        nameInput = findViewById(R.id.editNameInput);
        surnameInput = findViewById(R.id.editSurnameInput);
        phoneInput = findViewById(R.id.editPhoneInput);
        birthdayText = findViewById(R.id.birthdayText);
        profileImage = findViewById(R.id.profileImage);
        profileText = findViewById(R.id.profileImageText);
        genderSpinner = findViewById(R.id.genderSpinner);
        locationSpinner = findViewById(R.id.locationSpinner);
    }

    @Override
    public void toolsManagement(){
        Environment.getInstance().toolbarProcess(this, R.id.editAccountToolbar);
        buildView();
        spinnerProcess();
        inputProcess();
        getAccountInformation();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.editBirthdayItem:
                buildBirthdaySpinner();
                break;
            case R.id.updateEmailItem:
                Intent updateEmail = new Intent(getApplicationContext(), UpdateEmailActivity.class);
                startActivity(updateEmail);
                break;
            case R.id.changeProfileImageView:
                ProfileBottomSheetDialog profileBottomSheetDialog = new ProfileBottomSheetDialog(profileText.getVisibility() == View.VISIBLE);
                profileBottomSheetDialog.show(getSupportFragmentManager(), "bottom_sheet");
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onBackPressed();
        return true;
    }

    @Override
    public void onButtonClicked(String action) {
        switch (action){
            case "gallery":
                Intent galleryIntent = new Intent(this, GalleryActivity.class);
                galleryIntent.putExtra("isContext","Profile");
                galleryIntent.putExtra("register", false);
                startActivity(galleryIntent);
                break;
            case "camera":
                Intent cameraIntent = new Intent(this, CameraActivity.class);
                cameraIntent.putExtra("isContext","Profile");
                cameraIntent.putExtra("register", false);
                startActivity(cameraIntent);
                break;
        }
    }
}
