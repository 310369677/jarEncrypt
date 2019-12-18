package com.yang.encrypt;

import java.io.*;
import java.util.Enumeration;
import java.util.Random;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

/**
 * 描述:
 * 公司:jwell
 * 作者:杨川东
 * 日期:18-4-18
 */
class InnerEncryptUtil {

    static void encryptClassFile(File file, boolean generateNew, String newName, String destPath, EncryptApi.ClassFileHandleCallback classFileHandleCallback) throws IOException {
        assertIsClassFile(file);
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        //回调函数
        EncryptApi.ClassFileHandleCallback callback = new DefaultClassFileHandleCallbackImpl();
        if (classFileHandleCallback != null) {
            callback = classFileHandleCallback;
        }
        String newClassFileName = deleteSuffix(file.getName(), ".class") + "_generate.class";
        if ((newName != null) && (!"".equals(newName))) {
            newClassFileName = newName;
        }
        File newClassFile = new File(file.getParent(), newClassFileName);
        if ((destPath != null) && (!"".equals(destPath))) {
            newClassFile = new File(destPath);
        }
        try {
            fileInputStream = new FileInputStream(file);
            ByteArrayOutputStream fileContentByte = new ByteArrayOutputStream(fileInputStream.available());
            in2out(fileInputStream, fileContentByte);
            callback.startHandleClass(fileInputStream, file.getName());
            Random random = new Random(System.currentTimeMillis());
            byte[] newContent = encryptClass(fileContentByte.toByteArray(), random.nextInt(0xff) + 1);
            fileOutputStream = new FileOutputStream(newClassFile);
            fileOutputStream.write(newContent);
            if (!generateNew && !newClassFile.renameTo(file)) {
                throw new IOException("写入原始class文件失败:" + file.getName());
            }
            callback.endHandleClass(fileInputStream, file.getName());
            close(fileContentByte);
        } finally {
            close(fileOutputStream);
            close(fileInputStream);
        }
    }


    static void encryptClassInputStream(InputStream in, String fileName, String destPath, EncryptApi.ClassFileHandleCallback callback) throws IOException {
        File file = new File(destPath);
        if (!file.isDirectory()) {
            throw new RuntimeException("this is a not directory");
        }
        file = new File(destPath, fileName);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        try {
            ByteArrayOutputStream fileContentByte = new ByteArrayOutputStream(in.available());
            in2out(in, fileContentByte);
            callback.startHandleClass(in, fileName);
            Random random = new Random(System.currentTimeMillis());
            byte[] newContent = encryptClass(fileContentByte.toByteArray(), random.nextInt(0xff) + 1);
            fileOutputStream.write(newContent);
            callback.endHandleClass(in, file.getName());
            close(fileContentByte);
        } finally {
            close(in);
            close(fileOutputStream);
        }

    }


    static void encryptJarFile(File file, boolean generateNew, EncryptApi.JarFileHandleCallback jarFileHandleCallback) throws IOException {
        assertIsJarFile(file);
        JarFile jarFile = null;
        //原文件名
        String sourceJarName = file.getName();
        //新的文件名
        String newJarName = deleteSuffix(sourceJarName, ".jar") + "_generate.jar";
        //jar文件处理回调函数
        EncryptApi.JarFileHandleCallback callback = new DefaultJarFileHandleCallbackImpl();
        if (jarFileHandleCallback != null) {
            callback = jarFileHandleCallback;
        }
        String sourceJarParent = file.getParent();
        File newJarFile = new File(sourceJarParent, newJarName);
        JarOutputStream newJar = null;
        try {
            jarFile = new JarFile(file);
            //新的jar包
            newJar = new JarOutputStream(new FileOutputStream(newJarFile));
            Enumeration<JarEntry> entries = jarFile.entries();
            ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
            callback.startHandleJar(jarFile);
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                String name = jarEntry.getName();
                InputStream inputStream = jarFile.getInputStream(jarEntry);
                in2out(inputStream, outBytes);
                byte[] writeData = outBytes.toByteArray();
                if (!jarEntry.isDirectory() && name.endsWith(".class")) {
                    //处理class文件
                    callback.startHandleClass(jarEntry);
                    //加密
                    Random random = new Random(System.currentTimeMillis());
                    writeData = encryptClass(writeData, random.nextInt(0xff) + 1);
                } else {
                    //处理非class的文件
                    callback.startHandleOtherFile(jarEntry);
                }
                JarEntry jarEntry1 = new JarEntry(name);
                newJar.putNextEntry(jarEntry1);
                newJar.write(writeData);
                outBytes.reset();
                if (!jarEntry.isDirectory() && name.endsWith(".class")) {
                    callback.endHandleClass(jarEntry);
                } else {
                    callback.endHandleOtherFile(jarEntry);
                }
            }
            if (!generateNew && !newJarFile.renameTo(file)) {
                throw new IOException("写入原始jar文件失败");
            }
            callback.endHandleJar(jarFile);
        } finally {
            close(newJar);
            close(jarFile);
        }

    }

    /**
     * 输入流到输出流，自己关闭所有的流
     *
     * @param in
     * @param callback
     * @return
     * @throws IOException
     */

    static OutputStream encryptClassInputStream(InputStream in, EncryptApi.ClassFileHandleCallback callback) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream(in.available());
        in2out(in, out);
        callback.startHandleClass(in, null);
        Random random = new Random(System.currentTimeMillis());
        byte[] newContent = encryptClass(out.toByteArray(), random.nextInt(0xff) + 1);
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        arrayOutputStream.write(newContent);
        close(out);
        return arrayOutputStream;
    }

    private static void in2out(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] buffer = new byte[4096];
        int length;
        while ((length = inputStream.read(buffer, 0, buffer.length)) != -1) {
            outputStream.write(buffer, 0, length);
        }
    }


    public static class DefaultClassFileHandleCallbackImpl implements EncryptApi.ClassFileHandleCallback {

        @Override
        public void startHandleClass(InputStream in, String name) {
            //默认空实现，子类重写定义
        }

        @Override
        public void endHandleClass(InputStream in, String name) {
            //默认空实现，子类重写定义
        }
    }


    public static class DefaultJarFileHandleCallbackImpl implements EncryptApi.JarFileHandleCallback {

        @Override
        public void startHandleJar(JarFile jarFile) {
            //默认空实现，子类重写定义
        }

        @Override
        public void startHandleClass(JarEntry classEntry) {
            //默认空实现，子类重写定义
        }

        @Override
        public void endHandleClass(JarEntry classEntry) {
            //默认空实现，子类重写定义
        }

        @Override
        public void startHandleOtherFile(JarEntry otherEntry) {
            //默认空实现，子类重写定义
        }

        @Override
        public void endHandleOtherFile(JarEntry otherEntry) {
            //默认空实现，子类重写定义
        }

        @Override
        public void endHandleJar(JarFile jarFile) {
            //默认空实现，子类重写定义
        }
    }

    static void assertIsJarFile(File file) {
        if (file == null || !file.exists() || file.isDirectory() || !file.getName().endsWith(".jar")) {
            throw new IllegalArgumentException("这不是一个有效的jar文件");
        }
    }

    static void assertIsClassFile(File file) {
        if (file == null || !file.exists() || file.isDirectory() || !file.getName().endsWith(".class")) {
            throw new IllegalArgumentException("这不是一个有效的class文件");
        }
    }

    private static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static byte[] encryptClass(byte[] source, int fillCount) {
        if (byte2Int(source) != 0xbebafeca) {
            return source;
        }
        int defaultFillCount = 10;
        //为填充次数赋值
        if (fillCount > 0 && fillCount <= 0xff) {
            defaultFillCount = fillCount;
        }
        byte[] values = generateFourNumber(defaultFillCount);
        byte[] result = new byte[source.length + defaultFillCount + 1];
        generateEncryptByte(values, source, result, defaultFillCount);
        return result;
    }

    public static byte[] decryptClass(byte[] source) {
        if (byte2Int(source) == 0xbebafeca) {
            byte[] result = new byte[source.length];
            System.arraycopy(source, 0, result, 0, source.length);
            return result;
        }
        //开始解码
        int fillCount = byteShowByInt(source[0]) + byteShowByInt(source[1]) - byteShowByInt(source[2]) - byteShowByInt(source[3]);
        //跳转的基数
        int val = byteShowByInt(source[4]);
        //原始字节码的长度
        int classContentLength = source.length - 5 - fillCount;
        byte[] result = new byte[classContentLength + 4];
        //写入class的头
        wirteClassHead(result);
        //开始读取真正的内容
        readClassContent(source, result, val, fillCount, classContentLength);
        return result;
    }

    private static void readClassContent(byte[] source, byte[] result, int val, int fillCount, int classContentLength) {
        int count = classContentLength / val;
        int contentFillCount = count < fillCount ? count : fillCount;
        int resultPos = 4;
        int sourcePos = 5;
        for (int i = 0; i < contentFillCount; i++) {
            System.arraycopy(source, sourcePos, result, resultPos, val);
            sourcePos += val;
            resultPos += val;
            sourcePos++;
        }
        if (count < fillCount) {
            System.arraycopy(source, sourcePos, result, resultPos, result.length - resultPos);
        } else {
            System.arraycopy(source, sourcePos, result, resultPos, source.length - sourcePos);
        }
    }

    private static void wirteClassHead(byte[] result) {
        //0xbebafeca
        result[0] = (byte) 0xca;
        result[1] = (byte) 0xfe;
        result[2] = (byte) 0xba;
        result[3] = (byte) 0xbe;
    }

    private static void generateEncryptByte(byte[] values, byte[] source, byte[] result, int defaultFillCount) {
        int classContentLength = source.length - 4;
        int newBytePos = 0;
        //copy头
        System.arraycopy(values, 0, result, newBytePos, values.length);
        newBytePos += values.length;
        //开始填充字节数据
        Random random = new Random(System.currentTimeMillis());
        int val = random.nextInt(0xff) + 1;
        /*
         * 写入填充的基数，每次跳转的基数，如val=9,则第一次填充的位置就是9，
         * 第二次就是在此位置上+9以此类推，直达填充次数被填满,如果填充到末尾，仍未填满填充次数
         * 后续的填充全部填在末尾
         *
         **/
        result[newBytePos++] = (byte) val;
        int count = classContentLength / val;
        int fillCount = count < defaultFillCount ? count : defaultFillCount;
        for (int i = 0; i < fillCount; i++) {
            System.arraycopy(source, 4 + val * i, result, newBytePos, val);
            newBytePos += val;
            result[newBytePos++] = (byte) random.nextInt(0xff);
        }
        if (count < defaultFillCount) {
            //还需要在末尾填充字节
            //1 先把剩余的字节copy过来
            System.arraycopy(source, 4 + val * fillCount, result, newBytePos, source.length - (4 + val * fillCount));
            newBytePos += source.length - (4 + val * fillCount);
            //2 在末尾填充字节
            for (int i = 0; i < (defaultFillCount - count); i++) {
                result[newBytePos++] = (byte) random.nextInt(0xff);
            }
        } else {
            System.arraycopy(source, 4 + val * fillCount, result, newBytePos, source.length - (4 + val * fillCount));
        }
    }

    /**
     * 产生4个整数，每个整数只能是一个字节，使他们满足1+2-3-4的值等于defaultFillCount
     *
     * @param defaultFillCount 填充的次数
     * @return 产生的数结果数组
     */
    private static byte[] generateFourNumber(int defaultFillCount) {
        byte[] result = new byte[4];
        Random random = new Random(System.currentTimeMillis());
        //产生1~255的数
        int firstVal = random.nextInt(0xff) + 1;
        int threeVal = random.nextInt(firstVal);
        //得到一个随机的正数 1～firstVal
        int oneThree = firstVal - threeVal;
        int difference = oneThree - defaultFillCount;
        int absDiff = Math.abs(difference);
        int absFDe = Math.abs(firstVal - defaultFillCount);
        int minFDe = Math.abs(1 - defaultFillCount);
        int maxAbsDiff = absFDe > minFDe ? absFDe : minFDe;
        int temp1 = maxAbsDiff + random.nextInt(0xff - maxAbsDiff);
        int temp2 = temp1 - absDiff;
        //赋值
        result[0] = (byte) firstVal;
        result[2] = (byte) threeVal;
        if (difference > 0) {
            result[1] = (byte) temp2;
            result[3] = (byte) temp1;
        } else {
            result[1] = (byte) temp1;
            result[3] = (byte) temp2;
        }
        return result;
    }


    public static int byte2Int(byte[] bytes) {
        trueAndThrows(bytes.length < 4, new Byte2NumberException("当前的字节长度:" + bytes.length + ",期望的字节长度大于等于4"));
        int a = bytes[0] & 0xFF;

        a |= ((bytes[1] << 8) & 0xFF00);

        a |= ((bytes[2] << 16) & 0xFF0000);

        a |= ((bytes[3] << 24) & 0xFF000000);
        return a;
    }

    public static void trueAndThrows(boolean express, RuntimeException e) {
        if (express) {
            throw e;
        }
    }

    public static short byte2Short(byte[] bytes) {
        trueAndThrows(bytes.length < 2, new Byte2NumberException("当前的字节长度:" + bytes.length + ",期望的字节长度大于等于2"));
        short a = (short) (bytes[0] & 0xFF);
        a |= ((bytes[1] << 8) & 0xFF00);
        return a;
    }

    private static int byteShowByInt(byte val) {
        return 0xff & val;
    }

    private static String deleteSuffix(String name, String suffix) {
        if (name == null || "".equals(name.trim()) || suffix == null || "".equals(suffix.trim())) {
            throw new IllegalArgumentException("名字和后缀不能接受空字符串");
        }
        int index = name.lastIndexOf(suffix);
        if (index == -1) {
            return name;
        }
        return name.substring(0, index);
    }
}
