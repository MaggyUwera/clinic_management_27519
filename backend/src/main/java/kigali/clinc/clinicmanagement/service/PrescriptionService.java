package kigali.clinc.clinicmanagement.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kigali.clinc.clinicmanagement.domain.Appointment;
import kigali.clinc.clinicmanagement.domain.AppointmentStatus;
import kigali.clinc.clinicmanagement.domain.Prescription;
import kigali.clinc.clinicmanagement.repository.AppointmentRepository;
import kigali.clinc.clinicmanagement.repository.PrescriptionRepository;

@Service
public class PrescriptionService {

    @Autowired
    private PrescriptionRepository prescriptionRepo;

    @Autowired
    private AppointmentRepository appointmentRepo;

    public String savePrescription(UUID appointmentId, Prescription prescription) {
        Appointment appointment = appointmentRepo.findById(appointmentId).orElse(null);
        if (appointment == null) return "The appointment with that id does not exist";

        if (appointment.getStatus() != AppointmentStatus.COMPLETED)
            return "Prescriptions are only for COMPLETED appointments";

        prescription.setIssuedDate(LocalDate.now());
        prescription.setAppointment(appointment);
        prescriptionRepo.save(prescription);
        return "Prescription saved successfully";
    }

    public String checkAppointmentExists(UUID appointmentId) {
        if (!appointmentRepo.existsById(appointmentId)) return "The appointment with that id does not exist";
        return null;
    }

    public List<Prescription> getByAppointment(UUID appointmentId) {
        return prescriptionRepo.findByAppointmentId(appointmentId);
    }

    public List<Prescription> getByPatientSsNumber(String ssNumber) {
        return prescriptionRepo.findByPatientSsNumber(ssNumber);
    }

    public String updatePrescription(UUID id, Prescription updated) {
        Prescription existing = prescriptionRepo.findById(id).orElse(null);
        if (existing == null) return "The prescription with that id does not exist";
        existing.setMedicineName(updated.getMedicineName());
        existing.setDosage(updated.getDosage());
        existing.setDurationDays(updated.getDurationDays());
        prescriptionRepo.save(existing);
        return "Prescription updated successfully";
    }

    public String deletePrescription(UUID id) {
        Prescription existing = prescriptionRepo.findById(id).orElse(null);
        if (existing == null) return "The prescription with that id does not exist";
        prescriptionRepo.delete(existing);
        return "Prescription deleted successfully";
    }

    public boolean appointmentHasPrescriptions(UUID appointmentId) {
        return prescriptionRepo.existsByAppointmentId(appointmentId);
    }

    public List<Object[]> getTopMedicines() {
        return prescriptionRepo.findTopMedicines();
    }
}
