package pl.milosz000.github.user.repo.viewer.controller.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.milosz000.github.user.repo.viewer.exceptions.GitHubUnauthorizeException;
import pl.milosz000.github.user.repo.viewer.exceptions.GitHubUserNotFoundException;
import pl.milosz000.github.user.repo.viewer.exceptions.dto.ApiExceptionDTO;

@ControllerAdvice
public class ExceptionAdviceController {

    @ExceptionHandler(GitHubUserNotFoundException.class)
    public ResponseEntity<ApiExceptionDTO> handleUserNotFoundException(GitHubUserNotFoundException e) {
        return ExceptionResponseHelper.response(e, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(GitHubUnauthorizeException.class)
    public ResponseEntity<ApiExceptionDTO> handleUnauthorizedException(GitHubUnauthorizeException e) {
        return ExceptionResponseHelper.response(e, HttpStatus.UNAUTHORIZED);
    }
}
