package com.snms.utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class SaltGenerator {

	/** String object for generated salt **/
	private String salt;

	/**
	 * Method to generate Random Salt for password hashing
	 **/
	public void generateSalt() {
		/*
		 * SecureRandom sr = new SecureRandom(); byte[] bSalt = new byte[16];
		 * sr.nextBytes(bSalt);
		 */
		String secret = "mustbe16byteskey";
		byte[] encoded = Base64.getEncoder().encode(secret.getBytes());
		this.salt = new String(encoded);
	}

	public byte[] genSalt() throws NoSuchAlgorithmException {
		SecureRandom sr = new SecureRandom();
		byte[] salt = new byte[16];
		sr.nextBytes(salt);
		return salt;
	}

	
	
	/* *//**
			 * Method to get Random Salt for password hashing
			 * 
			 * @return String object of the Salt
			 **//*
			 * public String getSalt() { return salt; }
			 */

}
