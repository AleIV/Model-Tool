package me.aleiv.modeltool.exceptions;

public class InvalidAnimationException extends Exception {

    public InvalidAnimationException(String modelId, String animation) {
        super("Invalid animation \"" + animation + "\" for model \"" + modelId + "\"");
    }

}
