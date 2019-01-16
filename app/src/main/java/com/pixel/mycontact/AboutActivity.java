package com.pixel.mycontact;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        TextView myGmail = findViewById(R.id.myGmail);
        myGmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Intent.ACTION_SENDTO);
                intent1.setData(Uri.parse("mailto:sulycarl34@gmail.com"));
                intent1.putExtra(Intent.EXTRA_TEXT, "\nsent from My Contact");
                startActivity(intent1);
            }
        });
    }
}
