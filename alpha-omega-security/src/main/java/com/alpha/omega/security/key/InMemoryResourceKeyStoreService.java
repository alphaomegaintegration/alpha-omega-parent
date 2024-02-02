package com.globalpayments.security.key;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;

public class InMemoryResourceKeyStoreService implements KeyStoreService {


    Map<String, Resource> resourceMap = new HashMap<>();

    boolean persistPrivateKey = Boolean.FALSE;

    public InMemoryResourceKeyStoreService() {
    }

    @Override
    public KeyStoreInfo createKeyStore(String clientId) {
        KeyStoreInfo keyStoreInfo = new KeyStoreInfo();
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStoreType.PKCS12.name());
            keyStore.load(null, null);;
            keyStoreInfo.setKeyStoreId(clientId);
            keyStoreInfo.setKeyStore(keyStore);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            keyStore.store(baos, clientId.toCharArray());
            resourceMap.put(clientId,new ByteArrayResource(baos.toByteArray()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("Cannot create KeyStore ", e);
        }
        return keyStoreInfo;
    }


    // TODO REFACTOR
    @Override
    public KeyStoreInfo getKeyStore(String clientId) {
        KeyStoreInfo keyStoreInfo = null;
        try {
            //
            Resource resource = resourceMap.get(clientId);
            if (resource == null){
                keyStoreInfo = createKeyStore(clientId);
            } else {
                keyStoreInfo = new KeyStoreInfo();
                KeyStore keyStore = KeyStore.getInstance(KeyStoreType.PKCS12.name());
                try (InputStream is = resource.getInputStream()) {
                	   keyStore.load(is, clientId.toCharArray());
                       keyStoreInfo.setKeyStoreId(clientId);
                       keyStoreInfo.setKeyStore(keyStore);
                }
             
            }


        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("Cannot create KeyStore ", e);
        }
        return keyStoreInfo;
    }

    @Override
    public KeyStoreInfo saveOrUpdatePublicKey(String clientId, KeyPairInfo keyPairInfo) {
        KeyStoreInfo keyStoreInfo = this.getKeyStore(clientId);
        try {
            KeyStore keyStore = keyStoreInfo.getKeyStore();
            if (persistPrivateKey){
                java.security.cert.X509Certificate[] chain = new java.security.cert.X509Certificate[1];
                chain[0]=keyPairInfo.getX509Certificate();
                keyStore.setKeyEntry(keyPairInfo.getPrivateKeyId(), keyPairInfo.getKeyPair().getPrivate(), clientId.toCharArray(), chain);
            }

            KeyStore.PasswordProtection passwordProtection = new KeyStore.PasswordProtection(clientId.toCharArray());
            KeyStore.TrustedCertificateEntry certificateEntry = new KeyStore.TrustedCertificateEntry(keyPairInfo.getX509Certificate());
            keyStore.setEntry(keyPairInfo.getPublicKeyId(),certificateEntry, null);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            keyStore.store(baos, clientId.toCharArray());
            resourceMap.put(clientId,new ByteArrayResource(baos.toByteArray()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("Cannot save public key KeyStore ", e);
        }

        return keyStoreInfo;
    }

    @Override
    public KeyStoreInfo saveKeyStoreToFile(String clientId, String filePath) {
        KeyStoreInfo keyStoreInfo = this.getKeyStore(clientId);

        try{
            KeyStore keyStore = keyStoreInfo.getKeyStore();
            try (FileOutputStream os  = new FileOutputStream(filePath)) {
            	 keyStore.store(os, clientId.toCharArray());
            }

        }catch(Exception e){
            e.printStackTrace();
            throw new IllegalStateException("Cannot save key KeyStore to file ", e);
        }
        return keyStoreInfo;
    }

    @Override
    public String deleteKeyStore(String clientId) {
        resourceMap.remove(clientId);
        return clientId;
    }
}
