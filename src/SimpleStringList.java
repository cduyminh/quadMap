class SimpleStringList {
    private String[] items;
    private int size;
    private int capacity;

    public SimpleStringList() {
        capacity = 10; // initial capacity
        items = new String[capacity];
        size = 0;
    }

    public void add(String item) {
        if (size == capacity) {
            increaseCapacity();
        }
        items[size++] = item;
    }

    private void increaseCapacity() {
        String[] newItems = new String[capacity * 2];
        System.arraycopy(items, 0, newItems, 0, size);
        items = newItems;
        capacity *= 2;
    }

    public int size() {
        return size;
    }

    public String get(int index) {
        if (index >= size || index < 0) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        return items[index];
    }

    // For ease of use, implementing a simple join method
    public String join(String delimiter) {
        if (size == 0) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append(items[i]);
            if (i < size - 1) {
                sb.append(delimiter);
            }
        }
        return sb.toString();
    }
}
