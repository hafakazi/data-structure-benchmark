class HashTableQuadratic {
    private final Integer[] table;
    private final boolean[] tomb;
    private final int m;

    public HashTableQuadratic(int m) {
        if (m <= 0) {
            throw new IllegalArgumentException("Table size must be positive");
        }

        this.m = m;
        this.table = new Integer[m];
        this.tomb = new boolean[m];
    }

    // Modulo-division hash - non-negative.
    private int h(int key) {
        int hk = key % m;
        
        if (hk < 0) {
            hk += m;
        }

        return hk;
    }

    // Insert key using quadratic probing, ignores duplicates.
    public void insert(int key) {
        int base = h(key);
        int j = 0;
        int firstTomb = -1;

        while (j < m) {
            int idx = (base + j * j) % m;
            Integer v = table[idx];

            if (v == null) {
                // Empty slot - place at first tombstone if seen, else here.
                if (firstTomb != -1) {
                    idx = firstTomb;
                }

                table[idx] = key;
                tomb[idx] = false;
                return;
            }

            if (v == key) {
                // Duplicate - ignore
                return;
            }

            // First tombstone to place new key there if later there's an empty slot
            if (tomb[idx] && firstTomb == -1) {
                firstTomb = idx;
            }

            j++;
        }
        throw new IllegalStateException("Hash table is full");
    }

    // Returns true if the key is present.
    public boolean contains(int key) {
        return findQuadraticIndex(key) != -1;
    }


    // Verification helper - Returns the array index where the specified key is stored, 
    // or -1 if the key is not in the table.

    private int findQuadraticIndex(int key) {
        int base = h(key);
        int j = 0;

        while (j < m) {
            int idx = (base + j * j) % m;
            Integer v = table[idx];
            
            if (v == null && !tomb[idx]) {
                return -1;  // no empty slot found
            }
            
            if (v != null && v == key) {
                return idx;   // found
            }
            
            j++;
        }
        return -1;
    }

    public void getQuadraticIndex(int key) {
        int idx = findQuadraticIndex(key);
        
        if (idx == -1) {
            System.out.println("Key " + key + " not found in quadratic hash table.");
        } else {
            System.out.println("Key " + key + " is stored at index " + idx);
        }
    }
}
