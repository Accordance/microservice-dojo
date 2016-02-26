package svcdojo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@SuppressWarnings({ "rawtypes", "unchecked" })
@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler
  @ResponseBody
  ResponseEntity handleValidationException(AccountException ex) {
    return new ResponseEntity(ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

}