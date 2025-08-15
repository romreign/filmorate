package com.example.filmorate.util;

import com.example.filmorate.exception.InvalidIdException;

public class IdValidation {
    public static void validateIdExists (long id) {
        if (id < 1)
            throw new InvalidIdException("The value of id cannot be less than 1");
    }
}
