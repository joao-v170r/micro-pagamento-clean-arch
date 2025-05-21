package br.com.microservice.pagamento.gateway.adapter;

import br.com.microservice.pagamento.domain.value_objects.MetodoPagamento;
import br.com.microservice.pagamento.domain.value_objects.MoedaPagamento;
import br.com.microservice.pagamento.domain.value_objects.StatusPagamento;
import br.com.microservice.pagamento.gateway.dto.InputExternalPagamentoDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PagamentoMercadoPagoMockAdapterTest {

    private PagamentoMercadoPagoMockAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new PagamentoMercadoPagoMockAdapter();
    }

    @Test
    void shouldCreatePixPagamento() {
        // Arrange
        var input = new InputExternalPagamentoDTO(
                BigDecimal.valueOf(100.0),
                MoedaPagamento.BRL.toString(), // converter para String
                MetodoPagamento.PIX
        );

        // Act
        var result = adapter.solicitacaoPagamento(input);

        // Assert
//        assertNotNull(result);
//        assertTrue(result.getCodigo().startsWith("PIX")); // usar getCodigo()
//        assertEquals(StatusPagamento.PENDENTE.toString(), result.getStatus());
//        assertEquals(input.getValor(), result.getValor());
//        assertEquals(input.getMoeda(), result.getMoeda());
//        assertEquals(input.getMetodo(), result.getMetodo());
//
//        var detalhes = result.getDetalhes();
//        assertNotNull(detalhes.get("qr_code"));
//        assertNotNull(detalhes.get("codigo_copia_cola"));
//        assertNotNull(detalhes.get("expiracao"));
    }

    @Test
    void shouldCreateBoletoPagamento() {
        // Arrange
        var input = new InputExternalPagamentoDTO(
                BigDecimal.valueOf(100.0),
                MoedaPagamento.BRL.toString(),
                MetodoPagamento.BOLETO
        );

        // Act
        var result = adapter.solicitacaoPagamento(input);

        // Assert
        assertNotNull(result);
        assertEquals(StatusPagamento.PENDENTE.toString(), result.status());
        assertEquals(input.valor(), result.valor());
        assertEquals(input.moeda(), result.moeda());

        var detalhes = result.detalhes();
        assertNotNull(detalhes.get("linha_digitavel"));
        assertEquals(
                LocalDate.now().plusDays(3).toString(),
                detalhes.get("vencimento")
        );
    }

    @Test
    void shouldSearchPixPagamento() {
        // Arrange
        String codPagamento = "PIX123";

        // Act
        var result = adapter.searchPagamento(codPagamento);

        // Assert
        assertNotNull(result);
        assertEquals(StatusPagamento.CONCLUIDO.toString(), result.status());

        var detalhes = result.detalhes();
        assertEquals("PAGO", detalhes.get("status"));
        assertNotNull(detalhes.get("qr_code"));
        assertNotNull(detalhes.get("codigo_copia_cola"));
    }

    @Test
    void shouldSearchBoletoPagamento() {
        // Arrange
        String codPagamento = "BOLETO123";

        // Act
        var result = adapter.searchPagamento(codPagamento);

        // Assert
        assertNotNull(result);
        assertEquals(StatusPagamento.CONCLUIDO.toString(), result.status());

        var detalhes = result.detalhes();
        assertEquals("PAGO", detalhes.get("status"));
        assertNotNull(detalhes.get("linha_digitavel"));
    }

    @Test
    void shouldReturnCorrectGatewayName() {
        // Act
        String gatewayName = adapter.nameGateway();

        // Assert
        assertEquals("mercado_pago", gatewayName);
    }
}