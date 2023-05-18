package com.example.myhome.exception;

public class EmptyObjectException extends RuntimeException {
    public EmptyObjectException() {super("Empty object");}
    public EmptyObjectException(String msg) {super(msg);}
}
