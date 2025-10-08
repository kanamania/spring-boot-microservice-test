package tz.co.flex.flexsms.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "group_customers")
public class GroupCustomer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private CustomerGroup group;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
}
