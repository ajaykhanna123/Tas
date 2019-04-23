package com.chicmic.task5;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.graphics.BitmapCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class AddContactActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView imageUserProfile;
    private ImageView addImage;
    private EditText edtUserName;
    private EditText edtPhnNo;
    private EditText edtEmail;
    private EditText edtAddress;
    private RadioButton radioMale;
    private RadioButton radioFemale;
    private RadioGroup radioGender;
    private EditText edtFax;
    private static int RESULT_LOAD_IMAGE = 1;

    private String mFileName;
    private Contact mLoadedNote = null;

    private String lastImgAccessed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        imageUserProfile = findViewById(R.id.user_profile_image);
        addImage=findViewById(R.id.addImageButton);
        edtUserName=findViewById(R.id.edtContactName);
        edtAddress=findViewById(R.id.edtAddress);
        edtPhnNo=findViewById(R.id.edtPhoneNo);
        edtEmail=findViewById(R.id.edtEmail);
        edtFax=findViewById(R.id.edtFaxNo);
        radioFemale=findViewById(R.id.radioFemale);
        radioMale=findViewById(R.id.radioMale);
        radioGender=findViewById(R.id.radioGender);



        addImage.setOnClickListener(this);

        mFileName = getIntent().getStringExtra(Utilities.EXTRAS_NOTE_FILENAME);
        Toast.makeText(this,mFileName+"",Toast.LENGTH_SHORT).show();
        if(mFileName != null && !mFileName.isEmpty() && mFileName.endsWith(Utilities.FILE_EXTENSION)) {
            mLoadedNote = Utilities.getContactByFileName(getApplicationContext(), mFileName);
            if (mLoadedNote != null) {
                //update the widgets from the loaded note
                edtUserName.setText(mLoadedNote.getName());
                edtAddress.setText(mLoadedNote.getAddress());
                edtPhnNo.setText(mLoadedNote.getPhnNo());
                edtFax.setText(mLoadedNote.getFaxNo());
                edtEmail.setText(mLoadedNote.getEmail());

                if(mLoadedNote.getGender().matches("Male"))
                {
                    radioMale.setChecked(true);
                }
                else if(mLoadedNote.getGender().matches("Female"))
                {
                    radioFemale.setChecked(true);
                }
            }
        }
    }
    public boolean isRadioGroupChecked()
    {
        if(radioGender.getCheckedRadioButtonId()==-1)
        {
            return false;
        }
        return true;
    }
    public void saveContact()
    {
        Contact contact;
        String userName=edtUserName.getText().toString().trim();
        String userEmailAddress=edtEmail.getText().toString().trim();
        String userAddress=edtAddress.getText().toString().trim();
        String userPhnNo=edtPhnNo.getText().toString().trim();
        String userfax=edtFax.getText().toString().trim();
        String userGender="";
        if(isRadioGroupChecked())
        {
            if(radioMale.isChecked())
            {
                userGender=radioMale.getText().toString().trim();
                Toast.makeText(this,userGender,Toast.LENGTH_SHORT).show();
            }
            if(radioFemale.isChecked())
            {
                userGender=radioFemale.getText().toString().trim();
                Toast.makeText(this,userGender,Toast.LENGTH_SHORT).show();
            }
        }

        if(TextUtils.isEmpty(userName) || TextUtils.isEmpty(userAddress) || TextUtils.isEmpty(userPhnNo)
        || TextUtils.isEmpty(userfax) || TextUtils.isEmpty(userGender) || TextUtils.isEmpty(userEmailAddress))
        {
            Toast.makeText(AddContactActivity.this,"All fields are mandatory",Toast.LENGTH_SHORT).show();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(userEmailAddress).matches())
        {
            Toast.makeText(AddContactActivity.this,"Email is invalid",Toast.LENGTH_SHORT).show();
            return;
        }
        String contactUniqueId= UUID.randomUUID().toString();

        if (mLoadedNote == null){
            contact=new Contact(System.currentTimeMillis(),userName,userPhnNo,userAddress,userGender,userEmailAddress,userfax);

        }
        else
        {
            contact=new Contact(mLoadedNote.getTime(),mLoadedNote.getName(),userPhnNo,userAddress,userGender,userEmailAddress,userfax);
            Toast.makeText(this, System.currentTimeMillis()+"", Toast.LENGTH_SHORT).show();
        }

        if(Utilities.saveContact(this,contact))
        {
            Toast.makeText(this, "Contact has been saved", Toast.LENGTH_SHORT).show();

        }
        else
        {
            Toast.makeText(this, "can not save the contact. make sure you have enough space " +
                    "on your device", Toast.LENGTH_SHORT).show();
        }

        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_contact, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.addContactMenu)
        {
            saveContact();
            return true;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.addImageButton) {
            Intent i = new Intent(
                    Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            startActivityForResult(i, RESULT_LOAD_IMAGE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();


            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);

            cursor.close();
            Picasso.get().setLoggingEnabled(true);
            Picasso.get().load(data.getData()).placeholder(R.drawable.placeholder_image).resize(imageUserProfile.getWidth()
                    ,imageUserProfile.getHeight()).centerCrop().into(imgTarget);

            lastImgAccessed = data.getData().toString();




            Toast.makeText(this,picturePath,Toast.LENGTH_SHORT).show();

            //imageUserProfile.setImageBitmap(selectedImageBitmap);

            // String picturePath contains the path of selected Image
        }

    }

    Target imgTarget = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

            imageUserProfile.setImageBitmap(bitmap);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,70,byteArrayOutputStream);
            byte[] imageAsByte = byteArrayOutputStream.toByteArray();
            String rawString = Base64.encodeToString(imageAsByte,Base64.DEFAULT);
            byte[] imgBytes = Base64.decode(rawString,Base64.DEFAULT);
            Bitmap bitmap1 = BitmapFactory.decodeByteArray(imgBytes,0,imgBytes.length);
           imageUserProfile.setImageBitmap(bitmap1);
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };


}
