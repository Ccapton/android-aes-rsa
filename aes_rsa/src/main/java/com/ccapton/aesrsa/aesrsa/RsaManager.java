package com.ccapton.aesrsa.aesrsa;

import android.os.SystemClock;
import android.util.Log;

import com.blankj.utilcode.util.SPUtils;

import java.security.KeyPair;



public class RsaManager {

    private static final String TAG="RsaManager";

    private static RsaManagerKeeper rsaManagerKeeper;

    private RsaManager(){

    }

    public interface RsaManagerKeeper{
        KeyPair getKeyPaire(String kpKey);
        void setKeyPair(String kpKey, KeyPair keyParie);
    }

    static class RsaManagerHolder{
        final static RsaManager rsaManager = new RsaManager();
    }

    public static RsaManager getInstance(){
        return RsaManagerHolder.rsaManager;
    }

    public static void init(RsaManagerKeeper keeper){
        rsaManagerKeeper = keeper;
    }

    /*
    * 加密
    * @content  需要加密的字符串
    * @encryptedRsaTextKey  spf保存已加密的文本-键
    * @encryptedAesKey  spf保存aes已加密密钥-键
    * @kpKey  RsaManagerKeeper中的map保存KeyPair-键
    * */
    public String save(String content,String encryptedRsaTextKey,String encryptedAesKey,String kpKey){

        long start = SystemClock.currentThreadTimeMillis();

        RsaExecutor rsaExecutor = new RsaExecutor();

        if (rsaManagerKeeper!=null){
            if (rsaManagerKeeper.getKeyPaire(kpKey)==null){
                rsaExecutor.setRsaKey(RSAEncryptDecrypt.generateRSAKey());
                rsaManagerKeeper.setKeyPair(kpKey,rsaExecutor.getRsaKey());
            } else {
                rsaExecutor.setRsaKey(rsaManagerKeeper.getKeyPaire(kpKey));
            }
        }

        String encryptText = rsaExecutor.encryptString(content);
        long end = SystemClock.currentThreadTimeMillis();

        Log.w(TAG, "save: spend "+(end - start));

        SPUtils.getInstance().put(encryptedRsaTextKey,encryptText);
        SPUtils.getInstance().put(encryptedAesKey,ByteUtil.toHexString(rsaExecutor.getEncryptedAESKey()));

        return encryptText;
    }

    /*
    * 解密
    * @encryptedRsaTextKey  spf保存已加密的文本-键
    * @encryptedAesKey  spf保存aes已加密密钥-键
    * @kpKey  RsaManagerKeeper中的map保存KeyPair-键
    * */
    public String load(String encryptedRsaTextKey,String encryptedAesKey,String kpKey){
        String decryptText = "";
        try{
            long start = SystemClock.currentThreadTimeMillis();

            RsaExecutor executor = new RsaExecutor();

            if (rsaManagerKeeper!=null){
                if (rsaManagerKeeper.getKeyPaire(kpKey)!=null){
                    executor.setEncryptedAESKey(ByteUtil.toByteArray(SPUtils.getInstance().getString(encryptedAesKey)));
                }
                if (executor.getRsaKey() == null)
                    executor.setRsaKey(rsaManagerKeeper.getKeyPaire(kpKey));
            }

            Log.w(TAG, "load: "+GsonUtil.to_String((rsaManagerKeeper.getKeyPaire(kpKey))));
            decryptText = executor.decryptString(SPUtils.getInstance().getString(encryptedRsaTextKey));

            long end = SystemClock.currentThreadTimeMillis();
            Log.w(TAG, "load: spend "+(end-start));


        }catch (Exception e){
            Log.w(TAG, "load: ",e);
        }

        return decryptText;
    }



}
