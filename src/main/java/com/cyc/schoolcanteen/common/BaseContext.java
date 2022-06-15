package com.cyc.schoolcanteen.common;

/**
 * @author 虚幻的元亨利贞
 * @Description
 * @date 2022-05-19 12:59
 */
public class BaseContext {
    private static final ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setId(Long id) {
        threadLocal.set(id);
    }

    public static Long getId() {
        return threadLocal.get();
    }
}