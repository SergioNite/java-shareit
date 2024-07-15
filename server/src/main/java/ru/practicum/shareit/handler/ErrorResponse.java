package ru.practicum.shareit.handler;

public class ErrorResponse {
    private final String error;
    private final String description;

    public ErrorResponse(String title, String description) {
        this.error = title;
        this.description = description;
    }

    public String getError() {
        return error;
    }

    public String getDescription() {
        return description;
    }
}
