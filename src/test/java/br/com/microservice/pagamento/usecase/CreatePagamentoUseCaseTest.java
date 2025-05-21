package br.com.microservice.pagamento.usecase;

import br.com.microservice.pagamento.domain.Pagamento;
import br.com.microservice.pagamento.domain.value_objects.MetodoPagamento;
import br.com.microservice.pagamento.domain.value_objects.MoedaPagamento;
import br.com.microservice.pagamento.domain.value_objects.StatusPagamento;
import br.com.microservice.pagamento.dto.usecase.CreatePagamentoDTO;
import br.com.microservice.pagamento.gateway.CrudPagamentoGateway;
import br.com.microservice.pagamento.gateway.PagamentoExternalGateway;
import br.com.microservice.pagamento.gateway.dto.InputExternalPagamentoDTO;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreatePagamentoUseCaseTest {

    @Mock
    private PagamentoExternalGateway pagamentoExternalGateway;

    @Mock
    private CrudPagamentoGateway crudPagamentoGateway;

    private CreatePagamentoUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CreatePagamentoUseCase(pagamentoExternalGateway, crudPagamentoGateway);
    }

    @Test
    void shouldCreateNewPagamento() {
        // Arrange
        var createDTO = new CreatePagamentoDTO(
                "cliente123",
                "pedido123",
                MetodoPagamento.PIX,
                BigDecimal.valueOf(100.0)
        );

        var outputExternal = new OutputExternalPagamentoDTO(
                "pedido123",
                "CONCLUIDO",
                BigDecimal.valueOf(100.0),
                "moeda",
                null,
                MetodoPagamento.PIX,
                LocalDateTime.now()
        );
        var savedPagamento = Pagamento.criar(
                "pedido123",
                BigDecimal.valueOf(100.0),
                MetodoPagamento.PIX,
                "gateway",
                "transaction123",
                new HashMap<>(),
                StatusPagamento.CONCLUIDO
        );

        when(crudPagamentoGateway.findByPedidoId("pedido123")).thenReturn(Optional.empty());
        when(pagamentoExternalGateway.solicitacaoPagamento(any(InputExternalPagamentoDTO.class)))
                .thenReturn(outputExternal);
        when(pagamentoExternalGateway.nameGateway()).thenReturn("gateway");
        when(crudPagamentoGateway.save(any(Pagamento.class))).thenReturn(savedPagamento);

        // Act
        var result = useCase.create(createDTO);

        // Assert
        assertNotNull(result);
        assertEquals("pedido123", result.pedidoId());
        assertEquals(BigDecimal.valueOf(100.0), result.valorTotal());
        assertEquals(MetodoPagamento.PIX, result.metodoPagamento());
        assertEquals(StatusPagamento.CONCLUIDO, result.status());

        verify(crudPagamentoGateway).findByPedidoId("pedido123");
        verify(pagamentoExternalGateway).solicitacaoPagamento(any());
        verify(crudPagamentoGateway).save(any());
    }

    @Test
    void shouldReturnExistingPagamento() {
        // Arrange
        var createDTO = new CreatePagamentoDTO(
                "cliente123",
                "pedido123",
                MetodoPagamento.PIX,
                BigDecimal.valueOf(100.0)
        );

        var existingPagamento = Pagamento.criar(
                "pedido123",
                BigDecimal.valueOf(100.0),
                MetodoPagamento.PIX,
                "gateway",
                "transaction123",
                new HashMap<>(),
                StatusPagamento.CONCLUIDO
        );

        when(crudPagamentoGateway.findByPedidoId("pedido123")).thenReturn(Optional.of(existingPagamento));

        // Act
        var result = useCase.create(createDTO);

        // Assert
        assertNotNull(result);
        assertEquals("pedido123", result.pedidoId());

        verify(crudPagamentoGateway).findByPedidoId("pedido123");
        verify(pagamentoExternalGateway, never()).solicitacaoPagamento(any());
        verify(crudPagamentoGateway, never()).save(any());
    }
}