/**
 * Mini Password Manager
 *
 * Copyright (c) 2014-2015 Karthik M'lore
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.karthikmlore.minipasswordmanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import android.util.Base64;


public class Crypter {
    private Key key;

    public Crypter(String pattern) {
        String key_string = pattern.substring(10,26);
        byte[] keyBytes = key_string.getBytes(Charset.forName("UTF-8"));
        key = new SecretKeySpec(keyBytes, "AES");
    }

    public String encrypt(String text) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher c = Cipher.getInstance("AES");
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] textBytes = c.doFinal(text.getBytes());
        String cipherText = Base64.encodeToString(textBytes,Base64.DEFAULT);
        return cipherText;
    }

    public String decrypt(String cipherText) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] textBytes = cipher.doFinal(Base64.decode(cipherText,Base64.DEFAULT));
        String text = new String(textBytes);
        return text;
    }

    public boolean encryptDB(File in, File out) {
        FileInputStream ipStream;
        CipherOutputStream opStream;
        boolean status = false;
        try {
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.ENCRYPT_MODE, key);
            ipStream = new FileInputStream(in);
            opStream = new CipherOutputStream(new FileOutputStream(out), c);
            if(write(ipStream,opStream))
                status = true;
            ipStream.close();
            opStream.close();
        } catch(Exception e) {
        }
        return status;
    }

    public boolean decryptDB(File in, File out) {
        CipherInputStream ipStream;
        FileOutputStream opStream;
        boolean status = false;
        try {
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.DECRYPT_MODE, key);
            ipStream = new CipherInputStream(new FileInputStream(in), c);
            opStream = new FileOutputStream(out);
            if(write(ipStream,opStream))
                status = true;
            ipStream.close();
            opStream.close();
        } catch(Exception e) {
        }
        return status;
    }

    public boolean write(InputStream iStream, OutputStream oStream) {
        int i;
        byte[] b = new byte[1024];
        try {
            while((i=iStream.read(b))!=-1) {
                oStream.write(b, 0, i);
            }
        } catch(Exception e) {
            return false;
        }
        return true;
    }
}