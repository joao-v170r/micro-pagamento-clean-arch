package br.com.microservice.pagamento.gateway.exception.mongo;

import br.com.microservice.pagamento.gateway.exception.GatewayException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public final class PagamentoMongoError {

    private final static String PREFIX_MONGO = "mongo_db_gateway:";

    // Erro quando o cliente não é encontrado (HTTP 404)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static final class PagamentoNotFoundException extends RuntimeException implements GatewayException {
        public PagamentoNotFoundException(String message) {
            super(PREFIX_MONGO + message);
        }
    }

    // Erro quando um argumento inválido é passado (HTTP 400)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static final class PagamentoInvalidArgumentException extends IllegalArgumentException implements GatewayException {
        public PagamentoInvalidArgumentException(String message) {
            super(PREFIX_MONGO + message);
        }
    }

    // Erro genérico de persistência (ex: falha no MongoDB) (HTTP 500)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public static final class PagamentoPersistenceException extends RuntimeException implements GatewayException {
        public PagamentoPersistenceException(String message, Throwable cause) {
            super(PREFIX_MONGO + message, cause);
        }
    }

    // Erro quando há conflito (ex: CPF duplicado) (HTTP 409)
    @ResponseStatus(HttpStatus.CONFLICT)
    public static final class PagamentoConflictException extends RuntimeException implements GatewayException {
        public PagamentoConflictException(String message) {
            super(PREFIX_MONGO + message);
        }
    }
}