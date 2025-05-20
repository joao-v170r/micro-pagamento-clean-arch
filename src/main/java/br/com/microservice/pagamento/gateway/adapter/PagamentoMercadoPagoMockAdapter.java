package br.com.microservice.pagamento.gateway.adapter;

import br.com.microservice.pagamento.domain.value_objects.MetodoPagamento;
import br.com.microservice.pagamento.domain.value_objects.StatusPagamento;
import br.com.microservice.pagamento.gateway.PagamentoExternalGateway;
import br.com.microservice.pagamento.gateway.dto.InputExternalPagamentoDTO;
import br.com.microservice.pagamento.gateway.dto.OutputExternalPagamentoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PagamentoMercadoPagoMockAdapter implements PagamentoExternalGateway {

    @Override
    public OutputExternalPagamentoDTO solicitacaoPagamento(InputExternalPagamentoDTO input) {
        OutputExternalPagamentoDTO outputPagamento = null;

        switch (input.metodo()) {
            case PIX -> {
                HashMap<String,String> detalhes = new HashMap<>();

                detalhes.put("qr_code", "data:image/png;base64");
                detalhes.put("codigo_copia_cola", "00020126360014BR.GOV.BCB.PIX");
                detalhes.put("expiracao", LocalDateTime.now().toString());

                outputPagamento = new OutputExternalPagamentoDTO(
                        "PIX"+UUID.randomUUID(),
                        StatusPagamento.PENDENTE.toString(),
                        input.valor(),
                        input.moeda(),
                        detalhes,
                        input.metodo(),
                        null
                );
            }
            case BOLETO -> {
                HashMap<String, String> detalhes = new HashMap<>();

                detalhes.put("linha_digitavel", "34191.79001 01043.510047 91020.150008 7 84460000002000");
                detalhes.put("vencimento", LocalDate.now().plusDays(3).toString());

                outputPagamento = new OutputExternalPagamentoDTO(
                        "BOLETO"+UUID.randomUUID(),
                        StatusPagamento.PENDENTE.toString(),
                        input.valor(),
                        input.moeda(),
                        detalhes,
                        input.metodo(),
                        null
                );
            }
        }

        return outputPagamento;
    }

    @Override
    public OutputExternalPagamentoDTO searchPagamento(String codPagamento) {
        OutputExternalPagamentoDTO outputPagamento = null;

        if(codPagamento.contains("PIX")) {
            HashMap<String,String> detalhes = new HashMap<>();

            detalhes.put("qr_code", "data:image/png;base64");
            detalhes.put("codigo_copia_cola", "00020126360014BR.GOV.BCB.PIX");
            detalhes.put("status", "PAGO");

            outputPagamento = new OutputExternalPagamentoDTO(
                codPagamento,
                StatusPagamento.CONCLUIDO.toString(),
                null,
                null,
                detalhes,
                MetodoPagamento.PIX,
                LocalDateTime.now()
            );
        } else {
            HashMap<String, String> detalhes = new HashMap<>();

            detalhes.put("linha_digitavel", "34191.79001 01043.510047 91020.150008 7 84460000002000");
            detalhes.put("status", "PAGO");

            outputPagamento = new OutputExternalPagamentoDTO(
                codPagamento,
                StatusPagamento.CONCLUIDO.toString(),
                null,
                null,
                detalhes,
                MetodoPagamento.BOLETO,
                LocalDateTime.now()
            );
        }

        return outputPagamento;
    }

    @Override
    public String nameGateway() {
         return "mercado_pago";
    }
}
