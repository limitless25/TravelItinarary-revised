package com.example.travelitinerary;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CustomAlbum extends Activity {
	 TextView txt;
	 ArrayList<String> uriList;
	 
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_customalbum);   
	   
	    uriList = getIntent().getStringArrayListExtra("uriList");
	    
	    GridView gridview = (GridView) findViewById(R.id.gridview);
	    gridview.setAdapter(new ImageAdapter(getApplicationContext()));
	    
	    gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position,
					long id) {
				// TODO Auto-generated method stub
				Toast.makeText(CustomAlbum.this, "" + position, Toast.LENGTH_SHORT).show();
			}
		});
	
	    // TODO Auto-generated method stub
	}
	
	public class ImageAdapter extends BaseAdapter {
		private Context mContext;
		
		public ImageAdapter(Context c) {
			mContext = c;
		}

		public int getCount() {
			// TODO Auto-generated method stub
			return uriList.size();
		}

		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ImageView imageView;
			if(convertView == null){
				imageView = new ImageView(mContext);
				imageView.setLayoutParams(new GridView.LayoutParams(85,85));
				imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
				imageView.setPadding(8,  8, 8, 8);
			} else {
				imageView = (ImageView) convertView;
			}
						
		/*	//////////////////////////////////////////// 
			1개 사진은 제대로 나온다. 2개부터 out of memory...*/	
			
			// 비트맵의 사이즈를 줄이는 decode를 해서 imageView에 set 시켰다. 될까 안될까 이따 해보자!
			imageView.setImageBitmap( decodeBitmapFromStringpath(uriList.get(position), 100, 100) );
			// uriList에서 가져온 것은 string형태의 uri주소인데 decodeFileㅎ했을 때 에러 안남.오키 진행해보자			
		/*//////////////////////////////////////////// */	
			
			
//			imageView.setImageURI(Uri.parse(uriList.get(position)));
			return imageView;			
		}
		
		public Bitmap decodeBitmapFromStringpath(String str, int reqWidth, int reqHeight){
			
			//First convert string str to Filepath
			String filePath = getFilePathfromUri(Uri.parse(str));			
			
			//Second decode with inJustDecodeBounds=true to check dimensions
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			// to get options.outwidth... these are just dimensions of bitmap
			BitmapFactory.decodeFile(filePath, options);
			
			//Calculate inSampleSize
			options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
			options.inJustDecodeBounds = false; // means return BITMAP!!!			
			return BitmapFactory.decodeFile(filePath, options);
		}	
		
        private String getFilePathfromUri(Uri uri){
        	String[] projection = { MediaStore.Images.Media.DATA };
        	Cursor cursor = managedQuery(uri, projection, null, null, null);
        	int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        	cursor.moveToFirst();
        	return cursor.getString(column_index_data);
        }
        
		private int calculateInSampleSize(Options options, int reqWidth,
				int reqHeight) {
			// TODO Auto-generated method stub
			// to set dimensions with unprocessed bitmaps dimensions
			final int height = options.outHeight;
			final int width = options.outWidth;
			int inSampleSize = 1;
			
			if (height > reqHeight || width > reqWidth){
				if(width > height){ // 작은 크기에 맞춰서 줄이는데 왜? // 큰키게에 맞춰서 줄여야지 여백이 생기더라도 이미지가 잘리지 않을 텐데
					//600,800경우와 300, 80 경우를 생각을 해보면 req100,100일때... 맞는거 같은데 궁금
					//그래서 내 생각대로 바꿈
					inSampleSize = Math.round((float)width / (float)reqWidth);					
				} else{
					inSampleSize = Math.round((float)height / (float)reqHeight);
				}
			}			
			return inSampleSize;
		}
		
		private Integer[] mThumbIds = {
	            R.drawable.sample_2, R.drawable.sample_3,
	            R.drawable.sample_4, R.drawable.sample_5,
	            R.drawable.sample_6, R.drawable.sample_7,
	            R.drawable.sample_0, R.drawable.sample_1,
	            R.drawable.sample_2, R.drawable.sample_3,
	            R.drawable.sample_4, R.drawable.sample_5,
	            R.drawable.sample_6, R.drawable.sample_7,
	            R.drawable.sample_0, R.drawable.sample_1,
	            R.drawable.sample_2, R.drawable.sample_3,
	            R.drawable.sample_4, R.drawable.sample_5,
	            R.drawable.sample_6, R.drawable.sample_7
	    };		
	}	
}
