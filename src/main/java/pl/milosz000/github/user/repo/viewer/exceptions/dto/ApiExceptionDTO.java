package pl.milosz000.github.user.repo.viewer.exceptions.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
public class ApiExceptionDTO {
    private HttpStatus status;
    private String message;
}
