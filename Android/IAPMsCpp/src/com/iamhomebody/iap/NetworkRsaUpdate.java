package com.iamhomebody.iap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

public class NetworkRsaUpdate  extends AsyncTask<String, Void, ResultData>{

	ResultData result = null;
	
	public NetworkRsaUpdate(){
	}
	
	@Override
	protected ResultData doInBackground(String... params) {
		HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonStr = null;
        // If there's no zip code, there's nothing to look up.  Verify size of params.
        if (params.length == 0) {
            return null;
        }
        String ServerURL =  params[0];
        String paramsString = params[1];
        String paramsStringUpdate = params[2];

//        String ServerURL = "http://192.168.58.1:12120/verification";
//        String ServerURL = "https://192.168.58.1:12120/verification";
        String username = "marklai";
        Log.d("Params",paramsString);
        Log.d("Params",paramsStringUpdate);
        
        try{
        	final String USERNAME = "username";
        	final String HASH = "hash";
        	final String NEWHASH = "newHash";
        	Uri builtUri = Uri.parse(ServerURL).buildUpon()//.appendQueryParameter("test", paramsString).build();
        			.appendQueryParameter(USERNAME, username)
        			.appendQueryParameter(HASH, paramsString)
        			.appendQueryParameter(NEWHASH, paramsStringUpdate).build();
        	
        	URL url = new URL(builtUri.toString());
        	Log.d("NET","url: "+builtUri.toString());
        	urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            
            Log.d("NET","inputStream.......");
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
            	Log.d("NET","inputStream == null");
                return null;
            }
            
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
            	Log.d("NET","buffer.length() == 0");
                // Stream was empty.  No point in parsing.
                return null;
            }
            
            jsonStr = buffer.toString();
            Log.d("NET","UPDATE-JSON-String: "+jsonStr);
            getDataFromJson(jsonStr);
        	
        }catch (IOException e){
        	
        }catch (JSONException e){
        	
        }finally{
        	
        }
        
		return result;
	}

	private void getDataFromJson(String jsonStr) throws JSONException {
        try {
            JSONObject resultJson = new JSONObject(jsonStr);
            String message = resultJson.getString("message");
            Boolean status = resultJson.getBoolean("status");
            Log.d("JSON","message: "+message);
            Log.d("JSON","status: "+status.toString());
            result = new ResultData(message, status);
        } catch (JSONException e) {
            Log.e("JSON_ERROR", e.getMessage(), e);
            e.printStackTrace();
        }
    }
}
