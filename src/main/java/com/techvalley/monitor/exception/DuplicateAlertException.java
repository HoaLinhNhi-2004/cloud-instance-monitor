package com.techvalley.monitor.exception;

// ném khi alert cùng loại, cùng instance đã tồn tại và chưa resolve (business rule #2)
public class DuplicateAlertException extends RuntimeException {
    public DuplicateAlertException(String message) {
        super(message);
    }
}
