package org.example.radicalmotor.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "item_invoice")
public class ItemInvoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    @Column(name = "quantity")
    @Positive(message = "Quantity must be positive")
    private int quantity;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chassisNumber", referencedColumnName = "chassisNumber")
    @ToString.Exclude
    private Vehicle vehicle;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", referencedColumnName = "id")
    @ToString.Exclude
    private Invoice invoice;
}
