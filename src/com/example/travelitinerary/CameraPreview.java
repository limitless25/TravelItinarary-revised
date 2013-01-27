package com.example.travelitinerary;

import com.example.travelitinerary.MyItemizedOverlay;
import com.example.travelitinerary.R;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.Toast;

public class CameraPreview extends MapActivity implements LocationListener {    
    private Preview mPreview;
    private Button mCaptureButton;
    private TabHost mTabHost;
    public static Context mContext;
    
    private MapView mv;        
    LocationManager locManager;   
    Location location;
    Drawable marker;
    
    LocationListener locListener;
    
   
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camerapreview);
        
        mTabHost = (TabHost) findViewById(R.id.tabhost);
        mTabHost.setup();
        
        mTabHost.addTab(mTabHost.newTabSpec("tab1")
        		.setContent(R.id.tab1)
        		.setIndicator("Camera"));
        
        mTabHost.addTab(mTabHost.newTabSpec("tab2")
        		.setContent(R.id.tab2)
        		.setIndicator("Map"));        
       
        mContext = CameraPreview.this;
        //Camera
        mPreview = (Preview)findViewById(R.id.preview_layer);      
        
        //MapView // ���⼭ mv�� �� �Ǵµ� �� preview������ �ȵ�??
        mv = (MapView)findViewById(R.id.map);
        mv.setBuiltInZoomControls(true);
        
        //Location
        locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Log.i("location", "here");       
        locManager.requestLocationUpdates(locManager.NETWORK_PROVIDER, 3000, 1, this);
      //���� ���� ¥������ �ȉ���µ� NETWORK_PROVIDER�� �ٲٴϱ� �ȴ�........���� �̰�
		 final Location location = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);       
        
        //marker
        marker = getResources().getDrawable(R.drawable.marker);          
        //make 1 ItemizedOverlay with marker & mv
       final MyItemizedOverlay myItemizedOverlay = new MyItemizedOverlay(marker, mv);
        // ���⼭�� DB ��� myItemizedoverlay.items ����Ʈ���ٰ� �����۵� �ε��� ������� �־�� �ҵ�

        //
   	mv.getOverlays().add(myItemizedOverlay);
        
        mCaptureButton = (Button)findViewById(R.id.capture_button);
        mCaptureButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {			
				if(location != null){
		        	int lat = (int) (location.getLatitude()*1E6);
		        	int longi = (int) (location.getLongitude()*1E6);
		        	GeoPoint currentLoc = new GeoPoint(lat, longi);
		        	// Item�����ϱ� ���� �ߺ� �˻� �ǽ��ؾ� �ҵ�		        	
		        	// Geopoint �̿��ؼ� current��ġ�� ��Ŀ�� ������ �ε����� �˾Ƴ�   // 
		        	int itemIndex = -1;
		        	
		        	if(myItemizedOverlay.size() == 0){
		        		OverlayItem overlayItem = new OverlayItem(currentLoc, "", "");
		        		myItemizedOverlay.addItem(overlayItem);
		        	}
		        	else{
		        		for(int i=0; i<myItemizedOverlay.size(); i++){
		        			if( currentLoc.equals(myItemizedOverlay.getItemsPoint(i)) ){
		        				itemIndex = i;
		        				break;
		        			}
		        		}
		        	}
		        	// // �ߺ����� 	        	
//					String uri = 
							mPreview.takePicture("My Photo", "Photo taken by sample application", currentLoc);
					//////////////////////////////// * ���⿡�� photoClassList����??		
//					Log.i("URI", uri);	        	
		        }else{
		        	Toast.makeText(CameraPreview.this, "Cound't get provider", Toast.LENGTH_SHORT).show();
		        }				    		
			}			
        });
        
    }
    
    @Override
	protected void onResume() {
		super.onResume();
		mPreview.openCamera();
	}
    
    @Override
	protected void onPause() {
		super.onPause();
		mPreview.releaseCamera();
	}
    
	private static final int MENU_SWITCH_CAM = 0;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_SWITCH_CAM, Menu.NONE, "ī�޶� ��ȯ");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case MENU_SWITCH_CAM:
			mPreview.switchCamera();
			return true;
		}
		return false;
	}

	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
	}

	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
}
