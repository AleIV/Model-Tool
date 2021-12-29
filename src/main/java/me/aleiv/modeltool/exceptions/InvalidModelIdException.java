package me.aleiv.modeltool.exceptions;

public class InvalidModelIdException extends Exception {

    public InvalidModelIdException(String modelId) {
        super("Invalid model Id: " + modelId);
    }

}
