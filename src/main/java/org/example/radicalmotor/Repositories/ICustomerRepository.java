package org.example.radicalmotor.Repositories;

import org.example.radicalmotor.Entities.Customer;
import org.example.radicalmotor.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ICustomerRepository extends JpaRepository<Customer, String> {

}