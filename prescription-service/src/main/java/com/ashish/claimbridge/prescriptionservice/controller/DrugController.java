package com.ashish.claimbridge.prescriptionservice.controller;

import com.ashish.claimbridge.prescriptionservice.dto.ApiResponse;
import com.ashish.claimbridge.prescriptionservice.dto.DrugDto;
import com.ashish.claimbridge.prescriptionservice.dto.DrugInsurerDto;
import com.ashish.claimbridge.prescriptionservice.service.DrugService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/drugs")
public class DrugController {
    private final DrugService drugService;
    @Autowired
    public DrugController(DrugService drugService){
        this.drugService= drugService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<DrugDto>> SearchDrug(
            @RequestParam String name,@RequestParam Long patientId,
            @RequestHeader("role")String role,@RequestHeader("tenantId")String tenantId){
        return ResponseEntity.ok(drugService.searchDrug(name,patientId,role,tenantId));
    }

    @GetMapping("/id/{drugCode}")
    public ResponseEntity<DrugDto> searchByDrugCode(
            @PathVariable("drugCode") String drugCode,@RequestHeader("role")String role,
            @RequestHeader("tenantId")String tenantId,@RequestParam Long patientId) {
        return ResponseEntity.ok(drugService.findByDrugCode(drugCode,patientId,tenantId,role));
    }

    @PostMapping("/new")
    public ResponseEntity<DrugInsurerDto> addNewDrug(
            @RequestBody DrugInsurerDto dto,
            @RequestHeader("role")String role,
            @RequestHeader("tenantID")String tenantId){
        return ResponseEntity.ok(drugService.addDrug(dto,role,tenantId));

    }
    @PostMapping("update/{id}")
    public ResponseEntity<DrugInsurerDto> updateDrugById(
            @PathVariable("id")Long id, @RequestBody DrugInsurerDto dto,
            @RequestHeader("role")String role, @RequestHeader("tenantId")String tenantId){
        return ResponseEntity.ok(drugService.updateDrugById(id,dto,role,tenantId));

    }
    @PostMapping("delete/{id}")
        public ResponseEntity<ApiResponse> deleteDrugById(
                @PathVariable("id") Long id, @RequestHeader("role")String role,
                @RequestHeader("tenantID")String tenantId){
        return ResponseEntity.ok(drugService.deleteById(id,role,tenantId));

        }




}
