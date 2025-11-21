import java.util.*;

class HashTableChaining {
    private final List<Integer>[] buckets;
    private final int m;

    @SuppressWarnings("unchecked")
    public HashTableChaining(int m) {
        this.m = m;
        buckets = new List[m];
        
        for (int i = 0; i < m; i++) {
            buckets[i] = new LinkedList<>();
        }
    }

    private int h(int key) {
        int hk = key % m;
        
        if (hk < 0) {
            hk += m;
        }
        
        return hk;
    }

    public void insert(int key) {
        int i = h(key);
        List<Integer> b = buckets[i];
        
        for (int v : b) {
            if (v == key) {
                return;
            }
        }
        
        b.add(key);
    }

    public boolean contains(int key) {
        int i = h(key);
        
        for (int v : buckets[i]) {
            if (v == key) {
                return true;
            }
        }

        return false;
    }

    public void getChain(int index) {
        if (index < 0 || index >= m) {
            System.out.println("[]");
            return;
        }

        List<Integer> b = buckets[index];
        System.out.println(b.toString());
    }
}
