package com.yang.encrypt;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class JarOrClassEncrypt implements EncryptApi {


    private JarOrClassEncrypt() {

    }


    public static JarOrClassEncrypt newInstance() {
        return new JarOrClassEncrypt();
    }

    @Override
    public void encryptClassFile(File file, ClassFileHandleCallback classFileHandleCallback) {
        encryptClassFile(file, true, null, null, classFileHandleCallback);
    }

    @Override
    public void encryptClassFile(File file, boolean generateNewFile, String name, String destPath, ClassFileHandleCallback classFileHandleCallback) {
        try {
            InnerEncryptUtil.encryptClassFile(file, generateNewFile, name, destPath, classFileHandleCallback);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void encryptClassFile(String path, ClassFileHandleCallback callback) {
        File file = new File(path);
        InnerEncryptUtil.assertIsClassFile(file);
        encryptClassFile(file, callback);
    }

    @Override
    public void encryptClassFile(String path, boolean generateNewFile, String name, String destPath, ClassFileHandleCallback classFileHandleCallback) {
        File file = new File(path);
        InnerEncryptUtil.assertIsClassFile(file);
        encryptClassFile(file, generateNewFile, name, destPath, classFileHandleCallback);
    }

    @Override
    public void encryptClassInputStream(InputStream classInputStream, String fileName, String destPath, ClassFileHandleCallback callback) {
        try {
            InnerEncryptUtil.encryptClassInputStream(classInputStream, fileName, destPath, callback);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public OutputStream encryptClassInputStream(InputStream in, ClassFileHandleCallback classFileHandleCallback) {
        try {
            return InnerEncryptUtil.encryptClassInputStream(in, classFileHandleCallback);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void encryptJarFile(File file, boolean generateNew, JarFileHandleCallback jarFileHandleCallback) {
        try {
            InnerEncryptUtil.encryptJarFile(file, generateNew, jarFileHandleCallback);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
