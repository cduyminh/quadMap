class SimpleSet {
    private String[] elements;
    private int size;
    private int capacity;

    public SimpleSet() {
        capacity = 10;
        elements = new String[capacity];
        size = 0;
    }

    public void add(String element) {
        if (!contains(element)) {
            if (size == capacity) {
                increaseCapacity();
            }
            elements[size++] = element;
        }
    }

    public boolean contains(String element) {
        for (int i = 0; i < size; i++) {
            if (elements[i].equals(element)) {
                return true;
            }
        }
        return false;
    }

    private void increaseCapacity() {
        String[] newElements = new String[capacity * 2];
        System.arraycopy(elements, 0, newElements, 0, size);
        elements = newElements;
        capacity *= 2;
    }

    public int size() {
        return size;
    }

    public String get(int index) {
        if (index >= size || index < 0) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        return elements[index];
    }

    @Override
    public String toString() {
        if (size == 0) {
            return "No services";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append(elements[i]);
            if (i < size - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
}
