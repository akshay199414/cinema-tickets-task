package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.paymentgateway.TicketPaymentServiceImpl;
import thirdparty.seatbooking.SeatReservationService;
import thirdparty.seatbooking.SeatReservationServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.util.Scanner;

public class TicketServiceExample {
    public static void main(String[] args) {

        SeatReservationService seatReservationService = new SeatReservationServiceImpl();
        TicketPaymentService ticketPaymentService = new TicketPaymentServiceImpl();
        TicketService ticketService = new TicketServiceImpl(seatReservationService, ticketPaymentService);

        Scanner scanner = new Scanner(System.in);

        System.out.println("Please enter your account ID:");
        Long accountId = scanner.nextLong();

        System.out.println("Please enter number of Adults: ");
        TicketTypeRequest ticketTypeRequestAdult = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, scanner.nextInt());

        System.out.println("Please enter number of Children: ");
        TicketTypeRequest ticketTypeRequestChild = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, scanner.nextInt());

        System.out.println("Please enter number of Infants: ");
        TicketTypeRequest ticketTypeRequestInfants = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, scanner.nextInt());

        try {
            ticketService.purchaseTickets(accountId, ticketTypeRequestAdult, ticketTypeRequestChild, ticketTypeRequestInfants);
        } catch (InvalidPurchaseException e) {
            System.out.println("Invalid purchase request");
        }
        scanner.close();
    }
}