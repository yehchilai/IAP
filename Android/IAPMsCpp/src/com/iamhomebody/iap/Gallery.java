package com.iamhomebody.iap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.net.*;
import android.database.*;

import com.unity3d.player.*;

public class Gallery extends Activity {

	private String TAG_INFO = "### Java : ";
	private int PHOTO_GALLERY = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent, PHOTO_GALLERY);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		//Log.e(TAG_INFO, "START : "+requestCode + " , " + resultCode + " , " + data.toString());
		//Log.e(TAG_INFO, "RESULT_OK : "+ RESULT_OK );
		if( resultCode== RESULT_OK && requestCode==PHOTO_GALLERY && data != null)
		{
			//Log.e(TAG_INFO, "OK");
			Uri uri = data.getData();
			//Log.i(TAG_INFO, "URI");
			String[] fileColumn = {MediaStore.Images.Media.DATA};
			//Log.e(TAG_INFO, "fileColumn");
			Cursor cursor = this.getContentResolver().query(uri, fileColumn, null, null, null);
			//Log.e(TAG_INFO, "Cursor");
			cursor.moveToFirst();
			//Log.e(TAG_INFO, "MoveToFirst");
			int columnIndex = cursor.getColumnIndex(fileColumn[0]);
			//Log.e(TAG_INFO, "columnIndex");
			
			String photoPath = cursor.getString(columnIndex);
										//GameObject name, method name, argument
			//Log.e(TAG_INFO, "GetString");
			
			UnityPlayer.UnitySendMessage("Gallery", "OnPhotoPick", photoPath);
			//Log.d(TAG_INFO, "SendMessage");
			
			Gallery.this.finish();
		}else{
			UnityPlayer.UnitySendMessage("Gallery", "OnPhotoPick", "No PhotoPath");
			Gallery.this.finish();
		}
	}

	
}
