package com.tjba.comparecimento.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Wrapper padrão para todas as respostas da API.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private List<String> errors;
    private int statusCode;
    private LocalDateTime timestamp;
    private PaginationInfo pagination;

    public ApiResponse() {
        this.timestamp = LocalDateTime.now();
    }

    // === SUCCESS RESPONSES ===
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = true;
        response.data = data;
        response.statusCode = HttpStatus.OK.value();
        return response;
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        ApiResponse<T> response = success(data);
        response.message = message;
        return response;
    }

    public static <T> ApiResponse<T> success(T data, PaginationInfo pagination) {
        ApiResponse<T> response = success(data);
        response.pagination = pagination;
        return response;
    }

    public static <T> ApiResponse<T> created(T data, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = true;
        response.data = data;
        response.message = message;
        response.statusCode = HttpStatus.CREATED.value();
        return response;
    }

    // === ERROR RESPONSES ===
    public static <T> ApiResponse<T> error(String message, HttpStatus status) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = false;
        response.message = message;
        response.statusCode = status.value();
        return response;
    }

    public static <T> ApiResponse<T> badRequest(String message) {
        return error(message, HttpStatus.BAD_REQUEST);
    }

    public static <T> ApiResponse<T> notFound(String message) {
        return error(message, HttpStatus.NOT_FOUND);
    }

    public static <T> ApiResponse<T> unauthorized(String message) {
        return error(message, HttpStatus.UNAUTHORIZED);
    }

    // === GETTERS E SETTERS ===
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    public List<String> getErrors() { return errors; }
    public void setErrors(List<String> errors) { this.errors = errors; }

    public int getStatusCode() { return statusCode; }
    public void setStatusCode(int statusCode) { this.statusCode = statusCode; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public PaginationInfo getPagination() { return pagination; }
    public void setPagination(PaginationInfo pagination) { this.pagination = pagination; }

    // === PAGINATION INFO ===
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PaginationInfo {
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;
        private boolean first;
        private boolean last;

        public PaginationInfo(int page, int size, long totalElements, int totalPages, boolean first, boolean last) {
            this.page = page;
            this.size = size;
            this.totalElements = totalElements;
            this.totalPages = totalPages;
            this.first = first;
            this.last = last;
        }

        // Getters
        public int getPage() { return page; }
        public int getSize() { return size; }
        public long getTotalElements() { return totalElements; }
        public int getTotalPages() { return totalPages; }
        public boolean isFirst() { return first; }
        public boolean isLast() { return last; }
    }
}