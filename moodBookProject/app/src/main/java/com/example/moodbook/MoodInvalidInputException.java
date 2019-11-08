package com.example.moodbook;

/**
 * This Exception class is used by Mood to ensure inputs for Mood fields are valid
 */
public class MoodInvalidInputException extends Exception {
    private String input_type;

    /**
     * This constructor is used by Mood to throw customized exception
     * @param input_type
     *  This is the type of input where error occurs
     * @param message
     *  This is the error message
     */
    public MoodInvalidInputException(String input_type, String message) {
        super(message);
        this.input_type = input_type;
    }

    @Deprecated
    public String getInputType() {
        return this.input_type;
    }
}
