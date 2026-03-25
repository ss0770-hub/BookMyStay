import java.util.*;

// Reservation (Represents booking request)
class Reservation {
    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }

    public void display() {
        System.out.println("Guest: " + guestName + " | Requested Room: " + roomType);
    }
}

// Booking Request Queue (FIFO)
class BookingRequestQueue {
    private Queue<Reservation> queue;

    public BookingRequestQueue() {
        queue = new LinkedList<>();
    }

    // Add request to queue
    public void addRequest(Reservation reservation) {
        queue.offer(reservation);
        System.out.println("Request added for " + reservation.getGuestName());
    }

    // View all requests (Read-only)
    public void viewRequests() {
        System.out.println("\nBooking Requests in Queue (FIFO Order):\n");

        if (queue.isEmpty()) {
            System.out.println("No booking requests available.");
            return;
        }

        for (Reservation r : queue) {
            r.display();
        }
    }

    // Peek next request (without removing)
    public Reservation peekNext() {
        return queue.peek();
    }
}

// Main Class
public class BookMyStay {
    public static void main(String[] args) {

        // Step 1: Create Booking Queue
        BookingRequestQueue bookingQueue = new BookingRequestQueue();

        // Step 2: Guests submit booking requests
        bookingQueue.addRequest(new Reservation("Sriram", "Single"));
        bookingQueue.addRequest(new Reservation("Arun", "Suite"));
        bookingQueue.addRequest(new Reservation("Priya", "Double"));
        bookingQueue.addRequest(new Reservation("Kavya", "Suite"));

        // Step 3: View all queued requests (FIFO order)
        bookingQueue.viewRequests();

        // Step 4: Show next request to be processed
        Reservation next = bookingQueue.peekNext();
        if (next != null) {
            System.out.println("\nNext request to process:");
            next.display();
        }
    }
}