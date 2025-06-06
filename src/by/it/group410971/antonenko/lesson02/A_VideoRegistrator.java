package by.it.group410971.antonenko.lesson02;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class A_VideoRegistrator {

    public static void main(String[] args) {
        A_VideoRegistrator instance = new A_VideoRegistrator();
        double[] events = new double[]{1, 1.1, 1.6, 2.2, 2.4, 2.7, 3.9, 8.1, 9.1, 5.5, 3.7};
        List<Double> starts = instance.calcStartTimes(events, 1);
        System.out.println(starts); // [1.0, 2.2, 3.7, 5.5, 8.1, 9.1]
    }

    List<Double> calcStartTimes(double[] events, double workDuration) {
        List<Double> result = new ArrayList<>();

        // Сортируем события по возрастанию
        Arrays.sort(events);

        int i = 0; // Индекс текущего события
        while (i < events.length) {
            // Запоминаем время старта (текущее событие)
            double startTime = events[i];
            result.add(startTime);

            // Вычисляем время окончания работы
            double endTime = startTime + workDuration;

            // Пропускаем все события, которые попадают в этот интервал
            while (i < events.length && events[i] <= endTime) {
                i++;
            }
        }

        return result;
    }
}