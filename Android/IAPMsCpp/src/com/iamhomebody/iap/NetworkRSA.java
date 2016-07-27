package com.iamhomebody.iap;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

public class NetworkRSA extends AsyncTask<String, Void, ResultData>{

	ResultData result = null;
	IABBinder mIABBinder;
//	Activity activity;
//	public NetworkRSA(Activity a){
//		activity = a;
//	}
	
	public NetworkRSA(IABBinder a){
		mIABBinder = a;
	}
	
	@Override
	protected ResultData doInBackground(String... params) {
		// TODO Auto-generated method stub
		HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonStr = null;
        // If there's no zip code, there's nothing to look up.  Verify size of params.
        if (params.length == 0) {
            return null;
        }
        String paramsString = params[0];

        String ServerURL = "http://192.168.58.1:12120/verification";
//        String ServerURL = "https://192.168.58.1:12120/verification";
        String username = "marklai";
//        String hash = "RkJnOlLHM010GBcEomRhMcKUrYQ=";
//        String ServerURL = "http://192.168.58.1:5000/encryption";
//        Log.d("Params",paramsString);
        try{
        	final String USERNAME = "username";
        	final String HASH = "hash";
        	Uri builtUri = Uri.parse(ServerURL).buildUpon()//.appendQueryParameter("test", paramsString).build();
        			.appendQueryParameter(USERNAME, username)
        			.appendQueryParameter(HASH, paramsString).build();
        	
        	URL url = new URL(builtUri.toString());
        	Log.d("NET","url: "+builtUri.toString());
        	urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
//        	InputStream inputStream = this.getSSLInputStream(builtUri.toString());
            
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
            Log.d("NET","JSON String: "+jsonStr);
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

    @Override
    protected void onPostExecute(ResultData result) {
//        this.activity.showNetworkRSA(result);
    	this.mIABBinder.showNetworkRSA(result);
    }
    
    @SuppressLint("TrulyRandom")
	private InputStream getSSLInputStream(String urlStr) throws IOException
    {
        URL url = new URL(urlStr);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        try{
        	 // Create the SSL connection
            SSLContext sc;
            sc = SSLContext.getInstance("TLS");
            sc.init(null, null, new java.security.SecureRandom());
            conn.setSSLSocketFactory(sc.getSocketFactory());
        }catch (Exception e){
        	Log.d("JSON","SSL CONNECTION ERROR: "+e.getMessage());
        }
       

        // Use this if you need SSL authentication
//        String userpass = user + ":" + password;
//        String basicAuth = "Basic " + Base64.encodeToString(userpass.getBytes(), Base64.DEFAULT);
//        conn.setRequestProperty("Authorization", basicAuth);

        // set Timeout and method
        conn.setReadTimeout(7000);
        conn.setConnectTimeout(7000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);

        // Add any data you wish to post here

        conn.connect();
        return conn.getInputStream();
    } 
	
}
