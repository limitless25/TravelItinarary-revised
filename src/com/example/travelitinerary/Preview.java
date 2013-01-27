package com.example.travelitinerary;


import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import com.example.travelitinerary.PhotoList;
import com.example.travelitinerary.MyDB;
import com.example.travelitinerary.AllTheEvil;

import com.google.android.maps.GeoPoint;



import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;


import android.net.Uri;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


class Preview extends ViewGroup implements SurfaceHolder.Callback {
    private final String TAG = "Preview";

    SurfaceView mSurfaceView;
    SurfaceHolder mHolder;
    Size mPreviewSize;
    Uri mUri;
    List<Size> mSupportedPreviewSizes;
    
    Camera mCamera;
    int camCount;
    int currCamIdx = 0;
    Context mContext;  
   
    private void init(Context context){
    	mSurfaceView = new SurfaceView(context);
        addView(mSurfaceView);
        
        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        
        camCount = Camera.getNumberOfCameras();
        CameraInfo info = new CameraInfo();
        for(int i=0; i<camCount; i++){
        	Camera.getCameraInfo(i, info);
        	if(info.facing == CameraInfo.CAMERA_FACING_BACK)
        		currCamIdx = i;
        }        
        mContext = context;
 //       myDB = new MyDB(mContext); //// 오잉 여기에 만드니까 되네.....하긴 저기 위에 멤버 벼수만드는데다 하면 안돼지... 근데 문제는 init 생성시에 계속 db만드는 거 아님??
        // 내말은 DB는 항상 한개이어야만 하는데.........확인해 보니까 역시 카메라 탭 띄울때는 실행이 되네...... 메인에서 한번 띄우는게 날 것같은데
    }
    
    Preview(Context context) {
        super(context);
        init(context);
    }
    
    public Preview(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public Preview(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	public void openCamera(){
		mCamera = Camera.open(currCamIdx);
		preparePreviewSize();
	}

	public void preparePreviewSize(){
		mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
        mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, 
        		getResources().getDisplayMetrics().widthPixels, 
        		getResources().getDisplayMetrics().heightPixels);
	}
	
	public void setCamera(Camera camera) {
        mCamera = camera;
        preparePreviewSize();
    }

    public void switchCamera() {
    	releaseCamera();
    	currCamIdx = (++currCamIdx)%camCount;
    	setCamera(Camera.open(currCamIdx));
    	try {
    		mCamera.setPreviewDisplay(mHolder);
    	} catch (IOException exception) {
    		Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
    	}
    	Camera.Parameters parameters = mCamera.getParameters();
    	parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
    	mCamera.setParameters(parameters);
    	mCamera.startPreview();
    	requestLayout();
    }
    
    public void releaseCamera(){
    	if(mCamera != null){
    		mCamera.stopPreview();
    		mCamera.release();
    		mCamera = null;
    	}
    }
    public void takePicture(final String title, final String description, final GeoPoint crtPoint){
    	
    	mCamera.takePicture(null, null, new PictureCallback(){
    		
    		// 왜 onPictureTaken은 바로 실행 되어지지가 않지? 실행 순서는 어떻게 되어있지? 
			public void onPictureTaken(byte[] data, Camera camera) {
				//picture - 찍은 사진
				Bitmap picture = BitmapFactory.decodeByteArray(data, 0, data.length);
				
				//uri 를 가져온 두가지 방법
				String urii = MediaStore.Images.Media.insertImage(mContext.getContentResolver(), picture, title, description);
				
				Log.e("URI", urii);
				/*
				Uri uri = MediaStore.Images.Media.getContentUri("external");
				mUri = uri;
				*/
				
				Toast.makeText(mContext, "이미지가 저장되었습니다.", Toast.LENGTH_SHORT).show();
				mCamera.startPreview();	
				
				int _index = -1;
				
				if(PhotoList.photoClassList.size() == 0){
					PhotoList.photoClassList = new ArrayList<PhotoList>();
					PhotoList.photoClassList.add(0, new PhotoList(crtPoint, 0));
					PhotoList.photoClassList.get(0).photoUriList = new ArrayList<String>();
					PhotoList.photoClassList.get(0).photoUriList.add(urii);
					PhotoList.photoClassList.get(0).index = 0; // 첫번째
					_index = 0;
				}
				else{
					int size = PhotoList.photoClassList.size();
					boolean existed = false;
					for(int i=0; i < size; i++){ // if there is at least one, compare with crtPoint
						if(PhotoList.photoClassList.get(i).mGeoPoint.equals(crtPoint)){
							PhotoList.photoClassList.get(i).photoUriList.add(urii);
							existed = true;
							_index = i;
							break; // 찾았으면 Uri 저장후에 종료시켜 버렸음 							
						}					
					}
					// 새로운 지역이다 싶으면 클래스를 하나 추가 existed = false이면 똑같은 지역이 없다는 의미 그래서 새로운 지역 추가
					if(!existed){
						PhotoList.photoClassList.add(new PhotoList(crtPoint, size )); // size가 index로 활용
						PhotoList.photoClassList.get(size).photoUriList = new ArrayList<String>();
						PhotoList.photoClassList.get(size).photoUriList.add(urii);
						_index = size;
					}
				}
				
////////////////DATABASE 		
				MyDB mDB;
				mDB = AllTheEvil.getInstance().getDB();
				mDB.open();
				mDB.createRec(crtPoint.getLatitudeE6(), crtPoint.getLongitudeE6(), urii, _index);
				mDB.close();
				///////////////////////
			}			
    	});
    }
 
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (getChildCount() > 0) {
            final View child = getChildAt(0);

            final int width = r - l;
            final int height = b - t;

            int previewWidth = width;
            int previewHeight = height;
            if (mPreviewSize != null) {
                previewWidth = mPreviewSize.width;
                previewHeight = mPreviewSize.height;
            }

            if (width * previewHeight > height * previewWidth) {
                final int scaledChildWidth = previewWidth * height / previewHeight;
                child.layout((width - scaledChildWidth) / 2, 0,
                        (width + scaledChildWidth) / 2, height);
            } else {
                final int scaledChildHeight = previewHeight * width / previewWidth;
                child.layout(0, (height - scaledChildHeight) / 2,
                        width, (height + scaledChildHeight) / 2);
            }
        }
    }
    
    private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    public void surfaceCreated(SurfaceHolder holder) {
        try {
            if (mCamera != null) {
                mCamera.setPreviewDisplay(holder);
            }
        } catch (IOException exception) {
            Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
            mCamera.release();
            mCamera = null;
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mCamera != null) {
            mCamera.stopPreview();
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
    	if(mCamera != null){
	        Camera.Parameters parameters = mCamera.getParameters();
	        parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
	        mCamera.setParameters(parameters);
	        mCamera.startPreview();
    	}
    }

}