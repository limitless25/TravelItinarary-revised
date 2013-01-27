package com.example.travelitinerary;

import java.util.ArrayList;

import com.google.android.maps.GeoPoint;

import android.content.Context;
import android.database.Cursor;

public class AllTheEvil {
	private static AllTheEvil instance = null; // instance 객체를 글로벌 객체로 사용
	private Context context;
	private MyDB myDB;
	
	private AllTheEvil() {
	}
	
	public static void initialize(Context context){
		instance = new AllTheEvil();
		instance.context = context;
		instance.myDB = new MyDB(context);
		PhotoList.photoClassList = new ArrayList<PhotoList>();
		instance.initDBdata(); // DB -> PhotoClassList로 옮기는 것
	}
	
	public static void close() {
		instance.myDB.close();
	}
	
	public static AllTheEvil getInstance(){
		if (instance == null)
			throw new RuntimeException("call initialize plz");
		return instance;
	}
	
	public Context getContext() {
		return context;
	}
	
	public MyDB getDB(){
		return myDB;
	}
	
	public void initDBdata(){
		MyDB mDB = instance.getDB();
		mDB.open();		
		Cursor cursor = mDB.fetchAllRec();
		cursor.moveToFirst();
		
		while(!cursor.isAfterLast()){
			
			int size = PhotoList.photoClassList.size();			
			int index = cursor.getInt(4);
			int longi = cursor.getInt(1);
			int lat = cursor.getInt(2);
			GeoPoint mGeo = new GeoPoint(longi, lat);
			String uri = cursor.getString(3);
			
			if(size != 0)
			{
				boolean existed = false;
				for(int i=0; i<PhotoList.photoClassList.size(); i++){
					if(PhotoList.photoClassList.get(i).index == index)
					{					
						PhotoList.photoClassList.get(i).mGeoPoint = mGeo;
						PhotoList.photoClassList.get(i).getUriList().add(uri);
						existed = true;
						break;
					}					
				}
				if(!existed){
					PhotoList.photoClassList.add(new PhotoList(mGeo, size));
					PhotoList.photoClassList.get(size).photoUriList = new ArrayList<String>();
					PhotoList.photoClassList.get(size).photoUriList.add(uri);
				}				
			}
			else{
				PhotoList.photoClassList.add(0, new PhotoList(mGeo, 0));
				PhotoList.photoClassList.get(0).photoUriList = new ArrayList<String>();
				PhotoList.photoClassList.get(0).photoUriList.add(uri);
				
				// 아마 위에 세줄은 겹치는 게 많아서 또 다른 서브 함수로 바꿀수 있을 듯 나중에 해보자
			}
			
			cursor.moveToNext();
		}
		cursor.close();
		mDB.close();
	}
}
