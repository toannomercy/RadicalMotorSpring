package org.example.radicalmotor.Repositories;

import org.example.radicalmotor.Entities.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IVehicleTypeRepository extends JpaRepository<VehicleType, Long> {
    List<VehicleType> findByIsDeletedFalse();
}
