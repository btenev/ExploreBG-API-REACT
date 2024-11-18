package bg.exploreBG.config;

import bg.exploreBG.exception.AppException;
import bg.exploreBG.model.dto.ErrorDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(value = {AppException.class})
    public ResponseEntity<ErrorDto> handleException(AppException ex) {
        return ResponseEntity.status(ex.getCode()).body(new ErrorDto(ex.getMessage()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, List<String>>> handleValidationMultipartFileErrors(ConstraintViolationException ex) {
        Map<String, List<String>> errorMap = new HashMap<>();

        List<String> collect =
                ex.getConstraintViolations().
                        stream()
                        .map(ConstraintViolation::getMessage)
                        .collect(Collectors.toList());

        errorMap.put("errors", collect);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMap);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, List<String>>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, List<String>> errorMap = new HashMap<>();

        List<String> errorList = ex
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        errorMap.put("errors", errorList);

        return ResponseEntity.status(ex.getStatusCode()).body(errorMap);
    }
}

