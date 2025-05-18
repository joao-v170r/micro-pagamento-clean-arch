package br.com.microservice.pagamento.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public final class PagamentoError {

    // Exception para cliente não encontrado (HTTP 404)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static final class PagamentoNotFoundException extends RuntimeException {
        public PagamentoNotFoundException(String message) {
            super(message);
        }
    }

    // Exception para argumentos inválidos (HTTP 400)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static final class ClienteIllegalArgumentException extends IllegalArgumentException {
        public ClienteIllegalArgumentException(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.CONFLICT) // HTTP 409
    public static final class ClienteAlreadyExistsException extends RuntimeException {
        public ClienteAlreadyExistsException(String message) {
            super(message);
        }
    }
}