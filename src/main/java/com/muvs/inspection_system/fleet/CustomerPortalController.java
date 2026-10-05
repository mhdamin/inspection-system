package com.muvs.inspection_system.fleet;

import com.muvs.inspection_system.entity.Role;
import com.muvs.inspection_system.entity.User;
import com.muvs.inspection_system.repository.RoleRepository;
import com.muvs.inspection_system.repository.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CustomerPortalController {
    private final FleetCustomerRepository customers;
    private final FleetBookingRepository bookings;
    private final FleetRentalRepository rentals;
    private final FleetService fleet;
    private final UserRepository users;
    private final RoleRepository roles;
    private final PasswordEncoder encoder;
    private final com.muvs.inspection_system.service.ChecklistService checklists;

    public record PortalAccount(@NotBlank @Size(min=3, max=20) String username,
                                @NotBlank @Size(min=10, max=40) String password) {}

    @PostMapping("/customers/{id}/portal-account")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    @Transactional
    public Map<String, String> createAccount(@PathVariable UUID id, @Valid @RequestBody PortalAccount request) {
        FleetCustomer customer = customers.findById(id).orElseThrow();
        if (customer.getPortalUsername() != null || users.findByUsername(request.username()).isPresent())
            throw new IllegalStateException("Customer already has access or username is in use");
        Role role = roles.findByName("ROLE_CUSTOMER").orElseGet(() -> roles.save(Role.builder().name("ROLE_CUSTOMER").level(0).description("Own rental records only").build()));
        users.save(User.builder().username(request.username()).password(encoder.encode(request.password())).roles(Set.of(role)).build());
        customer.setPortalUsername(request.username());
        return Map.of("username", request.username());
    }

    private FleetCustomer customer(Principal principal) {
        return customers.findByPortalUsername(principal.getName())
                .orElseThrow(() -> new org.springframework.security.access.AccessDeniedException("No customer account is linked"));
    }

    @GetMapping("/portal/me")
    @Transactional(readOnly = true)
    public Map<String, Object> me(Principal principal) {
        FleetCustomer customer = customer(principal);
        String id = customer.getId().toString();
        var myRentals = rentals.findByCustomerIdOrderByCreatedAtDesc(id);
        Set<String> rentalIds = new HashSet<>(); myRentals.forEach(r -> rentalIds.add(r.getId().toString()));
        return Map.of("customer", fleet.getCustomer(customer.getId()),
                "bookings", bookings.findByCustomerIdOrderByCreatedAtDesc(id).stream().map(b -> fleet.getBooking(b.getId())).toList(),
                "rentals", myRentals.stream().map(r -> fleet.getRental(r.getId())).toList(),
                "invoices", fleet.getInvoices().stream().filter(i -> rentalIds.contains(String.valueOf(i.get("rentalId")))).toList(),
                "ratePlans", fleet.getRatePlans().stream().filter(p -> Boolean.TRUE.equals(p.get("active"))).toList());
    }

    @PostMapping("/portal/bookings")
    public Map<String, Object> book(Principal principal, @Valid @RequestBody FleetDtos.BookingRequest request) {
        FleetCustomer customer = customer(principal);
        request.setCustomerId(customer.getId().toString());

        return fleet.bookCustomerTrip(request);
    }

    @PostMapping("/portal/quote")
    public Map<String, Object> quote(Principal principal, @Valid @RequestBody FleetDtos.BookingRequest request) {
        request.setCustomerId(customer(principal).getId().toString());
        return fleet.previewBooking(request);
    }

    @GetMapping("/portal/inspections")
    public List<com.muvs.inspection_system.dto.ChecklistResponseDTO> inspections(Principal principal) {
        var ids = rentals.findByCustomerIdOrderByCreatedAtDesc(customer(principal).getId().toString()).stream().map(FleetRental::getId).toList();
        return checklists.getAllChecklists().stream().filter(c -> c.isCompleted() && ids.contains(c.getRentalId())).toList();
    }

    @PostMapping("/portal/bookings/{id}/cancel")
    public Map<String, Object> cancel(Principal principal, @PathVariable UUID id) {
        String customerId = customer(principal).getId().toString();
        FleetBooking booking = bookings.findById(id).orElseThrow(() -> new com.muvs.inspection_system.exception.ResourceNotFoundException("Booking not found"));
        if (!customerId.equals(booking.getCustomerId())) throw new org.springframework.security.access.AccessDeniedException("Not your booking");
        return fleet.cancelBooking(id);
    }
}
