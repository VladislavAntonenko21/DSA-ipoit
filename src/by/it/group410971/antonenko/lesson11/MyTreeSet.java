package by.it.group410971.antonenko.lesson11;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

public class MyTreeSet<E> implements Set<E> {

    private Object[] elements;
    private int size;
    private static final int INITIAL_CAPACITY = 10;
    private static final int GROW_FACTOR = 2;

    // Компаратор для сравнения элементов
    @SuppressWarnings("unchecked")
    private final Comparator<? super E> comparator;

    // Конструктор по умолчанию (использует естественный порядок)
    @SuppressWarnings("unchecked")
    public MyTreeSet() {
        elements = new Object[INITIAL_CAPACITY];
        size = 0;
        comparator = (Comparator<? super E>) Comparator.naturalOrder();
    }

    // Конструктор с компаратором
    public MyTreeSet(Comparator<? super E> comparator) {
        elements = new Object[INITIAL_CAPACITY];
        size = 0;
        this.comparator = comparator;
    }

    // Вспомогательный метод для увеличения емкости
    private void ensureCapacity(int minCapacity) {
        if (minCapacity > elements.length) {
            int newCapacity = elements.length * GROW_FACTOR;
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }
            Object[] newElements = new Object[newCapacity];
            for (int i = 0; i < size; i++) {
                newElements[i] = elements[i];
            }
            elements = newElements;
        }
    }

    // Бинарный поиск - возвращает индекс элемента или позицию для вставки
    @SuppressWarnings("unchecked")
    private int binarySearch(Object key) {
        int low = 0;
        int high = size - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            E midVal = (E) elements[mid];
            int cmp = compare((E) key, midVal);

            if (cmp < 0) {
                high = mid - 1;
            } else if (cmp > 0) {
                low = mid + 1;
            } else {
                return mid; // ключ найден
            }
        }
        return -(low + 1); // ключ не найден, возвращаем позицию для вставки
    }

    // Сравнение элементов с использованием компаратора или Comparable
    @SuppressWarnings("unchecked")
    private int compare(E e1, E e2) {
        if (comparator != null) {
            return comparator.compare(e1, e2);
        } else {
            return ((Comparable<? super E>) e1).compareTo(e2);
        }
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
        for (int i = 0; i < size; i++) {
            sb.append(elements[i]);
            if (i < size - 1) {
                sb.append(", ");
            }
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
        for (int i = 0; i < size; i++) {
            elements[i] = null;
        }
        size = 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean contains(Object o) {
        if (o == null) {
            throw new NullPointerException("TreeSet does not permit null elements");
        }

        int index = binarySearch(o);
        return index >= 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean add(E e) {
        if (e == null) {
            throw new NullPointerException("TreeSet does not permit null elements");
        }

        // Проверяем, есть ли уже такой элемент
        int index = binarySearch(e);
        if (index >= 0) {
            return false; // элемент уже существует
        }

        // Вычисляем позицию для вставки
        int insertionPoint = -(index + 1);

        // Увеличиваем емкость если нужно
        ensureCapacity(size + 1);

        // Сдвигаем элементы вправо, чтобы освободить место
        for (int i = size; i > insertionPoint; i--) {
            elements[i] = elements[i - 1];
        }

        // Вставляем новый элемент
        elements[insertionPoint] = e;
        size++;

        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean remove(Object o) {
        if (o == null) {
            throw new NullPointerException("TreeSet does not permit null elements");
        }

        int index = binarySearch(o);
        if (index < 0) {
            return false; // элемент не найден
        }

        // Сдвигаем элементы влево
        for (int i = index; i < size - 1; i++) {
            elements[i] = elements[i + 1];
        }

        // Очищаем последний элемент
        elements[size - 1] = null;
        size--;

        return true;
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
        if (c == null) {
            throw new NullPointerException("Collection cannot be null");
        }

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
            throw new NullPointerException("Collection cannot be null");
        }

        boolean modified = false;

        // Эффективный алгоритм удаления всех элементов коллекции
        // Создаем новый массив только с элементами, которые нужно сохранить
        Object[] newElements = new Object[elements.length];
        int newSize = 0;

        for (int i = 0; i < size; i++) {
            if (!c.contains(elements[i])) {
                newElements[newSize++] = elements[i];
            } else {
                modified = true;
            }
        }

        elements = newElements;
        size = newSize;

        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        if (c == null) {
            throw new NullPointerException("Collection cannot be null");
        }

        boolean modified = false;

        // Создаем новый массив только с элементами, которые нужно сохранить
        Object[] newElements = new Object[elements.length];
        int newSize = 0;

        for (int i = 0; i < size; i++) {
            if (c.contains(elements[i])) {
                newElements[newSize++] = elements[i];
            } else {
                modified = true;
            }
        }

        elements = newElements;
        size = newSize;

        return modified;
    }

    /////////////////////////////////////////////////////////////////////////
    //////          Остальные методы интерфейса Set                   ///////
    /////////////////////////////////////////////////////////////////////////

    @Override
    public Iterator<E> iterator() {
        return new TreeSetIterator();
    }

    private class TreeSetIterator implements Iterator<E> {
        private int cursor = 0;
        private int lastReturned = -1;

        @Override
        public boolean hasNext() {
            return cursor < size;
        }

        @Override
        @SuppressWarnings("unchecked")
        public E next() {
            if (!hasNext()) {
                throw new java.util.NoSuchElementException();
            }
            lastReturned = cursor;
            return (E) elements[cursor++];
        }

        @Override
        public void remove() {
            if (lastReturned == -1) {
                throw new IllegalStateException();
            }

            MyTreeSet.this.remove(elements[lastReturned]);
            cursor = lastReturned;
            lastReturned = -1;
        }
    }

    @Override
    public Object[] toArray() {
        Object[] result = new Object[size];
        for (int i = 0; i < size; i++) {
            result[i] = elements[i];
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

        for (int i = 0; i < size; i++) {
            a[i] = (T) elements[i];
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
        for (int i = 0; i < size; i++) {
            if (elements[i] != null) {
                hashCode += elements[i].hashCode();
            }
        }
        return hashCode;
    }

    /////////////////////////////////////////////////////////////////////////
    //////          Дополнительные методы (не обязательные)          ///////
    /////////////////////////////////////////////////////////////////////////

    // Получение первого (наименьшего) элемента
    @SuppressWarnings("unchecked")
    public E first() {
        if (size == 0) {
            throw new java.util.NoSuchElementException();
        }
        return (E) elements[0];
    }

    // Получение последнего (наибольшего) элемента
    @SuppressWarnings("unchecked")
    public E last() {
        if (size == 0) {
            throw new java.util.NoSuchElementException();
        }
        return (E) elements[size - 1];
    }

    // Найти наименьший элемент, больший или равный заданному
    @SuppressWarnings("unchecked")
    public E ceiling(E e) {
        if (e == null) {
            throw new NullPointerException();
        }

        int index = binarySearch(e);
        if (index >= 0) {
            // Элемент найден
            return (E) elements[index];
        } else {
            // Элемент не найден, вычисляем позицию вставки
            int insertionPoint = -(index + 1);
            if (insertionPoint < size) {
                return (E) elements[insertionPoint];
            }
        }
        return null; // нет такого элемента
    }

    // Найти наибольший элемент, меньший или равный заданному
    @SuppressWarnings("unchecked")
    public E floor(E e) {
        if (e == null) {
            throw new NullPointerException();
        }

        int index = binarySearch(e);
        if (index >= 0) {
            // Элемент найден
            return (E) elements[index];
        } else {
            // Элемент не найден, вычисляем позицию вставки
            int insertionPoint = -(index + 1);
            if (insertionPoint > 0) {
                return (E) elements[insertionPoint - 1];
            }
        }
        return null; // нет такого элемента
    }
}