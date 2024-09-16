package com.cmpeq0.controlsystem.exception;

public class DataException extends Exception {

    public DataException() {
        super("Entity exception occurred");
    }

    public DataException(String message) {
        super("Entity exception occurred :: " + message);
    }

}
