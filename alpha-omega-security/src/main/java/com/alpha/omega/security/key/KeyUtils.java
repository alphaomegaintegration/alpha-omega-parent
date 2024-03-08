package com.alpha.omega.security.key;


import com.alpha.omega.security.utils.AOSecurityConstants;
import org.apache.commons.text.RandomStringGenerator;
import org.apache.commons.text.TextRandomProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static java.nio.charset.StandardCharsets.UTF_8;

public class KeyUtils {
	static final Logger logger = LoggerFactory.getLogger(KeyUtils.class);
	public final static String ALGORITHM = "RSA";
	public static final int DEFAULT_RSA_SIZE = 1024;

	public static PublicKey extractPublicKey(Map<String, Object> jwtKeyMap) throws InvalidPublicKeyException {

		String keyType = (String)jwtKeyMap.get("kty");

		if (!ALGORITHM.equalsIgnoreCase(keyType)) {
			return null;
		}

		try {
			KeyFactory kf = KeyFactory.getInstance(ALGORITHM);
			String nString = (String)jwtKeyMap.get("n");
			String eString = (String)jwtKeyMap.get("e");
			BigInteger modulus = new BigInteger(1, Base64.getUrlDecoder().decode(nString));
			BigInteger exponent = new BigInteger(1, Base64.getUrlDecoder().decode(eString));
			return kf.generatePublic(new RSAPublicKeySpec(modulus, exponent));
		} catch (InvalidKeySpecException e) {
			throw new InvalidPublicKeyException("Invalid public key", e);
		} catch (NoSuchAlgorithmException e) {
			throw new InvalidPublicKeyException("Invalid algorithm to generate key", e);
		}
	}

	public static Map<String, Object> createJWKFromPublicKey(PublicKey publicKey, String publicKeyId){

		Map<String, Object> keyMap = new HashMap<>();
		RSAPublicKey pub = (RSAPublicKey) publicKey;

		String encoodedModulus =  Base64.getUrlEncoder().encodeToString(pub.getModulus().toByteArray());
		String encodedExponent =  Base64.getUrlEncoder().encodeToString(pub.getPublicExponent().toByteArray());
		keyMap.put(AOSecurityConstants.KEY_ID, publicKeyId);
		keyMap.put(AOSecurityConstants.USE,AOSecurityConstants.USE_SIG);
		keyMap.put(AOSecurityConstants.KTY, pub.getAlgorithm());
		keyMap.put(AOSecurityConstants.EXPONENT, encodedExponent);
		keyMap.put(AOSecurityConstants.MODULUS, encoodedModulus);
		keyMap.put(AOSecurityConstants.ALG,AOSecurityConstants.RS256);
		return keyMap;
	}

	public static String sign(String plainText, PrivateKey privateKey) throws Exception {
		Signature privateSignature = Signature.getInstance("SHA256withRSA");
		privateSignature.initSign(privateKey);
		privateSignature.update(plainText.getBytes(UTF_8));

		byte[] signature = privateSignature.sign();

		return Base64.getEncoder().encodeToString(signature);
	}

	public static boolean verify(String plainText, String signature, PublicKey publicKey) throws Exception {
		Signature publicSignature = Signature.getInstance("SHA256withRSA");
		publicSignature.initVerify(publicKey);
		publicSignature.update(plainText.getBytes(UTF_8));

		byte[] signatureBytes = Base64.getDecoder().decode(signature);

		return publicSignature.verify(signatureBytes);
	}


    /*
    https://www.pixelstech.net/article/1408524957-Generate-certificate-in-Java----Store-certificate-in-KeyStore
    https://www.novixys.com/blog/how-to-generate-rsa-keys-java/
     */

	/**
	 * Generate the desired keypair
	 *
	 * @param alg
	 * @param keySize
	 * @return
	 */
	public static KeyPair generateKeyPair(String alg, int keySize) {
		try {
			KeyPairGenerator keyPairGenerator = null;
			keyPairGenerator = KeyPairGenerator.getInstance(alg);

			keyPairGenerator.initialize(keySize);

			return keyPairGenerator.generateKeyPair();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

	public static class SecureTextRandomProvider implements TextRandomProvider{

		Random random = new SecureRandom();

		@Override
		public int nextInt(int i) {
			return random.nextInt(i);
		}
	}

	public static String generateRandomSpecialCharacters(int length) {
		SecureTextRandomProvider stp = new SecureTextRandomProvider();
		RandomStringGenerator pwdGenerator = new RandomStringGenerator.Builder()
				.withinRange(33, 45)
				.usingRandom(stp)
				.build();
		return pwdGenerator.generate(length);
	}

	public static final Integer DEFAULT_KEY_SIZE = 256;
	public static String createAKey(int charcterSize){
		String key = null;
		try{
			key = generateRandomSpecialCharacters(charcterSize);
		} catch (Exception e){
			logger.error("Could not create key ",e);
		}
		return key;
	}

	/* Generating Secret key */

	// Generating Secret Key using KeyGenerator class with 256
	public static SecretKey generateKey(int n) throws NoSuchAlgorithmException {
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		keyGenerator.init(n);
		SecretKey originalKey = keyGenerator.generateKey();
		return originalKey;
	}

	// Generating Secret Key using password and salt
	public static SecretKey getKeyFromPassword(String password, String salt)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
		SecretKey originalKey = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
		return originalKey;
	}

	/* Converting Secret key into String */
	public static String convertSecretKeyToString(SecretKey secretKey) throws NoSuchAlgorithmException {
		// Converting the Secret Key into byte array
		byte[] rawData = secretKey.getEncoded();
		// Getting String - Base64 encoded version of the Secret Key
		String encodedKey = Base64.getEncoder().encodeToString(rawData);
		return encodedKey;
	}

	/* Converting String into Secret key into */
	public static SecretKey convertStringToSecretKeyto(String encodedKey) {
		// Decoding the Base64 encoded string into byte array
		byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
		// Rebuilding the Secret Key using SecretKeySpec Class
		SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
		return originalKey;
	}

}
