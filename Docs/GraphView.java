/****************************************************************************

    Copyright 2008, 2009  Clark Scheff
    
    This file is part of The Schwartz Unsheathed.

    The Schwartz Unsheathed is free software: you can redistribute it and/or
    modify it under the terms of the GNU General Public License as published
    by the Free Software Foundation version 3 of the License.

    AndroidBreakout is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with The Schwartz Unsheathed. If not, see http://www.gnu.org/licenses
    
****************************************************************************/

package com.android.app.schwarz;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.*;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.IOException;
import java.util.Random;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

import java.lang.Math;
import java.util.Vector;

/**
 * @author lithium
 *
 */
public class GraphView extends View implements Runnable
{
	public static final String APP_NAME = "TheSchwartz";
	
    public static final int SWING_DELAY = 500;
    public int HIT_DELAY = 250;
    public static final int MAX_COLORS = 9;
    public static final int NUM_SAMPLES = 2;
    public static final float ACCEL_THRESHOLD = 0.6f;
    public static final float SWING_FORCE = 2.0f;
    public static final float HIT_FORCE = 6.0f;//SensorManager.GRAVITY_EARTH;
    
    public static final int SND_SABROUT1 = 0;
    public static final int SND_SABROFF1 = 1;
    public static final int SND_SABRHUM = 2;
    public static final int SND_SABRSWG1 = 3;
    public static final int SND_SABRSWG2 = 4;
    public static final int SND_SABRSWG3 = 5;
    public static final int SND_SABRSWG4 = 6;
    public static final int SND_SABRSWG5 = 7;
    public static final int SND_SABRSWG6 = 8;
    public static final int SND_SABRSWG7 = 9;
    public static final int SND_HIT1 = 10;
    public static final int SND_HIT2 = 11;
    public static final int SND_HIT3 = 12;
	
    public static final int NO_CHANGE = 0;
    public static final int SWING_DETECTED = 1;
    public static final int HIT_DETECTED = 2;
    public static final int NO_MOVEMENT = 3;
    public static final float PI_OVER_180 = (float)(Math.PI / 180.0);
    
    private Bitmap  mBitmap;
    private Bitmap  mSabre;
    private Bitmap  mStarField;
    private Paint   mPaint = new Paint();
    private Canvas  mCanvas = new Canvas();
    private int		mWidth;
    private float   mHeight;
    private boolean mSabreOut = false;
    private int	mColorNum = 0;
    private int mGlowLevel = 0;
    private int mGlowInc = 1;
    private int mSabreHeight = 320;
    private MediaPlayer mMP = new MediaPlayer();
	private boolean mForceActive = false;
	private boolean mHumming = false;
    private SensorManager mSensorManager = null;
    private long lastTime = 0;
    private Context mContext;
    private boolean mZoom = false;
    private boolean mClash = false;
    private Vector<Float> mMagnitudes = new Vector<Float>();
    private int mLastMove = NO_MOVEMENT;
    private float mMaxDeviation = 0.0f;
    private boolean mBgVisible = true;
    private boolean mPlayHum = true;
    private boolean mSensitive = false;
    private WakeLock mWakeLock = null;
//    private int mOrientation = PhoneOrientation.ORIENTATION_INVALID;
//    private PhoneOrientation mPO = new PhoneOrientation();
    private boolean mKeepScreenOn = false;
    private float mSenseOffset = 5.0f;
    private int mCustomColor[] = new int[] {255,255,255};
    private Thread mThread = null;
    private boolean mPaused = false;
    
    public GraphView(Context context, SensorManager sm) {
        super(context);
        mContext = context;
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        mSabre = BitmapFactory.decodeResource(getResources(), R.drawable.saber_handle);
//        mSabre = BitmapFactory.decodeResource(getResources(), R.drawable.ring_hilt);
        mStarField = BitmapFactory.decodeResource(getResources(), R.drawable.background);
        mSensorManager = (SensorManager)sm;
        final PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE); 
        this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "TheSchwartz");
//        mWakeLock.acquire(1);
        mKeepScreenOn = this.getKeepScreenOn();
        this.setKeepScreenOn(true);
        mThread = new Thread(this);
        mThread.start();
    }
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
        mCanvas.setBitmap(mBitmap);
        mCanvas.drawColor(0xFF000000);
        mWidth = w;
        mHeight = h;

        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        synchronized (this) {
            if (mBitmap != null) {
            	this.drawSabre(canvas, mZoom);
            }
        }
    }
    
    public void setSabreOut(boolean out) {
    	mSabreOut = out;
    	if(out) {
    		mGlowLevel = 0x60;
    		mGlowInc = -2;
    	}
    	else {
    		mGlowLevel = 0x02;
    	}
    	this.invalidate();
    }
    
    public void setSabreColor(int color) {
        mColorNum = color;
    	this.invalidate();
    }
    
    public int getSabreColor() {
    	return this.mColorNum;
    }
    
    public void setBgVisible(boolean visible) {
    	mBgVisible = visible;
    }
    
    public boolean getBgVisible() {
    	return mBgVisible;
    }
    
    public void setSensitivity(boolean sensitive) {
    	mSensitive = sensitive;
    }
    
    public boolean getSensitivity() {
    	return mSensitive;
    }
    
    public boolean getSabreOut() {
    	return mSabreOut;
    }

    @Override
	public boolean onTouchEvent(MotionEvent event) {
    	if(event.getAction() != MotionEvent.ACTION_DOWN)
    		return true;
    	
    	float x = event.getX();
    	float y = event.getY();
    	
    	if( (x < 140 || x > 180) && false == mZoom )
    		return true;
    	
    	if(y >= 300 && false == mZoom) {
    		if(mMP.isPlaying() == true)
    			mMP.stop();
    		try {
    			mMP.prepare();
    		} catch (IllegalStateException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		mMP.release();
    		
    		if(mForceActive == false) {
    			mMP = MediaPlayer.create(mContext, R.raw.sabrout1);
    			mMP.start();
    			this.setSabreOut(true);

    			int mask = 0;
    			mask = SensorManager.SENSOR_ACCELEROMETER;

    			mSensorManager.registerListener(mListener, mask, SensorManager.SENSOR_DELAY_FASTEST);
    			mForceActive = true;
    			mHumming = false;
    		} else {
    			mMP = MediaPlayer.create(mContext, R.raw.sabroff1);
    			mMP.start();
    			this.setSabreOut(false);

    			mSensorManager.unregisterListener(mListener);
    			mForceActive = false;
	mHumming = false;
    		}
    	} else if(true == mForceActive) {
    		mColorNum++;
    		if(mColorNum >= MAX_COLORS)
    			mColorNum = 0;
    		this.invalidate();
    	}
    	return true;
    }

    public void onStop() {
    	// if there is any sound playing, stop it.
    	if(mMP.isPlaying() == true)
    		mMP.stop();

		try {
				mMP.prepare();
		} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
    	
    	// now release the media player
    	mMP.release();
    	
    	// clear out the vector of magnitudes
    	mMagnitudes.clear();
    	
    	// last unregister the sensor listener.
    	mSensorManager.unregisterListener(mListener);
    	
//    	mWakeLock.release();
    	this.setKeepScreenOn(mKeepScreenOn);
    }
 
    private final SensorListener mListener = new SensorListener() {
        public void onSensorChanged(int sensor, float[] values) {
        	if(!mPaused && sensor == SensorManager.SENSOR_ACCELEROMETER) {
        		long currTime = System.currentTimeMillis();
        		float magnitude = 0.0f;
    			magnitude = (float)Math.sqrt(values[0]*values[0]+values[1]*values[1]+values[2]*values[2]);
    			magnitude = Math.abs(magnitude - SensorManager.GRAVITY_EARTH);
        		int movement = NO_MOVEMENT;
       			if(magnitude >= (HIT_FORCE+mSenseOffset))
       				movement = HIT_DETECTED;
       			else if(magnitude >= (SWING_FORCE+mSenseOffset))
       				movement = SWING_DETECTED;
	
        		if(!mPaused && movement == HIT_DETECTED && (currTime - lastTime >= HIT_DELAY)) {
        			mClash = true;
        			
        			if(!mPaused) {
        			if(mMP.isPlaying() == true) {
       					mMP.stop();
       				}
       				try {
       					mMP.prepare();
       				} catch (IllegalStateException e) {
       					// TODO Auto-generated catch block
       					e.printStackTrace();
       				} catch (IOException e) {
       					// TODO Auto-generated catch block
       					e.printStackTrace();
       				}
       				mMP.release();
       				Random rand = new Random();
       				int i = rand.nextInt(3);
       				switch(i) {
      				case 0 : mMP = MediaPlayer.create(mContext, R.raw.hit01); break;
       				case 1 : mMP = MediaPlayer.create(mContext, R.raw.hit02); break;
       				case 2 : mMP = MediaPlayer.create(mContext, R.raw.hit03); break;
       				case 3 : mMP = MediaPlayer.create(mContext, R.raw.hit04); break;
       				case 4 : mMP = MediaPlayer.create(mContext, R.raw.hit05); break;
       				case 5 : mMP = MediaPlayer.create(mContext, R.raw.hit06); break;
       				}
    				
       				mMP.start();
        			}
       				mHumming = false;
       				mGlowLevel = 0xFF;
       				mGlowInc = -2;
       				lastTime = currTime;
        		} else if(!mPaused && movement == SWING_DETECTED && currTime-lastTime >= SWING_DELAY) {
        			mClash = false;
        			if(!mPaused && (mMP.isPlaying() == false || mHumming == true)) {
        				if(mMP.isPlaying() == true) {
           					mMP.stop();
           				}
        				try {
        					mMP.prepare();
        				} catch (IllegalStateException e) {
        					// TODO Auto-generated catch block
        					e.printStackTrace();
        				} catch (IOException e) {
        					// TODO Auto-generated catch block
        					e.printStackTrace();
        				}
       					mMP.release();

       					Random rand = new Random();
       					int i; 
   						i = rand.nextInt(7);
   						switch(i) {
  						case 0 : mMP = MediaPlayer.create(mContext, R.raw.fastsabr); break;
   						case 1 : mMP = MediaPlayer.create(mContext, R.raw.sabrswg1); break;
   						case 2 : mMP = MediaPlayer.create(mContext, R.raw.sabrswg4); break;
   						case 3 : mMP = MediaPlayer.create(mContext, R.raw.sabrswg7); break;
   						case 4 : mMP = MediaPlayer.create(mContext, R.raw.sabrswg2); break;
   						case 5 : mMP = MediaPlayer.create(mContext, R.raw.sabrswg5); break;
   						case 6 : mMP = MediaPlayer.create(mContext, R.raw.sabrswg6); break;
   						}
   						mMP.start();
  						mHumming = false;
   						mGlowLevel = 0xFF;
   						mGlowInc = -2;
   						lastTime = currTime;
        			}
        		} else if(!mPaused && false == mHumming) {// && (currTime-lastTime > SWING_DELAY) ) {
        			if(!mPaused && false == mMP.isPlaying()) {
        				try {
        					mMP.prepare();
        				} catch (IllegalStateException e) {
        					// TODO Auto-generated catch block
        					e.printStackTrace();
        				} catch (IOException e) {
        					// TODO Auto-generated catch block
        					e.printStackTrace();
        				}
        				if(!mPaused) {
        				mMP.release();
        				mMP = MediaPlayer.create(mContext, R.raw.sabrhum);
        				mMP.setLooping(true);
        				mMP.start();
        				mHumming = true;
        				}
        			}
        		}
        	}
        }

        public void onAccuracyChanged(int x, int y) {

        }
    };

    // this method will execute once thread.start is called.  This method
    // will notify the handler that it is time to update the game
    public void run() {
    	while(true) {
   			if(!mPaused)
   				handler.sendEmptyMessage(0);
   			try {
				// allow the thread to sleep a bit and allow other threads to run
   				// 17 milliseconds will allow for a frame rate of about 60 FPS.
   				Thread.sleep(15);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
    
    private Handler handler = new Handler() {
    	@Override
    	public void handleMessage(Message msg) {
    		if(mPaused)
    			return;
    		if(mSabreOut) {
    			if(mSabreHeight > 5)
    				mSabreHeight -= 15;
    			if(mSabreHeight < 5)
    				mSabreHeight = 5;
    			
    			if(mGlowLevel >= 0x62)
    				mGlowLevel -= 4;
    			else {
    				mGlowLevel += mGlowInc;
    			
    				if(mGlowLevel < 0x20 || mGlowLevel > 0x60) {
    					mGlowInc = -mGlowInc;
    					mGlowLevel += mGlowInc;
    				}
    			}
    		} else {
    			if(mSabreHeight < 320)
    				mSabreHeight += 15;
    			if(mSabreHeight > 320)
    				mSabreHeight = 320;
    		}
    			
    		invalidate();
    	}
    };
    
    public void toggleZoom() {
    	mZoom = !mZoom;
    	if(false == mForceActive && true == mZoom) {
    		mZoom = false;
    	}
    }
    
    public void toggleBackground() {
    	mBgVisible = !mBgVisible;
    }
    
    public void toggleHumming() {
    	mPlayHum = !mPlayHum;
    }
    
    public void toggleSensitivity() {
    	mSensitive = !mSensitive;
    }
    
    public void setSenseOffset(float offset) {
    	mSenseOffset = offset;
    }
    
    public float getSenseOffset() {
    	return mSenseOffset;
    }
    
    public void setCustomColor(int r, int g, int b) {
    	mCustomColor[0] = r;
    	mCustomColor[1] = g;
    	mCustomColor[2] = b;
    	
    	mColorNum = 8;
    }
    
    public int getCustomColor() {
    	return Color.rgb(mCustomColor[0], mCustomColor[1], mCustomColor[2]);
    }
/*    
    private int updateAccelReadings(float magnitude) {
    	if(mMagnitudes.size() == NUM_SAMPLES)
    		mMagnitudes.removeElementAt(0);
    	
    	mMagnitudes.add(mMagnitudes.size(), magnitude);
    	
    	if(mMagnitudes.size() < NUM_SAMPLES)
    		return NO_CHANGE;
    	
    	boolean moved = false;
    	boolean stopped = false;
    	boolean hit = false;
    	float diffThreshold = 2.00f;
    	float diffHit = 8.0f;
    	float diff = 0.0f;
    	
    	if(false == mSensitive) {
    		diffThreshold *= 2.00f;
    		diffHit *= 2.0f;
    	}
    	
    	float oldestMag = (float)mMagnitudes.get(0);
   		float newestMag = (float)mMagnitudes.get(NUM_SAMPLES-1);
   		diff = Math.abs(newestMag-oldestMag);
    		
   		if(newestMag > oldestMag) {
   			if(diff >= diffThreshold) 
   				moved = true;
   		} else if(oldestMag >= newestMag) {
   			if(diff >= diffThreshold && diff <= diffHit)
   				stopped = true;
   			else if(diff > diffHit)
   				hit = true;
   		}

    	if(moved) {
    		return SWING_DETECTED;
    	}
    	else if(hit) {
    		return HIT_DETECTED;
    	}
    	else if(stopped) {
    		return NO_MOVEMENT;
    	}

    	return NO_MOVEMENT;
    }
*/    
    private int updateAccelReadings(float magnitude) {
    	float maxDeviation = 0.0f;
    	if(mMagnitudes.size() == NUM_SAMPLES)
    		mMagnitudes.removeElementAt(0);
    	
    	mMagnitudes.add(mMagnitudes.size(), magnitude);
    	
    	if(mMagnitudes.size() < NUM_SAMPLES)
    		return NO_CHANGE;
    	
    	int moved = 0;
    	int stopped = 0;
    	int state = NO_CHANGE;
		float threshold = ACCEL_THRESHOLD;
		float hitForce = HIT_FORCE;

		if(false == mSensitive) {
			threshold *= 2.0f;
			hitForce += hitForce/2.0f;
		}

		for(int i = 0; i < mMagnitudes.size(); i++) {
    		float mag = mMagnitudes.get(i);
    		float deviation = Math.abs(mag - SensorManager.GRAVITY_EARTH);
    		if(deviation > maxDeviation)
    			maxDeviation = deviation;
    		
    		if(mag < (SensorManager.GRAVITY_EARTH-threshold) || mag > (SensorManager.GRAVITY_EARTH+threshold))
    			moved++;
    		else
    			stopped++;
    	}
    	
    	if(moved == NUM_SAMPLES) {
    		state = SWING_DETECTED;
    		if(mMaxDeviation < maxDeviation)
    			mMaxDeviation = maxDeviation;
    	} else if(stopped == NUM_SAMPLES) {
    		if(mMaxDeviation >= hitForce || mLastMove == SWING_DETECTED) {
    			state = HIT_DETECTED;
    		} else
    			state = NO_MOVEMENT;
    		
    		mMaxDeviation = 0.0f;
    	}

    	mLastMove = state;
    	return state;
    }
    
   private void drawSabre(Canvas canvas, boolean zoomed) {
        GradientDrawable background = null;
        GradientDrawable blade = null;

        if(false == mZoom)
        {
        	if(mBgVisible)
        		canvas.drawBitmap(mStarField, 0, 0, null);

        	switch(mColorNum) {
        	case 0:
        		blade = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
        				new int[] { 0xFF800000, 0xFFFF0000, 0xFFFF0000, 0xFF800000 });
        		blade.setStroke(2, 0xFFFF2020);
        		background = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
        				new int[] { 0x00000000, 0xFF000000+((mGlowLevel)<<16)+((mGlowLevel>>1)<<8)+(mGlowLevel>>1), 0x00000000 });
        		break;
        	case 1:
        		blade = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
        				new int[] { 0xFF008000, 0xFF80FF80, 0xFF80FF80, 0xFF008000 });
        		blade.setStroke(2, 0xFF80FF80);
        		background = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
        				new int[] { 0x00000000, 0xFF000000+((mGlowLevel>>1)<<16)+((mGlowLevel)<<8)+(mGlowLevel>>1), 0x00000000 });
        		break;
        	case 2:
        		blade = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
        				new int[] { 0xFF000080, 0xFF8080FF, 0xFF8080FF, 0xFF000080 });
        		blade.setStroke(2, 0xFF8080FF);
        		background = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
        				new int[] { 0x00000000, 0xFF000000+((mGlowLevel>>1)<<16)+((mGlowLevel>>1)<<8)+(mGlowLevel), 0x00000000 });
        		break;
        	case 3:
        		blade = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
        				new int[] { 0xFF808000, 0xFFFFFF80, 0xFFFFFF80, 0xFF808000 });
        		blade.setStroke(2, 0xFFFFFF80);
        		background = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
        				new int[] { 0x00000000, 0xFF000000+(mGlowLevel<<16)+(mGlowLevel<<8), 0x00000000 });
        		break;
        	case 4:
        		blade = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
        				new int[] { 0xFF800080, 0xFFFF80FF, 0xFFFF80FF, 0xFF800080 });
        		blade.setStroke(2, 0xFFFF80FF);
        		background = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
        				new int[] { 0x00000000, 0xFF000000+(mGlowLevel<<16)+(mGlowLevel), 0x00000000 });
        		break;
        	case 5:
        		blade = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
        				new int[] { 0xFF008080, 0xFF80FFFF, 0xFF80FFFF, 0xFF008080 });
        		blade.setStroke(2, 0xFF80FFFF);
        		background = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
        				new int[] { 0x00000000, 0xFF000000+(mGlowLevel)+(mGlowLevel<<8), 0x00000000 });
        		break;
        	case 6:
        		blade = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
        				new int[] { 0xFF803000, 0xFFFF6000, 0xFFFF6000, 0xFF803000 });
        		blade.setStroke(2, 0xFFFF8000);
        		background = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
        				new int[] { 0x00000000, 0xFF000000+(mGlowLevel<<16)+((mGlowLevel>>1)<<8), 0x00000000 });
        		break;
        	case 7:
        		blade = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
        				new int[] { 0xFF800040, 0xFFFF0080, 0xFFFF0080, 0xFF800040 });
        		blade.setStroke(2, 0xFFFF0080);
        		background = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
        				new int[] { 0x00000000, 0xFF000000+(mGlowLevel<<16)+(mGlowLevel>>1), 0x00000000 });
        		break;
        	case 8:
        		int colors[] = new int[3];
        		colors[0] = Color.argb(255, mCustomColor[0]/2, mCustomColor[1]/2, mCustomColor[2]/2);
        		colors[1] = Color.argb(255, mCustomColor[0], mCustomColor[1], mCustomColor[2]);
        		float percent = (float)mGlowLevel/255.0f;
        		colors[2] = Color.argb(255, (int)((float)mCustomColor[0]*percent), (int)((float)mCustomColor[1]*percent), (int)((float)mCustomColor[2]*percent));
        		blade = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
        				new int[] { colors[0], colors[1], colors[1], colors[0] });
        		blade.setStroke(2, colors[1]);
        		background = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
        				new int[] { 0x00000000, colors[2], 0x00000000 });
        		break;
        	}
        	if(mSabreOut)
        	{
        		background.setShape(GradientDrawable.LINEAR_GRADIENT);
        		if(false == mZoom) {
        			int mWidthDiv4 = (int)(mWidth / 3.0f);
        			background.setBounds(mWidthDiv4, -30, (int)mWidth - mWidthDiv4, 330);
        			
        			background.setCornerRadii(new float[] { 0.0f, 0.0f, 0.0f, 0.0f, 120.0f, 120.0f, 120.0f, 120.0f});
        		}
        		else
        		{
        			background.setBounds(0, 0, (int)mWidth, (int)mHeight);
        		}
        		background.draw(canvas);
        	}                	
        	
        	if(false == mZoom)
        	{
//        		blade.setBounds(149, mSabreHeight, 170, 360); //used for ring hilt
        		blade.setBounds(149, mSabreHeight, 170, 330);
        		blade.setShape(GradientDrawable.LINEAR_GRADIENT);
    			blade.setCornerRadii(new float[] { 10.0f, 10.0f, 10.0f, 10.0f, 0.0f, 0.0f, 0.0f, 0.0f});
//        		blade.setCornerRadius(5);
        		blade.draw(canvas);
        		
//        		canvas.drawBitmap(mSabre, 106, 339, null); //used for ring hilt
        		canvas.drawBitmap(mSabre, 144, 299, null);
        	}
        } else {
        	GradientDrawable back2 = null;
        	
        	switch(mColorNum) {
        	case 0:
        		blade = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
        				new int[] { 0xFF800000, 0xFFFF0000, 0xFFFF0000, 0xFF800000 });
        		blade.setStroke(5, 0xFFFF2020);
        		background = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
        				new int[] { 0x00000000, 0xFF000000+((mGlowLevel)<<16)+((mGlowLevel>>1)<<8)+(mGlowLevel>>1) });
        		back2 = new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT,
        				new int[] { 0x00000000, 0xFF000000+((mGlowLevel)<<16)+((mGlowLevel>>1)<<8)+(mGlowLevel>>1) });
        		break;
        	case 1:
        		blade = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
        				new int[] { 0xFF008000, 0xFF80FF80, 0xFF80FF80, 0xFF008000 });
        		blade.setStroke(5, 0xFF80FF80);
        		background = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
        				new int[] { 0x00000000, 0xFF000000+((mGlowLevel>>1)<<16)+((mGlowLevel)<<8)+(mGlowLevel>>1)});
        		back2 = new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT,
        				new int[] { 0x00000000, 0xFF000000+((mGlowLevel>>1)<<16)+((mGlowLevel)<<8)+(mGlowLevel>>1)});
        		break;
        	case 2:
        		blade = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
        				new int[] { 0xFF000080, 0xFF8080FF, 0xFF8080FF, 0xFF000080 });
        		blade.setStroke(5, 0xFF8080FF);
        		background = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
        				new int[] { 0x00000000, 0xFF000000+((mGlowLevel>>1)<<16)+((mGlowLevel>>1)<<8)+(mGlowLevel)});
        		back2 = new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT,
        				new int[] { 0x00000000, 0xFF000000+((mGlowLevel>>1)<<16)+((mGlowLevel>>1)<<8)+(mGlowLevel)});
        		break;
        	case 3:
        		blade = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
        				new int[] { 0xFF808000, 0xFFFFFF80, 0xFFFFFF80, 0xFF808000 });
        		blade.setStroke(5, 0xFFFFFF80);
        		background = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
        				new int[] { 0x00000000, 0xFF000000+(mGlowLevel<<16)+(mGlowLevel<<8) });
        		back2 = new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT,
        				new int[] { 0x00000000, 0xFF000000+(mGlowLevel<<16)+(mGlowLevel<<8) });
        		break;
        	case 4:
        		blade = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
        				new int[] { 0xFF800080, 0xFFFF80FF, 0xFFFF80FF, 0xFF800080 });
        		blade.setStroke(5, 0xFFFF80FF);
        		background = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
        				new int[] { 0x00000000, 0xFF000000+(mGlowLevel<<16)+(mGlowLevel) });
        		back2 = new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT,
        				new int[] { 0x00000000, 0xFF000000+(mGlowLevel<<16)+(mGlowLevel) });
        		break;
        	case 5:
        		blade = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
        				new int[] { 0xFF008080, 0xFF80FFFF, 0xFF80FFFF, 0xFF008080 });
        		blade.setStroke(5, 0xFF80FFFF);
        		background = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
        				new int[] { 0x00000000, 0xFF000000+(mGlowLevel)+(mGlowLevel<<8) });
        		back2 = new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT,
        				new int[] { 0x00000000, 0xFF000000+(mGlowLevel)+(mGlowLevel<<8) });
        		break;
        	case 6:
        		blade = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
        				new int[] { 0xFF803000, 0xFFFF6000, 0xFFFF6000, 0xFF803000 });
        		blade.setStroke(5, 0xFFFF8000);
        		background = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
        				new int[] { 0x00000000, 0xFF000000+(mGlowLevel<<16)+((mGlowLevel>>1)<<8) });
        		back2 = new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT,
        				new int[] { 0x00000000, 0xFF000000+(mGlowLevel<<16)+((mGlowLevel>>1)<<8) });
        		break;
        	case 7:
        		blade = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
        				new int[] { 0xFF800040, 0xFFFF0080, 0xFFFF0080, 0xFF800040 });
        		blade.setStroke(5, 0xFFFF0080);
        		background = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
        				new int[] { 0x00000000, 0xFF000000+(mGlowLevel<<16)+(mGlowLevel>>1) });
        		back2 = new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT,
        				new int[] { 0x00000000, 0xFF000000+(mGlowLevel<<16)+(mGlowLevel>>1) });
        		break;
        	case 8:
        		int colors[] = new int[3];
        		colors[0] = Color.argb(255, mCustomColor[0]/2, mCustomColor[1]/2, mCustomColor[2]/2);
        		colors[1] = Color.argb(255, mCustomColor[0], mCustomColor[1], mCustomColor[2]);
        		float percent = (float)mGlowLevel/255.0f;
        		colors[2] = Color.argb(255, (int)((float)mCustomColor[0]*percent), (int)((float)mCustomColor[1]*percent), (int)((float)mCustomColor[2]*percent));
        		
        		blade = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
        				new int[] { colors[0], colors[1], colors[1], colors[0] });
        		blade.setStroke(5, colors[1]);
        		background = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
        				new int[] { 0x00000000, colors[2] });
        		back2 = new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT,
        				new int[] { 0x00000000, colors[2] });
        		break;
        	}
    		
        	background.setShape(GradientDrawable.LINEAR_GRADIENT);
    		back2.setShape(GradientDrawable.LINEAR_GRADIENT);
   			int mWidthDiv = (int)(mWidth / 3.0f);
   			background.setBounds(0, 0, mWidthDiv, (int)mHeight);
   			back2.setBounds(mWidth - mWidthDiv, 0, mWidth, (int)mHeight);
    		
   			blade.setBounds(mWidthDiv, -10, mWidth - mWidthDiv, (int)mHeight+10);
    		blade.setShape(GradientDrawable.LINEAR_GRADIENT);
    		blade.draw(canvas);

    		background.draw(canvas);
    		back2.draw(canvas);
        }
		if(mZoom && mClash && mGlowLevel > 0xD0) {
			mPaint.setColor(0x00FFFFFF + (mGlowLevel<<24));
			canvas.drawRect(0, 0, mWidth, mHeight, mPaint);
		}
    }
   
   public void onPause() {
	   mPaused = true;
       this.setKeepScreenOn(mKeepScreenOn);
	   mSensorManager.unregisterListener(mListener);
	   mThread.suspend();
	   
//	   long startTime = System.currentTimeMillis();
//	   while(System.currentTimeMillis() - startTime <= 1000);
//	   mMP.release();
   }
   
   public void onResume() {
	   mPaused = false;
       this.setKeepScreenOn(true);
	   mMP = new MediaPlayer();
	   mMP = MediaPlayer.create(mContext, R.raw.sabrhum);
	   mMP.setLooping(true);
	   if(true == mSabreOut) {
		   int mask = 0;
		   mask = SensorManager.SENSOR_ACCELEROMETER;

		   mSensorManager.registerListener(mListener, mask, SensorManager.SENSOR_DELAY_FASTEST);
		   mMP.start();
		   mHumming = true;
	   }
	   mThread.resume();
//       mThread = new Thread(this);
   }
}

