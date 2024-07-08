package org.example.radicalmotor.Repositories;

import org.example.radicalmotor.Entities.Service;

import java.util.List;
import java.util.Optional;

public interface IServiceRepository {
    Optional<Service> getServiceById(Long serviceId);

    List<Service> getAllServices();

    Service addService(Service service);

    void updateService(Service service);

    void deleteService(Long serviceId);
}
