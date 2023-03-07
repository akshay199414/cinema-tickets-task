package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class TicketServiceImpl implements TicketService {
    /**
     * Should only have private methods other than the one below.
     */
    private int totalPrice = 0;
    private int totalNumberOfSeats = 0;
    private final int maxTickets = 20;
    private boolean adultTicketIncluded = false;
    private final SeatReservationService seatReservationService;
    private final TicketPaymentService ticketPaymentService;

    public TicketServiceImpl(SeatReservationService seatReservationService, TicketPaymentService ticketPaymentService) {
        this.seatReservationService = seatReservationService;
        this.ticketPaymentService = ticketPaymentService;
    }

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {

        validateAccountId(accountId);
        processTicketRequests(ticketTypeRequests);
        validateTicketRequests();

        ticketPaymentService.makePayment(accountId, totalPrice);
        seatReservationService.reserveSeat(accountId, totalNumberOfSeats);

        System.out.println("Reservation Successful \nTotal Price: £" + totalPrice + "\nTotal Number of Seats " + totalNumberOfSeats);
    }


    private void validateAccountId(long accountId) throws InvalidPurchaseException {
        if (accountId < 1) {
            throw new InvalidPurchaseException();
        }
    }

    private void validateTicketRequests() throws InvalidPurchaseException {
        if (totalNumberOfSeats > maxTickets) {
            throw new InvalidPurchaseException();
        }

        if (!adultTicketIncluded) {
            throw new InvalidPurchaseException();
        }
    }

    private void processTicketRequests(TicketTypeRequest... ticketTypeRequest) {
        for (TicketTypeRequest ticketRequest : ticketTypeRequest) {
            if (ticketRequest.getTicketType() != TicketTypeRequest.Type.INFANT) {
                totalNumberOfSeats += ticketRequest.getNoOfTickets();
            }
            calculatePrice(ticketRequest);
        }
    }

    private void calculatePrice(TicketTypeRequest ticketRequest) {

        //Would generally get the prices from a config, so they can be changed without changing the code
        if (ticketRequest.getTicketType() == TicketTypeRequest.Type.ADULT) {
            int ADULT_PRICE = 20;
            totalPrice += ticketRequest.getNoOfTickets() * ADULT_PRICE;
            adultTicketIncluded = true;
        }

        if (ticketRequest.getTicketType() == TicketTypeRequest.Type.CHILD) {
            int CHILD_PRICE = 10;
            totalPrice += ticketRequest.getNoOfTickets() * CHILD_PRICE;
        }
    }

}
