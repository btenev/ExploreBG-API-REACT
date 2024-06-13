package bg.exploreBG.config;

import bg.exploreBG.exception.AppException;
import bg.exploreBG.model.dto.ErrorDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(value = {AppException.class})
    public ResponseEntity<ErrorDto> handleException(AppException ex) {
        return ResponseEntity.status(ex.getCode()).body(new ErrorDto(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, List<String>>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String,List<String>> errorMap = new LinkedHashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> {
                    String current = error.getField();
                    errorMap.putIfAbsent(current, new ArrayList<>());

                    if (errorMap.containsKey(current)) {
                        List<String> errorList = errorMap.get(current);
                        errorList.add(error.getDefaultMessage());
                    }
                });

        return ResponseEntity.status(ex.getStatusCode()).body(errorMap);
    }
}

