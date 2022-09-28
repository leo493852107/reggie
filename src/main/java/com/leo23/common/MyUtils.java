package com.leo23.common;

import java.io.File;

/**
 * 我的工具类
 */
public class MyUtils {
    // 删除文件, 文件或文件夹都好使
    public boolean deleteFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return false;
        }
        if (file.isFile()) {
            return file.delete();
        }
        File[] files = file.listFiles();
        for (File f : files) {
            if (f.isFile()) {
                if (!f.delete()) {
                    System.out.println(f.getAbsolutePath() + " delete error!");
                    return false;
                }
            } else {
                if (!this.deleteFile(f.getAbsolutePath())) {
                    return false;
                }
            }
        }
        return file.delete();
    }
}
