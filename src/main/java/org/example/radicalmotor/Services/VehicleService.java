package org.example.radicalmotor.Services;

import lombok.RequiredArgsConstructor;
import org.example.radicalmotor.Entities.Image;
import org.example.radicalmotor.Entities.Vehicle;
import org.example.radicalmotor.Repositories.IVehicleRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = {Exception.class, Throwable.class})
public class VehicleService {
    private final IVehicleRepository vehicleRepository;

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAllActiveVehicles();
    }

    public List<Vehicle> getVehiclesByType(String vehicleTypeName, Integer pageNo, Integer pageSize, String sortBy) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
        return vehicleRepository.findByVehicleTypeName(vehicleTypeName, pageable).getContent();
    }

    public List<Vehicle> getFilteredVehicles(List<String> vehicleTypes, Double minPrice, Double maxPrice, Integer pageNo, Integer pageSize, String sortBy) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
        return vehicleRepository.findByVehicleTypesAndPriceRange(vehicleTypes, minPrice, maxPrice, pageable).getContent();
    }

    public List<Vehicle> searchVehicleNamesAndTypes(String searchString, Integer pageNo, Integer pageSize, String sortBy) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
        return vehicleRepository.searchVehicleNamesAndTypes(searchString, pageable).getContent();
    }

    public Map<String, String> getVehiclePrices() {
        List<Vehicle> vehicles = vehicleRepository.findAll();
        Map<String, String> formattedPrices = new HashMap<>();
        DecimalFormat df = new DecimalFormat("#,###");

        for (Vehicle vehicle : vehicles) {
            String formattedPrice = df.format(vehicle.getCostId().getBaseCost());
            formattedPrices.put(vehicle.getChassisNumber(), formattedPrice);
        }

        return formattedPrices;
    }

    public Optional<Vehicle> getByChassisNumber(String chassisNumber) {
        return vehicleRepository.findById(chassisNumber);
    }

    public void addVehicle(Vehicle vehicle) {
        vehicleRepository.save(vehicle);
    }

    public void updateVehicle(Vehicle vehicle) {
        Vehicle existingVehicle = vehicleRepository.findById(vehicle.getChassisNumber()).orElse(null);
        assert existingVehicle != null;
        existingVehicle.setVehicleTypeId(vehicle.getVehicleTypeId());
        existingVehicle.setVehicleName(vehicle.getVehicleName());
        existingVehicle.setColor(vehicle.getColor());
        existingVehicle.setImportDate(vehicle.getImportDate());
        existingVehicle.setVersion(vehicle.getVersion());
        existingVehicle.setSegment(vehicle.getSegment());
        existingVehicle.setSupplierId(vehicle.getSupplierId());
        existingVehicle.setCostId(vehicle.getCostId());
        existingVehicle.setImages(vehicle.getImages());
        vehicleRepository.save(existingVehicle);
    }
}

