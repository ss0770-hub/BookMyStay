import java.util.*;

// Reservation (Confirmed Booking)
class Reservation {
    private String reservationId;
    private String guestName;
    private String roomType;
    private String roomId;
    private boolean isCancelled;

    public Reservation(String reservationId, String guestName, String roomType, String roomId) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
        this.roomId = roomId;
        this.isCancelled = false;
    }

    public String getReservationId() {
        return reservationId;
    }

    public String getRoomType() {
        return roomType;
    }

    public String getRoomId() {
        return roomId;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public void cancel() {
        isCancelled = true;
    }

    public void display() {
        System.out.println("Reservation ID: " + reservationId +
                " | Guest: " + guestName +
                " | Room: " + roomType +
                " | Room ID: " + roomId +
                " | Status: " + (isCancelled ? "CANCELLED" : "CONFIRMED"));
    }
}

// Inventory Service
class InventoryService {
    private Map<String, Integer> availability;

    public InventoryService() {
        availability = new HashMap<>();
    }

    public void addRoom(String type, int count) {
        availability.put(type, count);
    }

    public void incrementRoom(String type) {
        availability.put(type, availability.getOrDefault(type, 0) + 1);
    }

    public int getAvailability(String type) {
        return availability.getOrDefault(type, 0);
    }
}

// Booking History
class BookingHistory {
    private Map<String, Reservation> reservations;

    public BookingHistory() {
        reservations = new HashMap<>();
    }

    public void addReservation(Reservation r) {
        reservations.put(r.getReservationId(), r);
    }

    public Reservation getReservation(String id) {
        return reservations.get(id);
    }

    public void displayAll() {
        System.out.println("\n--- Booking Records ---\n");
        for (Reservation r : reservations.values()) {
            r.display();
        }
    }
}

// Cancellation Service
class CancellationService {

    private BookingHistory history;
    private InventoryService inventory;

    // Stack for rollback tracking (LIFO)
    private Stack<String> rollbackStack;

    public CancellationService(BookingHistory history, InventoryService inventory) {
        this.history = history;
        this.inventory = inventory;
        this.rollbackStack = new Stack<>();
    }

    public void cancelBooking(String reservationId) {

        System.out.println("\nProcessing cancellation for: " + reservationId);

        Reservation r = history.getReservation(reservationId);

        // Validation
        if (r == null) {
            System.out.println("Cancellation FAILED: Reservation does not exist.");
            return;
        }

        if (r.isCancelled()) {
            System.out.println("Cancellation FAILED: Already cancelled.");
            return;
        }

        // Step 1: Push room ID to stack (LIFO rollback)
        rollbackStack.push(r.getRoomId());

        // Step 2: Restore inventory
        inventory.incrementRoom(r.getRoomType());

        // Step 3: Mark as cancelled
        r.cancel();

        System.out.println("Cancellation SUCCESS for Reservation ID: " + reservationId);
        System.out.println("Released Room ID: " + r.getRoomId());
    }

    public void showRollbackStack() {
        System.out.println("\nRollback Stack (Recent Releases First): " + rollbackStack);
    }
}

// Main Class
public class BookMyStay {
    public static void main(String[] args) {

        // Step 1: Setup Inventory
        InventoryService inventory = new InventoryService();
        inventory.addRoom("Single", 1);
        inventory.addRoom("Suite", 0);

        // Step 2: Setup Booking History
        BookingHistory history = new BookingHistory();

        history.addReservation(new Reservation("RES-101", "Sriram", "Single", "SI-101"));
        history.addReservation(new Reservation("RES-102", "Arun", "Suite", "SU-201"));

        // Step 3: Cancellation Service
        CancellationService service = new CancellationService(history, inventory);

        // Step 4: Perform cancellations
        service.cancelBooking("RES-101"); // valid
        service.cancelBooking("RES-101"); // already cancelled
        service.cancelBooking("RES-999"); // invalid

        // Step 5: Show rollback stack
        service.showRollbackStack();

        // Step 6: Display final state
        history.displayAll();

        System.out.println("\nUpdated Inventory:");
        System.out.println("Single Rooms: " + inventory.getAvailability("Single"));
        System.out.println("Suite Rooms: " + inventory.getAvailability("Suite"));
    }
}