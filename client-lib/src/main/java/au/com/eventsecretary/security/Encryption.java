package au.com.eventsecretary.security;

import au.com.eventsecretary.UnexpectedSystemException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.crypto.spec.SecretKeySpec;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class Encryption {
    private final Base64.Decoder decoder = Base64.getDecoder();
    private final Base64.Encoder encoder = Base64.getEncoder();
    private final PublicKey publicKey;
    private final PrivateKey privateKey;
    private final OAEPParameterSpec oaepParams;

    public Encryption(String publicKeyValue, String privateKeyValue) {
        try {
            KeyFactory kf = KeyFactory.getInstance("RSA");

            byte[] keyBytes = decoder.decode(publicKeyValue);
            publicKey = kf.generatePublic(new X509EncodedKeySpec(keyBytes));

            keyBytes = decoder.decode(privateKeyValue);
            privateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(keyBytes));

            oaepParams = new OAEPParameterSpec(
                    "SHA-256",
                    "MGF1",
                    new MGF1ParameterSpec("SHA-256"),
                    PSource.PSpecified.DEFAULT
            );

        } catch (Exception e) {
            throw new UnexpectedSystemException(e);
        }

    }

    public String encrypt(String value) {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256); // for example
            SecretKey secretKey = keyGen.generateKey();

            byte[] ivBytes = new byte[16];
            new SecureRandom().nextBytes(ivBytes);
            IvParameterSpec iv = new IvParameterSpec(ivBytes);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            byte[] cipherText = cipher.doFinal(value.getBytes());
            String encryptedValue = encoder.encodeToString(cipherText);

            Cipher encryptCipher = Cipher.getInstance("RSA/ECB/OAEPPadding");

            encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey, oaepParams);
            byte[] encryptedMessageBytes = encryptCipher.doFinal(secretKey.getEncoded());
            String encryptedKey = encoder.encodeToString(encryptedMessageBytes);

            return encoder.encodeToString(ivBytes) + ":" + encryptedKey + ":" + encryptedValue;
        } catch (Exception e) {
            throw new UnexpectedSystemException(e);
        }
    }

    public String decrypt(String value) {
        try {
            String[] split = value.split(":");
            byte[] ivBytes = decoder.decode(split[0]);
            byte[] secretKeyEncrypted = decoder.decode(split[1]);
            byte[] valueBytes = decoder.decode(split[2]);


            Cipher encryptCipher = Cipher.getInstance("RSA/ECB/OAEPPadding");

            encryptCipher.init(Cipher.DECRYPT_MODE, privateKey, oaepParams);
            byte[] secretKeyBytes = encryptCipher.doFinal(secretKeyEncrypted);
            SecretKey secretKey = new SecretKeySpec(secretKeyBytes, 0, secretKeyBytes.length, "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(ivBytes);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
            byte[] cipherText = cipher.doFinal(valueBytes);
            return new String(cipherText);
        } catch (Exception e) {
            throw new UnexpectedSystemException(e);
        }
    }
}
