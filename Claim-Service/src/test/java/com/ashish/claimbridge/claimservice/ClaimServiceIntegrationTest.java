package com.ashish.claimbridge.claimservice;

import com.ashish.claimbridge.claimservice.dto.ApiResponse;
import com.ashish.claimbridge.claimservice.dto.ClaimSubmitDto;
import com.ashish.claimbridge.claimservice.dto.PrescriptionDto;
import com.ashish.claimbridge.claimservice.feignClient.PatientClient;
import com.ashish.claimbridge.claimservice.feignClient.PrescriptionClient;
import com.ashish.claimbridge.claimservice.model.Claim;
import com.ashish.claimbridge.claimservice.model.ClaimStatus;
import com.ashish.claimbridge.claimservice.repository.ClaimRepository;
import com.ashish.claimbridge.claimservice.service.ClaimService;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

@SpringBootTest
@Testcontainers
class ClaimServiceIntegrationTest {
    private static final SecureRandom secureRandom = new SecureRandom();

    // 1. Start PostgreSQL Container
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("claims_db")
            .withUsername("testuser")
            .withPassword("testpass");

    // 2. Start Kafka Container
    @Container
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0"));

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @Autowired
    private ClaimService claimService;

    @Autowired
    private ClaimRepository claimRepository;
    public static long generateSecureRandomLong() {
        return secureRandom.nextLong();
    }

    // Mocking external dependencies to isolate Postgres and Kafka testing
    @Mock
    private RedissonClient redissonClient;
    @Mock
    private PatientClient patientClient;
    @Mock
    private PrescriptionClient prescriptionClient;

    private Consumer<String, String> kafkaConsumer;

    @BeforeEach
    void setUp() {
        // Setup a native Kafka consumer to read the messages produced by your service
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(kafka.getBootstrapServers(), "test-group", true);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        DefaultKafkaConsumerFactory<String, String> cf = new DefaultKafkaConsumerFactory<>(consumerProps);
        kafkaConsumer = cf.createConsumer();

        // Subscribe to the topic your producer sends to (update topic name if different)
        kafkaConsumer.subscribe(Collections.singleton("claim-submitted"));
    }

    @AfterEach
    void tearDown() {
        claimRepository.deleteAll(); // Clean up Postgres after each test
        kafkaConsumer.close();
    }

    @Test
    void submitClaim_PersistsToPostgresAndPublishesToKafka() throws InterruptedException {
        // --- ARRANGE ---
        ClaimSubmitDto dto = new ClaimSubmitDto();
        dto.setPrescriptionId(secureRandom.nextLong());
        dto.setPatientId(secureRandom.nextLong());
        dto.setTotalClaimAmount(500.0);

        // Mock Redis Lock to always succeed
        RLock mockLock = mock(RLock.class);
        when(redissonClient.getLock(anyString())).thenReturn(mockLock);
        when(mockLock.tryLock(anyLong(), anyLong(), eq(TimeUnit.SECONDS))).thenReturn(true);
        when(mockLock.isHeldByCurrentThread()).thenReturn(true);

        // Mock Feign Clients
        PrescriptionDto mockPrescription = new PrescriptionDto();
        mockPrescription.setPrescriptionStatus("DISPENSED");
        when(prescriptionClient.validateForClaim(eq(generateSecureRandomLong()), eq("HOSPITAL-1"), eq("SYSTEM_INTERNAL")))
                .thenReturn(ResponseEntity.ok(mockPrescription));

        // --- ACT ---
        ApiResponse response = claimService.submitClaim(dto, "HOSPITAL-1", "ROLE_HOSPITAL", "doc@hospital.com");

        // --- ASSERT ---
        assertThat(response.isStatus()).isTrue();

        // 1. Verify PostgreSQL Integration
        List<Claim> savedClaims = claimRepository.findAll();
        assertThat(savedClaims).hasSize(1);
        Claim saved = savedClaims.get(0);
        assertThat(saved.getPrescriptionId()).isEqualTo("RX-1001");
        assertThat(saved.getStatus()).isEqualTo(ClaimStatus.SUBMITTED);
        assertThat(saved.getTotalClaimAmount()).isEqualTo(500.0);

        // 2. Verify Kafka Integration
        // Read records from the test container topic
        ConsumerRecords<String, String> records = KafkaTestUtils.getRecords(kafkaConsumer, Duration.ofMinutes(5));
        assertThat(records.count()).isEqualTo(1);

        // Assuming your event serializes to JSON, check that it contains the claim ID
        String payload = records.iterator().next().value();
        assertThat(payload).contains(saved.getId().toString());
    }
}