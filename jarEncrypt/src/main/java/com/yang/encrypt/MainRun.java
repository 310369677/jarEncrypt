package com.yang.encrypt;

import java.io.File;
import java.io.InputStream;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 描述:
 * 公司:jwell
 * 作者:杨川东
 * 日期:18-4-20
 */
public class MainRun {

    public static void main(String[] args) {
        Map<String, Set<String>> argsMap = parseArg(args);
        Set<String> fileDir = argsMap.get("--fileDir");
        Set<String> javaClass = argsMap.get("--classes");
        Set<String> jars = argsMap.get("--jars");
        boolean generateNew = argsMap.get("--new") != null;
        if (collectIsEmpty(fileDir) && collectIsEmpty(jars) && collectIsEmpty(javaClass)) {
            System.out.println("usage:java -jar jarEncrypt [[--fileDir][dirPath1 dirPath2] [--classes][classPath1 classPath2] [--jars][jarPath1 jarPath2]] [--new]");
            return;
        }
        System.out.println("处理请求开始--------------------------------------------------------------");
        handleDir(fileDir, generateNew);
        handleJavaClass(javaClass, generateNew);
        handleJars(jars, generateNew);
        System.out.println("处理请求结束--------------------------------------------------------------");
    }


    private static void handleJars(Set<String> jars, boolean generateNew) {
        if (collectIsEmpty(jars)) {
            return;
        }
        for (String jarPath : jars) {
            File file = new File(jarPath);
            handleSingleJar(file, generateNew);
        }
    }

    private static void handleSingleJar(File file, boolean generateNew) {

        try {
            InnerEncryptUtil.encryptJarFile(file, generateNew, new InnerEncryptUtil.DefaultJarFileHandleCallbackImpl() {
                @Override
                public void startHandleJar(JarFile file) {
                    System.out.println("开始处理:" + file.getName());
                }

                @Override
                public void startHandleClass(JarEntry classEntry) {
                    System.out.println("开始处理:" + classEntry.getName());
                }

                @Override
                public void endHandleClass(JarEntry classEntry) {
                    System.out.println("结束处理:" + classEntry.getName());
                }

                @Override
                public void endHandleJar(JarFile file) {
                    System.out.println("结束处理:" + file.getName());
                }
            });
        } catch (Exception e) {
            //ignore
        }

    }

    private static void handleJavaClass(Set<String> javaClass, boolean generateNew) {
        if (collectIsEmpty(javaClass)) {
            return;
        }
        for (String classPath : javaClass) {
            File file = new File(classPath);
            handleSingleClass(file, generateNew);
        }
    }

    private static void handleDir(Set<String> fileDir, boolean generateNew) {
        if (collectIsEmpty(fileDir)) {
            return;
        }
        for (String dirPath : fileDir) {
            File file = new File(dirPath);
            if (isDir(file)) {
                handleSingleDir(file, generateNew);
            }
        }
    }

    private static void handleSingleDir(File dir, boolean generateNew) {
        File[] listFiles = dir.listFiles();
        for (File file : listFiles) {
            if (isDir(file)) {
                handleSingleDir(file, generateNew);
            } else if (isClassFile(file)) {
                handleSingleClass(file, generateNew);
            } else if (isJarFile(file)) {
                handleSingleJar(file, generateNew);
            }
        }
    }

    /**
     * 解析参数
     *
     * @param args 参数的结合
     * @return 解析后的结合
     */
    private static Map<String, Set<String>> parseArg(String[] args) {
        Map<String, Set<String>> result = new HashMap<>();
        boolean putParamVal = false;
        String currentParamKey = null;
        for (String arg : args) {
            if (arg.startsWith("--")) {
                result.put(arg, new HashSet<String>());
                putParamVal = true;
                currentParamKey = arg;
            }
            if (putParamVal) {
                result.get(currentParamKey).add(arg);
            }
        }
        return result;
    }

    private static boolean collectIsEmpty(Collection collection) {
        return (collection == null || collection.isEmpty());
    }

    private static void handleSingleClass(File file, boolean generateNew) {
        try {
            InnerEncryptUtil.encryptClassFile(file, generateNew, null, null, new InnerEncryptUtil.DefaultClassFileHandleCallbackImpl() {
                @Override
                public void startHandleClass(InputStream in, String name) {
                    System.out.println("开始处理:" + name);
                }

                @Override
                public void endHandleClass(InputStream in, String name) {
                    System.out.println("处理结束:" + name);
                }
            });
        } catch (Exception e) {
            //ignore
        }
    }

    private static boolean isDir(File file) {
        return file.exists() && file.isDirectory();
    }

    private static boolean isClassFile(File file) {
        return file != null && file.exists() && !file.isDirectory() && file.getName().endsWith(".class");
    }

    private static boolean isJarFile(File file) {
        return file != null && file.exists() && !file.isDirectory() && file.getName().endsWith(".jar");
    }
}
