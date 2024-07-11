package org.example.radicalmotor.Controllers;

import jakarta.validation.Valid;
import org.example.radicalmotor.Entities.Appointment;
import org.example.radicalmotor.Entities.AppointmentDetail;
import org.example.radicalmotor.Entities.Service;
import org.example.radicalmotor.Services.ServicesService;
import org.example.radicalmotor.Util.RecaptchaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/services")
public class ServiceController {
    @Autowired
    private ServicesService serviceService;

    @GetMapping("")
    public String getAllServices(Model model) {
        List<Service> services = serviceService.getAllServices();
        model.addAttribute("services", services);
        return "Service/index";
    }

    @PostMapping("/book")
    public String bookAppointment(@RequestParam("name") String name, @RequestParam("phoneNumber") String phoneNumber,
                                  @RequestParam("serviceId") Long serviceId, @RequestParam("serviceDate") String serviceDate,
                                  @RequestParam("notes") String notes, @RequestParam("g-recaptcha-response") String recaptchaResponse,
                                  Model model) {

        boolean isRecaptchaValid = RecaptchaUtil.verify(recaptchaResponse);
        if (!isRecaptchaValid) {
            model.addAttribute("error", "Invalid reCAPTCHA. Please try again.");
            model.addAttribute("services", serviceService.getAllServices());
            return "Service/index";
        }

        Service service = serviceService.findServiceById(serviceId);
        AppointmentDetail appointmentDetail = AppointmentDetail.builder()
                .description(notes)
                .serviceDate(LocalDate.parse(serviceDate))
                .service(service)
                .build();

        Appointment appointment = Appointment.builder()
                .dateCreated(LocalDate.now())
                .status("Booked")
                .appointmentDetails(Collections.singletonList(appointmentDetail))
                .build();

        appointmentDetail.setAppointment(appointment);

        serviceService.saveAppointment(appointment);
        return "Service/index";
    }


}
