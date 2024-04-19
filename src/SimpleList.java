class SimpleList {
    private Place[] items;
    private int size;
    private int capacity;

    public SimpleList() {
        capacity = 10;
        items = new Place[capacity];
        size = 0;
    }

    public void add(Place item) {
        if (size == capacity) {
            increaseCapacity();
        }
        items[size++] = item;
    }

    public void remove(int index) {
        if (index >= size || index < 0) {
            throw new IndexOutOfBoundsException("Index out of bounds.");
        }
        for (int i = index; i < size - 1; i++) {
            items[i] = items[i + 1];
        }
        size--;
    }

    private void increaseCapacity() {
        Place[] newItems = new Place[capacity * 2];
        System.arraycopy(items, 0, newItems, 0, size);
        items = newItems;
        capacity *= 2;
    }

    public int size() {
        return size;
    }

    public Place get(int index) {
        if (index >= size || index < 0) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        return items[index];
    }
}
