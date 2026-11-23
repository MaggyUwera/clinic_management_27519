package kigali.clinc.clinicmanagement.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kigali.clinc.clinicmanagement.domain.Appointment;
import kigali.clinc.clinicmanagement.domain.Doctor;
import kigali.clinc.clinicmanagement.service.AppointmentService;
import kigali.clinc.clinicmanagement.service.DoctorService;

@RestController
@RequestMapping({"/api/doctor", "/api/doctors"})
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private AppointmentService appointmentService;

    @PutMapping(value = "/{doctorId}/assign-office/{officeId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> assignOffice(@PathVariable UUID doctorId, @PathVariable UUID officeId) {
        String msg = doctorService.assignOffice(doctorId, officeId);
        if (msg.equals("Office is assigned Successfully")) return new ResponseEntity<>(msg, HttpStatus.OK);
        if (msg.endsWith("not found")) return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(msg, HttpStatus.CONFLICT);
    }

    @PutMapping(value = "/{doctorId}/specializations/{specializationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addSpecialization(@PathVariable UUID doctorId, @PathVariable UUID specializationId) {
        String msg = doctorService.addSpecialization(doctorId, specializationId);
        if (msg.equals("Specialization added to the doctor")) return new ResponseEntity<>(msg, HttpStatus.OK);
        if (msg.contains("does not exist")) return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(msg, HttpStatus.CONFLICT);
    }

    @DeleteMapping(value = "/{doctorId}/specializations/{specializationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> removeSpecialization(@PathVariable UUID doctorId, @PathVariable UUID specializationId) {
        String msg = doctorService.removeSpecialization(doctorId, specializationId);
        if (msg.equals("Specialization removed from the doctor")) return new ResponseEntity<>(msg, HttpStatus.OK);
        if (msg.contains("does not exist")) return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(msg, HttpStatus.CONFLICT);
    }

    @PatchMapping(value = "/{doctorId}/office", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> changeOffice(@PathVariable UUID doctorId, @RequestParam int officeNumber) {
        String msg = doctorService.changeOffice(doctorId, officeNumber);
        if (msg.equals("Office changed successfully")) return new ResponseEntity<>(msg, HttpStatus.OK);
        if (msg.contains("does not exist")) return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(msg, HttpStatus.CONFLICT);
    }

    @GetMapping(value = "/{doctorId}/agenda", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAgenda(@PathVariable UUID doctorId, @RequestParam String date) {
        if (doctorService.getDoctorById(doctorId) == null)
            return new ResponseEntity<>("The doctor with that id does not exist", HttpStatus.NOT_FOUND);
        List<Appointment> agenda = appointmentService.getDoctorAgenda(doctorId, java.time.LocalDate.parse(date));
        return new ResponseEntity<>(agenda, HttpStatus.OK);
    }

    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Doctor>> search(@RequestParam String name) {
        return new ResponseEntity<>(doctorService.searchByName(name), HttpStatus.OK);
    }

    @GetMapping(value = "/workload", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Object[]>> getWorkload(@RequestParam String start, @RequestParam String end) {
        return new ResponseEntity<>(doctorService.getDoctorWorkload(
                java.time.LocalDate.parse(start), java.time.LocalDate.parse(end)), HttpStatus.OK);
    }

    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> saveDoctor(@RequestBody Doctor doctor) {
        String msg = doctorService.saveDoctor(doctor);
        if (msg.equals("Doctor is saved Successfully")) return new ResponseEntity<>(msg, HttpStatus.CREATED);
        return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Doctor>> getAllDoctors() {
        return new ResponseEntity<>(doctorService.getAllDoctors(), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getDoctorById(@PathVariable UUID id) {
        Doctor doctor = doctorService.getDoctorById(id);
        if (doctor != null) return new ResponseEntity<>(doctor, HttpStatus.OK);
        return new ResponseEntity<>("Doctor with id " + id + " not found", HttpStatus.NOT_FOUND);
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateDoctor(@PathVariable UUID id, @RequestBody Doctor doctor) {
        String msg = doctorService.updateDoctor(id, doctor);
        if (msg.equals("Doctor is updated Successfully")) return new ResponseEntity<>(msg, HttpStatus.OK);
        if (msg.endsWith("not found")) return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteDoctor(@PathVariable UUID id) {
        String msg = doctorService.deleteDoctor(id);
        if (msg.equals("Doctor is deleted Successfully")) return new ResponseEntity<>(msg, HttpStatus.OK);
        if (msg.endsWith("not found")) return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(msg, HttpStatus.CONFLICT);
    }
}
