package com.pixel.mycontact;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
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
            Gson gson = new Gson();
            final People peopleFromQR = gson.fromJson(rawResult.getText(), People.class);
            peopleFromQR.appendNote("from QRCode");
            String toastStr = peopleFromQR.getName() + ":" + peopleFromQR.getNumber();
            Log.d("scanResult", peopleFromQR.toString());
            AlertDialog.Builder builder = new AlertDialog.Builder(QRCodeScanActivity.this);
            builder.setTitle("Found contact in this QRCode")
                    .setMessage("Add this contact?" + "\n" + peopleFromQR.getName())
                    .setPositiveButton("Add", new DialogInterface.OnClickListener() {
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
//            Toast.makeText(getApplicationContext(), toastStr, Toast.LENGTH_LONG).show();
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
