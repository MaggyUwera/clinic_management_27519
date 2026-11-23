package kigali.clinc.clinicmanagement.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kigali.clinc.clinicmanagement.domain.Doctor;
import kigali.clinc.clinicmanagement.domain.Specialization;
import kigali.clinc.clinicmanagement.repository.DoctorRepository;
import kigali.clinc.clinicmanagement.repository.SpecializationRepository;

@Service
public class SpecializationService {

    @Autowired
    private SpecializationRepository specializationRepo;

    @Autowired
    private DoctorRepository doctorRepo;

    public String saveSpecialization(Specialization specialization) {
        if (specialization.getName() == null || specialization.getName().isBlank())
            return "Specialization name is required";
        specializationRepo.save(specialization);
        return "Specialization saved successfully";
    }

    public List<Specialization> getAllSpecializations() {
        return specializationRepo.findAll();
    }

    public List<Specialization> getUnusedSpecializations() {
        return specializationRepo.findUnusedSpecializations();
    }

    public Specialization getSpecializationById(UUID id) {
        return specializationRepo.findById(id).orElse(null);
    }

    public String updateSpecialization(UUID id, Specialization specialization) {
        Specialization existing = specializationRepo.findById(id).orElse(null);
        if (existing == null) return "Specialization with id " + id + " not found";
        if (specialization.getName() == null || specialization.getName().isBlank())
            return "Specialization name is required";
        existing.setName(specialization.getName());
        specializationRepo.save(existing);
        return "Specialization updated successfully";
    }

    public String deleteSpecialization(UUID id) {
        Specialization existing = specializationRepo.findById(id).orElse(null);
        if (existing == null) return "Specialization with id " + id + " not found";
        if (!existing.getDoctors().isEmpty()) return "Specialization has doctors assigned and cannot be deleted";
        specializationRepo.delete(existing);
        return "Specialization deleted successfully";
    }

    public String assignDoctor(UUID specializationId, UUID doctorId) {
        Specialization specialization = specializationRepo.findById(specializationId).orElse(null);
        if (specialization == null) return "Specialization with id " + specializationId + " not found";

        Doctor doctor = doctorRepo.findById(doctorId).orElse(null);
        if (doctor == null) return "Doctor with id " + doctorId + " not found";

        if (specialization.getDoctors().contains(doctor)) return "Doctor is already assigned to this specialization";

        specialization.getDoctors().add(doctor);
        specializationRepo.save(specialization);
        return "Doctor assigned to specialization successfully";
    }

    public String removeDoctor(UUID specializationId, UUID doctorId) {
        Specialization specialization = specializationRepo.findById(specializationId).orElse(null);
        if (specialization == null) return "Specialization with id " + specializationId + " not found";

        Doctor doctor = doctorRepo.findById(doctorId).orElse(null);
        if (doctor == null) return "Doctor with id " + doctorId + " not found";

        specialization.getDoctors().remove(doctor);
        specializationRepo.save(specialization);
        return "Doctor removed from specialization successfully";
    }
}
