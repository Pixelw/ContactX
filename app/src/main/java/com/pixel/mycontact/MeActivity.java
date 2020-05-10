package com.pixel.mycontact;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import com.google.android.material.textfield.TextInputLayout;

public class MeActivity extends AppCompatActivity {

    private TextInputLayout tilName;
    private TextInputLayout tilNumber;
    private TextInputLayout tilEmail;
    private AppCompatImageView ivAvatar;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me);
        ivAvatar = findViewById(R.id.me_avatar);
        tilName = findViewById(R.id.me_name);
        tilNumber = findViewById(R.id.me_telephone);
        tilEmail = findViewById(R.id.me_email);
        Toolbar toolbar = findViewById(R.id.toolbarMe);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        tilName.getEditText().setText(sharedPreferences.getString("me_name",""));
        tilNumber.getEditText().setText(sharedPreferences.getString("me_number",""));
        tilEmail.getEditText().setText(sharedPreferences.getString("me_email",""));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.adduser_toolbar,menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String name = tilName.getEditText().getText().toString();
        String number = tilNumber.getEditText().getText().toString();
        String email = tilEmail.getEditText().getText().toString();

        if (TextUtils.isEmpty(name)){
            tilName.setError(getString(R.string.missingname));
            return super.onOptionsItemSelected(item);
        }
        if (TextUtils.isEmpty(number)){
            tilNumber.setError(getString(R.string.missingNumber));
            return super.onOptionsItemSelected(item);
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("me_name",name);
        editor.putString("me_number",number);
        if (!TextUtils.isEmpty(email)){
            editor.putString("me_email",email);
        }
        editor.apply();
        finish();
        return super.onOptionsItemSelected(item);
    }
}
