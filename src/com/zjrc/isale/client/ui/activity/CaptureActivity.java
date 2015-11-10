package com.zjrc.isale.client.ui.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.zjrc.isale.client.R;
import com.zjrc.isale.client.ui.view.ShutterButton;

public class CaptureActivity extends Activity implements SurfaceHolder.Callback {
	/** 分辨率 */
	public static final int WIDTH = 640;
	
	public static final int HEIGHT = 480;	
	
	//摄像头
	private Camera mCamera;
	
	private SurfaceHolder mSurfaceHolder;
	
	private SurfaceView mSurfaceView;
	
	//拍照按钮
	private View mShutterButton;
	
	//是否暂停
	private boolean mIsPaused;
	
	//是否在预览中
	private boolean mPreviewRunning;
	
	//重力感应所需参数
	private Sensor mSensor;
	private SensorManager mManager;
	
	private boolean bneedrotate;
    
	
	//对重力感应事件进行处理

    private SensorEventListener mSensorListener = new SensorEventListener(){
		  public  void onSensorChanged(SensorEvent event){
			  float f = event.values[0];
				float f1 = Math.abs(event.values[1]);
				float f2 = Math.abs(f);
				if (f1 > f2) {
					bneedrotate = true;
				} else {
					bneedrotate = false;
				}
		  }
		  
		  public void onAccuracyChanged(Sensor sensor, int accuracy){
	      }
	  };
	
	private Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
		
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			if (data != null){
				Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);		
				try {
					if (bneedrotate){
						Matrix matrix = new Matrix();
						matrix.reset();
						matrix.postRotate(90);
						Bitmap bitmaprotate = Bitmap.createBitmap(bitmap, 0, 0,bitmap.getWidth(), bitmap.getHeight(), matrix,true);
						bitmap = bitmaprotate;//Bitmap.createBitmap(bitmaprotate,0,0,WIDTH,HEIGHT);						
					}

					String filepath = Environment.getExternalStorageDirectory().getPath()+File.separator+ "isale"+File.separator+"image";
					
					File imagedir = new File(filepath);
					if (!imagedir.exists() && !imagedir.mkdirs()) {
						return;
					}
					String filename = filepath + File.separator + System.currentTimeMillis() + ".jpg";
					FileOutputStream fout = new FileOutputStream(filename);
					bitmap.compress(Bitmap.CompressFormat.JPEG,95, fout);
					fout.flush();
					fout.close();
					mPreviewRunning = true;	
					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					bundle.putString("filepath", filename);
					intent.putExtras(bundle);
					setResult(Activity.RESULT_OK,intent);
				    finish();
				} catch (FileNotFoundException e) {
					
				} catch (IOException e) {
					
				}				
			}
		}
	};

	
	
	private OnTouchListener mShutterButtonTouch = new OnTouchListener(){

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				
				if (mCamera!=null){
					mCamera.autoFocus(null);
				}
			}else if (event.getAction() == MotionEvent.ACTION_UP) {
				if (mCamera!=null){
					mCamera.takePicture(null, null, mPictureCallback);
				}
				mShutterButton.setEnabled(false);
			}
			return true;
		}
		
	};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);        
        DisplayMetrics displaymetrics =  new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        FrameLayout fl = new FrameLayout(this);
        
        mSurfaceView = new SurfaceView(this);
        mSurfaceView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.FILL_PARENT));
        
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); 
        
        fl.addView(mSurfaceView);
        
        RelativeLayout rl = new RelativeLayout(this);
        rl.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.FILL_PARENT)); 
        mShutterButton = new ShutterButton(this);
        mShutterButton.setFocusable(true);
        mShutterButton.setClickable(true);
        mShutterButton.setBackgroundResource(R.drawable.btn_shutter);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.rightMargin = 8;
        lp.topMargin = 8;
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,RelativeLayout.TRUE);     
        mShutterButton.setLayoutParams(lp);
        mShutterButton.setOnTouchListener(mShutterButtonTouch);
        rl.addView(mShutterButton); 
        fl.addView(rl);
        
        setContentView(fl);
        
        mManager = (SensorManager) getSystemService("sensor");
		mSensor = mManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        
        mIsPaused = false;
        mPreviewRunning = false;     
    } 
    
    @Override
    protected void onPause() {    
    	mIsPaused = true;
		if (mPreviewRunning) {
			mPreviewRunning = false;
		}
		if (mCamera != null) {
			mCamera.setPreviewCallback(null);
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
		mManager.unregisterListener(mSensorListener);
		mShutterButton.setVisibility(View.INVISIBLE);
		super.onPause();
    }
    
    @Override
    protected void onResume() {
    	mManager.registerListener(mSensorListener, mSensor, SensorManager.SENSOR_DELAY_NORMAL); 
    	mIsPaused = false;
    	mShutterButton.setVisibility(View.VISIBLE);	
    	super.onResume();
    }
    
	@Override
	protected void onDestroy() {  
		super.onDestroy();
	} 

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		if (mIsPaused){
			return;
		}  			
		if (mCamera == null) {
			mCamera = Camera.open();
		}
		if (mPreviewRunning) {
			mCamera.stopPreview();
		}
		Camera.Parameters params = mCamera.getParameters();

	    // Supported picture formats (all devices should support JPEG).
	    List<Integer> formats = params.getSupportedPictureFormats();

	    if (formats.contains(PixelFormat.JPEG)){
	        params.setPictureFormat(PixelFormat.JPEG);
	        params.setJpegQuality(100);
	    }else
	        params.setPictureFormat(PixelFormat.RGB_565);

	    // Now the supported picture sizes.
	    //List<Size> sizes = params.getSupportedPictureSizes();
	    //Camera.Size size = sizes.get(0);
		List<Size> sizes = params.getSupportedPictureSizes();
		for(int i=0;i<sizes.size();i++){
			for(int j=0;j<sizes.size()-1;j++){
				Size size1 = sizes.get(j);
				Size size2 = sizes.get(j+1);
				if (size1.height*size1.width<size2.height*size2.width){
					sizes.set(j, size2);
					sizes.set(j+1, size1);
				}
			}
		}
		Size size = sizes.get(sizes.size()-1);
		for(int i=0;i<sizes.size();i++){
			Size size1 = sizes.get(i);
			if (size1.height*size1.width>=HEIGHT*WIDTH){
				size = size1;
			}
		}	  
	    if (size!=null){
	    	params.setPictureSize(size.width, size.height);
	    }else{
	    	params.setPictureSize(WIDTH, HEIGHT);
	    }

	    // Set the brightness to auto.
	    params.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);

	    // Set the flash mode to auto.
	    params.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);

	    // Set the scene mode to auto.
	    params.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);

	    // Lastly set the focus to auto.
	    params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);

	    mCamera.setParameters(params);
		
		try {
			mCamera.setPreviewDisplay(mSurfaceHolder);
		} catch (IOException e) {
			e.printStackTrace();
		}
		mCamera.startPreview();	
		mPreviewRunning = true;	
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (mPreviewRunning) {
			mCamera.stopPreview();
			mPreviewRunning = false;
			mCamera.release();
			mCamera = null;
		}
	}
}
