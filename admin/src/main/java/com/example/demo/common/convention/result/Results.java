package com.example.demo.common.convention.result;


import com.example.demo.common.convention.errorcode.BaseErrorCode;
import com.example.demo.common.convention.excetion.AbstractException;

/**
 * Author 70ash
 * Date 2024/1/24 22:54
 * Description:
 */
public class Results {
    public static <T> Result<T> success(T data) {
        return new Result<T>()
                .setData(data)
                .setCode(Result.SUCCESS_CODE);
    }

    public static Result<Void> success() {
        return new Result<Void>()
                .setCode(Result.SUCCESS_CODE);
    }

    public static Result<Void> fail(AbstractException ex) {
        return new Result<Void>()
                .setCode(ex.getErrorCode())
                .setMessage(ex.getErrorMessage());
    }

    public static Result<Void> fail() {
        return new Result<Void>()
                .setCode(BaseErrorCode.SERVICE_ERROR.code())
                .setMessage(BaseErrorCode.SERVICE_ERROR.message());
    }

    public static Result<Void> fail(String errorCode, String errorMessage) {
        return new Result<Void>()
                .setCode(errorCode)
                .setMessage(errorMessage);
    }
}
