package by.it.group410971.antonenko.lesson11;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class MyLinkedHashSet<E> implements Set<E> {

    // Внутренний класс для узла хэш-таблицы
    private static class Node<E> {
        final E data;
        final int hash;
        Node<E> next; // для цепочки коллизий
        Node<E> before, after; // для поддержания порядка добавления

        Node(E data, int hash, Node<E> next) {
            this.data = data;
            this.hash = hash;
            this.next = next;
        }
    }

    // Массив бакетов
    private Node<E>[] table;

    // Голова и хвост двусвязного списка для порядка добавления
    private Node<E> head;
    private Node<E> tail;

    private int size;
    private static final float LOAD_FACTOR = 0.75f;
    private static final int INITIAL_CAPACITY = 16;
    private int threshold;

    @SuppressWarnings("unchecked")
    public MyLinkedHashSet() {
        table = (Node<E>[]) new Node[INITIAL_CAPACITY];
        threshold = (int)(INITIAL_CAPACITY * LOAD_FACTOR);
        size = 0;
        head = null;
        tail = null;
    }

    @SuppressWarnings("unchecked")
    public MyLinkedHashSet(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Illegal initial capacity: " + initialCapacity);
        }

        int capacity = 1;
        while (capacity < initialCapacity) {
            capacity <<= 1;
        }

        table = (Node<E>[]) new Node[capacity];
        threshold = (int)(capacity * LOAD_FACTOR);
        size = 0;
        head = null;
        tail = null;
    }

    private int hash(Object key) {
        if (key == null) return 0;
        int h = key.hashCode();
        return h ^ (h >>> 16);
    }

    private int indexFor(int hash, int length) {
        return hash & (length - 1);
    }

    // Добавление узла в конец списка порядка
    private void linkNodeLast(Node<E> node) {
        if (tail == null) {
            head = node;
        } else {
            tail.after = node;
            node.before = tail;
        }
        tail = node;
    }

    // Удаление узла из списка порядка
    private void unlinkNode(Node<E> node) {
        Node<E> before = node.before;
        Node<E> after = node.after;

        if (before == null) {
            head = after;
        } else {
            before.after = after;
            node.before = null;
        }

        if (after == null) {
            tail = before;
        } else {
            after.before = before;
            node.after = null;
        }
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        Node<E>[] oldTable = table;
        int oldCapacity = oldTable.length;
        int newCapacity = oldCapacity << 1;
        threshold = (int)(newCapacity * LOAD_FACTOR);

        Node<E>[] newTable = (Node<E>[]) new Node[newCapacity];

        // Перехеширование
        Node<E> current = head;
        while (current != null) {
            int newIndex = indexFor(current.hash, newCapacity);

            // Сохраняем ссылку на следующий узел в списке порядка
            Node<E> nextInOrder = current.after;

            // Вставляем в новую таблицу
            current.next = newTable[newIndex];
            newTable[newIndex] = current;

            current = nextInOrder;
        }

        table = newTable;
    }

    /////////////////////////////////////////////////////////////////////////
    //////               Обязательные к реализации методы             ///////
    /////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        if (size == 0) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("[");
        Node<E> current = head;
        boolean first = true;

        while (current != null) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(current.data);
            first = false;
            current = current.after;
        }

        sb.append("]");
        return sb.toString();
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void clear() {
        if (size > 0) {
            // Очищаем таблицу
            for (int i = 0; i < table.length; i++) {
                table[i] = null;
            }

            // Очищаем связи в списке порядка
            Node<E> current = head;
            while (current != null) {
                Node<E> next = current.after;
                current.before = null;
                current.after = null;
                current = next;
            }

            head = null;
            tail = null;
            size = 0;
        }
    }

    @Override
    public boolean contains(Object o) {
        if (o == null) {
            // Поиск null
            Node<E> node = table[0];
            while (node != null) {
                if (node.data == null) {
                    return true;
                }
                node = node.next;
            }
        } else {
            int hash = hash(o);
            int index = indexFor(hash, table.length);
            Node<E> node = table[index];
            while (node != null) {
                if (node.hash == hash && o.equals(node.data)) {
                    return true;
                }
                node = node.next;
            }
        }
        return false;
    }

    @Override
    public boolean add(E e) {
        if (e == null) {
            return addNull();
        }

        int hash = hash(e);
        int index = indexFor(hash, table.length);

        // Проверяем наличие элемента
        Node<E> node = table[index];
        while (node != null) {
            if (node.hash == hash && e.equals(node.data)) {
                return false;
            }
            node = node.next;
        }

        // Создаем новый узел
        Node<E> newNode = new Node<>(e, hash, table[index]);
        table[index] = newNode;

        // Добавляем в конец списка порядка
        linkNodeLast(newNode);
        size++;

        if (size > threshold) {
            resize();
        }

        return true;
    }

    private boolean addNull() {
        int index = 0;
        Node<E> node = table[index];

        while (node != null) {
            if (node.data == null) {
                return false;
            }
            node = node.next;
        }

        Node<E> newNode = new Node<>(null, 0, table[index]);
        table[index] = newNode;
        linkNodeLast(newNode);
        size++;

        if (size > threshold) {
            resize();
        }

        return true;
    }

    @Override
    public boolean remove(Object o) {
        if (o == null) {
            return removeNull();
        }

        int hash = hash(o);
        int index = indexFor(hash, table.length);

        Node<E> prev = null;
        Node<E> node = table[index];

        while (node != null) {
            if (node.hash == hash && o.equals(node.data)) {
                // Удаляем из цепочки коллизий
                if (prev == null) {
                    table[index] = node.next;
                } else {
                    prev.next = node.next;
                }

                // Удаляем из списка порядка
                unlinkNode(node);
                size--;
                return true;
            }
            prev = node;
            node = node.next;
        }

        return false;
    }

    private boolean removeNull() {
        int index = 0;
        Node<E> prev = null;
        Node<E> node = table[index];

        while (node != null) {
            if (node.data == null) {
                if (prev == null) {
                    table[index] = node.next;
                } else {
                    prev.next = node.next;
                }

                unlinkNode(node);
                size--;
                return true;
            }
            prev = node;
            node = node.next;
        }

        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object element : c) {
            if (!contains(element)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean modified = false;
        for (E element : c) {
            if (add(element)) {
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        if (c == null) {
            throw new NullPointerException();
        }

        boolean modified = false;

        // Проходим по списку порядка и удаляем элементы из коллекции
        Node<E> current = head;
        while (current != null) {
            Node<E> next = current.after; // Сохраняем ссылку на следующий

            if (c.contains(current.data)) {
                // Находим и удаляем узел из цепочки коллизий
                int hash = current.hash;
                int index = indexFor(hash, table.length);

                Node<E> prev = null;
                Node<E> node = table[index];

                while (node != null) {
                    if (node == current) {
                        if (prev == null) {
                            table[index] = node.next;
                        } else {
                            prev.next = node.next;
                        }
                        break;
                    }
                    prev = node;
                    node = node.next;
                }

                // Удаляем из списка порядка
                unlinkNode(current);
                size--;
                modified = true;
            }

            current = next;
        }

        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        if (c == null) {
            throw new NullPointerException();
        }

        boolean modified = false;

        // Проходим по списку порядка и удаляем элементы НЕ из коллекции
        Node<E> current = head;
        while (current != null) {
            Node<E> next = current.after; // Сохраняем ссылку на следующий

            if (!c.contains(current.data)) {
                // Находим и удаляем узел из цепочки коллизий
                int hash = current.hash;
                int index = indexFor(hash, table.length);

                Node<E> prev = null;
                Node<E> node = table[index];

                while (node != null) {
                    if (node == current) {
                        if (prev == null) {
                            table[index] = node.next;
                        } else {
                            prev.next = node.next;
                        }
                        break;
                    }
                    prev = node;
                    node = node.next;
                }

                // Удаляем из списка порядка
                unlinkNode(current);
                size--;
                modified = true;
            }

            current = next;
        }

        return modified;
    }

    /////////////////////////////////////////////////////////////////////////
    //////          Остальные методы интерфейса Set                   ///////
    /////////////////////////////////////////////////////////////////////////

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private Node<E> current = head;
            private Node<E> lastReturned = null;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public E next() {
                if (!hasNext()) {
                    throw new java.util.NoSuchElementException();
                }
                lastReturned = current;
                E data = current.data;
                current = current.after;
                return data;
            }

            @Override
            public void remove() {
                if (lastReturned == null) {
                    throw new IllegalStateException();
                }
                MyLinkedHashSet.this.remove(lastReturned.data);
                lastReturned = null;
            }
        };
    }

    @Override
    public Object[] toArray() {
        Object[] result = new Object[size];
        int index = 0;
        Node<E> current = head;

        while (current != null) {
            result[index++] = current.data;
            current = current.after;
        }

        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        if (a.length < size) {
            a = (T[]) java.lang.reflect.Array.newInstance(
                    a.getClass().getComponentType(), size);
        }

        int index = 0;
        Object[] result = a;
        Node<E> current = head;

        while (current != null) {
            result[index++] = current.data;
            current = current.after;
        }

        if (a.length > size) {
            a[size] = null;
        }

        return a;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Set)) {
            return false;
        }

        Set<?> other = (Set<?>) o;
        if (this.size() != other.size()) {
            return false;
        }

        return containsAll(other);
    }

    @Override
    public int hashCode() {
        int hashCode = 0;
        Node<E> current = head;
        while (current != null) {
            if (current.data != null) {
                hashCode += current.data.hashCode();
            }
            current = current.after;
        }
        return hashCode;
    }
}