package uk.gov.dwp.uc.pairtest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TicketServiceImplTest {

    private TicketPaymentService ticketPaymentService;
    private SeatReservationService seatReservationService;

    private TicketService ticketService;

    @BeforeEach
    void setUp() {
        ticketPaymentService = Mockito.mock(TicketPaymentService.class);
        seatReservationService = Mockito.mock(SeatReservationService.class);
        ticketService = new TicketServiceImpl(seatReservationService, ticketPaymentService);
    }

    @Test
    public void testCalculateCorrectPriceAndSeats() {

        TicketTypeRequest ticketTypeRequestAdult = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);
        TicketTypeRequest ticketTypeRequestChild = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2);
        TicketTypeRequest ticketTypeRequestInfant = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 2);

        this.ticketService.purchaseTickets(12345L, ticketTypeRequestAdult, ticketTypeRequestChild, ticketTypeRequestInfant);
        verify(seatReservationService).reserveSeat(12345L, 4);
        verify(ticketPaymentService).makePayment(12345L, 60);
    }


    @Test
    public void testInvalidAccountNumber() {
        TicketTypeRequest ticketTypeRequestAdult = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);
        assertThrows(InvalidPurchaseException.class, () -> this.ticketService.purchaseTickets(0L, ticketTypeRequestAdult));
    }

    @Test
    public void testTooManyTicketsRequest() {
        TicketTypeRequest ticketTypeRequestAdult = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 21);
        assertThrows(InvalidPurchaseException.class, () -> this.ticketService.purchaseTickets(123L, ticketTypeRequestAdult));
    }

    @Test
    public void testChildWithoutAdult() {
        TicketTypeRequest ticketTypeRequestChild = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2);
        assertThrows(InvalidPurchaseException.class, () -> this.ticketService.purchaseTickets(123L, ticketTypeRequestChild));
    }

    @Test
    public void testInfantWithoutAdult() {
        TicketTypeRequest ticketTypeRequestChild = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 2);
        assertThrows(InvalidPurchaseException.class, () -> this.ticketService.purchaseTickets(123L, ticketTypeRequestChild));
    }


}