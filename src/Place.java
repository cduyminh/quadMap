class Place {
    int x, y; // Coordinates
    byte services; // Services offered, represented as a bitmask

    // Constructor
    Place(int x, int y, byte services) {
        this.x = x;
        this.y = y;
        this.services = services;
    }

    // Method to add a service using service ID
    void addService(int serviceId) {
        services |= (1 << serviceId);
    }

    // Method to check if a service is offered
    boolean offersService(int serviceId) {
        return (services & (1 << serviceId)) != 0;
    }

    SimpleStringList getServicesList() {
        SimpleStringList serviceNames = new SimpleStringList();
        String[] serviceMapping = {
                "ATMs", "Restaurants", "Hospitals", "Gas Stations",
                "Pharmacies", "Coffee Shops", "Supermarkets", "Parks",
                "Hotels", "Banks"  // Define up to 10 services
        };
        for (int i = 0; i < serviceMapping.length; i++) {
            if ((services & (1 << i)) != 0) {
                serviceNames.add(serviceMapping[i]);
            }
        }
        return serviceNames;
    }
}
