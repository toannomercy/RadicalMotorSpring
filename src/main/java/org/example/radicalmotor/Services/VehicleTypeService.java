package org.example.radicalmotor.Services;

import lombok.RequiredArgsConstructor;
import org.example.radicalmotor.Entities.VehicleType;
import org.example.radicalmotor.Repositories.IVehicleTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = {Exception.class, Throwable.class})
public class VehicleTypeService {
    private final IVehicleTypeRepository vehicleTypeRepository;
    public List<VehicleType> getAllVehicleTypes() {
        return vehicleTypeRepository.findByIsDeletedFalse();
    }
    public Optional<VehicleType> getVehicleTypeById(Long vehicleTypeId) {
        return vehicleTypeRepository.findById(vehicleTypeId);
    }
    public void addVehicleType(VehicleType vehicleType) {
        vehicleTypeRepository.save(vehicleType);
    }
    public void updateVehicleType(VehicleType vehicleType) {
        VehicleType existingVehicleType = vehicleTypeRepository.findById(vehicleType
                        .getVehicleTypeId())
                .orElse(null);
        assert existingVehicleType != null;
        existingVehicleType.setVehicleTypeName(vehicleType.getVehicleTypeName());
        vehicleTypeRepository.save(existingVehicleType);
    }
}
