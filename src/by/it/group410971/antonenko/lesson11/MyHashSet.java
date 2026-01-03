package by.it.group410971.antonenko.lesson11;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class MyHashSet<E> implements Set<E> {

    // Внутренний класс для узла односвязного списка
    private static class Node<E> {
        final E data;
        final int hash;
        Node<E> next;

        Node(E data, int hash, Node<E> next) {
            this.data = data;
            this.hash = hash;
            this.next = next;
        }
    }

    // Массив бакетов (корзин)
    private Node<E>[] table;

    // Количество элементов в множестве
    private int size;

    // Коэффициент загрузки
    private static final float LOAD_FACTOR = 0.75f;

    // Начальная емкость (степень двойки для лучшего распределения)
    private static final int INITIAL_CAPACITY = 16;

    // Порог для увеличения размера
    private int threshold;

    // Конструктор
    @SuppressWarnings("unchecked")
    public MyHashSet() {
        table = (Node<E>[]) new Node[INITIAL_CAPACITY];
        threshold = (int)(INITIAL_CAPACITY * LOAD_FACTOR);
        size = 0;
    }

    // Конструктор с начальной емкостью
    @SuppressWarnings("unchecked")
    public MyHashSet(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Illegal initial capacity: " + initialCapacity);
        }

        // Находим ближайшую степень двойки >= initialCapacity
        int capacity = 1;
        while (capacity < initialCapacity) {
            capacity <<= 1;
        }

        table = (Node<E>[]) new Node[capacity];
        threshold = (int)(capacity * LOAD_FACTOR);
        size = 0;
    }

    // Вычисление хэш-кода
    private int hash(Object key) {
        if (key == null) {
            return 0;
        }
        int h = key.hashCode();
        // Дополнительное перемешивание для уменьшения коллизий
        return h ^ (h >>> 16);
    }

    // Получение индекса в таблице
    private int indexFor(int hash, int length) {
        return hash & (length - 1);
    }

    // Увеличение размера таблицы
    @SuppressWarnings("unchecked")
    private void resize() {
        Node<E>[] oldTable = table;
        int oldCapacity = oldTable.length;
        int newCapacity = oldCapacity << 1; // Удваиваем
        threshold = (int)(newCapacity * LOAD_FACTOR);

        Node<E>[] newTable = (Node<E>[]) new Node[newCapacity];

        // Перехеширование всех элементов
        for (int i = 0; i < oldCapacity; i++) {
            Node<E> node = oldTable[i];
            while (node != null) {
                Node<E> next = node.next;
                int newIndex = indexFor(node.hash, newCapacity);
                node.next = newTable[newIndex];
                newTable[newIndex] = node;
                node = next;
            }
        }

        table = newTable;
    }

    /////////////////////////////////////////////////////////////////////////
    //////               Обязательные к реализации методы             ///////
    /////////////////////////////////////////////////////////////////////////

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
            for (int i = 0; i < table.length; i++) {
                table[i] = null;
            }
            size = 0;
        }
    }

    @Override
    public boolean contains(Object o) {
        if (o == null) {
            // Ищем null в бакете с индексом 0
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

        // Проверяем, нет ли уже такого элемента
        Node<E> node = table[index];
        while (node != null) {
            if (node.hash == hash && e.equals(node.data)) {
                return false; // Элемент уже существует
            }
            node = node.next;
        }

        // Добавляем новый элемент в начало списка
        table[index] = new Node<>(e, hash, table[index]);
        size++;

        // Проверяем, не нужно ли увеличить размер таблицы
        if (size > threshold) {
            resize();
        }

        return true;
    }

    // Отдельный метод для добавления null
    private boolean addNull() {
        // null всегда хранится в бакете 0
        int index = 0;
        Node<E> node = table[index];

        // Проверяем, нет ли уже null
        while (node != null) {
            if (node.data == null) {
                return false; // null уже существует
            }
            node = node.next;
        }

        // Добавляем null
        table[index] = new Node<>(null, 0, table[index]);
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
                // Нашли элемент для удаления
                if (prev == null) {
                    // Удаляем первый элемент в списке
                    table[index] = node.next;
                } else {
                    prev.next = node.next;
                }
                size--;
                return true;
            }
            prev = node;
            node = node.next;
        }

        return false;
    }

    // Отдельный метод для удаления null
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
                size--;
                return true;
            }
            prev = node;
            node = node.next;
        }

        return false;
    }

    @Override
    public String toString() {
        if (size == 0) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("[");
        boolean first = true;

        // Обходим все бакеты
        for (int i = 0; i < table.length; i++) {
            Node<E> node = table[i];
            while (node != null) {
                if (!first) {
                    sb.append(", ");
                }
                sb.append(node.data);
                first = false;
                node = node.next;
            }
        }

        sb.append("]");
        return sb.toString();
    }

    /////////////////////////////////////////////////////////////////////////
    //////          Остальные методы интерфейса Set (заглушки)       ///////
    /////////////////////////////////////////////////////////////////////////

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private int bucketIndex = 0;
            private Node<E> currentNode = null;
            private Node<E> nextNode = findNextNode();

            private Node<E> findNextNode() {
                // Если есть следующий элемент в текущем списке
                if (currentNode != null && currentNode.next != null) {
                    return currentNode.next;
                }

                // Ищем следующий непустой бакет
                for (int i = bucketIndex; i < table.length; i++) {
                    if (table[i] != null) {
                        bucketIndex = i + 1;
                        return table[i];
                    }
                }

                return null;
            }

            @Override
            public boolean hasNext() {
                return nextNode != null;
            }

            @Override
            public E next() {
                if (!hasNext()) {
                    throw new java.util.NoSuchElementException();
                }
                currentNode = nextNode;
                nextNode = findNextNode();
                return currentNode.data;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("remove not supported");
            }
        };
    }

    @Override
    public Object[] toArray() {
        Object[] result = new Object[size];
        int index = 0;

        for (int i = 0; i < table.length; i++) {
            Node<E> node = table[i];
            while (node != null) {
                result[index++] = node.data;
                node = node.next;
            }
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

        for (int i = 0; i < table.length; i++) {
            Node<E> node = table[i];
            while (node != null) {
                result[index++] = node.data;
                node = node.next;
            }
        }

        if (a.length > size) {
            a[size] = null;
        }

        return a;
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
    public boolean retainAll(Collection<?> c) {
        boolean modified = false;
        Iterator<E> it = iterator();
        while (it.hasNext()) {
            if (!c.contains(it.next())) {
                it.remove();
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean modified = false;
        for (Object element : c) {
            if (remove(element)) {
                modified = true;
            }
        }
        return modified;
    }

    // Метод remove для итератора
    private void removeNode(Node<E> nodeToRemove) {
        if (nodeToRemove == null) {
            return;
        }

        int hash = nodeToRemove.hash;
        int index = indexFor(hash, table.length);

        Node<E> prev = null;
        Node<E> node = table[index];

        while (node != null) {
            if (node == nodeToRemove) {
                if (prev == null) {
                    table[index] = node.next;
                } else {
                    prev.next = node.next;
                }
                size--;
                return;
            }
            prev = node;
            node = node.next;
        }
    }
}