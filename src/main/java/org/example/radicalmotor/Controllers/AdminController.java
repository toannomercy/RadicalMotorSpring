package org.example.radicalmotor.Controllers;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.example.radicalmotor.Entities.Vehicle;
import org.example.radicalmotor.Services.CostTableService;
import org.example.radicalmotor.Services.SupplierService;
import org.example.radicalmotor.Services.VehicleService;
import org.example.radicalmotor.Services.VehicleTypeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


import java.util.List;


@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final VehicleService vehicleService;
    private final VehicleTypeService vehicleTypeService;
    private final SupplierService supplierService;
    private final CostTableService costTableService;
    @GetMapping
    public String admin(
            @NotNull Model model,
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(defaultValue = "chassisNumber") String sortBy,
            @RequestParam(required = false) String searchString,
            @RequestParam(required = false) String vehicleType
    ) {
        List<Vehicle> vehicles;
        vehicles = vehicleService.getAllVehicles(pageNo, pageSize, sortBy);
        model.addAttribute("vehicles", vehicles);
        model.addAttribute("currentPage", pageNo);
        return "admin/index";
    }

}
