package by.it.group410971.antonenko.lesson08;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;

public class A_Knapsack {

    int getMaxWeight(InputStream stream) {
        Scanner scanner = new Scanner(stream);
        int w = scanner.nextInt(); // вместимость рюкзака
        int n = scanner.nextInt(); // количество слитков
        int[] gold = new int[n];
        for (int i = 0; i < n; i++) {
            gold[i] = scanner.nextInt(); // веса слитков
        }

        // массив для динамики: dp[i] — максимальный вес, который можно набрать с весом i
        int[] dp = new int[w + 1];

        for (int i = 0; i <= w; i++) {
            for (int g : gold) {
                if (g <= i) {
                    dp[i] = Math.max(dp[i], dp[i - g] + g);
                }
            }
        }

        return dp[w]; // максимальный вес при полной вместимости
    }

    public static void main(String[] args) throws FileNotFoundException {
        InputStream stream = A_Knapsack.class.getResourceAsStream("dataA.txt");
        A_Knapsack instance = new A_Knapsack();
        int res = instance.getMaxWeight(stream);
        System.out.println(res);
    }
}
