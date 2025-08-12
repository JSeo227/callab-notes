package dev.collab_sync.common.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseApi<T> {
    private Integer status;

    private boolean success;

    private String message;

    private T data;

    public static <T> ResponseApi<T> success(T data) {
        return ResponseApi.<T>builder()
                .success(true)
                .status(200)
                .message("success")
                .data(data)
                .build();
    }

    public static <T> ResponseApi<T> success(String message, T data) {
        return ResponseApi.<T>builder()
                .success(true)
                .status(200)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ResponseApi<T> error(T data) {
        return error(data, 500);
    }

    public static <T> ResponseApi<T> error(T data, Integer status) {
        return ResponseApi.<T>builder()
                .success(false)
                .status(status)
                .message("false")
                .data(data)
                .build();
    }

    public static <T> ResponseApi<T> error(String message) {
        return error(message, 500);
    }

    public static <T> ResponseApi<T> error(String message, Integer status) {
        return ResponseApi.<T>builder()
                .success(false)
                .status(status)
                .message(message)
                .data(null)
                .build();
    }
}
