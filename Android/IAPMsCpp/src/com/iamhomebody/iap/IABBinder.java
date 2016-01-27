package com.iamhomebody.iap;

import java.util.Arrays;

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
	private Inventory myInventory;
	
	// Constructor and initialize the IAB functionality
	public IABBinder(String base64EncodedPublicKey, String strEventHandler){
		mActivity = UnityPlayer.currentActivity;
		mEventHandler = strEventHandler;
		
		if(mIabHelper != null){
			dispose();
		}
		
		mIabHelper = new IabHelper(mActivity, base64EncodedPublicKey);
		
		mIabHelper.enableDebugLogging(true); // Turn to false when the app is published
		
		mIabHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
			
			@Override
			public void onIabSetupFinished(IabResult result) {
				// TODO Auto-generated method stub
				if(!result.isSuccess()){
					// sent a message to Unity gameObject with JSON format
					// UnitySendMessage(GameObject Name, Function Name, Agrs)
					UnityPlayer.UnitySendMessage(mEventHandler, TAG, "{\"code\":\"1\",\"ret\":\"false\",\"desc\":\""+result.toString()+"\"}");
					dispose();
					return;
				}
				
				// sent a message to Unity gameObject with JSON format
				UnityPlayer.UnitySendMessage(mEventHandler, TAG, "{\"code\":\"1\",\"ret\":\"false\",\"desc\":\""+result.toString()+"\"}");
				
				// get inventory
				mIabHelper.queryInventoryAsync(mGotInventoryListener);
				
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

	IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {

	    public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
	    	
	    	UnityPlayer.UnitySendMessage(mEventHandler, TAG, "Inventory" + result.getMessage());
			
	    	if (result.isFailure()){
	    		
	    	}else{
	    		
	    		myInventory = inventory;
	    		
	    		if (inventory.hasPurchase("android.test.purchased")) {
		        	
		        	mIabHelper.consumeAsync(inventory.getPurchase("android.test.purchased"), null);
		        }
	    	}
	        
//	        
//			if (result.isFailure()) {
//			    // handle error here
//			  }
//			  else {
//			    // does the user have the premium upgrade?
//			//mIsPremium = inventory.hasPurchase(SKU_PREMIUM);        
//			// update UI accordingly
//			  }
	    }
	};
	
	public void dispose() {
		// TODO Auto-generated method stub
		if(mIabHelper != null){
			mIabHelper.dispose();
		}
		
		mIabHelper = null;
	}
	
	// Get inventory information
	public void inventoryInfo(String[] skus){
		UnityPlayer.UnitySendMessage(mEventHandler, TAG, "Inventory Request !!!");
		//mIabHelper.queryInventoryAsync(mGotInventoryListener);
		//ArrayList<String> list = (ArrayList<String>) Arrays.asList(skus); 
		//mIabHelper.queryInventoryAsync(true, Arrays.asList(skus), mGotInventoryListener);
		
		if(myInventory != null){
			for(String sku:skus){
				SkuDetails detail = myInventory.getSkuDetails(sku);
				if(detail != null){
					UnityPlayer.UnitySendMessage(mEventHandler, TAG, 
							"Product: " + detail.getTitle() +
							"\nPrice: " + detail.getPrice() +
							"\nDescription" + detail.getDescription() +"\n");
				}else{
					UnityPlayer.UnitySendMessage(mEventHandler, TAG, "## Sku: " + sku + " does not exist!");
				}
				
			}
		}else{
			UnityPlayer.UnitySendMessage(mEventHandler, TAG, " ### myInventory == null ###");
		}
	}
	
	
	// Purchase products
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
	
	// Consume products
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
