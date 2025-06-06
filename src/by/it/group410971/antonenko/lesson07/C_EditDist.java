package by.it.group410971.antonenko.lesson07;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;

public class C_EditDist {

    String getDistanceEdinting(String one, String two) {
        int n = one.length();
        int m = two.length();

        int[][] dp = new int[n + 1][m + 1];
        String[][] op = new String[n + 1][m + 1]; // Храним операции

        // Инициализация
        for (int i = 0; i <= n; i++) {
            dp[i][0] = i;
            op[i][0] = (i == 0) ? "" : op[i - 1][0] + "-" + one.charAt(i - 1) + ",";
        }
        for (int j = 0; j <= m; j++) {
            dp[0][j] = j;
            op[0][j] = (j == 0) ? "" : op[0][j - 1] + "+" + two.charAt(j - 1) + ",";
        }

        // Основной цикл
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                char a = one.charAt(i - 1);
                char b = two.charAt(j - 1);

                // Варианты операций
                int costReplace = dp[i - 1][j - 1] + (a == b ? 0 : 1);
                int costInsert = dp[i][j - 1] + 1;
                int costDelete = dp[i - 1][j] + 1;

                dp[i][j] = Math.min(costReplace, Math.min(costInsert, costDelete));

                if (dp[i][j] == costReplace) {
                    op[i][j] = op[i - 1][j - 1] + (a == b ? "#," : "~" + b + ",");
                } else if (dp[i][j] == costInsert) {
                    op[i][j] = op[i][j - 1] + "+" + b + ",";
                } else {
                    op[i][j] = op[i - 1][j] + "-" + a + ",";
                }
            }
        }

        return op[n][m];
    }

    public static void main(String[] args) throws FileNotFoundException {
        InputStream stream = C_EditDist.class.getResourceAsStream("dataABC.txt");
        C_EditDist instance = new C_EditDist();
        Scanner scanner = new Scanner(stream);
        System.out.println(instance.getDistanceEdinting(scanner.nextLine(), scanner.nextLine()));
        System.out.println(instance.getDistanceEdinting(scanner.nextLine(), scanner.nextLine()));
        System.out.println(instance.getDistanceEdinting(scanner.nextLine(), scanner.nextLine()));
    }
}
