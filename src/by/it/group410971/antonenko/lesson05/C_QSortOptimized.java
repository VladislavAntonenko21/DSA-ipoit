package by.it.group410971.antonenko.lesson05;

import java.io.InputStream;
import java.util.Scanner;

public class C_QSortOptimized {

    public static void main(String[] args) {
        InputStream stream = C_QSortOptimized.class.getResourceAsStream("dataC.txt");
        C_QSortOptimized instance = new C_QSortOptimized();
        int[] result = instance.getAccessory2(stream);
        for (int index : result) {
            System.out.print(index + " ");
        }
    }

    int[] getAccessory2(InputStream stream) {
        Scanner scanner = new Scanner(stream);
        int n = scanner.nextInt();
        Segment[] segments = new Segment[n];
        int m = scanner.nextInt();
        int[] points = new int[m];
        int[] result = new int[m];

        for (int i = 0; i < n; i++) {
            segments[i] = new Segment(scanner.nextInt(), scanner.nextInt());
        }

        for (int i = 0; i < m; i++) {
            points[i] = scanner.nextInt();
        }

        // Сортировка отрезков по началу и затем по концу
        quickSort(segments, 0, segments.length - 1);

        // Подсчет количества отрезков, содержащих каждую точку
        for (int i = 0; i < m; i++) {
            result[i] = countCovers(segments, points[i]);
        }

        return result;
    }

    // Быстрая сортировка с 3-разбиением и хвостовой рекурсией
    void quickSort(Segment[] a, int low, int high) {
        while (low < high) {
            int lt = low, gt = high;
            Segment pivot = a[low];
            int i = low + 1;

            while (i <= gt) {
                int cmp = a[i].compareTo(pivot);
                if (cmp < 0) swap(a, lt++, i++);
                else if (cmp > 0) swap(a, i, gt--);
                else i++;
            }

            // Хвостовая рекурсия
            if ((lt - low) < (high - gt)) {
                quickSort(a, low, lt - 1);
                low = gt + 1;
            } else {
                quickSort(a, gt + 1, high);
                high = lt - 1;
            }
        }
    }

    void swap(Segment[] a, int i, int j) {
        Segment tmp = a[i];
        a[i] = a[j];
        a[j] = tmp;
    }

    // Подсчет количества отрезков, покрывающих точку
    int countCovers(Segment[] segments, int point) {
        int left = 0, right = segments.length - 1;
        int lastValid = -1;

        // Найдем последний сегмент, у которого start <= point
        while (left <= right) {
            int mid = (left + right) / 2;
            if (segments[mid].start <= point) {
                lastValid = mid;
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        // Подсчет всех таких, где stop >= point
        int count = 0;
        for (int i = 0; i <= lastValid; i++) {
            if (segments[i].stop >= point) {
                count++;
            }
        }

        return count;
    }

    // Отрезок с компаратором
    private class Segment implements Comparable<Segment> {
        int start;
        int stop;

        Segment(int start, int stop) {
            this.start = start;
            this.stop = stop;
        }

        @Override
        public int compareTo(Segment other) {
            if (this.start != other.start)
                return Integer.compare(this.start, other.start);
            else
                return Integer.compare(this.stop, other.stop);
        }
    }
}
