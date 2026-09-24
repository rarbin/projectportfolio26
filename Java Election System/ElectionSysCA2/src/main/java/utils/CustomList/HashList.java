package utils.CustomList;

import java.io.Serializable;

public class HashList<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * HYBRID DATA STRUCTURE:
     * 1. Linked List: For sequential operations, sorting, iteration
     * 2. Hash Table: For O(1) lookups by hash key
     */

    // Linked List Node
    private static class ListNode<T> {
        int key;           // Hash key for this entry
        T value;          // The actual data
        ListNode<T> next; // Next in linked list (for sequential operations)
        ListNode<T> prev; // Previous in linked list

        // Hash table links
        ListNode<T> hashNext; // Next in hash collision chain

        ListNode(int key, T value) {
            this.key = key;
            this.value = value;
            this.next = null;
            this.prev = null;
            this.hashNext = null;
        }
    }

    // Linked List properties
    private ListNode<T> listHead;
    private ListNode<T> listTail;
    private int listSize = 0;

    // Hash Table properties
    private ListNode<T>[] hashTable;
    private int hashTableSize;
    private static final int DEFAULT_HASH_SIZE = 101; // Prime number
    private static final float LOAD_FACTOR = 0.75f;

    // Interfaces
    public interface SearchCondition<T> {
        boolean matches(T item);
    }

    public interface CompareFunction<T> {
        int compare(T a, T b);
    }

    public interface Action<T> {
        void perform(T item);
    }

    public interface StringMapper<T> {
        String mapToString(T item);
    }

    @SuppressWarnings("unchecked")
    public HashList() {
        this(DEFAULT_HASH_SIZE);
    }

    @SuppressWarnings("unchecked")
    public HashList(int hashSize) {
        this.hashTableSize = Math.max(16, hashSize);
        this.hashTable = new ListNode[hashTableSize];
    }

    /**
     * HASH TABLE OPERATIONS
     */

    // index
    private int hashIndex(int key) {
        return Math.abs(key) % hashTableSize;
    }

    // add to table
    private void addToHashTable(int key, ListNode<T> node) {
        int index = hashIndex(key);
        node.hashNext = hashTable[index];
        hashTable[index] = node;
    }

    // Remove from hash table
    private boolean removeFromHashTable(int key, ListNode<T> node) {
        int index = hashIndex(key);
        ListNode<T> current = hashTable[index];
        ListNode<T> previous = null;

        while (current != null) {
            if (current == node) {
                if (previous == null) {
                    hashTable[index] = current.hashNext;
                } else {
                    previous.hashNext = current.hashNext;
                }
                return true;
            }
            previous = current;
            current = current.hashNext;
        }
        return false;
    }

    // Find in hash table by key
    private ListNode<T> findInHashTable(int key) {
        int index = hashIndex(key);
        ListNode<T> current = hashTable[index];

        while (current != null) {
            if (current.key == key) {
                return current;
            }
            current = current.hashNext;
        }
        return null;
    }

    // Find in hash table by key with condition
    private ListNode<T> findInHashTable(int key, SearchCondition<T> condition) {
        int index = hashIndex(key);
        ListNode<T> current = hashTable[index];

        while (current != null) {
            if (current.key == key && (condition == null || condition.matches(current.value))) {
                return current;
            }
            current = current.hashNext;
        }
        return null;
    }

    /**
     * LINKED LIST OPERATIONS
     */

    // Add to end of linked list
    private void addToList(ListNode<T> node) {
        if (listHead == null) {
            listHead = listTail = node;
        } else {
            node.prev = listTail;
            listTail.next = node;
            listTail = node;
        }
        listSize++;
    }

    // Remove from linked list
    private void removeFromList(ListNode<T> node) {
        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            listHead = node.next;
        }

        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            listTail = node.prev;
        }

        node.next = null;
        node.prev = null;
        listSize--;
    }

    // Get node at index
    private ListNode<T> getListNode(int index) {
        if (index < 0 || index >= listSize) return null;

        ListNode<T> current;
        if (index < listSize / 2) {
            // Start from head
            current = listHead;
            for (int i = 0; i < index; i++) {
                current = current.next;
            }
        } else {
            // Start from tail
            current = listTail;
            for (int i = listSize - 1; i > index; i--) {
                current = current.prev;
            }
        }
        return current;
    }

    /**
     * PUBLIC API - COMBINING BOTH STRUCTURES
     */

    /**
     * Add with specific hash key (O(1) average)
     */
    public void add(int hashKey, T value) {
        if (value == null) return;

        // Check if key already exists
        ListNode<T> existing = findInHashTable(hashKey);
        if (existing != null) {
            // Update existing value
            existing.value = value;
            return;
        }

        // Create new node
        ListNode<T> newNode = new ListNode<>(hashKey, value);

        // Add to both structures
        addToList(newNode);
        addToHashTable(hashKey, newNode);

        // Check if resize needed
        if ((float)listSize / hashTableSize > LOAD_FACTOR) {
            resizeHashTable();
        }
    }

    /**
     * Add using object's hashCode (O(1) average)
     */
    public void addAtLast(T value) {
        if (value == null) return;
        add(value.hashCode(), value);
    }

    /**
     * Get by hash key (O(1) average)
     */
    public T get(int hashKey) {
        ListNode<T> node = findInHashTable(hashKey);
        return node != null ? node.value : null;
    }

    /**
     * Get by hash key with condition (O(1) average)
     */
    public T get(int hashKey, SearchCondition<T> condition) {
        ListNode<T> node = findInHashTable(hashKey, condition);
        return node != null ? node.value : null;
    }

    /**
     * Get by index (O(n) worst case, O(1) for head/tail)
     */
    public T getByIndex(int index) {
        ListNode<T> node = getListNode(index);
        return node != null ? node.value : null;
    }

    /**
     * Remove by hash key (O(1) average)
     */
    public boolean removeByKey(int hashKey) {
        ListNode<T> node = findInHashTable(hashKey);
        if (node == null) return false;

        removeFromList(node);
        removeFromHashTable(hashKey, node);
        return true;
    }

    /**
     * Remove first occurrence of value (O(n))
     */
    public boolean removeFirstHit(T value) {
        if (value == null) return false;

        ListNode<T> current = listHead;
        while (current != null) {
            if ((value == null && current.value == null) ||
                    (value != null && value.equals(current.value))) {
                removeFromList(current);
                removeFromHashTable(current.key, current);
                return true;
            }
            current = current.next;
        }
        return false;
    }

    /**
     * Remove first element (O(1))
     */
    public T removeFirst() {
        if (listHead == null) return null;

        T value = listHead.value;
        removeFromHashTable(listHead.key, listHead);
        removeFromList(listHead);
        return value;
    }

    /**
     * Remove last element (O(1))
     */
    public T removeLast() {
        if (listTail == null) return null;

        T value = listTail.value;
        removeFromHashTable(listTail.key, listTail);
        removeFromList(listTail);
        return value;
    }

    /**
     * Resize hash table when load factor is exceeded
     */
    @SuppressWarnings("unchecked")
    private void resizeHashTable() {
        int newSize = hashTableSize * 2;
        ListNode<T>[] newTable = new ListNode[newSize];

        // Rehash all nodes
        ListNode<T> current = listHead;
        while (current != null) {
            int newIndex = Math.abs(current.key) % newSize;

            // Save next pointer before modifying
            ListNode<T> nextNode = current.hashNext;

            // Insert at head of new bucket
            current.hashNext = newTable[newIndex];
            newTable[newIndex] = current;

            current = nextNode;
        }

        hashTable = newTable;
        hashTableSize = newSize;
    }

    /**
     * QUERY OPERATIONS
     */

    public boolean containsKey(int hashKey) {
        return findInHashTable(hashKey) != null;
    }

    public boolean contains(T value) {
        if (value == null) {
            ListNode<T> current = listHead;
            while (current != null) {
                if (current.value == null) return true;
                current = current.next;
            }
            return false;
        }

        ListNode<T> current = listHead;
        while (current != null) {
            if (value.equals(current.value)) return true;
            current = current.next;
        }
        return false;
    }

    public void clear() {
        listHead = null;
        listTail = null;
        listSize = 0;

        for (int i = 0; i < hashTableSize; i++) {
            hashTable[i] = null;
        }
    }

    public boolean isEmpty() {
        return listSize == 0;
    }

    public int size() {
        return listSize;
    }

    public int getSize() {
        return listSize;
    }

    public int getCapacity() {
        return hashTableSize;
    }

    /**
     * SORTING (Uses linked list)
     */

    public void selectionSort(CompareFunction<T> comparator) {
        if (listSize <= 1) return;

        ListNode<T> current = listHead;
        while (current != null) {
            ListNode<T> minNode = current;
            ListNode<T> inner = current.next;

            while (inner != null) {
                if (comparator.compare(inner.value, minNode.value) < 0) {
                    minNode = inner;
                }
                inner = inner.next;
            }

            // Swap values (not nodes to preserve hash table links)
            if (minNode != current) {
                T temp = current.value;
                current.value = minNode.value;
                minNode.value = temp;
            }

            current = current.next;
        }
    }

    public void insertionSort(CompareFunction<T> comparator) {
        if (listSize <= 1) return;

        ListNode<T> sorted = null;
        ListNode<T> current = listHead;

        while (current != null) {
            ListNode<T> next = current.next;

            // Insert current into sorted list
            if (sorted == null || comparator.compare(current.value, sorted.value) <= 0) {
                current.next = sorted;
                if (sorted != null) sorted.prev = current;
                sorted = current;
                current.prev = null;
            } else {
                ListNode<T> search = sorted;
                while (search.next != null &&
                        comparator.compare(current.value, search.next.value) > 0) {
                    search = search.next;
                }

                current.next = search.next;
                if (search.next != null) search.next.prev = current;
                search.next = current;
                current.prev = search;
            }

            current = next;
        }

        // Update list head and fix tail
        listHead = sorted;
        listTail = sorted;
        while (listTail != null && listTail.next != null) {
            listTail = listTail.next;
        }
    }

    public HashList<T> getSortedValues(CompareFunction<T> comparator, boolean ascending) {
        HashList<T> sorted = new HashList<>(hashTableSize);

        // Copy values
        ListNode<T> current = listHead;
        while (current != null) {
            sorted.add(current.key, current.value);
            current = current.next;
        }

        // Sort
        sorted.selectionSort((a, b) -> {
            int compare = comparator.compare(a, b);
            return ascending ? compare : -compare;
        });

        return sorted;
    }

    /**
     * SEARCH (Can use both structures)
     */

    public HashList<T> findAll(SearchCondition<T> condition) {
        HashList<T> results = new HashList<>();

        ListNode<T> current = listHead;
        while (current != null) {
            if (condition.matches(current.value)) {
                results.add(current.key, current.value);
            }
            current = current.next;
        }

        return results;
    }

    public T find(SearchCondition<T> condition) {
        ListNode<T> current = listHead;
        while (current != null) {
            if (condition.matches(current.value)) {
                return current.value;
            }
            current = current.next;
        }
        return null;
    }

    public void forEach(Action<T> action) {
        ListNode<T> current = listHead;
        while (current != null) {
            action.perform(current.value);
            current = current.next;
        }
    }

    /**
     * ITERATION/COLLECTION METHODS
     */

    public HashList<T> getAllValues() {
        HashList<T> copy = new HashList<>(hashTableSize);

        ListNode<T> current = listHead;
        while (current != null) {
            copy.add(current.key, current.value);
            current = current.next;
        }

        return copy;
    }

    public HashList<T> copy() {
        return getAllValues();
    }

    /**
     * TABLE DATA WRAPPER (for compatibility)
     */

    public TableDataWrapper<T> toTableDataWrapper() {
        return new TableDataWrapper<T>() {
            @Override
            public int size() {
                return listSize;
            }

            @Override
            public T get(int index) {
                return getByIndex(index);
            }
        };
    }

    public interface TableDataWrapper<T> {
        int size();
        T get(int index);
    }

    /**
     * HASH STATISTICS (for debugging)
     */

    public String getHashStatistics() {
        int emptyBuckets = 0;
        int maxBucketSize = 0;
        int totalCollisions = 0;

        for (int i = 0; i < hashTableSize; i++) {
            int bucketSize = 0;
            ListNode<T> current = hashTable[i];

            while (current != null) {
                bucketSize++;
                current = current.hashNext;
            }

            if (bucketSize == 0) {
                emptyBuckets++;
            } else {
                totalCollisions += (bucketSize - 1);
                if (bucketSize > maxBucketSize) {
                    maxBucketSize = bucketSize;
                }
            }
        }

        return String.format(
                "Hash Table Statistics:\n" +
                        "Size: %d items, Capacity: %d buckets\n" +
                        "Load Factor: %.2f\n" +
                        "Empty Buckets: %d (%.1f%%)\n" +
                        "Max Bucket Size: %d\n" +
                        "Total Collisions: %d\n",
                listSize, hashTableSize,
                (float)listSize / hashTableSize,
                emptyBuckets, (emptyBuckets * 100.0f / hashTableSize),
                maxBucketSize, totalCollisions
        );
    }
}