package com.iamhomebody.iap;

import com.iamhomebody.iap.util.*;
import com.unity3d.player.UnityPlayer;

import android.app.Activity;
import android.content.Intent;

import com.iamhomebody.iap.IABActivity;;

public class IABBinder {
	
	static final String TAG = "msgReceiver";
	private Activity mActivity;
	private IabHelper mIabHelper;
	private String mEventHandler;
	
	
	public IABBinder(String base64EncodedPublicKey, String strEventHandler){
		mActivity = UnityPlayer.currentActivity;
		mEventHandler = strEventHandler;
		
		if(mIabHelper != null){
			dispose();
		}
		
		mIabHelper = new IabHelper(mActivity, base64EncodedPublicKey);
		
		mIabHelper.enableDebugLogging(true);
		
		mIabHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
			
			@Override
			public void onIabSetupFinished(IabResult result) {
				// TODO Auto-generated method stub
				if(!result.isSuccess()){
					// sent a message to Unity gameObject with JSON format
					UnityPlayer.UnitySendMessage(mEventHandler, TAG, "{\"code\":\"1\",\"ret\":\"false\",\"desc\":\""+result.toString()+"\"}");
					dispose();
					return;
				}
				
				// sent a message to Unity gameObject with JSON format
				UnityPlayer.UnitySendMessage(mEventHandler, TAG, "{\"code\":\"1\",\"ret\":\"false\",\"desc\":\""+result.toString()+"\"}");
				
				// register callback function in IabActivity
				IABActivity.registerOnActivityResultCallbackFunction(new IABActivity.callbackEvent() {
					
					@Override
					public boolean callbackEventFunction(int requestCode, int resultCode,
							Intent data) {
						// TODO Auto-generated method stub
						if(mIabHelper.handleActivityResult(requestCode, resultCode, data)){
							return true;
						}else{
							return false;
						}
					}
				});
				
			}
		});
		
	}


	public void dispose() {
		// TODO Auto-generated method stub
		if(mIabHelper != null){
			mIabHelper.dispose();
		}
		
		mIabHelper = null;
	}
	
	public void purchase(String SKU, String requestCode, String payload){
		int code = Integer.parseInt(requestCode);
		if(mIabHelper != null){
			mIabHelper.launchPurchaseFlow(mActivity, SKU, code, mPurchaseFinishedListener, payload);
		}
	}
	
	IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
		
		@Override
		public void onIabPurchaseFinished(IabResult result, Purchase info) {
			// TODO Auto-generated method stub
			if(result.isFailure()){
				// sent a message to Unity gameObject with JSON format
				UnityPlayer.UnitySendMessage(mEventHandler, TAG, "{\"code\":\"2\",\"ret\":\"false\",\"desc\":\"\",\"sign\":\"\"}");
				return;
			}
				
			if(result.isSuccess()){
				boolean resultFlag = false;
				String resultJSON = "";
				String resultSignature = "";
				
				if(info != null){
					resultJSON = info.getOriginalJson().replace('\"', '\'');
					resultSignature = info.getSignature();
				}
				
				UnityPlayer.UnitySendMessage(mEventHandler, TAG, "{\"code\":\"2\",\"ret\":\""+resultFlag+"\",\"desc\":\""+resultJSON+"\",\"sign\":\""+resultSignature+"\"}");
				
			}
		}
	};
	
	public void consume(String itemType, String purchaseJSON, String signature){
		String json = purchaseJSON.replace('\"', '\'');
		if(mIabHelper == null){
			return;
		}
		
		Purchase purchase = null;
		try{
			purchase = new Purchase(itemType, json, signature);
		}catch(Exception e){
			purchase = null;
		}
		
		if(purchase !=null){
			final Purchase currentPurchase = purchase;
			mActivity.runOnUiThread(new Runnable(){
				public void run(){
					mIabHelper.consumeAsync(currentPurchase, mConsumeFinishedListener);
				}
			});
		}
	}
	
	IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
		
		@Override
		public void onConsumeFinished(Purchase purchase, IabResult result) {
			// TODO Auto-generated method stub
			if(result.isSuccess()){
				UnityPlayer.UnitySendMessage(mEventHandler, TAG, "{\"code\":\"3\",\"ret\":\"true\",\"desc\":\""+purchase.getOriginalJson().replace('\"', '\'')+"\",\"sign\":\""+purchase.getSignature()+"\"}");
				
			}else{
				UnityPlayer.UnitySendMessage(mEventHandler, TAG, "{\"code\":\"3\",\"ret\":\"false\",\"desc\":\"\",\"sign\":\"\"}");
				
			}
		}
	};
}
