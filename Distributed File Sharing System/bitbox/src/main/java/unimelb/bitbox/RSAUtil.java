package unimelb.bitbox;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RSAUtil {

    private static String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA5pM2/OBWTWPIJ+CuXfah0CDnO14zWP++fshqrzzA7yfCond8dvwH0/PB4tVeOUBf5bdi7hekWP3sj46x5P+e17u/eF662hrB89UhyPgfW0z50Zwxng7tzzbTeiRZRasJhmRui24fw8/45KiavL8TdE/a1bcItm3xw0MXrLyWcr6RQQ8DXB68ubEMelkyFrpDfXLb5voomhj3/fnXmuehgDRY4uSYa2/06C/vc1iFFznDysoJgB1prILo0BORKUUZ+fzOpC8xtNkWBRti89sfOrN9Ay20HVeJYsi55XDyDSyodbKTYdQ3yXKBo7k/iyyPOVURXUfs2tkSUuKXmTXAlQIDAQAB";
    private static String privateKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDmkzb84FZNY8gn4K5d9qHQIOc7XjNY/75+yGqvPMDvJ8Kid3x2/AfT88Hi1V45QF/lt2LuF6RY/eyPjrHk/57Xu794XrraGsHz1SHI+B9bTPnRnDGeDu3PNtN6JFlFqwmGZG6Lbh/Dz/jkqJq8vxN0T9rVtwi2bfHDQxesvJZyvpFBDwNcHry5sQx6WTIWukN9ctvm+iiaGPf9+dea56GANFji5Jhrb/ToL+9zWIUXOcPKygmAHWmsgujQE5EpRRn5/M6kLzG02RYFG2Lz2x86s30DLbQdV4liyLnlcPINLKh1spNh1DfJcoGjuT+LLI85VRFdR+za2RJS4peZNcCVAgMBAAECggEBAIwAXUYELULGUHUuLpGtPCk0oVJh60mh15gBtw16FSfhe4PMi01v0Iz4mARUS+b1WIUR6cbpogfK/oVYPlnugBM705GR62CdGKEmC2KMjfWQ8qs8bMAtDmZgQt4KDg6IQLtSZfDRSAZMVDL60SHD31sjVZuojGjAryhBp7FhvPQj7KXJy/1Z/fW4Z8rb/L9gPyDbzuiOU/9/UBm+wWtVRyHDJ4n1sDTp2E6d6eqsHZaZ5umLBL1HvkSqb5wbeZKzJFggpwZM+2ttZq0nA6bUoM1OlmdKdvK4z/GGLBLWyAmn/Zodhtt/lD+6iJYTeqK6eU96LSPBjehpr00lwzqVX+0CgYEA/Nn98jiQY9fbmTC8mArmWV6GOjq5ofRb1DxYJa+wy7ZLUnGPc0ZKKurb0rEfRBH5QzkvpUIJD24C8vTLbLJJDoFRacXlUVNKAWg06OD28xD7KdJW/vDPpl0yAbSEhhOT6KdJ+9DxTReGEr9Lcxvbk1Fhlc1PHkdub63CIl+AYosCgYEA6XI2c8XutaBY0kEM6QaARaWFp8NJxFz0/VsNv10jCxaZhUY6Y1GqaPC8uH33WYONuwga0vHV4BLDBO0gkR85MWZ7jHPhwwrqnoHHj0kNJ6qguRuXI4kvQ5Bn1G7ld9q+D4iMceln2x/dDjpo0ItbvX87t9fAB2NyM+WgZptUbV8CgYAOfb/cHfnIfxqK0Qw9+oHxJUW0GKGC8qpAo6S5pDQRuMTgWLnL9X9Srlsi3Bvant0WSTS91+cFB10L55OxCxa8yhSMZ1cZLhjTs9E0d5Avpg1+/BsYSVzdQAIZrurZdE3Jy6ylzffGX07DzErasgIHk2ZwW2/pYFan9+FkbuzAuQKBgBRpGf2xVpemt11atqhBG0H7oN30IyT6A6mLJn6OxBuaFD4kz8ITR9T5B2cSDGhVKjUqFj5PSqXWvhpWKTzHABcjoLW9BAYrlCvbqPkMKAxJzNeiY+qFeg5sN8fJEmMSSv/MrorfH2d3N7qgvL1PEexVjYEbafy7YybKcuXFuvH9AoGBAPXxdGyOq3Uf1FU4wwKOaI5alcF7y6FVHsab3/Rs+5nVYHpuNCkJZDQs/3FMM0u9230fc0zUq/6LHQS2IF8pm2mtY5y7ZwUzUpAKX3//kzjoKGc0Wq1cLOi7vDkq1QpD7h+zJ9+8xKsRoPAPGFKMaJ963ZhAIxU3eakdPyuGZsKx"; 
  
    public static PublicKey getPublicKey(String base64PublicKey){
        PublicKey publicKey = null;
        try{
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(base64PublicKey.getBytes()));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            publicKey = keyFactory.generatePublic(keySpec);
            return publicKey;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return publicKey;
    }

    public static PrivateKey getPrivateKey(String base64PrivateKey){
        PrivateKey privateKey = null;
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(base64PrivateKey.getBytes()));
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            privateKey = keyFactory.generatePrivate(keySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return privateKey;
    }

    public static byte[] encrypt(String data, String publicKey) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(publicKey));
        return cipher.doFinal(data.getBytes());
    }

    public static String decrypt(byte[] data, PrivateKey privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return new String(cipher.doFinal(data));
    }

    public static String decrypt(String data, String base64PrivateKey) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        return decrypt(Base64.getDecoder().decode(data.getBytes()), getPrivateKey(base64PrivateKey));
    }

    public static void main(String[] args) throws IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, BadPaddingException {
        try {
            String encryptedString = Base64.getEncoder().encodeToString(encrypt("abcdefghijklmnopqrstuvwxyz", publicKey));
            System.out.println(encryptedString);
            String decryptedString = RSAUtil.decrypt(encryptedString, privateKey);
            System.out.println(decryptedString);
        } catch (NoSuchAlgorithmException e) {
            System.err.println(e.getMessage());
        }

    }
}

