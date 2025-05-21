package br.com.microservice.pagamento.exception;

import br.com.microservice.pagamento.gateway.exception.mongo.PagamentoMongoError;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    private ServletWebRequest getWebRequest() {
        return new ServletWebRequest(new MockHttpServletRequest("POST", "/test-path"));
    }

    @Test
    void handleClienteNotFound() {
        PagamentoError.PagamentoNotFoundException ex = new PagamentoError.PagamentoNotFoundException("Cliente não encontrado");
        ResponseEntity<Object> response = handler.handleClienteNotFound(ex, getWebRequest());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Cliente não encontrado"));
    }

    @Test
    void handleClienteAlreadyExists() {
        PagamentoError.ClienteAlreadyExistsException ex = new PagamentoError.ClienteAlreadyExistsException("Cliente já existe");
        ResponseEntity<Object> response = handler.handleClienteAlreadyExists(ex, getWebRequest());
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Cliente já existe"));
    }

    @Test
    void handleInvalidArgument() {
        IllegalArgumentException ex = new IllegalArgumentException("Argumento inválido");
        ResponseEntity<Object> response = handler.handleInvalidArgument(ex, getWebRequest());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Argumento inválido"));
    }

    @Test
    void handlePersistenceException() {
        PagamentoMongoError.PagamentoPersistenceException ex =
                new PagamentoMongoError.PagamentoPersistenceException("Erro de persistência", null);
        ResponseEntity<Object> response = handler.handlePersistenceException(ex, getWebRequest());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Erro de persistência"));
    }

    @Test
    void handleAllExceptions() {
        Exception ex = new Exception("Erro inesperado");
        ResponseEntity<Object> response = handler.handleAllExceptions(ex, getWebRequest());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Ocorreu um erro inesperado"));
    }
}