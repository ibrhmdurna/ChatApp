package com.ibrhmdurna.chatapp.Settings;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import com.ibrhmdurna.chatapp.Application.App;
import com.ibrhmdurna.chatapp.Application.ViewComponentFactory;
import com.ibrhmdurna.chatapp.Image.CameraActivity;
import com.ibrhmdurna.chatapp.Image.GalleryActivity;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.Util.Environment;
import com.ibrhmdurna.chatapp.Util.ProfileBottomSheetDialog;
import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import java.util.Date;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditAccountActivity extends AppCompatActivity implements ViewComponentFactory, View.OnClickListener, ProfileBottomSheetDialog.BottomSheetListener {

    private TextInputLayout nameInput, surnameInput, phoneInput;
    private SmartMaterialSpinner genderSpinner, locationSpinner;
    private TextView birthdayText, profileText;
    private CircleImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.Theme.getInstance().getTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);

        toolsManagement();
        randomProfileImage();
        profileText.setText("N");
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

    private void randomProfileImage(){
        Random r = new Random();
        int index = r.nextInt(10);
        switch (index){
            case 0:
                profileImage.setImageResource(R.drawable.ic_avatar_0);
                break;
            case 1:
                profileImage.setImageResource(R.drawable.ic_avatar_1);
                break;
            case 2:
                profileImage.setImageResource(R.drawable.ic_avatar_2);
                break;
            case 3:
                profileImage.setImageResource(R.drawable.ic_avatar_3);
                break;
            case 4:
                profileImage.setImageResource(R.drawable.ic_avatar_4);
                break;
            case 5:
                profileImage.setImageResource(R.drawable.ic_avatar_5);
                break;
            case 6:
                profileImage.setImageResource(R.drawable.ic_avatar_6);
                break;
            case 7:
                profileImage.setImageResource(R.drawable.ic_avatar_7);
                break;
            case 8:
                profileImage.setImageResource(R.drawable.ic_avatar_8);
                break;
            case 9:
                profileImage.setImageResource(R.drawable.ic_avatar_9);
                break;
        }
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
        SmartMaterialSpinner spinner = findViewById(R.id.genderSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.gender, R.layout.spinner_text);
        adapter.setDropDownViewResource(R.layout.spinner_layout);
        spinner.setAdapter(adapter);

        // Location spinner
        SmartMaterialSpinner locSpinner = findViewById(R.id.locationSpinner);
        ArrayAdapter<CharSequence> locAdapter = ArrayAdapter.createFromResource(this, R.array.countries_array, R.layout.spinner_text);
        locAdapter.setDropDownViewResource(R.layout.spinner_layout);
        locSpinner.setAdapter(locAdapter);
    }

    @Override
    public void buildView(){
        nameInput = findViewById(R.id.edit_name_input);
        surnameInput = findViewById(R.id.edit_surname_input);
        phoneInput = findViewById(R.id.edit_phone_input);
        birthdayText = findViewById(R.id.birthday_text);
        profileImage = findViewById(R.id.profile_image);
        profileText = findViewById(R.id.profile_image_text);
    }

    @Override
    public void toolsManagement(){
        Environment.getInstance().toolbarProcess(this, R.id.edit_account_toolbar);
        buildView();
        spinnerProcess();
        inputProcess();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.edit_birthday_item:
                buildBirthdaySpinner();
                break;
            case R.id.update_email_item:
                Intent updateEmail = new Intent(getApplicationContext(), UpdateEmailActivity.class);
                startActivity(updateEmail);
                break;
            case R.id.change_profile_image_view:
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
