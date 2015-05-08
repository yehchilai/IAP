package com.iamhomebody.iaptest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.vending.billing.IInAppBillingService;
//import com.unity3d.player.UnityPlayer;






import android.support.v7.app.ActionBarActivity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends ActionBarActivity {

	protected static final String TAG = "IAMHOMEBODY_IAB";
	
	IabHelper mHelper;
	
	// SKUs for our products
    static final String SKU_COINS = "produt_2_coin";
    static final String SKU_PRODUCT = "COIN";
    static final String SKU_TEST = "com.andoid";
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
	   // ...
	   String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAuC+EuTAkKk0YottDyfUjfMXzEBfTrx9MPjiyWEAxJLxXh1ejOj8fvlnGGRCDCUHv7lr17Hap5v+I9JfCuKt0VbUv5mfq/ockfUdMLCiMPHHL26Xgpco9+i8jjZnxsndyto9cF2Qs4FYZfCTObj5QP5WdVlO9vr6fvc7SUVS4YBOGvP9D7fLu8hVk8C1Cw54UVojruM9iQCKjKnP0xHkxQX/sCH/zLbBtFofUzLRlRkqZBCi9vkDSRAEN54KDByx862ticFOtTMabJ5J6YQIvU/bR4T9+EDyeU00pufmcRZqdef74A/vwVAu7y1MK+NSiXYmf3UF1+avK2akBn3K3SwIDAQAB";
	   super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_main);
       Log.d(TAG, "OnCreate IABTEST");
       
       // purchase button control
	   Button purchase = (Button) findViewById(R.id.purchase);
		
	   purchase.setOnClickListener(new OnClickListener(){
	
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				buy();
			}
			
	   });
       
	   // compute your public key and store it in base64EncodedPublicKey
	   mHelper = new IabHelper(this, base64EncodedPublicKey);
	   
	   mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
		   public void onIabSetupFinished(IabResult result) {
		      if (!result.isSuccess()) {
		         // Oh noes, there was a problem.
		         Log.d(TAG, "Problem setting up In-app Billing: " + result);
		      }
		         // Hooray, IAB is fully set up!
		   }
		});
	}
	
	IabHelper.QueryInventoryFinishedListener 
	   mQueryFinishedListener = new IabHelper.QueryInventoryFinishedListener() {
	   public void onQueryInventoryFinished(IabResult result, Inventory inventory)   
	   {
	      if (result.isFailure()) {
	         // handle error
	    	 Log.d(TAG, "result.isFailure() error");
	         return;
	       }
	      
	      List<String> productDetails = inventory.getAllOwnedSkus();
//	      SkuDetails productDetails = inventory.getSkuDetails(SKU_COINS);

	      if (productDetails != null){
	    	  Log.d(TAG,"Deck price is: " + productDetails.size());
//	    	  Log.d(TAG,"Deck price is: " + productDetails.getPrice());
	      }else{

	    	  Log.d(TAG,"No Product Detail" );
	      }
	       // update the UI 
	   }
	};
	
	private void buy(){
		Log.d(TAG, "Click Button !! ");
		String[] moreSkus = {SKU_COINS};
		mHelper.queryInventoryAsync(true, Arrays.asList(moreSkus), mQueryFinishedListener);
//		List additionalSkuList = new List();
//		additionalSkuList.add(SKU_COINS);
//		mHelper.queryInventoryAsync(true, additionalSkuList,
//		   mQueryFinishedListener);
	}
	
	@Override
	public void onDestroy() {
	   super.onDestroy();
	   if (mHelper != null) mHelper.dispose();
	   mHelper = null;
	}
	
}
