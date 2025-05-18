package br.com.microservice.pagamento.gateway;

import br.com.microservice.pagamento.gateway.dto.InputExternalPagamentoDTO;
import br.com.microservice.pagamento.gateway.dto.OutputExternalPagamentoDTO;

public interface PagamentoExternalGateway {
    OutputExternalPagamentoDTO solicitacaoPagamento(InputExternalPagamentoDTO input);
    OutputExternalPagamentoDTO searchPagamento(String codPagamento);
    String nameGateway();
}
