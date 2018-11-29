package com.example.mrlevick.cpre_388_project;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.barcode.Barcode;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    public static final int REQUEST_CODE = 100;
    public static final int PERMISSION_REQUEST = 200;

    private CheckBox boxNumber;
    private CheckBox boxEmail;
    private TextView userName;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent i;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    return true;
                case R.id.navigation_scanner:
                    if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
                    {
                        i = new Intent(MainActivity.this, ScannerActivity.class);
                        startActivityForResult(i, REQUEST_CODE);
                        return true;
                    }
                    else
                        {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST);
                        }
                case R.id.navigation_qrGen:
                    i = new Intent(MainActivity.this, QRgenActivity.class);
                    i.putExtra("name", userName.getText());

                    if(boxNumber.isChecked())
                    {
                        i.putExtra("number", boxNumber.getText());
                    }
                    if(boxEmail.isChecked())
                    {
                        i.putExtra("email", "jDoe@email.com");
                    }
                    startActivity(i);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        //mTextMessage.setText(R.string.us);
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST);
        }
        boxNumber = findViewById(R.id.phoneNumber);
        boxEmail = findViewById(R.id.email);
        userName = (TextView) findViewById(R.id.userName);


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK)
        {
            if(data != null)
            {
                Barcode barcode = data.getParcelableExtra("qrCode");
                Log.i("Success", "GOT QR Code: " + barcode.displayValue);
                Toast.makeText(getApplicationContext(), "GOT QR DATA: " + barcode.displayValue, Toast.LENGTH_SHORT).show();
            }
        }

    }

}
