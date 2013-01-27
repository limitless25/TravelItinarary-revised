package com.example.travelitinerary;

import java.util.ArrayList;

import android.app.Activity;
import android.net.Uri;

import com.google.android.maps.GeoPoint;

public class PhotoList extends Activity {
	static ArrayList<PhotoList> photoClassList = null;
	public GeoPoint mGeoPoint = null;
	public ArrayList<String> photoUriList = null;
	public int index = -1;
	
	public PhotoList(GeoPoint geoPoint, int _index){
		mGeoPoint = geoPoint;
		index = _index;
	}
	
	public void addUri(String uri){
		photoUriList.add(uri);
	}
	
	public ArrayList<String> getUriList(){
		return photoUriList;
	}	
	
	public void addPhotoClass(PhotoList phClassList){
		photoClassList.add(phClassList);
	}

	public ArrayList<PhotoList> getPhotoClassList(){
		return photoClassList;
	}
	
	
}

