package br.com.microservice.pagamento.exception;

import br.com.microservice.pagamento.gateway.exception.mongo.PagamentoMongoError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Formato padrão para respostas de erro
    private Map<String, Object> createErrorResponse(HttpStatus status, String message, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("path", request.getDescription(false).replace("uri=", ""));
        return body;
    }

    // 404 - Cliente não encontrado
    @ExceptionHandler(PagamentoError.PagamentoNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleClienteNotFound(PagamentoError.PagamentoNotFoundException ex, WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(createErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request));
    }

    // 404 - Cliente não encontrado
    @ExceptionHandler(PagamentoMongoError.PagamentoNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleClienteNotFound(PagamentoMongoError.PagamentoNotFoundException ex, WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(createErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request));
    }

    // 409 - Cliente já existe
    @ExceptionHandler(PagamentoError.ClienteAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Object> handleClienteAlreadyExists(PagamentoError.ClienteAlreadyExistsException ex, WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(createErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), request));
    }

    // 400 - Argumentos inválidos
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleInvalidArgument(IllegalArgumentException ex, WebRequest request) {
        return ResponseEntity
                .badRequest()
                .body(createErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request));
    }

    // 422 - Validação de campos (Spring Validation)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        Map<String, Object> body = createErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, "Erro de validação", request);
        body.put("errors", errors);

        return ResponseEntity
                .unprocessableEntity()
                .body(body);
    }

    // 500 - Erros internos inesperados
    @ExceptionHandler(PagamentoMongoError.PagamentoPersistenceException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handlePersistenceException(PagamentoMongoError.PagamentoPersistenceException ex, WebRequest request) {
        return ResponseEntity
                .internalServerError()
                .body(createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request));
    }

    // Fallback para outras exceptions não tratadas
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        return ResponseEntity
                .internalServerError()
                .body(createErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Ocorreu um erro inesperado: " + ex.getMessage(),
                        request
                ));
    }
}