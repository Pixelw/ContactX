package com.pixel.mycontact;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.Result;
import com.pixel.mycontact.beans.People;
import com.pixel.mycontact.daos.PeopleDB;
import com.pixel.mycontact.utils.PermissionsUtils;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QRCodeScanActivity extends AppCompatActivity {
    private ZXingScannerView scannerView;
    private PeopleDB peopleDB;
    private People peopleFromQR;
    private LinearLayout linearLayout;

    private ZXingScannerView.ResultHandler resultHandler = new ZXingScannerView.ResultHandler() {
        @Override
        public void handleResult(Result rawResult) {
//            scannerView.resumeCameraPreview(resultHandler);
            if (rawResult.getText().startsWith("pixel://mct?json")) {
                Uri url = Uri.parse(rawResult.getText());
                peopleFromQR = PeopleResolver.resolveJson(url.getQueryParameter("json"));
                handleUrl();
            } else if (rawResult.getText().startsWith("pixel://mct?b64")) {
                Uri url = Uri.parse(rawResult.getText());
                peopleFromQR = PeopleResolver.resolveBase64Json(url.getQueryParameter("b64"));
                handleUrl();
            } else {
                AlertDialog.Builder invalidQr = new AlertDialog.Builder(QRCodeScanActivity.this);
                invalidQr.setTitle(R.string.invalidqr)
                        .setMessage(R.string.not_supported_qr)
                        .setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                scannerView.resumeCameraPreview(resultHandler);
                            }
                        })
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                scannerView.resumeCameraPreview(resultHandler);
                            }
                        })
                        .show();

            }
        }
    };

    private void handleUrl() {
        if (peopleFromQR != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(QRCodeScanActivity.this);
            builder.setTitle(R.string.foundcontact)
                    .setMessage(getString(R.string.addthis) + "\n" + peopleFromQR.getName())
                    .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (peopleDB.insertContact(peopleFromQR) > 0) {
                                Toast.makeText(getApplicationContext(), getString(R.string.contactsave), Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    })
                    .setNegativeButton(R.string.cancelDia, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            scannerView.resumeCameraPreview(resultHandler);
                        }
                    })
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            scannerView.resumeCameraPreview(resultHandler);
                        }
                    })
                    .show();

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_scan);
        linearLayout = findViewById(R.id.layout_qrscan);

        Toolbar toolbar = findViewById(R.id.toolbarScan);
        toolbar.setTitle(getString(R.string.scan_qr));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        peopleDB = new PeopleDB(QRCodeScanActivity.this);
        if (PermissionsUtils.getPermissionForCamera(this)) {
            initScanner();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 10) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initScanner();
            } else {
                Snackbar.make(linearLayout, R.string.perde, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }
    }

    private void initScanner() {
        scannerView = findViewById(R.id.scannerView);
        scannerView.setResultHandler(resultHandler);
        scannerView.startCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (scannerView != null){
            scannerView.setResultHandler(resultHandler);
            scannerView.startCamera();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (scannerView != null){
            scannerView.stopCamera();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        peopleDB.closeDB();
    }
}
