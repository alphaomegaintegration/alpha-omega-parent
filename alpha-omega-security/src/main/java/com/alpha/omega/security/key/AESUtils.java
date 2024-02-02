package com.globalpayments.security.key;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class AESUtils {
    static String plainText = "This is a plain text which need to be encrypted by Java AES 256 Algorithm in CBC Mode";

    public static final int GCM_TAG_LENGTH = 16;
    
    private static String GCM_MODE = "AES/GCM/NoPadding";
     /*
        This example shows how to decrypt what was created using this openssl command:

openssl enc -e -aes-256-cbc -in hamlet.xml -out hamlet.enc -pass file:./secret.txt
This example shows how to do this:

openssl enc -d -aes-256-cbc -in hamlet.enc -out hamlet_dec.xml -pass file:./secret.txt

         */

    private static Logger logger = LoggerFactory.getLogger(AESUtils.class);

    public static void main(String[] args) throws Exception {

        /*
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);

        // Generate Key
        SecretKey key = keyGenerator.generateKey();

        // Generating IV.
        byte[] IV = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(IV);

        System.out.println("IV " + IV);

        System.out.println("Original Text  : " + plainText);

        byte[] cipherText = encrypt(plainText.getBytes(), key, IV);
        System.out.println("Encrypted Text : " + Base64.getEncoder().encodeToString(cipherText));

        String decryptedText = new String(decrypt(cipherText, key, IV));
        System.out.println("DeCrypted Text : " + decryptedText);

        */
    }

    /*
    public static byte[] encrypt(byte[] plaintext, SecretKey key) throws Exception{

    }
    public static byte[] decrypt(byte[] cipherText, SecretKey key) throws Exception{

    }
    */


    public static byte[] generateRandomIV(){
        byte[] IV = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(IV);
        return IV;
    }

    public static String encodeBytesToString(byte[] decodedBytes){
        return new String(Base64.getEncoder().encode(decodedBytes));
    }

    public static byte[] decodeBytesFromString(String encodeStr){
        return Base64.getDecoder().decode(encodeStr.getBytes());
    }

	
    public static byte[] encrypt(byte[] plaintext, SecretKey key, byte[] IV) throws Exception {
        //Get Cipher Instance
        Cipher cipher = Cipher.getInstance(GCM_MODE);

        if (null != IV && IV.length > 0){
            
            //Initialize Cipher for ENCRYPT_MODE
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH*8, IV));
        } else {
            cipher.init(Cipher.ENCRYPT_MODE, key);
        }


        //Perform Encryption
        byte[] cipherText = cipher.doFinal(plaintext);

        return cipherText;
    }

    public static byte[] decrypt(byte[] cipherText, SecretKey key, byte[] IV) throws Exception {
        //Get Cipher Instance
        Cipher cipher = Cipher.getInstance(GCM_MODE);

        if (null != IV && IV.length > 0){
            
            //Initialize Cipher for DECRYPT_MODE
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH*8, IV));
        } else {
            cipher.init(Cipher.DECRYPT_MODE, key);
        }
        //Perform Decryption
        byte[] decryptedText = cipher.doFinal(cipherText);

        return decryptedText;
    }

	 
	 


    public static void encryptInputStream(InputStream plaintext, SecretKey key, byte[] IV, OutputStream outputStream) throws Exception {
        //Get Cipher Instance
    	  Cipher cipher = Cipher.getInstance(GCM_MODE);

        if (null != IV && IV.length > 0){
            
            //Initialize Cipher for ENCRYPT_MODE
        	cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH*8, IV));
        } else {
            cipher.init(Cipher.ENCRYPT_MODE, key);
        }

        //Perform Encryption
        CipherOutputStream out = new CipherOutputStream(outputStream, cipher);

        byte[] b = new byte[1024];
        int numberOfBytedRead;
        while ((numberOfBytedRead = plaintext.read(b)) >= 0) {
            out.write(b, 0, numberOfBytedRead);
        }
        plaintext.close();
        outputStream.flush();
        out.close();
    }

    public static void decryptInputStream(InputStream cipherText, SecretKey key, byte[] IV, OutputStream outputStream) throws Exception {
        //Get Cipher Instance
    	Cipher cipher = Cipher.getInstance(GCM_MODE);
    	
        if (null != IV && IV.length > 0){
          
            //Initialize Cipher for DECRYPT_MODE
        	cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH*8, IV));
        } else {
            cipher.init(Cipher.DECRYPT_MODE, key);
        }
        //Perform Decryption
        CipherInputStream cipherInputStream = new CipherInputStream(cipherText, cipher);
        //ByteArrayOutputStream baos = new ByteArrayOutputStream();

        long results = IOUtils.copy(cipherInputStream, outputStream);
        logger.info("Size of file decrypted => {}",results);
    }

	/*
	 * public static byte[] encryptWithIVStr(byte[] plaintext, SecretKey key, String
	 * ivStr) throws Exception { //Get Cipher Instance Cipher cipher =
	 * Cipher.getInstance("AES/CBC/PKCS5Padding");
	 * 
	 * byte[] IV = decodeBytesFromString(ivStr); if (null != IV && IV.length > 0){
	 * //Create IvParameterSpec IvParameterSpec ivSpec = new IvParameterSpec(IV);
	 * 
	 * //Initialize Cipher for ENCRYPT_MODE cipher.init(Cipher.ENCRYPT_MODE, key,
	 * ivSpec); } else { cipher.init(Cipher.ENCRYPT_MODE, key); }
	 * 
	 * 
	 * //Perform Encryption byte[] cipherText = cipher.doFinal(plaintext);
	 * 
	 * return cipherText; }
	 */

	/*
	 * public static byte[] decryptWithIVStr(byte[] cipherText, SecretKey key,
	 * String ivStr) throws Exception { //Get Cipher Instance Cipher cipher =
	 * Cipher.getInstance("AES/CBC/PKCS5Padding");
	 * 
	 * byte[] IV = decodeBytesFromString(ivStr); if (null != IV && IV.length > 0){
	 * //Create IvParameterSpec IvParameterSpec ivSpec = new IvParameterSpec(IV);
	 * 
	 * //Initialize Cipher for DECRYPT_MODE cipher.init(Cipher.DECRYPT_MODE, key,
	 * ivSpec); } else { cipher.init(Cipher.DECRYPT_MODE, key); }
	 * 
	 * 
	 * 
	 * //Perform Decryption byte[] decryptedText = cipher.doFinal(cipherText);
	 * 
	 * return decryptedText; }
	 */
    /*
    https://www.ibm.com/support/knowledgecenter/en/SSWPVP_3.0.1/com.ibm.sklm.doc/overview/cpt/cpt_ic_oview_tech_cryptographic_algorithm.html
     */
    public static final int KEY_BIT_SIZE_256 = 256;
    public static final String AES = "AES";

    public static SecretKey decodeSecretKeyString(String secretKeyStr){

        byte[] decodedSecretKey = Base64.getDecoder().decode(secretKeyStr);
        SecretKey secretKey = new SecretKeySpec(decodedSecretKey, AES);
        return secretKey;
    }

    public static String encodeSecretKey(SecretKey secretKey) {
        String secretKeyStr = Base64.getEncoder().encodeToString(secretKey.getEncoded());
        return secretKeyStr;
    }

    public static SecretKey generateSecretKey(String algorithm, int keyBitSize) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm);
        SecureRandom secureRandom = new SecureRandom();
        keyGenerator.init(keyBitSize, secureRandom);
        SecretKey secretKey = keyGenerator.generateKey();
        return secretKey;
    }



}
