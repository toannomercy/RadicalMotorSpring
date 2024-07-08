package org.example.radicalmotor.Controllers;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.example.radicalmotor.Entities.Image;
import org.example.radicalmotor.Entities.Vehicle;
import org.example.radicalmotor.Services.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
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

    @GetMapping("/detail/{chassisNumber}")
    public String showProductDetail(@NotNull Model model, @PathVariable String chassisNumber) {
        Vehicle vehicle = vehicleService.getByChassisNumber(chassisNumber);
        if (vehicle == null) {
            return "error/404";
        }
        List<Image> images = vehicle.getImages();
        String formattedPrice = formatPrice(vehicle.getCostId().getBaseCost());
        model.addAttribute("images", images);
        model.addAttribute("product", vehicle);
        model.addAttribute("vehicleTypes", vehicleTypeService.getAllVehicleTypes());
        model.addAttribute("suppliers", supplierService.getAllSuppliers());
        model.addAttribute("formattedPrice", formattedPrice);

        return "product/detail";
    }
    private String formatPrice(double price) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        DecimalFormat decimalFormat = new DecimalFormat("#,###", symbols);
        return decimalFormat.format(price);
    }

}
