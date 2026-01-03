package by.it.group410971.antonenko.lesson12;

import java.util.*;

public class MySplayMap implements NavigableMap<Integer, String> {

    private static class Node {
        Integer key;
        String value;
        Node left;
        Node right;
        Node parent;

        Node(Integer key, String value, Node parent) {
            this.key = key;
            this.value = value;
            this.parent = parent;
            this.left = null;
            this.right = null;
        }
    }

    private Node root;
    private int size;

    public MySplayMap() {
        root = null;
        size = 0;
    }

    // Вспомогательные методы для splay-дерева

    private void rotateRight(Node x) {
        Node y = x.left;
        if (y != null) {
            x.left = y.right;
            if (y.right != null) {
                y.right.parent = x;
            }
            y.parent = x.parent;
            if (x.parent == null) {
                root = y;
            } else if (x == x.parent.right) {
                x.parent.right = y;
            } else {
                x.parent.left = y;
            }
            y.right = x;
            x.parent = y;
        }
    }

    private void rotateLeft(Node x) {
        Node y = x.right;
        if (y != null) {
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
    }

    private void splay(Node x) {
        while (x.parent != null) {
            if (x.parent.parent == null) {
                // Zig
                if (x == x.parent.left) {
                    rotateRight(x.parent);
                } else {
                    rotateLeft(x.parent);
                }
            } else if (x == x.parent.left && x.parent == x.parent.parent.left) {
                // Zig-zig (правый-правый)
                rotateRight(x.parent.parent);
                rotateRight(x.parent);
            } else if (x == x.parent.right && x.parent == x.parent.parent.right) {
                // Zig-zig (левый-левый)
                rotateLeft(x.parent.parent);
                rotateLeft(x.parent);
            } else if (x == x.parent.right && x.parent == x.parent.parent.left) {
                // Zig-zag (левый-правый)
                rotateLeft(x.parent);
                rotateRight(x.parent);
            } else {
                // Zig-zag (правый-левый)
                rotateRight(x.parent);
                rotateLeft(x.parent);
            }
        }
    }

    private Node findNode(Integer key) {
        Node current = root;
        while (current != null) {
            int cmp = key.compareTo(current.key);
            if (cmp == 0) {
                return current;
            } else if (cmp < 0) {
                current = current.left;
            } else {
                current = current.right;
            }
        }
        return null;
    }

    private Node splayFind(Integer key) {
        Node node = findNode(key);
        if (node != null) {
            splay(node);
        }
        return node;
    }

    private Node findMin(Node node) {
        if (node == null) return null;
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    private Node findMax(Node node) {
        if (node == null) return null;
        while (node.right != null) {
            node = node.right;
        }
        return node;
    }

    /////////////////////////////////////////////////////////////////////////
    //////               Обязательные к реализации методы             ///////
    /////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        if (root == null) {
            return "{}";
        }

        List<String> entries = new ArrayList<>();
        inOrderTraversal(root, entries);

        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (int i = 0; i < entries.size(); i++) {
            sb.append(entries.get(i));
            if (i < entries.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("}");
        return sb.toString();
    }

    private void inOrderTraversal(Node node, List<String> entries) {
        if (node != null) {
            inOrderTraversal(node.left, entries);
            entries.add(node.key + "=" + node.value);
            inOrderTraversal(node.right, entries);
        }
    }

    @Override
    public Comparator<? super Integer> comparator() {
        // Возвращаем null для естественного порядка (как TreeMap)
        return null;
    }

    @Override
    public String put(Integer key, String value) {
        if (key == null) {
            throw new NullPointerException("Key cannot be null");
        }

        if (root == null) {
            root = new Node(key, value, null);
            size++;
            return null;
        }

        Node current = root;
        Node parent = null;

        while (current != null) {
            parent = current;
            int cmp = key.compareTo(current.key);

            if (cmp == 0) {
                String oldValue = current.value;
                current.value = value;
                splay(current);
                return oldValue;
            } else if (cmp < 0) {
                current = current.left;
            } else {
                current = current.right;
            }
        }

        Node newNode = new Node(key, value, parent);
        if (key.compareTo(parent.key) < 0) {
            parent.left = newNode;
        } else {
            parent.right = newNode;
        }

        splay(newNode);
        size++;
        return null;
    }

    @Override
    public String remove(Object keyObj) {
        if (keyObj == null) {
            throw new NullPointerException("Key cannot be null");
        }

        Integer key = (Integer) keyObj;
        Node node = findNode(key);

        if (node == null) {
            return null;
        }

        splay(node);

        String removedValue = node.value;

        if (node.left == null) {
            // Нет левого поддерева
            root = node.right;
            if (root != null) {
                root.parent = null;
            }
        } else if (node.right == null) {
            // Нет правого поддерева
            root = node.left;
            if (root != null) {
                root.parent = null;
            }
        } else {
            // Есть оба поддерева
            Node minRight = findMin(node.right);

            if (minRight.parent != node) {
                // Перемещаем minRight на место node.right
                if (minRight == minRight.parent.left) {
                    minRight.parent.left = minRight.right;
                } else {
                    minRight.parent.right = minRight.right;
                }
                if (minRight.right != null) {
                    minRight.right.parent = minRight.parent;
                }
                minRight.right = node.right;
                minRight.right.parent = minRight;
            }

            minRight.left = node.left;
            minRight.left.parent = minRight;
            root = minRight;
            root.parent = null;
        }

        size--;
        return removedValue;
    }

    @Override
    public String get(Object keyObj) {
        if (keyObj == null) {
            throw new NullPointerException("Key cannot be null");
        }

        Integer key = (Integer) keyObj;
        Node node = splayFind(key);
        return (node != null) ? node.value : null;
    }

    @Override
    public boolean containsKey(Object key) {
        if (key == null) {
            throw new NullPointerException("Key cannot be null");
        }
        return get(key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        return containsValueRecursive(root, value);
    }

    private boolean containsValueRecursive(Node node, Object value) {
        if (node == null) {
            return false;
        }

        if (value == null) {
            if (node.value == null) {
                return true;
            }
        } else {
            if (value.equals(node.value)) {
                return true;
            }
        }

        return containsValueRecursive(node.left, value) ||
                containsValueRecursive(node.right, value);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public SortedMap<Integer, String> headMap(Integer toKey) {
        if (toKey == null) {
            throw new NullPointerException("toKey cannot be null");
        }
        return new SubMap(null, toKey);
    }

    @Override
    public SortedMap<Integer, String> tailMap(Integer fromKey) {
        if (fromKey == null) {
            throw new NullPointerException("fromKey cannot be null");
        }
        return new SubMap(fromKey, null);
    }

    @Override
    public SortedMap<Integer, String> subMap(Integer fromKey, Integer toKey) {
        if (fromKey == null || toKey == null) {
            throw new NullPointerException("Keys cannot be null");
        }
        if (fromKey.compareTo(toKey) > 0) {
            throw new IllegalArgumentException("fromKey > toKey");
        }
        return new SubMap(fromKey, toKey);
    }

    @Override
    public Integer firstKey() {
        if (root == null) {
            throw new NoSuchElementException();
        }
        Node min = findMin(root);
        splay(min);
        return min.key;
    }

    @Override
    public Integer lastKey() {
        if (root == null) {
            throw new NoSuchElementException();
        }
        Node max = findMax(root);
        splay(max);
        return max.key;
    }

    @Override
    public Integer lowerKey(Integer key) {
        if (key == null) {
            throw new NullPointerException("Key cannot be null");
        }
        Node result = lowerKeyRecursive(root, key, null);
        if (result != null) {
            splay(result);
            return result.key;
        }
        return null;
    }

    private Node lowerKeyRecursive(Node node, Integer key, Node best) {
        if (node == null) {
            return best;
        }

        int cmp = key.compareTo(node.key);
        if (cmp <= 0) {
            return lowerKeyRecursive(node.left, key, best);
        } else {
            return lowerKeyRecursive(node.right, key, node);
        }
    }

    @Override
    public Integer floorKey(Integer key) {
        if (key == null) {
            throw new NullPointerException("Key cannot be null");
        }
        Node result = floorKeyRecursive(root, key, null);
        if (result != null) {
            splay(result);
            return result.key;
        }
        return null;
    }

    private Node floorKeyRecursive(Node node, Integer key, Node best) {
        if (node == null) {
            return best;
        }

        int cmp = key.compareTo(node.key);
        if (cmp == 0) {
            return node;
        } else if (cmp < 0) {
            return floorKeyRecursive(node.left, key, best);
        } else {
            return floorKeyRecursive(node.right, key, node);
        }
    }

    @Override
    public Integer ceilingKey(Integer key) {
        if (key == null) {
            throw new NullPointerException("Key cannot be null");
        }
        Node result = ceilingKeyRecursive(root, key, null);
        if (result != null) {
            splay(result);
            return result.key;
        }
        return null;
    }

    private Node ceilingKeyRecursive(Node node, Integer key, Node best) {
        if (node == null) {
            return best;
        }

        int cmp = key.compareTo(node.key);
        if (cmp == 0) {
            return node;
        } else if (cmp < 0) {
            return ceilingKeyRecursive(node.left, key, node);
        } else {
            return ceilingKeyRecursive(node.right, key, best);
        }
    }

    @Override
    public Integer higherKey(Integer key) {
        if (key == null) {
            throw new NullPointerException("Key cannot be null");
        }
        Node result = higherKeyRecursive(root, key, null);
        if (result != null) {
            splay(result);
            return result.key;
        }
        return null;
    }

    private Node higherKeyRecursive(Node node, Integer key, Node best) {
        if (node == null) {
            return best;
        }

        int cmp = key.compareTo(node.key);
        if (cmp >= 0) {
            return higherKeyRecursive(node.right, key, best);
        } else {
            return higherKeyRecursive(node.left, key, node);
        }
    }

    // Класс для представления под-карты
    private class SubMap extends AbstractMap<Integer, String> implements SortedMap<Integer, String> {
        private final Integer fromKey;
        private final Integer toKey;

        SubMap(Integer fromKey, Integer toKey) {
            this.fromKey = fromKey;
            this.toKey = toKey;
        }

        @Override
        public Comparator<? super Integer> comparator() {
            return MySplayMap.this.comparator();
        }

        @Override
        public SortedMap<Integer, String> subMap(Integer fromKey, Integer toKey) {
            if (fromKey == null || toKey == null) {
                throw new NullPointerException();
            }
            if (fromKey.compareTo(toKey) > 0) {
                throw new IllegalArgumentException();
            }

            Integer newFromKey = (this.fromKey != null && this.fromKey.compareTo(fromKey) > 0)
                    ? this.fromKey : fromKey;
            Integer newToKey = (this.toKey != null && this.toKey.compareTo(toKey) < 0)
                    ? this.toKey : toKey;

            return new SubMap(newFromKey, newToKey);
        }

        @Override
        public SortedMap<Integer, String> headMap(Integer toKey) {
            if (toKey == null) {
                throw new NullPointerException();
            }
            Integer newToKey = (this.toKey != null && this.toKey.compareTo(toKey) < 0)
                    ? this.toKey : toKey;
            return new SubMap(this.fromKey, newToKey);
        }

        @Override
        public SortedMap<Integer, String> tailMap(Integer fromKey) {
            if (fromKey == null) {
                throw new NullPointerException();
            }
            Integer newFromKey = (this.fromKey != null && this.fromKey.compareTo(fromKey) > 0)
                    ? this.fromKey : fromKey;
            return new SubMap(newFromKey, this.toKey);
        }

        @Override
        public Integer firstKey() {
            Node node = findFirstInRange(root);
            if (node == null) {
                throw new NoSuchElementException();
            }
            return node.key;
        }

        @Override
        public Integer lastKey() {
            Node node = findLastInRange(root);
            if (node == null) {
                throw new NoSuchElementException();
            }
            return node.key;
        }

        private Node findFirstInRange(Node node) {
            if (node == null) {
                return null;
            }

            Node result = null;
            if (isInRange(node.key)) {
                result = node;
                Node leftResult = findFirstInRange(node.left);
                if (leftResult != null) {
                    result = leftResult;
                }
            } else if (toKey != null && node.key.compareTo(toKey) >= 0) {
                result = findFirstInRange(node.left);
            } else {
                result = findFirstInRange(node.right);
            }

            return result;
        }

        private Node findLastInRange(Node node) {
            if (node == null) {
                return null;
            }

            Node result = null;
            if (isInRange(node.key)) {
                result = node;
                Node rightResult = findLastInRange(node.right);
                if (rightResult != null) {
                    result = rightResult;
                }
            } else if (fromKey != null && node.key.compareTo(fromKey) < 0) {
                result = findLastInRange(node.right);
            } else {
                result = findLastInRange(node.left);
            }

            return result;
        }

        private boolean isInRange(Integer key) {
            boolean fromOk = (fromKey == null) || key.compareTo(fromKey) >= 0;
            boolean toOk = (toKey == null) || key.compareTo(toKey) < 0;
            return fromOk && toOk;
        }

        @Override
        public Set<Entry<Integer, String>> entrySet() {
            Set<Entry<Integer, String>> entries = new TreeSet<>(Comparator.comparing(Entry::getKey));
            collectEntries(root, entries);
            return entries;
        }

        private void collectEntries(Node node, Set<Entry<Integer, String>> entries) {
            if (node != null) {
                collectEntries(node.left, entries);
                if (isInRange(node.key)) {
                    entries.add(new SimpleEntry<>(node.key, node.value));
                }
                collectEntries(node.right, entries);
            }
        }

        @Override
        public int size() {
            int count = 0;
            Node node = findFirstInRange(root);
            while (node != null && isInRange(node.key)) {
                count++;
                // Найти следующую ноду в порядке возрастания
                node = findNextInOrder(node);
            }
            return count;
        }

        private Node findNextInOrder(Node node) {
            if (node.right != null) {
                Node current = node.right;
                while (current.left != null) {
                    current = current.left;
                }
                return current;
            } else {
                Node parent = node.parent;
                while (parent != null && node == parent.right) {
                    node = parent;
                    parent = parent.parent;
                }
                return parent;
            }
        }

        @Override
        public boolean isEmpty() {
            return findFirstInRange(root) == null;
        }

        @Override
        public boolean containsKey(Object key) {
            if (!(key instanceof Integer)) {
                return false;
            }
            Integer k = (Integer) key;
            return isInRange(k) && MySplayMap.this.containsKey(k);
        }

        @Override
        public String get(Object key) {
            if (!containsKey(key)) {
                return null;
            }
            return MySplayMap.this.get(key);
        }

        @Override
        public String put(Integer key, String value) {
            if (!isInRange(key)) {
                throw new IllegalArgumentException("Key out of range");
            }
            return MySplayMap.this.put(key, value);
        }

        @Override
        public String remove(Object key) {
            if (!containsKey(key)) {
                return null;
            }
            return MySplayMap.this.remove(key);
        }

        @Override
        public void clear() {
            // Удаляем все элементы в диапазоне
            List<Integer> keysToRemove = new ArrayList<>();
            Node node = findFirstInRange(root);
            while (node != null && isInRange(node.key)) {
                keysToRemove.add(node.key);
                node = findNextInOrder(node);
            }

            for (Integer key : keysToRemove) {
                MySplayMap.this.remove(key);
            }
        }
    }

    // Простой класс Entry для SubMap
    private static class SimpleEntry implements Entry<Integer, String> {
        private final Integer key;
        private String value;

        SimpleEntry(Integer key, String value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public Integer getKey() {
            return key;
        }

        @Override
        public String getValue() {
            return value;
        }

        @Override
        public String setValue(String value) {
            String oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Entry)) return false;
            Entry<?, ?> e = (Entry<?, ?>) o;
            return Objects.equals(key, e.getKey()) && Objects.equals(value, e.getValue());
        }

        @Override
        public int hashCode() {
            return (key == null ? 0 : key.hashCode()) ^ (value == null ? 0 : value.hashCode());
        }
    }

    /////////////////////////////////////////////////////////////////////////
    //////          Остальные методы NavigableMap (заглушки)         ///////
    /////////////////////////////////////////////////////////////////////////

    @Override
    public Entry<Integer, String> lowerEntry(Integer key) { return null; }

    @Override
    public Entry<Integer, String> floorEntry(Integer key) { return null; }

    @Override
    public Entry<Integer, String> ceilingEntry(Integer key) { return null; }

    @Override
    public Entry<Integer, String> higherEntry(Integer key) { return null; }

    @Override
    public Entry<Integer, String> firstEntry() { return null; }

    @Override
    public Entry<Integer, String> lastEntry() { return null; }

    @Override
    public Entry<Integer, String> pollFirstEntry() { return null; }

    @Override
    public Entry<Integer, String> pollLastEntry() { return null; }

    @Override
    public NavigableMap<Integer, String> descendingMap() { return null; }

    @Override
    public NavigableSet<Integer> navigableKeySet() { return null; }

    @Override
    public NavigableSet<Integer> descendingKeySet() { return null; }

    @Override
    public NavigableMap<Integer, String> subMap(Integer fromKey, boolean fromInclusive,
                                                Integer toKey, boolean toInclusive) { return null; }

    @Override
    public NavigableMap<Integer, String> headMap(Integer toKey, boolean inclusive) { return null; }

    @Override
    public NavigableMap<Integer, String> tailMap(Integer fromKey, boolean inclusive) { return null; }

    @Override
    public void putAll(Map<? extends Integer, ? extends String> m) { }

    @Override
    public Set<Integer> keySet() { return null; }

    @Override
    public Collection<String> values() { return null; }

    @Override
    public Set<Entry<Integer, String>> entrySet() { return null; }
}