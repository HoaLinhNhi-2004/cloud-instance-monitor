package com.techvalley.monitor.exception;

// ném khi cố xóa instance đang RUNNING (business rule #5)
public class ActiveInstanceException extends RuntimeException {
    public ActiveInstanceException(String message) {
        super(message);
    }
}
