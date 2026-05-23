package com.ashish.claimbridge.fraudservice.service;

import com.ashish.claimbridge.fraudservice.StrategyPattern.FraudRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FraudEvaluationService {
    private final List<FraudRule>fraudRules;
    @Autowired
    public FraudEvaluationService(List<FraudRule> fraudRules){
        this.fraudRules=fraudRules;
    }






}
