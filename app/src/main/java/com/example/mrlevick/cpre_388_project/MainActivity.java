package com.example.mrlevick.cpre_388_project;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.barcode.Barcode;
import com.journeyapps.barcodescanner.BarcodeResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final int CONTACTS_REQUEST = 55;
    private boolean contactPermission = false;
    private TextView mTextMessage;
    public static final int REQUEST_CODE = 100;
    public static final int PERMISSION_REQUEST = 200;
    private String userName = "Leo Freier";
    private String userPhone = "5554446789";
    private String userEmail = "lmfreier@iastate.edu";
    private String userWebsite = "leofreier.com";
    private String userNickname = "Leo";
    private CheckBox boxNumber;
    private CheckBox boxEmail;
    private CheckBox boxWebsite;
    private CheckBox boxNickname;
    private TextView userNameText;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent i;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    return true;
                case R.id.navigation_scanner:
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        i = new Intent(MainActivity.this, ScannerActivity.class);
                        startActivityForResult(i, REQUEST_CODE);
                        return true;
                    } else {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST);
                        break;
                    }
                case R.id.navigation_qrGen:
                    i = new Intent(MainActivity.this, QRgenActivity.class);
                    i.putExtra("name", userName);

                    if (!boxNumber.isChecked()) {
                        userPhone = "";
                    }
                    if (!boxEmail.isChecked()) {
                        userEmail = "";
                    }
                    if (!boxWebsite.isChecked()) {
                        userWebsite = "";
                    }
                    if (!boxNickname.isChecked()) {
                        userNickname = "";
                    }
                    i.putExtra("number", userPhone);
                    i.putExtra("email", userEmail);
                    i.putExtra("website", userWebsite);
                    i.putExtra("nickname", userNickname);
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
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST);
        }
        boxNumber = findViewById(R.id.phoneNumber);
        boxEmail = findViewById(R.id.email);
        boxWebsite = findViewById(R.id.website);
        boxNickname = findViewById(R.id.nickname);
        userNameText = (TextView) findViewById(R.id.userName);
    }

    @Override
    protected void onResume(){
        super.onResume();
        requestContactsPermission();
    }

    @Override
    protected void onPause(){
        //save stuff to sharedPreferences
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                Barcode barcode = data.getParcelableExtra("qrCode");
                Log.i("Success", "GOT QR Code: " + barcode.displayValue);
                Toast.makeText(getApplicationContext(), "GOT QR DATA: " + barcode.displayValue, Toast.LENGTH_SHORT).show();
                JSONObject readCode = null;

                Intent writeProfileIntent = new Intent(Intent.ACTION_INSERT);
                writeProfileIntent.setType(ContactsContract.Contacts.CONTENT_TYPE);
                String contactName = "", contactPhone = "", contactEmail = "", contactWebsite = "", contactNickname = "";

                try{
                    readCode = new JSONObject(barcode.rawValue);
                    contactName = readCode.getString("name");
                    System.out.println("Name: " + contactName);
                    contactPhone = readCode.getString("number");
                    System.out.println("Phone: " + contactPhone);
                    contactEmail = readCode.getString("email");
                    System.out.println("Email: " + contactEmail);
                    contactWebsite = readCode.getString("website");
                    Log.i("Website: ", contactWebsite);
                    contactNickname = readCode.getString("nickname");
                    System.out.println("Nickname: " + contactNickname);
                }catch(JSONException e){
                    Log.i("JSON Failure", "JSONObjet failed to create");
                }

                ArrayList<ContentValues> otherData = new ArrayList<ContentValues>();
                ContentValues websiteRow = new ContentValues();
                ContentValues nicknameRow = new ContentValues();
                if(!contactName.equals("")) {
                    writeProfileIntent.putExtra(ContactsContract.Intents.Insert.NAME, contactName);
                }
                if(!contactPhone.equals("")) {
                    writeProfileIntent.putExtra(ContactsContract.Intents.Insert.PHONE, contactPhone);
                }
                if(!contactEmail.equals("")) {
                    writeProfileIntent.putExtra(ContactsContract.Intents.Insert.EMAIL, contactEmail);
                }
                if(!contactWebsite.equals("")) {
                    websiteRow.put(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE);
                    websiteRow.put(ContactsContract.CommonDataKinds.Website.URL, contactWebsite);
                    otherData.add(websiteRow);
                }
                if(!contactNickname.equals("")) {
                    nicknameRow.put(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE);
                    nicknameRow.put(ContactsContract.CommonDataKinds.Nickname.NAME, contactNickname);
                    otherData.add(nicknameRow);
                }

                writeProfileIntent.putParcelableArrayListExtra(ContactsContract.Intents.Insert.DATA, otherData);
                this.startActivity(writeProfileIntent);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case CONTACTS_REQUEST:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //do stuff
                    contactPermission = true;
                    retrieveUserName();
                } else {
                    contactPermission = false;
                    Toast.makeText(this, "Please allow permission to read and write contacts.", Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void requestContactsPermission() {
        if (checkSelfPermission(Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED ) {
            String[] requiredPerm = {Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS};
            ActivityCompat.requestPermissions(this, requiredPerm, CONTACTS_REQUEST);
        } else {
            contactPermission = true;
            retrieveUserName();
        }
    }

    private void retrieveUserName() {
        String contactName = null;
        String userId = "";
        // querying profile name
        Cursor cursor = getContentResolver().query(ContactsContract.Profile.CONTENT_URI, null, null, new String[]{}, null);
        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Profile.DISPLAY_NAME));
//            userId = cursor.getString(cursor.getColumnIndex(ContactsContract.Profile.NAME_RAW_CONTACT_ID));
//            cursor = getContentResolver().query(ContactsContract.RawContacts.CONTENT_URI, null,  null, null, null);
//            cursor.moveToFirst();
//            cursor.getString(cursor.getColumnIndex(ContactsContract.RawContacts.Data.DATA1));

        }
        if(true){
            //get sharedpreferences
        }
        else {
            //tell user a profile must be created
            DialogInterface.OnClickListener createProfileListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            break;
                    }
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("No user profile found. Please enter your name manually.").setPositiveButton("OK", createProfileListener).show();
        }
        cursor.close();
        if (contactName != null) {
            userNameText.setText(contactName);
        }

    }
}
