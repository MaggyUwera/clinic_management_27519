package kigali.clinc.clinicmanagement.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kigali.clinc.clinicmanagement.domain.Patient;
import kigali.clinc.clinicmanagement.repository.PatientRepository;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepo;

    public String savePatient(Patient patient) {
        if (patient.getFirstName() == null || patient.getLastName() == null)
            return "First name and last name are required";
        patientRepo.save(patient);
        return "Patient is saved Successfully";
    }

    public List<Patient> getAllPatients() {
        return patientRepo.findAll();
    }

    public Patient getPatientById(UUID id) {
        return patientRepo.findById(id).orElse(null);
    }

    public Patient getPatientBySsNumber(String ssNumber) {
        return patientRepo.findBySsNumber(ssNumber).orElse(null);
    }

    public List<Patient> getPatientsByLastName(String lastName) {
        return patientRepo.findByLastNameIgnoreCaseOrderByFirstNameAsc(lastName);
    }

    public List<Patient> getPatientsOfDoctor(UUID doctorId) {
        return patientRepo.findPatientsOfDoctor(doctorId);
    }

    public List<Patient> getFrequentPatients(long min) {
        return patientRepo.findFrequentPatients(min);
    }

    public String updatePatient(UUID id, Patient patient) {
        Patient existing = patientRepo.findById(id).orElse(null);
        if (existing == null) return "Patient with id " + id + " not found";
        if (patient.getFirstName() == null || patient.getLastName() == null)
            return "First name and last name are required";
        existing.setFirstName(patient.getFirstName());
        existing.setLastName(patient.getLastName());
        existing.setDateOfBirth(patient.getDateOfBirth());
        existing.setSsNumber(patient.getSsNumber());
        patientRepo.save(existing);
        return "Patient is updated Successfully";
    }

    @Transactional
    public String deletePatient(UUID id) {
        Patient existing = patientRepo.findById(id).orElse(null);
        if (existing == null) return "Patient with id " + id + " not found";
        if (!existing.getAppointments().isEmpty()) return "Patient has appointments and cannot be deleted";
        patientRepo.delete(existing);
        return "Patient is deleted Successfully";
    }
}
