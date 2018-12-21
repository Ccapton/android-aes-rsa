package com.ccapton.aesrsa;

import android.app.Application;

import com.ccapton.aesrsa.aesrsa.RsaManager;

import java.security.KeyPair;
import java.util.HashMap;
import java.util.Map;

public class AesRsaApp extends Application implements RsaManager.RsaManagerKeeper {

    private Map<String,KeyPair> keyPairMap = new HashMap<>();

    @Override
    public void onCreate() {
        super.onCreate();

        RsaManager.init(this);
    }

    public KeyPair getKeyPaire(String kpKey) {
        return keyPairMap.get(kpKey);
    }


    public void setKeyPair(String kpKey,KeyPair keyParie) {
        keyPairMap.put(kpKey,keyParie);
    }
}
