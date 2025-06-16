package de.tomwey2.taskappbackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Diese Annotation sorgt dafür, dass Spring automatisch einen HTTP 404 Status zurückgibt,
// wenn diese Exception vom Controller nicht anders behandelt wird.
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}