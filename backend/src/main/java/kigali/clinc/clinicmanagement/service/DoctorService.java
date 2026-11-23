package kigali.clinc.clinicmanagement.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kigali.clinc.clinicmanagement.domain.AppointmentStatus;
import kigali.clinc.clinicmanagement.domain.Doctor;
import kigali.clinc.clinicmanagement.domain.Office;
import kigali.clinc.clinicmanagement.domain.Specialization;
import kigali.clinc.clinicmanagement.repository.AppointmentRepository;
import kigali.clinc.clinicmanagement.repository.DoctorRepository;
import kigali.clinc.clinicmanagement.repository.OfficeRepository;
import kigali.clinc.clinicmanagement.repository.SpecializationRepository;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepo;

    @Autowired
    private OfficeRepository officeRepo;

    @Autowired
    private SpecializationRepository specializationRepo;

    @Autowired
    private AppointmentRepository appointmentRepo;

    public String saveDoctor(Doctor doctor) {
        if (doctor.getFirstName() == null || doctor.getLastName() == null)
            return "First name and last name are required";
        doctor.setOffice(null);
        doctorRepo.save(doctor);
        return "Doctor is saved Successfully";
    }

    public List<Doctor> getAllDoctors() {
        return doctorRepo.findAll();
    }

    public Doctor getDoctorById(UUID id) {
        return doctorRepo.findById(id).orElse(null);
    }

    public String updateDoctor(UUID id, Doctor doctor) {
        Doctor existing = doctorRepo.findById(id).orElse(null);
        if (existing == null) return "Doctor with id " + id + " not found";
        if (doctor.getFirstName() == null || doctor.getLastName() == null)
            return "First name and last name are required";
        existing.setFirstName(doctor.getFirstName());
        existing.setLastName(doctor.getLastName());
        existing.setDateOfBirth(doctor.getDateOfBirth());
        doctorRepo.save(existing);
        return "Doctor is updated Successfully";
    }

    public String addSpecialization(UUID doctorId, UUID specializationId) {
        Doctor doctor = doctorRepo.findById(doctorId).orElse(null);
        if (doctor == null) return "The doctor with that id does not exist";

        Specialization spec = specializationRepo.findById(specializationId).orElse(null);
        if (spec == null) return "That specialization does not exist";

        if (doctor.getSpecializations().contains(spec)) return "The doctor already has that specialization";

        doctor.getSpecializations().add(spec);
        doctorRepo.save(doctor);
        return "Specialization added to the doctor";
    }

    public String removeSpecialization(UUID doctorId, UUID specializationId) {
        Doctor doctor = doctorRepo.findById(doctorId).orElse(null);
        if (doctor == null) return "The doctor with that id does not exist";

        Specialization spec = specializationRepo.findById(specializationId).orElse(null);
        if (spec == null) return "That specialization does not exist";

        if (!doctor.getSpecializations().contains(spec)) return "The doctor does not have that specialization";

        doctor.getSpecializations().remove(spec);
        doctorRepo.save(doctor);
        return "Specialization removed from the doctor";
    }

    public String assignOffice(UUID doctorId, UUID officeId) {
        Doctor doctor = doctorRepo.findById(doctorId).orElse(null);
        if (doctor == null) return "Doctor with id " + doctorId + " not found";

        Office office = officeRepo.findById(officeId).orElse(null);
        if (office == null) return "Office with id " + officeId + " not found";

        if (doctorRepo.existsByOfficeId(officeId) &&
                !officeId.equals(doctor.getOffice() != null ? doctor.getOffice().getId() : null))
            return "Office is already assigned to another doctor";

        doctor.setOffice(office);
        doctorRepo.save(doctor);
        return "Office is assigned Successfully";
    }

    public String changeOffice(UUID doctorId, int officeNumber) {
        Doctor doctor = doctorRepo.findById(doctorId).orElse(null);
        if (doctor == null) return "The doctor with that id does not exist";

        Office office = officeRepo.findByOfficeNumber(officeNumber);
        if (office == null) return "The office with that office number does not exist";

        if (doctorRepo.existsByOfficeId(office.getId()) &&
                !office.getId().equals(doctor.getOffice() != null ? doctor.getOffice().getId() : null))
            return "That office is taken by other doctor";

        doctor.setOffice(office);
        doctorRepo.save(doctor);
        return "Office changed successfully";
    }

    public List<Doctor> searchByName(String name) {
        return doctorRepo.searchByName(name);
    }

    public List<Object[]> getDoctorWorkload(LocalDate start, LocalDate end) {
        return appointmentRepo.findDoctorWorkload(AppointmentStatus.COMPLETED, start, end);
    }

    public String deleteDoctor(UUID id) {
        Doctor existing = doctorRepo.findById(id).orElse(null);
        if (existing == null) return "Doctor with id " + id + " not found";
        if (!existing.getAppointments().isEmpty()) return "Doctor has appointments and cannot be deleted";
        if (!existing.getSpecializations().isEmpty()) return "Doctor has specializations and cannot be deleted";
        doctorRepo.delete(existing);
        return "Doctor is deleted Successfully";
    }
}
