package ru.practicum.shareit.user.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class DublicateEmailErrorException extends RuntimeException{
    public DublicateEmailErrorException(String message) {
        super(message);
    }
}
