package pl.milosz000.github.user.repo.viewer.controller.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.milosz000.github.user.repo.viewer.exceptions.dto.ApiExceptionDTO;

public class ExceptionResponseHelper {

    public static ResponseEntity<ApiExceptionDTO> response(Exception exception, HttpStatus httpStatus) {
        return new ResponseEntity<>(new ApiExceptionDTO(httpStatus, exception.getMessage()), httpStatus);
    }
}
