package com.iamhomebody.ms;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;

public class HSA {
private String mXmlPath;
	
	public HSA(String XmlPath){
		mXmlPath = XmlPath;
	}
	
	public String calculateHSA(){
		String digest = "no result";
		try {
			MessageDigest md = MessageDigest.getInstance("SHA");
			digest = getDigest(new FileInputStream(new File(mXmlPath)), md, 2048);
//			System.out.println("SHA Digest:" + digest);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return digest;
	}
	
	private String getDigest(InputStream is, MessageDigest md, int byteArraySize){
		md.reset();
		byte[] bytes = new byte[byteArraySize];
		int numBytes;
		try {
			while ((numBytes = is.read(bytes)) != -1) {
				md.update(bytes, 0, numBytes);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] digest = md.digest();
//		String result = new String(Hex.encodeHex(digest));
//		return result;
		return new String(Base64.encodeBase64(digest));
//		return new String(digest);
	}
}
