package com.muvs.inspection_system.fleet;

import com.muvs.inspection_system.entity.Vehicle;
import com.muvs.inspection_system.exception.ResourceNotFoundException;
import com.muvs.inspection_system.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FleetService {

    private final FleetCustomerRepository customerRepository;
    private final FleetRatePlanRepository ratePlanRepository;
    private final FleetBookingRepository bookingRepository;
    private final FleetRentalRepository rentalRepository;
    private final FleetReturnRepository returnRepository;
    private final FleetInvoiceRepository invoiceRepository;
    private final FleetPaymentRepository paymentRepository;
    private final FleetRefundRepository refundRepository;
    private final FleetSettlementRepository settlementRepository;
    private final FleetWorkOrderRepository workOrderRepository;
    private final FleetDamageCaseRepository damageCaseRepository;
    private final FleetExceptionRepository exceptionRepository;
    private final FleetBranchRepository branchRepository;
    private final FleetTransferRepository transferRepository;
    private final FleetApprovalRepository approvalRepository;
    private final FleetNotificationRepository notificationRepository;
    private final VehicleRepository vehicleRepository;

    public void seedIfEmpty() {
        if (customerRepository.count() > 0) {
            return;
        }

        FleetCustomer alicia = customerRepository.save(FleetCustomer.builder()
                .customerNumber("CUS-1001")
                .fullName("Alicia Tan")
                .email("alicia.tan@example.com")
                .phone("+65 9000 1200")
                .licenseNumber("S1234567A")
                .licenseExpiry("2027-09-30")
                .identityStatus(CustomerIdentityStatusValue.VERIFIED)
                .status(CustomerStatusValue.ACTIVE)
                .notes("Frequent airport pickup customer.")
                .build());

        FleetCustomer marco = customerRepository.save(FleetCustomer.builder()
                .customerNumber("CUS-1002")
                .fullName("Marco Lim")
                .email("marco.lim@example.com")
                .phone("+65 8123 4556")
                .licenseNumber("F7654321K")
                .licenseExpiry("2026-12-31")
                .identityStatus(CustomerIdentityStatusValue.PENDING)
                .status(CustomerStatusValue.WATCHLIST)
                .notes("Requires manual ID verification before handover.")
                .build());

        FleetRatePlan sedan = ratePlanRepository.save(FleetRatePlan.builder()
                .name("Standard Sedan")
                .vehicleClass("Sedan")
                .dailyRate(95)
                .includedMileagePerDay(250)
                .depositAmount(200)
                .taxRate(0.09)
                .active(true)
                .build());
        FleetRatePlan suv = ratePlanRepository.save(FleetRatePlan.builder()
                .name("Flex SUV")
                .vehicleClass("SUV")
                .dailyRate(130)
                .includedMileagePerDay(250)
                .depositAmount(250)
                .taxRate(0.09)
                .active(true)
                .build());
        FleetRatePlan van = ratePlanRepository.save(FleetRatePlan.builder()
                .name("Utility Van")
                .vehicleClass("Van")
                .dailyRate(165)
                .includedMileagePerDay(300)
                .depositAmount(300)
                .taxRate(0.09)
                .active(true)
                .build());

        Vehicle reservedVehicle = ensureVehicle("FLE1001A", "Altis", "Toyota", 2024, "Available");
        Vehicle rentedVehicle = ensureVehicle("FLE2001B", "Hiace", "Toyota", 2024, "Rented");

        Pricing bookingOnePricing = calculatePricingBreakdown(sedan, "Sedan", now().plusHours(36).toString(), now().plusHours(84).toString(), 18, null);
        Pricing bookingTwoPricing = calculatePricingBreakdown(suv, "SUV", now().plusHours(12).toString(), now().plusHours(48).toString(), 0, null);

        FleetBooking bookingOne = bookingRepository.save(FleetBooking.builder()
                .bookingNumber("BK-1001")
                .customerId(alicia.getId().toString())
                .customerName(alicia.getFullName())
                .pickupLocation("Changi T3")
                .dropoffLocation("City Branch")
                .pickupDateTime(now().plusHours(36).toString())
                .dropoffDateTime(now().plusHours(84).toString())
                .vehicleClass("Sedan")
                .assignedVehicleId(reservedVehicle.getId().toString())
                .assignedVehiclePlate(reservedVehicle.getPlateNumber())
                .estimatedTotal(bookingOnePricing.estimatedTotal)
                .depositAmount(sedan.getDepositAmount())
                .ratePlanId(sedan.getId().toString())
                .baseRate(bookingOnePricing.baseRate)
                .rentalDays(bookingOnePricing.rentalDays)
                .addOnTotal(bookingOnePricing.addOnTotal)
                .discountTotal(bookingOnePricing.discountTotal)
                .taxTotal(bookingOnePricing.taxTotal)
                .depositStatus(DepositStatusValue.HELD)
                .status(BookingStatusValue.ASSIGNED)
                .notes("Child seat requested.")
                .build());

        bookingRepository.save(FleetBooking.builder()
                .bookingNumber("BK-1002")
                .customerId(marco.getId().toString())
                .customerName(marco.getFullName())
                .pickupLocation("HQ")
                .dropoffLocation("HQ")
                .pickupDateTime(now().plusHours(12).toString())
                .dropoffDateTime(now().plusHours(48).toString())
                .vehicleClass("SUV")
                .estimatedTotal(bookingTwoPricing.estimatedTotal)
                .depositAmount(suv.getDepositAmount())
                .ratePlanId(suv.getId().toString())
                .baseRate(bookingTwoPricing.baseRate)
                .rentalDays(bookingTwoPricing.rentalDays)
                .addOnTotal(bookingTwoPricing.addOnTotal)
                .discountTotal(bookingTwoPricing.discountTotal)
                .taxTotal(bookingTwoPricing.taxTotal)
                .depositStatus(DepositStatusValue.HELD)
                .status(BookingStatusValue.CONFIRMED)
                .notes("Late-night pickup window requested.")
                .build());

        Pricing rentalPricing = calculatePricingBreakdown(van, "Van", now().minusHours(24).toString(), now().plusHours(8).toString(), 25, null);
        FleetRental rental = rentalRepository.save(FleetRental.builder()
                .rentalNumber("RNT-1001")
                .bookingId(bookingOne.getId().toString())
                .bookingNumber(bookingOne.getBookingNumber())
                .customerId(alicia.getId().toString())
                .customerName(alicia.getFullName())
                .vehicleId(rentedVehicle.getId().toString())
                .vehiclePlate(rentedVehicle.getPlateNumber())
                .vehicleClass("Van")
                .pickupDateTime(now().minusHours(24).toString())
                .expectedReturnDateTime(now().plusHours(8).toString())
                .odometerOut(45210)
                .fuelOut("Full")
                .depositAmount(van.getDepositAmount())
                .depositStatus(DepositStatusValue.HELD)
                .ratePlanId(van.getId().toString())
                .baseRate(rentalPricing.baseRate)
                .rentalDays(rentalPricing.rentalDays)
                .addOnTotal(rentalPricing.addOnTotal)
                .discountTotal(rentalPricing.discountTotal)
                .taxTotal(rentalPricing.taxTotal)
                .estimatedTotal(rentalPricing.estimatedTotal)
                .addOns(List.of("GPS"))
                .status(RentalStatusValue.ACTIVE)
                .notes("Corporate rental extension likely.")
                .build());

        FleetInvoice rentalInvoice = buildInvoice(bookingOne.getId().toString(), rental.getId().toString(), null, alicia.getFullName(), List.of(
                lineItem("Rental charges", rentalPricing.baseRate * rentalPricing.rentalDays, InvoiceItemCategoryValue.RENTAL),
                lineItem("Add-ons", rentalPricing.addOnTotal, InvoiceItemCategoryValue.ADD_ON),
                lineItem("Tax", rentalPricing.taxTotal, InvoiceItemCategoryValue.TAX)
        ), InvoiceStatusValue.PAID);
        rentalInvoice.setAmountPaid(rentalInvoice.getTotal());
        rentalInvoice.setBalanceDue(0);
        invoiceRepository.save(rentalInvoice);

        paymentRepository.save(FleetPayment.builder()
                .paymentNumber("PAY-1001")
                .invoiceId(rentalInvoice.getId().toString())
                .customerName(alicia.getFullName())
                .amount(rentalInvoice.getTotal())
                .method(PaymentMethodValue.CARD)
                .status(PaymentStatusValue.CAPTURED)
                .paymentType(PaymentTypeValue.INVOICE)
                .createdAtValue(now().toString())
                .build());

        branchRepository.saveAll(List.of(
                FleetBranch.builder().code("HQ").name("Headquarters").city("Singapore").manager("Sarah Lee").vehicleCount(42).active(true).build(),
                FleetBranch.builder().code("CTY").name("City Branch").city("Singapore").manager("Adam Ng").vehicleCount(26).active(true).build(),
                FleetBranch.builder().code("AIR").name("Airport Branch").city("Singapore").manager("Maya Chen").vehicleCount(18).active(true).build()
        ));

        transferRepository.save(FleetTransfer.builder()
                .requestNumber("TRF-1001")
                .vehiclePlate("FLE1001A")
                .fromBranch("Headquarters")
                .toBranch("Airport Branch")
                .requestedBy("Sarah Lee")
                .status(TransferStatusValue.REQUESTED)
                .eta(now().plusHours(8).toString())
                .createdAtValue(now().toString())
                .build());

        approvalRepository.saveAll(List.of(
                FleetApproval.builder().approvalNumber("APR-1001").category(ApprovalCategoryValue.REFUND).requester("Finance Desk").summary("Approve refund above $150 for settlement SET-1001.").approver("Regional Manager").status(ApprovalStatusValue.PENDING).createdAtValue(now().toString()).build(),
                FleetApproval.builder().approvalNumber("APR-1002").category(ApprovalCategoryValue.TRANSFER).requester("Operations Desk").summary("Approve urgent branch transfer for airport demand.").approver("Fleet Director").status(ApprovalStatusValue.PENDING).createdAtValue(now().toString()).build()
        ));

        createNotificationIfMissing("Airport demand spike", "Transfer review needed for additional sedan inventory at Airport Branch.", NotificationChannelValue.OPERATIONS);
        createNotificationIfMissing("Refund approval pending", "One refund request exceeds auto-approval threshold.", NotificationChannelValue.FINANCE);
    }

    @Transactional(readOnly = true)
    public List<FleetDtos.VehicleOptionDto> listVehicleOptions() {
        return vehicleRepository.findAll().stream()
                .map(vehicle -> FleetDtos.VehicleOptionDto.builder()
                        .id(vehicle.getId())
                        .plateNumber(vehicle.getPlateNumber())
                        .label(vehicle.getManufacturer() + " " + vehicle.getModel() + " - " + vehicle.getPlateNumber())
                        .vehicleClass(inferVehicleClass(vehicle))
                        .operationalStatus(toOperationalStatus(vehicle.getStatus()))
                        .build())
                .sorted(Comparator.comparing(FleetDtos.VehicleOptionDto::getPlateNumber))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getRatePlans() {
        return ratePlanRepository.findAll().stream().sorted(Comparator.comparing(FleetRatePlan::getCreatedAt).reversed()).map(this::toRatePlan).toList();
    }

    public Map<String, Object> createRatePlan(FleetDtos.RatePlanRequest request) {
        FleetRatePlan plan = ratePlanRepository.save(FleetRatePlan.builder()
                .name(request.getName())
                .vehicleClass(request.getVehicleClass())
                .dailyRate(request.getDailyRate())
                .includedMileagePerDay(request.getIncludedMileagePerDay())
                .depositAmount(request.getDepositAmount())
                .taxRate(request.getTaxRate())
                .active(request.isActive())
                .build());
        return toRatePlan(plan);
    }

    public Map<String, Object> updateRatePlan(UUID id, FleetDtos.RatePlanRequest request) {
        FleetRatePlan plan = ratePlanRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Rate plan not found"));
        plan.setName(request.getName());
        plan.setVehicleClass(request.getVehicleClass());
        plan.setDailyRate(request.getDailyRate());
        plan.setIncludedMileagePerDay(request.getIncludedMileagePerDay());
        plan.setDepositAmount(request.getDepositAmount());
        plan.setTaxRate(request.getTaxRate());
        plan.setActive(request.isActive());
        return toRatePlan(plan);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getCustomers(String query) {
        String normalized = normalize(query);
        return customerRepository.findAll().stream()
                .filter(customer -> normalized.isBlank() || containsAny(normalized, customer.getFullName(), customer.getEmail(), customer.getPhone(), customer.getLicenseNumber()))
                .sorted(Comparator.comparing(FleetCustomer::getCreatedAt).reversed())
                .map(this::toCustomer)
                .toList();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getCustomer(UUID id) {
        return toCustomer(customerRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Customer not found")));
    }

    public Map<String, Object> createCustomer(FleetDtos.CustomerRequest request) {
        FleetCustomer customer = customerRepository.save(FleetCustomer.builder()
                .customerNumber(generateNumber("CUS"))
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .licenseNumber(request.getLicenseNumber())
                .licenseExpiry(request.getLicenseExpiry())
                .identityStatus(parseEnum(CustomerIdentityStatusValue.class, request.getIdentityStatus()))
                .status(parseEnum(CustomerStatusValue.class, request.getStatus()))
                .notes(request.getNotes())
                .build());
        return toCustomer(customer);
    }

    public Map<String, Object> updateCustomer(UUID id, FleetDtos.CustomerRequest request) {
        FleetCustomer customer = customerRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        customer.setFullName(request.getFullName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setLicenseNumber(request.getLicenseNumber());
        customer.setLicenseExpiry(request.getLicenseExpiry());
        customer.setIdentityStatus(parseEnum(CustomerIdentityStatusValue.class, request.getIdentityStatus()));
        customer.setStatus(parseEnum(CustomerStatusValue.class, request.getStatus()));
        customer.setNotes(request.getNotes());
        return toCustomer(customer);
    }

    @Transactional(readOnly = true)
    public FleetDtos.CustomerHistorySummaryDto getCustomerHistory(UUID customerId) {
        List<FleetBooking> bookings = bookingRepository.findByCustomerIdOrderByCreatedAtDesc(customerId.toString());
        List<FleetRental> rentals = rentalRepository.findByCustomerIdOrderByCreatedAtDesc(customerId.toString());
        return FleetDtos.CustomerHistorySummaryDto.builder()
                .bookings(bookings.size())
                .rentals(rentals.size())
                .activeRentals((int) rentals.stream().filter(rental -> rental.getStatus() == RentalStatusValue.ACTIVE || rental.getStatus() == RentalStatusValue.OVERDUE).count())
                .lastBookingDate(bookings.isEmpty() ? null : bookings.get(0).getCreatedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getBookings(String status, String query) {
        String normalized = normalize(query);
        return bookingRepository.findAll().stream()
                .filter(booking -> status == null || status.isBlank() || "All".equalsIgnoreCase(status) || toLabel(booking.getStatus()).equalsIgnoreCase(status))
                .filter(booking -> normalized.isBlank() || containsAny(normalized, booking.getBookingNumber(), booking.getCustomerName(), booking.getPickupLocation(), booking.getDropoffLocation()))
                .sorted(Comparator.comparing(FleetBooking::getCreatedAt).reversed())
                .map(this::toBooking)
                .toList();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getBooking(UUID id) {
        return toBooking(bookingRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Booking not found")));
    }

    @Transactional(readOnly = true)
    public FleetDtos.PricingBreakdownDto getBookingQuote(UUID id) {
        FleetBooking booking = bookingRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        return pricingDto(booking.getRatePlanId(), booking.getBaseRate(), booking.getRentalDays(), booking.getAddOnTotal(), booking.getDiscountTotal(), booking.getTaxTotal(), booking.getEstimatedTotal());
    }

    public Map<String, Object> createBooking(FleetDtos.BookingRequest request) {
        FleetCustomer customer = customerRepository.findById(UUID.fromString(request.getCustomerId())).orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        FleetRatePlan ratePlan = findRatePlan(request.getVehicleClass());
        Pricing pricing = calculatePricingBreakdown(ratePlan, request.getVehicleClass(), request.getPickupDateTime(), request.getDropoffDateTime(), 0, request.getEstimatedTotal() > 0 ? request.getEstimatedTotal() : null);
        FleetBooking booking = bookingRepository.save(FleetBooking.builder()
                .bookingNumber(generateNumber("BK"))
                .customerId(customer.getId().toString())
                .customerName(customer.getFullName())
                .pickupLocation(request.getPickupLocation())
                .dropoffLocation(request.getDropoffLocation())
                .pickupDateTime(request.getPickupDateTime())
                .dropoffDateTime(request.getDropoffDateTime())
                .vehicleClass(request.getVehicleClass())
                .estimatedTotal(pricing.estimatedTotal)
                .depositAmount(ratePlan != null ? ratePlan.getDepositAmount() : request.getDepositAmount())
                .ratePlanId(ratePlan != null ? ratePlan.getId().toString() : null)
                .baseRate(pricing.baseRate)
                .rentalDays(pricing.rentalDays)
                .addOnTotal(pricing.addOnTotal)
                .discountTotal(pricing.discountTotal)
                .taxTotal(pricing.taxTotal)
                .depositStatus(DepositStatusValue.HELD)
                .status(BookingStatusValue.DRAFT)
                .notes(request.getNotes())
                .build());
        return toBooking(booking);
    }

    public Map<String, Object> updateBooking(UUID id, FleetDtos.BookingRequest request) {
        FleetBooking booking = bookingRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        FleetRatePlan ratePlan = findRatePlan(request.getVehicleClass());
        Pricing pricing = calculatePricingBreakdown(ratePlan, request.getVehicleClass(), request.getPickupDateTime(), request.getDropoffDateTime(), booking.getAddOnTotal(), request.getEstimatedTotal() > 0 ? request.getEstimatedTotal() : null);
        booking.setPickupLocation(request.getPickupLocation());
        booking.setDropoffLocation(request.getDropoffLocation());
        booking.setPickupDateTime(request.getPickupDateTime());
        booking.setDropoffDateTime(request.getDropoffDateTime());
        booking.setVehicleClass(request.getVehicleClass());
        booking.setEstimatedTotal(pricing.estimatedTotal);
        booking.setDepositAmount(ratePlan != null ? ratePlan.getDepositAmount() : request.getDepositAmount());
        booking.setRatePlanId(ratePlan != null ? ratePlan.getId().toString() : null);
        booking.setBaseRate(pricing.baseRate);
        booking.setRentalDays(pricing.rentalDays);
        booking.setAddOnTotal(pricing.addOnTotal);
        booking.setDiscountTotal(pricing.discountTotal);
        booking.setTaxTotal(pricing.taxTotal);
        booking.setNotes(request.getNotes());
        return toBooking(booking);
    }

    public Map<String, Object> confirmBooking(UUID id) {
        FleetBooking booking = bookingRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        booking.setStatus(booking.getAssignedVehicleId() == null ? BookingStatusValue.CONFIRMED : BookingStatusValue.ASSIGNED);
        return toBooking(booking);
    }

    public Map<String, Object> cancelBooking(UUID id) {
        FleetBooking booking = bookingRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        booking.setStatus(BookingStatusValue.CANCELLED);
        if (booking.getAssignedVehicleId() != null) {
            updateVehicleStatus(UUID.fromString(booking.getAssignedVehicleId()), VehicleOperationalStatusValue.AVAILABLE);
        }
        return toBooking(booking);
    }

    public Map<String, Object> assignVehicleToBooking(UUID id, String vehicleId) {
        FleetBooking booking = bookingRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        Vehicle vehicle = vehicleRepository.findById(UUID.fromString(vehicleId)).orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));
        booking.setAssignedVehicleId(vehicle.getId().toString());
        booking.setAssignedVehiclePlate(vehicle.getPlateNumber());
        booking.setStatus(BookingStatusValue.ASSIGNED);
        updateVehicleStatus(vehicle.getId(), VehicleOperationalStatusValue.RESERVED);
        return toBooking(booking);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getRentals() {
        return rentalRepository.findAll().stream().sorted(Comparator.comparing(FleetRental::getCreatedAt).reversed()).map(this::toRental).toList();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getRental(UUID id) {
        return toRental(rentalRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Rental not found")));
    }

    @Transactional(readOnly = true)
    public FleetDtos.RentalFinancialSummaryDto getRentalFinancialSummary(UUID id) {
        FleetRental rental = rentalRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Rental not found"));
        return FleetDtos.RentalFinancialSummaryDto.builder()
                .pricingBreakdown(pricingDto(rental.getRatePlanId(), rental.getBaseRate(), rental.getRentalDays(), rental.getAddOnTotal(), rental.getDiscountTotal(), rental.getTaxTotal(), rental.getEstimatedTotal()))
                .depositStatus(toLabel(rental.getDepositStatus()))
                .depositAmount(rental.getDepositAmount())
                .build();
    }

    public Map<String, Object> createRentalFromBooking(FleetDtos.RentalCreateRequest request) {
        FleetBooking booking = bookingRepository.findById(UUID.fromString(request.getBookingId())).orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        Vehicle vehicle = vehicleRepository.findById(UUID.fromString(request.getVehicleId())).orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));
        FleetRatePlan ratePlan = findRatePlan(booking.getVehicleClass());
        double addOnTotal = Optional.ofNullable(request.getAddOns()).orElse(List.of()).size() * 15.0;
        Pricing pricing = calculatePricingBreakdown(ratePlan, booking.getVehicleClass(), booking.getPickupDateTime(), booking.getDropoffDateTime(), addOnTotal, null);
        FleetRental rental = rentalRepository.save(FleetRental.builder()
                .rentalNumber(generateNumber("RNT"))
                .bookingId(booking.getId().toString())
                .bookingNumber(booking.getBookingNumber())
                .customerId(booking.getCustomerId())
                .customerName(booking.getCustomerName())
                .vehicleId(vehicle.getId().toString())
                .vehiclePlate(vehicle.getPlateNumber())
                .vehicleClass(booking.getVehicleClass())
                .pickupDateTime(booking.getPickupDateTime())
                .expectedReturnDateTime(booking.getDropoffDateTime())
                .odometerOut(request.getOdometerOut())
                .fuelOut(request.getFuelOut())
                .depositAmount(request.getDepositAmount() > 0 ? request.getDepositAmount() : booking.getDepositAmount())
                .depositStatus(booking.getDepositStatus())
                .ratePlanId(ratePlan != null ? ratePlan.getId().toString() : null)
                .baseRate(pricing.baseRate)
                .rentalDays(pricing.rentalDays)
                .addOnTotal(pricing.addOnTotal)
                .discountTotal(pricing.discountTotal)
                .taxTotal(pricing.taxTotal)
                .estimatedTotal(pricing.estimatedTotal)
                .addOns(Optional.ofNullable(request.getAddOns()).orElseGet(ArrayList::new))
                .status(RentalStatusValue.RESERVED)
                .notes(request.getNotes())
                .build());

        booking.setAssignedVehicleId(vehicle.getId().toString());
        booking.setAssignedVehiclePlate(vehicle.getPlateNumber());
        booking.setStatus(BookingStatusValue.ASSIGNED);
        updateVehicleStatus(vehicle.getId(), VehicleOperationalStatusValue.RESERVED);

        invoiceRepository.save(buildInvoice(booking.getId().toString(), rental.getId().toString(), null, rental.getCustomerName(), List.of(
                lineItem(rental.getVehicleClass() + " rental", pricing.baseRate * pricing.rentalDays, InvoiceItemCategoryValue.RENTAL),
                lineItem("Add-ons", pricing.addOnTotal, InvoiceItemCategoryValue.ADD_ON),
                lineItem("Tax", pricing.taxTotal, InvoiceItemCategoryValue.TAX)
        ), InvoiceStatusValue.DRAFT));

        return toRental(rental);
    }

    public Map<String, Object> startRental(UUID id) {
        FleetRental rental = rentalRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Rental not found"));
        rental.setStatus(RentalStatusValue.ACTIVE);
        FleetBooking booking = bookingRepository.findById(UUID.fromString(rental.getBookingId())).orElse(null);
        if (booking != null) {
            booking.setStatus(BookingStatusValue.CHECKED_OUT);
        }
        updateVehicleStatus(UUID.fromString(rental.getVehicleId()), VehicleOperationalStatusValue.RENTED);
        invoiceRepository.findFirstByRentalIdAndStatus(rental.getId().toString(), InvoiceStatusValue.DRAFT).ifPresent(invoice -> invoice.setStatus(InvoiceStatusValue.ISSUED));
        return toRental(rental);
    }

    public Map<String, Object> extendRental(UUID id, FleetDtos.RentalExtendRequest request) {
        FleetRental rental = rentalRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Rental not found"));
        FleetRatePlan ratePlan = rental.getRatePlanId() == null ? null : ratePlanRepository.findById(UUID.fromString(rental.getRatePlanId())).orElse(null);
        Pricing pricing = calculatePricingBreakdown(ratePlan, rental.getVehicleClass(), rental.getPickupDateTime(), request.getExpectedReturnDateTime(), rental.getAddOnTotal(), null);
        rental.setExpectedReturnDateTime(request.getExpectedReturnDateTime());
        rental.setBaseRate(pricing.baseRate);
        rental.setRentalDays(pricing.rentalDays);
        rental.setTaxTotal(pricing.taxTotal);
        rental.setEstimatedTotal(pricing.estimatedTotal);
        rental.setNotes(request.getNotes());
        return toRental(rental);
    }

    public Map<String, Object> closeRental(UUID id) {
        FleetRental rental = rentalRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Rental not found"));
        rental.setStatus(RentalStatusValue.CLOSED);
        rental.setActualReturnDateTime(now().toString());
        FleetBooking booking = bookingRepository.findById(UUID.fromString(rental.getBookingId())).orElse(null);
        if (booking != null) {
            booking.setStatus(BookingStatusValue.COMPLETED);
        }
        updateVehicleStatus(UUID.fromString(rental.getVehicleId()), VehicleOperationalStatusValue.AVAILABLE);
        return toRental(rental);
    }

    public FleetDtos.QuoteResponse quoteReturnCharges(FleetDtos.ReturnQuoteRequest request) {
        ReturnCharge charge = calculateReturnOutcome(request.getFuelIn(), request.isDamageFlag(), request.isMaintenanceFlag(), request.getLateHours(), request.getExtraCharges());
        return new FleetDtos.QuoteResponse(charge.baseCharges, charge.totalCharges, toLabel(charge.outcome));
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getReturns() {
        return returnRepository.findAll().stream().sorted(Comparator.comparing(FleetReturn::getCreatedAt).reversed()).map(this::toReturn).toList();
    }

    public Map<String, Object> submitReturn(FleetDtos.ReturnSubmitRequest request) {
        FleetRental rental = rentalRepository.findById(UUID.fromString(request.getRentalId())).orElseThrow(() -> new ResourceNotFoundException("Rental not found"));
        ReturnCharge charge = calculateReturnOutcome(request.getFuelIn(), request.isDamageFlag(), request.isMaintenanceFlag(), request.getLateHours(), request.getExtraCharges());
        FleetReturn fleetReturn = returnRepository.save(FleetReturn.builder()
                .returnNumber(generateNumber("RET"))
                .rentalId(rental.getId().toString())
                .rentalNumber(rental.getRentalNumber())
                .bookingNumber(rental.getBookingNumber())
                .customerName(rental.getCustomerName())
                .vehiclePlate(rental.getVehiclePlate())
                .odometerIn(request.getOdometerIn())
                .fuelIn(request.getFuelIn())
                .checklistId(request.getChecklistId())
                .damageFlag(request.isDamageFlag())
                .maintenanceFlag(request.isMaintenanceFlag())
                .lateHours(request.getLateHours())
                .baseCharges(charge.baseCharges)
                .extraCharges(request.getExtraCharges())
                .totalCharges(charge.totalCharges)
                .outcome(charge.outcome)
                .submittedAt(now().toString())
                .notes(request.getNotes())
                .build());

        double depositHeld = rental.getDepositAmount();
        double depositApplied = round(Math.min(depositHeld, fleetReturn.getTotalCharges()));
        double amountDue = round(Math.max(0, fleetReturn.getTotalCharges() - depositHeld));
        double amountRefundable = round(Math.max(0, depositHeld - fleetReturn.getTotalCharges()));

        FleetInvoice settlementInvoice = buildInvoice(null, rental.getId().toString(), null, rental.getCustomerName(), List.of(
                lineItem("Return charges", fleetReturn.getTotalCharges(), InvoiceItemCategoryValue.PENALTY)
        ), amountDue > 0 ? InvoiceStatusValue.ISSUED : InvoiceStatusValue.PAID);
        if (amountDue == 0) {
            settlementInvoice.setAmountPaid(settlementInvoice.getTotal());
            settlementInvoice.setBalanceDue(0);
        }
        invoiceRepository.save(settlementInvoice);

        FleetRefund autoRefund = null;
        if (amountRefundable > 0) {
            autoRefund = refundRepository.save(FleetRefund.builder()
                    .refundNumber(generateNumber("RFD"))
                    .invoiceId(settlementInvoice.getId().toString())
                    .customerName(rental.getCustomerName())
                    .amount(amountRefundable)
                    .reason("Unused deposit refund")
                    .status(RefundStatusValue.PROCESSED)
                    .createdAtValue(now().toString())
                    .build());
        }

        FleetSettlement settlement = settlementRepository.save(FleetSettlement.builder()
                .settlementNumber(generateNumber("SET"))
                .rentalId(rental.getId().toString())
                .returnId(fleetReturn.getId().toString())
                .customerName(rental.getCustomerName())
                .vehiclePlate(rental.getVehiclePlate())
                .depositHeld(depositHeld)
                .depositApplied(depositApplied)
                .depositRefunded(amountRefundable)
                .returnCharges(fleetReturn.getTotalCharges())
                .invoiceId(settlementInvoice.getId().toString())
                .refundId(autoRefund != null ? autoRefund.getId().toString() : null)
                .amountDue(amountDue)
                .amountRefundable(amountRefundable)
                .status(amountRefundable > 0 ? SettlementStatusValue.REFUNDED : amountDue > 0 ? SettlementStatusValue.OPEN : SettlementStatusValue.SETTLED)
                .createdAtValue(now().toString())
                .build());

        fleetReturn.setSettlementId(settlement.getId().toString());
        settlementInvoice.setSettlementId(settlement.getId().toString());
        if (autoRefund != null) {
            autoRefund.setSettlementId(settlement.getId().toString());
        }

        rental.setOdometerIn(request.getOdometerIn());
        rental.setFuelIn(request.getFuelIn());
        rental.setActualReturnDateTime(fleetReturn.getSubmittedAt());
        rental.setStatus(RentalStatusValue.CLOSED);
        rental.setDepositStatus(amountRefundable > 0 ? DepositStatusValue.REFUNDED : amountDue > 0 || depositApplied == depositHeld ? DepositStatusValue.APPLIED : DepositStatusValue.PARTIALLY_REFUNDED);

        bookingRepository.findById(UUID.fromString(rental.getBookingId())).ifPresent(booking -> {
            booking.setStatus(BookingStatusValue.COMPLETED);
            booking.setDepositStatus(rental.getDepositStatus());
        });

        if (charge.outcome == ReturnOutcomeValue.DAMAGE_REVIEW_REQUIRED) {
            updateVehicleStatus(UUID.fromString(rental.getVehicleId()), VehicleOperationalStatusValue.INSPECTION_HOLD);
            createDamageCaseIfMissing(fleetReturn);
        } else if (charge.outcome == ReturnOutcomeValue.MAINTENANCE_HOLD) {
            updateVehicleStatus(UUID.fromString(rental.getVehicleId()), VehicleOperationalStatusValue.MAINTENANCE);
            createWorkOrderIfMissing(fleetReturn);
        } else {
            updateVehicleStatus(UUID.fromString(rental.getVehicleId()), VehicleOperationalStatusValue.AVAILABLE);
        }
        if (amountDue > 0) {
            createSettlementExceptionIfMissing(settlement);
        }
        syncNotifications();
        return toReturn(fleetReturn);
    }

    public Map<String, Object> finalizeReturn(UUID id) {
        return toReturn(returnRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Return not found")));
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getInvoices() {
        return invoiceRepository.findAll().stream().sorted(Comparator.comparing(FleetInvoice::getCreatedAt).reversed()).map(this::toInvoice).toList();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getInvoice(UUID id) {
        return toInvoice(invoiceRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Invoice not found")));
    }

    public Map<String, Object> createPayment(FleetDtos.PaymentCreateRequest request) {
        FleetInvoice invoice = invoiceRepository.findById(UUID.fromString(request.getInvoiceId())).orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));
        FleetPayment payment = paymentRepository.save(FleetPayment.builder()
                .paymentNumber(generateNumber("PAY"))
                .invoiceId(invoice.getId().toString())
                .customerName(request.getCustomerName())
                .amount(round(request.getAmount()))
                .method(parseEnum(PaymentMethodValue.class, request.getMethod()))
                .status(PaymentStatusValue.CAPTURED)
                .paymentType(parseEnum(PaymentTypeValue.class, request.getPaymentType()))
                .createdAtValue(now().toString())
                .build());
        applyPayment(invoice, payment.getAmount());
        settlementRepository.findAll().stream().filter(item -> item.getInvoiceId().equals(invoice.getId().toString()) && invoice.getBalanceDue() <= 0).findFirst().ifPresent(item -> item.setStatus(SettlementStatusValue.SETTLED));
        syncNotifications();
        return toPayment(payment);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getPayments() {
        return paymentRepository.findAll().stream().sorted(Comparator.comparing(FleetPayment::getCreatedAt).reversed()).map(this::toPayment).toList();
    }

    public Map<String, Object> processRefund(FleetDtos.RefundRequest request) {
        FleetRefund refund = refundRepository.save(FleetRefund.builder()
                .refundNumber(generateNumber("RFD"))
                .invoiceId(request.getInvoiceId())
                .settlementId(request.getSettlementId())
                .customerName(request.getCustomerName())
                .amount(round(request.getAmount()))
                .reason(request.getReason())
                .status(RefundStatusValue.PROCESSED)
                .createdAtValue(now().toString())
                .build());
        if (request.getInvoiceId() != null && !request.getInvoiceId().isBlank()) {
            FleetInvoice invoice = invoiceRepository.findById(UUID.fromString(request.getInvoiceId())).orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));
            applyRefund(invoice, refund.getAmount());
        }
        if (request.getSettlementId() != null && !request.getSettlementId().isBlank()) {
            FleetSettlement settlement = settlementRepository.findById(UUID.fromString(request.getSettlementId())).orElseThrow(() -> new ResourceNotFoundException("Settlement not found"));
            settlement.setRefundId(refund.getId().toString());
            settlement.setAmountRefundable(round(Math.max(0, settlement.getAmountRefundable() - refund.getAmount())));
            settlement.setDepositRefunded(round(settlement.getDepositRefunded() + refund.getAmount()));
            settlement.setStatus(SettlementStatusValue.REFUNDED);
        }
        syncNotifications();
        return toRefund(refund);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getRefunds() {
        return refundRepository.findAll().stream().sorted(Comparator.comparing(FleetRefund::getCreatedAt).reversed()).map(this::toRefund).toList();
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getSettlements() {
        return settlementRepository.findAll().stream().sorted(Comparator.comparing(FleetSettlement::getCreatedAt).reversed()).map(this::toSettlement).toList();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getSettlement(UUID id) {
        return toSettlement(settlementRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Settlement not found")));
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getWorkOrders() {
        return workOrderRepository.findAll().stream().sorted(Comparator.comparing(FleetWorkOrder::getCreatedAt).reversed()).map(this::toWorkOrder).toList();
    }

    public Map<String, Object> updateWorkOrder(UUID id, Map<String, Object> payload) {
        FleetWorkOrder workOrder = workOrderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Work order not found"));
        if (payload.containsKey("issueSummary")) workOrder.setIssueSummary(String.valueOf(payload.get("issueSummary")));
        if (payload.containsKey("assignee")) workOrder.setAssignee(String.valueOf(payload.get("assignee")));
        if (payload.containsKey("vendor")) workOrder.setVendor(payload.get("vendor") == null ? null : String.valueOf(payload.get("vendor")));
        if (payload.containsKey("estimatedCost")) workOrder.setEstimatedCost(Double.parseDouble(String.valueOf(payload.get("estimatedCost"))));
        if (payload.containsKey("status")) workOrder.setStatus(parseEnum(WorkOrderStatusValue.class, String.valueOf(payload.get("status"))));
        if (payload.containsKey("priority")) workOrder.setPriority(parseEnum(PriorityLevelValue.class, String.valueOf(payload.get("priority"))));
        exceptionRepository.findByTypeAndLinkedId(ExceptionTypeValue.MAINTENANCE, workOrder.getId().toString()).ifPresent(item -> {
            item.setOwner(workOrder.getAssignee());
            item.setSummary(workOrder.getIssueSummary());
            item.setStatus(workOrder.getStatus() == WorkOrderStatusValue.COMPLETED ? ExceptionStatusValue.RESOLVED : item.getStatus());
        });
        return toWorkOrder(workOrder);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getDamageCases() {
        return damageCaseRepository.findAll().stream().sorted(Comparator.comparing(FleetDamageCase::getCreatedAt).reversed()).map(this::toDamageCase).toList();
    }

    public Map<String, Object> updateDamageCase(UUID id, Map<String, Object> payload) {
        FleetDamageCase damageCase = damageCaseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Damage case not found"));
        if (payload.containsKey("description")) damageCase.setDescription(String.valueOf(payload.get("description")));
        if (payload.containsKey("estimatedRepairCost")) damageCase.setEstimatedRepairCost(Double.parseDouble(String.valueOf(payload.get("estimatedRepairCost"))));
        if (payload.containsKey("status")) damageCase.setStatus(parseEnum(DamageCaseStatusValue.class, String.valueOf(payload.get("status"))));
        if (payload.containsKey("severity")) damageCase.setSeverity(parseEnum(DamageSeverityValue.class, String.valueOf(payload.get("severity"))));
        if (payload.containsKey("insuranceStatus")) damageCase.setInsuranceStatus(parseEnum(InsuranceStatusValue.class, String.valueOf(payload.get("insuranceStatus"))));
        exceptionRepository.findByTypeAndLinkedId(ExceptionTypeValue.DAMAGE, damageCase.getId().toString()).ifPresent(item -> {
            item.setSummary(damageCase.getDescription());
            item.setStatus(damageCase.getStatus() == DamageCaseStatusValue.RESOLVED ? ExceptionStatusValue.RESOLVED : item.getStatus());
        });
        return toDamageCase(damageCase);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getExceptions() {
        return exceptionRepository.findAll().stream().sorted(Comparator.comparing(FleetException::getCreatedAt).reversed()).map(this::toException).toList();
    }

    public Map<String, Object> updateException(UUID id, Map<String, Object> payload) {
        FleetException exception = exceptionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Exception not found"));
        if (payload.containsKey("summary")) exception.setSummary(String.valueOf(payload.get("summary")));
        if (payload.containsKey("owner")) exception.setOwner(String.valueOf(payload.get("owner")));
        if (payload.containsKey("status")) exception.setStatus(parseEnum(ExceptionStatusValue.class, String.valueOf(payload.get("status"))));
        return toException(exception);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getBranches() {
        return branchRepository.findAll().stream().sorted(Comparator.comparing(FleetBranch::getName)).map(this::toBranch).toList();
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getTransfers() {
        return transferRepository.findAll().stream().sorted(Comparator.comparing(FleetTransfer::getCreatedAt).reversed()).map(this::toTransfer).toList();
    }

    public Map<String, Object> createTransfer(FleetDtos.TransferRequestInput request) {
        FleetTransfer transfer = transferRepository.save(FleetTransfer.builder()
                .requestNumber(generateNumber("TRF"))
                .vehiclePlate(request.getVehiclePlate())
                .fromBranch(request.getFromBranch())
                .toBranch(request.getToBranch())
                .requestedBy(request.getRequestedBy())
                .status(parseEnum(TransferStatusValue.class, request.getStatus()))
                .eta(request.getEta())
                .createdAtValue(now().toString())
                .build());
        createNotificationIfMissing("Transfer request created", transfer.getVehiclePlate() + " requested from " + transfer.getFromBranch() + " to " + transfer.getToBranch() + ".", NotificationChannelValue.OPERATIONS);
        return toTransfer(transfer);
    }

    public Map<String, Object> updateTransfer(UUID id, Map<String, Object> payload) {
        FleetTransfer transfer = transferRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Transfer not found"));
        if (payload.containsKey("vehiclePlate")) transfer.setVehiclePlate(String.valueOf(payload.get("vehiclePlate")));
        if (payload.containsKey("fromBranch")) transfer.setFromBranch(String.valueOf(payload.get("fromBranch")));
        if (payload.containsKey("toBranch")) transfer.setToBranch(String.valueOf(payload.get("toBranch")));
        if (payload.containsKey("requestedBy")) transfer.setRequestedBy(String.valueOf(payload.get("requestedBy")));
        if (payload.containsKey("eta")) transfer.setEta(String.valueOf(payload.get("eta")));
        if (payload.containsKey("status")) transfer.setStatus(parseEnum(TransferStatusValue.class, String.valueOf(payload.get("status"))));
        return toTransfer(transfer);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getApprovals() {
        return approvalRepository.findAll().stream().sorted(Comparator.comparing(FleetApproval::getCreatedAt).reversed()).map(this::toApproval).toList();
    }

    public Map<String, Object> updateApproval(UUID id, Map<String, Object> payload) {
        FleetApproval approval = approvalRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Approval not found"));
        if (payload.containsKey("summary")) approval.setSummary(String.valueOf(payload.get("summary")));
        if (payload.containsKey("approver")) approval.setApprover(String.valueOf(payload.get("approver")));
        if (payload.containsKey("status")) approval.setStatus(parseEnum(ApprovalStatusValue.class, String.valueOf(payload.get("status"))));
        return toApproval(approval);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getNotifications() {
        syncNotifications();
        return notificationRepository.findAll().stream().sorted(Comparator.comparing(FleetNotification::getCreatedAt).reversed()).map(this::toNotification).toList();
    }

    public Map<String, Object> markNotificationRead(UUID id) {
        FleetNotification notification = notificationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        notification.setStatus(NotificationStatusValue.READ);
        return toNotification(notification);
    }

    private void syncNotifications() {
        createNotificationIfMissing("Customer portal ready", customerRepository.count() + " customer accounts now have access to bookings and invoices.", NotificationChannelValue.CUSTOMER);
        createNotificationIfMissing("Pricing catalog refreshed", ratePlanRepository.findAll().stream().filter(FleetRatePlan::isActive).count() + " active rate plans are available for quoting.", NotificationChannelValue.FINANCE);
        createNotificationIfMissing("Production demand snapshot", bookingRepository.count() + " bookings and " + rentalRepository.count() + " rentals currently active in the system.", NotificationChannelValue.OPERATIONS);
        long openInvoices = invoiceRepository.findAll().stream().filter(invoice -> invoice.getBalanceDue() > 0).count();
        createNotificationIfMissing("Collections snapshot", openInvoices + " invoices still have open balances; " + paymentRepository.count() + " payments captured.", NotificationChannelValue.FINANCE);
    }

    private void createWorkOrderIfMissing(FleetReturn fleetReturn) {
        if (workOrderRepository.findByReturnId(fleetReturn.getId().toString()).isPresent()) {
            return;
        }
        FleetWorkOrder workOrder = workOrderRepository.save(FleetWorkOrder.builder()
                .workOrderNumber(generateNumber("WO"))
                .rentalId(fleetReturn.getRentalId())
                .returnId(fleetReturn.getId().toString())
                .vehiclePlate(fleetReturn.getVehiclePlate())
                .issueSummary(fleetReturn.getNotes() == null || fleetReturn.getNotes().isBlank() ? "Vehicle flagged for maintenance after return assessment." : fleetReturn.getNotes())
                .priority(fleetReturn.getTotalCharges() > 150 ? PriorityLevelValue.HIGH : PriorityLevelValue.MEDIUM)
                .assignee("Workshop Desk")
                .estimatedCost(fleetReturn.getTotalCharges())
                .status(WorkOrderStatusValue.OPEN)
                .createdAtValue(now().toString())
                .build());
        createExceptionIfMissing(ExceptionTypeValue.MAINTENANCE, workOrder.getId().toString(), workOrder.getVehiclePlate(), null, workOrder.getIssueSummary(), workOrder.getAssignee(), workOrder.getCreatedAtValue());
    }

    private void createDamageCaseIfMissing(FleetReturn fleetReturn) {
        if (damageCaseRepository.findByReturnId(fleetReturn.getId().toString()).isPresent()) {
            return;
        }
        FleetDamageCase damageCase = damageCaseRepository.save(FleetDamageCase.builder()
                .caseNumber(generateNumber("DMG"))
                .rentalId(fleetReturn.getRentalId())
                .returnId(fleetReturn.getId().toString())
                .vehiclePlate(fleetReturn.getVehiclePlate())
                .customerName(fleetReturn.getCustomerName())
                .description(fleetReturn.getNotes() == null || fleetReturn.getNotes().isBlank() ? "Damage review triggered from return inspection." : fleetReturn.getNotes())
                .severity(fleetReturn.getTotalCharges() > 250 ? DamageSeverityValue.MAJOR : fleetReturn.getTotalCharges() > 100 ? DamageSeverityValue.MODERATE : DamageSeverityValue.MINOR)
                .estimatedRepairCost(fleetReturn.getTotalCharges())
                .insuranceStatus(InsuranceStatusValue.UNSUBMITTED)
                .status(DamageCaseStatusValue.OPEN)
                .createdAtValue(now().toString())
                .build());
        createExceptionIfMissing(ExceptionTypeValue.DAMAGE, damageCase.getId().toString(), damageCase.getVehiclePlate(), damageCase.getCustomerName(), damageCase.getDescription(), "Claims Desk", damageCase.getCreatedAtValue());
    }

    private void createSettlementExceptionIfMissing(FleetSettlement settlement) {
        createExceptionIfMissing(ExceptionTypeValue.SETTLEMENT, settlement.getId().toString(), settlement.getVehiclePlate(), settlement.getCustomerName(), "Outstanding settlement balance of $" + String.format(Locale.US, "%.2f", settlement.getAmountDue()) + " requires follow-up.", "Finance Desk", settlement.getCreatedAtValue());
    }

    private void createExceptionIfMissing(ExceptionTypeValue type, String linkedId, String vehiclePlate, String customerName, String summary, String owner, String createdAt) {
        if (exceptionRepository.findByTypeAndLinkedId(type, linkedId).isPresent()) {
            return;
        }
        exceptionRepository.save(FleetException.builder()
                .referenceNumber(generateNumber("EXP"))
                .type(type)
                .linkedId(linkedId)
                .vehiclePlate(vehiclePlate)
                .customerName(customerName)
                .summary(summary)
                .owner(owner)
                .status(ExceptionStatusValue.OPEN)
                .createdAtValue(createdAt)
                .build());
    }

    private void createNotificationIfMissing(String title, String body, NotificationChannelValue channel) {
        notificationRepository.findByTitle(title).orElseGet(() -> notificationRepository.save(FleetNotification.builder()
                .title(title)
                .body(body)
                .channel(channel)
                .status(NotificationStatusValue.UNREAD)
                .createdAtValue(now().toString())
                .build()));
    }

    private FleetInvoice buildInvoice(String bookingId, String rentalId, String settlementId, String customerName, List<FleetInvoiceLineItem> lineItems, InvoiceStatusValue status) {
        double subtotal = round(lineItems.stream().filter(item -> item.getCategory() != InvoiceItemCategoryValue.TAX).mapToDouble(FleetInvoiceLineItem::getAmount).sum());
        double taxTotal = round(lineItems.stream().filter(item -> item.getCategory() == InvoiceItemCategoryValue.TAX).mapToDouble(FleetInvoiceLineItem::getAmount).sum());
        double total = round(lineItems.stream().mapToDouble(FleetInvoiceLineItem::getAmount).sum());
        return FleetInvoice.builder()
                .invoiceNumber(generateNumber("INV"))
                .bookingId(bookingId)
                .rentalId(rentalId)
                .settlementId(settlementId)
                .customerName(customerName)
                .status(status)
                .lineItems(lineItems)
                .subtotal(subtotal)
                .taxTotal(taxTotal)
                .total(total)
                .amountPaid(0)
                .balanceDue(total)
                .issuedAt(now().toString())
                .build();
    }

    private FleetInvoiceLineItem lineItem(String label, double amount, InvoiceItemCategoryValue category) {
        return FleetInvoiceLineItem.builder().id(generateId("line")).label(label).amount(round(amount)).category(category).build();
    }

    private void applyPayment(FleetInvoice invoice, double amount) {
        invoice.setAmountPaid(round(invoice.getAmountPaid() + amount));
        invoice.setBalanceDue(round(Math.max(0, invoice.getTotal() - invoice.getAmountPaid())));
        if (invoice.getAmountPaid() >= invoice.getTotal()) {
            invoice.setStatus(InvoiceStatusValue.PAID);
        } else if (invoice.getAmountPaid() > 0) {
            invoice.setStatus(InvoiceStatusValue.PARTIALLY_PAID);
        }
    }

    private void applyRefund(FleetInvoice invoice, double amount) {
        invoice.setAmountPaid(round(Math.max(0, invoice.getAmountPaid() - amount)));
        invoice.setBalanceDue(round(Math.max(0, invoice.getTotal() - invoice.getAmountPaid())));
        invoice.setStatus(invoice.getAmountPaid() > 0 ? InvoiceStatusValue.PARTIALLY_PAID : InvoiceStatusValue.REFUNDED);
    }

    private Pricing calculatePricingBreakdown(FleetRatePlan ratePlan, String vehicleClass, String pickupDateTime, String dropoffDateTime, double addOnTotal, Double overrideEstimatedTotal) {
        int rentalDays = Math.max(1, (int) Math.ceil(ChronoUnit.HOURS.between(parseDateTime(pickupDateTime), parseDateTime(dropoffDateTime)) / 24.0));
        double baseRate = ratePlan != null ? ratePlan.getDailyRate() : defaultBaseRate(vehicleClass);
        double discountTotal = 0;
        double subtotal = baseRate * rentalDays + addOnTotal - discountTotal;
        double taxTotal = round(subtotal * (ratePlan != null ? ratePlan.getTaxRate() : 0.09));
        double estimatedTotal = round(overrideEstimatedTotal != null ? overrideEstimatedTotal : subtotal + taxTotal);
        return new Pricing(ratePlan != null ? ratePlan.getId().toString() : null, round(baseRate), rentalDays, round(addOnTotal), discountTotal, taxTotal, estimatedTotal);
    }

    private ReturnCharge calculateReturnOutcome(String fuelIn, boolean damageFlag, boolean maintenanceFlag, int lateHours, double extraCharges) {
        double lateCharge = lateHours > 0 ? lateHours * 15.0 : 0;
        double fuelCharge = switch (fuelIn) {
            case "Full" -> 0;
            case "3/4" -> 20;
            case "1/2" -> 45;
            case "1/4" -> 75;
            default -> 100;
        };
        double damageCharge = damageFlag ? 250 : 0;
        double maintenanceCharge = maintenanceFlag ? 120 : 0;
        double baseCharges = round(lateCharge + fuelCharge + damageCharge + maintenanceCharge);
        double totalCharges = round(baseCharges + extraCharges);
        ReturnOutcomeValue outcome = damageFlag ? ReturnOutcomeValue.DAMAGE_REVIEW_REQUIRED : maintenanceFlag ? ReturnOutcomeValue.MAINTENANCE_HOLD : totalCharges > 0 ? ReturnOutcomeValue.CHARGES_APPLIED : ReturnOutcomeValue.CLEAN_CLOSE;
        return new ReturnCharge(baseCharges, totalCharges, outcome);
    }

    private Map<String, Object> toCustomer(FleetCustomer customer) { Map<String, Object> map = new LinkedHashMap<>(); map.put("id", customer.getId()); map.put("customerNumber", customer.getCustomerNumber()); map.put("fullName", customer.getFullName()); map.put("email", customer.getEmail()); map.put("phone", customer.getPhone()); map.put("licenseNumber", customer.getLicenseNumber()); map.put("licenseExpiry", customer.getLicenseExpiry()); map.put("identityStatus", toLabel(customer.getIdentityStatus())); map.put("notes", customer.getNotes()); map.put("status", toLabel(customer.getStatus())); map.put("createdAt", customer.getCreatedAt()); return map; }
    private Map<String, Object> toRatePlan(FleetRatePlan plan) { Map<String, Object> map = new LinkedHashMap<>(); map.put("id", plan.getId()); map.put("name", plan.getName()); map.put("vehicleClass", plan.getVehicleClass()); map.put("dailyRate", plan.getDailyRate()); map.put("includedMileagePerDay", plan.getIncludedMileagePerDay()); map.put("depositAmount", plan.getDepositAmount()); map.put("taxRate", plan.getTaxRate()); map.put("active", plan.isActive()); map.put("createdAt", plan.getCreatedAt()); return map; }
    private Map<String, Object> toBooking(FleetBooking booking) { Map<String, Object> map = new LinkedHashMap<>(); map.put("id", booking.getId()); map.put("bookingNumber", booking.getBookingNumber()); map.put("customerId", booking.getCustomerId()); map.put("customerName", booking.getCustomerName()); map.put("pickupLocation", booking.getPickupLocation()); map.put("dropoffLocation", booking.getDropoffLocation()); map.put("pickupDateTime", booking.getPickupDateTime()); map.put("dropoffDateTime", booking.getDropoffDateTime()); map.put("vehicleClass", booking.getVehicleClass()); map.put("assignedVehicleId", booking.getAssignedVehicleId()); map.put("assignedVehiclePlate", booking.getAssignedVehiclePlate()); map.put("estimatedTotal", booking.getEstimatedTotal()); map.put("depositAmount", booking.getDepositAmount()); map.put("pricingBreakdown", pricingDto(booking.getRatePlanId(), booking.getBaseRate(), booking.getRentalDays(), booking.getAddOnTotal(), booking.getDiscountTotal(), booking.getTaxTotal(), booking.getEstimatedTotal())); map.put("depositStatus", toLabel(booking.getDepositStatus())); map.put("status", toLabel(booking.getStatus())); map.put("createdAt", booking.getCreatedAt()); map.put("notes", booking.getNotes()); return map; }
    private Map<String, Object> toRental(FleetRental rental) { Map<String, Object> map = new LinkedHashMap<>(); map.put("id", rental.getId()); map.put("rentalNumber", rental.getRentalNumber()); map.put("bookingId", rental.getBookingId()); map.put("bookingNumber", rental.getBookingNumber()); map.put("customerId", rental.getCustomerId()); map.put("customerName", rental.getCustomerName()); map.put("vehicleId", rental.getVehicleId()); map.put("vehiclePlate", rental.getVehiclePlate()); map.put("vehicleClass", rental.getVehicleClass()); map.put("pickupDateTime", rental.getPickupDateTime()); map.put("expectedReturnDateTime", rental.getExpectedReturnDateTime()); map.put("actualReturnDateTime", rental.getActualReturnDateTime()); map.put("odometerOut", rental.getOdometerOut()); map.put("odometerIn", rental.getOdometerIn()); map.put("fuelOut", rental.getFuelOut()); map.put("fuelIn", rental.getFuelIn()); map.put("depositAmount", rental.getDepositAmount()); map.put("depositStatus", toLabel(rental.getDepositStatus())); map.put("pricingBreakdown", pricingDto(rental.getRatePlanId(), rental.getBaseRate(), rental.getRentalDays(), rental.getAddOnTotal(), rental.getDiscountTotal(), rental.getTaxTotal(), rental.getEstimatedTotal())); map.put("addOns", rental.getAddOns()); map.put("status", toLabel(rental.getStatus())); map.put("createdAt", rental.getCreatedAt()); map.put("notes", rental.getNotes()); return map; }
    private Map<String, Object> toReturn(FleetReturn item) { Map<String, Object> map = new LinkedHashMap<>(); map.put("id", item.getId()); map.put("returnNumber", item.getReturnNumber()); map.put("rentalId", item.getRentalId()); map.put("rentalNumber", item.getRentalNumber()); map.put("bookingNumber", item.getBookingNumber()); map.put("customerName", item.getCustomerName()); map.put("vehiclePlate", item.getVehiclePlate()); map.put("odometerIn", item.getOdometerIn()); map.put("fuelIn", item.getFuelIn()); map.put("checklistId", item.getChecklistId()); map.put("damageFlag", item.isDamageFlag()); map.put("maintenanceFlag", item.isMaintenanceFlag()); map.put("lateHours", item.getLateHours()); map.put("baseCharges", item.getBaseCharges()); map.put("extraCharges", item.getExtraCharges()); map.put("totalCharges", item.getTotalCharges()); map.put("settlementId", item.getSettlementId()); map.put("outcome", toLabel(item.getOutcome())); map.put("submittedAt", item.getSubmittedAt()); map.put("notes", item.getNotes()); return map; }
    private Map<String, Object> toInvoice(FleetInvoice invoice) { Map<String, Object> map = new LinkedHashMap<>(); map.put("id", invoice.getId()); map.put("invoiceNumber", invoice.getInvoiceNumber()); map.put("bookingId", invoice.getBookingId()); map.put("rentalId", invoice.getRentalId()); map.put("settlementId", invoice.getSettlementId()); map.put("customerName", invoice.getCustomerName()); map.put("status", toLabel(invoice.getStatus())); map.put("lineItems", invoice.getLineItems().stream().map(item -> FleetDtos.InvoiceLineItemDto.builder().id(item.getId()).label(item.getLabel()).amount(item.getAmount()).category(toLabel(item.getCategory())).build()).collect(Collectors.toList())); map.put("subtotal", invoice.getSubtotal()); map.put("taxTotal", invoice.getTaxTotal()); map.put("total", invoice.getTotal()); map.put("amountPaid", invoice.getAmountPaid()); map.put("balanceDue", invoice.getBalanceDue()); map.put("issuedAt", invoice.getIssuedAt()); return map; }
    private Map<String, Object> toPayment(FleetPayment payment) { Map<String, Object> map = new LinkedHashMap<>(); map.put("id", payment.getId()); map.put("paymentNumber", payment.getPaymentNumber()); map.put("invoiceId", payment.getInvoiceId()); map.put("customerName", payment.getCustomerName()); map.put("amount", payment.getAmount()); map.put("method", toLabel(payment.getMethod())); map.put("status", toLabel(payment.getStatus())); map.put("paymentType", toLabel(payment.getPaymentType())); map.put("createdAt", payment.getCreatedAtValue()); return map; }
    private Map<String, Object> toRefund(FleetRefund refund) { Map<String, Object> map = new LinkedHashMap<>(); map.put("id", refund.getId()); map.put("refundNumber", refund.getRefundNumber()); map.put("invoiceId", refund.getInvoiceId()); map.put("settlementId", refund.getSettlementId()); map.put("customerName", refund.getCustomerName()); map.put("amount", refund.getAmount()); map.put("reason", refund.getReason()); map.put("status", toLabel(refund.getStatus())); map.put("createdAt", refund.getCreatedAtValue()); return map; }
    private Map<String, Object> toSettlement(FleetSettlement settlement) { Map<String, Object> map = new LinkedHashMap<>(); map.put("id", settlement.getId()); map.put("settlementNumber", settlement.getSettlementNumber()); map.put("rentalId", settlement.getRentalId()); map.put("returnId", settlement.getReturnId()); map.put("customerName", settlement.getCustomerName()); map.put("vehiclePlate", settlement.getVehiclePlate()); map.put("depositHeld", settlement.getDepositHeld()); map.put("depositApplied", settlement.getDepositApplied()); map.put("depositRefunded", settlement.getDepositRefunded()); map.put("returnCharges", settlement.getReturnCharges()); map.put("invoiceId", settlement.getInvoiceId()); map.put("refundId", settlement.getRefundId()); map.put("amountDue", settlement.getAmountDue()); map.put("amountRefundable", settlement.getAmountRefundable()); map.put("status", toLabel(settlement.getStatus())); map.put("createdAt", settlement.getCreatedAtValue()); return map; }
    private Map<String, Object> toWorkOrder(FleetWorkOrder workOrder) { Map<String, Object> map = new LinkedHashMap<>(); map.put("id", workOrder.getId()); map.put("workOrderNumber", workOrder.getWorkOrderNumber()); map.put("rentalId", workOrder.getRentalId()); map.put("returnId", workOrder.getReturnId()); map.put("vehiclePlate", workOrder.getVehiclePlate()); map.put("issueSummary", workOrder.getIssueSummary()); map.put("priority", toLabel(workOrder.getPriority())); map.put("assignee", workOrder.getAssignee()); map.put("vendor", workOrder.getVendor()); map.put("estimatedCost", workOrder.getEstimatedCost()); map.put("status", toLabel(workOrder.getStatus())); map.put("createdAt", workOrder.getCreatedAtValue()); return map; }
    private Map<String, Object> toDamageCase(FleetDamageCase damageCase) { Map<String, Object> map = new LinkedHashMap<>(); map.put("id", damageCase.getId()); map.put("caseNumber", damageCase.getCaseNumber()); map.put("rentalId", damageCase.getRentalId()); map.put("returnId", damageCase.getReturnId()); map.put("vehiclePlate", damageCase.getVehiclePlate()); map.put("customerName", damageCase.getCustomerName()); map.put("description", damageCase.getDescription()); map.put("severity", toLabel(damageCase.getSeverity())); map.put("estimatedRepairCost", damageCase.getEstimatedRepairCost()); map.put("insuranceStatus", toLabel(damageCase.getInsuranceStatus())); map.put("status", toLabel(damageCase.getStatus())); map.put("createdAt", damageCase.getCreatedAtValue()); return map; }
    private Map<String, Object> toException(FleetException item) { Map<String, Object> map = new LinkedHashMap<>(); map.put("id", item.getId()); map.put("referenceNumber", item.getReferenceNumber()); map.put("type", toLabel(item.getType())); map.put("linkedId", item.getLinkedId()); map.put("vehiclePlate", item.getVehiclePlate()); map.put("customerName", item.getCustomerName()); map.put("summary", item.getSummary()); map.put("owner", item.getOwner()); map.put("status", toLabel(item.getStatus())); map.put("createdAt", item.getCreatedAtValue()); return map; }
    private Map<String, Object> toBranch(FleetBranch branch) { Map<String, Object> map = new LinkedHashMap<>(); map.put("id", branch.getId()); map.put("code", branch.getCode()); map.put("name", branch.getName()); map.put("city", branch.getCity()); map.put("manager", branch.getManager()); map.put("vehicleCount", branch.getVehicleCount()); map.put("active", branch.isActive()); return map; }
    private Map<String, Object> toTransfer(FleetTransfer transfer) { Map<String, Object> map = new LinkedHashMap<>(); map.put("id", transfer.getId()); map.put("requestNumber", transfer.getRequestNumber()); map.put("vehiclePlate", transfer.getVehiclePlate()); map.put("fromBranch", transfer.getFromBranch()); map.put("toBranch", transfer.getToBranch()); map.put("requestedBy", transfer.getRequestedBy()); map.put("status", toLabel(transfer.getStatus())); map.put("eta", transfer.getEta()); map.put("createdAt", transfer.getCreatedAtValue()); return map; }
    private Map<String, Object> toApproval(FleetApproval approval) { Map<String, Object> map = new LinkedHashMap<>(); map.put("id", approval.getId()); map.put("approvalNumber", approval.getApprovalNumber()); map.put("category", toLabel(approval.getCategory())); map.put("requester", approval.getRequester()); map.put("summary", approval.getSummary()); map.put("approver", approval.getApprover()); map.put("status", toLabel(approval.getStatus())); map.put("createdAt", approval.getCreatedAtValue()); return map; }
    private Map<String, Object> toNotification(FleetNotification notification) { Map<String, Object> map = new LinkedHashMap<>(); map.put("id", notification.getId()); map.put("title", notification.getTitle()); map.put("body", notification.getBody()); map.put("channel", toLabel(notification.getChannel())); map.put("status", toLabel(notification.getStatus())); map.put("createdAt", notification.getCreatedAtValue()); return map; }

    private FleetDtos.PricingBreakdownDto pricingDto(String ratePlanId, double baseRate, int rentalDays, double addOnTotal, double discountTotal, double taxTotal, double estimatedTotal) { return FleetDtos.PricingBreakdownDto.builder().ratePlanId(ratePlanId).baseRate(baseRate).rentalDays(rentalDays).addOnTotal(addOnTotal).discountTotal(discountTotal).taxTotal(taxTotal).estimatedTotal(estimatedTotal).build(); }
    private FleetRatePlan findRatePlan(String vehicleClass) { return ratePlanRepository.findFirstByVehicleClassIgnoreCaseAndActiveTrue(vehicleClass).orElse(null); }
        private Vehicle findVehicleByPlate(String plate) { return vehicleRepository.findByPlateNumber(plate).orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with plate " + plate)); }
    private Vehicle ensureVehicle(String plate, String model, String manufacturer, int year, String status) {
        return vehicleRepository.findByPlateNumber(plate)
                .orElseGet(() -> vehicleRepository.save(Vehicle.builder().plateNumber(plate).model(model).manufacturer(manufacturer).year(year).status(status).build()));
    }
    private void updateVehicleStatus(UUID vehicleId, VehicleOperationalStatusValue status) { Vehicle vehicle = vehicleRepository.findById(vehicleId).orElseThrow(() -> new ResourceNotFoundException("Vehicle not found")); vehicle.setStatus(switch (status) { case AVAILABLE -> "Available"; case RESERVED -> "Reserved"; case RENTED -> "Rented"; case MAINTENANCE -> "Maintenance"; case INSPECTION_HOLD -> "Inspection Hold"; }); }
    private String inferVehicleClass(Vehicle vehicle) { String model = vehicle.getModel().toLowerCase(Locale.ROOT); if (model.contains("rav4") || model.contains("cr-v") || model.contains("hr-v") || model.contains("qashqai") || model.contains("x-trail") || model.contains("cx-5") || model.contains("cx-9") || model.contains("explorer") || model.contains("glc") || model.contains("x5") || model.contains("model y") || model.contains("vezel")) return "SUV"; if (model.contains("alphard") || model.contains("transit") || model.contains("hiace") || model.contains("ranger") || model.contains("van")) return "Van"; return "Sedan"; }
    private String toOperationalStatus(String vehicleStatus) { return switch (vehicleStatus == null ? "" : vehicleStatus.toLowerCase(Locale.ROOT)) { case "reserved" -> "reserved"; case "rented" -> "rented"; case "maintenance" -> "maintenance"; case "inspection hold" -> "inspection_hold"; default -> "available"; }; }
        private LocalDateTime parseDateTime(String value) {
        if (value.endsWith("Z")) {
            return java.time.OffsetDateTime.parse(value).toLocalDateTime();
        }
        return LocalDateTime.parse(value);
    }
    private double defaultBaseRate(String vehicleClass) { return switch (vehicleClass) { case "SUV" -> 120; case "Van" -> 150; default -> 90; }; }
    private String normalize(String value) { return value == null ? "" : value.trim().toLowerCase(Locale.ROOT); }
    private boolean containsAny(String query, String... values) { for (String value : values) { if (value != null && value.toLowerCase(Locale.ROOT).contains(query)) return true; } return false; }
    private String generateNumber(String prefix) { return prefix + "-" + System.currentTimeMillis(); }
    private String generateId(String prefix) { return prefix + "-" + UUID.randomUUID().toString().substring(0, 8); }
    private LocalDateTime now() { return LocalDateTime.now(); }
    private double round(double value) { return Math.round(value * 100.0) / 100.0; }
    private <E extends Enum<E>> E parseEnum(Class<E> enumType, String value) { String normalized = value.trim().replace('-', '_').replace(' ', '_').toUpperCase(Locale.ROOT); return Enum.valueOf(enumType, normalized); }
    private String toLabel(Enum<?> value) { if (value == null) return null; return switch (value.name()) { case "CHECKED_OUT" -> "Checked Out"; case "PARTIALLY_REFUNDED" -> "Partially Refunded"; case "PARTIALLY_PAID" -> "Partially Paid"; case "DAMAGE_REVIEW_REQUIRED" -> "Damage Review Required"; case "MAINTENANCE_HOLD" -> "Maintenance Hold"; case "IN_TRANSIT" -> "In Transit"; case "BANK_TRANSFER" -> "Bank Transfer"; case "CORPORATE_CREDIT" -> "Corporate Credit"; case "ADD_ON" -> "Add-on"; default -> toTitleCase(value.name()); }; }
    private String toTitleCase(String text) { String[] parts = text.split("_"); StringBuilder builder = new StringBuilder(); for (int i = 0; i < parts.length; i++) { String part = parts[i].toLowerCase(Locale.ROOT); builder.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1)); if (i < parts.length - 1) builder.append(' '); } return builder.toString(); }

    private record Pricing(String ratePlanId, double baseRate, int rentalDays, double addOnTotal, double discountTotal, double taxTotal, double estimatedTotal) {}
    private record ReturnCharge(double baseCharges, double totalCharges, ReturnOutcomeValue outcome) {}
}





