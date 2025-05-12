package com.dfn.lsf.service.integration;

import com.dfn.lsf.model.CommonResponse;
import com.dfn.lsf.util.IntegrationConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import com.dfn.lsf.model.QueMsgDto;
@ExtendWith(MockitoExtension.class)
public class RestTemplateIntegrationServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private RestTemplateIntegrationService integrationService;
    
    private Executor virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor();

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(integrationService, "omsBaseUrl", "http://localhost:8080/oms");
        ReflectionTestUtils.setField(integrationService, "notificationBaseUrl", "http://localhost:8080/notification");
        ReflectionTestUtils.setField(integrationService, "iflexBaseUrl", "http://localhost:8080/iflex");
        ReflectionTestUtils.setField(integrationService, "virtualThreadExecutor", virtualThreadExecutor);
    }

    @Test
    public void testGetCustomerRelatedOmsData_Success() {
        // Arrange
        String requestBody = "{\"customerId\":\"123\"}";
        String expectedResponse = "{\"responseObject\":\"1||Success\"}";
        
        when(restTemplate.postForObject(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(expectedResponse);

        // Act
        String response = integrationService.getCustomerRelatedOmsData(requestBody);

        // Assert
        assertEquals(expectedResponse, response);
        verify(restTemplate).postForObject(
                eq("http://localhost:8080/oms" + IntegrationConstants.OMS_CUSTOMER_INFO_ENDPOINT),
                any(HttpEntity.class),
                eq(String.class));
    }

    @Test
    public void testGetCustomerRelatedOmsData_Error() {
        // Arrange
        String requestBody = "{\"customerId\":\"123\"}";
        
        when(restTemplate.postForObject(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            integrationService.getCustomerRelatedOmsData(requestBody);
        });
    }

    @Test
    public void testSendSmsNotification_Success() {
        // Arrange
        String requestBody = "{\"phone\":\"1234567890\",\"message\":\"Test\"}";
        String mockResponse = "{\"status\":\"OK\"}";
        
        when(restTemplate.postForObject(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(mockResponse);
        
        QueMsgDto queMsgDto = new QueMsgDto();
        queMsgDto.setQueueName("thirdPartySmsQueue");
        queMsgDto.setMessage(requestBody);

        // Act
        boolean result = integrationService.sendSmsNotification(queMsgDto);

        // Assert
        assertTrue(result);
        verify(restTemplate).postForObject(
                eq("http://localhost:8080/notification" + IntegrationConstants.SMS_ENDPOINT),
                any(HttpEntity.class),
                eq(String.class));
    }

    @Test
    public void testSendSmsNotification_Error() {
        // Arrange
        String requestBody = "{\"phone\":\"1234567890\",\"message\":\"Test\"}";
        
        when(restTemplate.postForObject(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        QueMsgDto queMsgDto = new QueMsgDto();
        queMsgDto.setQueueName("thirdPartySmsQueue");
        queMsgDto.setMessage(requestBody);

        // Act
        boolean result = integrationService.sendSmsNotification(queMsgDto);

        // Assert
        assertFalse(result);
    }

    @Test
    public void testProcessOmsCommonResponse_Success() {
        // Arrange
        String response = "{\"responseObject\":\"1||Success Message\"}";

        // Act
        CommonResponse result = integrationService.processOmsCommonResponse(response);

        // Assert
        assertEquals(200, result.getResponseCode());
        assertEquals("Success Message", result.getResponseMessage());
    }

    @Test
    public void testProcessOmsCommonResponse_Error() {
        // Arrange
        String response = "{\"responseObject\":\"-1||Error Message\"}";

        // Act
        CommonResponse result = integrationService.processOmsCommonResponse(response);

        // Assert
        assertEquals(500, result.getResponseCode());
        assertEquals("Error Message", result.getErrorMessage());
    }

    @Test
    public void testProcessOmsCommonResponse_NullResponse() {
        // Act
        CommonResponse result = integrationService.processOmsCommonResponse(null);

        // Assert
        assertEquals(500, result.getResponseCode());
    }

    @Test
    public void testSendRequestAsync() throws Exception {
        // Arrange
        String requestBody = "{\"data\":\"test\"}";
        String producerName = IntegrationConstants.PRODUCER_OMS;
        String expectedResponse = "{\"result\":\"success\"}";
        
        when(restTemplate.postForObject(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(expectedResponse);

        // Act
        CompletableFuture<String> future = integrationService.sendRequestAsync(requestBody, producerName);
        String result = future.get(); // This will block until the future completes

        // Assert
        assertEquals(expectedResponse, result);
        verify(restTemplate).postForObject(eq("http://localhost:8080/oms"), any(HttpEntity.class), eq(String.class));
    }
}