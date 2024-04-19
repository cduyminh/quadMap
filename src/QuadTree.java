class Quadtree {
    private Node root;

    class Node {
        int x, y; // Coordinates of the center
        int width, height; // Dimensions of the node
        SimpleList places; // Points in this node
        Node[] children; // Child quadrants

        public Node(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.places = new SimpleList();
            this.children = null; // initially, there are no children
        }

        boolean isLeaf() {
            return children == null;
        }
    }

    public Quadtree(int width, int height) {
        this.root = new Node(0, 0, width, height);
    }

    public void add(Place place) {
        addRecursive(root, place);
    }

    private void addRecursive(Node node, Place place) {
        // Check if the node is a leaf
        if (!node.isLeaf()) {
            // Determine the correct quadrant for the place
            int index = getIndexForPlace(node, place.x, place.y);
            if (node.children[index] == null) {
                createChildNode(node, index);
            }
            addRecursive(node.children[index], place);
            return;
        }

        // Add place to the node
        node.places.add(place);

        // Check if the node needs to split
//        if (node.places.size() > 4) { // Assuming max 4 places per node before splitting
//            splitNode(node);
//        }
    }

    private void splitNode(Node node) {
        node.children = new Node[4];
        for (int i = 0; i < 4; i++) {
            createChildNode(node, i);
        }

        // Redistribute places into the new children
        while (node.places.size() > 0) {
            Place p = node.places.get(0);
            node.places.add(p); // Remove the first place and re-add it to ensure it goes to a child
            addRecursive(node, p);
            node.places.remove(0); // Ensure it is removed after redistribution
        }
    }

    private int getIndexForPlace(Node node, int x, int y) {
        boolean isTop = y < node.y + node.height / 2;
        boolean isLeft = x < node.x + node.width / 2;
        if (isTop && isLeft) return 0;
        if (isTop) return 1;
        if (isLeft) return 2;
        return 3;
    }

    private void createChildNode(Node parent, int index) {
        int halfWidth = parent.width / 2;
        int halfHeight = parent.height / 2;
        int offsetX = (index % 2 == 0) ? 0 : halfWidth;
        int offsetY = (index < 2) ? 0 : halfHeight;
        parent.children[index] = new Node(parent.x + offsetX, parent.y + offsetY, halfWidth, halfHeight);
    }


    public void edit(int x, int y, byte newServices) {
        Place place = findPlace(root, x, y);
        if (place != null) {
            place.services = newServices;
        }
    }

    private Place findPlace(Node node, int x, int y) {
        if (!node.isLeaf()) {
            int index = getIndexForPlace(node, x, y);
            if (node.children[index] != null) {
                return findPlace(node.children[index], x, y);
            }
            return null;
        }

        for (int i = 0; i < node.places.size(); i++) {
            Place place = node.places.get(i);
            if (place.x == x && place.y == y) {
                return place;
            }
        }
        return null;
    }

    private boolean isEmpty(Node node) {
        if (!node.isLeaf()) {
            for (int i = 0; i < 4; i++) {
                if (node.children[i] != null && !isEmpty(node.children[i])) {
                    return false;
                }
            }
        }
        return node.places.size() == 0;
    }

    private void mergeChildren(Node node) {
        // Only attempt to merge if all children are present and are leaves
        if (node.children == null || node.children.length != 4) {
            return;  // No children to merge
        }

        // Check if all children are leaves and count total places
        int totalPlaces = 0;
        for (Node child : node.children) {
            if (child == null || !child.isLeaf()) {
                return;  // Cannot merge if any child is not a leaf
            }
            totalPlaces += child.places.size();
        }

        // Only merge if total places are less than a certain threshold to avoid excessive merging
        if (totalPlaces <= 4) { // Arbitrary threshold: adjust based on performance needs
            node.places = new SimpleList();
            for (Node child : node.children) {
                for (int i = 0; i < child.places.size(); i++) {
                    node.places.add(child.places.get(i));
                }
            }
            // After merging, children should be discarded
            node.children = null;
        }
    }

    public void remove(int x, int y) {
        removeRecursive(root, x, y);
    }

    private boolean removeRecursive(Node node, int x, int y) {
        if (!node.isLeaf()) {
            int index = getIndexForPlace(node, x, y);
            if (node.children[index] != null) {
                boolean removed = removeRecursive(node.children[index], x, y);
                if (removed && isEmpty(node)) { // Check if child nodes are empty to possibly merge
                    mergeChildren(node);
                }
                return removed;
            }
            return false;
        }

        for (int i = 0; i < node.places.size(); i++) {
            if (node.places.get(i).x == x && node.places.get(i).y == y) {
                node.places.remove(i);
                return true;
            }
        }
        return false;
    }

    public SimpleList searchAroundMe(int centerX, int centerY, int distance, int serviceId) {
        int topLeftX = centerX - distance;
        int topLeftY = centerY - distance;
        if (topLeftX < 0)
            topLeftX = 0;
        if (topLeftY < 0)
            topLeftY = 0;
        int widthHeight = distance * 2; // Width and height of the bounding box
        return search(topLeftX, topLeftY, widthHeight, widthHeight, serviceId);
    }

    public SimpleList search(int x, int y, int width, int height, int serviceId) {
        SimpleList results = new SimpleList();
        searchRecursive(root, x, y, width, height, serviceId, results);
        return results;
    }

    private void searchRecursive(Node node, int searchX, int searchY, int searchWidth, int searchHeight, int serviceId, SimpleList results) {
        if (!intersects(node, searchX, searchY, searchWidth, searchHeight)) {
            return; // No intersection with the search area
        }

        if (!node.isLeaf()) {
            for (int i = 0; i < 4; i++) {
                if (node.children[i] != null) {
                    searchRecursive(node.children[i], searchX, searchY, searchWidth, searchHeight, serviceId, results);
                }
            }
            return;
        }

        // Check each place in this leaf
        byte serviceBit = (byte) (1 << serviceId); // Calculate bitmask for the requested service
        for (int i = 0; i < node.places.size(); i++) {
            Place place = node.places.get(i);
            if (place.x >= searchX && place.x <= searchX + searchWidth && place.y >= searchY && place.y <= searchY + searchHeight) {
                if ((place.services & serviceBit) != 0) { // Check if the place offers the service
                    results.add(place);
                }
            }
        }
    }

    private boolean intersects(Node node, int searchX, int searchY, int searchWidth, int searchHeight) {
        return !(node.x + node.width < searchX || node.x > searchX + searchWidth ||
                node.y + node.height < searchY || node.y > searchY + searchHeight);
    }
}
