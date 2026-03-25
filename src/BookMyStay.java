import java.util.*;

// Reservation (Booking Request)
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

// Inventory Service (State Holder)
class InventoryService {
    private Map<String, Integer> availability;

    public InventoryService() {
        availability = new HashMap<>();
    }

    public void addRoom(String type, int count) {
        availability.put(type, count);
    }

    public int getAvailability(String type) {
        return availability.getOrDefault(type, 0);
    }

    public void decrementRoom(String type) {
        availability.put(type, availability.get(type) - 1);
    }
}

// Booking Service (Allocation Logic)
class BookingService {
    private Queue<Reservation> requestQueue;
    private InventoryService inventory;

    // Track allocated room IDs
    private Set<String> allocatedRoomIds;

    // Map room type -> allocated room IDs
    private Map<String, Set<String>> roomAllocations;

    public BookingService(Queue<Reservation> requestQueue, InventoryService inventory) {
        this.requestQueue = requestQueue;
        this.inventory = inventory;
        this.allocatedRoomIds = new HashSet<>();
        this.roomAllocations = new HashMap<>();
    }

    // Generate unique room ID
    private String generateRoomId(String roomType) {
        String roomId;
        do {
            roomId = roomType.substring(0, 2).toUpperCase() + "-" + (int)(Math.random() * 1000);
        } while (allocatedRoomIds.contains(roomId)); // Ensure uniqueness
        return roomId;
    }

    // Process bookings (FIFO)
    public void processBookings() {
        System.out.println("\nProcessing Booking Requests...\n");

        while (!requestQueue.isEmpty()) {

            Reservation request = requestQueue.poll(); // FIFO
            String type = request.getRoomType();

            System.out.println("Processing request for " + request.getGuestName());

            // Check availability
            if (inventory.getAvailability(type) > 0) {

                // Generate unique room ID
                String roomId = generateRoomId(type);

                // Store in Set (prevent duplicates)
                allocatedRoomIds.add(roomId);

                // Map room type to IDs
                roomAllocations.putIfAbsent(type, new HashSet<>());
                roomAllocations.get(type).add(roomId);

                // Update inventory immediately
                inventory.decrementRoom(type);

                // Confirm reservation
                System.out.println("Booking CONFIRMED for " + request.getGuestName());
                System.out.println("Room Type: " + type);
                System.out.println("Allocated Room ID: " + roomId);
                System.out.println("-----------------------------");

            } else {
                System.out.println("Booking FAILED for " + request.getGuestName() + " (No availability)");
                System.out.println("-----------------------------");
            }
        }
    }

    // Display allocation summary
    public void displayAllocations() {
        System.out.println("\nFinal Room Allocations:\n");

        for (String type : roomAllocations.keySet()) {
            System.out.println("Room Type: " + type + " -> " + roomAllocations.get(type));
        }
    }
}

// Main Class
public class BookMyStay {
    public static void main(String[] args) {

        // Step 1: Create Queue (from Use Case 5)
        Queue<Reservation> queue = new LinkedList<>();
        queue.offer(new Reservation("Sriram", "Single"));
        queue.offer(new Reservation("Arun", "Suite"));
        queue.offer(new Reservation("Priya", "Single"));
        queue.offer(new Reservation("Kavya", "Suite"));
        queue.offer(new Reservation("Rahul", "Suite"));

        // Step 2: Setup Inventory
        InventoryService inventory = new InventoryService();
        inventory.addRoom("Single", 2);
        inventory.addRoom("Suite", 2);

        // Step 3: Booking Service
        BookingService bookingService = new BookingService(queue, inventory);

        // Step 4: Process Bookings
        bookingService.processBookings();

        // Step 5: Display Final Allocations
        bookingService.displayAllocations();
    }
}