package br.com.microservice.pagamento.usecase;

import br.com.microservice.pagamento.domain.Pagamento;
import br.com.microservice.pagamento.domain.value_objects.MetodoPagamento;
import br.com.microservice.pagamento.domain.value_objects.StatusPagamento;
import br.com.microservice.pagamento.exception.PagamentoError;
import br.com.microservice.pagamento.gateway.CrudPagamentoGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReadPagamentoUseCaseTest {

    @Mock
    private CrudPagamentoGateway gateway;

    private ReadPagamentoUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ReadPagamentoUseCase(gateway);
    }

    @Test
    void shouldFindPagamentoById() {
        // Arrange
        String id = "pagamento123";
        var pagamento = Pagamento.criar(
                "pedido123",
                BigDecimal.valueOf(100.0),
                MetodoPagamento.PIX,
                "gateway",
                "transaction123",
                new HashMap<>(),
                StatusPagamento.CONCLUIDO
        );

        when(gateway.findById(id)).thenReturn(Optional.of(pagamento));

        // Act
        var result = useCase.find(id);

        // Assert
        assertNotNull(result);
        assertEquals("pedido123", result.pedidoId());
        assertEquals(BigDecimal.valueOf(100.0), result.valorTotal());
        assertEquals(MetodoPagamento.PIX, result.metodoPagamento());
        assertEquals(StatusPagamento.CONCLUIDO, result.status());
    }

    @Test
    void shouldFindPagamentoByPedidoId() {
        // Arrange
        String pedidoId = "pedido123";
        var pagamento = Pagamento.criar(
                pedidoId,
                BigDecimal.valueOf(100.0),
                MetodoPagamento.PIX,
                "gateway",
                "transaction123",
                new HashMap<>(),
                StatusPagamento.CONCLUIDO
        );

        when(gateway.findByPedidoId(pedidoId)).thenReturn(Optional.of(pagamento));

        // Act
        var result = useCase.findByPedidoId(pedidoId);

        // Assert
        assertNotNull(result);
        assertEquals(pedidoId, result.pedidoId());
        assertEquals(BigDecimal.valueOf(100.0), result.valorTotal());
    }

    @Test
    void shouldFindAllPagamentos() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        var pagamento1 = Pagamento.criar(
                "pedido123",
                BigDecimal.valueOf(100.0),
                MetodoPagamento.PIX,
                "gateway",
                "transaction123",
                new HashMap<>(),
                StatusPagamento.CONCLUIDO
        );
        var pagamento2 = Pagamento.criar(
                "pedido456",
                BigDecimal.valueOf(200.0),
                MetodoPagamento.PIX,
                "gateway",
                "transaction456",
                new HashMap<>(),
                StatusPagamento.CONCLUIDO
        );

        when(gateway.findAll(pageable)).thenReturn(List.of(pagamento1, pagamento2));

        // Act
        var results = useCase.findAll(pageable);

        // Assert
        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals("pedido123", results.get(0).pedidoId());
        assertEquals("pedido456", results.get(1).pedidoId());
    }

    @Test
    void shouldThrowExceptionWhenPagamentoNotFoundById() {
        // Arrange
        String id = "pagamento123";
        when(gateway.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(PagamentoError.PagamentoNotFoundException.class,
                () -> useCase.find(id));
    }

    @Test
    void shouldThrowExceptionWhenPagamentoNotFoundByPedidoId() {
        // Arrange
        String pedidoId = "pedido123";
        when(gateway.findByPedidoId(pedidoId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(PagamentoError.PagamentoNotFoundException.class,
                () -> useCase.findByPedidoId(pedidoId));
    }
}