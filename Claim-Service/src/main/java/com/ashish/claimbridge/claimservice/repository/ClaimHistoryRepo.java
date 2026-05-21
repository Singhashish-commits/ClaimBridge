package com.ashish.claimbridge.claimservice.repository;

import com.ashish.claimbridge.claimservice.model.ClaimHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClaimHistoryRepo extends JpaRepository<ClaimHistory, Long> {
  List<ClaimHistory> findByClaimId(Long claimId);
}
