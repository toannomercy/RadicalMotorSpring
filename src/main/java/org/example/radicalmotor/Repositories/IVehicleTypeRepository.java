package org.example.radicalmotor.Repositories;

import org.example.radicalmotor.Entities.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IVehicleTypeRepository extends JpaRepository<VehicleType, Long> {
}
