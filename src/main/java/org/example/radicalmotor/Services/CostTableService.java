package org.example.radicalmotor.Services;

import lombok.RequiredArgsConstructor;
import org.example.radicalmotor.Entities.CostTable;
import org.example.radicalmotor.Entities.Vehicle;
import org.example.radicalmotor.Repositories.ICostTableRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = {Exception.class, Throwable.class})
public class CostTableService {
    private final ICostTableRepository costTableRepository;
    public List<CostTable> getAllCostTables() {
        return costTableRepository.findByIsDeletedFalse();
    }
    public Optional<CostTable> getCostTableById(Long id) {
        return costTableRepository.findById(id);
    }
    public void addCostTable(CostTable costTable) {
        costTableRepository.save(costTable);
    }
    public void updateCostTable(CostTable costTable) {
        CostTable existingCostTable = costTableRepository.findById(costTable.getCostId())
                .orElse(null);
        assert existingCostTable != null;
        existingCostTable.setDateCreated(costTable.getDateCreated());
        existingCostTable.setBaseCost(costTable.getBaseCost());
        costTableRepository.save(existingCostTable);
    }
    public double getBaseCostByChassisNumber(String chassisNumber) {
        Optional<Vehicle> vehicleOptional = costTableRepository.findVehicleWithCostByChassisNumber(chassisNumber);
        return vehicleOptional.map(vehicle -> {
            CostTable costTable = vehicle.getCostId();
            if (costTable != null) {
                return costTable.getBaseCost();
            }
            return 0.0;
        }).orElse(0.0);
    }
}
