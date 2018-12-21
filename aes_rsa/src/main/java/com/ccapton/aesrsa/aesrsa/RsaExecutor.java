package com.ccapton.aesrsa.aesrsa;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyPair;
import java.util.Arrays;
import org.ow2.util.base64.Base64;

public class RsaExecutor {

    private static final String TAG = "RsaExecutor";

    //RSA key pair (public and private)
    private  KeyPair mSaKey = null;

    //encrypted aes key and ivs combined
    private byte[] encryptedAESKey = null;

    public byte[] getEncryptedAESKey() {
        return encryptedAESKey;
    }

    public void setEncryptedAESKey(byte[] encryptedAESKey) {
        this.encryptedAESKey = encryptedAESKey;
    }

    public KeyPair getRsaKey() {
        return mSaKey;
    }

    public void setRsaKey(KeyPair rsaKey) {
        mSaKey = rsaKey;
    }

    public RsaExecutor(){
        AESEncryptDecrypt.setProvider(new org.spongycastle.jce.provider.BouncyCastleProvider(),"SC");
        //setRsaKey(RSAEncryptDecrypt.generateRSAKey());
    }

    public String encryptString(String inputtedUnencryptedText)
    {
        ByteArrayInputStream plainTextInputStream;
        try
        {
            //create an inputstream from a string
            plainTextInputStream = new ByteArrayInputStream(inputtedUnencryptedText.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e)
        {
            Log.e(TAG, e.getMessage(), e);
            return null;
        }

        ByteArrayOutputStream encOutputStream = new ByteArrayOutputStream(1024 * 10);

        //main aes encrypt
        byte[] iv = AESEncryptDecrypt.aesEncrypt(plainTextInputStream,
                AESEncryptDecrypt.NOT_SECRET_ENCRYPTION_KEY.toCharArray(),
                AESEncryptDecrypt.AESCipherType.AES_CBC_PKCS5PADDING,
                encOutputStream);

        long start = System.currentTimeMillis();
        //combine the aes key and iv
        byte[] combined = RsaUtil.concat(AESEncryptDecrypt.NOT_SECRET_ENCRYPTION_KEY.getBytes(),
                iv);

        System.out.println("RsaExecutor.encryptString "+(System.currentTimeMillis() - start));
        encryptedAESKey = RSAEncryptDecrypt.encryptRSA(combined, getRsaKey().getPublic());

        return new String(Base64.encode(encOutputStream.toByteArray()));
    }

    public String decryptString(String encryptedText)
    {

        //sanity test on input from ui
        if (encryptedText != null && encryptedText.trim().length() > 0)
        {
            //decrypt the stored aes and ivs key
            byte[] decryptedAESKeyIVS = RSAEncryptDecrypt.decryptRSA(this.encryptedAESKey, getRsaKey().getPrivate());

            long start = System.currentTimeMillis();
            //we combined the aes key and iv earlier in encryptButton() now after we decrypted
            //the value we split it up
            byte[] aesKey = Arrays.copyOfRange(decryptedAESKeyIVS, 0, 32);
            byte[] ivs = Arrays.copyOfRange(decryptedAESKeyIVS, 32, 48);

            char[] aesKeyChar = null;
            try
            {
                //convert the binary aes key to a char array
                aesKeyChar = new String(aesKey, "UTF-8").toCharArray();
            } catch (UnsupportedEncodingException e)
            {
                Log.e(TAG, e.getMessage(), e);
                return "";
            }

            //set up your streams for decryption
            ByteArrayInputStream encInputStream = new ByteArrayInputStream(Base64.decode(encryptedText.toCharArray()));
            ByteArrayOutputStream plainTextOutputStream = new ByteArrayOutputStream(1024 * 10);
            String unencryptedString = "";

            System.out.println("RsaExecutor.decryptString "+(System.currentTimeMillis()-start));

            //main aes decrypt function
            AESEncryptDecrypt.aesDecrypt(encInputStream,
                    aesKeyChar,
                    ivs,
                    AESEncryptDecrypt.AESCipherType.AES_CBC_PKCS5PADDING,
                    plainTextOutputStream);

            try
            {
                //convert decrypted outputstream to a string
                unencryptedString = new String(plainTextOutputStream.toByteArray(),"UTF-8");
            } catch (UnsupportedEncodingException e)
            {
                Log.e(TAG, e.getMessage(), e);
                return "";
            }

           return unencryptedString;

        }
        return "";
    }


}
