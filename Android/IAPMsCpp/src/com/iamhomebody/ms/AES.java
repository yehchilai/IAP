package com.iamhomebody.ms;

import android.annotation.SuppressLint;
import java.security.InvalidKeyException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AES {

	private static Cipher mCipher;
	private static final String ALGORITHM = "AES";
	private static final String MY_KEY = "1234567812345678"; // 16 Bytes X 8 bits = 128 bits
	public SecretKey mSecretKey;
	public AES() throws Exception {
		mSecretKey = new SecretKeySpec(MY_KEY.getBytes(), ALGORITHM); // Define the Key
		mCipher = Cipher.getInstance(ALGORITHM);
	}
	
	@SuppressLint("TrulyRandom")
	public byte[] encrypt(String plainText, SecretKey key){
		byte[] plainTextByte = plainText.getBytes();
		byte[] encyptedByte = null;
		try {
			mCipher.init(Cipher.ENCRYPT_MODE, key);  // InvalidKeyException
			encyptedByte = mCipher.doFinal(plainTextByte); // IllegalBlockSizeException, BadPaddingException
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return encyptedByte;
	}
	
	public byte[] decrypt(byte[] encryptedTextByte, SecretKey key){
		byte[] plainTextByte = null;
		try {
			mCipher.init(Cipher.DECRYPT_MODE, key); // InvalidKeyException
			plainTextByte = mCipher.doFinal(encryptedTextByte); // IllegalBlockSizeException, BadPaddingException
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		
		return plainTextByte;
	}
}
