package org.example.radicalmotor.Services;

import org.example.radicalmotor.Entities.Appointment;
import org.example.radicalmotor.Repositories.AppointmentRepository;
import org.example.radicalmotor.Repositories.IServiceRepository;
import org.example.radicalmotor.Repositories.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.example.radicalmotor.Entities.Service;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
public class ServicesService implements IServiceRepository {

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Override
    public Optional<Service> getServiceById(Long serviceId) {
        return serviceRepository.findById(serviceId);
    }

    public Service findServiceById(Long id) {
        return serviceRepository.findById(id).orElse(null);
    }

    @Override
    public List<Service> getAllServices() {
        return serviceRepository.findAll();
    }

    @Override
    public Service addService(Service service) {
        return serviceRepository.save(service);
    }

    @Override
    public void updateService(Service service) {
        serviceRepository.save(service);
    }

    @Override
    public void deleteService(Long serviceId) {
        Optional<Service> service = serviceRepository.findById(serviceId);
        service.ifPresent(serviceRepository::delete);
    }

    public void saveAppointment(Appointment appointment) {
        appointmentRepository.save(appointment);
    }

}
