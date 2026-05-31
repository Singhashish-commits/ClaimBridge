package com.ashish.claimbridge.claimservice.repository;

import com.ashish.claimbridge.claimservice.model.OutBoxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutBoxRepository  extends JpaRepository<OutBoxEvent,Long> {

    List<OutBoxEvent> findByPublishedFalse();
}
