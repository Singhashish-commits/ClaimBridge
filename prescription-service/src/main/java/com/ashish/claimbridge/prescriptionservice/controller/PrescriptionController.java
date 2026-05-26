package com.ashish.claimbridge.prescriptionservice.controller;

import com.ashish.claimbridge.prescriptionservice.dto.ApiResponse;
import com.ashish.claimbridge.prescriptionservice.dto.DrugDto;
import com.ashish.claimbridge.prescriptionservice.dto.PrescriptionDto;
import com.ashish.claimbridge.prescriptionservice.dto.PrescriptionItemDto;
import com.ashish.claimbridge.prescriptionservice.service.PrescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/prescriptions")
public class PrescriptionController {
    private final PrescriptionService prescriptionService;
    @Autowired
    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    @PostMapping("/issue-new")
    public ResponseEntity<ApiResponse> issuePrescription(@RequestBody PrescriptionDto dto,
                                                         @RequestHeader("tenantId") String tenantId,
                                                         @RequestHeader("role")String role,
                                                         @RequestHeader("email")String email) {
       ApiResponse response = prescriptionService.createPrescription(dto,tenantId,role,email);
       return   new ResponseEntity<>(response, HttpStatus.OK);
    }
        @GetMapping("/{id}")
    public ResponseEntity<List<PrescriptionDto>> getPrescriptionsByPatientId(@PathVariable Long id,
                                                                     @RequestHeader("tenantId")String tenantId,
                                                                     @RequestHeader("role")String role){
            List<PrescriptionDto> result = prescriptionService.getPrescriptionsByPatientId(id,tenantId,role);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        @GetMapping("/prescription/{id}")
public ResponseEntity<PrescriptionDto> getPrescriptionById(@PathVariable Long id,
                                                           @RequestHeader("tenantId")String tenantId,
                                                          @RequestHeader("role")String role){
        PrescriptionDto dto = prescriptionService.getPrescriptionById(id,tenantId,role);
        return new ResponseEntity<>(dto, HttpStatus.OK);


}

@PatchMapping("{id}/dispense")
    public ResponseEntity<ApiResponse> dispense(@PathVariable Long id,
                                                @RequestHeader("tenantId")String tenantId,
                                                @RequestHeader("role")String role){
        ApiResponse response= prescriptionService.dispense(id,tenantId,role);
        return new ResponseEntity<>(response, HttpStatus.OK);
}

@GetMapping("/validate/{id}")
public ResponseEntity<PrescriptionDto> validateForClaim(@PathVariable Long id,
                                                    @RequestHeader("tenantId")String tenantId,
                                                    @RequestHeader("role")String role){
        PrescriptionDto dto = prescriptionService.validateForClaim(id,tenantId,role);
        return new ResponseEntity<>(dto, HttpStatus.OK);
}

@GetMapping("list/{patientId}")
    public ResponseEntity<List<PrescriptionItemDto>> ItemListByPateintId(
            @PathVariable("patientId")Long patientId,@RequestHeader("tenantId")String tenantId,
            @RequestHeader("role")String role){
      List<PrescriptionItemDto> items =  prescriptionService.getItemList(patientId,tenantId,role);
      return ResponseEntity.ok(items);

}







}
