package br.com.microservice.pagamento.controller;

import br.com.microservice.pagamento.domain.value_objects.MetodoPagamento;
import br.com.microservice.pagamento.domain.value_objects.MoedaPagamento;
import br.com.microservice.pagamento.domain.value_objects.StatusPagamento;
import br.com.microservice.pagamento.dto.PagamentoDTO;
import br.com.microservice.pagamento.dto.rest_controller.InputCreatePagamentoDTO;
import br.com.microservice.pagamento.dto.usecase.CreatePagamentoDTO;
import br.com.microservice.pagamento.usecase.CreatePagamentoUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CreatePagamentoController.class)
class CreatePagamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CreatePagamentoUseCase useCase;

    @Test
    void shouldCreatePagamento() throws Exception {
        InputCreatePagamentoDTO input = new InputCreatePagamentoDTO(
                "cliente123",
                "pedido123",
                MetodoPagamento.PIX,
                BigDecimal.valueOf(100.0)
        );

        PagamentoDTO expectedResponse = new PagamentoDTO(
                "pagamento123",
                "pedido123",
                BigDecimal.valueOf(100.0),
                MoedaPagamento.BRL,
                MetodoPagamento.PIX,
                "gateway",
                null,
                StatusPagamento.CONCLUIDO,
                LocalDateTime.now(),
                LocalDateTime.now(),
                "gateway"
        );

        when(useCase.create(any(CreatePagamentoDTO.class))).thenReturn(expectedResponse);

        mockMvc.perform(post("/solicita-pagamento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReturnBadRequestWhenInvalidInput() throws Exception {
        InputCreatePagamentoDTO invalidInput = new InputCreatePagamentoDTO(
                null,
                null,
                MetodoPagamento.PIX,
                BigDecimal.valueOf(100.0)
        );

        mockMvc.perform(post("/solicita-pagamento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidInput)));
    }
}