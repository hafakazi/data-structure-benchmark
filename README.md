# Student Info
* Name: Hafa Kazi  
* NetID: nsk210004  
* Course: 3345
* Section: 002
* Date: 10/29/2025

# Data Structure Benchmark - README

A Java project that implements, evaluates, and compares the efficiency of three  data structures in handling large, dynamically changing datasets:
1. AVL Tree
2. Splay Tree
3. Hash Table
   - Collision Resolution:
     - Chaining (linked lists)
     - Quadratic Probing (open addressing)

The data structures are evaluated on **insertion** and **search (lookup)** operations using datasets of increasing size:  
**Small (1,000 elements)**, **Medium (10,000 elements)**, and **Large (100,000 elements)**.

## System Configuration
| Component            | Details                  |
|----------------------|--------------------------|
| Java Version         | OpenJDK 21               |
| Operating System     | macOS / Windows / Linux  |
| CPU                  | e.g. Apple M2, Intel i7  |
| JVM Heap Size        | Default (512 MB)         |

## Implementation Summary

### 1. AVL Tree
- Self-balancing binary search tree.  
- Maintains height balance after each insertion or deletion.  
- Rotation operations (LL, RR, LR, RL) ensure O(log N) time complexity.

### 2. Splay Tree
- Self-adjusting binary search tree.  
- Recently accessed elements are moved to the root using splaying.  
- Amortized O(log N) for operations, though worst case can degrade temporarily.

### 3. Hash Table
- Hash Function: Modulo division: key % tableSize.  
- Implemented two collision-resolution strategies:
  - Chaining: Each index contains a linked list of keys.
  - Quadratic Probing: Resolves collisions using: (hash + i²) % tableSize.

## Testing Setup

| Scale  | Elements (N) | Table Size (Chaining) | Table Size (Quadratic) |
|--------|--------------|-----------------------|------------------------|
| Small  | 1,000        | 928                   | 2,003                  |
| Medium | 10,000       | 8,329                 | 20,011                 |
| Large  | 100,000.     | 83,329                | 200,003                |

Each test was repeated multiple times, using provided key files:
- itr1_insert_key.txt, itr1_search_key.txt
- itr2_insert_key.txt, itr2_search_key.txt
- itr3_insert_key.txt, itr3_search_key.txt

## Test Results

### Insertion Performance (Time in ms)
| Data Structure         | 1,000 | 10,000 | 100,000 |
|------------------------|-------|--------|---------|
| AVL Tree               |    353|    1755|  117,633|
| Splay Tree             |    391|    3091|   34,547|
| Hash Table (Chaining)  |    207|     916|    6,315|
| Hash Table (Quadratic) |     85|     576|    2,090|

### Search Performance (Time in ms)
| Data Structure         | 1,000 | 10,000 | 100,000 |
|------------------------|-------|--------|---------|
| AVL Tree               |    109|   1,063|    7,277|
| Splay Tree             |    172|   1,905|   21,640|
| Hash Table (Chaining)  |    124|     806|    5,012|
| Hash Table (Quadratic) |    102|     605|    1,966|


### When Each Data Structure Performs Best/Worst
- **AVL Tree:** Performs best when the dataset is large and requires frequent searches; balancing ensures consistent log-time access.
- **Splay Tree:** Performs better for workloads with repeated access to the same elements.
- **Hash Table (Chaining):** Good average performance; insertion and search are near O(1) but memory usage is higher.
- **Hash Table (Quadratic):** Slightly faster than chaining for sparse tables, but degrades as load factor increases.

### Trade-offs Between Speed and Memory
- Trees use more CPU time due to pointer operations and rotations but are more memory-efficient.
- Hash Tables are faster but need larger memory allocations to minimize collisions.

### Observations About Splaying and Balancing
- AVL Tree balancing ensures predictable performance at all scales.
- Splay Tree’s adaptive behavior helps on localized workloads but adds overhead for uniformly random access.

## Conclusion
- Fastest overall structure: Hash Table (Chaining) for random datasets.  
- Most consistent performance: AVL Tree.  
- Best for repeated key access: Splay Tree.  
- The trade-off between speed (Hash Tables) and predictability (AVL) defines their ideal use cases.