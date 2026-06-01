package com.ashish.claimbridge.claimservice.service;

import com.ashish.claimbridge.claimservice.model.OutBoxEvent;
import com.ashish.claimbridge.claimservice.repository.OutBoxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OutboxRelay {
    private final OutBoxRepository outBoxRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public OutboxRelay(OutBoxRepository outBoxRepository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.outBoxRepository = outBoxRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelayString = "${outbox.relay.fixed-delay-ms}")
    public void relay() {
        List<OutBoxEvent> unpublished= outBoxRepository.findByPublishedFalse();

        for(OutBoxEvent event :unpublished){
            try{
                kafkaTemplate.send(event.getTopic(),event.getPayload());
                event.setPublished(true);
                event.setPublishedAt(LocalDateTime.now());
                outBoxRepository.save(event);
                System.out.println("published Event:  " + event.getEventType());

            }catch (Exception e){
                System.out.println("failed to publish Event:  " + event.getEventType());
                throw new RuntimeException("failed to publish Event:  " + event.getEventType());
            }

        }

    }


}
