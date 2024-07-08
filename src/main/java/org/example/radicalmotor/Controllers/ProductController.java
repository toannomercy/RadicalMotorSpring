package org.example.radicalmotor.Controllers;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.example.radicalmotor.Entities.Image;
import org.example.radicalmotor.Entities.Vehicle;
import org.example.radicalmotor.Services.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final VehicleService vehicleService;
    private final VehicleTypeService vehicleTypeService;
    private final SupplierService supplierService;
    private final CostTableService costTableService;

    @GetMapping
    public String showAllProducts(
            @NotNull Model model,
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(defaultValue = "chassisNumber") String sortBy,
            @RequestParam(required = false) String searchString,
            @RequestParam(required = false) String vehicleType)
    {
        List<Vehicle> vehicles;
        if (searchString != null && !searchString.isEmpty()) {
            vehicles = vehicleService.searchVehicles(searchString, pageNo, pageSize, sortBy);
        } else if (vehicleType != null && !vehicleType.isEmpty()) {
            vehicles = vehicleService.getVehiclesByType(vehicleType, pageNo, pageSize, sortBy);
        } else {
            vehicles = vehicleService.getAllVehicles(pageNo, pageSize, sortBy);
        }

        Map<String, List<Image>> vehicleImages = new HashMap<>();
        Map<String, String> prices = vehicleService.getVehiclePrices();

        for (Vehicle vehicle : vehicles) {
            vehicleImages.put(vehicle.getChassisNumber(), vehicle.getImages());
        }

        model.addAttribute("products", vehicles);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("vehicleTypes", vehicleTypeService.getAllVehicleTypes());
        model.addAttribute("suppliers", supplierService.getAllSuppliers());
        model.addAttribute("costTables", costTableService.getAllCostTables());
        model.addAttribute("totalPages", (int) Math.ceil((double) vehicles.size() / pageSize));
        model.addAttribute("vehicleImages", vehicleImages);
        model.addAttribute("prices", prices);

        return "product/index";
    }

    public String showProductDetail(@NotNull Model model, @RequestParam String chassisNumber) {
        Vehicle vehicle = vehicleService.getByChassisNumber(chassisNumber);
        if (vehicle == null) {
            return "redirect:/products";
        }

        model.addAttribute("product", vehicle);
        model.addAttribute("vehicleTypes", vehicleTypeService.getAllVehicleTypes());
        model.addAttribute("suppliers", supplierService.getAllSuppliers());
        model.addAttribute("costTables", costTableService.getAllCostTables());

        return "product/detail";
    }

}
