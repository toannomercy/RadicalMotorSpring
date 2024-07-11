package org.example.radicalmotor.Controllers;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.example.radicalmotor.Entities.Image;
import org.example.radicalmotor.Entities.Vehicle;
import org.example.radicalmotor.Services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private VehicleTypeService vehicleTypeService;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private CostTableService costTableService;

    @Autowired
    private MenuService menuService;

    @GetMapping
    public String showAllProducts(
            @NotNull Model model,
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(defaultValue = "chassisNumber") String sortBy,
            @RequestParam(required = false) String searchString,
            @RequestParam(required = false) List<String> vehicleTypes,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {
        List<Vehicle> vehicles;
        if (vehicleTypes != null && !vehicleTypes.isEmpty() && minPrice != null && maxPrice != null) {
            vehicles = vehicleService.getFilteredVehicles(vehicleTypes, minPrice, maxPrice, pageNo, pageSize, sortBy);
        } else if (searchString != null && !searchString.isEmpty()) {
            vehicles = vehicleService.searchVehicleNamesAndTypes(searchString, pageNo, pageSize, sortBy);
        } else if (vehicleTypes != null && !vehicleTypes.isEmpty()) {
            vehicles = vehicleService.getVehiclesByType(String.valueOf(vehicleTypes), pageNo, pageSize, sortBy);
        } else {
            vehicles = vehicleService.getAllVehicles();
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

    @GetMapping("/detail/{chassisNumber}")
    public String showProductDetail(@NotNull Model model, @PathVariable String chassisNumber) {
        Optional<Vehicle> vehicleOptional = vehicleService.getByChassisNumber(chassisNumber);
        if (vehicleOptional.isEmpty()) {
            return "error/404";
        }
        Vehicle vehicle = vehicleOptional.get();
        List<Image> images = vehicle.getImages();
        String formattedPrice = formatPrice(vehicle.getCostId().getBaseCost());
        model.addAttribute("images", images);
        model.addAttribute("product", vehicle);
        model.addAttribute("vehicleTypes", vehicleTypeService.getAllVehicleTypes());
        model.addAttribute("suppliers", supplierService.getAllSuppliers());
        model.addAttribute("formattedPrice", formattedPrice);

        return "product/detail";
    }

    private List<Vehicle> filterVehiclesByPrice(List<Vehicle> vehicles, double minPrice, double maxPrice) {
        List<Vehicle> filteredVehicles = new ArrayList<>();
        for (Vehicle vehicle : vehicles) {
            double price = vehicle.getCostId().getBaseCost();
            if (price >= minPrice && price <= maxPrice) {
                filteredVehicles.add(vehicle);
            }
        }
        return filteredVehicles;
    }

    private String formatPrice(double price) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        DecimalFormat decimalFormat = new DecimalFormat("#,###", symbols);
        return decimalFormat.format(price);
    }

    @GetMapping("/autocomplete")
    public @ResponseBody List<Map<String, String>> autocomplete(@RequestParam String term) {
        List<Vehicle> vehicles = vehicleService.searchVehicleNamesAndTypes(term, 0, 20, "vehicleName");
        List<Map<String, String>> suggestions = new ArrayList<>();
        for (Vehicle vehicle : vehicles) {
            Map<String, String> suggestion = new HashMap<>();
            suggestion.put("label", vehicle.getVehicleName() + " (" + vehicle.getVehicleTypeId().getVehicleTypeName() + ")");
            suggestion.put("value", vehicle.getVehicleName());
            suggestions.add(suggestion);
        }
        return suggestions;
    }
}
