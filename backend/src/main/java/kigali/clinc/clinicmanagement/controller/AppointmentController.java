package kigali.clinc.clinicmanagement.controller;

import java.time.LocalDate;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kigali.clinc.clinicmanagement.domain.Appointment;
import kigali.clinc.clinicmanagement.domain.AppointmentStatus;
import kigali.clinc.clinicmanagement.service.AppointmentService;

@RestController
@RequestMapping({"/api/appointment", "/api/appointments"})
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @PostMapping(value = "/save/{patientId}/{doctorId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> saveAppointment(@PathVariable UUID patientId, @PathVariable UUID doctorId,
            @RequestBody Appointment appointment) {
        String msg = appointmentService.saveAppointment(patientId, doctorId, appointment);
        if (msg.equals("Appointment is saved Successfully")) return new ResponseEntity<>(msg, HttpStatus.CREATED);
        if (msg.equals("Appointment date cannot be in the past")) return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
        if (msg.equals("Doctor is already booked on that date")) return new ResponseEntity<>(msg, HttpStatus.CONFLICT);
        if (msg.endsWith("not found")) return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
    }

    @PatchMapping(value = "/{id}/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateStatus(@PathVariable UUID id, @RequestParam AppointmentStatus status) {
        String msg = appointmentService.updateStatus(id, status);
        if (msg.equals("Appointment status updated successfully")) return new ResponseEntity<>(msg, HttpStatus.OK);
        if (msg.equals("The appointment with that id does not exist")) return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
    }

    @PatchMapping(value = "/{id}/reschedule", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> reschedule(@PathVariable UUID id, @RequestParam String newDate) {
        String msg = appointmentService.reschedule(id, LocalDate.parse(newDate));
        if (msg.equals("Appointment rescheduled successfully")) return new ResponseEntity<>(msg, HttpStatus.OK);
        if (msg.equals("The appointment with that id does not exist")) return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
        if (msg.equals("Doctor is already booked on that date")) return new ResponseEntity<>(msg, HttpStatus.CONFLICT);
        return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> deleteAppointment(@PathVariable UUID id) {
        String msg = appointmentService.deleteAppointment(id);
        if (msg.equals("Appointment deleted successfully")) return new ResponseEntity<>(msg, HttpStatus.OK);
        if (msg.equals("The appointment with that id does not exist")) return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(msg, HttpStatus.CONFLICT);
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Appointment>> getAllAppointments() {
        return new ResponseEntity<>(appointmentService.getAllAppointments(), HttpStatus.OK);
    }

    @GetMapping(value = "/by-status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Appointment>> getByStatus(@RequestParam AppointmentStatus status) {
        return new ResponseEntity<>(appointmentService.getByStatus(status), HttpStatus.OK);
    }

    @GetMapping(value = "/between", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Appointment>> getBetween(@RequestParam String start, @RequestParam String end) {
        return new ResponseEntity<>(appointmentService.getBetween(LocalDate.parse(start), LocalDate.parse(end)), HttpStatus.OK);
    }

    @GetMapping(value = "/stats/by-status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Object[]>> getCountPerStatus() {
        return new ResponseEntity<>(appointmentService.getCountPerStatus(), HttpStatus.OK);
    }

    @PatchMapping(value = "/cancel-day", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> cancelDay(@RequestParam UUID doctorId, @RequestParam String date) {
        return new ResponseEntity<>(appointmentService.cancelDoctorDay(doctorId, LocalDate.parse(date)), HttpStatus.OK);
    }
}
