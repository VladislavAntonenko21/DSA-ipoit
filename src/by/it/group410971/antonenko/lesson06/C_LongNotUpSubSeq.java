package by.it.group410971.antonenko.lesson06;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

public class C_LongNotUpSubSeq {

    public static void main(String[] args) throws FileNotFoundException {
        InputStream stream = C_LongNotUpSubSeq.class.getResourceAsStream("dataC.txt");
        C_LongNotUpSubSeq instance = new C_LongNotUpSubSeq();
        int result = instance.getNotUpSeqSize(stream);
        System.out.print(result);
    }

    int getNotUpSeqSize(InputStream stream) throws FileNotFoundException {
        Scanner scanner = new Scanner(stream);

        int n = scanner.nextInt();
        int[] a = new int[n];
        for (int i = 0; i < n; i++) {
            a[i] = scanner.nextInt();
        }

        // dp[i] — длина НВП (невозрастающей подпоследовательности), заканчивающейся в a[i]
        int[] dp = new int[n];
        // prev[i] — индекс предыдущего элемента в НВП для восстановления пути
        int[] prev = new int[n];
        Arrays.fill(dp, 1);
        Arrays.fill(prev, -1);

        int maxLen = 1;
        int lastIndex = 0;

        for (int i = 1; i < n; i++) {
            for (int j = 0; j < i; j++) {
                if (a[j] >= a[i] && dp[j] + 1 > dp[i]) {
                    dp[i] = dp[j] + 1;
                    prev[i] = j;
                }
            }
            if (dp[i] > maxLen) {
                maxLen = dp[i];
                lastIndex = i;
            }
        }

        // Восстановление пути
        List<Integer> indices = new ArrayList<>();
        while (lastIndex != -1) {
            indices.add(lastIndex + 1); // +1 для перехода к индексации с 1
            lastIndex = prev[lastIndex];
        }
        Collections.reverse(indices);

        // Вывод
        System.out.println(maxLen);
        for (int idx : indices) {
            System.out.print(idx + " ");
        }
        System.out.println();

        return maxLen;
    }
}
