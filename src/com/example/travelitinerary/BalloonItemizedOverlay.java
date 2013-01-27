package com.example.travelitinerary;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

/**
 * An abstract extension of ItemizedOverlay for displaying an information balloon
 * upon screen-tap of each marker overlay.
 * 
 * @author Jeff Gilfelt
 */
public abstract class BalloonItemizedOverlay<Item extends OverlayItem> extends ItemizedOverlay<Item> {

		private MapView mv;
        private BalloonOverlayView<Item> balloonView;
        private View clickRegion;
        private int viewOffset;
//      final MapController mc;
        private Item currentFocussedItem;
        private int currentFocussedIndex;
        private Uri mUri;
        
        private Context c;
        
        /**
         * Create a new BalloonItemizedOverlay
         * 
         * @param defaultMarker - A bounded Drawable to be drawn on the map for each item in the overlay.
         * @param mapView - The view upon which the overlay items are to be drawn.
         */
        
        public BalloonItemizedOverlay(Drawable marker) {
            super(boundCenter(marker));
        }
        
        public BalloonItemizedOverlay(Drawable m, MapView mapView) {
                this(m);
                c = mapView.getContext();
                mv = mapView;
                viewOffset = 10;
        }
        
        /**
         * Set the horizontal distance between the marker and the bottom of the information
         * balloon. The default is 0 which works well for center bounded markers. If your
         * marker is center-bottom bounded, call this before adding overlay items to ensure
         * the balloon hovers exactly above the marker. 
         * 
         * @param pixels - The padding between the center point and the bottom of the
         * information balloon.
         */
        public int getBalloonBottomOffset() {
                return viewOffset;
        }
        
        /**
         * Override this method to handle a "tap" on a balloon. By default, does nothing 
         * and returns false.
         * 
         * @param index - The index of the item whose balloon is tapped.
         * @param item - The item whose balloon is tapped.
         * @return true if you handled the tap, otherwise false.
         */
        protected boolean onBalloonTap(int index, Item item) {
                return false;
        }

        /* (non-Javadoc)
         * @see com.google.android.maps.ItemizedOverlay#onTap(int)
         */
        @Override
        protected boolean onTap(int index) {
        	Log.i("tap", "check");
        	
        	currentFocussedIndex = index;
        	// 맵에 표시된 마커 아이템 가져오기
            currentFocussedItem = createItem(index);
///////////////////// revise            
            //   this is the uriList whose geopoint is exactly same as the tapped Item
            ArrayList<String> uriList = checkGeoPoint(currentFocussedItem);
/////////////////////        	
            balloonView = createBalloonOverlayView(uriList);
/////////////////////        	
            clickRegion = (View) balloonView.findViewById(R.id.balloon_inner_layout);
            clickRegion.setOnTouchListener(createBalloonTouchListener());         
            
            balloonView.setData(currentFocussedItem);
            
            GeoPoint point = currentFocussedItem.getPoint();
            MapView.LayoutParams params = new MapView.LayoutParams(
                            LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, point,
                            MapView.LayoutParams.BOTTOM_CENTER);
            params.mode = MapView.LayoutParams.MODE_MAP;
            
            balloonView.setVisibility(View.VISIBLE);
            
            //balloon을 띄운다
            mv.addView(balloonView, params);           
            
            Toast.makeText(c, "22", Toast.LENGTH_LONG).show();
            return false;  
        }
//////////////////////////////////////////////////////////// revise
        /**
         * Check GeoPoint and return PhotoClassArray's index?         * 
         */
        public ArrayList<String> checkGeoPoint(Item item){
        	int index=-1;
        	int size = PhotoList.photoClassList.size();
        	for(int i=0; i<size; i++){ //photoClassList 에 저장된 photo들의 mGeoPoint를 현재 터치된 지점의 geoPoint와 일치하면 그때 인덱스 저장
        		//////////////////우와 ...........저거 equal 또 까먹었었어.......근데 디버깅 보면서 리스트에 척척 다 들어가 있다는걸 깨달음
        		if(PhotoList.photoClassList.get(i).mGeoPoint.equals(item.getPoint())){
        			index = i;
            		break;
            	}
        	}
        	if(index == -1){
        		Log.d("cechkGeoPoint", "error");
        		return null;
        	}
        	return PhotoList.photoClassList.get(index).getUriList();
        }

//////////////////////////////////////////////////////////// revise
        
        /**
         * Creates the balloon view. Override to create a sub-classed view that
         * can populate additional sub-views.
         */
        
        protected BalloonOverlayView<Item> createBalloonOverlayView(ArrayList<String> uriList) { 
        	/////////////////////////
        	return new BalloonOverlayView<Item>(c, getBalloonBottomOffset(), uriList);
        	////////////////////////
    }
        
        private OnTouchListener createBalloonTouchListener() {
            return new OnTouchListener() {
                    public boolean onTouch(View v, MotionEvent event) {
                            
                            View l =  ((View) v.getParent()).findViewById(R.id.balloon_main_layout);
                            Drawable d = l.getBackground();
                            
                            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                    int[] states = {android.R.attr.state_pressed};
                                    if (d.setState(states)) {
                                            d.invalidateSelf();
                                    }
                                    return true;
                            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                                    int newStates[] = {};
                                    if (d.setState(newStates)) {
                                            d.invalidateSelf();
                                    }
                                    // call overridden method
                                    onBalloonTap(currentFocussedIndex, currentFocussedItem);
                                    return true;
                            } else {
                                    return false;
                            }
                            
                    }
            };
    }
        public void setUri(Uri uri){
        	mUri = uri;
        }        
}
