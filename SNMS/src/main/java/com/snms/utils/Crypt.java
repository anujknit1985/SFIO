package com.snms.utils;

import java.security.Key;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Crypt {

	private static final String ALGO = "AES"; // Default uses ECB PKCS5Padding

	public static String encrypt(String Data, String secret) throws Exception {
		Key key = generateKey(secret);
		Cipher c = Cipher.getInstance(ALGO);
		c.init(Cipher.ENCRYPT_MODE, key);
		byte[] encVal = c.doFinal(Data.getBytes());
		String encryptedValue = Base64.getEncoder().encodeToString(encVal);
		return encryptedValue;
	}

	public static String decrypt(String strToDecrypt, String secret) {
		try {
			Key key = generateKey(secret);
			Cipher cipher = Cipher.getInstance(ALGO);
			cipher.init(Cipher.DECRYPT_MODE, key);
			return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
		} catch (Exception e) {
		}
		return null;
	}

	public static String encodeKey() {
		 SecureRandom sr = new SecureRandom();
	        byte[] bSalt = new byte[32];
	        sr.nextBytes(bSalt);
	      //  BASE64Encoder b64encoder = new BASE64Encoder();
	        return Base64.getEncoder().encodeToString(bSalt);
	}
	/*
	 * public static String encodeKey() { SecureRandom sr = new SecureRandom();
	 * byte[] bSalt = new byte[32]; sr.nextBytes(bSalt);
	 * 
	 * BASE64Encoder b64encoder = new BASE64Encoder(); return
	 * b64encoder.encode(bSalt); }
	 */

	private static Key generateKey(String secret) throws Exception {
		byte[] decoded = Base64.getDecoder().decode(secret.getBytes());
		Key key = new SecretKeySpec(decoded, ALGO);
		return key;
	}

	public static String decodeKey(String str) {
		byte[] decoded = Base64.getDecoder().decode(str.getBytes());
		return new String(decoded);
	}

	public static String encodeKey(String str) {
		byte[] encoded = Base64.getEncoder().encode(str.getBytes());
		return new String(encoded);
	}

	/*
	 * public static void main(String a[]) throws Exception {
	 * 
	 * Secret Key must be in the form of 16 byte like,
	 *
	 * private static final byte[] secretKey = new byte[] { ‘m’, ‘u’, ‘s’, ‘t’, ‘b’,
	 * ‘e’, ‘1’, ‘6’, ‘b’, ‘y’, ‘t’,’e’, ‘s’, ‘k’, ‘e’, ‘y’};
	 *
	 * below is the direct 16byte string we can use
	 * 
	 * String secretKey = "mustbe16byteskey"; String encodedBase64Key =
	 * encodeKey(secretKey); System.out.println("EncodedBase64Key =" +
	 * encodedBase64Key); // This // need // to be // share // between // client //
	 * and // server // To check actual key from encoded base 64 secretKey // String
	 * toDecodeBase64Key = decodeKey(encodedBase64Key); String toEncrypt =
	 * "Please encrypt this message!";
	 * 
	 * // AES Encryption based on above secretKey String encrStr =
	 * Crypt.encrypt(toEncrypt, encodedBase64Key);
	 * 
	 * // AES Decryption based on above secretKey String decrStr =
	 * Crypt.decrypt("KLeNFv/QkanM0xDGLESIMQ==", encodedBase64Key);
	 * 
	 * }
	 */
}
