package com.yang.encrypt;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public interface EncryptApi {

    /**
     * 加密class文件,并且在当前文件所在目录产生一个新的class文件
     *
     * @param file                    class文件
     * @param classFileHandleCallback 回调函数
     */
    void encryptClassFile(File file, ClassFileHandleCallback classFileHandleCallback);

    /**
     * 加密class文件
     *
     * @param file                    class文件
     * @param generateNewFile         是否产生新的文件
     * @param name                    新文件的名字
     * @param destPath                新文件产生的路劲,可以为空，如果为空，就会在当前文件所在目录产生一个新的文件
     * @param classFileHandleCallback 回调函数
     */
    void encryptClassFile(File file, boolean generateNewFile, String name, String destPath, ClassFileHandleCallback classFileHandleCallback);


    /**
     * 通过class文件的路径进行加密
     *
     * @param path     class文件的路劲
     * @param callback 回调
     */
    void encryptClassFile(String path, ClassFileHandleCallback callback);


    /**
     * 过class文件的路径进行加密
     *
     * @param path                    原始class文件的路径
     * @param generateNewFile         是否产生新的文件
     * @param name                    新文件的名字，如果为空，将由系统生成文件
     * @param destPath                新文件产生的路劲,可以为空，如果为空，就会在当前文件所在目录产生一个新的文件
     * @param classFileHandleCallback 回到
     */
    void encryptClassFile(String path, boolean generateNewFile, String name, String destPath, ClassFileHandleCallback classFileHandleCallback);

    /**
     * 加密class文件流，输出到对应的路劲
     *
     * @param classInputStream class文件流
     * @param fileName         产生的文件名字
     * @param destPath         目标路劲
     * @param callback         回调函数
     */
    void encryptClassInputStream(InputStream classInputStream, String fileName, String destPath, ClassFileHandleCallback callback);


    /**
     * 加密class inputStream,输出对应的流
     *
     * @param in                      class字节流
     * @param classFileHandleCallback 回到函数
     * @return 输出的流
     */
    OutputStream encryptClassInputStream(InputStream in, ClassFileHandleCallback classFileHandleCallback);


    /**
     * 加密jar文件
     *
     * @param file                  jar文件
     * @param generateNew           是否产生新的jar
     * @param jarFileHandleCallback 回调函数
     */
    void encryptJarFile(File file, boolean generateNew, JarFileHandleCallback jarFileHandleCallback);


    interface JarFileHandleCallback {
        void startHandleJar(JarFile jarFile);

        void startHandleClass(JarEntry classEntry);

        void endHandleClass(JarEntry classEntry);

        void startHandleOtherFile(JarEntry otherEntry);

        void endHandleOtherFile(JarEntry otherEntry);

        void endHandleJar(JarFile jarFile);
    }


    interface ClassFileHandleCallback {

        /**
         * 在文件加密前进行处理
         *
         * @param in   输入流
         * @param name 文件的名字（如果存在的话）
         */
        void startHandleClass(InputStream in, String name);

        /**
         * 在文件加密后进行处理
         *
         * @param in   输入流
         * @param name 文件的名字（如果存在的话）
         */
        void endHandleClass(InputStream in, String name);
    }

}
