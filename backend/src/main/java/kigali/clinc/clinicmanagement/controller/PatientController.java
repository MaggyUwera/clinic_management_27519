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

import kigali.clinc.clinicmanagement.domain.Appointment;
import kigali.clinc.clinicmanagement.domain.Patient;
import kigali.clinc.clinicmanagement.service.AppointmentService;
import kigali.clinc.clinicmanagement.service.DoctorService;
import kigali.clinc.clinicmanagement.service.PatientService;

@RestController
@RequestMapping({"/api/patient", "/api/patients"})
public class PatientController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private AppointmentService appointmentService;

    @GetMapping(value = "/by-last-name", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getByLastName(@RequestParam String lastName) {
        return new ResponseEntity<>(patientService.getPatientsByLastName(lastName), HttpStatus.OK);
    }

    @GetMapping(value = "/of-doctor/{doctorId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getPatientsOfDoctor(@PathVariable UUID doctorId) {
        if (doctorService.getDoctorById(doctorId) == null)
            return new ResponseEntity<>("The doctor with that id does not exist", HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(patientService.getPatientsOfDoctor(doctorId), HttpStatus.OK);
    }

    @GetMapping(value = "/frequent", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getFrequent(@RequestParam long min) {
        return new ResponseEntity<>(patientService.getFrequentPatients(min), HttpStatus.OK);
    }

    @GetMapping(value = "/{ssNumber}/upcoming", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUpcoming(@PathVariable String ssNumber) {
        if (patientService.getPatientBySsNumber(ssNumber) == null)
            return new ResponseEntity<>("That patient with that ssnumber does not exist", HttpStatus.NOT_FOUND);
        List<Appointment> upcoming = appointmentService.getUpcoming(ssNumber);
        return new ResponseEntity<>(upcoming, HttpStatus.OK);
    }

    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> savePatient(@RequestBody Patient patient) {
        String msg = patientService.savePatient(patient);
        if (msg.equals("Patient is saved Successfully")) return new ResponseEntity<>(msg, HttpStatus.CREATED);
        return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Patient>> getAllPatients() {
        return new ResponseEntity<>(patientService.getAllPatients(), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getPatientById(@PathVariable UUID id) {
        Patient patient = patientService.getPatientById(id);
        if (patient != null) return new ResponseEntity<>(patient, HttpStatus.OK);
        return new ResponseEntity<>("Patient with id " + id + " not found", HttpStatus.NOT_FOUND);
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updatePatient(@PathVariable UUID id, @RequestBody Patient patient) {
        String msg = patientService.updatePatient(id, patient);
        if (msg.equals("Patient is updated Successfully")) return new ResponseEntity<>(msg, HttpStatus.OK);
        if (msg.endsWith("not found")) return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deletePatient(@PathVariable UUID id) {
        String msg = patientService.deletePatient(id);
        if (msg.equals("Patient is deleted Successfully")) return new ResponseEntity<>(msg, HttpStatus.OK);
        if (msg.endsWith("not found")) return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(msg, HttpStatus.CONFLICT);
    }
}
