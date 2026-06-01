package com.ashish.claimbridge.notificationservice.repository;

import com.ashish.claimbridge.notificationservice.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository  extends JpaRepository<Notification, Long> {

}
