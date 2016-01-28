using UnityEngine;
using System.Collections;
using System.Collections.Generic;

public class IABGUI : MonoBehaviour {


	IABController iabCtrl = IABController.instance;

	public GUIStyle myStyle;
	public Vector2 scrollPosition = Vector2.zero;
	private float scrollerValueV;
	private float scrollerValueH;
	public GUIStyle scrollerV;
	public GUIStyle scrollerH;


	void Start () {
		string PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmqNHL8jWtWPShIQVQEWVfj4MBejOpqT5yB6+g3u7uM99roiOISXmhS6Kaxfd6I2nWl05bSqCxzJxVlXOb/6QVHiBlbHvZkT3zmLRKD8k2sEqgRJJM/12GmdMXH/aoz4vcdq6Wp5KpOh3HgoPkE7eDlddKsXPuodOQOVcXh0GN2RQWRhC/sIE8hnED6m3fFLVHeJlBpSe8y3uGGpxrrlH6hcP4e66aMsMPn4zzPeEQA+Ir911oq0uB7n9O9mh1uPYcQMPfxktON6/cNX9UpEucKDxHpkmmaqwZuyGhZ+a4zBZXPFrUi1q+FhUn8KTpOkHgC/EEF52l0j2pZtHtuLNpwIDAQAB";
		iabCtrl.init(PUBLIC_KEY, delegate (object[] ret){
			if (true ==(bool)ret[0]){
				Debug.Log("### iab successfully initialized");
			}
			else{
				Debug.Log("### failed to initialize iab");
			}
		});

//		iabCtrl.queryInventory(new string[]{"product_1_coin", "produt_2_coin", "coin"});
//		iabCtrl.inventoryInfo(new string[]{"product_1_coin", "produt_2_coin", "coin"}, delegate(object[] resultArray) {
//		});
	}

	#if UNITY_ANDROID
	/// <summary>
	/// IAB implementation
	/// </summary>
	void OnGUI(){

		//scrollerValueV = GUI.VerticalScrollbar(new Rect(300, 10, 300, 300), scrollerValueV, 3.0f, 0.0f, 10.0f);
		//scrollerValueH = GUI.HorizontalScrollbar(new Rect(10, 300, 300, 300), scrollerValueH, 3.0f, 0.0f, 10.0f);


		scrollPosition = GUI.BeginScrollView(new Rect(300, 10, 300, 300), scrollPosition, new Rect(0, 0, 1000, 2000));
		GUILayout.Label(iabCtrl.mMessage);
		//GUI.Label(new Rect(0, 0, 290, 2000), innerText + iabCtrl.mMessage);
		//GUI.TextField(new Rect(0, 0, 220, 200), innerText + iabCtrl.mMessage);
		//GUI.TextArea(new Rect(0, 0, 220, 200), innerText + iabCtrl.mMessage);

		GUI.EndScrollView();

		// Initial Button
		if(GUI.Button(new Rect(10,10,256,256), "INIT")){
//			iabCtrl.queryInventory();
//			string PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmqNHL8jWtWPShIQVQEWVfj4MBejOpqT5yB6+g3u7uM99roiOISXmhS6Kaxfd6I2nWl05bSqCxzJxVlXOb/6QVHiBlbHvZkT3zmLRKD8k2sEqgRJJM/12GmdMXH/aoz4vcdq6Wp5KpOh3HgoPkE7eDlddKsXPuodOQOVcXh0GN2RQWRhC/sIE8hnED6m3fFLVHeJlBpSe8y3uGGpxrrlH6hcP4e66aMsMPn4zzPeEQA+Ir911oq0uB7n9O9mh1uPYcQMPfxktON6/cNX9UpEucKDxHpkmmaqwZuyGhZ+a4zBZXPFrUi1q+FhUn8KTpOkHgC/EEF52l0j2pZtHtuLNpwIDAQAB";
//				iabCtrl.init(PUBLIC_KEY, delegate (object[] ret){
//					if (true ==(bool)ret[0]){
//						Debug.Log("### iab successfully initialized");
//					}
//					else{
//						Debug.Log("### failed to initialize iab");
//					}
//				});
//
//			Debug.Log("### UNITY BUTTON INIT START");
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
			string SKU = "coin";
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
