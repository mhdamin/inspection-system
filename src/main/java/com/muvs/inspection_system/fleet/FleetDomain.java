package com.muvs.inspection_system.fleet;

import com.muvs.inspection_system.entity.BaseEntity;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

public final class FleetDomain {
    private FleetDomain() {
    }
}

enum BookingStatusValue { DRAFT, CONFIRMED, ASSIGNED, CANCELLED, CHECKED_OUT, COMPLETED }
enum RentalStatusValue { RESERVED, ACTIVE, OVERDUE, CLOSED }
enum ReturnOutcomeValue { CLEAN_CLOSE, CHARGES_APPLIED, DAMAGE_REVIEW_REQUIRED, MAINTENANCE_HOLD }
enum VehicleOperationalStatusValue { AVAILABLE, RESERVED, RENTED, MAINTENANCE, INSPECTION_HOLD }
enum DepositStatusValue { HELD, APPLIED, PARTIALLY_REFUNDED, REFUNDED }
enum InvoiceStatusValue { DRAFT, ISSUED, PAID, PARTIALLY_PAID, REFUNDED }
enum PaymentStatusValue { PENDING, CAPTURED, FAILED, REFUNDED }
enum PaymentMethodValue { CARD, BANK_TRANSFER, CASH, CORPORATE_CREDIT }
enum PaymentTypeValue { DEPOSIT, INVOICE, SETTLEMENT }
enum WorkOrderStatusValue { OPEN, IN_PROGRESS, WAITING_PARTS, COMPLETED }
enum DamageCaseStatusValue { OPEN, REVIEW, REPAIRING, RESOLVED }
enum ExceptionStatusValue { OPEN, ASSIGNED, RESOLVED }
enum ExceptionTypeValue { MAINTENANCE, DAMAGE, SETTLEMENT }
enum ApprovalStatusValue { PENDING, APPROVED, REJECTED }
enum TransferStatusValue { REQUESTED, IN_TRANSIT, COMPLETED }
enum NotificationStatusValue { UNREAD, READ }
enum CustomerIdentityStatusValue { VERIFIED, PENDING, FLAGGED }
enum CustomerStatusValue { ACTIVE, WATCHLIST, INACTIVE }
enum InvoiceItemCategoryValue { RENTAL, DEPOSIT, ADD_ON, TAX, PENALTY, REFUND }
enum SettlementStatusValue { OPEN, SETTLED, REFUNDED }
enum PriorityLevelValue { LOW, MEDIUM, HIGH }
enum DamageSeverityValue { MINOR, MODERATE, MAJOR }
enum InsuranceStatusValue { UNSUBMITTED, SUBMITTED, APPROVED }
enum ApprovalCategoryValue { REFUND, TRANSFER, OVERRIDE }
enum NotificationChannelValue { OPERATIONS, FINANCE, CUSTOMER }
enum RefundStatusValue { PENDING, PROCESSED }

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "fleet_customers")
class FleetCustomer extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String customerNumber;
    @Column(nullable = false)
    private String fullName;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String phone;
    @Column(nullable = false)
    private String licenseNumber;
    @Column(nullable = false)
    private String licenseExpiry;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomerIdentityStatusValue identityStatus;
    @Column(length = 2000)
    private String notes;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomerStatusValue status;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "fleet_rate_plans")
class FleetRatePlan extends BaseEntity {
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String vehicleClass;
    @Column(nullable = false)
    private double dailyRate;
    @Column(nullable = false)
    private int includedMileagePerDay;
    @Column(nullable = false)
    private double depositAmount;
    @Column(nullable = false)
    private double taxRate;
    @Column(nullable = false)
    private boolean active;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "fleet_bookings")
class FleetBooking extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String bookingNumber;
    @Column(nullable = false)
    private String customerId;
    @Column(nullable = false)
    private String customerName;
    @Column(nullable = false)
    private String pickupLocation;
    @Column(nullable = false)
    private String dropoffLocation;
    @Column(nullable = false)
    private String pickupDateTime;
    @Column(nullable = false)
    private String dropoffDateTime;
    @Column(nullable = false)
    private String vehicleClass;
    private String assignedVehicleId;
    private String assignedVehiclePlate;
    @Column(nullable = false)
    private double estimatedTotal;
    @Column(nullable = false)
    private double depositAmount;
    private String ratePlanId;
    @Column(nullable = false)
    private double baseRate;
    @Column(nullable = false)
    private int rentalDays;
    @Column(nullable = false)
    private double addOnTotal;
    @Column(nullable = false)
    private double discountTotal;
    @Column(nullable = false)
    private double taxTotal;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DepositStatusValue depositStatus;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatusValue status;
    @Column(length = 2000)
    private String notes;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "fleet_rentals")
class FleetRental extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String rentalNumber;
    @Column(nullable = false)
    private String bookingId;
    @Column(nullable = false)
    private String bookingNumber;
    @Column(nullable = false)
    private String customerId;
    @Column(nullable = false)
    private String customerName;
    @Column(nullable = false)
    private String vehicleId;
    @Column(nullable = false)
    private String vehiclePlate;
    @Column(nullable = false)
    private String vehicleClass;
    @Column(nullable = false)
    private String pickupDateTime;
    @Column(nullable = false)
    private String expectedReturnDateTime;
    private String actualReturnDateTime;
    @Column(nullable = false)
    private long odometerOut;
    private Long odometerIn;
    @Column(nullable = false)
    private String fuelOut;
    private String fuelIn;
    @Column(nullable = false)
    private double depositAmount;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DepositStatusValue depositStatus;
    private String ratePlanId;
    @Column(nullable = false)
    private double baseRate;
    @Column(nullable = false)
    private int rentalDays;
    @Column(nullable = false)
    private double addOnTotal;
    @Column(nullable = false)
    private double discountTotal;
    @Column(nullable = false)
    private double taxTotal;
    @Column(nullable = false)
    private double estimatedTotal;
    @ElementCollection
    @CollectionTable(name = "fleet_rental_add_ons", joinColumns = @JoinColumn(name = "rental_id"))
    @Column(name = "add_on")
    @Builder.Default
    private List<String> addOns = new ArrayList<>();
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RentalStatusValue status;
    @Column(length = 2000)
    private String notes;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "fleet_returns")
class FleetReturn extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String returnNumber;
    @Column(nullable = false)
    private String rentalId;
    @Column(nullable = false)
    private String rentalNumber;
    @Column(nullable = false)
    private String bookingNumber;
    @Column(nullable = false)
    private String customerName;
    @Column(nullable = false)
    private String vehiclePlate;
    @Column(nullable = false)
    private long odometerIn;
    @Column(nullable = false)
    private String fuelIn;
    private String checklistId;
    @Column(nullable = false)
    private boolean damageFlag;
    @Column(nullable = false)
    private boolean maintenanceFlag;
    @Column(nullable = false)
    private int lateHours;
    @Column(nullable = false)
    private double baseCharges;
    @Column(nullable = false)
    private double extraCharges;
    @Column(nullable = false)
    private double totalCharges;
    private String settlementId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReturnOutcomeValue outcome;
    @Column(nullable = false)
    private String submittedAt;
    @Column(length = 2000)
    private String notes;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
class FleetInvoiceLineItem {
    @Column(name = "line_item_id")
    private String id;
    private String label;
    private double amount;
    @Enumerated(EnumType.STRING)
    private InvoiceItemCategoryValue category;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "fleet_invoices")
class FleetInvoice extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String invoiceNumber;
    private String bookingId;
    private String rentalId;
    private String settlementId;
    @Column(nullable = false)
    private String customerName;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatusValue status;
    @ElementCollection
    @CollectionTable(name = "fleet_invoice_line_items", joinColumns = @JoinColumn(name = "invoice_id"))
    @Builder.Default
    private List<FleetInvoiceLineItem> lineItems = new ArrayList<>();
    @Column(nullable = false)
    private double subtotal;
    @Column(nullable = false)
    private double taxTotal;
    @Column(nullable = false)
    private double total;
    @Column(nullable = false)
    private double amountPaid;
    @Column(nullable = false)
    private double balanceDue;
    @Column(nullable = false)
    private String issuedAt;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "fleet_payments")
class FleetPayment extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String paymentNumber;
    @Column(nullable = false)
    private String invoiceId;
    @Column(nullable = false)
    private String customerName;
    @Column(nullable = false)
    private double amount;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethodValue method;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatusValue status;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentTypeValue paymentType;
    @Column(nullable = false)
    private String createdAtValue;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "fleet_refunds")
class FleetRefund extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String refundNumber;
    private String invoiceId;
    private String settlementId;
    @Column(nullable = false)
    private String customerName;
    @Column(nullable = false)
    private double amount;
    @Column(nullable = false, length = 2000)
    private String reason;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RefundStatusValue status;
    @Column(nullable = false)
    private String createdAtValue;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "fleet_settlements")
class FleetSettlement extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String settlementNumber;
    @Column(nullable = false)
    private String rentalId;
    @Column(nullable = false)
    private String returnId;
    @Column(nullable = false)
    private String customerName;
    @Column(nullable = false)
    private String vehiclePlate;
    @Column(nullable = false)
    private double depositHeld;
    @Column(nullable = false)
    private double depositApplied;
    @Column(nullable = false)
    private double depositRefunded;
    @Column(nullable = false)
    private double returnCharges;
    @Column(nullable = false)
    private String invoiceId;
    private String refundId;
    @Column(nullable = false)
    private double amountDue;
    @Column(nullable = false)
    private double amountRefundable;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SettlementStatusValue status;
    @Column(nullable = false)
    private String createdAtValue;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "fleet_work_orders")
class FleetWorkOrder extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String workOrderNumber;
    @Column(nullable = false)
    private String rentalId;
    @Column(nullable = false)
    private String returnId;
    @Column(nullable = false)
    private String vehiclePlate;
    @Column(nullable = false, length = 2000)
    private String issueSummary;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PriorityLevelValue priority;
    @Column(nullable = false)
    private String assignee;
    private String vendor;
    @Column(nullable = false)
    private double estimatedCost;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkOrderStatusValue status;
    @Column(nullable = false)
    private String createdAtValue;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "fleet_damage_cases")
class FleetDamageCase extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String caseNumber;
    @Column(nullable = false)
    private String rentalId;
    @Column(nullable = false)
    private String returnId;
    @Column(nullable = false)
    private String vehiclePlate;
    @Column(nullable = false)
    private String customerName;
    @Column(nullable = false, length = 2000)
    private String description;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DamageSeverityValue severity;
    @Column(nullable = false)
    private double estimatedRepairCost;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InsuranceStatusValue insuranceStatus;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DamageCaseStatusValue status;
    @Column(nullable = false)
    private String createdAtValue;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "fleet_exceptions")
class FleetException extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String referenceNumber;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExceptionTypeValue type;
    @Column(nullable = false)
    private String linkedId;
    @Column(nullable = false)
    private String vehiclePlate;
    private String customerName;
    @Column(nullable = false, length = 2000)
    private String summary;
    @Column(nullable = false)
    private String owner;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExceptionStatusValue status;
    @Column(nullable = false)
    private String createdAtValue;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "fleet_branches")
class FleetBranch extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String code;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String city;
    @Column(nullable = false)
    private String manager;
    @Column(nullable = false)
    private int vehicleCount;
    @Column(nullable = false)
    private boolean active;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "fleet_transfers")
class FleetTransfer extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String requestNumber;
    @Column(nullable = false)
    private String vehiclePlate;
    @Column(nullable = false)
    private String fromBranch;
    @Column(nullable = false)
    private String toBranch;
    @Column(nullable = false)
    private String requestedBy;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransferStatusValue status;
    @Column(nullable = false)
    private String eta;
    @Column(nullable = false)
    private String createdAtValue;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "fleet_approvals")
class FleetApproval extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String approvalNumber;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalCategoryValue category;
    @Column(nullable = false)
    private String requester;
    @Column(nullable = false, length = 2000)
    private String summary;
    @Column(nullable = false)
    private String approver;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalStatusValue status;
    @Column(nullable = false)
    private String createdAtValue;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "fleet_notifications")
class FleetNotification extends BaseEntity {
    @Column(nullable = false)
    private String title;
    @Column(nullable = false, length = 2000)
    private String body;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationChannelValue channel;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatusValue status;
    @Column(nullable = false)
    private String createdAtValue;
}
