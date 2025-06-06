package by.it.group410971.antonenko.lesson04;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;

public class C_GetInversions {

    public static void main(String[] args) throws FileNotFoundException {
        InputStream stream = C_GetInversions.class.getResourceAsStream("dataC.txt");
        C_GetInversions instance = new C_GetInversions();
        int result = instance.calc(stream);
        System.out.print(result);
    }

    int calc(InputStream stream) throws FileNotFoundException {
        Scanner scanner = new Scanner(stream);

        // Чтение размера массива
        int n = scanner.nextInt();
        int[] a = new int[n];

        // Чтение элементов массива
        for (int i = 0; i < n; i++) {
            a[i] = scanner.nextInt();
        }

        // Вызов модифицированной сортировки слиянием с подсчетом инверсий
        return mergeSortAndCount(a, 0, a.length - 1);
    }

    private int mergeSortAndCount(int[] array, int left, int right) {
        int count = 0;

        if (left < right) {
            int mid = (left + right) / 2;

            // Рекурсивный подсчет инверсий в левой и правой половинах
            count += mergeSortAndCount(array, left, mid);
            count += mergeSortAndCount(array, mid + 1, right);

            // Подсчет инверсий при слиянии половин
            count += mergeAndCount(array, left, mid, right);
        }

        return count;
    }

    private int mergeAndCount(int[] array, int left, int mid, int right) {
        // Размеры временных подмассивов
        int n1 = mid - left + 1;
        int n2 = right - mid;

        // Создаем временные массивы
        int[] leftArray = new int[n1];
        int[] rightArray = new int[n2];

        // Копируем данные во временные массивы
        System.arraycopy(array, left, leftArray, 0, n1);
        System.arraycopy(array, mid + 1, rightArray, 0, n2);

        // Индексы для слияния
        int i = 0, j = 0, k = left;
        int count = 0;

        // Слияние временных массивов обратно в основной
        while (i < n1 && j < n2) {
            if (leftArray[i] <= rightArray[j]) {
                array[k++] = leftArray[i++];
            } else {
                array[k++] = rightArray[j++];
                // Все оставшиеся элементы в leftArray будут больше rightArray[j]
                count += (n1 - i);
            }
        }

        // Копируем оставшиеся элементы leftArray (если есть)
        while (i < n1) {
            array[k++] = leftArray[i++];
        }

        // Копируем оставшиеся элементы rightArray (если есть)
        while (j < n2) {
            array[k++] = rightArray[j++];
        }

        return count;
    }
}