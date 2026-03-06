package com.muvs.inspection_system.fleet;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FleetController {

    private final FleetService fleetService;

    @GetMapping("/vehicle-options")
    public List<FleetDtos.VehicleOptionDto> listVehicleOptions() { return fleetService.listVehicleOptions(); }

    @GetMapping("/rate-plans")
    public List<Map<String, Object>> getRatePlans() { return fleetService.getRatePlans(); }
    @PostMapping("/rate-plans")
    public ResponseEntity<Map<String, Object>> createRatePlan(@Valid @RequestBody FleetDtos.RatePlanRequest request) { return new ResponseEntity<>(fleetService.createRatePlan(request), HttpStatus.CREATED); }
    @PutMapping("/rate-plans/{id}")
    public Map<String, Object> updateRatePlan(@PathVariable UUID id, @Valid @RequestBody FleetDtos.RatePlanRequest request) { return fleetService.updateRatePlan(id, request); }

    @GetMapping("/customers")
    public List<Map<String, Object>> getCustomers(@RequestParam(required = false) String query) { return fleetService.getCustomers(query); }
    @GetMapping("/customers/{id}")
    public Map<String, Object> getCustomer(@PathVariable UUID id) { return fleetService.getCustomer(id); }
    @PostMapping("/customers")
    public ResponseEntity<Map<String, Object>> createCustomer(@Valid @RequestBody FleetDtos.CustomerRequest request) { return new ResponseEntity<>(fleetService.createCustomer(request), HttpStatus.CREATED); }
    @PutMapping("/customers/{id}")
    public Map<String, Object> updateCustomer(@PathVariable UUID id, @Valid @RequestBody FleetDtos.CustomerRequest request) { return fleetService.updateCustomer(id, request); }
    @GetMapping("/customers/{id}/history-summary")
    public FleetDtos.CustomerHistorySummaryDto getCustomerHistory(@PathVariable UUID id) { return fleetService.getCustomerHistory(id); }

    @GetMapping("/bookings")
    public List<Map<String, Object>> getBookings(@RequestParam(required = false) String status, @RequestParam(required = false) String query) { return fleetService.getBookings(status, query); }
    @GetMapping("/bookings/{id}")
    public Map<String, Object> getBooking(@PathVariable UUID id) { return fleetService.getBooking(id); }
    @GetMapping("/bookings/{id}/quote-breakdown")
    public FleetDtos.PricingBreakdownDto getBookingQuote(@PathVariable UUID id) { return fleetService.getBookingQuote(id); }
    @PostMapping("/bookings")
    public ResponseEntity<Map<String, Object>> createBooking(@Valid @RequestBody FleetDtos.BookingRequest request) { return new ResponseEntity<>(fleetService.createBooking(request), HttpStatus.CREATED); }
    @PutMapping("/bookings/{id}")
    public Map<String, Object> updateBooking(@PathVariable UUID id, @Valid @RequestBody FleetDtos.BookingRequest request) { return fleetService.updateBooking(id, request); }
    @PostMapping("/bookings/{id}/confirm")
    public Map<String, Object> confirmBooking(@PathVariable UUID id) { return fleetService.confirmBooking(id); }
    @PostMapping("/bookings/{id}/cancel")
    public Map<String, Object> cancelBooking(@PathVariable UUID id) { return fleetService.cancelBooking(id); }
    @PostMapping("/bookings/{id}/assign-vehicle")
    public Map<String, Object> assignVehicle(@PathVariable UUID id, @Valid @RequestBody FleetDtos.AssignVehicleRequest request) { return fleetService.assignVehicleToBooking(id, request.getVehicleId()); }

    @GetMapping("/rentals")
    public List<Map<String, Object>> getRentals() { return fleetService.getRentals(); }
    @GetMapping("/rentals/{id}")
    public Map<String, Object> getRental(@PathVariable UUID id) { return fleetService.getRental(id); }
    @GetMapping("/rentals/{id}/financial-summary")
    public FleetDtos.RentalFinancialSummaryDto getRentalFinancialSummary(@PathVariable UUID id) { return fleetService.getRentalFinancialSummary(id); }
    @PostMapping("/rentals/from-booking")
    public ResponseEntity<Map<String, Object>> createRental(@Valid @RequestBody FleetDtos.RentalCreateRequest request) { return new ResponseEntity<>(fleetService.createRentalFromBooking(request), HttpStatus.CREATED); }
    @PostMapping("/rentals/{id}/start")
    public Map<String, Object> startRental(@PathVariable UUID id) { return fleetService.startRental(id); }
    @PutMapping("/rentals/{id}/extend")
    public Map<String, Object> extendRental(@PathVariable UUID id, @Valid @RequestBody FleetDtos.RentalExtendRequest request) { return fleetService.extendRental(id, request); }
    @PostMapping("/rentals/{id}/close")
    public Map<String, Object> closeRental(@PathVariable UUID id) { return fleetService.closeRental(id); }

    @PostMapping("/returns/quote")
    public FleetDtos.QuoteResponse quoteReturn(@Valid @RequestBody FleetDtos.ReturnQuoteRequest request) { return fleetService.quoteReturnCharges(request); }
    @GetMapping("/returns")
    public List<Map<String, Object>> getReturns() { return fleetService.getReturns(); }
    @PostMapping("/returns")
    public ResponseEntity<Map<String, Object>> submitReturn(@Valid @RequestBody FleetDtos.ReturnSubmitRequest request) { return new ResponseEntity<>(fleetService.submitReturn(request), HttpStatus.CREATED); }
    @PostMapping("/returns/{id}/finalize")
    public Map<String, Object> finalizeReturn(@PathVariable UUID id) { return fleetService.finalizeReturn(id); }

    @GetMapping("/invoices")
    public List<Map<String, Object>> getInvoices() { return fleetService.getInvoices(); }
    @GetMapping("/invoices/{id}")
    public Map<String, Object> getInvoice(@PathVariable UUID id) { return fleetService.getInvoice(id); }
    @GetMapping("/payments")
    public List<Map<String, Object>> getPayments() { return fleetService.getPayments(); }
    @PostMapping("/payments")
    public ResponseEntity<Map<String, Object>> createPayment(@Valid @RequestBody FleetDtos.PaymentCreateRequest request) { return new ResponseEntity<>(fleetService.createPayment(request), HttpStatus.CREATED); }
    @GetMapping("/refunds")
    public List<Map<String, Object>> getRefunds() { return fleetService.getRefunds(); }
    @PostMapping("/refunds")
    public ResponseEntity<Map<String, Object>> createRefund(@Valid @RequestBody FleetDtos.RefundRequest request) { return new ResponseEntity<>(fleetService.processRefund(request), HttpStatus.CREATED); }
    @GetMapping("/settlements")
    public List<Map<String, Object>> getSettlements() { return fleetService.getSettlements(); }
    @GetMapping("/settlements/{id}")
    public Map<String, Object> getSettlement(@PathVariable UUID id) { return fleetService.getSettlement(id); }

    @GetMapping("/operations/work-orders")
    public List<Map<String, Object>> getWorkOrders() { return fleetService.getWorkOrders(); }
    @PatchMapping("/operations/work-orders/{id}")
    public Map<String, Object> updateWorkOrder(@PathVariable UUID id, @RequestBody Map<String, Object> payload) { return fleetService.updateWorkOrder(id, payload); }
    @GetMapping("/operations/damage-cases")
    public List<Map<String, Object>> getDamageCases() { return fleetService.getDamageCases(); }
    @PatchMapping("/operations/damage-cases/{id}")
    public Map<String, Object> updateDamageCase(@PathVariable UUID id, @RequestBody Map<String, Object> payload) { return fleetService.updateDamageCase(id, payload); }
    @GetMapping("/operations/exceptions")
    public List<Map<String, Object>> getExceptions() { return fleetService.getExceptions(); }
    @PatchMapping("/operations/exceptions/{id}")
    public Map<String, Object> updateException(@PathVariable UUID id, @RequestBody Map<String, Object> payload) { return fleetService.updateException(id, payload); }

    @GetMapping("/branches")
    public List<Map<String, Object>> getBranches() { return fleetService.getBranches(); }
    @GetMapping("/transfers")
    public List<Map<String, Object>> getTransfers() { return fleetService.getTransfers(); }
    @PostMapping("/transfers")
    public ResponseEntity<Map<String, Object>> createTransfer(@Valid @RequestBody FleetDtos.TransferRequestInput request) { return new ResponseEntity<>(fleetService.createTransfer(request), HttpStatus.CREATED); }
    @PatchMapping("/transfers/{id}")
    public Map<String, Object> updateTransfer(@PathVariable UUID id, @RequestBody Map<String, Object> payload) { return fleetService.updateTransfer(id, payload); }
    @GetMapping("/approvals")
    public List<Map<String, Object>> getApprovals() { return fleetService.getApprovals(); }
    @PatchMapping("/approvals/{id}")
    public Map<String, Object> updateApproval(@PathVariable UUID id, @RequestBody Map<String, Object> payload) { return fleetService.updateApproval(id, payload); }
    @GetMapping("/notifications")
    public List<Map<String, Object>> getNotifications() { return fleetService.getNotifications(); }
    @PostMapping("/notifications/{id}/read")
    public Map<String, Object> markNotificationRead(@PathVariable UUID id) { return fleetService.markNotificationRead(id); }
}
