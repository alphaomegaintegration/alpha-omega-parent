package com.globalpayments.security.key;


import com.auth0.jwk.InvalidPublicKeyException;
import com.globalpayments.security.token.AuthConstants;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static java.nio.charset.StandardCharsets.UTF_8;

//import java.util.zip.ZipFile;
//import net.lingala.zip4j.core.ZipFile;
//import net.lingala.zip4j.util.Zip4jConstants;

public class RSAKeyUtils {

    public final static String ALGORITHM = "RSA";
    public static final int DEFAULT_RSA_SIZE = 1024;
    public final static String SIGNER = "SHA1WithRSA";


    /*
    https://stackoverflow.com/questions/3660132/formatting-rsa-keys-for-openssl-in-java
     */

    /*
    public static void main(String[] argv) throws Exception {
    String algorithm = "DSA"; // or RSA, DH, etc.

    // Generate a 1024-bit Digital Signature Algorithm (DSA) key pair
    KeyPairGenerator keyGen = KeyPairGenerator.getInstance(algorithm);
    keyGen.initialize(1024);
    KeyPair keypair = keyGen.genKeyPair();
    PrivateKey privateKey = keypair.getPrivate();
    PublicKey publicKey = keypair.getPublic();

    byte[] privateKeyBytes = privateKey.getEncoded();
    byte[] publicKeyBytes = publicKey.getEncoded();

    KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
    EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
    PrivateKey privateKey2 = keyFactory.generatePrivate(privateKeySpec);

    EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
    PublicKey publicKey2 = keyFactory.generatePublic(publicKeySpec);

    // The orginal and new keys are the same
    boolean same = privateKey.equals(privateKey2);
    same = publicKey.equals(publicKey2);
  }
     */

    private static Logger logger = LoggerFactory.getLogger(RSAKeyUtils.class);

    /*
    {
      "kid": "hQnjm8xb5ETnacv3SRlDatrTAqh4NNyI0i5hB8LkjsA",
      "use": "sig",
      "kty": "RSA",
      "e": "AQAB",
      "n": "v8nq4v_5JXiDU-hT11wCkUVefDtXZUfn2jLqfREPT2f_Hxv4TNAedyph1F5thOBjXuh9Qwj02Uhuj_MmHIJhLFqYT8HlDbfiimMwRXJKms5J0lD6mGTSVV3z2gxsXXo7XIaqwJpkpWXt82NY31OUKg8Dh2Xi6k-UIV4zBr3YreZf2KImOVcoNZi5UYAgZso58cz93OYHeSmKLSVY47mroVTP6ad3NE7JU3eudw68lEXtj9D90VcYUEuEWoZcqBo0k9gAy0khQAnNmiuci17vlNceIiGQSE1x0_DSIh3Zl3wl5WRms3T1cBxGOChKLtey2k4k8jGjVlJ4v1PyWaRBWw"
    }
     */

    public static boolean keyPairMatches(KeyPair keyPair) throws Exception {
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();
        // create a challenge
        byte[] challenge = new byte[10000];
        ThreadLocalRandom.current().nextBytes(challenge);

// sign using the private key
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initSign(privateKey);
        sig.update(challenge);
        byte[] signature = sig.sign();

// verify signature using the public key
        sig.initVerify(publicKey);
        sig.update(challenge);

        boolean keyPairMatches = sig.verify(signature);
        return keyPairMatches;
    }

    public static PublicKey extractPublicKey(Map<String, Object> jwtKeyMap) throws InvalidPublicKeyException {

        String keyType = (String)jwtKeyMap.get("kty");

        if (!ALGORITHM.equalsIgnoreCase(keyType)) {
            return null;
        }

        try {
            KeyFactory kf = KeyFactory.getInstance(ALGORITHM);
            String nString = (String)jwtKeyMap.get("n");
            String eString = (String)jwtKeyMap.get("e");
            BigInteger modulus = new BigInteger(1, Base64.getDecoder().decode(nString));
            BigInteger exponent = new BigInteger(1, Base64.getDecoder().decode(eString));
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

        //String encoodedModulus = Base64.getEncoder().encodeToString(pub.getModulus().toString(16).getBytes());
        //String encodedExponent = Base64.getEncoder().encodeToString(pub.getPublicExponent().toString(16).getBytes());
        keyMap.put(AuthConstants.KEY_ID, publicKeyId);
        keyMap.put(AuthConstants.USE,AuthConstants.USE_SIG);
        //keyMap.put(AuthConstants.KTY, ALGORITHM);
        keyMap.put(AuthConstants.KTY, pub.getAlgorithm());
        keyMap.put(AuthConstants.EXPONENT, encodedExponent);
        keyMap.put(AuthConstants.MODULUS, encoodedModulus);
        keyMap.put("alg","RS256");
        return keyMap;
    }

//    private static void openssldemo() {
//        // https://pasztor.at/blog/working-with-certificates-in-java
//        /*
//        verify certificate
//        openssl x509 -text -in cer.pem
//
//        verify private key
//        openssl rsa -in pri.pem -noout -text
//
//        openssl rsautl -in enc1.txt -inkey pri.pem  -out enc1-out.txt -decrypt
//         */
//
//        KeyPairInfo keyPairInfo = createKeyPair(DEFAULT_RSA_SIZE, ALGORITHM);
//
//        // Certificate
//        String certificatePem = pemFromCertifcate(keyPairInfo.getX509Certificate());
//
//        // Private key
//        String pkc1Str = pkcs1FromPrivateKey(keyPairInfo.getKeyPair().getPrivate());
//        logger.info("Certificate start");
//        logger.info(certificatePem);
//        logger.info("Certificate end");
//
//        logger.info("Private key start");
//        logger.info(pkc1Str);
//        logger.info("Private key end");
//
//        String testStr = "Lets encrypt this!";
//        try {
//            String encryptedStr = encrypt(testStr, keyPairInfo.getKeyPair().getPublic());
//            logger.info("Test String => {}",testStr);
//            logger.info("Encrypted test string => [{}]",encryptedStr);
//            String decryptedStr = decrypt(encryptedStr, keyPairInfo.getKeyPair().getPrivate());
//            logger.info("decryptedStr test string => [{}]",decryptedStr);
//            String decryptedStr2 = expectExceptionDecrypt(encryptedStr, keyPairInfo.getKeyPair().getPublic());
//            logger.info("decryptedStr2 test string => [{}]",decryptedStr2);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//    }

    public static String pkcs1FromPrivateKey(PrivateKey privateKey) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (PemWriter pemWriter = new PemWriter(new OutputStreamWriter(outputStream))) {
            //Need to convert to PKCS#1
            PrivateKeyInfo pkInfo = PrivateKeyInfo.getInstance(privateKey.getEncoded());
            ASN1Encodable privateKeyPKCS1ASN1Encodable = pkInfo.parsePrivateKey();
            ASN1Primitive privateKeyPKCS1ASN1 = privateKeyPKCS1ASN1Encodable.toASN1Primitive();

            pemWriter.writeObject(new PemObject("RSA PRIVATE KEY", privateKeyPKCS1ASN1.getEncoded()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new String(outputStream.toByteArray());
    }

    public static String pkc8FromPrivateKey(PrivateKey privateKey) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (PemWriter pemWriter = new PemWriter(new OutputStreamWriter(outputStream))) {
            pemWriter.writeObject(new PemObject("PRIVATE KEY", privateKey.getEncoded()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new String(outputStream.toByteArray());
    }

    public static String pemFromCertifcate(X509Certificate x509Certificate) {
        String out = "";

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (PemWriter pemWriter = new PemWriter(new OutputStreamWriter(outputStream))) {
            pemWriter.writeObject(new PemObject("CERTIFICATE", x509Certificate.getEncoded()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        out =  new String(outputStream.toByteArray());
        return out;
    }



    public static KeyPairInfo createKeyPair(int keySize, String algorithm) {

        // keySize = 1024
        // algorithm = RSA
        // signer =
        KeyPairInfo keyPairInfo = null;

        try {
            String uuid = UUID.randomUUID().toString();

            // RSA, 1024
            KeyPairGenerator kpg = KeyPairGenerator.getInstance(algorithm);
            kpg.initialize(keySize); // You can set a different value here


            KeyPair keyPair = kpg.generateKeyPair();
            X509Certificate x509Certificate = generateV3Certificate(keyPair, uuid);

            keyPairInfo = new KeyPairInfo();
            keyPairInfo.setX509Certificate(x509Certificate);
            keyPairInfo.setKeyPair(keyPair);
            keyPairInfo.setPrivateKeyId(uuid);
            keyPairInfo.setPublicKeyId(uuid);
        } catch (Exception e) {
            e.printStackTrace();
            throw new KeyServiceException("Caould not create key pair with ");
        }
        return keyPairInfo;
    }

    public static KeyPairInfo createKeyPair(int keySize, String algorithm, String clientId) {

        // keySize = 1024
        // algorithm = RSA
        // signer =
        KeyPairInfo keyPairInfo = createKeyPair(keySize, algorithm);
        keyPairInfo.setPrivateKeyId(clientId);
        keyPairInfo.setPublicKeyId(clientId);

        return keyPairInfo;
    }

    public static X509Certificate generateV3Certificate(KeyPair pair, String uuid) throws InvalidKeyException,
            NoSuchProviderException, SignatureException {

        X509Certificate x509Certificate = null;

        try {
            KeyPair keys = pair;

            Calendar start = Calendar.getInstance();
            Calendar expiry = Calendar.getInstance();
            expiry.add(Calendar.YEAR, 1);
            String nameHold = "CN=" + uuid;
            X500Name name = new X500Name(nameHold);
            X509v3CertificateBuilder certificateBuilder = new X509v3CertificateBuilder(name, BigInteger.ONE,
                    start.getTime(), expiry.getTime(), name, SubjectPublicKeyInfo.getInstance(keys.getPublic().getEncoded()));
            ContentSigner signer = new JcaContentSignerBuilder("SHA1WithRSA").setProvider(new BouncyCastleProvider()).build(keys.getPrivate());
            X509CertificateHolder holder = certificateBuilder
                    .build(signer);
            x509Certificate = new JcaX509CertificateConverter().setProvider(new BouncyCastleProvider()).getCertificate(holder);
            if (null != x509Certificate) {
                logger.info("Created x509Certificate => {}", x509Certificate.getType());
            }

        } catch (Exception ex) {
            throw new KeyServiceException("Unable to generate self-signed certificate", ex);
        }

        return x509Certificate;
    }


    public static KeyPair generateKeyPair(int byteSize) {

        KeyPairGenerator keyGen = null;
        try {
            keyGen = KeyPairGenerator.getInstance(ALGORITHM);
            keyGen.initialize(byteSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null != keyGen ? keyGen.genKeyPair() : null;
    }

    public static String convertToBase64Encoded(PrivateKey privateKey) {

        byte[] privateKeyBytes = privateKey.getEncoded();
        byte[] encoded = Base64.getEncoder().encode(privateKeyBytes);
        return new String(encoded);
    }


    public static String convertToBase64Encoded(PublicKey publicKey) {

        byte[] privateKeyBytes = publicKey.getEncoded();
        byte[] encoded = Base64.getEncoder().encode(privateKeyBytes);
        return new String(encoded);
    }

    public static Key loadPublicKey(String stored) throws GeneralSecurityException, IOException {
        byte[] data = Base64.getDecoder().decode((stored.getBytes()));
        X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
        KeyFactory fact = KeyFactory.getInstance(ALGORITHM);
        return fact.generatePublic(spec);

    }

    public static Key loadPrivateKey(String stored) throws GeneralSecurityException, IOException {
        byte[] data = Base64.getDecoder().decode((stored.getBytes()));
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(data);
        KeyFactory fact = KeyFactory.getInstance(ALGORITHM);
        return fact.generatePrivate(spec);

    }


    public static KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048, new SecureRandom());
        KeyPair pair = generator.generateKeyPair();

        return pair;
    }

    public byte[] sign(String data, String keyFile) throws InvalidKeyException, Exception {
        Signature rsa = Signature.getInstance("SHA1withRSA");
        rsa.initSign((PrivateKey) loadPrivateKey(keyFile));
        rsa.update(data.getBytes());
        return rsa.sign();
    }

    private boolean verifySignature(byte[] data, byte[] signature, String keyFile) throws Exception {
        Signature sig = Signature.getInstance("SHA1withRSA");
        sig.initVerify((PublicKey) loadPublicKey(keyFile));
        sig.update(data);

        return sig.verify(signature);
    }

    public static SecretKey decryptSecretKeyString(String encryptedKeyStr, PrivateKey privateKey) throws Exception {

        String decryptedKeyStr = decrypt(encryptedKeyStr, privateKey);
        byte[] decodedSecretKey = Base64.getDecoder().decode(decryptedKeyStr);
        SecretKey secretKey = new SecretKeySpec(decodedSecretKey, "AES");
        return secretKey;
    }

    public static String encryptSecretKey(SecretKey secretKey, PublicKey publicKey) throws Exception {
        String secretKeyStr = Base64.getEncoder().encodeToString(secretKey.getEncoded());
        String encrypted = encrypt(secretKeyStr, publicKey);
        return encrypted;
    }

    public static String encrypt(String plainText, PublicKey publicKey) throws Exception {
        Cipher encryptCipher = Cipher.getInstance("RSA/ECB/OAEPWithMD5AndMGF1Padding");
        encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);

        byte[] cipherText = encryptCipher.doFinal(plainText.getBytes(UTF_8));

        return Base64.getEncoder().encodeToString(cipherText);
    }

    public static String decrypt(String cipherText, PrivateKey privateKey) throws Exception {
        byte[] bytes = Base64.getDecoder().decode(cipherText);

        Cipher decriptCipher = Cipher.getInstance("RSA/ECB/OAEPWithMD5AndMGF1Padding");
        decriptCipher.init(Cipher.DECRYPT_MODE, privateKey);

        return new String(decriptCipher.doFinal(bytes), UTF_8);
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


    private static String getPEMPrivateKeyFromDER(PrivateKey privateKey) {
        String begin = "-----BEGIN PRIVATE KEY-----";
        String end = "-----END PRIVATE KEY-----";
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
        String key = Base64.getEncoder().encodeToString(pkcs8EncodedKeySpec.getEncoded());
        return begin + "\n" + key + "\n" + end;
    }

    private static String getPEMPrivateKeyFromDER2(PrivateKey privateKey) {
        String begin = "-----BEGIN PRIVATE KEY-----";
        String end = "-----END PRIVATE KEY-----";
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
        String key = Base64.getEncoder().encodeToString(pkcs8EncodedKeySpec.getEncoded());
        return begin + "\n" + key + "\n" + end;
    }


    private static String getPublicKeyFromDER(PublicKey publicKey) {
        String begin = "-----BEGIN PUBLIC KEY-----";
        String end = "-----END PUBLIC KEY-----";
        X509EncodedKeySpec pkcs8EncodedKeySpec = new X509EncodedKeySpec(publicKey.getEncoded());
        String key = Base64.getEncoder().encodeToString(pkcs8EncodedKeySpec.getEncoded());
        return begin + "\n" + key + "\n" + end;
    }

    public static String printAliases(Enumeration<String> aliasEnumeration) {
        StringBuilder sb = new StringBuilder();
        while (aliasEnumeration.hasMoreElements()) {
            sb.append(aliasEnumeration.nextElement());
        }
        return sb.toString();
    }

    public static void main(String... argv) throws Exception {



    }

    public static byte[] encryptOpenSSl(PublicKey key, byte[] plaintext) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(plaintext);
    }

    public static byte[] decryptOpenSSl(PrivateKey key, byte[] ciphertext) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(ciphertext);
    }

    private static void dumpKeyPair(KeyPair keyPair) {
        PublicKey pub = keyPair.getPublic();
        System.out.println("Public Key: " + getHexString(pub.getEncoded()));

        PrivateKey priv = keyPair.getPrivate();
        System.out.println("Private Key: " + getHexString(priv.getEncoded()));
    }

    private static String getHexString(byte[] b) {
        String result = "";
        for (int i = 0; i < b.length; i++) {
            result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
        }
        return result;
    }

    public static void saveKeyPairLocally(String path, KeyPair keyPair) throws IOException {
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();

        // Store Public Key.
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(
                publicKey.getEncoded());
       try (FileOutputStream fos = new FileOutputStream(path + "/public.key")) {
    	   fos.write(x509EncodedKeySpec.getEncoded());
          // fos.close();
       }

        // Store Private Key.
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(
                privateKey.getEncoded());
       try (FileOutputStream fos = new FileOutputStream(path + "/private.key")) {
    	   fos.write(pkcs8EncodedKeySpec.getEncoded());
          // fos.close();
       }

    }

    public static KeyPair loadKeyPairFromLocal(String path, String algorithm)
            throws IOException, NoSuchAlgorithmException,
            InvalidKeySpecException {
        // Read Public Key.
        File filePublicKey = new File(path + "/public.key");
        byte[] encodedPublicKey = new byte[(int) filePublicKey.length()];

        try (FileInputStream fis = new FileInputStream(path + "/public.key")) {
        	 fis.read(encodedPublicKey);
             //fis.close();
        }

        // Read Private Key.
        File filePrivateKey = new File(path + "/private.key");
        byte[] encodedPrivateKey = new byte[(int) filePrivateKey.length()];
        try (FileInputStream  fis = new FileInputStream(path + "/private.key")) {
        	  fis.read(encodedPrivateKey);
             // fis.close();
        }

        // Generate KeyPair.
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(
                encodedPublicKey);
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(
                encodedPrivateKey);
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

        return new KeyPair(publicKey, privateKey);
    }


    public PrivateKey decodePemEncodedPrivateKey(Reader privateKeyReader, String password) {
        try (PEMParser pemParser = new PEMParser(privateKeyReader)) {
            Object keyPair = pemParser.readObject();
            // retrieve the PrivateKeyInfo from the returned keyPair object. if the key is encrypted, it needs to be
            // decrypted using the specified password first.
            PrivateKeyInfo keyInfo;
            if (keyPair instanceof PEMEncryptedKeyPair) {
                if (password == null) {
                    throw new RuntimeException("Unable to import private key. Key is encrypted, but no password was provided.");
                }
                PEMDecryptorProvider decryptor = new JcePEMDecryptorProviderBuilder().build(password.toCharArray());
                PEMKeyPair decryptedKeyPair = ((PEMEncryptedKeyPair) keyPair).decryptKeyPair(decryptor);
                keyInfo = decryptedKeyPair.getPrivateKeyInfo();
            } else {
                keyInfo = ((PEMKeyPair) keyPair).getPrivateKeyInfo();
            }
            return new JcaPEMKeyConverter().getPrivateKey(keyInfo);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


     /*
    22

Here you have the commands you need to encrypt or decrypt using openssl:
echo `pbpaste`
Decrypt:

$ openssl rsautl -decrypt -in $ENCRYPTED -out $PLAINTEXT -inkey keys/privkey.pem
Encrypt:

$ openssl rsautl -encrypt -in $PLAINTEXT -out $PLAINTEXT.encrypt -pubin -inkey keys/pubkey.pem

        openssl rsautl -decrypt -in $ENCRYPTED -out $PLAINTEXT -inkey keys/privkey.pem

        https://www.shellhacks.com/encrypt-decrypt-file-password-openssl/

openssl rsautl -encrypt -in beforeEnc.txt -out afterEnc.txt -pubin -inkey keys/pubkey.pem
        openssl rsautl -decrypt -in afterEnc.txt -outbeforeEnc.txt -inkey keys/privkey.pem

     */

}
