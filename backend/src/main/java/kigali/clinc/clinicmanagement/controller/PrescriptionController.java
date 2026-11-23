package kigali.clinc.clinicmanagement.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kigali.clinc.clinicmanagement.domain.Prescription;
import kigali.clinc.clinicmanagement.service.PrescriptionService;

@RestController
@RequestMapping({"/api/prescription", "/api/prescriptions"})
public class PrescriptionController {

    @Autowired
    private PrescriptionService prescriptionService;

    @GetMapping(value = "/stats/top-medicines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Object[]>> getTopMedicines() {
        return new ResponseEntity<>(prescriptionService.getTopMedicines(), HttpStatus.OK);
    }

    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> save(@RequestParam UUID appointmentId, @RequestBody Prescription prescription) {
        String msg = prescriptionService.savePrescription(appointmentId, prescription);
        if (msg.equals("Prescription saved successfully")) return new ResponseEntity<>(msg, HttpStatus.CREATED);
        if (msg.equals("The appointment with that id does not exist")) return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
    }

    @GetMapping(value = "/appointment/{appointmentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getByAppointment(@PathVariable UUID appointmentId) {
        String check = prescriptionService.checkAppointmentExists(appointmentId);
        if (check != null) return new ResponseEntity<>(check, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(prescriptionService.getByAppointment(appointmentId), HttpStatus.OK);
    }

    @GetMapping(value = "/patient/{ssNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Prescription>> getByPatient(@PathVariable String ssNumber) {
        return new ResponseEntity<>(prescriptionService.getByPatientSsNumber(ssNumber), HttpStatus.OK);
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> update(@PathVariable UUID id, @RequestBody Prescription prescription) {
        String msg = prescriptionService.updatePrescription(id, prescription);
        if (msg.equals("Prescription updated successfully")) return new ResponseEntity<>(msg, HttpStatus.OK);
        return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
    }

    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        String msg = prescriptionService.deletePrescription(id);
        if (msg.equals("Prescription deleted successfully")) return new ResponseEntity<>(msg, HttpStatus.OK);
        return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
    }
}
