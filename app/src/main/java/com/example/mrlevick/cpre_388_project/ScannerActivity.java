package com.example.mrlevick.cpre_388_project;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

public class ScannerActivity extends AppCompatActivity {

    SurfaceView cameraView;
    BarcodeDetector barcode;
    CameraSource cameraSource;
    SurfaceHolder holder;

    /**Navigation bar listener */
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent i;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    i = new Intent(ScannerActivity.this, MainActivity.class);
                    startActivity(i);
                    return true;
                case R.id.navigation_scanner:
                    return true;
                case R.id.navigation_qrGen:
                    i = new Intent(ScannerActivity.this, QRgenActivity.class);
                    startActivity(i);
                    return true;
            }
            return false;
        }
    };

    /**When ScannerActivity is created, navigate, use camera to scan for a QR code*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        /*Create Camera view*/
        cameraView = (SurfaceView) findViewById(R.id.cameraView);
        cameraView.setZOrderMediaOverlay(true);
        holder = cameraView.getHolder();

        /*Create barcode detector set to scan for QR codes, which the camera view will implement*/
        barcode = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.QR_CODE).build();
        if(!barcode.isOperational())
        {
            Toast.makeText(getApplicationContext(), "Sorry, couldn't setup", Toast.LENGTH_SHORT).show();
        }

        /*Set camera view to scan to implement barcode detector and set display information*/
        cameraSource = new CameraSource.Builder(this, barcode)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(24)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(1020, 1020)
                .build();

        /*Creates Camera view and sets callback when camera detects QR code*/
        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            /**Creates camera view if having camera permissions*/
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try{
                    if(ContextCompat.checkSelfPermission(ScannerActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
                    {
                        cameraSource.start(cameraView.getHolder());
                    }

                }catch (Exception e)
                {
                    Log.i("EXC", "EXCEPTION CREATING SURFACE\n");
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });

        barcode.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            /**When camera detects QR code, it sends QR code information back in an Intent*/
            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if(barcodes.size()>0)
                {
                    Intent i = new Intent();
                    i.putExtra("qrCode", barcodes.valueAt(0));
                    setResult(RESULT_OK, i);
                    finish();

                }
            }
        });
    }

}
