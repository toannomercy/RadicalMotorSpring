package org.example.radicalmotor.Repositories;


import org.example.radicalmotor.Entities.AppointmentDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentDetailRepository extends JpaRepository<AppointmentDetail, Long> {
}
