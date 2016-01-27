using UnityEngine;
using System.Collections;
using System.Collections.Generic;

public class IABController{

	public delegate void callbackEventHandler(object[] resultArray);

	private callbackEventHandler iabSetupCallback;
	private callbackEventHandler iabPurchaseCallback;
	private callbackEventHandler iabConsumeCallback;
	private callbackEventHandler iabInventoryCallback;

	// Message from the jar library
	public string mMessage = "Message from the jar library\n";

	AndroidJavaObject mIabHelperObj;

	public static IABController instance = Nest._instance;

	public static class Nest{

		public static IABController _instance = new IABController(); 
	}

	private IABController(){
		instance = this;
	}

	public void init(string base64EncodedPublicKey, callbackEventHandler tmpIabSetupCBFunc){

		if(instance == null) return;

		instance.iabSetupCallback = tmpIabSetupCBFunc;
		dispose();
		instance.mIabHelperObj = new AndroidJavaObject("com.iamhomebody.iap.IABBinder", new object[2]{base64EncodedPublicKey, "IABBinder"});
	}

	public void dispose ()
	{
		if(instance == null) return;

		if(instance.mIabHelperObj != null){
			instance.mIabHelperObj.Call("dispose");
			instance.mIabHelperObj.Dispose();
			instance.mIabHelperObj = null;
		}
	}

	public void inventoryInfo(string[] skus, callbackEventHandler tmpIabInventoryCBFunc){
		if(instance == null) return;
		
		instance.iabInventoryCallback = tmpIabInventoryCBFunc;
		
		if(instance.mIabHelperObj != null)
			instance.mIabHelperObj.Call("inventoryInfo", new object[1]{skus});
	}

	public void purchase(string SKU, int requestCode, string payload, callbackEventHandler tmpIabPurchaseCBFunc){

		if(instance == null) return;

		instance.iabPurchaseCallback = tmpIabPurchaseCBFunc;

		if(instance.mIabHelperObj != null)
			instance.mIabHelperObj.Call("purchase", new object[3]{SKU, requestCode.ToString(), payload});
	}

	public void consumeInapp(string purchaseJSON, string signature, callbackEventHandler tmpIabConsumeCBFunc){

		if(instance == null) return;

		instance.iabConsumeCallback = tmpIabConsumeCBFunc;

		if(instance.mIabHelperObj != null)
			instance.mIabHelperObj.Call("consume", new object[3]{"inapp", purchaseJSON, signature});
	}

	public void msgReceiver(string message){
		if(instance == null) return;
		mMessage += "\n### Unity nsgReceiver..." + message;
		Debug.Log("### Unity nsgReceiver...");
//		Dictionary<string,object> json = MiniJSON.Json.Deserialize(message) as Dictionary<string,object>;
//		if(json.ContainsKey("code") == true){
//			int value = 0;
//			int.TryParse((string)json["code"], out value);
//			switch(value){
//			case 0:
//				mMessage += "\n### Unity-IABBundle :cannot parse json[code]\n ### " + message;
//
//				Debug.Log("### Unity-IABBundle :cannot parse json[code]");
//
//				break;
//			case 1:
//				codeCase(json, 1, iabSetupCallback);
//				mMessage += "\n### Unity-IABBundle : json = 1, " + json.ToString();
//				Debug.Log("### Unity-IABBundle : json = 1, " + json.ToString());
//
//				break;
//			case 2:
//				codeCase(json, 2, iabPurchaseCallback);
//				mMessage += "\n### Unity-IABBundle : json = 2, : " + json.ToString();
//				Debug.Log("### Unity-IABBundle : json = 2, : " + json.ToString());
//
//				break;
//			case 3:
//				codeCase(json, 3, iabConsumeCallback);
//				mMessage += "\n### Unity-IABBundle : json =3 , : " + json.ToString();
//				
//
//				break;
//			}
//		}
	}

	public void codeCase(Dictionary<string,object> json, int code, callbackEventHandler callback){

		if(json.ContainsKey("ret") == true){
			
			string resultValue = (string)json["ret"];
			
			if(resultValue.Equals("true")){

				if(code == 1){
					if(callback != null) callback(new object[1]{true});
				}else{
					if(callback != null) callback(new object[3]{true, (string)json["desc"], (string)json["sign"]});
				}
				
			}else if(resultValue.Equals("false")){

				if(code == 1){
					if(callback != null) callback(new object[1]{false});
				}else{
					if(callback != null) callback(new object[3]{false,"",""});
				}


			}else{
				Debug.Log("### Unity-IABBundle :cannot parse json[ret], code=" + code.ToString());
			}
		}
	}
}
