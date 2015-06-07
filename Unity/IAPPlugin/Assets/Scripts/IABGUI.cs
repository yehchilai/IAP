using UnityEngine;
using System.Collections;

public class IABGUI : MonoBehaviour {


	IABController iabCtrl = IABController.instance;

	void Start () {

	}

	#if UNITY_ANDROID
	/// <summary>
	/// IAB implementation
	/// </summary>
	void OnGUI(){

		if(GUI.Button(new Rect(10,10,256,256), "INIT")){

			string PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAp75ekSrDG4AWaAR2C2QLm+yWsVJHl3McElBKShAEUuuX7I25dFowHDpO8CFC92n55AgGcZ98/hnuaiEfnmuhbrJiKRppTAoXvNFY4uh45DpSrLDSrIFQGAJ9iJZT9Pb3PK5ruB+B86EfRAUdgROp0bDqDk1TIYoZqch89HdVCjQQ9vYS5ya4n5hRlhEhpv20XQGlR+D8L72NMDha1igZn3GHG9W3EdpZVjpgxjR3NWwR/2GznvLianOAGxyMZd8DbNnBEFwmVl7YYqatTlPb6w1x/2sTGkAJ4peRDxFXLnnf48Fl1j1pXg7+7Q/n1/IRbeJOfWjYpYELOF8LlapJrQIDAQAB";
			iabCtrl.init(PUBLIC_KEY, 
			             delegate (object[] ret){
					if (true ==(bool)ret[0]){
						Debug.Log("### iab successfully initialized");
					}
					else{
						Debug.Log("### failed to initialize iab");
					}
				});

			Debug.Log("### UNITY BUTTON INIT START");
		}

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
	#endif
}
