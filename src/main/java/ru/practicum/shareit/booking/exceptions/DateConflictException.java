package ru.practicum.shareit.booking.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class DateConflictException extends RuntimeException {
    public DateConflictException(String message) {
        super(message);
    }
}
