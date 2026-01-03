package by.it.group410971.antonenko.lesson10;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;

public class MyPriorityQueue<E> implements Queue<E> {

    private static final int DEFAULT_INITIAL_CAPACITY = 11;
    private Object[] queue;
    private int size = 0;

    public MyPriorityQueue() {
        queue = new Object[DEFAULT_INITIAL_CAPACITY];
    }

    public MyPriorityQueue(Collection<? extends E> c) {
        if (c == null)
            throw new NullPointerException();
        initFromCollection(c);
    }

    private void initFromCollection(Collection<? extends E> c) {
        // Преобразуем коллекцию в массив
        queue = new Object[c.size()];
        int i = 0;
        for (E element : c) {
            queue[i++] = element;
        }
        size = c.size();
        heapify();
    }

    private void heapify() {
        for (int i = (size >>> 1) - 1; i >= 0; i--)
            siftDown(i, (E) queue[i]);
    }

    private void siftUp(int k, E x) {
        Comparable<? super E> key = (Comparable<? super E>) x;
        while (k > 0) {
            int parent = (k - 1) >>> 1;
            Object e = queue[parent];
            if (key.compareTo((E) e) >= 0)
                break;
            queue[k] = e;
            k = parent;
        }
        queue[k] = key;
    }

    private void siftDown(int k, E x) {
        Comparable<? super E> key = (Comparable<? super E>) x;
        int half = size >>> 1;
        while (k < half) {
            int child = (k << 1) + 1;
            Object c = queue[child];
            int right = child + 1;
            if (right < size &&
                    ((Comparable<? super E>) c).compareTo((E) queue[right]) > 0)
                c = queue[child = right];
            if (key.compareTo((E) c) <= 0)
                break;
            queue[k] = c;
            k = child;
        }
        queue[k] = key;
    }

    private void grow(int minCapacity) {
        int oldCapacity = queue.length;
        int newCapacity = oldCapacity + ((oldCapacity < 64) ?
                (oldCapacity + 2) :
                (oldCapacity >> 1));
        if (newCapacity - minCapacity < 0)
            newCapacity = minCapacity;

        // Создаем новый массив и копируем элементы
        Object[] newQueue = new Object[newCapacity];
        for (int i = 0; i < size; i++) {
            newQueue[i] = queue[i];
        }
        queue = newQueue;
    }

    @Override
    public String toString() {
        if (size == 0) return "[]";

        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < size; i++) {
            if (i > 0) sb.append(", ");
            sb.append(queue[i]);
        }
        sb.append(']');
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
    public boolean contains(Object o) {
        return indexOf(o) != -1;
    }

    private int indexOf(Object o) {
        if (o != null) {
            for (int i = 0; i < size; i++)
                if (o.equals(queue[i]))
                    return i;
        }
        return -1;
    }

    @Override
    public boolean add(E e) {
        return offer(e);
    }

    @Override
    public boolean offer(E e) {
        if (e == null)
            throw new NullPointerException();
        int i = size;
        if (i >= queue.length)
            grow(i + 1);
        size = i + 1;
        if (i == 0)
            queue[0] = e;
        else
            siftUp(i, e);
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public E peek() {
        return (size == 0) ? null : (E) queue[0];
    }

    @Override
    public E element() {
        E e = peek();
        if (e != null)
            return e;
        else
            throw new java.util.NoSuchElementException();
    }

    @Override
    @SuppressWarnings("unchecked")
    public E poll() {
        if (size == 0)
            return null;
        int s = --size;
        E result = (E) queue[0];
        E x = (E) queue[s];
        queue[s] = null;
        if (s != 0)
            siftDown(0, x);
        return result;
    }

    @Override
    public E remove() {
        E x = poll();
        if (x != null)
            return x;
        else
            throw new java.util.NoSuchElementException();
    }

    @Override
    public boolean remove(Object o) {
        int i = indexOf(o);
        if (i == -1)
            return false;
        else {
            removeAt(i);
            return true;
        }
    }

    @SuppressWarnings("unchecked")
    private void removeAt(int i) {
        int s = --size;
        if (s == i) {
            queue[i] = null;
        } else {
            E moved = (E) queue[s];
            queue[s] = null;
            siftDown(i, moved);
            if (queue[i] == moved) {
                siftUp(i, moved);
            }
        }
    }

    @Override
    public void clear() {
        for (int i = 0; i < size; i++)
            queue[i] = null;
        size = 0;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        if (c == null)
            throw new NullPointerException();
        if (c == this)
            throw new IllegalArgumentException();

        boolean modified = false;
        for (E e : c) {
            if (e == null)
                throw new NullPointerException();
            if (offer(e))
                modified = true;
        }
        return modified;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object e : c)
            if (!contains(e))
                return false;
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        if (c == null) throw new NullPointerException();
        if (c.isEmpty()) return false;

        boolean modified = false;
        // Создаем новый массив с элементами, которые нужно сохранить
        Object[] newQueue = new Object[queue.length];
        int newSize = 0;

        for (int i = 0; i < size; i++) {
            if (!c.contains(queue[i])) {
                newQueue[newSize++] = queue[i];
            } else {
                modified = true;
            }
        }

        // Заменяем старый массив новым
        queue = newQueue;
        size = newSize;

        // Перестраиваем кучу
        heapify();

        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        if (c == null) throw new NullPointerException();

        boolean modified = false;
        // Создаем новый массив с элементами, которые нужно сохранить
        Object[] newQueue = new Object[queue.length];
        int newSize = 0;

        for (int i = 0; i < size; i++) {
            if (c.contains(queue[i])) {
                newQueue[newSize++] = queue[i];
            } else {
                modified = true;
            }
        }

        // Заменяем старый массив новым
        queue = newQueue;
        size = newSize;

        // Перестраиваем кучу
        heapify();

        return modified;
    }

    @Override
    public Iterator<E> iterator() {
        return new Itr();
    }

    private class Itr implements Iterator<E> {
        private int cursor = 0;
        private int lastRet = -1;

        @Override
        public boolean hasNext() {
            return cursor < size;
        }

        @Override
        @SuppressWarnings("unchecked")
        public E next() {
            if (cursor >= size)
                throw new java.util.NoSuchElementException();
            lastRet = cursor;
            return (E) queue[cursor++];
        }

        @Override
        public void remove() {
            if (lastRet < 0)
                throw new IllegalStateException();

            removeAt(lastRet);
            cursor = lastRet;
            lastRet = -1;
        }
    }

    @Override
    public Object[] toArray() {
        Object[] result = new Object[size];
        for (int i = 0; i < size; i++) {
            result[i] = queue[i];
        }
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        if (a.length < size) {
            // Создаем новый массив нужного типа
            a = (T[]) java.lang.reflect.Array.newInstance(
                    a.getClass().getComponentType(), size);
        }

        for (int i = 0; i < size; i++) {
            a[i] = (T) queue[i];
        }

        if (a.length > size) {
            a[size] = null;
        }

        return a;
    }
}