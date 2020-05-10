package com.pixel.mycontact;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pixel.mycontact.adapter.PeopleAdapter;
import com.pixel.mycontact.beans.People;
import com.pixel.mycontact.daos.RealmTransactions;
import com.pixel.mycontact.utils.StyleUtils;

import java.util.ArrayList;
import java.util.List;

public class ImportActivity extends AppCompatActivity {

//    private PeopleDB peopleDB;
    private RealmTransactions realmTransactions;
    private List<People> sysConList;
    private PeopleAdapter adapter;

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.adduser_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.accept) {
            realmTransactions.insertContacts(adapter.getCheckedPeople(), new RealmTransactions.Callback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(ImportActivity.this, R.string.imported, Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onFailed(String reason) {
                    Toast.makeText(ImportActivity.this, R.string.failed, Toast.LENGTH_SHORT).show();
                }
            });
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
        StyleUtils.setStatusBarTransparent(getWindow(), toolbar);
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

        readAllSystemContacts();

//        peopleDB = new PeopleDB(ImportActivity.this);
        realmTransactions = new RealmTransactions(ContactXApplication.getRealmInstance());

        adapter = new PeopleAdapter(sysConList,this);
        recyclerView.setAdapter(adapter);
        Toast.makeText(ImportActivity.this, getString(R.string.import_tutorial), Toast.LENGTH_LONG).show();
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
                    People people = new People(name, null, number, null, null,
                            0, 0, 0, null);
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
