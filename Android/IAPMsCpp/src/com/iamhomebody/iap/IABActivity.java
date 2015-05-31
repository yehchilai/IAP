package com.iamhomebody.iap;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.unity3d.player.UnityPlayerActivity;


public class IABActivity extends UnityPlayerActivity{
	
	// Callback function
	public interface callbackEvent{
		public boolean callbackEventFunction(int requestCode, int resultCode, Intent data);
	}

	private final String TAG = "IABActivity"; 
	protected callbackEvent mCallbackEvent;
	static protected IABActivity mIabActivity;
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		
		mIabActivity = this;
		Log.v(TAG, "### onCreate ");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.v(TAG, "### onActivityResult !!");
		
		boolean result =false;
        if (mCallbackEvent !=null){
            try{
            	result = mCallbackEvent.callbackEventFunction(requestCode, resultCode, data);
            }
            catch(Exception e){
            	result =false;
            }
        }
 
        if (result ==false){
            super.onActivityResult(requestCode, resultCode, data);
        }
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mIabActivity = null;
		Log.v(TAG, "### onDestroy");
	}
	
	public static void registerOnActivityResultCallbackFunction(final callbackEvent callbackFunction){
		if(mIabActivity != null){
			mIabActivity.mCallbackEvent = callbackFunction;
		}
	}

}
