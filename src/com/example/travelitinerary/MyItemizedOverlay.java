package com.example.travelitinerary;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class MyItemizedOverlay extends BalloonItemizedOverlay<OverlayItem> {
	private List<OverlayItem> items = new ArrayList<OverlayItem>();
	private Context c;

	public MyItemizedOverlay(Drawable m, MapView mapView) {
		// TODO Auto-generated constructor stub
		super(m, mapView);
		c = mapView.getContext();
		boundCenterBottom(m);
	}

	@Override
	protected OverlayItem createItem(int i) {
		// TODO Auto-generated method stub
		return (items.get(i));
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return items.size();
	}	
	
	public void addItem(OverlayItem item){
		items.add(item);
		this.populate();
//		super.setUri(uri);
	}
	
	public GeoPoint getItemsPoint(int i){
		return items.get(i).getPoint();
	}
/*
	protected boolean onTap(int index){
	//	items.get(index);
	//	Toast.makeText(c, "11", Toast.LENGTH_SHORT).show();
		
		return true;
	}
*/
}