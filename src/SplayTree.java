class SplayTree {
    static class Node {
        int key;
        Node left, right, parent;
        
        Node(int k) {
            key = k;
        }
    }

    private Node root;

    public void insert(int key) {
        if (root == null) {
            root = new Node(key);
            return;
        }

        Node z = root, p = null;
        
        while (z != null) {
            p = z;
            
            if (key < z.key) {
                z = z.left;
            } else if (key > z.key) {
                z = z.right;
            } else {
                splay(z);
                return;
            }
        }

        Node n = new Node(key);
        n.parent = p;
        
        if (key < p.key) {
            p.left = n;
        } else {
            p.right = n;
            splay(n);
        }
    }

    public boolean contains(int key) {
        Node z = root, last = null;
        
        while (z != null) {
            last = z;
            
            if (key < z.key) {
                z = z.left;
            }
            else if (key > z.key) {
                z = z.right;
            } else {
                splay(z); 
                return true;
            }
        }
        
        if (last != null) {
            splay(last);
        }

        return false;
    }

    public void DFSSplayTree() {
        dfs(root);
        System.out.println();
    }

    private void dfs(Node n) {
        if (n == null) {
            return;
        }

        System.out.print(n.key + " ");
        dfs(n.left);
        dfs(n.right);
    }

    private void rotateLeft(Node x) {
        Node y = x.right;
        
        if (y == null) {
            return;
        }

        x.right = y.left;

        if (y.left != null) {
            y.left.parent = x;
        }

        y.parent = x.parent;

        if (x.parent == null) {
            root = y;
        } else if (x == x.parent.left) {
            x.parent.left = y;
        } else {
            x.parent.right = y;
        }

        y.left = x;
        x.parent = y;
    }

    private void rotateRight(Node x) {
        Node y = x.left;

        if (y == null) {
            return;
        }

        x.left = y.right;

        if (y.right != null) {
            y.right.parent = x;
        }

        y.parent = x.parent;

        if (x.parent == null) {
            root = y;
        } else if (x == x.parent.left) {
            x.parent.left = y;
        } else {
            x.parent.right = y;
        }

        y.right = x;
        x.parent = y;
    }

    private void splay(Node x) {
        
        while (x.parent != null) {
            Node p = x.parent;
            Node g = p.parent;
            
            if (g == null) {
                if (x == p.left) {
                    rotateRight(p);
                } else {
                    rotateLeft(p);
                }
            
            } else if ((x == p.left) == (p == g.left)) {
                if (x == p.left) {
                    rotateRight(g);
                    rotateRight(p);
                } else {
                    rotateLeft(g);
                    rotateLeft(p);
                }
            
            } else {
                if (x == p.left) {
                    rotateRight(p);
                    rotateLeft(g);
                } else {
                    rotateLeft(p);
                    rotateRight(g);
                }
            }
        }
    }
}
