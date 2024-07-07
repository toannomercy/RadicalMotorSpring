package org.example.radicalmotor.Entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "contract")
public class Contract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "date_created")
    private LocalDate dateCreated;
    @Column(name = "total_amount")
    private Double totalAmount;
    @Column(name = "status")
    private String status;
    @Column(name = "installment_plan")
    private String installmentPlan;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    @ToString.Exclude
    private Customer customer;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", referencedColumnName = "id")
    @ToString.Exclude
    private Employee employee;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chassisNumber", referencedColumnName = "chassisNumber")
    @ToString.Exclude
    private Vehicle vehicle;
    @OneToMany(mappedBy = "contract", cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonIgnore
    private List<TransactionHistory> transactionHistories;
}
