package com.iamhomebody.iap;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.vending.billing.IInAppBillingService;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.unity3d.player.*;

public class GoogleIAB extends Activity {
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		buy();
	}

	IInAppBillingService mService;
	ServiceConnection mServiceConn;
	private String mPremiumUpgradePrice;
	private String mGasPrice;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//Log.e("### ","onCreate START");
		//setContentView(R.layout.activity_list_item);
		Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
		//Log.e("### ","serviceIntent START");
		serviceIntent.setPackage("com.android.vending");
		//Log.e("### ","serviceIntent.setPackage START");
		mServiceConn = new ServiceConnection() {
			   @Override
			   public void onServiceDisconnected(ComponentName name) {
			       mService = null;
			       Log.e("### ","mService = null;");
			   }

			   @Override
			   public void onServiceConnected(ComponentName name, 
			      IBinder service) {
			       mService = IInAppBillingService.Stub.asInterface(service);
			       Log.e("### ","mService = IInAppBillingService.Stub.asInterface(service)");
			       //UnityPlayer.UnitySendMessage("OnBuy", "OnBuy", "purchase");
			   }
			};
		bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);
		 
		//Log.e("### ","bindService");
		//buy();
	}
	
	public void buy(){
		Log.e("### ","buy");
		ArrayList<String> skuList = new ArrayList<String> ();
		skuList.add("android.test.purchased");
		skuList.add("gas");
		Bundle querySkus = new Bundle();
		querySkus.putStringArrayList("ITEM_ID_LIST", skuList);
		Log.e("### ","TRY");
		try {
			Log.e("### ","Before skuDetails");
			Bundle skuDetails = mService.getSkuDetails(3, 
					   getPackageName(), "inapp", querySkus);
			Log.e("### ","skuDetails");
			int response = skuDetails.getInt("RESPONSE_CODE");
			Log.e("### ","response");
			if (response == 0) {
			   ArrayList<String> responseList
			      = skuDetails.getStringArrayList("DETAILS_LIST");
			   
			   for (String thisResponse : responseList) {
			      JSONObject object = new JSONObject(thisResponse);
			      String sku = object.getString("productId");
			      String price = object.getString("price");
			      if (sku.equals("premiumUpgrade")) mPremiumUpgradePrice = price;
			      else if (sku.equals("gas")) mGasPrice = price;
			      
			      Bundle buyIntentBundle = mService.getBuyIntent(3, getPackageName(),
						   sku, "inapp", "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ");
			      
			      PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
			      
			      startIntentSenderForResult(pendingIntent.getIntentSender(),
			    		   1001, new Intent(), Integer.valueOf(0), Integer.valueOf(0),
			    		   Integer.valueOf(0));
			   }
			   
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SendIntentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) { 
		Log.e("### ","onActivityResult");
		if (requestCode == 1001) {           
	      int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
	      String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
	      String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");
	      
	      Log.e("### ","responseCode : " + responseCode);
	      Log.e("### ","dataSignature : " + dataSignature);
	      UnityPlayer.UnitySendMessage("OnBuy", "OnBuy", purchaseData);
	      if (resultCode == RESULT_OK) {
	         try {
	            JSONObject jo = new JSONObject(purchaseData);
	            String sku = jo.getString("productId");
	            Log.e("### ","You have bought the " + sku + ". Excellent choice,adventurer!");
	          }
	          catch (JSONException e) {
	        	  Log.i("### ","Failed to parse purchase data.");
	             e.printStackTrace();
	          }
	      }
	   }
	}
	
	@Override
	public void onDestroy() {
	    super.onDestroy();
	    if (mService != null) {
	        unbindService(mServiceConn);
	    }   
	}
	
}
