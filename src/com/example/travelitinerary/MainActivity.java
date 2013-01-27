package com.example.travelitinerary;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {
	private Button mCamera;
	private Button mGallery;
	private Uri ImageUri = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        AllTheEvil.initialize(getApplicationContext());
        
        
        mCamera = (Button)findViewById(R.id.camera_btn);
        mCamera.setOnClickListener(new OnClickListener() {
        	
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent("android.intent.action.CAMERA_ACTION");
				
		/*		//얻은 ImageUri를 다른 액티비티로 보내는 과정
				if(ImageUri != null){
					intent.putExtra("chosenPhoto", ImageUri);
					setResult(Activity.RESULT_OK, intent);
				} */
				
				startActivity(intent);
			}        	
        });    
        
        mGallery = (Button)findViewById(R.id.gallery_btn);
        mGallery.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
				photoPickerIntent.setType("image/*");
				startActivityForResult(photoPickerIntent, 1);
				}        	
        });        
    }
    
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK)
		{
			Uri chosenImageUri = data.getData();
			ImageUri = chosenImageUri;
			
			Bitmap mBitmap = null;
			try {
				//retrive bitmap from gallery
				mBitmap = Media.getBitmap(this.getContentResolver(), chosenImageUri);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
