package com.leo23.test;

import org.junit.jupiter.api.Test;

import java.util.UUID;

public class UploadFileTest {
    @Test
    public void test1() {
        // 原始文件名
        String originalFilename = "abc.jpg";
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        // 新文件名称
        String fileName = UUID.randomUUID().toString() + suffix;
        System.out.println(fileName);

    }
}
