package br.com.fiap.mslogin.config;

import br.com.fiap.mslogin.exception.ApiErrorResponse;
import br.com.fiap.mslogin.exception.UnauthorizedException;
import br.com.fiap.mslogin.exception.UserException;
import br.com.fiap.mslogin.exception.ValidationErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ControllerExceptionHandlerTest {

    @Test
    public void test_handleUnauthorizedException_returnsCorrectApiErrorResponse() {
        ControllerExceptionHandler handler = new ControllerExceptionHandler();
        UnauthorizedException exception = new UnauthorizedException(401, "Unauthorized access");

        ResponseEntity<ApiErrorResponse> response = handler.handleUnauthorizedException(exception);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Unauthorized access", response.getBody().message());
        assertEquals(401, response.getBody().status());
    }

    @Test
    public void test_response_entity_with_correct_status_and_message() {
        ControllerExceptionHandler handler = new ControllerExceptionHandler();
        UserException userException = new UserException(404, "User not found");

        ResponseEntity<ApiErrorResponse> response = handler.handleUserException(userException);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found", response.getBody().message());
        assertEquals(404, response.getBody().status());
    }

    @Test
    public void test_handle_validation_exception_with_errors() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        List<ObjectError> errors = List.of(new ObjectError("field", "error message"));

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(errors);

        ControllerExceptionHandler handler = new ControllerExceptionHandler();
        ResponseEntity<ValidationErrorResponse> response = handler.handleValidationException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().errors().size());
        assertEquals("error message", response.getBody().errors().get(0));
    }

    @Test
    public void test_handle_json_errors_returns_error_response() {
        ControllerExceptionHandler handler = new ControllerExceptionHandler();
        HttpMessageNotReadableException exception = new HttpMessageNotReadableException("Invalid JSON format");

        ResponseEntity<Map<String, String>> response = handler.handleJsonErrors(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid JSON format", response.getBody().get("error"));
    }
}
