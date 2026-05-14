package com.ashish.claimbridgeauthservice.Service;

import com.ashish.claimbridgeauthservice.event.HospitalCreateEvent;
import com.ashish.claimbridgeauthservice.event.InsurerCreateEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    private final KafkaTemplate<String,Object> kafkaTemplate;
    public void sendHospitalCreateEvent(HospitalCreateEvent hospital) {
        kafkaTemplate.send("hospital-created", hospital);
        System.out.println("Hospital event sent: " + hospital.getTenantId());
    }

    public void sendInsurerCreateEvent(InsurerCreateEvent insurer) {
        kafkaTemplate.send("insurer-created", insurer);
        System.out.println("Insurer event sent: " + insurer.getTenantId());
    }

}
