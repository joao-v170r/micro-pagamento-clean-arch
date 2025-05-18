package br.com.microservice.pagamento.gateway.database.mongo.entity;

import br.com.microservice.pagamento.domain.value_objects.MetodoPagamento;
import br.com.microservice.pagamento.domain.value_objects.MoedaPagamento;
import br.com.microservice.pagamento.domain.value_objects.StatusPagamento;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;

@Data
@AllArgsConstructor
@Document(collection = "pagamento")
public class PagamentoEntity {
    @Id
    private final String id;
    @Indexed(unique = true)
    private final String pedidoId;
    private final BigDecimal valorTotal;
    private final MoedaPagamento moedaPagamento;
    private final MetodoPagamento metodoPagamento;
    private final String gatewayPagamento;
    private final HashMap<String, String> detalhes;
    private StatusPagamento status;
    private final LocalDateTime dtCriacao;
    private LocalDateTime dtAtualizacao;
    private final String codGateway;
}