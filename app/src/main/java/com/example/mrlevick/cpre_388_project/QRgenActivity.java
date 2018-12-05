package com.example.mrlevick.cpre_388_project;

import android.content.Intent;
import android.content.SharedPreferences;
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

    private final String MY_PREFS_NAME = "UserInfo388";
    private TextView mTextMessage;
    private ImageView qrImage;

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
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String toAdd = prefs.getString("toAdd", "0000");
        String name = prefs.getString("username", "");
        String number = "", email = "", website = "", nickname = "";

        if(toAdd.substring(0,1).equals("1"))
            number = prefs.getString("number", "");
        if(toAdd.substring(1,2).equals("1"))
            email = prefs.getString("email", "");
        if(toAdd.substring(2,3).equals("1"))
            website = prefs.getString("website", "");
        if(toAdd.substring(3,4).equals("1"))
            nickname = prefs.getString("nickname", "");

        JSONObject json = new JSONObject();

        try {
            json.put("name", name);
            json.put("number", number);
            json.put("email", email);
            json.put("website", website);
            json.put("nickname", nickname);
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
