package com.leo23.common;

/**
 * https://blog.csdn.net/qq_52574792/article/details/126817960
 * 基于threadLocal封装工具类，用于保存和获取当前登录用户id
 */

public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrentId() {
        return threadLocal.get();
    }


}
