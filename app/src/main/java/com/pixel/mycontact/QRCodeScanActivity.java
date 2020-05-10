package com.pixel.mycontact;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.zxing.Result;
import com.pixel.mycontact.beans.People;
import com.pixel.mycontact.daos.RealmTransactions;
import com.pixel.mycontact.utils.PeopleUrl;
import com.pixel.mycontact.utils.PermissionsUtils;
import com.pixel.mycontact.utils.StyleUtils;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QRCodeScanActivity extends AppCompatActivity {
    private ZXingScannerView scannerView;
    private RealmTransactions realmTransactions;
//    private PeopleDB peopleDB;

    private ZXingScannerView.ResultHandler resultHandler = new ZXingScannerView.ResultHandler() {
        @Override
        public void handleResult(Result rawResult) {
//            scannerView.resumeCameraPreview(resultHandler);
            People peopleFromQR = PeopleUrl.parseUrl(rawResult.toString());
             if (peopleFromQR !=null) {
                handleUrl(peopleFromQR);
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

    private void handleUrl(final People people) {
        if (people != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(QRCodeScanActivity.this);
            builder.setTitle(R.string.foundcontact)
                    .setMessage(getString(R.string.addthis) + "\n" + people.getName())
                    .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                          realmTransactions.insertAContact(people, new RealmTransactions.Callback() {
                              @Override
                              public void onSuccess() {
                                  Toast.makeText(getApplicationContext(), getString(R.string.contactsave), Toast.LENGTH_SHORT).show();
                                  finish();
                              }

                              @Override
                              public void onFailed(String reason) {
                                  Toast.makeText(getApplicationContext(), getString(R.string.failed), Toast.LENGTH_SHORT).show();
                              }
                          });
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

        Toolbar toolbar = findViewById(R.id.toolbarScan);
        toolbar.setTitle(getString(R.string.scan_qr));
        setSupportActionBar(toolbar);
        StyleUtils.setStatusBarTransparent(getWindow(), toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        realmTransactions = new RealmTransactions(ContactXApplication.getRealmInstance());
        if (PermissionsUtils.hasOrRequestForCamera(this, 10)) {
            initScanner();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 10) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initScanner();
            } else {
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle(R.string.perde);
                dialog.setMessage(R.string.error_action);
                dialog.setCancelable(false);
                dialog.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PermissionsUtils.hasOrRequestForCamera(QRCodeScanActivity.this, 10);
                    }
                });

                dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
            }
        }
    }

    private void initScanner() {
        scannerView = findViewById(R.id.scannerView);
        scannerView.setResultHandler(resultHandler);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (scannerView != null){
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
    }
}
