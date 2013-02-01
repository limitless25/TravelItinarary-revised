package com.example.travelitinerary;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.maps.OverlayItem;

import com.example.travelitinerary.MyDB;

public class BalloonOverlayView<Item extends OverlayItem> extends FrameLayout {

    private LinearLayout layout;
    private EditText title;
    private EditText snippet;

    /**
     * Create a new BalloonOverlayView.
     * 
     * @param context - The activity context.
     * @param balloonBottomOffset - The bottom padding (in pixels) to be applied
     * when rendering this view.
     */
///////////////////////////////////////////////////////////
    public BalloonOverlayView(final Context context, int balloonBottomOffset, final ArrayList<String> uriList) {
///////////////////////////////////////////////////////////
    		   	
            super(context);
/*            
            MyDB mDB;
            mDB = AllTheEvil.getInstance().getDB();
            mDB.open();
            
            Cursor cursor = mDB.fetchAllRec();
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
	            int logi = cursor.getInt(1);
	            int lati = cursor.getInt(2);
	            String path = cursor.getString(3);
	            cursor.moveToNext();
	        }
            cursor.close();
            mDB.close();
  */          
// the way to make layout on Java code            
            setPadding(10, 0, 10, balloonBottomOffset);
            layout = new LinearLayout(context);
            layout.setVisibility(VISIBLE);
// layout // put View instance into layout  
            LayoutInflater inflater = (LayoutInflater) context
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.balloon_overlay, layout);
            title = (EditText) v.findViewById(R.id.balloon_item_title);
            snippet = (EditText) v.findViewById(R.id.balloon_item_snippet);

            // uri �ҷ��ͼ� ���� ǥ���ϱ�
            ImageView photo = (ImageView) v.findViewById(R.id.photoFromGallery);
            photo.setImageURI(Uri.parse(uriList.get(0))); // ù��° ���� ���� ���� ������ string -> uri�� �Ľ�
            /////////////////// �̹��� Ŭ���ÿ� GridView�� �Ѿ��
            photo.setOnClickListener(new OnClickListener(){
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent("android.intent.action.CUSTOM_ALBUM");
					intent.putStringArrayListExtra("uriList", uriList);
					/////// ���⼭ Custom Album�� �θ����µ� extend Activity�� ���ؼ� context ������ ��
					context.startActivity(intent);	
					}            	
            });
         
            ImageView close = (ImageView) v.findViewById(R.id.close_img_button);
            close.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                            layout.setVisibility(GONE);
                    }     
            });

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                            LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.NO_GRAVITY;

            addView(layout, params);

    }
    
    /**
     * Sets the view data from a given overlay item.
     * 
     * @param item - The overlay item containing the relevant view data 
     * (title and snippet). 
     */
    public void setData(Item item) {
            layout.setVisibility(VISIBLE);
            if (item.getTitle() != null) {
                    title.setVisibility(VISIBLE);
                    title.setText(item.getTitle());
            } else {
                    title.setVisibility(GONE);
            }
            if (item.getSnippet() != null) {
                    snippet.setVisibility(VISIBLE);
                    snippet.setText(item.getSnippet());
            } else {
                    snippet.setVisibility(GONE);
            }
            
    }

}