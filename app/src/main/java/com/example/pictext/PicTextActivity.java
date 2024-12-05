package com.example.pictext;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PicTextActivity extends Activity {
    Button btCapture,btRetake;
    TextView tvImageText;
    private static final int REQUEST_CODE_CAMERA = 100;

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


    }
}