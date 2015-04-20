using UnityEngine;
using System.Collections;

public class IABGUI : MonoBehaviour {

	AndroidJavaClass ajc;
	AndroidJavaObject ajo;
	AndroidJavaObject reference;

	void Start () {

		ajc = new AndroidJavaClass("com.unity3d.player.UnityPlayer");
		ajo = new AndroidJavaObject("com.iamhomebody.iap.IAPBinder");
		reference = new AndroidJavaObject("com.iamhomebody.iap.GoogleIAB");

	}

	#if UNITY_ANDROID
	void OnGUI(){
		/****
		 * IAB implementation
		 *
		 */

		if(GUI.Button(new Rect(10,350,256,256), "INIT")){

			ajo.CallStatic("Test");
			ajo.CallStatic("runGoogleIAB", ajc.GetStatic<AndroidJavaObject>("currentActivity"));
			Debug.Log("### UNITY BUTTON INIT START");
//			reference = ajc.GetStatic<AndroidJavaObject>("currentActivity");
//			Debug.Log("### UNITY BEFORE BUY");
//			reference.Call<AndroidJavaObject>("buy");
//			Debug.Log("### UNITY AFTER BUY");
		}

		if(GUI.Button(new Rect(350,350,256,256), "BUY")){

//			ajo.CallStatic("buyGoogleIAB");
			reference = ajc.GetStatic<AndroidJavaObject>("currentActivity");
			reference.Call<AndroidJavaObject>("buy");

		}


	}
	
	public void OnBuy(string afterBuy){
		Debug.Log("### Finish Buy : " + afterBuy);

		reference = ajc.GetStatic<AndroidJavaObject>("currentActivity");
		Debug.Log("### UNITY BEFORE BUY");
		reference.Call("buy");
		Debug.Log("### UNITY AFTER BUY");
	}
	#endif
}
