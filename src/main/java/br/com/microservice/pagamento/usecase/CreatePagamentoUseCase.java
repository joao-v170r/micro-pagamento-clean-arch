package br.com.microservice.pagamento.usecase;

import br.com.microservice.pagamento.domain.Pagamento;
import br.com.microservice.pagamento.domain.value_objects.MoedaPagamento;
import br.com.microservice.pagamento.domain.value_objects.StatusPagamento;
import br.com.microservice.pagamento.dto.PagamentoDTO;
import br.com.microservice.pagamento.dto.usecase.CreatePagamentoDTO;
import br.com.microservice.pagamento.gateway.CrudPagamentoGateway;
import br.com.microservice.pagamento.gateway.PagamentoExternalGateway;
import br.com.microservice.pagamento.gateway.dto.InputExternalPagamentoDTO;
import br.com.microservice.pagamento.gateway.dto.OutputExternalPagamentoDTO;
import br.com.microservice.pagamento.usecase.mapper.PagamentoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CreatePagamentoUseCase {

    private final PagamentoExternalGateway pagamentoGateway;
    private final CrudPagamentoGateway gateway;

    public CreatePagamentoUseCase(PagamentoExternalGateway pagamentoGateway, CrudPagamentoGateway gateway) {
        this.pagamentoGateway = pagamentoGateway;
        this.gateway = gateway;
    }

    public PagamentoDTO create(CreatePagamentoDTO pagamentoDTO) {
        var opCliente = gateway.findByPedidoId(pagamentoDTO.pedidoId());

        if(opCliente.isPresent()) {
            log.info(
                    "solicitação de pagamento de pedido ja registrada. id[{}]",
                    pagamentoDTO.clienteId()
            );
            return PagamentoMapper.mapToDTO(opCliente.get());
        }

        OutputExternalPagamentoDTO solicitacaoPagamento = pagamentoGateway.solicitacaoPagamento(
                new InputExternalPagamentoDTO(
                        pagamentoDTO.valorTotal(),
                        MoedaPagamento.BRL.toString(),
                        pagamentoDTO.metodoPagamento())
        );

        var pagamento = Pagamento.criar(
                pagamentoDTO.pedidoId(),
                pagamentoDTO.valorTotal(),
                pagamentoDTO.metodoPagamento(),
                pagamentoGateway.nameGateway(),
                solicitacaoPagamento.id(),
                solicitacaoPagamento.detalhes(),
                StatusPagamento.valueOf(solicitacaoPagamento.status())
        );

        return PagamentoMapper.mapToDTO(gateway.save(pagamento));
    }
}
