package com.pixel.mycontact;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.zxing.Result;
import com.pixel.mycontact.beans.People;
import com.pixel.mycontact.daos.PeopleDB;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QRCodeScanActivity extends AppCompatActivity {
    private ZXingScannerView scannerView;
    private PeopleDB peopleDB;
    private People peopleFromQR;

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
                            peopleDB = new PeopleDB(QRCodeScanActivity.this);
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

        scannerView = findViewById(R.id.scannerView);
        scannerView.setResultHandler(resultHandler);

        Toolbar toolbar = findViewById(R.id.toolbarScan);
        toolbar.setTitle(getString(R.string.scan_qr));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        scannerView.setResultHandler(resultHandler);
        scannerView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }
}
