package by.it.group410971.antonenko.lesson09;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ListB<E> implements List<E> {

    // Внутренний массив для хранения элементов
    private Object[] elements;

    // Текущее количество элементов в списке
    private int size;

    // Начальная емкость
    private static final int INITIAL_CAPACITY = 10;

    // Конструктор
    public ListB() {
        elements = new Object[INITIAL_CAPACITY];
        size = 0;
    }

    // Вспомогательный метод для увеличения емкости
    private void ensureCapacity(int minCapacity) {
        if (minCapacity > elements.length) {
            int newCapacity = elements.length * 2;
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }
            Object[] newElements = new Object[newCapacity];
            // Копируем старые элементы в новый массив
            for (int i = 0; i < size; i++) {
                newElements[i] = elements[i];
            }
            elements = newElements;
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
    public boolean add(E e) {
        // Увеличиваем емкость если нужно
        ensureCapacity(size + 1);
        // Добавляем элемент в конец
        elements[size] = e;
        size++;
        return true; // Всегда возвращаем true для List
    }

    @Override
    public E remove(int index) {
        // Проверяем валидность индекса
        checkIndex(index);

        // Сохраняем удаляемый элемент
        @SuppressWarnings("unchecked")
        E removedElement = (E) elements[index];

        // Сдвигаем все элементы после index влево
        for (int i = index; i < size - 1; i++) {
            elements[i] = elements[i + 1];
        }

        // Очищаем последнюю позицию и уменьшаем size
        elements[size - 1] = null;
        size--;

        return removedElement;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void add(int index, E element) {
        // Проверяем валидность индекса (можно добавлять в конец, поэтому size допустим)
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        // Увеличиваем емкость если нужно
        ensureCapacity(size + 1);

        // Сдвигаем элементы вправо начиная с index
        for (int i = size; i > index; i--) {
            elements[i] = elements[i - 1];
        }

        // Вставляем новый элемент
        elements[index] = element;
        size++;
    }

    @Override
    public boolean remove(Object o) {
        // Ищем индекс элемента
        int index = indexOf(o);
        if (index == -1) {
            return false; // Элемент не найден
        }

        // Удаляем по индексу
        remove(index);
        return true;
    }

    @Override
    public E set(int index, E element) {
        // Проверяем валидность индекса
        checkIndex(index);

        // Сохраняем старый элемент
        @SuppressWarnings("unchecked")
        E oldElement = (E) elements[index];

        // Заменяем элемент
        elements[index] = element;

        return oldElement;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void clear() {
        // Очищаем все ссылки для помощи сборщику мусора
        for (int i = 0; i < size; i++) {
            elements[i] = null;
        }
        size = 0;
    }

    @Override
    public int indexOf(Object o) {
        // Обрабатываем null
        if (o == null) {
            for (int i = 0; i < size; i++) {
                if (elements[i] == null) {
                    return i;
                }
            }
        } else {
            for (int i = 0; i < size; i++) {
                if (o.equals(elements[i])) {
                    return i;
                }
            }
        }
        return -1; // Не найден
    }

    @Override
    public E get(int index) {
        // Проверяем валидность индекса
        checkIndex(index);

        @SuppressWarnings("unchecked")
        E element = (E) elements[index];
        return element;
    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o) != -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        // Обрабатываем null
        if (o == null) {
            for (int i = size - 1; i >= 0; i--) {
                if (elements[i] == null) {
                    return i;
                }
            }
        } else {
            for (int i = size - 1; i >= 0; i--) {
                if (o.equals(elements[i])) {
                    return i;
                }
            }
        }
        return -1; // Не найден
    }

    /////////////////////////////////////////////////////////////////////////
    //////               Опциональные к реализации методы             ///////
    /////////////////////////////////////////////////////////////////////////

    @Override
    public boolean containsAll(Collection<?> c) {
        // Для каждого элемента коллекции проверяем, содержится ли он в нашем списке
        for (Object item : c) {
            if (!contains(item)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        if (c.isEmpty()) {
            return false;
        }

        // Увеличиваем емкость если нужно
        ensureCapacity(size + c.size());

        // Добавляем все элементы из коллекции
        for (E element : c) {
            elements[size] = element;
            size++;
        }

        return true;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        // Проверяем валидность индекса
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        if (c.isEmpty()) {
            return false;
        }

        // Увеличиваем емкость если нужно
        ensureCapacity(size + c.size());

        // Сдвигаем существующие элементы вправо
        int shift = c.size();
        for (int i = size - 1; i >= index; i--) {
            elements[i + shift] = elements[i];
        }

        // Вставляем новые элементы
        int i = index;
        for (E element : c) {
            elements[i] = element;
            i++;
        }

        size += c.size();
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        if (c.isEmpty()) {
            return false;
        }

        boolean modified = false;
        // Проходим по списку и удаляем элементы, которые есть в коллекции
        for (int i = size - 1; i >= 0; i--) {
            if (c.contains(elements[i])) {
                remove(i);
                modified = true;
            }
        }

        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean modified = false;
        // Проходим по списку и удаляем элементы, которых нет в коллекции
        for (int i = size - 1; i >= 0; i--) {
            if (!c.contains(elements[i])) {
                remove(i);
                modified = true;
            }
        }

        return modified;
    }

    // Вспомогательный метод для проверки индекса
    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }

    // Остальные методы (можно оставить заглушками или реализовать)

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        // Проверяем индексы
        if (fromIndex < 0 || toIndex > size || fromIndex > toIndex) {
            throw new IndexOutOfBoundsException();
        }

        ListB<E> subList = new ListB<>();
        // Увеличиваем емкость подсписка
        subList.ensureCapacity(toIndex - fromIndex);

        // Копируем элементы
        for (int i = fromIndex; i < toIndex; i++) {
            @SuppressWarnings("unchecked")
            E element = (E) elements[i];
            subList.add(element);
        }

        return subList;
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        // Упрощенная реализация - создаем новый список и возвращаем его итератор
        // В реальности нужно было бы создать собственный ListIterator
        throw new UnsupportedOperationException("listIterator not implemented");
    }

    @Override
    public ListIterator<E> listIterator() {
        return listIterator(0);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        if (a.length < size) {
            // Создаем новый массив нужного типа и размера
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
    public Object[] toArray() {
        Object[] result = new Object[size];
        for (int i = 0; i < size; i++) {
            result[i] = elements[i];
        }
        return result;
    }

    @Override
    public Iterator<E> iterator() {
        // Простая реализация итератора
        return new Iterator<E>() {
            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < size;
            }

            @Override
            @SuppressWarnings("unchecked")
            public E next() {
                if (!hasNext()) {
                    throw new java.util.NoSuchElementException();
                }
                return (E) elements[currentIndex++];
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("remove not supported");
            }
        };
    }
}