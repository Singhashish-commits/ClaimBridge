package com.ashish.claimbridge.claimservice.service;


import com.ashish.claimbridge.claimservice.model.OutBoxEvent;
import com.ashish.claimbridge.claimservice.repository.OutBoxRepository;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

@Service

public class OutBoxService {

    private final ObjectMapper objectMapper;
    private final OutBoxRepository outBoxRepository;

    public OutBoxService(ObjectMapper objectMapper, OutBoxRepository outBoxRepository) {
        this.objectMapper = objectMapper;
        this.outBoxRepository = outBoxRepository;
    }

    public void saveOutBoxEvent(String eventType, String topic , Object payload){
        try{
            OutBoxEvent outBoxEvent = new OutBoxEvent();
            outBoxEvent.setEventType(eventType);
            outBoxEvent.setTopic(topic);
            outBoxEvent.setPayload(objectMapper.writeValueAsString(payload));
            outBoxEvent.setPublished(false);
            outBoxEvent.setCreatedAt(LocalDateTime.now());
            outBoxRepository.save(outBoxEvent);
            System.out.println("OutBo Saved for the event " + eventType);


        }catch (Exception e){
            throw new RuntimeException("Falied to save the OutBox Message  "+e.getMessage());
        }

    }
}
