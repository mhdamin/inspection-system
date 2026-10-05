package com.muvs.inspection_system.fleet;

import com.muvs.inspection_system.dto.*;
import com.muvs.inspection_system.entity.*;
import com.muvs.inspection_system.repository.*;
import com.muvs.inspection_system.service.ChecklistService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.IntStream;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@SpringBootTest(properties = {"app.cors.allowed-origins=https://demo.example.test", "spring.datasource.url=jdbc:h2:mem:workflow;DB_CLOSE_DELAY=-1", "spring.datasource.username=sa", "spring.datasource.password=", "spring.jpa.hibernate.ddl-auto=create-drop", "aws.s3.enabled=false", "app.seed-demo-data=false", "aws.s3.access-key=test", "aws.s3.secret-key=test"})
@AutoConfigureMockMvc
@Transactional
class FleetWorkflowIntegrationTest {
    @Autowired FleetService fleet;
    @Autowired FleetCustomerRepository customers;
    @Autowired FleetBookingRepository bookings;
    @Autowired FleetRentalRepository rentals;
    @Autowired FleetInvoiceRepository invoices;
    @Autowired FleetReturnRepository returns;
    @Autowired FleetSettlementRepository settlements;
    @Autowired FleetRefundRepository refunds;
    @Autowired VehicleRepository vehicles;
    @Autowired ChecklistService checklists;
    @Autowired EntityManager em;
    @Autowired MockMvc mvc;
    private FleetCustomer customer;
    private Vehicle vehicle;

    @BeforeEach void setup() {
        customer = customers.save(FleetCustomer.builder().customerNumber(UUID.randomUUID().toString()).fullName("Customer").email("customer@example.test").phone("12345678").licenseNumber("TEST").licenseExpiry("2031-12-31").identityStatus(CustomerIdentityStatusValue.VERIFIED).status(CustomerStatusValue.ACTIVE).build());
        vehicle = vehicles.save(Vehicle.builder().plateNumber(UUID.randomUUID().toString()).manufacturer("Toyota").model("Corolla").year(2024).status("Available").build());
    }
    private FleetDtos.BookingRequest request(String start, String end) {
        return new FleetDtos.BookingRequest(customer.getId().toString(), "HQ", "HQ", start, end, "Sedan", 1, 150, "");
    }
    private UUID booking() {
        UUID id=(UUID)fleet.createBooking(request("2027-01-01T10:00", "2027-01-03T10:00")).get("id");
        fleet.confirmBooking(id); return id;
    }
    private UUID rental() {
        UUID booking=booking();
        return (UUID)fleet.createRentalFromBooking(new FleetDtos.RentalCreateRequest(booking.toString(),vehicle.getId().toString(),100,"1/2",150,List.of(),"")).get("id");
    }
    private UUID inspection(UUID rental, String type) {
        var evidence=new InspectionEvidenceDTO(); evidence.setOdometer(100); evidence.setFuelLevel("1/2");
        evidence.setExterior(IntStream.rangeClosed(101,120).mapToObj(id -> { var p=new InspectionEvidenceDTO.Point(); p.setId(id); p.setStatus("Normal"); return p; }).toList());
        evidence.setInterior(Map.of("dashboard","Normal","seats","Normal","carpets","Normal","windows","Normal","electronics","Normal","safety","Normal"));
        evidence.setTyres(Map.of("frontLeft","Normal","frontRight","Normal","rearLeft","Normal","rearRight","Normal"));
        evidence.setPhotos(List.of()); evidence.setAcknowledged(true); evidence.setAcknowledgedBy("Customer");
        return checklists.createChecklist(ChecklistRequestDTO.builder().checklistNumber(UUID.randomUUID().toString()).rentalStartDate(LocalDate.of(2027,1,1)).customerName("Customer").customerPhone("12345678").staffName("inspector").rentalType(type).vehicleId(vehicle.getId()).rentalId(rental).evidence(evidence).completed(true).build());
    }
    private void collectDeposit(UUID id) {
        FleetInvoice invoice=invoices.findByRentalId(id.toString()).stream().filter(i -> i.getLineItems().stream().anyMatch(l -> l.getCategory()==InvoiceItemCategoryValue.DEPOSIT)).findFirst().orElseThrow();
        FleetDtos.PaymentCreateRequest payment=new FleetDtos.PaymentCreateRequest(); payment.setInvoiceId(invoice.getId().toString()); payment.setAmount(150); payment.setMethod("Cash"); payment.setReference("Receipt 123"); payment.setRequestKey(UUID.randomUUID().toString());
        fleet.createPayment(payment); fleet.createPayment(payment);
        assertThat(invoice.getAmountPaid()).isEqualTo(150);
    }
    @Test void corsAllowsConfiguredFrontendAndRejectsOtherOrigins() throws Exception {
        mvc.perform(options("/api/auth/login").header("Origin","https://demo.example.test")
                .header("Access-Control-Request-Method","POST"))
                .andExpect(status().isOk()).andExpect(header().string("Access-Control-Allow-Origin","https://demo.example.test"));
        mvc.perform(options("/api/auth/login").header("Origin","https://untrusted.example.test")
                .header("Access-Control-Request-Method","POST")).andExpect(status().isForbidden());
    }
    @Test void anonymousCannotRegisterPrivilegedUser() throws Exception {
        mvc.perform(post("/api/auth/register").contentType("application/json").content("{\"username\":\"attacker\",\"password\":\"long-password\",\"roles\":[\"ROLE_SUPERADMIN\"]}")).andExpect(status().isUnauthorized());
    }
    @Test void customerCannotReadStaffRecordsAndStaffCannotRefund() throws Exception {
        mvc.perform(get("/api/customers").with(user("renter").roles("CUSTOMER"))).andExpect(status().isForbidden());
        mvc.perform(post("/api/refunds").with(user("staff").roles("USER")).contentType("application/json").content("{}")).andExpect(status().isForbidden());
    }
    @Test void superadminCanCreateVehicles() throws Exception {
        mvc.perform(post("/api/vehicles").with(user("owner").roles("SUPERADMIN"))
                .contentType("application/json").content("{\"plateNumber\":\"ADMIN-TEST\",\"model\":\"Corolla\",\"manufacturer\":\"Toyota\",\"year\":2026,\"status\":\"Available\"}"))
                .andExpect(status().isCreated());
    }
    @Test void offsetDatesReferToTheSameBookingWindow() {
        UUID id=booking(); fleet.assignVehicleToBooking(id,vehicle.getId().toString());
        UUID second=(UUID)fleet.createBooking(request("2027-01-01T02:00:00Z","2027-01-03T02:00:00Z")).get("id");
        fleet.confirmBooking(second);
        assertThatThrownBy(() -> fleet.assignVehicleToBooking(second,vehicle.getId().toString())).hasMessageContaining("already allocated");
        assertThatThrownBy(() -> fleet.previewBooking(request("invalid","invalid"))).isInstanceOf(IllegalArgumentException.class);
    }
    @Test void customerCannotConfirmAnUnreviewedPrice() {
        var request=request("2027-01-01T10:00","2027-01-03T10:00");
        assertThatThrownBy(() -> fleet.bookCustomerTrip(request)).hasMessageContaining("price has changed");
    }
    @Test void malformedJwtReturns401() throws Exception {
        mvc.perform(get("/api/customers").header("Authorization","Bearer invalid")).andExpect(status().isUnauthorized());
    }
    @Test void overlappingAssignmentsAreRejectedAndAdjacentSlotsAllowed() {
        UUID first=booking(); fleet.assignVehicleToBooking(first,vehicle.getId().toString());
        UUID second=booking();
        assertThatThrownBy(() -> fleet.assignVehicleToBooking(second,vehicle.getId().toString())).hasMessageContaining("already allocated");
        UUID adjacent=(UUID)fleet.createBooking(request("2027-01-03T10:00","2027-01-04T10:00")).get("id");
        fleet.confirmBooking(adjacent); fleet.assignVehicleToBooking(adjacent,vehicle.getId().toString());
    }
    @Test void invalidDatesAndHoldAreRejected() {
        assertThatThrownBy(() -> fleet.createBooking(request("2027-01-03T10:00","2027-01-01T10:00"))).hasMessageContaining("after pickup");
        UUID booking=booking(); vehicle.setStatus("Maintenance");
        assertThatThrownBy(() -> fleet.assignVehicleToBooking(booking,vehicle.getId().toString())).hasMessageContaining("on hold");
    }
    @Test void conversionIsIdempotentAndUsesAgreedQuote() {
        UUID id=booking(); double quoted=bookings.findById(id).orElseThrow().getEstimatedTotal();
        var request=new FleetDtos.RentalCreateRequest(id.toString(),vehicle.getId().toString(),100,"Full",150,List.of(),"");
        var a=fleet.createRentalFromBooking(request); var b=fleet.createRentalFromBooking(request);
        assertThat(a.get("id")).isEqualTo(b.get("id")); assertThat(rentals.count()).isEqualTo(1);
        assertThat(rentals.findById((UUID)a.get("id")).orElseThrow().getEstimatedTotal()).isEqualTo(quoted);
        assertThatThrownBy(() -> fleet.cancelBooking(id)).hasMessageContaining("rental already exists");
    }
    @Test void inspectionEvidenceSurvivesReloadAndIsImmutable() {
        UUID rental=rental(); UUID inspection=inspection(rental,"PICKUP"); em.flush(); em.clear();
        var saved=checklists.getChecklist(inspection);
        assertThat(saved.getEvidence().getExterior()).hasSize(20); assertThat(saved.getEvidence().getFuelLevel()).isEqualTo("1/2"); assertThat(saved.isCompleted()).isTrue();
        assertThatThrownBy(() -> checklists.deleteChecklist(inspection)).hasMessageContaining("retained as evidence");
    }
    @Test void handoverRequiresDepositAndInspection() {
        UUID rental=rental();
        assertThatThrownBy(() -> fleet.startRental(rental)).hasMessageContaining("deposit");
        collectDeposit(rental);
        assertThatThrownBy(() -> fleet.startRental(rental)).hasMessageContaining("pickup inspection");
        inspection(rental,"PICKUP"); assertThat(fleet.startRental(rental).get("status")).isEqualTo("Active");
    }
    @Test void returnReconcilesDepositAndInvoiceAndDoesNotDuplicate() {
        UUID rental=rental(); collectDeposit(rental); inspection(rental,"PICKUP"); fleet.startRental(rental);
        UUID returned=inspection(rental,"RETURN");
        var request=new FleetDtos.ReturnSubmitRequest(rental.toString(),100,"1/2",returned.toString(),false,false,0,200,"");
        var a=fleet.submitReturn(request); var b=fleet.submitReturn(request);
        assertThat(a.get("id")).isEqualTo(b.get("id")); assertThat(returns.count()).isEqualTo(1);
        FleetSettlement settlement=settlements.findById(UUID.fromString(String.valueOf(a.get("settlementId")))).orElseThrow();
        assertThat(settlement.getAmountDue()).isEqualTo(50); assertThat(invoices.findById(UUID.fromString(settlement.getInvoiceId())).orElseThrow().getBalanceDue()).isEqualTo(50);
    }
    @Test void unchangedFuelIsFreeAndRefundRemainsPending() {
        UUID rental=rental(); collectDeposit(rental); inspection(rental,"PICKUP"); fleet.startRental(rental); UUID returned=inspection(rental,"RETURN");
        var result=fleet.submitReturn(new FleetDtos.ReturnSubmitRequest(rental.toString(),100,"1/2",returned.toString(),false,false,0,0,""));
        assertThat(result.get("totalCharges")).isEqualTo(0.0);
        assertThat(refunds.findAll()).singleElement().satisfies(r -> assertThat(r.getStatus()).isEqualTo(RefundStatusValue.PENDING));
        FleetSettlement settlement=settlements.findAll().get(0); assertThat(settlement.getDepositRefunded()).isZero();
        var refund=new FleetDtos.RefundRequest(); refund.setSettlementId(settlement.getId().toString()); refund.setAmount(151); refund.setReference("Bank transfer"); refund.setRequestKey("refund-1"); refund.setReason("Deposit");
        assertThatThrownBy(() -> fleet.processRefund(refund)).hasMessageContaining("cannot exceed");
    }
    @Test void extensionUpdatesInvoiceAtOriginalRate() {
        UUID rental=rental(); fleet.extendRental(rental,new FleetDtos.RentalExtendRequest("2027-01-04T10:00","Approved extension"));
        FleetRental saved=rentals.findById(rental).orElseThrow();
        FleetInvoice invoice=invoices.findByRentalId(rental.toString()).stream().filter(i -> i.getLineItems().stream().anyMatch(l -> l.getCategory()==InvoiceItemCategoryValue.RENTAL)).findFirst().orElseThrow();
        assertThat(saved.getRentalDays()).isEqualTo(3); assertThat(invoice.getTotal()).isEqualTo(saved.getEstimatedTotal());
    }

    @Test void portalScopesSameNameCustomersByIdentityAndRejectsForeignCancellation() throws Exception {
        UUID ownRental=rental(); customer.setPortalUsername("renter");
        FleetCustomer other=customers.save(FleetCustomer.builder().customerNumber("OTHER").fullName(customer.getFullName()).email("other@example.test").phone("12345678").licenseNumber("OTHER").licenseExpiry("2031-12-31").identityStatus(CustomerIdentityStatusValue.VERIFIED).status(CustomerStatusValue.ACTIVE).build());
        var foreignRequest=request("2027-02-01T10:00","2027-02-03T10:00"); foreignRequest.setCustomerId(other.getId().toString());
        UUID foreign=(UUID)fleet.createBooking(foreignRequest).get("id");fleet.confirmBooking(foreign);
        UUID foreignRental=(UUID)fleet.createRentalFromBooking(new FleetDtos.RentalCreateRequest(foreign.toString(),vehicle.getId().toString(),100,"Full",150,List.of(),"")).get("id");
        em.flush();
        mvc.perform(get("/api/portal/me").with(user("renter").roles("CUSTOMER")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.bookings.length()").value(1))
                .andExpect(jsonPath("$.rentals[0].id").value(ownRental.toString()))
                .andExpect(jsonPath("$.invoices.length()").value(2));
        mvc.perform(post("/api/portal/bookings/"+foreign+"/cancel").with(user("renter").roles("CUSTOMER"))).andExpect(status().isForbidden());
        assertThat(rentals.findById(foreignRental)).isPresent();
    }

    @Test void incompleteEvidenceCannotBeCompleted() {
        var evidence=new InspectionEvidenceDTO();evidence.setAcknowledged(false);
        var request=ChecklistRequestDTO.builder().checklistNumber("INCOMPLETE").rentalStartDate(LocalDate.now()).customerName("Customer").customerPhone("12345678").staffName("inspector").rentalType("PICKUP").vehicleId(vehicle.getId()).completed(true).evidence(evidence).build();
        assertThatThrownBy(()->checklists.createChecklist(request)).hasMessageContaining("acknowledgement");
    }

    @Test void adminCannotProvisionSuperadmin() throws Exception {
        var role=em.createQuery("select r from Role r where r.name='ROLE_ADMIN'",Role.class).getSingleResult();
        em.persist(User.builder().username("test-admin").password("unused").roles(Set.of(role)).build());em.flush();
        mvc.perform(post("/api/auth/register").with(user("test-admin").roles("ADMIN")).contentType("application/json")
                .content("{\"username\":\"elevated\",\"password\":\"long-password\",\"roles\":[\"ROLE_SUPERADMIN\"]}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @org.springframework.transaction.annotation.Transactional(propagation=org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED)
    void competingAssignmentsCannotBothSucceed() throws Exception {
        UUID first=booking(),second=booking();
        var pool=java.util.concurrent.Executors.newFixedThreadPool(2);
        var start=new java.util.concurrent.CountDownLatch(1);
        try {
            java.util.concurrent.Callable<Boolean> a=()->{start.await();try{fleet.assignVehicleToBooking(first,vehicle.getId().toString());return true;}catch(IllegalStateException e){return false;}};
            java.util.concurrent.Callable<Boolean> b=()->{start.await();try{fleet.assignVehicleToBooking(second,vehicle.getId().toString());return true;}catch(IllegalStateException e){return false;}};
            var ra=pool.submit(a);var rb=pool.submit(b);start.countDown();
            assertThat(List.of(ra.get(10,java.util.concurrent.TimeUnit.SECONDS),rb.get(10,java.util.concurrent.TimeUnit.SECONDS))).containsExactlyInAnyOrder(true,false);
        } finally {
            pool.shutdownNow();bookings.deleteById(first);bookings.deleteById(second);vehicles.deleteById(vehicle.getId());customers.deleteById(customer.getId());
        }
    }
}
