package ru.practicum.shareit.exception;

public class UnknownStateException extends RuntimeException {

    private final String wrongState;

    public UnknownStateException(String stringState) {
        wrongState = stringState;
    }

    public String getWrongState() {
        return wrongState;
    }
}
