package com.example.pictext;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class PicTextActivity extends Activity {
    Button btCamera,btGallery,btRetake,btCopy;
    TextView tvImageText;
    Bitmap imageBitmap;
    ScrollView scrollView;
    private static final int REQUEST_CODE_CAMERA = 100;
    private static final int IMAGE_CROP_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_text);

        btCamera = findViewById(R.id.btCamera);
        btGallery = findViewById(R.id.btGallery);
        btRetake = findViewById(R.id.btRetake);
        tvImageText = findViewById(R.id.tvImageText);
        btCopy = findViewById(R.id.btCopy);
        scrollView = findViewById(R.id.scrollView);

        Paint paint = new Paint();
        paint.setMaskFilter(new BlurMaskFilter(50, BlurMaskFilter.Blur.SOLID));
        scrollView.setLayerType(View.LAYER_TYPE_SOFTWARE, paint);

        tvImageText.setBackgroundColor(Color.TRANSPARENT);

        imageBitmap = null;

        if(ContextCompat.checkSelfPermission(PicTextActivity.this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(PicTextActivity.this, new String[]{
                    Manifest.permission.CAMERA
            },REQUEST_CODE_CAMERA);
        }

        btGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery,111);
            }
        });

        btCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null)
                {
                    startActivityForResult(takePictureIntent, 101);
                }
            }
        });

        btRetake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageBitmap!=null)
                {
                    tvImageText.setText(" ");
                    tvImageText.postDelayed(() -> {
                        getTextFromImage(imageBitmap);
                        Toast.makeText(getApplicationContext(), "Retaken text", Toast.LENGTH_LONG).show();
                    }, 800);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"No image for retake..!",Toast.LENGTH_LONG).show();
                }
            }
        });

        btCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyToClipboard(tvImageText.getText().toString());
            }
        });

    }

    public void onActivityResult(int reqCode, int resCode, Intent data)
    {
        if(reqCode==101 && resCode==RESULT_OK)
        {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
        }
        else if(reqCode==111 && resCode==RESULT_OK)
        {
            Uri imageUri = data.getData();
//            Uri uri = data.getData();
//            Uri imageUri = Uri.fromFile(new File(getCacheDir(), "cropped_image.jpg"));
//            UCrop.of(uri, imageUri)
//                    .withAspectRatio(16, 9)
//                    .withMaxResultSize(800, 800)
//                    .start(PicTextActivity.this);

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
            getTextFromImage(imageBitmap);
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Error getting the image",Toast.LENGTH_SHORT).show();
        }
    }

    private void getTextFromImage(Bitmap bitmap)
    {
        TextRecognizer recognizer = new TextRecognizer.Builder(this).build();
        if(!recognizer.isOperational())
        {
            Toast.makeText(getApplicationContext(),"Error...!",Toast.LENGTH_SHORT).show();
        }
        else
        {
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> textBlockSparseArray = recognizer.detect(frame);
            StringBuilder stringBuilder = new StringBuilder();
            for(int i=0; i<textBlockSparseArray.size(); i++)
            {
                TextBlock textBlock = textBlockSparseArray.get(i);
                stringBuilder.append(textBlock.getValue());
                stringBuilder.append("\n");
            }
            tvImageText.setText(stringBuilder.toString());
        }
    }

    private void copyToClipboard(String text)
    {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("Copied Data",text);
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(getApplicationContext(),"Text Copied to Clipboard",Toast.LENGTH_SHORT).show();
    }

}