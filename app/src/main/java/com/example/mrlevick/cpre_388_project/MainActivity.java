package com.example.mrlevick.cpre_388_project;

import android.Manifest;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity {

    private static final String[] PROJECTION =
            {
                    ContactsContract.Data.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Email.DATA,
                    ContactsContract.CommonDataKinds.Phone.DATA
            };
    private final int CONTACTS_REQUEST = 55;
    private boolean contactPermission = false;
    private boolean profileExists = false;
    private TextView mTextMessage;
    public static final int REQUEST_CODE = 100;
    public static final int PERMISSION_REQUEST = 200;
    private String userPhone = "";
    private String userEmail = "";
    private String userWebsite = "";
    private String userNickname = "";
    private CheckBox boxNumber;
    private CheckBox boxEmail;
    private CheckBox boxWebsite;
    private CheckBox boxNickname;
    private TextView userName;

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
                    i.putExtra("name", userName.getText());

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
        userName = (TextView) findViewById(R.id.userName);
    }

    @Override
    protected void onResume(){
        super.onResume();
        requestContactsPermission();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                Barcode barcode = data.getParcelableExtra("qrCode");
                Log.i("Success", "GOT QR Code: " + barcode.displayValue);
                Toast.makeText(getApplicationContext(), "GOT QR DATA: " + barcode.displayValue, Toast.LENGTH_SHORT).show();

                ContentValues values = new ContentValues();
                values.put(ContactsContract.RawContacts.ACCOUNT_TYPE, "CprE388");
                values.put(ContactsContract.RawContacts.ACCOUNT_NAME, "Contact388");
                Uri contactUri = getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, values);
                long contactId = ContentUris.parseId(contactUri);
                values.clear();
                values.put(ContactsContract.Data.RAW_CONTACT_ID, contactId);
                String contactName, contactPhone, contactEmail, contactWebsite, contactNickname;

                //TODO I don't think using contactInfo is correct. Need to receive the json.
                contactName = barcode.contactInfo.name.formattedName;
                contactPhone = barcode.contactInfo.phones[0].number;
                contactEmail = barcode.contactInfo.emails[0].address;
                contactWebsite = barcode.contactInfo.urls[0];
                //TODO nickname is not in contactInfo.
                contactNickname = barcode.contactInfo.name.pronunciation;

                if(!contactName.equals("")) {
                    values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
                    values.put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, userName.getText().toString());
                }
                if(!contactPhone.equals("")) {
                    values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                    values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, userPhone);
                }
                if(!contactEmail.equals("")) {
                    values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE);
                    values.put(ContactsContract.CommonDataKinds.Email.ADDRESS, userEmail);
                }
                if(!contactWebsite.equals("")) {
                    values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE);
                    values.put(ContactsContract.CommonDataKinds.Website.URL, userWebsite);
                }
                if(!contactNickname.equals("")) {
                    values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE);
                    values.put(ContactsContract.CommonDataKinds.Nickname.NAME, userNickname);
                }

                getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);

                Intent writeProfileIntent = new Intent(Intent.ACTION_EDIT);
                writeProfileIntent.setDataAndType(contactUri, ContactsContract.Contacts.CONTENT_ITEM_TYPE);
                startActivityForResult(writeProfileIntent, CONTACTS_REQUEST);
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
        //if(myContactTextView.getText().toString().equals(getString(R.string.startText))) return;

        // querying contact data store
        Cursor cursor = getContentResolver().query(ContactsContract.Profile.CONTENT_URI, null, null, new String[]{}, null);

        if (cursor.moveToFirst()) {

            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Profile.DISPLAY_NAME));
//            cursor = getContentResolver().query(ContactsContract.Profile.CONTENT_RAW_CONTACTS_URI, null, null, null, null);
//            //String id = cursor.getString(cursor.getColumnIndex((ContactsContract.RawContacts.CONTACT_ID)));
//            Uri rawContactUri = ContentUris.withAppendedId(ContactsContract.Profile.CONTENT_RAW_CONTACTS_URI, 0);
//            Uri entityUri = Uri.withAppendedPath(rawContactUri, ContactsContract.RawContacts.Entity.CONTENT_DIRECTORY);
//            Cursor phoneCursor = getContentResolver().query(entityUri,
//                    new String[]{ContactsContract.RawContacts.SOURCE_ID, ContactsContract.RawContacts.Entity.DATA_ID, ContactsContract.RawContacts.Entity.MIMETYPE, ContactsContract.RawContacts.Entity.DATA1},
//                    null,
//                    null, null);
//            while(phoneCursor.moveToNext()){
//                userPhone = phoneCursor.getString(3);
//                int type = phoneCursor.getInt(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
//                switch (type) {
//                    case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
//                        // do something with the Home number here...
//                        break;
//                    case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
//                        // do something with the Mobile number here...
//                        break;
//                    case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
//                        // do something with the Work number here...
//                        break;
//                }
//            }
//            phoneCursor.close();
//            userEmail = cursor.getString(cursor.getColumnIndex(ContactsContract.RawContacts.Data.DATA1));
            profileExists = true;
        } else {
            //prompt user to create contact
            //tell user a profile must be created
            DialogInterface.OnClickListener createProfileListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            createProfile();
                            profileExists = true;
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            profileExists = false;
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("No user profile found. Would you like to create a new one?").setPositiveButton("Yes", createProfileListener)
                    .setNegativeButton("No", createProfileListener).show();
        }

        cursor.close();

        if (contactName != null) {
            userName = findViewById(R.id.userName);
            userName.setText(contactName);
        }

    }

    private void createProfile(){
        ContentValues values = new ContentValues();
        Uri contactUri = getContentResolver().insert(ContactsContract.Profile.CONTENT_RAW_CONTACTS_URI, values);
        long contactId = ContentUris.parseId(contactUri);
        values.clear();
        values.put(ContactsContract.Data.RAW_CONTACT_ID, contactId);
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, "YourName");

        getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);

        Intent writeProfileIntent = new Intent(Intent.ACTION_EDIT);
        writeProfileIntent.setDataAndType(contactUri, ContactsContract.Contacts.CONTENT_ITEM_TYPE);

        startActivityForResult(writeProfileIntent, CONTACTS_REQUEST);
    }

    public void onEditProfileClick(View v) {
        if (!contactPermission){
            requestContactsPermission();
            return;
        }
        if(profileExists)
            createProfile();
        else {
            retrieveUserName();
            Toast.makeText(this, "Phone is: " + userPhone, Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "Email is: " + userEmail, Toast.LENGTH_SHORT).show();
        }
    }
}
