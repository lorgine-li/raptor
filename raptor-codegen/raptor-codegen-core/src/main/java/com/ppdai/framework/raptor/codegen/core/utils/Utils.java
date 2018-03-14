package com.ppdai.framework.raptor.codegen.core.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

/**
 * Created by zhongyi on 2018/1/17.
 */
public class Utils {
    private static final Logger LOG = LoggerFactory.getLogger(Utils.class);
    /**
     * Collect all file whose extension matches from "input" into allProtoFile.
     *
     * @param input     A dirctory: collect all the files in it
     *                   A file:collect it if its' extention matches.
     * @param extension which kind of files you want to collect. ".proto","txt" or so.
     * @param allProtoFile collect all the qualified files into this list
     */
    public static void collectSpecificFiles(File input, String extension, List<File> allProtoFile) {
        if (input != null) {
            if (input.isDirectory()) {
                File[] fileArray = input.listFiles();
                if (fileArray != null) {
                    for (int i = 0; i < fileArray.length; i++) {
                        collectSpecificFiles(fileArray[i],extension,allProtoFile);
                    }
                }
            } else {
                if (StringUtils.endsWith(input.getName(), extension)) {
                    allProtoFile.add(input);
                }
            }
        }
    }

    /**
     * Copy a file from one place to another.
     * Attention. The PATH should be A FILE instead of a FOLDER.
     *
     * */
    public static void copyFile(String oldPath, String newPath)throws Exception {
        try {
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) {
                InputStream inStream = new FileInputStream(oldPath);
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                while ((byteread = inStream.read(buffer)) != -1) {
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            LOG.warn(e.toString());
            throw new Exception("Exception at Method copyFile.",e);
        }
    }

    /**
     * return the file path of CLI JAR
     * */
    public static String getCliJarPath(Class target) {
        java.net.URL url = target.getProtectionDomain().getCodeSource().getLocation();
        String filePath = null;
        try {
            filePath = java.net.URLDecoder.decode(url.getPath(), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (filePath.endsWith(".jar")) {
            filePath = filePath.substring(0, filePath.lastIndexOf("/") + 1);
        }
        File file = new File(filePath);
        filePath = file.getAbsolutePath();
        return filePath;
    }
    public static String getRealPath(Class target) {
        String realPath = target.getClassLoader().getResource("").getFile();
        File file = new File(realPath);
        realPath = file.getAbsolutePath();
        try {
            realPath = java.net.URLDecoder.decode(realPath, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return realPath;
    }
}
