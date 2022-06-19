package com.cyc.schoolcanteen.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 虚幻的元亨利贞
 * @Description
 * @date 2022-05-12 17:37
 */
@Data
public class Result<T> implements Serializable {
    private int code;
    private String msg;
    private T data;

    public static <T> Result<T> success(T object){
        Result<T> result = new Result<>();
        result.code = 1;
        result.data = object;

        return result;
    }

    public static <T> Result<T> error(String msg){
        Result<T> result = new Result<>();
        result.code = 0;
        result.msg = msg;

        return result;
    }
}
