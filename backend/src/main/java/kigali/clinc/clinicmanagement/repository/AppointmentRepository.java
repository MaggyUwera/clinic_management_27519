package kigali.clinc.clinicmanagement.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kigali.clinc.clinicmanagement.domain.Appointment;
import kigali.clinc.clinicmanagement.domain.AppointmentStatus;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    List<Appointment> findByStatusOrderByAppointmentDateAsc(AppointmentStatus status);

    List<Appointment> findByAppointmentDateBetweenOrderByAppointmentDateAsc(LocalDate start, LocalDate end);

    boolean existsByDoctorIdAndAppointmentDateAndStatusNot(UUID doctorId, LocalDate date, AppointmentStatus status);

    @Query("SELECT a.status, COUNT(a) FROM Appointment a GROUP BY a.status")
    List<Object[]> countAppointmentsPerStatus();

    @Query("SELECT o.name, o.officeNumber, COUNT(a) FROM Appointment a JOIN a.doctor d JOIN d.office o GROUP BY o.id, o.name, o.officeNumber ORDER BY COUNT(a) DESC")
    List<Object[]> findBusiestOffices(Pageable pageable);

    @Modifying
    @Query("UPDATE Appointment a SET a.status = :cancelled WHERE a.doctor.id = :doctorId AND a.appointmentDate = :date AND a.status <> :completed")
    int cancelDoctorDay(@Param("doctorId") UUID doctorId, @Param("date") LocalDate date,
                        @Param("cancelled") AppointmentStatus cancelled,
                        @Param("completed") AppointmentStatus completed);

    List<Appointment> findByDoctorIdAndAppointmentDateAndStatusNot(UUID doctorId, LocalDate date, AppointmentStatus status);

    List<Appointment> findByPatientSsNumberAndAppointmentDateGreaterThanEqualAndStatusInOrderByAppointmentDateAsc(
            String ssNumber, LocalDate date, List<AppointmentStatus> statuses);

    long countByAppointmentDate(LocalDate date);

    long countByStatus(AppointmentStatus status);

    @Query("SELECT d.firstName, d.lastName, COUNT(a) FROM Appointment a JOIN a.doctor d WHERE a.status = :status AND a.appointmentDate BETWEEN :start AND :end GROUP BY d.id, d.firstName, d.lastName ORDER BY COUNT(a) DESC")
    List<Object[]> findDoctorWorkload(@Param("status") AppointmentStatus status, @Param("start") LocalDate start, @Param("end") LocalDate end);
}
