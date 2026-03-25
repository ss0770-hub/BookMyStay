import java.util.*;

// Reservation (Confirmed Booking)
class Reservation {
    private String reservationId;
    private String guestName;
    private String roomType;

    public Reservation(String reservationId, String guestName, String roomType) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getReservationId() {
        return reservationId;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }

    public void display() {
        System.out.println("Reservation ID: " + reservationId +
                " | Guest: " + guestName +
                " | Room Type: " + roomType);
    }
}

// Booking History (State Holder)
class BookingHistory {
    private List<Reservation> history;

    public BookingHistory() {
        history = new ArrayList<>();
    }

    // Add confirmed booking
    public void addReservation(Reservation reservation) {
        history.add(reservation);
    }

    // Read-only access
    public List<Reservation> getAllReservations() {
        return history;
    }
}

// Booking Report Service
class BookingReportService {
    private BookingHistory bookingHistory;

    public BookingReportService(BookingHistory bookingHistory) {
        this.bookingHistory = bookingHistory;
    }

    // Display all bookings
    public void showAllBookings() {
        System.out.println("\n--- Booking History ---\n");

        List<Reservation> list = bookingHistory.getAllReservations();

        if (list.isEmpty()) {
            System.out.println("No bookings available.");
            return;
        }

        for (Reservation r : list) {
            r.display();
        }
    }

    // Generate summary report
    public void generateSummaryReport() {
        System.out.println("\n--- Booking Summary Report ---\n");

        List<Reservation> list = bookingHistory.getAllReservations();

        Map<String, Integer> roomTypeCount = new HashMap<>();

        for (Reservation r : list) {
            roomTypeCount.put(
                    r.getRoomType(),
                    roomTypeCount.getOrDefault(r.getRoomType(), 0) + 1
            );
        }

        for (String type : roomTypeCount.keySet()) {
            System.out.println("Room Type: " + type +
                    " | Total Bookings: " + roomTypeCount.get(type));
        }

        System.out.println("\nTotal Reservations: " + list.size());
    }
}

// Main Class
public class BookMyStay {
    public static void main(String[] args) {

        // Step 1: Create Booking History
        BookingHistory history = new BookingHistory();

        // Step 2: Add confirmed bookings (from Use Case 6)
        history.addReservation(new Reservation("RES-101", "Sriram", "Single"));
        history.addReservation(new Reservation("RES-102", "Arun", "Suite"));
        history.addReservation(new Reservation("RES-103", "Priya", "Single"));
        history.addReservation(new Reservation("RES-104", "Kavya", "Suite"));

        // Step 3: Create Report Service
        BookingReportService reportService = new BookingReportService(history);

        // Step 4: Admin views booking history
        reportService.showAllBookings();

        // Step 5: Generate summary report
        reportService.generateSummaryReport();
    }
}