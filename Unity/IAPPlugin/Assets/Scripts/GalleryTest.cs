using UnityEngine;
using System.Collections;

public class GalleryTest : MonoBehaviour {


#if UNITY_ANDROID	
	Texture2D image;
	bool isImageLoaded = false;

	WWW www;

	void OnGUI(){


		/***
		 * Gallery Sample
		 *
		 */
		if(isImageLoaded){

			GUI.DrawTexture(new Rect(0,0,Screen.width, Screen.height), image);

		}

		if(GUI.Button(new Rect(10,10,256,256), "OPEN")){
			//Debug.Log("##### Button");
			AndroidJavaClass ajc = new AndroidJavaClass("com.unity3d.player.UnityPlayer");
			//Debug.Log("##### ajc");
			AndroidJavaObject ajo = new AndroidJavaObject("com.iamhomebody.iap.IAPBinder");
			//Debug.Log("##### ajo");
			ajo.CallStatic("openGallary", ajc.GetStatic<AndroidJavaObject>("currentActivity"));
			//Debug.Log("##### openGallary");
		}

		if(www != null && www.isDone){
			image = new Texture2D(www.texture.width, www.texture.height);
			image.SetPixels32(www.texture.GetPixels32());
			image.Apply();
			www = null;
			isImageLoaded = true;
		}

	}

	public void OnPhotoPick(string findPath){
		Debug.Log(findPath);

		www = new WWW("file://" + findPath);
	}
#endif
}
