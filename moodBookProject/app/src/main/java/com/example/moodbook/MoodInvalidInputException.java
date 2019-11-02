package com.example.moodbook;

public class MoodInvalidInputException extends Exception {
    private String input_type;

    public MoodInvalidInputException(String input_type, String message) {
        super(message);
        this.input_type = input_type;
    }

    public String getInputType() {
        return this.input_type;
    }
}
