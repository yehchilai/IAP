using UnityEngine;
using System.Collections;
using System.Collections.Generic;

public class IABGUI : MonoBehaviour {


	IABController iabCtrl = IABController.instance;

	public GUIStyle myStyle;
	public Vector2 scrollPosition = Vector2.zero;
	//private float scrollerValue;

	void Start () {

	}

	#if UNITY_ANDROID
	/// <summary>
	/// IAB implementation
	/// </summary>
	void OnGUI(){

		//scrollerValue = GUI.VerticalScrollbar(new Rect(300, 10, 300, 300), scrollerValue, 1.0f, 0.0f, 10.0f);
		scrollPosition = GUI.BeginScrollView(new Rect(300, 10, 300, 300), scrollPosition, new Rect(0, 0, 1000, 2000));
		GUILayout.Label(iabCtrl.mMessage);
		//GUI.Label(new Rect(0, 0, 290, 2000), innerText + iabCtrl.mMessage);
		//GUI.TextField(new Rect(0, 0, 220, 200), innerText + iabCtrl.mMessage);
		//GUI.TextArea(new Rect(0, 0, 220, 200), innerText + iabCtrl.mMessage);

		GUI.EndScrollView();

		// Initial Button
		if(GUI.Button(new Rect(10,10,256,256), "INIT")){

			string PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAp75ekSrDG4AWaAR2C2QLm+yWsVJHl3McElBKShAEUuuX7I25dFowHDpO8CFC92n55AgGcZ98/hnuaiEfnmuhbrJiKRppTAoXvNFY4uh45DpSrLDSrIFQGAJ9iJZT9Pb3PK5ruB+B86EfRAUdgROp0bDqDk1TIYoZqch89HdVCjQQ9vYS5ya4n5hRlhEhpv20XQGlR+D8L72NMDha1igZn3GHG9W3EdpZVjpgxjR3NWwR/2GznvLianOAGxyMZd8DbNnBEFwmVl7YYqatTlPb6w1x/2sTGkAJ4peRDxFXLnnf48Fl1j1pXg7+7Q/n1/IRbeJOfWjYpYELOF8LlapJrQIDAQAB";
			iabCtrl.init(PUBLIC_KEY, delegate (object[] ret){
					if (true ==(bool)ret[0]){
						Debug.Log("### iab successfully initialized");
					}
					else{
						Debug.Log("### failed to initialize iab");
					}
				});

			Debug.Log("### UNITY BUTTON INIT START");
		}

		// Inventory Button
		if(GUI.Button(new Rect(600,10,256,256), "Inventory Info")){
			string[] skus = {"android.test.purchased", "product_1_coin", "produt_2_coin", "coin"};
			iabCtrl.inventoryInfo(skus, delegate(object[] resultArray) {
				if (false ==(bool)resultArray[0]){
					Debug.Log("### inventory cancelled");
				}else if (true ==(bool)resultArray[0]){
					Debug.Log("### inventory successful");
				}
				else{
					Debug.Log("### inventory error ?");
				}
			});
			
		}

		// Purchase Button
		if(GUI.Button(new Rect(350,350,256,256), "BUY")){
			string SKU = "android.test.purchased";
			string payload = "";
			iabCtrl.purchase(SKU, 1001, payload, delegate(object[] resultArray) {
				if (false ==(bool)resultArray[0]){
					Debug.Log("### purchase cancelled");
				}else if (true ==(bool)resultArray[0]){
					Debug.Log("### purchase successful");
				}
				else{
					Debug.Log("### purchase error ?");
//					string purchaseinfo =(string)resultArray[1];
//					string signature =(string)resultArray[2];
//					iabCtrl.consumeInapp(purchaseinfo, signature, 
//					                         delegate(object[] ret2){
//						if (false ==(bool)ret2[0])
//						{
//							Debug.Log("### failed to consume product");
//						}
//					});
				}
			});

		}

		// Consume Button
		if(GUI.Button(new Rect(10,300,256,256), "CONSUME")){

			string purchase = "{\"packageName\":\"com.iamhomebody.iaptest\","+
				"\"orderId\":\"transactionId.android.test.purchased\","+
					"\"productId\":\"android.test.purchased\",\"developerPayload\":\"\",\"purchaseTime\":0,"+
					"\"purchaseState\":0,\"purchaseToken\":\"inapp:com.iamhomebody.iaptest :android.test.purchased\"}";
			iabCtrl.consumeInapp(purchase, "", 
			                     delegate(object[] ret2){
				if (false ==(bool)ret2[0])
				{
					Debug.Log("### failed to consume product");
				}else if(true == (bool)ret2[0]){
					Debug.Log("### Consumption successful");
				}
			});
		}
	}

	void OnApplicationQuit(){
		iabCtrl.dispose();
	}

	public void msgReceiver(string message){
		iabCtrl.msgReceiver(message);
	}
	

	#endif
}
