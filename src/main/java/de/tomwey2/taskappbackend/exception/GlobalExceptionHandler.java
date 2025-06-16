package de.tomwey2.taskappbackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        // Wir holen uns alle Feld-Fehler aus der Exception
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            // Holen uns den Namen des Feldes
            String fieldName = ((FieldError) error).getField();
            // Holen uns die Fehlermeldung, die wir in der DTO-Annotation definiert haben
            String errorMessage = error.getDefaultMessage();
            // Fügen beides zur Map hinzu
            errors.put(fieldName, errorMessage);
        });

        // Geben die Map mit den Fehlern und dem HTTP-Status 400 zurück
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // Du kannst hier weitere @ExceptionHandler für andere Exceptions hinzufügen,
    // z.B. für deine ResourceNotFoundException, um auch diese einheitlich zu formatieren.
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleResourceNotFoundException(
            ResourceNotFoundException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
}