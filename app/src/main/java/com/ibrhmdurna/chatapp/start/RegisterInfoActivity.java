package com.ibrhmdurna.chatapp.start;

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
import com.ibrhmdurna.chatapp.application.App;
import com.ibrhmdurna.chatapp.application.ViewComponentFactory;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.util.Environment;
import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import java.util.Date;

public class RegisterInfoActivity extends AppCompatActivity implements ViewComponentFactory, View.OnClickListener {

    private TextInputLayout nameInput, surnameInput, phoneInput;
    private TextView birthdayText, nextView;
    private SmartMaterialSpinner genderSpinner, locationSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.Theme.getInstance().getTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_info);

        toolsManagement();
    }

    private void inputProcess(){
        nameInput.getEditText().addTextChangedListener(inputWatcher);
        surnameInput.getEditText().addTextChangedListener(inputWatcher);
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

        if(name.trim().length() > 0 && surname.trim().length() > 0){
            nextView.setEnabled(true);
        }
        else {
            nextView.setEnabled(false);
        }
    }

    private void buildBirthdaySpinner(){
        Date date = new Date();
        int day = Integer.parseInt(String.valueOf(DateFormat.format("dd", date.getTime())));
        int month = Integer.parseInt(String.valueOf(DateFormat.format("MM", date.getTime())));
        int year = Integer.parseInt(String.valueOf(DateFormat.format("yyyy", date.getTime())));

        new SpinnerDatePickerDialogBuilder()
                .context(RegisterInfoActivity.this)
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

                        checkInput();
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
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.gender, R.layout.spinner_text);
        adapter.setDropDownViewResource(R.layout.spinner_layout);
        genderSpinner.setAdapter(adapter);

        // Location spinner
        ArrayAdapter<CharSequence> locAdapter = ArrayAdapter.createFromResource(this, R.array.countries_array, R.layout.spinner_text);
        locAdapter.setDropDownViewResource(R.layout.spinner_layout);
        locationSpinner.setAdapter(locAdapter);
    }

    private void registerProcess(){
        String email = getIntent().getStringExtra("email");
        String password = getIntent().getStringExtra("password");
        String name = nameInput.getEditText().getText().toString();
        String surname = surnameInput.getEditText().getText().toString();
        String phone = phoneInput.getEditText().getText().toString();
        String birthday = birthdayText.getText().toString();
        int gender = genderSpinner.getSelectedItemPosition();
        int location = locationSpinner.getSelectedItemPosition();

        Intent finishIntent = new Intent(this, RegisterFinishActivity.class);
        finishIntent.putExtra("email", email);
        finishIntent.putExtra("password", password);
        finishIntent.putExtra("name", name);
        finishIntent.putExtra("surname", surname);
        finishIntent.putExtra("phone", phone);
        finishIntent.putExtra("birthday", birthday);
        finishIntent.putExtra("gender", gender);
        finishIntent.putExtra("location", location);
        startActivity(finishIntent);
    }

    @Override
    public void buildView(){
        nameInput = findViewById(R.id.reg_name_input);
        surnameInput = findViewById(R.id.reg_surname_input);
        phoneInput = findViewById(R.id.reg_phone_input);
        birthdayText = findViewById(R.id.birthday_text);
        nextView = findViewById(R.id.register_info_next_btn);
        genderSpinner = findViewById(R.id.genderSpinner);
        locationSpinner = findViewById(R.id.locationSpinner);
    }

    @Override
    public void toolsManagement(){
        Environment.getInstance().toolbarProcess(this, R.id.register_info_toolbar);
        buildView();
        spinnerProcess();
        inputProcess();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.edit_birthday_item:
                buildBirthdaySpinner();
                break;
            case R.id.reg_info_back_btn:
                super.onBackPressed();
                break;
            case R.id.register_info_next_btn:
                registerProcess();
                break;
        }
    }
}
