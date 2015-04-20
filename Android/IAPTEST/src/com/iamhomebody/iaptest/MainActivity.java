package com.iamhomebody.iaptest;

import java.util.ArrayList;

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

	IInAppBillingService mService;
	ServiceConnection mServiceConn = new ServiceConnection() {
		   @Override
		   public void onServiceDisconnected(ComponentName name) {
		       mService = null;
		   }

		   @Override
		   public void onServiceConnected(ComponentName name, 
		      IBinder service) {
		       mService = IInAppBillingService.Stub.asInterface(service);
		   }
		};
		
	private String mPremiumUpgradePrice;
	private String mGasPrice;
	private String inappid = "android.test.purchased";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
		//Log.e("### ","serviceIntent START");
		serviceIntent.setPackage("com.android.vending");
		//Log.e("### ","serviceIntent.setPackage START");
		bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);
		//Log.e("### ","bindService");
		
		Button purchase = (Button) findViewById(R.id.purchase);
		
		purchase.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				buy();
			}
			
		});
	}
	
	private void buy(){
		ArrayList<String> skuList = new ArrayList<String> ();
		skuList.add(inappid);
		skuList.add("gas");
		Bundle querySkus = new Bundle();
		querySkus.putStringArrayList("ITEM_ID_LIST", skuList);
		Log.e("### ","TRY");
		try {
			Bundle skuDetails = mService.getSkuDetails(3, 
					   getPackageName(), "inapp", querySkus);
			
			int response = skuDetails.getInt("RESPONSE_CODE");
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
//	      UnityPlayer.UnitySendMessage("OnBuy", "OnBuy", purchaseData);
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
