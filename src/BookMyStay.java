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

// Inventory Service (Shared Resource)
class InventoryService {
    private Map<String, Integer> availability;

    public InventoryService() {
        availability = new HashMap<>();
    }

    public synchronized boolean allocateRoom(String type) {
        int count = availability.getOrDefault(type, 0);

        if (count > 0) {
            availability.put(type, count - 1);
            return true;
        }
        return false;
    }

    public void addRoom(String type, int count) {
        availability.put(type, count);
    }

    public void displayInventory() {
        System.out.println("\nFinal Inventory:");
        for (String type : availability.keySet()) {
            System.out.println(type + ": " + availability.get(type));
        }
    }
}

// Booking Queue (Shared)
class BookingQueue {
    private Queue<Reservation> queue = new LinkedList<>();

    public synchronized void addRequest(Reservation r) {
        queue.offer(r);
    }

    public synchronized Reservation getRequest() {
        return queue.poll();
    }
}

// Booking Processor (Thread)
class BookingProcessor extends Thread {

    private BookingQueue queue;
    private InventoryService inventory;

    public BookingProcessor(BookingQueue queue, InventoryService inventory, String name) {
        super(name);
        this.queue = queue;
        this.inventory = inventory;
    }

    public void run() {
        while (true) {

            Reservation request;

            // Critical section: fetch request
            synchronized (queue) {
                request = queue.getRequest();
            }

            if (request == null) break;

            // Critical section: allocation
            boolean success;
            synchronized (inventory) {
                success = inventory.allocateRoom(request.getRoomType());
            }

            if (success) {
                System.out.println(Thread.currentThread().getName() +
                        " CONFIRMED booking for " + request.getGuestName() +
                        " (" + request.getRoomType() + ")");
            } else {
                System.out.println(Thread.currentThread().getName() +
                        " FAILED booking for " + request.getGuestName() +
                        " (" + request.getRoomType() + ")");
            }
        }
    }
}

// Main Class
public class BookMyStay {
    public static void main(String[] args) {

        // Step 1: Shared Inventory
        InventoryService inventory = new InventoryService();
        inventory.addRoom("Single", 2);
        inventory.addRoom("Suite", 1);

        // Step 2: Shared Booking Queue
        BookingQueue queue = new BookingQueue();

        // Simulate multiple guest requests
        queue.addRequest(new Reservation("Sriram", "Single"));
        queue.addRequest(new Reservation("Arun", "Suite"));
        queue.addRequest(new Reservation("Priya", "Single"));
        queue.addRequest(new Reservation("Kavya", "Suite"));
        queue.addRequest(new Reservation("Rahul", "Single"));

        // Step 3: Multiple Threads (Concurrent Processing)
        BookingProcessor t1 = new BookingProcessor(queue, inventory, "Thread-1");
        BookingProcessor t2 = new BookingProcessor(queue, inventory, "Thread-2");
        BookingProcessor t3 = new BookingProcessor(queue, inventory, "Thread-3");

        // Start threads
        t1.start();
        t2.start();
        t3.start();

        // Wait for threads to complete
        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Step 4: Final Inventory State
        inventory.displayInventory();
    }
}