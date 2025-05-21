package br.com.microservice.pagamento.usecase;

import br.com.microservice.pagamento.domain.Pagamento;
import br.com.microservice.pagamento.domain.value_objects.MetodoPagamento;
import br.com.microservice.pagamento.domain.value_objects.StatusPagamento;
import br.com.microservice.pagamento.exception.PagamentoError;
import br.com.microservice.pagamento.gateway.CrudPagamentoGateway;
import br.com.microservice.pagamento.gateway.PagamentoExternalGateway;
import br.com.microservice.pagamento.gateway.dto.OutputExternalPagamentoDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessamentoPagamentoUseCaseTest {

    @Mock
    private PagamentoExternalGateway pagamentoExternalGateway;

    @Mock
    private CrudPagamentoGateway crudPagamentoGateway;

    private ProcessamentoPagamentoUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ProcessamentoPagamentoUseCase(pagamentoExternalGateway, crudPagamentoGateway);
    }
    @Test
    void shouldProcessPagamentoPendente() {
        // Arrange
        String pagamentoId = "pagamento123";
        String codGateway = "transaction123";

        var pagamento = Pagamento.criar(
                "pedido123",
                BigDecimal.valueOf(100.0),
                MetodoPagamento.PIX,
                "gateway",
                codGateway,
                new HashMap<>(),
                StatusPagamento.PENDENTE
        );

        var outputExternal = new OutputExternalPagamentoDTO(
                codGateway,
                StatusPagamento.CONCLUIDO.name(),
                BigDecimal.valueOf(100.0),
                "BRL",
                new HashMap<>() {{ put("comprovante", "123456"); }},
                MetodoPagamento.PIX,
                LocalDateTime.now()
        );

        when(crudPagamentoGateway.findById(pagamentoId)).thenReturn(Optional.of(pagamento));
        when(pagamentoExternalGateway.searchPagamento(codGateway)).thenReturn(outputExternal);
        when(crudPagamentoGateway.save(any(Pagamento.class))).thenReturn(pagamento);

        // Act
        var result = useCase.processaPagamento(pagamentoId);

        // Assert
        assertNotNull(result);
        assertEquals(StatusPagamento.CONCLUIDO, result.status());
        verify(pagamentoExternalGateway).searchPagamento(codGateway);
        verify(crudPagamentoGateway).save(any(Pagamento.class));
    }

    @Test
    void shouldReturnExistingPagamentoWhenStatusConcluido() {
        // Arrange
        String pagamentoId = "pagamento123";

        var pagamento = Pagamento.criar(
                "pedido123",
                BigDecimal.valueOf(100.0),
                MetodoPagamento.PIX,
                "gateway",
                "transaction123",
                new HashMap<>(),
                StatusPagamento.CONCLUIDO
        );

        when(crudPagamentoGateway.findById(pagamentoId)).thenReturn(Optional.of(pagamento));

        // Act
        var result = useCase.processaPagamento(pagamentoId);

        // Assert
        assertNotNull(result);
        assertEquals(StatusPagamento.CONCLUIDO, result.status());
        verify(pagamentoExternalGateway, never()).searchPagamento(anyString());
        verify(crudPagamentoGateway, never()).save(any(Pagamento.class));
    }

    @Test
    void shouldThrowExceptionWhenPagamentoNotFound() {
        // Arrange
        String pagamentoId = "pagamento123";

        // Act & Assert
        when(crudPagamentoGateway.findById(pagamentoId)).thenReturn(Optional.empty());
        assertThrows(PagamentoError.PagamentoNotFoundException.class,
                () -> useCase.processaPagamento(pagamentoId));
        verify(crudPagamentoGateway).findById(pagamentoId);
    }
}