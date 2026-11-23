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
import org.springframework.web.bind.annotation.RestController;

import kigali.clinc.clinicmanagement.domain.Specialization;
import kigali.clinc.clinicmanagement.service.SpecializationService;

@RestController
@RequestMapping({"/api/specialization", "/api/specializations"})
public class SpecializationController {

    @Autowired
    private SpecializationService specializationService;

    @GetMapping(value = "/unused", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Specialization>> getUnused() {
        return new ResponseEntity<>(specializationService.getUnusedSpecializations(), HttpStatus.OK);
    }

    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> save(@RequestBody Specialization specialization) {
        String msg = specializationService.saveSpecialization(specialization);
        return msg.equals("Specialization saved successfully")
                ? new ResponseEntity<>(msg, HttpStatus.CREATED)
                : new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Specialization>> getAll() {
        return new ResponseEntity<>(specializationService.getAllSpecializations(), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        Specialization s = specializationService.getSpecializationById(id);
        return s != null ? new ResponseEntity<>(s, HttpStatus.OK)
                : new ResponseEntity<>("Specialization with id " + id + " not found", HttpStatus.NOT_FOUND);
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody Specialization specialization) {
        String msg = specializationService.updateSpecialization(id, specialization);
        if (msg.equals("Specialization updated successfully")) return new ResponseEntity<>(msg, HttpStatus.OK);
        if (msg.endsWith("not found")) return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        String msg = specializationService.deleteSpecialization(id);
        if (msg.equals("Specialization deleted successfully")) return new ResponseEntity<>(msg, HttpStatus.OK);
        if (msg.endsWith("not found")) return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(msg, HttpStatus.CONFLICT);
    }

    @PutMapping(value = "/{specializationId}/assign-doctor/{doctorId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> assignDoctor(@PathVariable UUID specializationId, @PathVariable UUID doctorId) {
        String msg = specializationService.assignDoctor(specializationId, doctorId);
        if (msg.equals("Doctor assigned to specialization successfully")) return new ResponseEntity<>(msg, HttpStatus.OK);
        if (msg.endsWith("not found")) return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(msg, HttpStatus.CONFLICT);
    }

    @DeleteMapping(value = "/{specializationId}/remove-doctor/{doctorId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> removeDoctor(@PathVariable UUID specializationId, @PathVariable UUID doctorId) {
        String msg = specializationService.removeDoctor(specializationId, doctorId);
        if (msg.endsWith("not found")) return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(msg, HttpStatus.OK);
    }
}
