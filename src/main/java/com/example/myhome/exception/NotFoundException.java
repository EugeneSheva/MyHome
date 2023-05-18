package com.example.myhome.exception;

public class NotFoundException extends RuntimeException{
        public NotFoundException() {
            super("Object not found");
        }

}
