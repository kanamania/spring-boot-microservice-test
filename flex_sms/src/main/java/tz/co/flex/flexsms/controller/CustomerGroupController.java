package tz.co.flex.flexsms.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.co.flex.flexsms.model.Customer;
import tz.co.flex.flexsms.model.CustomerGroup;
import tz.co.flex.flexsms.repository.CustomerGroupRepository;
import tz.co.flex.flexsms.repository.CustomerRepository;

import java.util.List;
import java.util.Set;

//@RestController
//@RequestMapping("/api/groups")
public class CustomerGroupController {

    private final CustomerGroupRepository groupRepository;
    private final CustomerRepository customerRepository;

    public CustomerGroupController(CustomerGroupRepository groupRepository, 
                                 CustomerRepository customerRepository) {
        this.groupRepository = groupRepository;
        this.customerRepository = customerRepository;
    }

    @PostMapping
    public ResponseEntity<CustomerGroup> createGroup(@RequestBody CustomerGroup group) {
        if (groupRepository.existsByName(group.getName())) {
            throw new RuntimeException("Group with this name already exists");
        }
        CustomerGroup savedGroup = groupRepository.save(group);
        return ResponseEntity.ok(savedGroup);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerGroup> getGroup(@PathVariable Long id) {
        CustomerGroup group = groupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        return ResponseEntity.ok(group);
    }

    @GetMapping
    public ResponseEntity<List<CustomerGroup>> getAllGroups() {
        return ResponseEntity.ok(groupRepository.findAll());
    }

    @PostMapping("/{groupId}/customers/{customerId}")
    public ResponseEntity<CustomerGroup> addCustomerToGroup(
            @PathVariable Long groupId,
            @PathVariable Long customerId) {
        
        CustomerGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        group.getCustomers().add(customer);
        CustomerGroup updatedGroup = groupRepository.save(group);
        
        return ResponseEntity.ok(updatedGroup);
    }

    @DeleteMapping("/{groupId}/customers/{customerId}")
    public ResponseEntity<Void> removeCustomerFromGroup(
            @PathVariable Long groupId,
            @PathVariable Long customerId) {
        
        CustomerGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        
        boolean removed = group.getCustomers().removeIf(c -> c.getId().equals(customerId));
        
        if (removed) {
            groupRepository.save(group);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
