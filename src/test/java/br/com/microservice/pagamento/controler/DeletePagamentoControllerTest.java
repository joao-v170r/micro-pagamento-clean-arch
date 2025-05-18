package br.com.microservice.pagamento.controler;

import br.com.microservice.pagamento.domain.Pagamento;
import br.com.microservice.pagamento.dto.rest_controller.InputCreatePagamentoDTO;
import br.com.microservice.pagamento.gateway.CrudPagamentoGateway;
import br.com.microservice.pagamento.utils.ClienteMockData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DeleteClienteController.class)
@AutoConfigureMockMvc
@Import(DeleteClienteUseCase.class)
class DeletePagamentoControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    CrudPagamentoGateway gateway;

    @Test
    void deleteSucess() throws Exception {
        InputCreatePagamentoDTO input = ClienteMockData.validInput();
        Pagamento pagamentoMock = ClienteMockData.validCliente();

        when(gateway.findById(any()))
                .thenReturn(Optional.of(pagamentoMock));

        mockMvc.perform(
                        delete("/delete-cliente/{id}", UUID.randomUUID().toString())
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isNoContent());
    }

    @Test
    void deleteWithClienteNotFoundException() throws Exception {
        mockMvc.perform(
                        delete("/delete-cliente/{id}",UUID.randomUUID().toString())
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Cliente não encontrado")));
    }
}