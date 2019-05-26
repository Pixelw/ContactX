package com.pixel.mycontact;

import android.content.DialogInterface;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.Result;
import com.pixel.mycontact.beans.People;
import com.pixel.mycontact.daos.PeopleDB;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QRCodeScanActivity extends AppCompatActivity {
    private ZXingScannerView scannerView;
    private PeopleDB peopleDB;

    private ZXingScannerView.ResultHandler resultHandler = new ZXingScannerView.ResultHandler() {
        @Override
        public void handleResult(Result rawResult) {
//            scannerView.resumeCameraPreview(resultHandler);
            if (rawResult.getText().startsWith("pixel://mct")) {
                Uri url = Uri.parse(rawResult.getText());
                final People peopleFromQR = PeopleResolver.resolveJson(url.getQueryParameter("json"));

                if (peopleFromQR != null) {

                    Log.d("scanResult", peopleFromQR.toJSON());
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

            }else{
                AlertDialog.Builder incapQr = new AlertDialog.Builder(QRCodeScanActivity.this);
                incapQr.setTitle(R.string.invalidqr)
                        .setMessage(R.string.not_supported_qr)
                        .setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                scannerView.resumeCameraPreview(resultHandler);
                            }
                        })
                        .show();

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_scan);

        scannerView = findViewById(R.id.scannerView);
        scannerView.setResultHandler(resultHandler);


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
