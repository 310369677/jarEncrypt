# jarEncrypt
可以针对jar包单个class进行加密
# 基础的用法
## 1作为jar包使用
usage:java -jar jarEncrypt-1.0.jar [[--fileDir][dirPath1 dirPath2] [--classes][classPath1 classPath2] [--jars][jarPath1 jarPath2]] [--new] 
##### 参数说明:
fileDir:需要加密jar或class文件所在的目录
<br>
classes:需要的加密的class文件
<br>
jars:需要加密的jar包的路径
<br>
new:是否会产生新的文件，如果存在这个参数,工具会自动为你生成新的文件，否则将会覆盖原文件.
##### 示例：
1 对文件夹中的jar或者class进行加密:
<br>
java -jar jarEncrypt-1.0.jar --fileDir ./dirEnTest/ --new  
<br>
2 对jar包进行加密：
<br>
java -jar jarEncrypt-1.0.jar --jars  /home/yang/dirEnTest/hello-world-1.0-SNAPSHOT.jar  --new
<br>
3 对class文件进行加密:
<br>
java -jar jarEncrypt-1.0.jar --classes  /home/yang/dirEnTest/MainTest.class --new
## 2作为第三方jar包使用:
```
   EncryptApi encryptApi= JarOrClassEncrypt.newInstance();
        File file=new File("/home/yang/hello-world-1.0-SNAPSHOT.jar");
        encryptApi.encryptJarFile(file, true, new EncryptApi.JarFileHandleCallback() {
            @Override
            public void startHandleJar(JarFile jarFile) {
                System.out.println("开始处理文件:"+jarFile.getName());
            }

            @Override
            public void startHandleClass(JarEntry jarEntry) {
                System.out.println(jarEntry.getName());
            }

            @Override
            public void endHandleClass(JarEntry jarEntry) {

            }

            @Override
            public void startHandleOtherFile(JarEntry jarEntry) {

            }

            @Override
            public void endHandleOtherFile(JarEntry jarEntry) {

            }

            @Override
            public void endHandleJar(JarFile jarFile) {
                System.out.println("结束处理文件:"+jarFile.getName());
            }
        });
```