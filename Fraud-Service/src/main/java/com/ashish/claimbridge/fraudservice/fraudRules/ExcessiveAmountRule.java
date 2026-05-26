package com.ashish.claimbridge.fraudservice.fraudRules;

import com.ashish.claimbridge.fraudservice.StrategyPattern.FraudRule;
import com.ashish.claimbridge.fraudservice.dto.PrescriptionItemDto;
import com.ashish.claimbridge.fraudservice.event.ClaimSubmittedEvent;
import com.ashish.claimbridge.fraudservice.feignClient.PrescriptionClient;
import com.ashish.claimbridge.fraudservice.model.FraudResult;
import com.ashish.claimbridge.fraudservice.model.FraudRuleResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ExcessiveAmountRule implements FraudRule {
    private final PrescriptionClient prescriptionClient;
    @Autowired
    public ExcessiveAmountRule(PrescriptionClient prescriptionClient) {
        this.prescriptionClient = prescriptionClient;
    }

    @Override
    public FraudRuleResult evaluate(ClaimSubmittedEvent event) {
       try{
           ResponseEntity<List<PrescriptionItemDto>> items  = prescriptionClient.ItemListByPateintId(
                   event.getPatientId(),event.getInsurerId(),"SYSTEM_INTERNAL");
           List<PrescriptionItemDto>  prescriptionItemDtoList = items.getBody();

           if(prescriptionItemDtoList.isEmpty() || prescriptionItemDtoList.isEmpty()){
               return new FraudRuleResult(FraudResult.PASS,"Excessive Amount Rule"," NO items to Evalate");
           }
           boolean isFraudulent = false;
           StringBuilder fraudReason = new StringBuilder();
           for (PrescriptionItemDto item : prescriptionItemDtoList) {
               if (item.getStandardCost() == null ||
                       item.getBilledAmount() == null || item.getQuantity() == null) {
                   continue;
               }
               double expectedTotalCost = item.getStandardCost() * item.getQuantity();
               double billedAmount = item.getBilledAmount();
               if(billedAmount > expectedTotalCost*3){
                   isFraudulent = true;
                   fraudReason.append(String.format(
                           "Price Gouging Alert for drug [%s]: Hospital billed %.2f, but expected cost is only %.2f. ",
                           item.getDrugCode(), billedAmount, expectedTotalCost
                   ));
               }
           }
           if (isFraudulent) {
               return new FraudRuleResult(FraudResult.FLAGGED, "ExcessiveAmountRule", fraudReason.toString());
           }
           return new FraudRuleResult(FraudResult.PASS, "ExcessiveAmountRule", "Pass");

       }catch (Exception e){
           return new FraudRuleResult(FraudResult.PASS, "ExcessiveAmountRule", "Prescription lookup failed, skipping rule.");
       }

    }
}
