import java.io.*;
import java.util.*;

// Reservation (Serializable)
class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;

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
        System.out.println(reservationId + " | " + guestName + " | " + roomType);
    }
}

// Inventory Service (Serializable)
class InventoryService implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<String, Integer> availability = new HashMap<>();

    public void addRoom(String type, int count) {
        availability.put(type, count);
    }

    public Map<String, Integer> getAvailabilityMap() {
        return availability;
    }

    public void display() {
        System.out.println("\nInventory State:");
        for (String type : availability.keySet()) {
            System.out.println(type + ": " + availability.get(type));
        }
    }
}

// Booking History (Serializable)
class BookingHistory implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Reservation> reservations = new ArrayList<>();

    public void addReservation(Reservation r) {
        reservations.add(r);
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void display() {
        System.out.println("\nBooking History:");
        for (Reservation r : reservations) {
            r.display();
        }
    }
}

// Wrapper class for full system state
class SystemState implements Serializable {
    private static final long serialVersionUID = 1L;

    InventoryService inventory;
    BookingHistory history;

    public SystemState(InventoryService inventory, BookingHistory history) {
        this.inventory = inventory;
        this.history = history;
    }
}

// Persistence Service
class PersistenceService {

    private static final String FILE_NAME = "system_state.dat";

    // Save state
    public void save(SystemState state) {
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {

            oos.writeObject(state);
            System.out.println("\nSystem state saved successfully.");

        } catch (IOException e) {
            System.out.println("Error saving system state: " + e.getMessage());
        }
    }

    // Load state
    public SystemState load() {
        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(FILE_NAME))) {

            SystemState state = (SystemState) ois.readObject();
            System.out.println("\nSystem state loaded successfully.");
            return state;

        } catch (FileNotFoundException e) {
            System.out.println("\nNo previous data found. Starting fresh.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("\nError loading data. Starting with safe defaults.");
        }
        return null;
    }
}

// Main Class
public class BookMyStay {
    public static void main(String[] args) {

        PersistenceService persistenceService = new PersistenceService();

        // Step 1: Try loading previous state
        SystemState loadedState = persistenceService.load();

        InventoryService inventory;
        BookingHistory history;

        if (loadedState != null) {
            inventory = loadedState.inventory;
            history = loadedState.history;
        } else {
            // Initialize fresh system
            inventory = new InventoryService();
            inventory.addRoom("Single", 2);
            inventory.addRoom("Suite", 1);

            history = new BookingHistory();

            // Add sample bookings
            history.addReservation(new Reservation("RES-101", "Sriram", "Single"));
            history.addReservation(new Reservation("RES-102", "Arun", "Suite"));
        }

        // Step 2: Display current state
        inventory.display();
        history.display();

        // Step 3: Simulate new booking (after restart)
        history.addReservation(new Reservation("RES-103", "Priya", "Single"));
        inventory.getAvailabilityMap().put("Single",
                inventory.getAvailabilityMap().get("Single") - 1);

        System.out.println("\nAfter new booking:");
        inventory.display();
        history.display();

        // Step 4: Save state before shutdown
        SystemState currentState = new SystemState(inventory, history);
        persistenceService.save(currentState);
    }
}