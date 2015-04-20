using UnityEngine;
using System.Collections;

public class Buttons : MonoBehaviour {

	// Use this for initialization
	void Start () {
	
	}
	
	// Update is called once per frame
	void Update () {
	
	}

#if UNITY_ANDROID
	void OnGUI()
	{
//		if(GUI.Button(new Rect(20,40,200,20), "show AlertDialog")) {
//			// 新增一個 AndroidJavaClass, 屬性預設就為 com.unity3d.player.UnityPlayer 即可
//			using (AndroidJavaClass jc = new AndroidJavaClass("com.unity3d.player.UnityPlayer")) {
//				// 得到目前的 Activity
//				AndroidJavaObject jo = jc.GetStatic<AndroidJavaObject>("MainActivity");
//				// 呼叫剛剛我們自己寫的 showAlertDialog, 並帶入參數
//				jo.Call("showAlertDialog", "Title", "Message");
//			}
//			print("click1");
//		}
	}
#endif
}
