package com.muvs.inspection_system.fleet;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public final class FleetDtos {
    private FleetDtos() {
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookingRequest {
        @NotBlank
        private String customerId;
        @NotBlank
        private String pickupLocation;
        @NotBlank
        private String dropoffLocation;
        @NotBlank
        private String pickupDateTime;
        @NotBlank
        private String dropoffDateTime;
        @NotBlank
        private String vehicleClass;
        @Min(0)
        private double estimatedTotal;
        @Min(0)
        private double depositAmount;
        private String notes;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerRequest {
        @NotBlank
        private String fullName;
        @NotBlank
        private String email;
        @NotBlank
        private String phone;
        @NotBlank
        private String licenseNumber;
        @NotBlank
        private String licenseExpiry;
        @NotBlank
        private String identityStatus;
        @NotBlank
        private String status;
        private String notes;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RatePlanRequest {
        @NotBlank
        private String name;
        @NotBlank
        private String vehicleClass;
        @Min(0)
        private double dailyRate;
        @Min(0)
        private int includedMileagePerDay;
        @Min(0)
        private double depositAmount;
        @Min(0)
        private double taxRate;
        private boolean active;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RentalCreateRequest {
        @NotBlank
        private String bookingId;
        @NotBlank
        private String vehicleId;
        @Min(0)
        private long odometerOut;
        @NotBlank
        private String fuelOut;
        @Min(0)
        private double depositAmount;
        private List<String> addOns;
        private String notes;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RentalExtendRequest {
        @NotBlank
        private String expectedReturnDateTime;
        private String notes;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReturnQuoteRequest {
        @NotBlank
        private String rentalId;
        @NotBlank
        private String fuelIn;
        private boolean damageFlag;
        private boolean maintenanceFlag;
        @Min(0)
        private int lateHours;
        @Min(0)
        private double extraCharges;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReturnSubmitRequest {
        @NotBlank
        private String rentalId;
        @Min(0)
        private long odometerIn;
        @NotBlank
        private String fuelIn;
        private String checklistId;
        private boolean damageFlag;
        private boolean maintenanceFlag;
        @Min(0)
        private int lateHours;
        @Min(0)
        private double extraCharges;
        private String notes;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentCreateRequest {
        @NotBlank
        private String invoiceId;
        @NotBlank
        private String customerName;
        @Min(0)
        private double amount;
        @NotBlank
        private String method;
        @NotBlank
        private String paymentType;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RefundRequest {
        private String invoiceId;
        private String settlementId;
        @NotBlank
        private String customerName;
        @Min(0)
        private double amount;
        @NotBlank
        private String reason;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TransferRequestInput {
        @NotBlank
        private String vehiclePlate;
        @NotBlank
        private String fromBranch;
        @NotBlank
        private String toBranch;
        @NotBlank
        private String requestedBy;
        @NotBlank
        private String status;
        @NotBlank
        private String eta;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AssignVehicleRequest {
        @NotBlank
        private String vehicleId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuoteResponse {
        private double baseCharges;
        private double totalCharges;
        private String outcome;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PricingBreakdownDto {
        private String ratePlanId;
        private double baseRate;
        private int rentalDays;
        private double addOnTotal;
        private double discountTotal;
        private double taxTotal;
        private double estimatedTotal;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerHistorySummaryDto {
        private int bookings;
        private int rentals;
        private int activeRentals;
        private LocalDateTime lastBookingDate;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VehicleOptionDto {
        private UUID id;
        private String plateNumber;
        private String label;
        private String vehicleClass;
        private String operationalStatus;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RentalFinancialSummaryDto {
        private PricingBreakdownDto pricingBreakdown;
        private String depositStatus;
        private double depositAmount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InvoiceLineItemDto {
        private String id;
        private String label;
        private double amount;
        private String category;
    }
}
