package org.example.radicalmotor.Services;

import lombok.RequiredArgsConstructor;
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


@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = {Exception.class, Throwable.class})
public class VehicleService {
    private final IVehicleRepository productRepository;

    public List<Vehicle> getAllVehicles(Integer pageNo, Integer pageSize, String sortBy) {
        return productRepository.findAllVehicles(pageNo, pageSize, sortBy);
    }

    public List<Vehicle> searchVehicles(String keyword, Integer pageNo, Integer pageSize, String sortBy) {
        return productRepository.searchVehicle(keyword);
    }

    public List<Vehicle> getVehiclesByType(String vehicleTypeName, Integer pageNo, Integer pageSize, String sortBy) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
        return productRepository.findByVehicleTypeName(vehicleTypeName, pageable).getContent();
    }

    public Map<String, String> getVehiclePrices() {
        List<Vehicle> vehicles = productRepository.findAll();
        Map<String, String> formattedPrices = new HashMap<>();
        DecimalFormat df = new DecimalFormat("#,###");

        for (Vehicle vehicle : vehicles) {
            String formattedPrice = df.format(vehicle.getCostId().getBaseCost());
            formattedPrices.put(vehicle.getChassisNumber(), formattedPrice);
        }

        return formattedPrices;
    }
}
