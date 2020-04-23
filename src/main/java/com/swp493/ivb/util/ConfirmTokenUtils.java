package com.swp493.ivb.util;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.swp493.ivb.common.user.EntityUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class ConfirmTokenUtils {
    @Autowired
    KeyPair keyPair;

    public String generateConfirmToken(EntityUser user) throws IllegalBlockSizeException, BadPaddingException,
            InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
        Cipher encryptCipher = Cipher.getInstance("RSA");
        encryptCipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
        String time = LocalDateTime.now().plusHours(1).atZone(ZoneId.systemDefault()).toString();
        String plainText = String.format("%50s%s", time, user.getId());
        byte[] cipherText = encryptCipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        return Base64.getUrlEncoder().withoutPadding().encodeToString(cipherText);
    }

    public String decodeConfirmToken(String token) throws InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        Cipher decryptCipher = Cipher.getInstance("RSA");
        decryptCipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
        String plainText = new String(decryptCipher.doFinal(Base64.getUrlDecoder().decode(token)),StandardCharsets.UTF_8);
        LocalDateTime expire = LocalDateTime.parse(plainText.substring(0, 50).trim().substring(0, 23));
        if(expire.isBefore(LocalDateTime.now())) throw new ResponseStatusException(HttpStatus.GONE);
        return plainText.substring(50);
    } 
}