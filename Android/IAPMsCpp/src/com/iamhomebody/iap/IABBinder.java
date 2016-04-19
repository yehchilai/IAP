package com.iamhomebody.iap;

import java.io.File;
import java.util.Arrays;

import com.iamhomebody.ms.*;
import com.iamhomebody.iap.util.*;
import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;
//import org.json.simple.JSONObject;

import com.iamhomebody.iap.IABActivity;

import org.apache.commons.codec.binary.Base64;

public class IABBinder {
	
	static final String TAG = "msgReceiver";
	private Activity mActivity;
	private IabHelper mIabHelper;
	private String mEventHandler;
	private Inventory myInventory;
	private String[] skus = {"product_1_coin", "produt_2_coin", "coin", "coins"};
	private int mAmount = 0;
	
	// Data Store
	SharedPreferences mSharedPreferences;
//	SharedPreferences mSharedPreferencesKey;
	private static final String PREFS_NAME = "com.iamhomebody.iap";
//	private static final String PREFS_NAME_KEY = "com.iamhomebody.iapKey";
	private static final String FILE_KEY = "securityInfo";
	private static final String TMP_KEY = "key";
	
	// Constructor and initialize the IAB functionality
	@SuppressLint("CommitPrefEdits")
	public IABBinder(String base64EncodedPublicKey, String strEventHandler){
		mActivity = UnityPlayer.currentActivity;
		mEventHandler = strEventHandler;
		// Get sharedPreferences instance
		mSharedPreferences = mActivity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		String info = mSharedPreferences.getString(FILE_KEY, "NO_KEY");
		if(info.equals("NO_KEY")){
			try {
				// Put the Tmp key
				if(putString(FILE_KEY, TMP_KEY)){
					UnityPlayer.UnitySendMessage(mEventHandler, TAG, "## FirstCheckFile-encryptedText: Tmp key successful");
				}else{
					UnityPlayer.UnitySendMessage(mEventHandler, TAG, "## FirstCheckFile-commit: commit fail.");
				}
				
				HSA hsa = new HSA("/data/data/com.iamhomebody.MsProject/shared_prefs/" + PREFS_NAME + ".xml");
				String str = hsa.calculateHSA();
				UnityPlayer.UnitySendMessage(mEventHandler, TAG, "## FirstCheckFile-hsa: " + str);
				AES aes = new AES();
				byte[] encryptedTextByte = aes.encrypt(str, aes.mSecretKey);
				String encryptedText = new String(Base64.encodeBase64(encryptedTextByte));
				
				// Put the key
				if(putString(FILE_KEY, encryptedText)){
					UnityPlayer.UnitySendMessage(mEventHandler, TAG, "## FirstCheckFile-encryptedText: " + encryptedText);
				}else{
					UnityPlayer.UnitySendMessage(mEventHandler, TAG, "## FirstCheckFile-commit: commit fail.");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			UnityPlayer.UnitySendMessage(mEventHandler, TAG, "## FirstCheckFile: Has File.");
		}
		
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
				
				// get inventory - check the consumable items
//				mIabHelper.queryInventoryAsync(mGotInventoryListener);

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
	    	
	    	UnityPlayer.UnitySendMessage(mEventHandler, TAG, "JAVAInventory : " + result.getMessage());
	    	myInventory = inventory;
	    	if (result.isFailure()){
	    		UnityPlayer.UnitySendMessage(mEventHandler, TAG, "JAVAInventory initialize fail : ");
	    	}else{
	    		
	    		if (inventory.hasPurchase("product_1_coin")) {
		        	
		        	mIabHelper.consumeAsync(inventory.getPurchase("product_1_coin"), mConsumeFinishedListener);
		        }else if (inventory.hasPurchase("produt_2_coin")) {
		        	
		        	mIabHelper.consumeAsync(inventory.getPurchase("produt_2_coin"), mConsumeFinishedListener);
		        }else if (inventory.hasPurchase("coin")) {
		        	
		        	mIabHelper.consumeAsync(inventory.getPurchase("coin"), mConsumeFinishedListener);
		        }else if (inventory.hasPurchase("coins")) {
		        	
		        	mIabHelper.consumeAsync(inventory.getPurchase("coins"), mConsumeFinishedListener);
		        }
	    	}
	    }
	};
	
	public void dispose() {
		// TODO Auto-generated method stub
		if(mIabHelper != null){
			mIabHelper.dispose();
		}
		
		mIabHelper = null;
	}
	
	// query inventory
	public void queryInventory(final String[] skus){
		UnityPlayer.UnitySendMessage(mEventHandler, TAG, "## Query Inventory !!! ");
		UnityPlayer.currentActivity.runOnUiThread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				mIabHelper.queryInventoryAsync(true, Arrays.asList(skus), new IabHelper.QueryInventoryFinishedListener() {
					
					@Override
					public void onQueryInventoryFinished(IabResult result, Inventory inv) {
						// TODO Auto-generated method stub
						UnityPlayer.UnitySendMessage(mEventHandler, TAG, "JAVAInventory : " + result.getMessage());
				    	myInventory = inv;
					}
				});
			}
			
		});
	}
	
	// Get inventory information
	public void inventoryInfo(String[] skus){
		UnityPlayer.UnitySendMessage(mEventHandler, TAG, "Inventory Request !!!");
		
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
	
	// Consume product from the inventory information
		public void consumeProduct(final String[] skus){
			UnityPlayer.UnitySendMessage(mEventHandler, TAG, "Consume product form the Inventory Request !!!");
			
			if(myInventory != null){
				UnityPlayer.currentActivity.runOnUiThread(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						for(String sku:skus){
							if (myInventory.hasPurchase(sku)) {
					        	
					        	mIabHelper.consumeAsync(myInventory.getPurchase(sku), mConsumeFinishedListener);
					        	UnityPlayer.UnitySendMessage(mEventHandler, TAG, "## consumeProduct: " + sku);
					        }else{
					        	UnityPlayer.UnitySendMessage(mEventHandler, TAG, "## consumeProduct-Do not Purchase: " + sku);
					        }
//							SkuDetails detail = myInventory.getSkuDetails(sku);
//							if(detail != null){
//								UnityPlayer.UnitySendMessage(mEventHandler, TAG, 
//										"Product: " + detail.getTitle() +
//										"\nPrice: " + detail.getPrice() +
//										"\nDescription" + detail.getDescription() +"\n");
//							}else{
//								UnityPlayer.UnitySendMessage(mEventHandler, TAG, "## Sku: " + sku + " does not exist!");
//							}
						}
						
					}
					
				});
				
			}else{
				UnityPlayer.UnitySendMessage(mEventHandler, TAG, " ### myInventory == null ###");
			}
		}
	
	
	// Purchase products
	public void purchase(String SKU,int amount, String requestCode, String payload){
		mAmount = amount;
		int code = Integer.parseInt(requestCode);
		if(mIabHelper != null && checkFile()){
			mIabHelper.launchPurchaseFlow(mActivity, SKU, code, mPurchaseFinishedListener, payload);
		}else{
			if(!checkFile()){
				UnityPlayer.UnitySendMessage(mEventHandler, TAG, "## Purchase Process: File Data has been compromised");
			}
		}
	}
	
	IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
		
		@Override
		public void onIabPurchaseFinished(IabResult result, Purchase info) {
			// TODO Auto-generated method stub
			if(result.isFailure()){
				// sent a message to Unity gameObject with JSON format
//				UnityPlayer.UnitySendMessage(mEventHandler, TAG, "{\"code\":\"2\",\"ret\":\"false\",\"desc\":\"\",\"sign\":\"\"}");
				UnityPlayer.UnitySendMessage(mEventHandler, TAG, "## JAVA onIabPurchaseFinished: result.isFailure() = TURE");
				return;
			}
				
			if(result.isSuccess()){
				boolean resultFlag = false;
				String resultJSON = "";
				String resultSignature = "";
				
				if(info != null){
					resultJSON = info.getOriginalJson().replace('\"', '\'');
					resultSignature = info.getSignature();
//					UnityPlayer.UnitySendMessage(mEventHandler, TAG, "## Purchase Process-getSku: " + info.getSku());
//					UnityPlayer.UnitySendMessage(mEventHandler, TAG, "## Purchase Process-getPackageName: " + info.getPackageName());
//					UnityPlayer.UnitySendMessage(mEventHandler, TAG, "## Purchase Process-getPurchaseState: " + info.getPurchaseState());
					
					
//					UnityPlayer.UnitySendMessage(mEventHandler, TAG, "{\"code\":\"2\",\"ret\":\""+resultFlag+"\",\"desc\":\""+resultJSON+"\",\"sign\":\""+resultSignature+"\"}");
					setData(info.getSku(), mAmount);
					mAmount = 0;
					UnityPlayer.UnitySendMessage(mEventHandler, TAG, "## JAVA Purchase-SetData: Finish SetData");
				}
				
				
				
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
				//UnityPlayer.UnitySendMessage(mEventHandler, TAG, "{\"code\":\"3\",\"ret\":\"true\",\"desc\":\""+purchase.getOriginalJson().replace('\"', '\'')+"\",\"sign\":\""+purchase.getSignature()+"\"}");
				UnityPlayer.UnitySendMessage(mEventHandler, TAG, "### JAVA Consume is Success!");
			}else{
				//UnityPlayer.UnitySendMessage(mEventHandler, TAG, "{\"code\":\"3\",\"ret\":\"false\",\"desc\":\"\",\"sign\":\"\"}");
				UnityPlayer.UnitySendMessage(mEventHandler, TAG, "### JAVA Consume is not Success!");
			}
		}
	};
	
	// consume purchased and saved product
	public boolean consumeLoacalProduct(String sku, int value){
		if(!checkFile()){
			UnityPlayer.UnitySendMessage(mEventHandler, TAG, "## consumeLoacalProduct: The data is compromised!");
			return false;
		}
		int tmp = mSharedPreferences.getInt(sku, Integer.MIN_VALUE);
		if(tmp == Integer.MIN_VALUE){
			UnityPlayer.UnitySendMessage(mEventHandler, TAG, "## consumeLoacalProduct: The is no purchased product!");
			return false;
		}else if(value > tmp){
			UnityPlayer.UnitySendMessage(mEventHandler, TAG, "## consumeLoacalProduct: The product amount is not enough!");
			return false;
		}else{
			// Get editor instance to put values in the XML file
			SharedPreferences.Editor editor  = mSharedPreferences.edit();
			// Put the calue in the XML file by using Key = PRODUCT_KEY, Value = 1
			int tmpInt = mSharedPreferences.getInt(sku, Integer.MIN_VALUE);
			editor.putInt(sku, tmp - value);
			// Commit the put value
			boolean isSaved = editor.commit();
			// Show if the data is saved successfully
			if(isSaved && putString(FILE_KEY, TMP_KEY)){
				UnityPlayer.UnitySendMessage(mEventHandler, TAG, "## consumeLoacalProduct: " + sku + String.valueOf(tmp - value) +" , "+String.valueOf(value));
			}else{
				UnityPlayer.UnitySendMessage(mEventHandler, TAG, "## consumeLoacalProduct: " + sku + " DOES NOT CONSUMED.");
			}
			
			// update hsa
			HSA hsa = new HSA("/data/data/com.iamhomebody.MsProject/shared_prefs/" + PREFS_NAME + ".xml");
			String current = hsa.calculateHSA();
			UnityPlayer.UnitySendMessage(mEventHandler, TAG, "## setData-update hsa: " + current);
			try {
				AES aes = new AES();
				byte[] encryptedTextByte = aes.encrypt(current, aes.mSecretKey);
				String encryptedText = new String(Base64.encodeBase64(encryptedTextByte));
				
				UnityPlayer.UnitySendMessage(mEventHandler, TAG, "## setData-encryptedText: " + encryptedText);
				// write the key back
				if(!putString(FILE_KEY, encryptedText)){
					UnityPlayer.UnitySendMessage(mEventHandler, TAG, "## setData-encryptedText: Fail to write the key");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		return true;
	}
	
	private void setData(String productKey, int value){
		
		// Get editor instance to put values in the XML file
		SharedPreferences.Editor editor  = mSharedPreferences.edit();
		// Put the calue in the XML file by using Key = PRODUCT_KEY, Value = 1
		int tmp = mSharedPreferences.getInt(productKey, Integer.MIN_VALUE);
		if( tmp == Integer.MIN_VALUE){
			editor.putInt(productKey, value);
		}else{
			editor.putInt(productKey, tmp + value);
		}
		// Commit the put value
		boolean isSaved = editor.commit();
		// Show if the data is saved successfully
		if(isSaved && putString(FILE_KEY, TMP_KEY)){
			UnityPlayer.UnitySendMessage(mEventHandler, TAG, "## Set Data: " + productKey + String.valueOf(value) +" , "+String.valueOf(tmp));
		}else{
			UnityPlayer.UnitySendMessage(mEventHandler, TAG, "## Set Data: " + productKey + " DOES NOT BE SEAVED.");
		}
		
		// update hsa
		HSA hsa = new HSA("/data/data/com.iamhomebody.MsProject/shared_prefs/" + PREFS_NAME + ".xml");
		String current = hsa.calculateHSA();
		UnityPlayer.UnitySendMessage(mEventHandler, TAG, "## setData-update hsa: " + current);
		try {
			AES aes = new AES();
			byte[] encryptedTextByte = aes.encrypt(current, aes.mSecretKey);
			String encryptedText = new String(Base64.encodeBase64(encryptedTextByte));
			
			UnityPlayer.UnitySendMessage(mEventHandler, TAG, "## setData-encryptedText: " + encryptedText);
			// write the key back
			if(!putString(FILE_KEY, encryptedText)){
				UnityPlayer.UnitySendMessage(mEventHandler, TAG, "## setData-encryptedText: Fail to write the key");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		// Get the XML file Path
		File file = new File(mActivity.getFilesDir().getParent(), "shared_prefs");
		
		if (file.isDirectory()) {
			String[] names = file.list();
			for(int i = 0; i < names.length ; i++){
				UnityPlayer.UnitySendMessage(mEventHandler, TAG, "## Data Path: " + file.getPath() + "Child Path : " + names[i]);
			}
		}
	}
	
	// Check whether the storage file is compromised
//	@SuppressLint("SdCardPath")
	private boolean checkFile(){
		String current;
		String tmp = mSharedPreferences.getString(FILE_KEY, "NO_FILE");
		boolean isEqual = false;
		// put tmp key
		if(putString(FILE_KEY, TMP_KEY)){
			HSA hsa = new HSA("/data/data/com.iamhomebody.MsProject/shared_prefs/" + PREFS_NAME + ".xml");
			current = hsa.calculateHSA();
			UnityPlayer.UnitySendMessage(mEventHandler, TAG, "## checkFile-current: " + current);
			try {
				AES aes = new AES();
				UnityPlayer.UnitySendMessage(mEventHandler, TAG, "## checkFile-info: " + tmp);
				if(!tmp.equals("NO_FILE")){
					byte[] decryptedTextByte = aes.decrypt(Base64.decodeBase64(tmp.getBytes()), aes.mSecretKey);
//					UnityPlayer.UnitySendMessage(mEventHandler, TAG, "## checkFile-decryptedTextByte: ...");
					String decryptedText = new String(decryptedTextByte);
					UnityPlayer.UnitySendMessage(mEventHandler, TAG, "## checkFile-decryptedText: " + decryptedText);
					isEqual = current.equals(decryptedText);
					// write the key back
					if(!putString(FILE_KEY, tmp)){
						UnityPlayer.UnitySendMessage(mEventHandler, TAG, "## checkFile: Fail to write the key");
					}
				}else{
					UnityPlayer.UnitySendMessage(mEventHandler, TAG, "## checkFile-info: The app is compromised- " + tmp);
					Toast.makeText(mActivity, "The data is compromised!", Toast.LENGTH_LONG).show();
					return false;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		
		return isEqual;
	}
	
	// put string data to the file
	private boolean putString(String str, String str2){
		// Get editor instance to put values in the XML file
		SharedPreferences.Editor editor  = mSharedPreferences.edit();
		// Put the Tmp key
		editor.putString(str, str2);
		return editor.commit();
	}
	
	// get value from the file
	public int getValue(String sku){
		if(checkFile()){
			int tmp = mSharedPreferences.getInt(sku, Integer.MIN_VALUE);
			if(tmp != Integer.MIN_VALUE){
				return tmp;
			}else{
				return Integer.MIN_VALUE;
			}
		}else{
			Toast.makeText(mActivity, "The data is compromised!", Toast.LENGTH_LONG).show();
			return Integer.MIN_VALUE;
		}
		
	}
}
