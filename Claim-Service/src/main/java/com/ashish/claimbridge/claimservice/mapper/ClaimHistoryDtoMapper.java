package com.ashish.claimbridge.claimservice.mapper;

import com.ashish.claimbridge.claimservice.dto.ClaimHistoryDto;
import com.ashish.claimbridge.claimservice.model.ClaimHistory;

public class ClaimHistoryDtoMapper {
    static public ClaimHistoryDto mapdto(ClaimHistory claimHistory) {
        ClaimHistoryDto dto = new ClaimHistoryDto();
        dto.setFromState(claimHistory.getFromState());
        dto.setToState(claimHistory.getToState());
        dto.setClaimId(claimHistory.getClaimId());
        dto.setNotes(claimHistory.getNotes());
        dto.setTriggeredBy(claimHistory.getTriggeredBy());
        dto.setTimestamp(claimHistory.getTimestamp());
        return dto;
    }
}
