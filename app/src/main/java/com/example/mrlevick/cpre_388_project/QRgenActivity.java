package com.example.mrlevick.cpre_388_project;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONException;
import org.json.JSONObject;

public class QRgenActivity extends AppCompatActivity {

    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent i;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    i = new Intent(QRgenActivity.this, MainActivity.class);
                    startActivity(i);
                    return true;
                case R.id.navigation_scanner:
                    i = new Intent(QRgenActivity.this, ScannerActivity.class);
                    startActivity(i);
                    return true;
                case R.id.navigation_qrGen:
                    return true;
            }
            return false;
        }
    };

    private ImageView qrImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrgen);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mTextMessage.setText(R.string.qrMessage);

        //Generate QR code
        qrImage = findViewById(R.id.imageView);
        Intent i = getIntent();
        String name = i.getStringExtra("name");
        String number = i.getStringExtra("number");
        String email = i.getStringExtra("email");

        JSONObject json = new JSONObject();

        try {
            json.put("name", name);
            json.put("number", number);
            json.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MultiFormatWriter mWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = mWriter.encode(json.toString(), BarcodeFormat.QR_CODE, 500,500);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            qrImage.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

}
