package org.tinygame.herostory.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * Author: ljf
 * CreatedAt: 2021/4/13 上午9:36
 * 命名空间实用工具
 */
public final class PackageUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(PackageUtil.class);

    private PackageUtil() {
    }

    /**
     * 列出指定包中所有的子类
     *
     * @param packageName
     * @param recursive
     * @param superClazz
     * @return
     */
    public static Set<Class<?>> listSubClazz(String packageName, boolean recursive, Class<?> superClazz) {
        if (superClazz == null) {
            return Collections.EMPTY_SET;
        }
        return listClazz(packageName, recursive, superClazz::isAssignableFrom);
    }

    private static Set<Class<?>> listClazz(String packageName, boolean recursive, IClassFilter filter) {

        if (packageName == null || packageName.isEmpty()) {
            return Collections.EMPTY_SET;
        }

        // 将点转换为斜杠
        final String packagePath = packageName.replace(".", "/");

        // 获取类加载器
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

        // 结果集合
        HashSet<Class<?>> resultSet = new HashSet<>();

        try {
            // 获取 URL
            Enumeration<URL> urlEnum = contextClassLoader.getResources(packagePath);

            while (urlEnum.hasMoreElements()) {
                // 获取当前的 currUrl
                URL currUrl = urlEnum.nextElement();
                // 获取协议文本
                final String protocol = currUrl.getProtocol();
                // 定义临时集合
                Set<Class<?>> temSet = null;

                if ("FILE".equalsIgnoreCase(protocol)) {
                    // 从文件系统中加载类
                    temSet = listClazzFromDir(new File(currUrl.getFile()), packageName, recursive, filter);
                } else if ("JAR".equalsIgnoreCase(protocol)) {
                    // 获取文件字符串
                    String fileStr = currUrl.getFile();

                    if (fileStr.startsWith("file:")) {
                        // 如果是以 "file:" 开头的，删除这个开头
                        fileStr = fileStr.substring(5);
                    }

                    if (fileStr.lastIndexOf("!") > 0) {
                        // 如果有 "!" 字符，截断 "!" 之后的所有字符
                        fileStr = fileStr.substring(0, fileStr.lastIndexOf("!"));
                    }

                    // 从 jar 文件中加载类
                    temSet = listClazzFromJar(new File(fileStr), packageName, recursive, filter);
                }
                if (temSet != null) {
                    resultSet.addAll(temSet);
                }
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return resultSet;
    }

    private static Set<Class<?>> listClazzFromJar(final File jarFilePath, String packageName, boolean recursive, IClassFilter filter) {
        if (jarFilePath == null || jarFilePath.isDirectory()) {
            return Collections.EMPTY_SET;
        }

        // 结果对象
        Set<Class<?>> resultSet = new HashSet<>();

        try {
            // 创建 .jar 文件输入流
            JarInputStream jarIn = new JarInputStream(new FileInputStream(jarFilePath));
            // 进入点
            JarEntry entry;

            while ((entry = jarIn.getNextJarEntry()) != null) {
                if (entry.isDirectory()) {
                    continue;
                }

                // 获取进入点名称
                String entryName = entry.getName();

                if (!entryName.endsWith(".class")) {
                    continue;
                }

                if (!recursive) {
                    // 如果没有开启递归，就需要判断当前 .class 文件是否在指定目录下
                    // 获取目录名称
                    String tmpStr = entryName.substring(0, entryName.lastIndexOf('/'));
                    //将目录中的 "/" 全部替换为 "."
                    tmpStr = join(tmpStr.split("/"), ".");

                    if (!packageName.equals(tmpStr)) {
                        // 如果报名和目录名字不一样，跳过
                        continue;
                    }
                }

                String clazzName;

                // 清除最后的 .class 尾巴
                clazzName = entryName.substring(0, entryName.lastIndexOf('.'));
                // 将所有的 / 改为 .
                clazzName = join(clazzName.split("/"), ".");
                // 加载类定义
                Class<?> aClass = Class.forName(clazzName);

                if (null != filter && !filter.accept(aClass)) {
                    continue;
                }
                resultSet.add(aClass);
            }
            // 关闭 jar 输入流
            jarIn.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return resultSet;
    }

    /**
     * 从目录中获取类列表
     *
     * @param dirFile     目录
     * @param packageName 包名称
     * @param recursive   是否递归查询子包
     * @param filter      类过滤器
     * @return 符合条件的类集合
     */
    private static Set<Class<?>> listClazzFromDir(final File dirFile, final String packageName, final boolean recursive, IClassFilter filter) {
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return Collections.EMPTY_SET;
        }

        File[] subFileArr = dirFile.listFiles();
        if (subFileArr == null || subFileArr.length <= 0)
            return Collections.EMPTY_SET;

        // 文件队列，将子文件队列添加到队列
        Queue<File> fileQ = new LinkedList<>(Arrays.asList(subFileArr));

        // 结果对象
        Set<Class<?>> resultSet = new HashSet<>();

        while (!fileQ.isEmpty()) {
            // 从队列中获取文件
            File currFile = fileQ.poll();
            if (currFile.isDirectory() && recursive) {
                subFileArr = currFile.listFiles();
                if (subFileArr != null && subFileArr.length > 0) {
                    fileQ.addAll(Arrays.asList(subFileArr));
                }
                continue;
            }

            if (!currFile.isFile() || !currFile.getName().endsWith(".class")) {
                continue;
            }

            // 类名称
            String clazzName;
            // 设置类名称
            clazzName = currFile.getAbsolutePath();
            // 清除最后的 .class 尾巴
            clazzName = clazzName.substring(dirFile.getAbsolutePath().length(), clazzName.lastIndexOf('.'));
            // 转化目录斜杠
            clazzName = clazzName.replace('\\', '/');
            // 清除开头的 /
            clazzName = trimLeft(clazzName, "/");
            // 将所有的 / 修改为 .
            clazzName = join(clazzName.split("/"), ".");
            // 包名 + 类名
            clazzName = packageName + "." + clazzName;

            try {
                Class<?> aClass = Class.forName(clazzName);
                if (null != filter && !filter.accept(aClass)) {
                    continue;
                }
                resultSet.add(aClass);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return resultSet;
    }

    /**
     * 使用连接符连接字符串数组
     *
     * @param strArr 字符串数组
     * @param conn   连接符
     * @return 连接后的字符串
     */
    private static String join(String[] strArr, String conn) {
        if (null == strArr || strArr.length <= 0) {
            return "";
        }

        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < strArr.length; i++) {
            if (i > 0) {
                sb.append(conn);
            }
            sb.append(strArr[i]);
        }

        return sb.toString();
    }

    /**
     * 清除源字符串左边的字符串
     *
     * @param src     原字符串
     * @param trimStr 需要被清除的字符串
     * @return 清除后的字符串
     */
    private static String trimLeft(String src, String trimStr) {
        if (null == src || src.isEmpty()) {
            return "";
        }

        if (null == trimStr || trimStr.isEmpty()) {
            return "";
        }

        if (src.equals(trimStr)) {
            return "";
        }

        while (src.startsWith(trimStr)) {
            src = src.substring(trimStr.length());
        }
        return src;
    }

    /**
     * 类名称过滤器
     */
    @FunctionalInterface
    public interface IClassFilter {
        boolean accept(Class<?> clazz);
    }
}


