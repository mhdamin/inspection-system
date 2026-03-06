package com.muvs.inspection_system.fleet;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface FleetCustomerRepository extends JpaRepository<FleetCustomer, UUID> {
}

interface FleetRatePlanRepository extends JpaRepository<FleetRatePlan, UUID> {
    Optional<FleetRatePlan> findFirstByVehicleClassIgnoreCaseAndActiveTrue(String vehicleClass);
}

interface FleetBookingRepository extends JpaRepository<FleetBooking, UUID> {
    List<FleetBooking> findByCustomerIdOrderByCreatedAtDesc(String customerId);
}

interface FleetRentalRepository extends JpaRepository<FleetRental, UUID> {
    List<FleetRental> findByCustomerIdOrderByCreatedAtDesc(String customerId);
}

interface FleetReturnRepository extends JpaRepository<FleetReturn, UUID> {
}

interface FleetInvoiceRepository extends JpaRepository<FleetInvoice, UUID> {
    Optional<FleetInvoice> findFirstByRentalIdAndStatus(String rentalId, InvoiceStatusValue status);
}

interface FleetPaymentRepository extends JpaRepository<FleetPayment, UUID> {
}

interface FleetRefundRepository extends JpaRepository<FleetRefund, UUID> {
}

interface FleetSettlementRepository extends JpaRepository<FleetSettlement, UUID> {
}

interface FleetWorkOrderRepository extends JpaRepository<FleetWorkOrder, UUID> {
    Optional<FleetWorkOrder> findByReturnId(String returnId);
}

interface FleetDamageCaseRepository extends JpaRepository<FleetDamageCase, UUID> {
    Optional<FleetDamageCase> findByReturnId(String returnId);
}

interface FleetExceptionRepository extends JpaRepository<FleetException, UUID> {
    Optional<FleetException> findByTypeAndLinkedId(ExceptionTypeValue type, String linkedId);
}

interface FleetBranchRepository extends JpaRepository<FleetBranch, UUID> {
}

interface FleetTransferRepository extends JpaRepository<FleetTransfer, UUID> {
}

interface FleetApprovalRepository extends JpaRepository<FleetApproval, UUID> {
}

interface FleetNotificationRepository extends JpaRepository<FleetNotification, UUID> {
    Optional<FleetNotification> findByTitle(String title);
}
