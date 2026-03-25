import java.util.*;

// Custom Exception for Invalid Booking
class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}

// Reservation
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

    public boolean isValidRoomType(String type) {
        return availability.containsKey(type);
    }

    public int getAvailability(String type) {
        return availability.getOrDefault(type, 0);
    }

    public void decrementRoom(String type) throws InvalidBookingException {
        int current = getAvailability(type);

        if (current <= 0) {
            throw new InvalidBookingException("No rooms available for type: " + type);
        }

        availability.put(type, current - 1);
    }
}

// Validator (Fail-Fast)
class InvalidBookingValidator {
    private InventoryService inventory;

    public InvalidBookingValidator(InventoryService inventory) {
        this.inventory = inventory;
    }

    public void validate(Reservation reservation) throws InvalidBookingException {

        // Validate guest name
        if (reservation.getGuestName() == null || reservation.getGuestName().isEmpty()) {
            throw new InvalidBookingException("Guest name cannot be empty.");
        }

        // Validate room type
        if (!inventory.isValidRoomType(reservation.getRoomType())) {
            throw new InvalidBookingException("Invalid room type: " + reservation.getRoomType());
        }

        // Validate availability
        if (inventory.getAvailability(reservation.getRoomType()) <= 0) {
            throw new InvalidBookingException("Room not available for type: " + reservation.getRoomType());
        }
    }
}

// Booking Service
class BookingService {
    private InventoryService inventory;
    private InvalidBookingValidator validator;

    public BookingService(InventoryService inventory) {
        this.inventory = inventory;
        this.validator = new InvalidBookingValidator(inventory);
    }

    public void processBooking(Reservation reservation) {

        try {
            // Step 1: Validate (Fail-Fast)
            validator.validate(reservation);

            // Step 2: Allocate room (only if valid)
            inventory.decrementRoom(reservation.getRoomType());

            // Step 3: Confirm booking
            System.out.println("Booking SUCCESS for " + reservation.getGuestName() +
                    " | Room Type: " + reservation.getRoomType());

        } catch (InvalidBookingException e) {
            // Graceful failure handling
            System.out.println("Booking FAILED for " + reservation.getGuestName());
            System.out.println("Reason: " + e.getMessage());
        }

        System.out.println("-----------------------------");
    }
}

// Main Class
public class BookMyStay {
    public static void main(String[] args) {

        // Step 1: Setup Inventory
        InventoryService inventory = new InventoryService();
        inventory.addRoom("Single", 1);
        inventory.addRoom("Suite", 1);

        // Step 2: Booking Service
        BookingService bookingService = new BookingService(inventory);

        // Step 3: Test Cases

        // Valid booking
        bookingService.processBooking(new Reservation("Sriram", "Single"));

        // Invalid room type
        bookingService.processBooking(new Reservation("Arun", "Deluxe"));

        // No availability
        bookingService.processBooking(new Reservation("Priya", "Single"));

        // Empty guest name
        bookingService.processBooking(new Reservation("", "Suite"));
    }
}