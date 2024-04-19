class TestRunner {

    private static Quadtree quadtree;
    private static final int mapSize = 10_000_000; // Given by your problem statement

    TestRunner() {
        initializeQuadtree();
        testAddMaximumPlaces();
//        testSearchWithMaxBoundingRectangle();
        testSearchPerformanceWithDifferentSizes();
    }

    private static void initializeQuadtree() {
        quadtree = new Quadtree(mapSize, mapSize);
    }

    private static void testAddMaximumPlaces() {
        long startTime = System.nanoTime();
        try {
            for (int i = 0; i < 70_000_000; i++) {
                if (i % 10_000_000 == 0) { // Every 1 million inserts
                    System.gc(); // Suggest garbage collection
                    System.out.println("Added " + i + " places");
                }
                int x = (int) (Math.random() * mapSize);
                int y = (int) (Math.random() * mapSize);
                byte services = 0; // Start with no services
                int serviceId = i % 10; // Simulate up to 10 types of services
                int serviceId2 = 1;
                services |= (1 << serviceId); // Set the bit corresponding to the service
                Place p = new Place(x, y, services);
                p.addService(serviceId2);
                quadtree.add(p);
            }
            long endTime = System.nanoTime();
            System.out.println("Test Add Maximum Places: SUCCESS");
            System.out.println("Time taken to add 100 million places: " + (endTime - startTime) / 1_000_000 + " ms");
        } catch (Throwable e) {
            System.out.println("Test Add Maximum Places: FAILED");
            e.printStackTrace();
        }
    }

    private static void testSearchWithMaxBoundingRectangle() {
        long startTime = System.nanoTime();
        try {
            SimpleList results = quadtree.searchAroundMe(5_000_000, 5_000_000, 10_000_000, 1);
            long endTime = System.nanoTime();
            System.out.println("Test Search With Max Bounding Rectangle: SUCCESS");
            System.out.println("Time taken to search with max bounding rectangle: " + (endTime - startTime) / 1_000_000 + " ms");
            System.out.println("Number of results: " + results.size());
            for (int i = 0; i < results.size(); i++) {
                Place place = results.get(i);
                System.out.println("Place at (" + place.x + ", " + place.y + ") offering services: " + place.getServicesList().join(", "));
            }
        } catch (Exception e) {
            System.out.println("Test Search With Max Bounding Rectangle: FAILED");
            e.printStackTrace();
        }
    }

    private static void testSearchPerformanceWithDifferentSizes() {
        int[] sizes = {100, 1000, 10_000, 100_000};
        for (int size : sizes) {
            long startTime = System.nanoTime();
            try {
                SimpleList results = quadtree.searchAroundMe(5_000_000, 5_000_000, size, 0);
                long endTime = System.nanoTime();
                System.out.println("Test Search Performance with size " + size + ": SUCCESS");
                System.out.println("Time taken to search with bounding size " + size + ": " + (endTime - startTime) / 1_000_000 + " ms");
                System.out.println("Number of results: " + results.size());
                for (int i = 0; i < results.size(); i++) {
                    Place place = results.get(i);
                    System.out.println("Place at (" + place.x + ", " + place.y + ") offering services: " + place.getServicesList().join(", "));
                }
            } catch (Exception e) {
                System.out.println("Test Search Performance with size " + size + ": FAILED");
                e.printStackTrace();
            }
        }
    }
}