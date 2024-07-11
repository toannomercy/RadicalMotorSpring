package org.example.radicalmotor.Repositories;

import org.example.radicalmotor.Entities.CostTable;
import org.example.radicalmotor.Entities.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ICostTableRepository extends JpaRepository<CostTable, Long> {
    List<CostTable> findByIsDeletedFalse();
    @Query("SELECT v FROM Vehicle v JOIN FETCH v.costId WHERE v.chassisNumber = :chassisNumber")
    Optional<Vehicle> findVehicleWithCostByChassisNumber(@Param("chassisNumber") String chassisNumber);
}
