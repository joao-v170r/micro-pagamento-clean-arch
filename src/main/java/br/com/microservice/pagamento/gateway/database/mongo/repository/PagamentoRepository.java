package br.com.microservice.pagamento.gateway.database.mongo.repository;

import br.com.microservice.pagamento.gateway.database.mongo.entity.PagamentoEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PagamentoRepository extends MongoRepository<PagamentoEntity, String> {
    Optional<PagamentoEntity> findByPedidoId(String pedidoId);
}
