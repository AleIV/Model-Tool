package me.aleiv.core.paper.exceptions;

public class InvalidModelIdException extends Exception {

    public InvalidModelIdException(String modelId) {
        super("Invalid model Id: " + modelId);
    }

}
