package org.example.radicalmotor.Controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.example.radicalmotor.Entities.*;
import org.example.radicalmotor.Services.*;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


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
        vehicles = vehicleService.getAllVehicles();
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
        return "redirect:/admin/addSupplier";
    }
    @GetMapping("/editSupplier/{supplierId}")
    public String editSupplierForm(@NotNull Model model, @PathVariable long supplierId)
    {
        var supplier = supplierService.getSupplierById(supplierId);
        model.addAttribute("supplier", supplier.orElseThrow(() -> new
                IllegalArgumentException("Supplier not found")));
        return "admin/editSupplier";
    }
    @PostMapping("/editSupplier")
    public String editSupplier(
            @Valid @ModelAttribute("supplier") Supplier supplier,
            @NotNull BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            var errors = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toArray(String[]::new);
            model.addAttribute("errors", errors);
            return "admin/editSupplier";
        }
        supplierService.updateSupplier(supplier);
        redirectAttributes.addFlashAttribute("message", "Supplier updated successfully!");
        return "redirect:/admin/supplier";
    }
    @PostMapping("/deleteSupplier/{supplierId}")
    public String deleteSupplier(@PathVariable long supplierId, RedirectAttributes redirectAttributes) {
        var supplier = supplierService.getSupplierById(supplierId).orElseThrow(() -> new IllegalArgumentException("Supplier not found"));
        supplier.setIsDeleted(true);
        supplierService.updateSupplier(supplier);
        redirectAttributes.addFlashAttribute("message", "Supplier deleted successfully!");
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
        redirectAttributes.addFlashAttribute("message", "Vehicle Type added successfully!");
        return "redirect:/admin/addVehicleType";
    }
    @GetMapping("/editVehicleType/{vehicleTypeId}")
    public String editVehicleTypeForm(@NotNull Model model, @PathVariable long vehicleTypeId)
    {
        var vehicleType = vehicleTypeService.getVehicleTypeById(vehicleTypeId);
        model.addAttribute("vehicleType", vehicleType.orElseThrow(() -> new
                IllegalArgumentException("Vehicle Type not found")));
        return "admin/editVehicleType";
    }
    @PostMapping("/editVehicleType")
    public String editVehicleType(
            @Valid @ModelAttribute("vehicleType") VehicleType vehicleType,
            @NotNull BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            var errors = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toArray(String[]::new);
            model.addAttribute("errors", errors);
            return "admin/editVehicleType";
        }
        vehicleTypeService.updateVehicleType(vehicleType);
        redirectAttributes.addFlashAttribute("message", "Vehicle Type updated successfully!");
        return "redirect:/admin/vehicleType";
    }
    @PostMapping("/deleteVehicleType/{vehicleTypeId}")
    public String deleteVehicleType(@PathVariable long vehicleTypeId, RedirectAttributes redirectAttributes) {
        var vehicleType = vehicleTypeService.getVehicleTypeById(vehicleTypeId).orElseThrow(() -> new IllegalArgumentException("Vehicle Type not found"));
        vehicleType.setIsDeleted(true);
        vehicleTypeService.updateVehicleType(vehicleType);
        redirectAttributes.addFlashAttribute("message", "Vehicle Type deleted successfully!");
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
        redirectAttributes.addFlashAttribute("message", "CostId added successfully!");
        return "redirect:/admin/addCost";
    }
    @GetMapping("/editCostTable/{costId}")
    public String editCostTableForm(@NotNull Model model, @PathVariable long costId)
    {
        var costTable = costTableService.getCostTableById(costId);
        model.addAttribute("costTable", costTable.orElseThrow(() -> new
                IllegalArgumentException("CostId not found")));
        return "admin/editCostTable";
    }
    @PostMapping("/editCostTable")
    public String editCostTable(
            @Valid @ModelAttribute("costTable") CostTable costTable,
            @NotNull BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            var errors = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toArray(String[]::new);
            model.addAttribute("errors", errors);
            return "admin/editCostTable";
        }
        costTableService.updateCostTable(costTable);
        redirectAttributes.addFlashAttribute("message", "CostId updated successfully!");
        return "redirect:/admin/cost";
    }
    @PostMapping("/deleteCostTable/{costId}")
    public String deleteCostTable(@PathVariable long costId, RedirectAttributes redirectAttributes) {
        var costTable = costTableService.getCostTableById(costId).orElseThrow(() -> new IllegalArgumentException("CostId not found"));
        costTable.setIsDeleted(true);
        costTableService.updateCostTable(costTable);
        redirectAttributes.addFlashAttribute("message", "CostId deleted successfully!");
        return "redirect:/admin/cost";
    }

    @GetMapping("/addVehicle")
    public String addVehicleForm(@NotNull Model model) {
        model.addAttribute("vehicleTypes", vehicleTypeService.getAllVehicleTypes());
        model.addAttribute("suppliers", supplierService.getAllSuppliers());
        model.addAttribute("costTables", costTableService.getAllCostTables());
        model.addAttribute("vehicle", new Vehicle());
        return "admin/addVehicle";
    }

    @PostMapping("/addVehicle")
    public String addVehicle(
            @Valid @ModelAttribute("vehicle") Vehicle vehicle,
            @NotNull BindingResult bindingResult,
            @RequestParam("files") MultipartFile[] files,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        List<Image> images = new ArrayList<>();
        Path uploadDir = Paths.get("src/main/resources/static/img");

        try {
            Files.createDirectories(uploadDir);
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
                    Path targetLocation = uploadDir.resolve(fileName);

                    try (InputStream inputStream = file.getInputStream()) {
                        Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
                    }

                    Image image = new Image();
                    image.setImageUrl("/img/" + fileName);
                    image.setChassisNumber(vehicle);
                    images.add(image);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to store files", e);
        }

        vehicle.setImages(images);
        vehicle.setIsDeleted(false);
        vehicle.setSold(true);

        if (bindingResult.hasErrors()) {
            var errors = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toArray(String[]::new);
            model.addAttribute("errors", errors);
            model.addAttribute("vehicleTypes", vehicleTypeService.getAllVehicleTypes());
            model.addAttribute("suppliers", supplierService.getAllSuppliers());
            model.addAttribute("costTables", costTableService.getAllCostTables());

            return "admin/addVehicle";
        }

        vehicleService.addVehicle(vehicle);
        redirectAttributes.addFlashAttribute("successMessage", "Vehicle added successfully!");
        return "redirect:/admin/addVehicle";
    }

    @PostMapping("/deleteVehicle/{chassisNumber}")
    public String deleteVehicle(@PathVariable String chassisNumber, RedirectAttributes redirectAttributes) {
        Optional<Vehicle> vehicleOptional = vehicleService.getByChassisNumber(chassisNumber);
        if (vehicleOptional.isEmpty()) {
            return "error/404";
        }
        Vehicle vehicle = vehicleOptional.get();
        vehicle.setIsDeleted(true);
        vehicleService.updateVehicle(vehicle);
        redirectAttributes.addFlashAttribute("message", "Vehicle deleted successfully!");
        return "redirect:/admin";
    }

    @GetMapping("/editVehicle/{id}")
    public String getEditVehicle(
            @PathVariable("id") String id,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        Optional<Vehicle> vehicleOptional = vehicleService.getByChassisNumber(id);

        if (vehicleOptional.isPresent()) {
            Vehicle vehicle = vehicleOptional.get();
            model.addAttribute("vehicle", vehicle);
            model.addAttribute("vehicleTypes", vehicleTypeService.getAllVehicleTypes());
            model.addAttribute("suppliers", supplierService.getAllSuppliers());
            model.addAttribute("costTables", costTableService.getAllCostTables());
            return "admin/editVehicle";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Vehicle not found!");
            return "redirect:/admin";
        }
    }

    @PostMapping("/editVehicle")
    public String editVehicle(
            @Valid @ModelAttribute("vehicle") Vehicle vehicle,
            @NotNull BindingResult bindingResult,
            @RequestParam("files") MultipartFile[] files,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("vehicleTypes", vehicleTypeService.getAllVehicleTypes());
            model.addAttribute("suppliers", supplierService.getAllSuppliers());
            model.addAttribute("costTables", costTableService.getAllCostTables());
            return "admin/editVehicle";
        }

        List<Image> images = new ArrayList<>();
        Path uploadDir = Paths.get("src/main/resources/static/img");

        try {
            Files.createDirectories(uploadDir);
            if (vehicle.getImages() != null) {
                for (Image image : vehicle.getImages()) {
                    Path imagePath = uploadDir.resolve(Paths.get(image.getImageUrl()).getFileName().toString());
                    Files.deleteIfExists(imagePath);
                }
            }
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
                    Path targetLocation = uploadDir.resolve(fileName);

                    try (InputStream inputStream = file.getInputStream()) {
                        Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
                    }

                    Image newImage = new Image();
                    newImage.setImageUrl("/img/" + fileName);
                    newImage.setChassisNumber(vehicle);
                    images.add(newImage);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to store files", e);
        }

        vehicle.setImages(images);

        vehicleService.updateVehicle(vehicle);
        redirectAttributes.addFlashAttribute("successMessage", "Vehicle edited successfully!");
        return "redirect:/admin";
    }


}
