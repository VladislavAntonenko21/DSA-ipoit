package by.it.group410971.antonenko.lesson08;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;

public class B_Knapsack {

    int getMaxWeight(InputStream stream) {
        Scanner scanner = new Scanner(stream);
        int w = scanner.nextInt(); // вместимость рюкзака
        int n = scanner.nextInt(); // количество слитков
        int[] gold = new int[n];
        for (int i = 0; i < n; i++) {
            gold[i] = scanner.nextInt(); // веса слитков
        }

        // dp[i] — максимальный вес при вместимости i
        int[] dp = new int[w + 1];

        // 0/1 рюкзак — каждый предмет можно использовать только один раз
        for (int i = 0; i < n; i++) {
            for (int j = w; j >= gold[i]; j--) {
                dp[j] = Math.max(dp[j], dp[j - gold[i]] + gold[i]);
            }
        }

        return dp[w]; // максимальный вес при полной вместимости
    }

    public static void main(String[] args) throws FileNotFoundException {
        InputStream stream = B_Knapsack.class.getResourceAsStream("dataB.txt");
        B_Knapsack instance = new B_Knapsack();
        int res = instance.getMaxWeight(stream);
        System.out.println(res);
    }
}
