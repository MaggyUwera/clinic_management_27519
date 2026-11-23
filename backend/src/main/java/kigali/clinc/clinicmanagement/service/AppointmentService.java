package kigali.clinc.clinicmanagement.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kigali.clinc.clinicmanagement.domain.Appointment;
import kigali.clinc.clinicmanagement.domain.AppointmentStatus;
import kigali.clinc.clinicmanagement.domain.Doctor;
import kigali.clinc.clinicmanagement.domain.Patient;
import kigali.clinc.clinicmanagement.repository.AppointmentRepository;
import kigali.clinc.clinicmanagement.repository.DoctorRepository;
import kigali.clinc.clinicmanagement.repository.PatientRepository;
import kigali.clinc.clinicmanagement.repository.PrescriptionRepository;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepo;

    @Autowired
    private PatientRepository patientRepo;

    @Autowired
    private DoctorRepository doctorRepo;

    @Autowired
    private PrescriptionRepository prescriptionRepo;

    public String saveAppointment(UUID patientId, UUID doctorId, Appointment appointment) {
        Patient patient = patientRepo.findById(patientId).orElse(null);
        if (patient == null) return "Patient with id " + patientId + " not found";

        Doctor doctor = doctorRepo.findById(doctorId).orElse(null);
        if (doctor == null) return "Doctor with id " + doctorId + " not found";

        if (appointment.getAppointmentDate() == null) return "Appointment date is required";

        if (appointment.getAppointmentDate().isBefore(LocalDate.now()))
            return "Appointment date cannot be in the past";

        if (appointmentRepo.existsByDoctorIdAndAppointmentDateAndStatusNot(
                doctorId, appointment.getAppointmentDate(), AppointmentStatus.CANCELLED))
            return "Doctor is already booked on that date";

        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointmentRepo.save(appointment);
        return "Appointment is saved Successfully";
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepo.findAll();
    }

    public List<Appointment> getByStatus(AppointmentStatus status) {
        return appointmentRepo.findByStatusOrderByAppointmentDateAsc(status);
    }

    public List<Appointment> getBetween(LocalDate start, LocalDate end) {
        return appointmentRepo.findByAppointmentDateBetweenOrderByAppointmentDateAsc(start, end);
    }

    public String updateStatus(UUID id, AppointmentStatus newStatus) {
        Appointment appointment = appointmentRepo.findById(id).orElse(null);
        if (appointment == null) return "The appointment with that id does not exist";

        AppointmentStatus current = appointment.getStatus();

        boolean allowed = (current == AppointmentStatus.SCHEDULED &&
                (newStatus == AppointmentStatus.CONFIRMED || newStatus == AppointmentStatus.CANCELLED))
                || (current == AppointmentStatus.CONFIRMED &&
                (newStatus == AppointmentStatus.COMPLETED || newStatus == AppointmentStatus.CANCELLED));

        if (!allowed)
            return "Cannot change status from " + current + " to " + newStatus;

        appointment.setStatus(newStatus);
        appointmentRepo.save(appointment);
        return "Appointment status updated successfully";
    }

    public String reschedule(UUID id, LocalDate newDate) {
        Appointment appointment = appointmentRepo.findById(id).orElse(null);
        if (appointment == null) return "The appointment with that id does not exist";

        if (appointment.getStatus() == AppointmentStatus.COMPLETED)
            return "A COMPLETED appointment cannot be rescheduled";

        if (appointment.getStatus() == AppointmentStatus.CANCELLED)
            return "A CANCELLED appointment cannot be rescheduled";

        if (newDate.isBefore(LocalDate.now()))
            return "Appointment date cannot be in the past";

        if (appointmentRepo.existsByDoctorIdAndAppointmentDateAndStatusNot(
                appointment.getDoctor().getId(), newDate, AppointmentStatus.CANCELLED))
            return "Doctor is already booked on that date";

        appointment.setAppointmentDate(newDate);
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointmentRepo.save(appointment);
        return "Appointment rescheduled successfully";
    }

    public List<Object[]> getCountPerStatus() {
        return appointmentRepo.countAppointmentsPerStatus();
    }

    @Transactional
    public String cancelDoctorDay(UUID doctorId, LocalDate date) {
        int cancelled = appointmentRepo.cancelDoctorDay(doctorId, date,
                AppointmentStatus.CANCELLED, AppointmentStatus.COMPLETED);
        return cancelled + " appointments cancelled";
    }

    public List<Appointment> getDoctorAgenda(UUID doctorId, LocalDate date) {
        return appointmentRepo.findByDoctorIdAndAppointmentDateAndStatusNot(
                doctorId, date, AppointmentStatus.CANCELLED);
    }

    public List<Appointment> getUpcoming(String ssNumber) {
        return appointmentRepo
                .findByPatientSsNumberAndAppointmentDateGreaterThanEqualAndStatusInOrderByAppointmentDateAsc(
                        ssNumber, LocalDate.now(),
                        List.of(AppointmentStatus.SCHEDULED, AppointmentStatus.CONFIRMED));
    }

    public long countByStatus(AppointmentStatus status) {
        return appointmentRepo.countByStatus(status);
    }

    public long countToday() {
        return appointmentRepo.countByAppointmentDate(LocalDate.now());
    }

    public String deleteAppointment(UUID id) {
        Appointment appointment = appointmentRepo.findById(id).orElse(null);
        if (appointment == null) return "The appointment with that id does not exist";
        if (prescriptionRepo.existsByAppointmentId(id)) return "That appointment has prescriptions, delete them first";
        appointmentRepo.delete(appointment);
        return "Appointment deleted successfully";
    }
}
