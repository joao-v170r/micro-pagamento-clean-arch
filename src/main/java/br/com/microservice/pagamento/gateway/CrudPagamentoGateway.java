package br.com.microservice.pagamento.gateway;

import br.com.microservice.pagamento.domain.Pagamento;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CrudPagamentoGateway {
    Optional<Pagamento> findByPedidoId(String pedidoId);
    Optional<Pagamento> findById(String id);
    Boolean existId(String id);
    List<Pagamento> findAll();
    Pagamento save(Pagamento pagamento);
    void deleteById(String id);
    List<Pagamento> findAll(Pageable page);
}
