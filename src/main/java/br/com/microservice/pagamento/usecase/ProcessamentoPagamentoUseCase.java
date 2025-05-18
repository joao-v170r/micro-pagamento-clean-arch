package br.com.microservice.pagamento.usecase;

import br.com.microservice.pagamento.domain.Pagamento;
import br.com.microservice.pagamento.domain.value_objects.StatusPagamento;
import br.com.microservice.pagamento.dto.PagamentoDTO;
import br.com.microservice.pagamento.exception.PagamentoError;
import br.com.microservice.pagamento.gateway.CrudPagamentoGateway;
import br.com.microservice.pagamento.gateway.PagamentoExternalGateway;
import br.com.microservice.pagamento.gateway.dto.OutputExternalPagamentoDTO;
import br.com.microservice.pagamento.usecase.mapper.PagamentoMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ProcessamentoPagamentoUseCase {

    private final PagamentoExternalGateway pagamentoGateway;
    private final CrudPagamentoGateway gateway;

    public ProcessamentoPagamentoUseCase(PagamentoExternalGateway pagamentoGateway, CrudPagamentoGateway gateway) {
        this.pagamentoGateway = pagamentoGateway;
        this.gateway = gateway;
    }

    public PagamentoDTO processaPagamento(String id) {
        Pagamento pagamento = gateway.findById(id)
                .orElseThrow(() -> new PagamentoError
                        .PagamentoNotFoundException("ProcessamentoPagamentoUseCase: pagamento não encontrado"));

        if(pagamento.getStatus().equals(StatusPagamento.CONCLUIDO) || pagamento.getStatus().equals(StatusPagamento.CANCELADO)) {
            return PagamentoMapper.mapToDTO(pagamento);
        }

        OutputExternalPagamentoDTO resultPagamentoGateway = pagamentoGateway.searchPagamento(pagamento.getCodGateway());

        pagamento.setStatus(StatusPagamento.valueOf(resultPagamentoGateway.status()));
        pagamento.setDtAtualizacao(LocalDateTime.now());
        resultPagamentoGateway.detalhes().forEach((k, v) -> {
            if(!pagamento.getDetalhes().containsKey(k)) {
                pagamento.addDetalhesPagamento(k, v);
            }
        });
        return PagamentoMapper.mapToDTO(gateway.save(pagamento));
    }
}
