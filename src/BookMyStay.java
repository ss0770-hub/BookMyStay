import java.util.*;

// Add-On Service (Represents optional service)
class AddOnService {
    private String serviceName;
    private double cost;

    public AddOnService(String serviceName, double cost) {
        this.serviceName = serviceName;
        this.cost = cost;
    }

    public String getServiceName() {
        return serviceName;
    }

    public double getCost() {
        return cost;
    }

    public void display() {
        System.out.println(serviceName + " - ₹" + cost);
    }
}

// Add-On Service Manager
class AddOnServiceManager {

    // Map: Reservation ID -> List of Services
    private Map<String, List<AddOnService>> serviceMap;

    public AddOnServiceManager() {
        serviceMap = new HashMap<>();
    }

    // Add services to a reservation
    public void addService(String reservationId, AddOnService service) {
        serviceMap.putIfAbsent(reservationId, new ArrayList<>());
        serviceMap.get(reservationId).add(service);

        System.out.println("Added service '" + service.getServiceName() +
                "' to Reservation ID: " + reservationId);
    }

    // View services for a reservation
    public void viewServices(String reservationId) {
        System.out.println("\nServices for Reservation ID: " + reservationId);

        List<AddOnService> services = serviceMap.get(reservationId);

        if (services == null || services.isEmpty()) {
            System.out.println("No services added.");
            return;
        }

        for (AddOnService service : services) {
            service.display();
        }
    }

    // Calculate total cost of services
    public double calculateTotalCost(String reservationId) {
        List<AddOnService> services = serviceMap.get(reservationId);

        double total = 0;

        if (services != null) {
            for (AddOnService service : services) {
                total += service.getCost();
            }
        }

        return total;
    }
}

// Main Class
public class BookMyStay {
    public static void main(String[] args) {

        // Step 1: Create Add-On Service Manager
        AddOnServiceManager manager = new AddOnServiceManager();

        // Sample Reservation IDs (from Use Case 6)
        String res1 = "RES-101";
        String res2 = "RES-102";

        // Step 2: Create Services
        AddOnService wifi = new AddOnService("Premium WiFi", 500);
        AddOnService breakfast = new AddOnService("Breakfast", 300);
        AddOnService spa = new AddOnService("Spa Access", 1200);

        // Step 3: Guest selects services
        manager.addService(res1, wifi);
        manager.addService(res1, breakfast);
        manager.addService(res2, spa);

        // Step 4: View services
        manager.viewServices(res1);
        manager.viewServices(res2);

        // Step 5: Calculate total cost
        System.out.println("\nTotal Add-On Cost for " + res1 + ": ₹" +
                manager.calculateTotalCost(res1));

        System.out.println("Total Add-On Cost for " + res2 + ": ₹" +
                manager.calculateTotalCost(res2));
    }
}