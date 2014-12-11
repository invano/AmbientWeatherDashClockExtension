package com.invano.ambientweather;

import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.preference.PreferenceManager;
import android.util.Log;

public class AmbientWeatherExtension extends DashClockExtension {
    private SensorManager mSensorManager;
    private SensorEventListener mTempListener;
    private SensorEventListener mHumidityListener;
    private SensorEventListener mPressureListener;
    private Sensor mTemp;
    private Sensor mHumidity;
    private Sensor mPressure;
    public static final String PREF_NAME = "pref_name_default";
    private float mTempValue = 0;
    private float mHumidityValue = 0;
    private float mPressureValue = 0;
    private boolean humidityOnTop;
    private String tempUnit;
    private String mTempDigits;
    private String mHumidityDigits;
    private String mPressureDigits;
    
	@Override
	public void onCreate() {
		mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mTemp = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        mHumidity = mSensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        mPressure = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        mTempListener = new SensorEventListener() {

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onSensorChanged(SensorEvent event) {
				mTempValue = event.values[0];
			}
        	
        };
        mHumidityListener = new SensorEventListener() {

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onSensorChanged(SensorEvent event) {
				mHumidityValue = event.values[0];				
			}
        	
        };
        mPressureListener = new SensorEventListener() {

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onSensorChanged(SensorEvent event) {
				mPressureValue = event.values[0];				
			}
        	
        };
		super.onCreate();
	}

	@Override
	public void onRebind(Intent intent) {
	}

	
	@Override
	protected void onInitialize(boolean isReconnect) {
		super.onInitialize(isReconnect);
		setUpdateWhenScreenOn(true);
	}

	@Override
    protected void onUpdateData(int reason) {
        // Get preference value.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        humidityOnTop = sp.getBoolean("prefHumidityOnTop", false);
        tempUnit = sp.getString("prefTempUnit", "C");
        mTempDigits = "%." + sp.getString("prefTempDigit", "0") + "f";
        mHumidityDigits = "%." + sp.getString("prefHumidityDigit", "0") + "f";
        mPressureDigits = "%." + sp.getString("prefPressionDigit", "0") + "f";
        register();
        try {
			Thread.sleep(1000);
			if (tempUnit.equals("F"))
				mTempValue = mTempValue*1.8f + 32.0f;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        // Publish the extension data update.
        publishUpdate(new ExtensionData()
                .visible(true)
                .icon(R.drawable.ic_ambientweather_extension)
                .status(humidityOnTop ? String.format(mHumidityDigits, mHumidityValue) + "%" : String.format(mTempDigits, mTempValue) + "??" + tempUnit)
                .expandedTitle(humidityOnTop ? 
                		"Relative humidity " + String.format(mHumidityDigits, mHumidityValue) + "%" : "Temperature " + String.format(mTempDigits, mTempValue) + "°" + tempUnit)
                .expandedBody((humidityOnTop ? "Temperature " + String.format(mTempDigits, mTempValue) + "??" + tempUnit : "Relative humidity " + String.format(mHumidityDigits, mHumidityValue) + "%") + 
                		"\nPressure " + String.format(mPressureDigits, mPressureValue) + " hPa")
//                .contentDescription("Completely different text for accessibility if needed.")
                .clickIntent(new Intent(this, AmbientWeatherSettingsActivity.class))
                );
        unregisterListener();
    }
	

  public void unregisterListener(){
		Log.i("SensorManager", "Unegistering Sensor Manager from BroadcastReceiver");

	  	mSensorManager.unregisterListener(mTempListener);
	  	mSensorManager.unregisterListener(mHumidityListener);
	  	mSensorManager.unregisterListener(mPressureListener);
  }
  
  public void register() {
		Log.i("SensorManager", "Registering Sensor Manager from BroadcastReceiver");

		mSensorManager.registerListener(mTempListener, mTemp, SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener(mHumidityListener, mHumidity, SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener(mPressureListener, mPressure, SensorManager.SENSOR_DELAY_NORMAL);
  }
 
}