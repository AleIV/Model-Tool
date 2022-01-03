package me.aleiv.modeltool.exceptions;

public class AlreadyUsedNameException extends Exception {

    public AlreadyUsedNameException(String name) {
        super("Name " + name + " is already used");
    }

}
