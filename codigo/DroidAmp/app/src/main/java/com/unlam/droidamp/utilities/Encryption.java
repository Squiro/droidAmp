package com.unlam.droidamp.utilities;

import android.content.Context;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;


import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Arrays;


import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.GCMParameterSpec;

public class Encryption {

    private static final String AndroidKeyStore = "AndroidKeyStore";
    private static final String AES_MODE = "AES/GCM/NoPadding";
    private static final String KEY_ALIAS = "droidAmpKey";
    private static final int TAG_LENGTH_BIT = 128;
    private static final int IV_LENGTH_BYTE = 12;
    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private KeyStore keyStore;
    private Cipher c;
    private SecureRandom secureRandom;

    public Encryption()
    {
        try {
            keyStore = KeyStore.getInstance(AndroidKeyStore);
            keyStore.load(null);
            secureRandom = new SecureRandom();
        }
        catch (Exception e) {
            Log.i("Exception", e.toString());
        }
    }

    public void generateKey(){
       try {
           if (!keyStore.containsAlias(KEY_ALIAS)) {
               KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, AndroidKeyStore);
               keyGenerator.init(
                       new KeyGenParameterSpec.Builder(KEY_ALIAS,
                               KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                               .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                               .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                               .setRandomizedEncryptionRequired(false)
                               .build());

               keyGenerator.generateKey();
           }
       } catch (Exception e)
       {
           Log.i("Exception", e.toString());
       }
    }

    private java.security.Key getSecretKey(Context context) throws Exception
    {
        return keyStore.getKey(KEY_ALIAS, null);
    }

    public String encrypt(Context context, String rawData)
    {
        try {
            // Generate new IV
            byte[] iv = new byte[IV_LENGTH_BYTE];
            secureRandom.nextBytes(iv);
            // Get cipher instance
            c = Cipher.getInstance(AES_MODE);
            // Init cihper with encrypt mode and newly generated IV
            c.init(Cipher.ENCRYPT_MODE, getSecretKey(context), new GCMParameterSpec(TAG_LENGTH_BIT, iv));
            // Cipher rawData
            byte[] encrypted = c.doFinal(rawData.getBytes(CHARSET));

            // Create a new byte buffer where we will store the encrypted data, with the IV at the beginning
            ByteBuffer byteBuffer = ByteBuffer.allocate(1 + iv.length + encrypted.length);
            byteBuffer.put(iv);
            byteBuffer.put(encrypted);
            return Base64.encodeToString(byteBuffer.array(), Base64.NO_WRAP);
        } catch (Exception e) {
            Log.i("Exception", e.toString());
        }
        return null;
    }

    public String decrypt(Context context, byte[] encryptedData)
    {
        try {
            ByteBuffer byteBuffer = ByteBuffer.wrap(Base64.decode(encryptedData, Base64.NO_WRAP));

            // Create a new byte with the iv length
            byte[] iv = new byte[IV_LENGTH_BYTE];
            // Get the iv from the encryptedData parameter
            byteBuffer.get(iv);
            // Get the encrypted data
            byte[] encrypted = new byte[byteBuffer.remaining()];
            byteBuffer.get(encrypted);

            // Decrypt data
            c = Cipher.getInstance(AES_MODE);
            c.init(Cipher.DECRYPT_MODE, getSecretKey(context), new GCMParameterSpec(TAG_LENGTH_BIT, iv));
            byte[] decrypted = c.doFinal(encrypted);

            // Paranoia
            Arrays.fill(iv, (byte) 0);
            Arrays.fill(encrypted, (byte) 0);

            // Return decrypted data as plain text
            return new String(decrypted, CHARSET);
        } catch (Exception e) {
            Log.i("Exception", e.toString());
        }
        return  null;
    }

}
