package com.alpha.omega.security.key;

import com.enterprise.pwc.datalabs.security.PwcSecurityConstants;

import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

public class KeyUtils {
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
		keyMap.put(PwcSecurityConstants.KEY_ID, publicKeyId);
		keyMap.put(PwcSecurityConstants.USE,PwcSecurityConstants.USE_SIG);
		keyMap.put(PwcSecurityConstants.KTY, pub.getAlgorithm());
		keyMap.put(PwcSecurityConstants.EXPONENT, encodedExponent);
		keyMap.put(PwcSecurityConstants.MODULUS, encoodedModulus);
		keyMap.put(PwcSecurityConstants.ALG,PwcSecurityConstants.RS256);
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

}
