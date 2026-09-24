package utils.CustomList;

import javafx.stage.Stage;

/**
 * Generic type 'Funk' can store any object type
 * <p>
 * node 'head' pointer to first node in list
 * node 'tail' pointer to last node in list
 * size is the number of nodes in the linked list
 * all nodes contain a value and reference next/prev node.
 *
 * @param <Funk> element type in this list
 */
public class FunkierList<Funk> implements java.io.Serializable {
    public Node<Funk> head;
    public Node<Funk> tail;
    private int size = 0;


    public int getSize() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }

    /**
     * Add element to start
     *
     * @param val element to add
     */
    public void addAtFirst(Funk val) {
        Node<Funk> newNode = new Node<>(val);
        if (head == null) {
            head = tail = newNode;
        } else {
            newNode.next = head;
            head.prev = newNode;
            head = newNode;
        }
        size++;
    }

    /**
     * Adds element at end
     *
     * @param val element to add
     */
    public void addAtLast(Funk val) {
        if (tail == null) {
            addAtFirst(val);
            return;
        }
        Node<Funk> newNode = new Node<>(val, tail, null);
        tail.next = newNode;
        tail = newNode;
        size++;
    }

    /**
     * Add all elements from other list
     *
     * @param otherList list to add from
     */
    public void addAll(FunkierList<Funk> otherList) {
        if (otherList == null || otherList.isEmpty()) {
            return;
        }

        Node<Funk> current = otherList.head;
        while (current != null) {
            if (current.value != null) {
                this.addAtLast(current.value);
            }
            current = current.next;
        }
    }

    /**
     * Gets element at given index
     *
     * @param index position of element
     * @return element at index, or null if invalid index
     */
    public Funk get(int index) {
        Node<Funk> node = getNode(index);
        return node != null ? node.value : null;
    }

    /**
     * Helper to get node at index
     *
     * @param index position of node
     * @return node at index, or null if index is invalid
     */
    private Node<Funk> getNode(int index) {
        if (index < 0 || index >= size) {
            return null;
        }

        if (index < size / 2) {
            Node<Funk> current = head;
            for (int i = 0; i < index; i++) {
                current = current.next;
            }
            return current;
        } else {
            Node<Funk> current = tail;
            for (int i = size - 1; i > index; i--) {
                current = current.prev;
            }
            return current;
        }
    }

    /**
     * Get the index of the first hit on given element
     *
     * @param val element to search for
     * @return index of element, or -1 if not found
     */
    public int indexOf(Funk val) {
        Node<Funk> current = head;
        int index = 0;
        while (current != null) {
            if ((val == null && current.value == null) || (val != null && val.equals(current.value))) {
                return index;
            }
            current = current.next;
            index++;
        }
        return -1;
    }

    /**
     * Checks if the list contains a specific element
     *
     * @param val element to search for
     * @return true if element in list, otherwise false
     */
    public boolean contains(Funk val) {
        return indexOf(val) != -1;
    }

    /**
     * Remove first element and return it
     *
     * @return first element, or null if empty
     */
    public Funk removeFirst() {
        if (head == null) return null;

        Funk value = head.value;
        head = head.next;
        if (head != null) {
            head.prev = null;
        } else {
            tail = null;
        }
        size--;
        return value;
    }

    /**
     * Remove last element and return it
     *
     * @return last element, or null if empty
     */
    public Funk removeLast() {
        if (tail == null) return null;

        Funk value = tail.value;
        tail = tail.prev;
        if (tail != null) {
            tail.next = null;
        } else {
            head = null;
        }
        size--;
        return value;
    }

    /**
     * Removes first hit of given value
     *
     * @param value element to remove
     * @return true if element was removed, false if not
     */
    public boolean removeFirstHit(Funk value) {
        if (head == null) return false;

        Node<Funk> current = head;
        while (current != null) {
            if ((value == null && current.value == null) || (value != null && value.equals(current.value))) {
                if (current == head) {
                    removeFirst();
                } else if (current == tail) {
                    removeLast();
                } else {
                    current.prev.next = current.next;
                    current.next.prev = current.prev;
                    size--;
                }
                return true;
            }
            current = current.next;
        }
        return false;
    }

    /**
     * Iterate through entire list from first - last, execute action for non-null elements.
     *
     * @param action action to perform
     */
    public void forEach(Action<Funk> action) {
        Node<Funk> current = head;
        while (current != null) {
            if (current.value != null) {
                action.perform(current.value);
            }
            current = current.next;
        }
    }

    /**
     * Find first element matching the condition
     *
     * @param condition search condition
     * @return first matches element, or null if none
     */
    public Funk find(SearchCondition<Funk> condition) {
        Node<Funk> current = head;
        while (current != null) {
            if (condition.matches(current.value)) {
                return current.value;
            }
            current = current.next;
        }
        return null;
    }

    /**
     * Find all elements matching the condition
     *
     * @param condition search condition
     * @return list containing all matching elements
     */
    public FunkierList<Funk> findAll(SearchCondition<Funk> condition) {
        FunkierList<Funk> results = new FunkierList<>();
        Node<Funk> current = head;
        while (current != null) {
            if (condition.matches(current.value)) {
                results.addAtLast(current.value);
            }
            current = current.next;
        }
        return results;
    }

    /**
     * Check if element in list satisfies given condition.
     *
     * @param condition search condition to test against
     * @return true if matches, false otherwise
     */
    public boolean anyMatch(SearchCondition<Funk> condition) {
        return find(condition) != null;
    }

    /**
     * Find max element based on compare function
     *
     * @param compareFunction comparison function
     * @return maximum element, or null if list is empty
     */
    public Funk max(CompareFunction<Funk> compareFunction) {
        if (head == null) return null;

        Funk max = head.value;
        Node<Funk> current = head.next;

        while (current != null) {
            if (compareFunction.compare(current.value, max) > 0) {
                max = current.value;
            }
            current = current.next;
        }
        return max;
    }

    /**
     * Sums values extracted from elements
     *
     * @param extractor function to extract num values
     * @return sum of extract values
     */
    public double sumValues(ValueExtractor<Funk> extractor) {
        double total = 0.0;
        for (Node<Funk> current = head; current != null; current = current.next) {
            if (current.value != null) {
                total += extractor.extractValue(current.value);
            }
        }
        return total;
    }
    /**
     * Sums integer values extracted from elements
     *
     * @param extractor function to extract integer values
     * @return sum of extracted integer values
     */

    public int sumIntValues(ValueExtractor<Funk> extractor) {
        int total = 0;
        for (Node<Funk> current = head; current != null; current = current.next) {
            if (current.value != null) {
                total += (int) extractor.extractValue(current.value);
            }
        }
        return total;
    }

    /**
     * FUNCTIONAL INTERFACES - passes behaviour parameters
     */

    /**
     * Take 2 parameters, compare them and return an int.
     *
     * @param <Funk> type of elements to compare
     */
    public interface CompareFunction<Funk> {
        int compare(Funk a, Funk b);
    }

    /**
     * Take 1 parameter, extract double value from element.
     * @param <Funk> element type
     */
    public interface ValueExtractor<Funk> {
        double extractValue(Funk item);
    }

    /**
     * Take 1 parameter, perform a given action and return void.
     *
     * @param <Funk> type element to act on
     */
    public interface Action<Funk> {
        void perform(Funk item);
    }

    /**
     * Node class for doubly linked list
     *
     * @param <Funk> type of value stored in node
     */
    public static class Node<Funk> {
        public Node<Funk> next;
        public Node<Funk> prev;
        public Funk value;

        public Node(Funk value) {
            this.value = value;
            this.next = null;
            this.prev = null;
        }

        public Node(Funk value, Node<Funk> prev, Node<Funk> next) {
            this.value = value;
            this.prev = prev;
            this.next = next;
        }
    }

    public interface StringMapper<Funk> {
        String mapToString(Funk item);
    }
    public interface MapViewOpener {
        void openMapView(Stage currentStage);
    }


}