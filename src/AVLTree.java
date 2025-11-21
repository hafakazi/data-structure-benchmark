class AVLTree {
    static class Node {
        int key;
        Node left, right;
        int height;
        
        Node(int k) {
            key = k;
            height = 1;
        }
    }
    
    private Node root;

    public void insert(int key) {
        root = insert(root, key);
    }
    
    public boolean contains(int key) {
        return contains(root, key);
    }

    public void getAVLKeyHeight(int key) {
        Node n = findNode(root, key);

        if (n == null) {
            System.out.println("Key " + key + " not found in AVL tree.");
        } else {
            System.out.println(n.height);
        }
    }

    private int h(Node n) {
        return n == null ? 0 : n.height;
    }

    private Node insert(Node n, int key) {
        if (n == null) {
            return new Node(key);
        }

        if (key < n.key) {
            n.left = insert(n.left, key);
        } else if (key > n.key) {
            n.right = insert(n.right, key);
        } else {
            return n;
        }

        update(n);
        return balance(n);
    }

    private boolean contains(Node n, int key) {
        while (n != null) {
            
            if (key < n.key) {
                n = n.left;
            } else if (key > n.key) {
                n = n.right;
            } else {
                return true;
            }
        }

        return false;
    }

    private Node findNode(Node n, int key) {
        while (n != null) {
            
            if (key < n.key) {
                n = n.left;
            } else if (key > n.key) {
                n = n.right;
            } else {
                return n;
            }
        }
        
        return null;
    }

    private void update(Node n) {
        n.height = 1 + Math.max(h(n.left), h(n.right));
    }

    private int bf(Node n) {
        return h(n.left) - h(n.right);
    }

    private Node rotateRight(Node y) {
        Node x = y.left;
        Node T2 = x.right;
        x.right = y;
        y.left = T2;
        update(y); update(x);
        
        return x;
    }

    private Node rotateLeft(Node x) {
        Node y = x.right;
        Node T2 = y.left;
        y.left = x;
        x.right = T2;
        update(x); update(y);
        
        return y;
    }

    private Node balance(Node n) {
        int balance = bf(n);
        
        if (balance > 1) {
            if (bf(n.left) < 0) {
                n.left = rotateLeft(n.left);
            }
            return rotateRight(n);

        } else if (balance < -1) {
            if (bf(n.right) > 0) {
                n.right = rotateRight(n.right);
            }
            return rotateLeft(n);
        }

        return n;
    }
}
