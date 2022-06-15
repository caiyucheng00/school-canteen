package com.cyc.schoolcanteen.exception;

import com.cyc.schoolcanteen.dto.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;


/**
 * @author 虚幻的元亨利贞
 * @Description
 * @date 2022-05-16 10:51
 */
@Slf4j
@RestControllerAdvice(annotations = {RestController.class, Controller.class})
public class GlobalExceptionHandler {

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public Result<String> exceptionHandler(SQLIntegrityConstraintViolationException e){
        log.info(e.getMessage());
        String msg = e.getMessage().split("'")[1];

        return Result.error(msg + "\t已存在");
    }

    @ExceptionHandler(CustomException.class)
    public Result<String> exceptionHandler(CustomException e){
        log.info(e.getMessage());
        return Result.error(e.getMessage());
    }
}
