package com.iamhomebody.iap;

import com.android.vending.billing.IInAppBillingService;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

public class IAPBinder {

	private static GoogleIAB googleIAB_instance;
	private static IAPBinder _instance;
	
	public static GoogleIAB instance(){
		if(_instance == null)
			googleIAB_instance = new GoogleIAB();
		
		return googleIAB_instance;
	}
	
	public static void openGallary(Activity activity){
		Intent intent = new Intent(activity, Gallery.class);
		activity.startActivity(intent);
	}
	
	public static void runGoogleIAB(Activity activity){
//		Intent intent = new Intent(activity, GoogleIAB.class);
//		Log.e("### ","RunGoogleIAB");
//	      
//		activity.startActivity(intent);
		
		
		Intent intent = new Intent(activity, GoogleIAB.class);
		Log.e("### ","RunGoogleIAB");
	      
		activity.startActivity(intent);
	}
	
	public static void Test(){
		Log.e("### ","Test");
	}
	
	public static void initGoogleIAB(){
		Log.e("### ","initGoogleIAB");
		if(_instance == null)
			googleIAB_instance = new GoogleIAB();
		
	}
	
	public static void buyGoogleIAB()
	{
		googleIAB_instance.buy();
	}
	public static void buy(){
		Log.e("### ","initGoogleIAB in IAPBinder");
	}
}
