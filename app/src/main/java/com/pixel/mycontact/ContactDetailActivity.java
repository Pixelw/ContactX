package com.pixel.mycontact;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.github.sumimakito.awesomeqr.AwesomeQrRenderer;
import com.github.sumimakito.awesomeqr.RenderResult;
import com.github.sumimakito.awesomeqr.option.RenderOption;
import com.github.sumimakito.awesomeqr.option.color.Color;
import com.pixel.mycontact.beans.DetailList;
import com.pixel.mycontact.beans.People;
import com.pixel.mycontact.daos.PeopleDB;

import java.util.ArrayList;
import java.util.List;

public class ContactDetailActivity extends AppCompatActivity {

    private List<DetailList> details;
    private People people;
    private CoordinatorLayout cdrLay;

    private PeopleDB peopleDB;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contact_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.shareContact){
            Log.d("sharePeople", people.toString());
            Color color = new Color();
            color.setLight(0xFFFFFFFF);
            color.setDark(0xff6b38fb);
            color.setBackground(0xffffffff);
            color.setAuto(false);
            RenderOption renderOption = new RenderOption();
            renderOption.setContent(people.toString());
            renderOption.setSize(1000);
            renderOption.setBorderWidth(20);
            renderOption.setColor(color);
            renderOption.setClearBorder(true); // if set to true, the background will NOT be drawn on the border area
            try {
                RenderResult renderResult = AwesomeQrRenderer.render(renderOption);
                if (renderResult.getBitmap() != null){
                    ImageView imgQRCode = new ImageView(getApplicationContext());
                    imgQRCode.setImageBitmap(renderResult.getBitmap());
                    AlertDialog.Builder builder = new AlertDialog.Builder(ContactDetailActivity.this/*,R.style.AlertDialogCustom*/);
                    builder.setTitle("Scan this QRCode with MyContact")
                            .setView(imgQRCode)
                            .show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        CollapsingToolbarLayout ctLayout = findViewById(R.id.toolbar_layout);
        ctLayout.setExpandedTitleColor(getResources().getColor(R.color.colorPrimary));
        ctLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.colorText));
        cdrLay = findViewById(R.id.cdntlayout);

        RecyclerView recyclerView = findViewById(R.id.detail_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        final Intent intent = getIntent();
        //接收来自上个活动传入的序列化的people对象

        people = (People) intent.getSerializableExtra("people");
        toolbar.setTitle(people.getName());
        setSupportActionBar(toolbar);
        //初始化详细信息列表，并设置适配器

        initList();
        DetailAdapter adapter = new DetailAdapter(details);
        recyclerView.setAdapter(adapter);
        //到AddUserActivity去修改联系人，extra传入序列化的people对象
        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContactDetailActivity.this, AddUserActivity.class);
                intent.putExtra("people", people);
                startActivity(intent);
                finish();
            }
        });
        //设置长按删除联系人，弹出确认对话框
        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                peopleDB = new PeopleDB(ContactDetailActivity.this);
                final AlertDialog.Builder builder = new AlertDialog.Builder(ContactDetailActivity.this);
                builder.setTitle(R.string.deletecontact)
                        .setMessage(getString(R.string.deleteQuestion) + "\n" + people.getName())
                        .setPositiveButton(R.string.comfirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (peopleDB.deleteContact(people.getId()) > 0) {
                                    finish();
                                }
                            }
                        })
                        .setNegativeButton(R.string.cancelDia, null);
                builder.show();
                return false;
            }
        });
        if (people.getId() < 0){
            fab.hide();
        }
        //设置返回键
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //拨打电话
        LinearLayout call = findViewById(R.id.ivCall);
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //确认并申请打电话权限
                if (ContextCompat.checkSelfPermission
                        (ContactDetailActivity.this, Manifest.permission.CALL_PHONE) !=
                        PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ContactDetailActivity.this, new String[]
                            {
                                    Manifest.permission.CALL_PHONE
                            }, 1);
                } else {
                    callPeople();
                }
            }
        });
        //发送电子邮件
        LinearLayout sendEmail = findViewById(R.id.sendEmail);
        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (people.getEmail().equals("")) {
                    Snackbar.make(cdrLay, R.string.no_email, Snackbar.LENGTH_SHORT).show();
                } else {
                    Intent intent1 = new Intent(Intent.ACTION_SENDTO);
                    intent1.setData(Uri.parse("mailto:" + people.getEmail()));
                    intent1.putExtra(Intent.EXTRA_TEXT, "\nsent from My Contact");
                    startActivity(intent1);
                }

            }
        });
        //发送短信
        LinearLayout sendSms = findViewById(R.id.sendSms);
        sendSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] item;
                AlertDialog.Builder builder = new AlertDialog.Builder(ContactDetailActivity.this);
                if (people.getNumber2().equals("") || people.getNumber1().equals("")) {
                    item = new String[]{people.getNumber()};
                } else {
                    item = new String[]{people.getNumber1(), people.getNumber2()};
                }
                builder.setTitle(R.string.picknumber)
                        .setItems(item, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    Intent intentSms = new Intent(Intent.ACTION_VIEW);
                                    intentSms.setData(Uri.parse("smsto:"));
                                    intentSms.putExtra("address", item[which]);
                                    intentSms.setType("vnd.android-dir/mms-sms");
                                    startActivity(intentSms);
                                } catch (SecurityException e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton(R.string.cancelDia, null)
                        .show();
            }
        });
    }

    //电话动作
    private void callPeople() {
        final String[] item;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (people.getNumber2().equals("") || people.getNumber1().equals("")) {
            item = new String[]{people.getNumber()};
        } else {
            item = new String[]{people.getNumber1(), people.getNumber2()};
        }
        builder.setTitle(R.string.picknumber)
                .setItems(item, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            Intent intent = new Intent(Intent.ACTION_CALL);
                            intent.setData(Uri.parse("tel:" + item[which]));
                            startActivity(intent);
                        } catch (SecurityException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton(R.string.cancelDia, null)
                .show();

    }

    //权限请求回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callPeople();
                } else {
                    Snackbar.make(cdrLay, R.string.perde, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                break;
            default:
        }
    }
    //生成详细信息的列表
    private void initList() {
        details = new ArrayList<>();
        if (!people.getNumber1().equals("")) {
            DetailList phone = new DetailList(getString(R.string.phone_number), R.drawable.ic_phone_iphone_black_24dp, people.getNumber1());
            details.add(phone);
        }
        if (!people.getNumber2().equals("")) {
            DetailList tele = new DetailList(getString(R.string.phone_number), R.drawable.ic_phone_black_24dp, people.getNumber2());
            details.add(tele);
        }
        if (!people.getEmail().equals("")) {
            DetailList email = new DetailList(getString(R.string.email_address), R.drawable.ic_email_black_24dp, people.getEmail());
            details.add(email);
        }
        if (people.getBirthMonth() != 0) {
            DetailList birth = new DetailList(getString(R.string.birthday), R.drawable.ic_date_range_black_24dp, people.getBirthMonth() + "/" + people.getBirthDay());
            details.add(birth);
        }
        if (!people.getNote().equals("")) {
            DetailList note = new DetailList(getString(R.string.notes), R.drawable.ic_format_list_bulleted_black_24dp, people.getNote());
            details.add(note);
        }
        if (people.getId() >= 0){
            DetailList id = new DetailList("ID", R.drawable.ic_code_black_24dp, Integer.toString(people.getId()));
            details.add(id);
        }

    }
}
