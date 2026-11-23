package kigali.clinc.clinicmanagement.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kigali.clinc.clinicmanagement.domain.Office;
import kigali.clinc.clinicmanagement.repository.AppointmentRepository;
import kigali.clinc.clinicmanagement.repository.DoctorRepository;
import kigali.clinc.clinicmanagement.repository.OfficeRepository;

@Service
public class OfficeService {

    @Autowired
    private OfficeRepository officeRepo;

    @Autowired
    private DoctorRepository doctorRepo;

    @Autowired
    private AppointmentRepository appointmentRepo;

    public List<Office> getFreeOffices() {
        return officeRepo.findFreeOffices();
    }

    public Optional<Object[]> getBusiestOffice() {
        return appointmentRepo.findBusiestOffices(PageRequest.of(0, 1)).stream().findFirst();
    }

    public String saveOffice(Office office) {
        if (officeRepo.findByOfficeNumber(office.getOfficeNumber()) != null)
            return "Office with that number " + office.getOfficeNumber() + " already exists";
        officeRepo.save(office);
        return "Office is saved Successfully";
    }

    public List<Office> getAllOffices() {
        return officeRepo.findAll();
    }

    public Office getOfficeById(UUID id) {
        return officeRepo.findById(id).orElse(null);
    }

    public String updateOffice(UUID id, Office office) {
        Office existing = officeRepo.findById(id).orElse(null);
        if (existing == null) return "Office with id " + id + " not found";
        Office sameNumber = officeRepo.findByOfficeNumber(office.getOfficeNumber());
        if (sameNumber != null && !sameNumber.getId().equals(id))
            return "Office with that number " + office.getOfficeNumber() + " already exists";
        existing.setName(office.getName());
        existing.setOfficeNumber(office.getOfficeNumber());
        officeRepo.save(existing);
        return "Office is updated Successfully";
    }

    @Transactional
    public String deleteOffice(UUID id) {
        Office existing = officeRepo.findById(id).orElse(null);
        if (existing == null) return "Office with id " + id + " not found";
        officeRepo.delete(existing);
        return "Office is deleted Successfully";
    }
}
