package pl.maciejem.devicemanager.web.error;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pl.maciejem.devicemanager.web.exception.DeviceNotFoundException;
import pl.maciejem.devicemanager.web.exception.NotAllowedStatusToUpdateDeviceStatus;
import pl.maciejem.devicemanager.web.exception.WrongSecretKeyException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class RestResponseEntityExceptionHandler
        extends ResponseEntityExceptionHandler {

    @ExceptionHandler(WrongSecretKeyException.class)
    public ResponseEntity<Object> wrongSecretKeyException(WrongSecretKeyException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return handleExceptionInternal(ex, errors, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @Override
    public ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        Throwable mostSpecificCause = ex.getMostSpecificCause();
        String errorMessage;
        if (mostSpecificCause != null) {
            errorMessage = mostSpecificCause.getMessage();
        } else {
            errorMessage = ex.getMessage();
        }
        return handleExceptionInternal(ex, errorMessage, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(DeviceNotFoundException.class)
    public ResponseEntity<Object> handleNotFound(final DeviceNotFoundException deviceNotFoundException, final WebRequest request) {
        return handleExceptionInternal(deviceNotFoundException, deviceNotFoundException.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(NotAllowedStatusToUpdateDeviceStatus.class)
    public ResponseEntity<String> notAllowedStatusUpdateException(NotAllowedStatusToUpdateDeviceStatus ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleServletRequestBindingException(ServletRequestBindingException ex,
                                                                          HttpHeaders headers, HttpStatus status, WebRequest request) {
        return new ResponseEntity<>("Missing secret key", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex) {
        String message =
                ex.getName() + " should be of type " + ex.getRequiredType().getName();
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }
}