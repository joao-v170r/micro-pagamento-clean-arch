package br.com.microservice.pagamento.gateway.database.mongo;

import br.com.microservice.pagamento.domain.Pagamento;
import br.com.microservice.pagamento.domain.value_objects.MetodoPagamento;
import br.com.microservice.pagamento.domain.value_objects.MoedaPagamento;
import br.com.microservice.pagamento.domain.value_objects.StatusPagamento;
import br.com.microservice.pagamento.gateway.database.mongo.entity.PagamentoEntity;
import br.com.microservice.pagamento.gateway.database.mongo.repository.PagamentoRepository;
import br.com.microservice.pagamento.gateway.exception.mongo.PagamentoMongoError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagamentoMongoGatewayTest {

    @Mock
    private PagamentoRepository repository;

    private PagamentoMongoGateway gateway;

    @BeforeEach
    void setUp() {
        gateway = new PagamentoMongoGateway(repository);
    }

    @Test
    void shouldFindByPedidoId() {
        // Arrange
        String pedidoId = "pedido123";
        var entity = new PagamentoEntity(
                "123", // id
                pedidoId,
                BigDecimal.valueOf(100.0),
                MoedaPagamento.BRL, // moeda
                MetodoPagamento.PIX,
                "gateway", // nomeGateway, // codGateway
                new HashMap<>(), // dadosAdicionais
                StatusPagamento.CONCLUIDO,
                LocalDateTime.now(), // dataCriacao
                LocalDateTime.now(), // dataAtualizacao
                "string" // versao
        );

        when(repository.findByPedidoId(pedidoId)).thenReturn(Optional.of(entity));

        // Act
        var result = gateway.findByPedidoId(pedidoId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(pedidoId, result.get().getPedidoId());
        verify(repository).findByPedidoId(pedidoId);
    }

    @Test
    void shouldFindById() {
        // Arrange
        String id = "pagamento123";
        var entity = new PagamentoEntity(
                "pagamento123", // id
                id,
                BigDecimal.valueOf(100.0),
                MoedaPagamento.BRL, // moeda
                MetodoPagamento.PIX,
                "gateway", // nomeGateway, // codGateway
                new HashMap<>(), // dadosAdicionais
                StatusPagamento.CONCLUIDO,
                LocalDateTime.now(), // dataCriacao
                LocalDateTime.now(), // dataAtualizacao
                "string" // versao
        );

        when(repository.findById(id)).thenReturn(Optional.of(entity));

        // Act
        var result = gateway.findById(id);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
    }

    @Test
    void shouldSavePagamento() {
        // Arrange
        var pagamento = Pagamento.criar(
                "pedido123",
                BigDecimal.valueOf(100.0),
                MetodoPagamento.PIX,
                "gateway",
                "transaction123",
                new HashMap<>(),
                StatusPagamento.CONCLUIDO
        );

        var entity = new PagamentoEntity(
                "123", // id
                pagamento.getPedidoId(),
                pagamento.getValorTotal(),
                MoedaPagamento.BRL, // moeda
                MetodoPagamento.PIX,
                "gateway", // nomeGateway, // codGateway
                new HashMap<>(), // dadosAdicionais
                StatusPagamento.CONCLUIDO,
                LocalDateTime.now(), // dataCriacao
                LocalDateTime.now(), // dataAtualizacao
                "string" // versao
        );

        when(repository.save(any(PagamentoEntity.class))).thenReturn(entity);

        // Act
        var result = gateway.save(pagamento);

        // Assert
        assertNotNull(result);
        assertEquals(pagamento.getPedidoId(), result.getPedidoId());
        verify(repository).save(any(PagamentoEntity.class));
    }

    @Test
    void shouldFindAllWithPagination() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        var entity = new PagamentoEntity(
                "123", // id
                "pedido123",
                BigDecimal.valueOf(100.0),
                MoedaPagamento.BRL, // moeda
                MetodoPagamento.PIX,
                "gateway", // nomeGateway, // codGateway
                new HashMap<>(), // dadosAdicionais
                StatusPagamento.CONCLUIDO,
                LocalDateTime.now(), // dataCriacao
                LocalDateTime.now(), // dataAtualizacao
                "string" // versao
        );

        Page<PagamentoEntity> page = new PageImpl<>(List.of(entity));
        when(repository.findAll(pageable)).thenReturn(page);

        // Act
        var results = gateway.findAll(pageable);

        // Assert
        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
    }

    @Test
    void shouldDeleteById() {
        // Arrange
        String id = "pagamento123";
        when(repository.existsById(id)).thenReturn(true);
        doNothing().when(repository).deleteById(id);

        // Act & Assert
        assertDoesNotThrow(() -> gateway.deleteById(id));
        verify(repository).deleteById(id);
    }

    @Test
    void shouldThrowExceptionWhenPedidoIdIsNull() {
        // Act & Assert
        assertThrows(PagamentoMongoError.PagamentoInvalidArgumentException.class,
                () -> gateway.findByPedidoId(null));
    }

    @Test
    void shouldThrowExceptionWhenSavingNullPagamento() {
        // Act & Assert
        assertThrows(PagamentoMongoError.PagamentoInvalidArgumentException.class,
                () -> gateway.save(null));
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentPagamento() {
        // Arrange
        String id = "pagamento123";
        when(repository.existsById(id)).thenReturn(false);

        // Act & Assert
        assertThrows(PagamentoMongoError.PagamentoNotFoundException.class,
                () -> gateway.deleteById(id));
    }

    @Test
    void shouldThrowExceptionWhenSavingConflictingPagamento() {
        // Arrange
        var pagamento = Pagamento.criar(
                "pedido123",
                BigDecimal.valueOf(100.0),
                MetodoPagamento.PIX,
                "gateway",
                "transaction123",
                new HashMap<>(),
                StatusPagamento.CONCLUIDO
        );

        when(repository.save(any(PagamentoEntity.class)))
                .thenThrow(new DataIntegrityViolationException("Conflict"));

        // Act & Assert
        assertThrows(PagamentoMongoError.PagamentoConflictException.class,
                () -> gateway.save(pagamento));
    }

    @Test
    void shouldThrowExceptionWhenFindByBlankPedidoId() {
        // Act & Assert
        assertThrows(PagamentoMongoError.PagamentoInvalidArgumentException.class,
                () -> gateway.findByPedidoId("  "));
    }

    @Test
    void shouldThrowPersistenceExceptionWhenFindByPedidoIdFails() {
        // Arrange
        when(repository.findByPedidoId(any()))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(PagamentoMongoError.PagamentoPersistenceException.class,
                () -> gateway.findByPedidoId("pedido123"));
    }

    @Test
    void shouldThrowExceptionWhenFindByBlankId() {
        // Act & Assert
        assertThrows(PagamentoMongoError.PagamentoInvalidArgumentException.class,
                () -> gateway.findById("  "));
    }

    @Test
    void shouldThrowPersistenceExceptionWhenFindByIdFails() {
        // Arrange
        when(repository.findById(any()))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(PagamentoMongoError.PagamentoPersistenceException.class,
                () -> gateway.findById("123"));
    }

    @Test
    void shouldThrowPersistenceExceptionWhenExistIdFails() {
        // Arrange
        when(repository.existsById(any()))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(PagamentoMongoError.PagamentoPersistenceException.class,
                () -> gateway.existId("123"));
    }

    @Test
    void shouldReturnTrueWhenIdExists() {
        // Arrange
        when(repository.existsById("123")).thenReturn(true);

        // Act & Assert
        assertTrue(gateway.existId("123"));
    }

    @Test
    void shouldThrowPersistenceExceptionWhenFindAllFails() {
        // Arrange
        when(repository.findAll())
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(PagamentoMongoError.PagamentoPersistenceException.class,
                () -> gateway.findAll());
    }

    @Test
    void shouldThrowPersistenceExceptionWhenDeleteFails() {
        // Arrange
        String id = "123";
        when(repository.existsById(id)).thenReturn(true);
        doThrow(new RuntimeException("Database error"))
                .when(repository).deleteById(id);

        // Act & Assert
        assertThrows(PagamentoMongoError.PagamentoPersistenceException.class,
                () -> gateway.deleteById(id));
    }

    @Test
    void shouldThrowPersistenceExceptionWhenSaveFails() {
        // Arrange
        var pagamento = Pagamento.criar(
                "pedido123",
                BigDecimal.valueOf(100.0),
                MetodoPagamento.PIX,
                "gateway",
                "transaction123",
                new HashMap<>(),
                StatusPagamento.CONCLUIDO
        );

        when(repository.save(any(PagamentoEntity.class)))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(PagamentoMongoError.PagamentoPersistenceException.class,
                () -> gateway.save(pagamento));
    }
}