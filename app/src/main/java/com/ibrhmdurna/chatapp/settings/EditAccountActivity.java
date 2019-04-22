package com.ibrhmdurna.chatapp.settings;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.chivorn.smartmaterialspinner.SmartMaterialSpinner;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.application.App;
import com.ibrhmdurna.chatapp.application.ViewComponentFactory;
import com.ibrhmdurna.chatapp.database.Update;
import com.ibrhmdurna.chatapp.database.bridge.AbstractFind;
import com.ibrhmdurna.chatapp.database.bridge.Find;
import com.ibrhmdurna.chatapp.database.find.AccountEditFindInfo;
import com.ibrhmdurna.chatapp.databinding.ActivityEditAccountBinding;
import com.ibrhmdurna.chatapp.image.CameraActivity;
import com.ibrhmdurna.chatapp.image.GalleryActivity;
import com.ibrhmdurna.chatapp.util.Environment;
import com.ibrhmdurna.chatapp.util.controller.DialogController;
import com.ibrhmdurna.chatapp.util.controller.ImageController;
import com.ibrhmdurna.chatapp.util.dialog.ProfileBottomSheetDialog;
import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import java.util.Date;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditAccountActivity extends AppCompatActivity implements ViewComponentFactory, View.OnClickListener, ProfileBottomSheetDialog.BottomSheetListener {

    private ActivityEditAccountBinding binding;

    private TextInputLayout nameInput, surnameInput, phoneInput;
    private SmartMaterialSpinner genderSpinner, locationSpinner;
    private TextView birthdayText, profileText;
    private CircleImageView profileImage;
    private TextView saveBtn;
    private TextView genderError, locationError;
    private View genderLine, locationLine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.Theme.getInstance().getTheme(this);
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_edit_account);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_account);

        toolsManagement();
    }

    private void profileImageDeleteProcess(){
        ImageController.getInstance().setProfileImageBytes(null);

        String image = binding.getAccount().getProfile_image();

        if(image.substring(0, 8).equals("default_")){
            String value = image.substring(8,9);
            int index = Integer.parseInt(value);
            setProfileImage(index);
        }
        else {
            randomProfileImage();
        }

        String name = binding.getAccount().getName().substring(0,1);
        profileText.setVisibility(View.VISIBLE);
        profileText.setText(name);
        profileImage.setSaveEnabled(true);
        checkInput();

    }

    private void randomProfileImage(){
        Random r = new Random();
        int imageIndex = r.nextInt(10);
        switch (imageIndex){
            case 0:
                profileImage.setImageResource(R.drawable.ic_avatar_0);
                binding.getAccount().setProfile_image("default_0");
                binding.getAccount().setThumb_image("default_0");
                break;
            case 1:
                profileImage.setImageResource(R.drawable.ic_avatar_1);
                binding.getAccount().setProfile_image("default_1");
                binding.getAccount().setThumb_image("default_1");
                break;
            case 2:
                profileImage.setImageResource(R.drawable.ic_avatar_2);
                binding.getAccount().setProfile_image("default_2");
                binding.getAccount().setThumb_image("default_2");
                break;
            case 3:
                profileImage.setImageResource(R.drawable.ic_avatar_3);
                binding.getAccount().setProfile_image("default_3");
                binding.getAccount().setThumb_image("default_3");
                break;
            case 4:
                profileImage.setImageResource(R.drawable.ic_avatar_4);
                binding.getAccount().setProfile_image("default_4");
                binding.getAccount().setThumb_image("default_4");
                break;
            case 5:
                profileImage.setImageResource(R.drawable.ic_avatar_5);
                binding.getAccount().setProfile_image("default_5");
                binding.getAccount().setThumb_image("default_5");
                break;
            case 6:
                profileImage.setImageResource(R.drawable.ic_avatar_6);
                binding.getAccount().setProfile_image("default_6");
                binding.getAccount().setThumb_image("default_6");
                break;
            case 7:
                profileImage.setImageResource(R.drawable.ic_avatar_7);
                binding.getAccount().setProfile_image("default_7");
                binding.getAccount().setThumb_image("default_7");
                break;
            case 8:
                profileImage.setImageResource(R.drawable.ic_avatar_8);
                binding.getAccount().setProfile_image("default_8");
                binding.getAccount().setThumb_image("default_8");
                break;
            case 9:
                profileImage.setImageResource(R.drawable.ic_avatar_9);
                binding.getAccount().setProfile_image("default_9");
                binding.getAccount().setThumb_image("default_9");
                break;
        }
    }

    private void setProfileImage(int index){
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

    private void profileImageProcess(){

        final byte[] bytes = ImageController.getInstance().getProfileImageBytes();

        if(bytes != null){
            Handler h = new Handler();
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    profileImage.setImageBitmap(bitmap);
                    profileText.setText(null);
                    profileText.setVisibility(View.GONE);
                    profileImage.setSaveEnabled(true);
                    checkInput();
                }
            },100);
        }
    }

    private void saveEdit(){
        binding.getAccount().setName(nameInput.getEditText().getText().toString());
        binding.getAccount().setSurname(surnameInput.getEditText().getText().toString());
        binding.getAccount().setPhone(phoneInput.getEditText().getText().toString());
        binding.getAccount().setBirthday(birthdayText.getText().toString());
        binding.getAccount().setGender(genderSpinner.getSelectedItemPosition());
        binding.getAccount().setLocation(locationSpinner.getSelectedItemPosition());
        binding.getAccount().setSearch_name(nameInput.getEditText().getText().toString().toLowerCase() + " " + surnameInput.getEditText().getText().toString().toLowerCase());

        Bitmap bitmap = null;

        if(profileText.getText().length() > 0){
            bitmap = null;
        }
        else if (profileImage.isSaveEnabled()){
            bitmap = ((BitmapDrawable) profileImage.getDrawable()).getBitmap();
        }

        String name = nameInput.getEditText().getText().toString();
        String surname = surnameInput.getEditText().getText().toString();
        String birthday = birthdayText.getText().toString();
        String phone = phoneInput.getEditText().getText().toString();

        if(name.trim().length() > 0 && surname.trim().length() > 0 && birthday.trim().length() > 0 && (phone.trim().length() == 18 || phone.trim().length() == 0) && genderSpinner.getSelectedItemPosition() > 0 && locationSpinner.getSelectedItemPosition() > 0){
            Update.getInstance().updateAccount(this, binding.getAccount(), bitmap);
        }
        else {
            if(!(name.trim().length() > 0)){
                nameInput.setError("* Enter a name");
            }
            else {
                nameInput.setError(null);
                nameInput.setErrorEnabled(false);
            }
            if(!(surname.trim().length() > 0)){
                surnameInput.setError("* Enter a surname");
            }
            else {
                surnameInput.setError(null);
                surnameInput.setErrorEnabled(false);
            }
            if((phone.trim().length() > 0 && phone.trim().length() < 18)){
                phoneInput.setError("Enter a valid phone number");
            }
            else {
                phoneInput.setError(null);
                phoneInput.setErrorEnabled(false);
            }
            if(!(genderSpinner.getSelectedItemPosition() > 0)){
                genderError.setVisibility(View.VISIBLE);
                genderLine.setBackgroundColor(getColor(R.color.colorError));
            }
            else {
                genderError.setVisibility(View.GONE);
                genderLine.setBackgroundColor(getColor(R.color.colorGray));
            }
            if(!(locationSpinner.getSelectedItemPosition() > 0)){
                locationError.setVisibility(View.VISIBLE);
                locationLine.setBackgroundColor(getColor(R.color.colorError));
            }
            else {
                locationError.setVisibility(View.GONE);
                locationLine.setBackgroundColor(getColor(R.color.colorGray));
            }
        }

    }

    private void getAccountInformation(){
        AbstractFind findAccount = new Find(new AccountEditFindInfo(binding));
        findAccount.getContent();
    }

    private void watcherProcess(){
        nameInput.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String name = binding.getAccount().getName();
                String name_input = nameInput.getEditText().getText().toString();

                nameInput.setSaveEnabled(!name_input.equals(name));

                checkInput();
            }
        });

        surnameInput.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String surname = binding.getAccount().getSurname();
                String surname_input = surnameInput.getEditText().getText().toString();

                surnameInput.setSaveEnabled(!surname_input.equals(surname));

                checkInput();
            }
        });

        phoneInput.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String phone = binding.getAccount().getPhone();
                String phone_input = phoneInput.getEditText().getText().toString();

                phoneInput.setSaveEnabled(!phone_input.equals(phone));

                checkInput();
            }
        });

        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int gender = binding.getAccount().getGender();

                genderSpinner.setSaveEnabled(gender != genderSpinner.getSelectedItemPosition());

                checkInput();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int location = binding.getAccount().getLocation();

                locationSpinner.setSaveEnabled(location != locationSpinner.getSelectedItemPosition());

                checkInput();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        birthdayText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String birthday = binding.getAccount().getBirthday();
                String birthday_input = birthdayText.getText().toString();

                birthdayText.setSaveEnabled(!birthday_input.equals(birthday));

                checkInput();
            }
        });
    }

    private void checkInput(){
        if(nameInput.isSaveEnabled() || surnameInput.isSaveEnabled() || phoneInput.isSaveEnabled() ||
            genderSpinner.isSaveEnabled() || locationSpinner.isSaveEnabled() || birthdayText.isSaveEnabled() || profileImage.isSaveEnabled()){
            saveBtn.setEnabled(true);
        }
        else {
            saveBtn.setEnabled(false);
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
        genderSpinner = findViewById(R.id.genderSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.gender, R.layout.spinner_text);
        adapter.setDropDownViewResource(R.layout.spinner_layout);
        genderSpinner.setAdapter(adapter);

        // Location spinner
        ArrayAdapter<CharSequence> locAdapter = ArrayAdapter.createFromResource(this, R.array.countries_array, R.layout.spinner_text);
        locAdapter.setDropDownViewResource(R.layout.spinner_layout);
        locationSpinner.setAdapter(locAdapter);
    }

    private void onBack(){
        if(saveBtn.isEnabled()){
            final AlertDialog dialog = DialogController.getInstance().dialogCustom(this, "You have unsaved changes. Are you sure want to cancel?", "No", "Yes");
            TextView positiveBtn = dialog.findViewById(R.id.dialog_positive_btn);
            positiveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    finish();
                    ImageController.getInstance().setProfileImageBytes(null);
                }
            });
        }
        else {
            ImageController.getInstance().setProfileImageBytes(null);
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        profileImageProcess();
        getAccountInformation();
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
        saveBtn = findViewById(R.id.editSaveBtn);
        genderError = findViewById(R.id.gender_error_text);
        genderLine = findViewById(R.id.gender_line);
        locationError = findViewById(R.id.location_error_text);
        locationLine = findViewById(R.id.location_line);
    }

    @Override
    public void toolsManagement(){
        Environment.getInstance().toolbarProcess(this, R.id.editAccountToolbar);
        buildView();
        spinnerProcess();
        watcherProcess();
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
            case R.id.editCancelBtn:
                onBack();
                break;
            case R.id.editSaveBtn:
                saveEdit();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImageController.getInstance().setProfileImageBytes(null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        onBack();
    }

    @Override
    public void onButtonClicked(String action) {
        switch (action){
            case "gallery":
                Intent galleryIntent = new Intent(this, GalleryActivity.class);
                galleryIntent.putExtra("isContext","Profile");
                galleryIntent.putExtra("isRegister", false);
                startActivity(galleryIntent);
                break;
            case "camera":
                Intent cameraIntent = new Intent(this, CameraActivity.class);
                cameraIntent.putExtra("isContext","Profile");
                cameraIntent.putExtra("isRegister", false);
                startActivity(cameraIntent);
                break;
            case "delete":
                profileImageDeleteProcess();
                break;
        }
    }
}
