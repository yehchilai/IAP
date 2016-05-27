package com.iamhomebody.iap;

import java.io.DataInputStream;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class Rsa {
	
	/**
	   * String to hold name of the encryption algorithm.
	   */
	public static final String ALGORITHM = "RSA";
	
	/**
	 * 
	 */
	public Rsa(){
		
	}
	/**
	 * 
	 * @param plainText
	 * @param publicKey
	 * @return
	 */
	public byte[] encrypt(String plainText, PublicKey publicKey){
		String str = "TEST";
		byte[] cipherText = str.getBytes();
		
	    try {
		    Cipher pkCipher = Cipher.getInstance("RSA/None/PKCS1PADDING");
		    pkCipher.init(Cipher.ENCRYPT_MODE, publicKey);
		    cipherText = pkCipher.doFinal(plainText.getBytes());
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	    return cipherText;
	}
	
	/**
	 * 
	 * @param encryptedText
	 * @param privateKey
	 * @return
	 */
	public String decrypt(byte[] encryptedText, PrivateKey privateKey){
		byte[] dectyptedText = null;
	    try {
	      // get an RSA cipher object and print the provider
	      final Cipher cipher = Cipher.getInstance(ALGORITHM);

	      // decrypt the text using the private key
	      cipher.init(Cipher.DECRYPT_MODE, privateKey);
	      dectyptedText = cipher.doFinal(encryptedText);

	    } catch (Exception ex) {
	      ex.printStackTrace();
	    }

	    return new String(dectyptedText);
	}
	
	/**
	 * 
	 * @param inputStream
	 * @return
	 * @throws Exception
	 */
	public PublicKey getPublicKey(InputStream inputStream)throws Exception {
		DataInputStream dis = new DataInputStream(inputStream);
		byte[] keyBytes = new byte[inputStream.available()];
		dis.readFully(keyBytes);
		dis.close();
		X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
		KeyFactory kf = KeyFactory.getInstance(ALGORITHM);
		return kf.generatePublic(spec);
	}
	
	/**
	 * 
	 * @param inputStream
	 * @return
	 * @throws Exception
	 */
	public PrivateKey getPrivateKey(InputStream inputStream) throws Exception {
		DataInputStream dis = new DataInputStream(inputStream);
		byte[] keyBytes = new byte[inputStream.available()];
		dis.readFully(keyBytes);
		dis.close();
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory kf = KeyFactory.getInstance(ALGORITHM);
	    return kf.generatePrivate(spec);
	}
}
