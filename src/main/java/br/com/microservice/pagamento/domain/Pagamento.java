package br.com.microservice.pagamento.domain;

import br.com.microservice.pagamento.domain.value_objects.MetodoPagamento;
import br.com.microservice.pagamento.domain.value_objects.MoedaPagamento;
import br.com.microservice.pagamento.domain.value_objects.StatusPagamento;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;

@Slf4j
@Getter
public class Pagamento {

    private final String id;
    private final String pedidoId;
    private final BigDecimal valorTotal;
    private final MoedaPagamento moedaPagamento;
    private final MetodoPagamento metodoPagamento;
    private final String gatewayPagamento;
    private final HashMap<String, String> detalhes;
    @Setter
    private StatusPagamento status;
    private final LocalDateTime dtCriacao;
    @Setter
    private LocalDateTime dtAtualizacao;
    private final String codGateway;

    public Pagamento(
            String codGateway,
            LocalDateTime dtAtualizacao,
            LocalDateTime dtCriacao,
            String gatewayPagamento,
            MetodoPagamento metodoPagamento,
            MoedaPagamento moedaPagamento,
            BigDecimal valorTotal,
            String pedidoId,
            String id, HashMap<String, String> detalhes,
            StatusPagamento status
    ) {
        this.codGateway = codGateway;
        this.dtAtualizacao = dtAtualizacao;
        this.dtCriacao = dtCriacao;
        this.gatewayPagamento = gatewayPagamento;
        this.metodoPagamento = metodoPagamento;
        this.moedaPagamento = moedaPagamento;
        this.valorTotal = valorTotal;
        this.pedidoId = pedidoId;
        this.id = id;
        this.detalhes = detalhes;
        this.status = status;
    }

    public static Pagamento criar(
            String idPedido,
            BigDecimal valorTotal,
            MetodoPagamento metodoPagamento,
            String gatewayPagamento,
            String codGateway,
            HashMap<String,String> detalhes,
            StatusPagamento status
    ) {
        return new Pagamento(
                codGateway,
                null,
                LocalDateTime.now(),
                gatewayPagamento,
                metodoPagamento,
                MoedaPagamento.BRL,
                valorTotal,
                idPedido,
                null,
                detalhes,
                status
        );
    }

    public static Pagamento reconstituir(
            String codGateway,
            LocalDateTime dtAtualizacao,
            LocalDateTime dtCriacao,
            String gatewayPagamento,
            MetodoPagamento metodoPagamento,
            MoedaPagamento moedaPagamento,
            BigDecimal valorTotal,
            String idPedido,
            String id,
            HashMap<String, String> detalhes,
            StatusPagamento status
    ) {
        return new Pagamento(
                codGateway,
                dtAtualizacao,
                dtCriacao,
                gatewayPagamento,
                metodoPagamento,
                moedaPagamento,
                valorTotal,
                idPedido,
                id,
                detalhes,
                status
        );
    }

    public void addDetalhesPagamento(String key, String valeu) {
        detalhes.put(key, valeu);
    }
}
