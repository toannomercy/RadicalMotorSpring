package org.example.radicalmotor.Repositories;

import org.example.radicalmotor.Entities.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {
    Optional<Service> findByServiceId(Long serviceId);
}
