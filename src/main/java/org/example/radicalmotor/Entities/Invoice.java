package org.example.radicalmotor.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "invoices")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "invoice_date")
    private Date invoiceDate = new Date();
    @Column(name = "total")
    @Positive(message = "Total must be positive")
    private Double totalAmount;
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<ItemInvoice> itemInvoices = new ArrayList<>();
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customerId", referencedColumnName = "id")
    @ToString.Exclude
    private Customer customer;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transactionHistoryId", referencedColumnName = "id")
    @ToString.Exclude
    private TransactionHistory transactionHistory;
}
