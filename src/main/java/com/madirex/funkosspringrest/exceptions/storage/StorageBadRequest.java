package com.madirex.funkosspringrest.exceptions.storage;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class StorageBadRequest extends StorageException {
    @Serial
    private static final long serialVersionUID = 81248974575434657L;

    public StorageBadRequest(String msg) {
        super(msg);
    }
}