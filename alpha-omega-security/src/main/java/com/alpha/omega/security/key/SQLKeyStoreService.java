package com.globalpayments.security.key;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.util.Base64;

public class SQLKeyStoreService implements KeyStoreService {
    private static Logger logger = LoggerFactory.getLogger(SQLKeyStoreService.class);

    /*
    @Override
    public KeyStoreInfo createKeyStore(String clientId) {
        return null;
    }

    @Override
    public KeyStoreInfo getKeyStore(String clientId) {
        return null;
    }

    @Override
    public KeyStoreInfo saveOrUpdatePublicKey(String clientId, KeyPairInfo keyPairInfo) {
        return null;
    }

    @Override
    public KeyStoreInfo saveKeyStoreToFile(String clientId, String filePath) {
        return null;
    }
    */

    private boolean persistPrivateKey = false;
    private ClientKeyStoreRepository clientKeyStoreRepository;

    public SQLKeyStoreService(boolean persistPrivateKey, ClientKeyStoreRepository clientKeyStoreRepository) {
        this.persistPrivateKey = persistPrivateKey;
        this.clientKeyStoreRepository = clientKeyStoreRepository;
    }

    @Override
    public KeyStoreInfo createKeyStore(String clientId) {
        KeyStoreInfo keyStoreInfo = new KeyStoreInfo();
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStoreType.PKCS12.name());
            keyStore.load(null, null);

            keyStoreInfo.setKeyStoreId(clientId);
            keyStoreInfo.setKeyStore(keyStore);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            keyStore.store(baos, clientId.toCharArray());

            String encodedKeyStore = Base64.getEncoder().encodeToString(baos.toByteArray());
            ClientKeyStore clientKeyStore = new ClientKeyStore();
            clientKeyStore.setClientId(clientId);
            clientKeyStore.setKeyStore(encodedKeyStore);
            clientKeyStoreRepository.save(clientKeyStore);
            logger.info("ClientKeyStore created and saved with clientid => {}",clientId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new KeyServiceException("Cannot create KeyStore with clientId "+clientId, e);
        }
        return keyStoreInfo;
    }

    @Override
    public KeyStoreInfo getKeyStore(String clientId) {

        KeyStoreInfo keyStoreInfo = null;

        try {

            logger.info("Looking for ClientKeyStore with clientId => {}",clientId);
            ClientKeyStore clientKeyStore = clientKeyStoreRepository.findByClientId(clientId);

            if (clientKeyStore == null){
                logger.info("ClientKeyStore is null. Creating a new one with clientid => {}",clientId);
                keyStoreInfo = this.createKeyStore(clientId);
            } else {

                logger.info("ClientKeyStore found. Loading from database with clientId => {}",clientId);
                keyStoreInfo = new KeyStoreInfo();
                String encoded = clientKeyStore.getKeyStore();
                ByteArrayInputStream bais = new ByteArrayInputStream(Base64.getDecoder().decode(encoded));
                KeyStore keyStore = KeyStore.getInstance(KeyStoreType.PKCS12.name());
                keyStore.load(bais, clientId.toCharArray());
                logger.info("in getKeyStore found keyStore.aliases() => {} with clientId => {}",RSAKeyUtils.printAliases(keyStore.aliases()),clientId );
                keyStoreInfo.setKeyStore(keyStore);
                keyStoreInfo.setKeyStoreId(clientId);

            }


        } catch (Exception e) {
            e.printStackTrace();
            throw new KeyServiceException("Cannot create KeyStore with clientId "+clientId, e);
        }

        return keyStoreInfo;
    }

    @Override
    public KeyStoreInfo saveOrUpdatePublicKey(String clientId, KeyPairInfo keyPairInfo) {

        String publicKeyId = keyPairInfo.getPublicKeyId();
        KeyStoreInfo keyStoreInfo = this.getKeyStore(clientId);
        if (keyStoreInfo == null) {
            keyStoreInfo = this.createKeyStore(clientId);
        }
        logger.info("------------------- saveOrUpdatePublicKey clientId => {}, publicKeyId => {}",new Object[]{clientId, keyPairInfo.getPublicKeyId()});
        try {
            KeyStore keyStore = keyStoreInfo.getKeyStore();
            if (persistPrivateKey) {
                java.security.cert.X509Certificate[] chain = new java.security.cert.X509Certificate[1];
                chain[0] = keyPairInfo.getX509Certificate();
                keyStore.setKeyEntry(keyPairInfo.getPrivateKeyId(), keyPairInfo.getKeyPair().getPrivate(), clientId.toCharArray(), chain);
            }

            KeyStore.PasswordProtection passwordProtection = new KeyStore.PasswordProtection(clientId.toCharArray());
            KeyStore.TrustedCertificateEntry certificateEntry = new KeyStore.TrustedCertificateEntry(keyPairInfo.getX509Certificate());

            keyStore.setCertificateEntry(keyPairInfo.getPublicKeyId(), keyPairInfo.getX509Certificate());
            keyStore.setEntry(keyPairInfo.getPublicKeyId(), certificateEntry, null);
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            keyStore.store(baos, clientId.toCharArray());


            //TODO PUT KEYSTORE IN BLOB COLUMN
            ClientKeyStore clientKeyStore = clientKeyStoreRepository.findByClientId(clientId);
            if (clientKeyStore == null){
                clientKeyStore = new ClientKeyStore();
            }
            String encodedKeyStore = Base64.getEncoder().encodeToString(baos.toByteArray());
            //logger.info("REMOVE FROM LOGS => {}",encodedKeyStore);
            baos.close();
            clientKeyStore.setClientId(clientId);
            clientKeyStore.setKeyStore(encodedKeyStore);
            clientKeyStoreRepository.save(clientKeyStore);
            logger.info("in saveOrUpdatePublicKey found clientId => {}, keyStore.aliases() => {}", clientId,RSAKeyUtils.printAliases(keyStore.aliases()));

            //resourceMap.put(clientId,new ByteArrayResource(baos.toByteArray()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new KeyServiceException("Cannot create KeyStore with clientId "+clientId, e);
        }

        return keyStoreInfo;
    }

    @Override
    public KeyStoreInfo saveKeyStoreToFile(String clientId, String filePath) {

        KeyStoreInfo keyStoreInfo = this.getKeyStore(clientId);

        try {
            KeyStore keyStore = keyStoreInfo.getKeyStore();
            try (FileOutputStream os = new FileOutputStream(filePath)) {
                keyStore.store(os, clientId.toCharArray());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new KeyServiceException("Cannot save key KeyStore to file ", e);
        }
        return keyStoreInfo;
    }

    @Override
    public String deleteKeyStore(String clientId) {
        ClientKeyStore clientKeyStore = clientKeyStoreRepository.findByClientId(clientId);
        if (clientKeyStore != null){
            clientKeyStoreRepository.delete(clientKeyStore);
        }

        return clientId;
    }
}
