package com.globalpayments.security.key;

import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509ExtensionUtils;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.bc.BcDigestCalculatorProvider;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class DefaultKeyService implements KeyService{

    private static Logger logger = LoggerFactory.getLogger(DefaultKeyService.class);

    KeyStoreService keyStoreService;

    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    public DefaultKeyService(KeyStoreService keyStoreService) {
        this.keyStoreService = keyStoreService;
    }

    @Override
    public KeyPairInfo createKeyPair(int keySize, String algorithm){

        // keySize = 1024
        // algorithm = RSA
        // signer =
        KeyPairInfo keyPairInfo = null;

        try {
            String uuid = UUID.randomUUID().toString();

            KeyPairGenerator kpg = KeyPairGenerator.getInstance(algorithm);
            kpg.initialize(keySize); // You can set a different value here


            KeyPair keyPair = kpg.generateKeyPair();
            X509Certificate x509Certificate = generateV3Certificate(keyPair,uuid);

            keyPairInfo = new KeyPairInfo();
            keyPairInfo.setX509Certificate(x509Certificate);
            keyPairInfo.setKeyPair(keyPair);
            keyPairInfo.setPrivateKeyId(uuid);
            keyPairInfo.setPublicKeyId(uuid);
        } catch (Exception e) {
            e.printStackTrace();
            throw new KeyServiceException("Could not create key pair with ");
        }
        return keyPairInfo;
    }

    public X509Certificate generateV3Certificate(KeyPair pair,String uuid) throws InvalidKeyException,
            NoSuchProviderException, SignatureException {

        X509Certificate x509Certificate = null;

        try {
            KeyPair keys = pair;

            Calendar start = Calendar.getInstance();
            Calendar expiry = Calendar.getInstance();
            expiry.add(Calendar.YEAR, 1);
            String nameHold = "CN="+uuid;
            X500Name name = new X500Name(nameHold);
            X509v3CertificateBuilder certificateBuilder = new X509v3CertificateBuilder(name, BigInteger.ONE,
                    start.getTime(), expiry.getTime(), name, SubjectPublicKeyInfo.getInstance(keys.getPublic().getEncoded()));
            ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA").setProvider(new BouncyCastleProvider()).build(keys.getPrivate());
            X509CertificateHolder holder = certificateBuilder
                    .build(signer);
            x509Certificate = new JcaX509CertificateConverter().setProvider(new BouncyCastleProvider()).getCertificate(holder);
            if (null != x509Certificate){
                logger.info("Created x509Certificate => {}",x509Certificate.getType());
            }

        } catch (Exception ex) {
            throw new KeyServiceException("Unable to generate self-signed certificate", ex);
        }

        return x509Certificate;
    }

    public static X509Certificate generateV3Certificate(final KeyPair keyPair,
                                           final String uuid,
                                           final int days)
            throws OperatorCreationException, CertificateException, CertIOException {
        X509Certificate x509Certificate = null;
        try{
            final Instant now = Instant.now();
            final Date notBefore = Date.from(now);
            final Date notAfter = Date.from(now.plus(Duration.ofDays(days)));

            // SHA256withRSA
            // SHA1WithRSA"
            final ContentSigner contentSigner = new JcaContentSignerBuilder("SHA256withRSA").build(keyPair.getPrivate());
            final X500Name x500Name = new X500Name("CN=" + uuid);
            final X509v3CertificateBuilder certificateBuilder =
                    new JcaX509v3CertificateBuilder(x500Name,
                            BigInteger.valueOf(now.toEpochMilli()),
                            notBefore,
                            notAfter,
                            x500Name,
                            keyPair.getPublic())
                            .addExtension(Extension.subjectKeyIdentifier, false, createSubjectKeyId(keyPair.getPublic()))
                            .addExtension(Extension.authorityKeyIdentifier, false, createAuthorityKeyId(keyPair.getPublic()))
                            .addExtension(Extension.basicConstraints, true, new BasicConstraints(true));
            x509Certificate =  new JcaX509CertificateConverter()
                    .setProvider(new BouncyCastleProvider()).getCertificate(certificateBuilder.build(contentSigner));

        }catch (Exception ex) {
            throw new KeyServiceException("Unable to generate self-signed certificate", ex);
        }

        return x509Certificate;
    }

    private static AuthorityKeyIdentifier createAuthorityKeyId(final PublicKey publicKey)
            throws OperatorCreationException
    {
        final SubjectPublicKeyInfo publicKeyInfo = SubjectPublicKeyInfo.getInstance(publicKey.getEncoded());
        final DigestCalculator digCalc =
                new BcDigestCalculatorProvider().get(new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1));

        return new X509ExtensionUtils(digCalc).createAuthorityKeyIdentifier(publicKeyInfo);
    }
    private static SubjectKeyIdentifier createSubjectKeyId(final PublicKey publicKey) throws OperatorCreationException {
        final SubjectPublicKeyInfo publicKeyInfo = SubjectPublicKeyInfo.getInstance(publicKey.getEncoded());
        final DigestCalculator digCalc =
                new BcDigestCalculatorProvider().get(new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1));

        return new X509ExtensionUtils(digCalc).createSubjectKeyIdentifier(publicKeyInfo);
    }

    @Override
    public KeyPairInfo saveKeyPair(KeyPairInfo keyPairInfo, String clientId) {

        KeyStoreInfo keyStoreInfo = keyStoreService.saveOrUpdatePublicKey(clientId, keyPairInfo);
        return keyPairInfo;
    }

    @Override
    public SecretKeyInfo createSecretKey(int keySize, String algorithm) {

        // keySize = 256
        // algorithm AES
        SecretKeyInfo secretKeyInfo = null;

        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm);
            keyGenerator.init(keySize);
            SecretKey secretKey = keyGenerator.generateKey();
            secretKeyInfo = new SecretKeyInfo();
            secretKeyInfo.setSecretKey(secretKey);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new KeyServiceException("Could not create key with ");
        }

        return secretKeyInfo;
    }


    @Override
    public KeyPairInfo findKeyPair(String privateKeyId, String publicKeyId, String clientId) {

        KeyStoreInfo keyStoreInfo = keyStoreService.getKeyStore(clientId);
        logger.info("findKeyPair Looking for public key with id => {}",publicKeyId);
        KeyPairInfo keyPairInfo = null;
        try {

            logger.info("findKeyPair => {}, keystore aliases => {}",keyStoreInfo,RSAKeyUtils.printAliases(keyStoreInfo.getKeyStore().aliases()));
            //X509Certificate x509Certificate = (X509Certificate)keyStoreInfo.getKeyStore().getEntry(publicKeyId,null);

            // Refactoring
            /*
            X509Certificate x509Certificate = (X509Certificate) keyStoreInfo.getKeyStore().getCertificate(publicKeyId);
            keyPairInfo.setX509Certificate(x509Certificate);
            keyPairInfo.setPublicKeyId(publicKeyId);
            if (x509Certificate == null){

            }
            KeyPair keyPair = new KeyPair(x509Certificate.getPublicKey(), null);
            keyPairInfo.setKeyPair(keyPair);
            */


            keyPairInfo = getKeyPairFromAlias(publicKeyId, keyStoreInfo.getKeyStore());

        } catch (Exception e) {
            e.printStackTrace();
            throw new KeyServiceException("Could not find keypair info "+e.getMessage());
        }

        return keyPairInfo;
    }

    //KeyStoreException
    private KeyPairInfo getKeyPairFromAlias(String alias, KeyStore keyStore){
        KeyPairInfo keyPairInfo = new KeyPairInfo();
        try{
            X509Certificate x509Certificate = (X509Certificate) keyStore.getCertificate(alias);
            keyPairInfo.setX509Certificate(x509Certificate);
            keyPairInfo.setPublicKeyId(alias);
            if (x509Certificate == null){

            }
            KeyPair keyPair = new KeyPair(x509Certificate.getPublicKey(), null);
            keyPairInfo.setKeyPair(keyPair);
        } catch (KeyStoreException kse){
            throw new KeyServiceException("Could not find keypair info with publicKeyId "+alias);
        }
        return keyPairInfo;
    }

    @Override
    public List<KeyPairInfo> findKeyPairs(String clientId) {
        final KeyStoreInfo keyStoreInfo = keyStoreService.getKeyStore(clientId);
        logger.info("findKeyPair Looking for clientId keypairs with id => {}",clientId);
        List<KeyPairInfo> keyPairInfos = null;
        try{
            List<String> publicKeys = Collections.list(keyStoreInfo.getKeyStore().aliases());
            keyPairInfos = publicKeys.stream()
                    .map(publicKey -> getKeyPairFromAlias(publicKey, keyStoreInfo.getKeyStore()))
                    .collect(Collectors.toList());

        }catch (Exception e) {
            e.printStackTrace();
            throw new KeyServiceException("Could not find keypairs info for clientId "+clientId);
        }
        return keyPairInfos;
    }


}
