package com.tjba.comparecimento.exception;

/**
 * Exceção para regras de negócio.
 */
public class BusinessException extends RuntimeException {

    private String code;
    private Object[] parameters;

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(String code, String message, Object... parameters) {
        super(message);
        this.code = code;
        this.parameters = parameters;
    }

    public BusinessException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public Object[] getParameters() {
        return parameters;
    }
}