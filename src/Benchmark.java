import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Benchmark {

    // Configuration
    static class ScaleCfg {
        final String label;
        final int N, mChaining, mQuadratic, iter; // iter = 1/2/3
        
        ScaleCfg(String label, int N, int mChaining, int mQuadratic, int iter) {
            this.label = label;
            this.N = N;
            this.mChaining = mChaining;
            this.mQuadratic = mQuadratic;
            this.iter = iter;
        }
    }

    static final ScaleCfg SMALL  = new ScaleCfg("1,000",     1_000,     928,     2_003,    1);
    static final ScaleCfg MEDIUM = new ScaleCfg("10,000",   10_000,   8_329,    20_011,   2);
    static final ScaleCfg LARGE  = new ScaleCfg("100,000", 100_000,  83_329,   200_003,   3);

    static final List<ScaleCfg> SCALES = Arrays.asList(SMALL, MEDIUM, LARGE);

    // How many times to repeat each timed block to avoid "0 ms"
    static final int REPEATS = Integer.getInteger("repeats", 5);

    // Measurement helpers
    static class Measure {
        static long timeNanos(Runnable r) {
            long start = System.nanoTime();
            r.run();
            return System.nanoTime() - start;
        }
        
        static long ms(long nanos) {
            return TimeUnit.NANOSECONDS.toMicros(nanos);
        }
    }

    // Key helpers
    static List<Integer> readInts(Path path, int limit) throws IOException {
        List<Integer> list = new ArrayList<>(limit);
        
        try (BufferedReader br = Files.newBufferedReader(path)) {
            String line;
            
            while ((line = br.readLine()) != null && (limit <= 0 || list.size() < limit)) {
                line = line.trim();
                
                if (line.isEmpty()) {
                    continue;
                }

                list.add(Integer.parseInt(line));
            }
        }
        return list;
    }

    static Optional<Path> findFile(String fileName) {
        Path here = Paths.get(fileName);
        
        if (Files.exists(here)) {
            return Optional.of(here);
        }
        
        Path parent = Paths.get("..", fileName);
        
        if (Files.exists(parent)) {
            return Optional.of(parent);
        }
        
        return Optional.empty();
    }

    static List<Integer> maybeLoadKeys(int iter, String kind, int limit) throws IOException {
        // "insert" or "search"
        String fname = String.format("itr%d_%s_key.txt", iter, kind);
        Optional<Path> p = findFile(fname);
        
        if (p.isPresent()) {
            return readInts(p.get(), limit);
        }
        
        return null; // caller will fall back
    }

    static List<Integer> randomUniqueInts(int n, long seed) {
        List<Integer> nums = new ArrayList<>(n * 2);
        
        for (int i = 1; i <= n * 2; i++) {
            nums.add(i);
        }
        
        Collections.shuffle(nums, new Random(seed));
        return new ArrayList<>(nums.subList(0, n));
    }

    static List<Integer> halfExistingHalfMissing(List<Integer> inserted, int n, long seed) {
        Random rnd = new Random(seed);
        List<Integer> queries = new ArrayList<>(n);
        
        for (int i = 0; i < n / 2; i++) {
            queries.add(inserted.get(rnd.nextInt(inserted.size())));
        }
        
        int base = 1_000_000_000;

        for (int i = n/2; i < n; i++) {
            queries.add(base + i);
        }
        
        return queries;
    }

    // Results
    static class Results {
        long insertMs; long searchMs;
    }

    static Results benchAVL(List<Integer> insertKeys, List<Integer> searchKeys) {
        Results res = new Results();
        // Inserts - repeat with fresh trees
        long nanosIns = 0;
        
        for (int r = 0; r < REPEATS; r++) {
            AVLTree t = new AVLTree();
            
            nanosIns += Measure.timeNanos(() -> {
                for (int k : insertKeys) {
                    t.insert(k);
                }
            });
        }

        res.insertMs = Measure.ms(nanosIns / REPEATS);

        // Searches - build once, repeat queries
        AVLTree t = new AVLTree();
        
        for (int k : insertKeys) {
            t.insert(k);
        }

        long nanosSearch = 0;
        
        for (int r = 0; r < REPEATS; r++) {
            nanosSearch += Measure.timeNanos(() -> {
                for (int q : searchKeys) {
                    t.contains(q);
                }
            });
        }

        res.searchMs = Measure.ms(nanosSearch / REPEATS);

        // Verification - not timed
        if (!insertKeys.isEmpty()) {
            t.getAVLKeyHeight(insertKeys.get(0));
        }

        return res;
    }

    static Results benchSplay(List<Integer> insertKeys, List<Integer> searchKeys) {
        Results res = new Results();
        long nanosIns = 0;
        
        for (int r = 0; r < REPEATS; r++) {
            SplayTree t = new SplayTree();
            
            nanosIns += Measure.timeNanos(() -> {
                for (int k : insertKeys) {
                    t.insert(k);
                }
            });
        }

        res.insertMs = Measure.ms(nanosIns / REPEATS);

        SplayTree t = new SplayTree();
        
        for (int k : insertKeys) {
            t.insert(k);
        }

        long nanosSearch = 0;
        
        for (int r = 0; r < REPEATS; r++) {
            nanosSearch += Measure.timeNanos(() -> {
                for (int q : searchKeys) {
                    t.contains(q);
                }
            });
        }

        res.searchMs = Measure.ms(nanosSearch / REPEATS);
        return res;
    }

    static Results benchHashChaining(List<Integer> insertKeys, List<Integer> searchKeys, int m) {
        Results res = new Results();
        long nanosIns = 0;
        
        for (int r = 0; r < REPEATS; r++) {
            HashTableChaining h = new HashTableChaining(m);
            
            nanosIns += Measure.timeNanos(() -> { 
                for (int k : insertKeys) {
                    h.insert(k);
                }
            });
        }
        
        res.insertMs = Measure.ms(nanosIns / REPEATS);

        HashTableChaining h = new HashTableChaining(m);
        
        for (int k : insertKeys) {
            h.insert(k);
        }

        long nanosSearch = 0;
        
        for (int r = 0; r < REPEATS; r++) {
            nanosSearch += Measure.timeNanos(() -> {
                for (int q : searchKeys) {
                    h.contains(q);
                }
            });
        }

        res.searchMs = Measure.ms(nanosSearch / REPEATS);
        return res;
    }

    static Results benchHashQuadratic(List<Integer> insertKeys, List<Integer> searchKeys, int m) {
        Results res = new Results();
        long nanosIns = 0;
        
        for (int r = 0; r < REPEATS; r++) {
            HashTableQuadratic h = new HashTableQuadratic(m);
            
            nanosIns += Measure.timeNanos(() -> {
                for (int k : insertKeys) {
                    h.insert(k);
                }
            });
        }

        res.insertMs = Measure.ms(nanosIns / REPEATS);

        HashTableQuadratic h = new HashTableQuadratic(m);
        
        for (int k : insertKeys) {
            h.insert(k);
        }

        long nanosSearch = 0;
        
        for (int r = 0; r < REPEATS; r++) {
            nanosSearch += Measure.timeNanos(() -> {
                for (int q : searchKeys) {
                    h.contains(q);
                }
            });
        }

        res.searchMs = Measure.ms(nanosSearch / REPEATS);

        // Verification helper
        if (!insertKeys.isEmpty()) {
            h.getQuadraticIndex(insertKeys.get(0));
        }

        return res;
    }

    // Pretty table
    static void printTable(String title, long[][] rows, String[] rowNames, String[] colNames) {
        System.out.println();
        System.out.println(title);
        System.out.printf("%-24s", "Data Structure");
        
        for (String c : colNames) {
            System.out.printf("%12s", c);
        }

        System.out.println();
        
        for (int i = 0; i < rows.length; i++) {
            System.out.printf("%-24s", rowNames[i]);
            
            for (long v : rows[i]) {
                System.out.printf("%12d", v);
            }

            System.out.println();
        }
    }

    static Results[] runScale(ScaleCfg cfg, List<Integer> insertKeys, List<Integer> searchKeys) {
        Results avl   = benchAVL(insertKeys, searchKeys);
        Results splay = benchSplay(insertKeys, searchKeys);
        Results chain = benchHashChaining(insertKeys, searchKeys, cfg.mChaining);
        Results quad  = benchHashQuadratic(insertKeys, searchKeys, cfg.mQuadratic);
        
        return new Results[]{avl, splay, chain, quad};
    }

    // Main
    public static void main(String[] args) throws Exception {
        String[] dsNames = {"AVL Tree","Splay Tree","Hash Table (Chaining)","Hash Table (Quadratic)"};
        String[] colNames = SCALES.stream().map(s -> s.label).toArray(String[]::new);

        long[][] ins = new long[4][SCALES.size()];
        long[][] sea = new long[4][SCALES.size()];

        int col = 0;
        
        for (ScaleCfg cfg : SCALES) {
            // Load keys if present, otherwise generate random
            List<Integer> insertKeys = maybeLoadKeys(cfg.iter, "insert", cfg.N);
            
            if (insertKeys == null) {
                insertKeys = randomUniqueInts(cfg.N, 100 + cfg.iter);
            }

            List<Integer> searchKeys = maybeLoadKeys(cfg.iter, "search", cfg.N);
            
            if (searchKeys == null) {
                searchKeys = halfExistingHalfMissing(insertKeys, cfg.N, 200 + cfg.iter);
            }

            Results[] r = runScale(cfg, insertKeys, searchKeys);
            
            for (int i = 0; i < 4; i++) {
                ins[i][col] = r[i].insertMs;
                sea[i][col] = r[i].searchMs;
            }

            col++;
        }

        printTable("Insertion Performance (Time in ms)", ins, dsNames, colNames);
        printTable("Search Performance (Time in ms)",    sea, dsNames, colNames);

        AVLTree avlTree = new AVLTree();
        SplayTree splayTree = new SplayTree();
        HashTableChaining hashTableChaining = new HashTableChaining(50);
        HashTableQuadratic hashTableQuadratic = new HashTableQuadratic(50);

        avlTree.insert(5);
        avlTree.insert(10);
        avlTree.insert(15);

        splayTree.insert(5);
        splayTree.insert(10);
        splayTree.insert(15);

        hashTableChaining.insert(5);
        hashTableChaining.insert(15);
        hashTableChaining.insert(25);

        hashTableQuadratic.insert(5);
        hashTableQuadratic.insert(15);
        hashTableQuadratic.insert(25);

        System.out.print("\nAVL Tree Height of a Key: ");
        avlTree.getAVLKeyHeight(10);
        
        System.out.print("\nSplay Tree DFS Traversal: ");
        splayTree.DFSSplayTree();

        System.out.print("\nHash Table (Chaining): ");
        hashTableChaining.getChain(5);

        System.out.print("\nHash Table (Quadratic): ");
        hashTableQuadratic.getQuadraticIndex(5);

        System.out.println();
        System.out.println("Environment:");
        System.out.println("Java version: " + System.getProperty("java.version"));
        System.out.println("JVM vendor: " + System.getProperty("java.vendor"));
        System.out.println("OS: " + System.getProperty("os.name") + " " + System.getProperty("os.arch"));
        System.out.println("Repeats per measurement: " + REPEATS);
    }
}
