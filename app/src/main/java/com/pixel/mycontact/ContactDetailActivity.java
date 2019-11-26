package com.pixel.mycontact;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.sumimakito.awesomeqr.AwesomeQrRenderer;
import com.github.sumimakito.awesomeqr.RenderResult;
import com.github.sumimakito.awesomeqr.option.RenderOption;
import com.github.sumimakito.awesomeqr.option.background.BlendBackground;
import com.github.sumimakito.awesomeqr.option.color.Color;
import com.github.sumimakito.awesomeqr.option.logo.Logo;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.pixel.mycontact.beans.DetailList;
import com.pixel.mycontact.beans.People;
import com.pixel.mycontact.daos.PeopleDB;
import com.pixel.mycontact.utils.PeopleResolver;
import com.pixel.mycontact.utils.PermissionsUtils;
import com.pixel.mycontact.utils.StyleUtils;

import java.util.ArrayList;
import java.util.List;

public class ContactDetailActivity extends AppCompatActivity {

    private List<DetailList> details;
    private People people;
    private CoordinatorLayout cdrLay;
    private SharedPreferences preferences;
    private PeopleDB peopleDB;
    private Activity activity;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contact_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.shareContact) {
            Log.d("sharePeople", people.toJSON());
            generateQR();

        }
        return super.onOptionsItemSelected(item);
    }

    private void generateQR() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.flat)
                .copy(Bitmap.Config.ARGB_8888, true);


        BlendBackground background = new BlendBackground();
        background.setBitmap(bitmap);
//        background.setClippingRect(new Rect(100, 100, 1400, 1400));
        background.setAlpha(1.0f);
        background.setBorderRadius(10);

        Color color = new Color();
        color.setLight(0xfff4f7f3);
        color.setDark(0xff2e403a);
        color.setAuto(false);

        Logo logo = new Logo();
        Bitmap bitmap1 = BitmapFactory.decodeResource
                (getResources(), R.drawable.pixel)
                .copy(Bitmap.Config.ARGB_8888, true);
        logo.setBitmap(bitmap1);
        logo.setBorderRadius(10); // radius for logo's corners
        logo.setBorderWidth(10); // width of the border to be added around the logo
        logo.setScale(0.2f); // scale for the logo in the QR code
//        logo.setClippingRect(new Rect(0, 0, 200, 200)); // crop the logo image before applying it to the QR code
        boolean isUsingBase64 = preferences.getBoolean("base64", false);

        RenderOption renderOption = new RenderOption();
        String stringContent;
        if (isUsingBase64) {
            stringContent = PeopleResolver.urlHeader + PeopleResolver.b64QueryData +
                    Base64.encodeToString(people.toJSON().getBytes(), Base64.DEFAULT);
            Log.d("base64", stringContent);
            renderOption.setContent(stringContent);
        } else {
            stringContent = PeopleResolver.urlHeader + PeopleResolver.jsonQueryPara + people.toJSON();
            renderOption.setContent(stringContent);
        }
        Log.d("generateQR: length", String.valueOf(stringContent.length()));
        if (stringContent.length() >= 512) {
            Snackbar.make(cdrLay, getString(R.string.too_large), Snackbar.LENGTH_SHORT).show();
        } else {
            renderOption.setSize(800);
            renderOption.setBorderWidth(20);
            renderOption.setRoundedPatterns(true);
            renderOption.setPatternScale(0.5f);
            renderOption.setColor(color);
            renderOption.setBackground(background);
            renderOption.setClearBorder(false);
            renderOption.setLogo(logo);

            try {
                RenderResult renderResult = AwesomeQrRenderer.render(renderOption);
                if (renderResult.getBitmap() != null) {
                    ImageView imgQRCode = new ImageView(getApplicationContext());
                    imgQRCode.setImageBitmap(renderResult.getBitmap());
                    AlertDialog.Builder builder = new AlertDialog.Builder(ContactDetailActivity.this/*,R.style.AlertDialogCustom*/);
                    builder.setTitle(R.string.scanthis)
                            .setView(imgQRCode)
                            .setNegativeButton(R.string.done, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);
        activity = this;
        Toolbar toolbar = findViewById(R.id.toolbar);
        CollapsingToolbarLayout ctLayout = findViewById(R.id.toolbar_layout);
//        ctLayout.setExpandedTitleColor(getResources().getColor(R.color.white));
//        ctLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.colorOnPrimary));
        cdrLay = findViewById(R.id.cdntlayout);

        RecyclerView recyclerView = findViewById(R.id.detail_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        final Intent intent = getIntent();
        //接收来自上个活动传入的序列化的people对象

        people = (People) intent.getSerializableExtra("people");
        toolbar.setTitle(people.getName());
        setSupportActionBar(toolbar);
        StyleUtils.setStatusBarTransparent(getWindow(), ((ColorDrawable) toolbar.getBackground()).getColor());
        //初始化详细信息列表，并设置适配器

        initList();
        DetailAdapter adapter = new DetailAdapter(details);
        recyclerView.setAdapter(adapter);
        //到AddUserActivity去修改联系人，extra传入序列化的people对象
        FloatingActionButton fab = findViewById(R.id.fab_edit);

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
                                    peopleDB.closeDB();
                                    finish();
                                }
                            }
                        })
                        .setNegativeButton(R.string.cancelDia, null);
                builder.show();
                return false;
            }
        });
        if (people.getId() < 0) {
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
                if (PermissionsUtils.hasOrRequestForCall(activity, 1)) {
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
                    Snackbar.make(cdrLay, R.string.no_email, Snackbar.LENGTH_SHORT)
                            .setAction(R.string.settings, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(ContactDetailActivity.this,
                                            AddUserActivity.class);
                                    intent.putExtra("people", people);
                                    startActivity(intent);
                                    finish();
                                }
                            }).show();
                } else {
                    try {
                        Intent intent1 = new Intent(Intent.ACTION_SENDTO);
                        intent1.setData(Uri.parse("mailto:" + people.getEmail()));
                        intent1.putExtra(Intent.EXTRA_TEXT, "\nsent from " + R.string.app_name);
                        startActivity(intent1);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Snackbar.make(cdrLay, getString(R.string.error_action), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
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
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Snackbar.make(cdrLay, getString(R.string.error_action), Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }
                            }
                        })
                        .setNegativeButton(R.string.cancelDia, null)
                        .show();
            }
        });

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
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
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            Intent intent = new Intent(Intent.ACTION_CALL);
                            intent.setData(Uri.parse("tel:" + item[which]));
                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Snackbar.make(cdrLay, getString(R.string.error_action),
                                    Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    }
                })
                .setNegativeButton(R.string.cancelDia, null)
                .show();

    }

    //权限请求回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callPeople();
            } else {
                Snackbar.make(cdrLay, R.string.perde, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
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
        if (people.getId() >= 0) {
            DetailList id = new DetailList("ID", R.drawable.ic_code_black_24dp, Integer.toString(people.getId()));
            details.add(id);
        }

    }
}
