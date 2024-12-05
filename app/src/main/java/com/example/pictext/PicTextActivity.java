package com.example.pictext;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class PicTextActivity extends Activity {
    Button btCapture,btRetake;
    TextView tvImageText;
    private static final int REQUEST_CODE_CAMERA = 100;
    private static final int IMAGE_CROP_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_text);

        btCapture = findViewById(R.id.btCapture);
        btRetake = findViewById(R.id.btRetake);
        tvImageText = findViewById(R.id.tvImageText);

        if(ContextCompat.checkSelfPermission(PicTextActivity.this,android.Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(PicTextActivity.this, new String[]{
                    android.Manifest.permission.CAMERA
            },REQUEST_CODE_CAMERA);
        }

        btCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery,111);

//                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                if (takePictureIntent.resolveActivity(getPackageManager()) != null)
//                {
//                    startActivityForResult(takePictureIntent, 101);
//                }
            }
        });

    }

    public void onActivityResult(int reqCode, int resCode, Intent data)
    {
        Bitmap imageBitmap = null;
        if(reqCode==101 && resCode==RESULT_OK)
        {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
        }
        else if(reqCode==111 && resCode==RESULT_OK)
        {
            Uri imageUri = data.getData();

            try
            {
                ContentResolver contentResolver = getContentResolver();
                InputStream inputStream = contentResolver.openInputStream(imageUri);
                imageBitmap = BitmapFactory.decodeStream(inputStream);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }
        if(resCode==RESULT_OK)
        {
            Toast.makeText(getApplicationContext(),"Got the image",Toast.LENGTH_LONG).show();
        }
    }

}