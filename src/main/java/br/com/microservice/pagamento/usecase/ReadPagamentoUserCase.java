package br.com.microservice.pagamento.usecase;

import br.com.microservice.pagamento.domain.Pagamento;
import br.com.microservice.pagamento.dto.PagamentoDTO;
import br.com.microservice.pagamento.exception.PagamentoError;
import br.com.microservice.pagamento.gateway.CrudPagamentoGateway;
import br.com.microservice.pagamento.usecase.mapper.PagamentoMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReadPagamentoUserCase {
    private final CrudPagamentoGateway gateway;

    public ReadPagamentoUserCase(CrudPagamentoGateway gateway) {
        this.gateway = gateway;
    }

    public PagamentoDTO find(String id) {
        Pagamento pagamento = gateway.findById(id).orElseThrow(
                () -> new PagamentoError.PagamentoNotFoundException("pagamento não encontrado"));
        return PagamentoMapper.mapToDTO(pagamento);
    }

    public List<PagamentoDTO> findAll(Pageable page) {
        List<Pagamento> pagamentos = gateway.findAll(page);
        return pagamentos.stream().map(PagamentoMapper::mapToDTO).toList();
    }
}