package com.pixel.mycontact;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.pixel.mycontact.beans.People;
import com.pixel.mycontact.daos.PeopleDB;

import java.util.ArrayList;
import java.util.List;

public class ImportActivity extends AppCompatActivity {

    private PeopleDB peopleDB;
    private List<People> sysConList;
    private PeopleAdapter adapter;

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.adduser_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.accept) {
            if (peopleDB.insertSysContacts(adapter.getCheckedPeople()) > 0) {
                Toast.makeText(ImportActivity.this, "Imported", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);
        Toolbar toolbar = findViewById(R.id.toolbarImport);
        toolbar.setTitle(R.string.import_contact_from_system);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        RecyclerView recyclerView = findViewById(R.id.importList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        sysConList = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(ImportActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ImportActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, 1);
        } else {
            readAllSystemContacts();
        }

        peopleDB = new PeopleDB(ImportActivity.this);
        adapter = new PeopleAdapter(sysConList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        LinearLayout layout = findViewById(R.id.importList);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                readAllSystemContacts();
            } else {
                Snackbar.make(layout, R.string.perde, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }
    }

    public void readAllSystemContacts() {
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(ContactsContract.CommonDataKinds
                    .Phone.CONTENT_URI, null, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String name = cursor.getString(cursor.getColumnIndex
                            (ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String number = cursor.getString(cursor.getColumnIndex
                            (ContactsContract.CommonDataKinds.Phone.NUMBER));
                    People people = new People(name, "", number, "", "",
                            0, 0, 0, "Imported", -139);
                    sysConList.add(people);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
