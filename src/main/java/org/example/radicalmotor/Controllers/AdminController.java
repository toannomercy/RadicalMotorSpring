package org.example.radicalmotor.Controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.example.radicalmotor.Entities.CostTable;
import org.example.radicalmotor.Entities.Supplier;
import org.example.radicalmotor.Entities.Vehicle;
import org.example.radicalmotor.Entities.VehicleType;
import org.example.radicalmotor.Services.CostTableService;
import org.example.radicalmotor.Services.SupplierService;
import org.example.radicalmotor.Services.VehicleService;
import org.example.radicalmotor.Services.VehicleTypeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


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
    @GetMapping("/supplier")
    public String showSupplier(
            @NotNull Model model,
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(defaultValue = "supplierId") String sortBy
    ) {
        model.addAttribute("suppliers", supplierService.getAllSuppliers());
        model.addAttribute("currentPage", pageNo);
        return "admin/supplier";
    }
    @GetMapping("/addSupplier")
    public String addSupplierForm() {
        return "admin/addSupplier";
    }
    @PostMapping("/addSupplier")
    public String addSupplier(
            @Valid @ModelAttribute("supplier") Supplier supplier,
            RedirectAttributes redirectAttributes
    ) {
        supplier.setIsDeleted(false);
        supplierService.addSupplier(supplier);
        redirectAttributes.addFlashAttribute("message", "Supplier added successfully!");
        return "redirect:/admin/supplier";
    }
    @GetMapping("/vehicleType")
    public String showVehicleTypes(
            @NotNull Model model,
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(defaultValue = "vehicleTypeId") String sortBy
    ) {
        model.addAttribute("vehicleTypes", vehicleTypeService.getAllVehicleTypes());
        model.addAttribute("currentPage", pageNo);
        return "admin/vehicleType";
    }
    @GetMapping("/addVehicleType")
    public String addVehicleTypeForm() {
        return "admin/addVehicleType";
    }
    @PostMapping("/addVehicleType")
    public String addVehicleType(
            @Valid @ModelAttribute("vehicleType") VehicleType vehicleType,
            RedirectAttributes redirectAttributes
    ) {
        vehicleType.setIsDeleted(false);
        vehicleTypeService.addVehicleType(vehicleType);
        redirectAttributes.addFlashAttribute("message", "Book added successfully!");
        return "redirect:/admin/vehicleType";
    }
    @GetMapping("/cost")
    public String showCostTables(
            @NotNull Model model,
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(defaultValue = "costId") String sortBy
    ) {
        model.addAttribute("costTables", costTableService.getAllCostTables());
        model.addAttribute("currentPage", pageNo);
        return "admin/cost";
    }
    @GetMapping("/addCost")
    public String addCostTableForm() {
        return "admin/addCost";
    }
    @PostMapping("/addCost")
    public String addCostTable(
            @Valid @ModelAttribute("costTable") CostTable costTable,
            RedirectAttributes redirectAttributes
    ) {
        costTable.setIsDeleted(false);
        costTableService.addCostTable(costTable);
        redirectAttributes.addFlashAttribute("message", "Book added successfully!");
        return "redirect:/admin/cost";
    }

}
