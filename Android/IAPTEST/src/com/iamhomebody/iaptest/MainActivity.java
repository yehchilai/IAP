package com.iamhomebody.iaptest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.vending.billing.IInAppBillingService;
//import com.unity3d.player.UnityPlayer;

import com.iamhomebody.iaptest.util.IabHelper;
import com.iamhomebody.iaptest.util.IabResult;
import com.iamhomebody.iaptest.util.Inventory;
import com.iamhomebody.iaptest.util.Purchase;
import com.iamhomebody.iaptest.R;

import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
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

public class MainActivity extends Activity {

	protected static final String TAG = "IAMHOMEBODY_IAB";
	
	IabHelper mHelper;
	
	// SKUs for our products
    static final String SKU_COINS = "produt_2_coin";
    static final String SKU_PRODUCT = "COIN";
    static final String SKU_TEST = "android.test.purchased";
    
 // (arbitrary) request code for the purchase flow
    static final int RC_REQUEST = 10001;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
	   // ...
	   
	   super.onCreate(savedInstanceState);
	   
       setContentView(R.layout.activity_main);
       Log.d(TAG, "### OnCreate IABTEST");
       
       String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAuC+EuTAkKk0YottDyfUjfMXzEBfTrx9MPjiyWEAxJLxXh1ejOj8fvlnGGRCDCUHv7lr17Hap5v+I9JfCuKt0VbUv5mfq/ockfUdMLCiMPHHL26Xgpco9+i8jjZnxsndyto9cF2Qs4FYZfCTObj5QP5WdVlO9vr6fvc7SUVS4YBOGvP9D7fLu8hVk8C1Cw54UVojruM9iQCKjKnP0xHkxQX/sCH/zLbBtFofUzLRlRkqZBCi9vkDSRAEN54KDByx862ticFOtTMabJ5J6YQIvU/bR4T9+EDyeU00pufmcRZqdef74A/vwVAu7y1MK+NSiXYmf3UF1+avK2akBn3K3SwIDAQAB";
	   
       // purchase button control
//	   Button purchase = (Button) findViewById(R.id.purchase);
//		
//	   purchase.setOnClickListener(new OnClickListener(){
//	
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				buy();
//			}
//			
//	   });
       
	   // compute your public key and store it in base64EncodedPublicKey
	   mHelper = new IabHelper(this, base64EncodedPublicKey);
	   
	   // enable debug logging (for a production application, you should set this to false).
       mHelper.enableDebugLogging(true);

	   mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
		   public void onIabSetupFinished(IabResult result) {
			   Log.d(TAG, "### Setup finished.");
			   
		      if (!result.isSuccess()) {
		         // Oh noes, there was a problem.
		         Log.d(TAG, "### Problem setting up In-app Billing: " + result);
		      }
		   // Have we been disposed of in the meantime? If so, quit.
              if (mHelper == null) return;
              Log.d(TAG, "### Setup successful. Querying inventory.");
              mHelper.queryInventoryAsync(mGotInventoryListener);
		   }
		});
	}

	IabHelper.QueryInventoryFinishedListener 
		mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
	   public void onQueryInventoryFinished(IabResult result, Inventory inventory)   
	   {
		   
	      if (result.isFailure()) {
	    	 
	         // handle error
	    	 Log.d(TAG, "### result.isFailure() error");
	         return;
	       }
	      
	      List<String> productDetails = inventory.getAllOwnedSkus();
//	      SkuDetails productD = inventory.getSkuDetails(SKU_TEST);

	      if (productDetails != null){
	    	  Log.d(TAG,"### Deck amount is : " + productDetails.size());
//	    	  Log.d(TAG,"Deck price is: " + productD.getPrice());
	      }else{

	    	  Log.d(TAG,"### No Product Detail" );
	      }
	      
	      Purchase gasPurchase = inventory.getPurchase(SKU_TEST);
          if (gasPurchase != null && verifyDeveloperPayload(gasPurchase)) {
              Log.d(TAG, "### We have gas. Consuming it.");
              mHelper.consumeAsync(inventory.getPurchase(SKU_TEST), mConsumeFinishedListener);
              return;
          }
	       // update the UI 
	   }
	};
	
	IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
	   public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
		   Log.d(TAG, "### Purchase finished: " + result + ", purchase: " + purchase);
		   // if we were disposed of in the meantime, quit.
           if (mHelper == null) return;

           if (result.isFailure()) {
        	   
		         Log.d(TAG, "### Error purchasing: " + result);
		         return;
		      }    
		      else if (purchase.getSku().equals(SKU_TEST)) {
		    	  Log.d(TAG, "### Get Test Product !!!");
		    	  Log.d(TAG, "### Purchase is TEST. Starting TEST consumption.");
	              mHelper.consumeAsync(purchase, mConsumeFinishedListener);
		         // consume the gas and update the UI
		      }
		      else if (purchase.getSku().equals(SKU_COINS)) {
		    	  Log.d(TAG, "### Get Coins Product !!!");
		         // give user access to premium content and update the UI
		      }
	   	}
	};
	
	// Called when consumption is complete
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            Log.d(TAG, "### Consumption finished. Purchase: " + purchase + ", result: " + result);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            // We know this is the "gas" sku because it's the only one we consume,
            // so we don't check which sku was consumed. If you have more than one
            // sku, you probably should check...
            if (result.isSuccess()) {
                // successfully consumed, so we apply the effects of the item in our
                // game world's logic, which in our case means filling the gas tank a bit
                Log.d(TAG, "### Consumption successful. Provisioning.");
             
            }
            else {
            	Log.d(TAG, "### Error while consuming: " + result);
            }
            Log.d(TAG, "### End consumption flow.");
        }
    };
	
	public void onBuyGasButtonClicked(View v)
	{
		Log.d(TAG, "### Buy Gay button clicked!! ");
		
		// Purchase Product
		//setWaitScreen(true);
		String payload = "";
		mHelper.launchPurchaseFlow(this, SKU_TEST, RC_REQUEST,   
				   mPurchaseFinishedListener, payload);
		
	}
	
	public void onBuyItemButtonClicked(View v){
		Log.d(TAG, "### Buy Item button clicked!! ");
		
		// Purchase Product
		//setWaitScreen(true);
		String payload = "";
		mHelper.launchPurchaseFlow(this, SKU_COINS, RC_REQUEST,   
				   mPurchaseFinishedListener, payload);
	}
	
	public void onConsumeButtonClicked(View v){
		Purchase purchase = null; 
		try {
             purchase = new Purchase("inapp", "{\"packageName\":\"com.iamhomebody.iaptest\","+
                    "\"orderId\":\"transactionId.android.test.purchased\","+
                    "\"productId\":\"android.test.purchased\",\"developerPayload\":\"\",\"purchaseTime\":0,"+
                    "\"purchaseState\":0,\"purchaseToken\":\"inapp:com.iamhomebody.iaptest :android.test.purchased\"}",
                    "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mHelper.consumeAsync(purchase, mConsumeFinishedListener);
	}
	
	private void buy(){
//		Log.d(TAG, "Buy button clicked!! ");
//		
//		// Purchase Product
//		//setWaitScreen(true);
//		String payload = "";
//		mHelper.launchPurchaseFlow(this, SKU_TEST, 10001,   
//				   mPurchaseFinishedListener, payload);
		
		//Query Inventory
//		String[] moreSkus = {SKU_COINS};
//		mHelper.queryInventoryAsync(true, Arrays.asList(moreSkus), mQueryFinishedListener);
		
		
		
//		List additionalSkuList = new List();
//		additionalSkuList.add(SKU_COINS);
//		mHelper.queryInventoryAsync(true, additionalSkuList,
//		   mQueryFinishedListener);
	}
	
	 /** Verifies the developer payload of a purchase. */
    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();

        /*
         * TODO: verify that the developer payload of the purchase is correct. It will be
         * the same one that you sent when initiating the purchase.
         *
         * WARNING: Locally generating a random string when starting a purchase and
         * verifying it here might seem like a good approach, but this will fail in the
         * case where the user purchases an item on one device and then uses your app on
         * a different device, because on the other device you will not have access to the
         * random string you originally generated.
         *
         * So a good developer payload has these characteristics:
         *
         * 1. If two different users purchase an item, the payload is different between them,
         *    so that one user's purchase can't be replayed to another user.
         *
         * 2. The payload must be such that you can verify it even when the app wasn't the
         *    one who initiated the purchase flow (so that items purchased by the user on
         *    one device work on other devices owned by the user).
         *
         * Using your own server to store and verify developer payloads across app
         * installations is recommended.
         */

        return true;
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (mHelper == null) return;

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        }
        else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }
    
	@Override
	public void onDestroy() {
	   super.onDestroy();
	   if (mHelper != null) mHelper.dispose();
	   mHelper = null;
	}
	
}