package com.example.bookingapp.controller;

import com.example.bookingapp.dto.payment.PaymentDto;
import com.example.bookingapp.model.Payment;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.model.checkout.Session;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import java.util.List;
import static com.example.bookingapp.utils.CommonTestConstants.CUSTOMER_ROLE;
import static com.example.bookingapp.utils.CommonTestConstants.MANAGER_ROLE;
import static com.example.bookingapp.utils.PaymentUtils.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PaymentControllerTest {
    protected static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(username = CUSTOMER_EMAIL, authorities = MANAGER_ROLE)
    @DisplayName("List payments with valid user ID")
    @Sql(scripts = {
            ADD_BASE_TEST_DATA_SQL,
            ADD_ACCOMMODATION_TEST_DATA_SQL,
            ADD_BOOKING_TEST_DATA_SQL,
            ADD_PAYMENT_TEST_DATA_SQL
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            CLEAR_PAYMENT_TEST_DATA_SQL,
            CLEAR_BOOKING_TEST_DATA_SQL,
            CLEAR_ACCOMMODATION_TEST_DATA_SQL,
            CLEAR_BASE_TEST_DATA_SQL
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void list_ValidUserId_Success() throws Exception {
        // Given
        String userId = VALID_USER_ID.toString();

        // When
        MvcResult result = mockMvc.perform(get(BASE_URL)
                        .param(USER_ID_PARAM, userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        List<PaymentDto> actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<>() {
                }
        );
        assertThat(actual).hasSize(1);
        assertThat(actual.get(0).getId()).isEqualTo(VALID_PAYMENT_ID);
    }

    @Test
    @WithMockUser(username = CUSTOMER_EMAIL, roles = CUSTOMER_ROLE)
    @DisplayName("Get current user's payments")
    @Sql(scripts = {
            ADD_BASE_TEST_DATA_SQL,
            ADD_ACCOMMODATION_TEST_DATA_SQL,
            ADD_BOOKING_TEST_DATA_SQL,
            ADD_PAYMENT_TEST_DATA_SQL
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            CLEAR_PAYMENT_TEST_DATA_SQL,
            CLEAR_BOOKING_TEST_DATA_SQL,
            CLEAR_ACCOMMODATION_TEST_DATA_SQL,
            CLEAR_BASE_TEST_DATA_SQL
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getMyPayments_AuthenticatedUser_Success() throws Exception {
        // Given
        // Test data is set up via SQL scripts

        // When
        MvcResult result = mockMvc.perform(get(BASE_URL + MY_PAYMENTS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        List<PaymentDto> actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<List<PaymentDto>>() {}
        );
        assertThat(actual).hasSize(1);
        assertThat(actual.get(0).getId()).isEqualTo(VALID_PAYMENT_ID);
    }

    @Test
    @WithMockUser(username = CUSTOMER_EMAIL, roles = CUSTOMER_ROLE)
    @DisplayName("Handle successful payment with valid session ID")
    @Sql(scripts = {
            ADD_BASE_TEST_DATA_SQL,
            ADD_ACCOMMODATION_TEST_DATA_SQL,
            ADD_BOOKING_TEST_DATA_SQL,
            ADD_PAYMENT_TEST_DATA_SQL
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            CLEAR_PAYMENT_TEST_DATA_SQL,
            CLEAR_BOOKING_TEST_DATA_SQL,
            CLEAR_ACCOMMODATION_TEST_DATA_SQL,
            CLEAR_BASE_TEST_DATA_SQL
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void handleSuccess_ValidSessionId_Success() throws Exception {
        // Given
        try (MockedStatic<Session> mockedSession = mockStatic(Session.class)) {
            Session mockSession = mockStripeSession();
            mockedSession.when(() -> Session.retrieve(VALID_SESSION_ID)).thenReturn(mockSession);
            when(mockSession.getPaymentStatus()).thenReturn(PAID_STATUS);

            // When
            MvcResult result = mockMvc.perform(get(BASE_URL + SUCCESS_ENDPOINT)
                            .param(SESSION_ID_PARAM, VALID_SESSION_ID)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

            // Then
            PaymentDto actual = objectMapper.readValue(result.getResponse().getContentAsString(), PaymentDto.class);
            assertThat(actual.getId()).isEqualTo(VALID_PAYMENT_ID);
            assertThat(actual.getStatus()).isEqualTo(Payment.PaymentStatus.PAID);
        }
    }

    @Test
    @WithMockUser(username = CUSTOMER_EMAIL, roles = CUSTOMER_ROLE)
    @DisplayName("Handle canceled payment with valid session ID")
    @Sql(scripts = {
            ADD_BASE_TEST_DATA_SQL,
            ADD_ACCOMMODATION_TEST_DATA_SQL,
            ADD_BOOKING_TEST_DATA_SQL,
            ADD_PAYMENT_TEST_DATA_SQL
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            CLEAR_PAYMENT_TEST_DATA_SQL,
            CLEAR_BOOKING_TEST_DATA_SQL,
            CLEAR_ACCOMMODATION_TEST_DATA_SQL,
            CLEAR_BASE_TEST_DATA_SQL
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void handleCancel_ValidSessionId_Success() throws Exception {
        // Given
        // Test data is set up via SQL scripts

        // When
        MvcResult result = mockMvc.perform(get(BASE_URL + CANCEL_ENDPOINT)
                        .param(SESSION_ID_PARAM, VALID_SESSION_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        PaymentDto actual = objectMapper.readValue(result.getResponse().getContentAsString(), PaymentDto.class);
        assertThat(actual.getId()).isEqualTo(VALID_PAYMENT_ID);
        assertThat(actual.getStatus()).isEqualTo(Payment.PaymentStatus.PENDING);
    }
}
