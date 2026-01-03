package by.it.group410971.antonenko.lesson13;

import by.it.HomeWork;
import org.junit.Test;

@SuppressWarnings("NewClassNamingConvention")
public class Test_Part2_Lesson13 extends HomeWork {

    @Test
    public void testGraphA() {
        run("0 -> 1", true).include("0 1");
        run("0 -> 1, 1 -> 2", true).include("0 1 2");
        run("0 -> 2, 1 -> 2, 0 -> 1", true).include("0 1 2");
        run("0 -> 2, 1 -> 3, 2 -> 3, 0 -> 1", true).include("0 1 2 3");
        run("1 -> 3, 2 -> 3, 2 -> 3, 0 -> 1, 0 -> 2", true).include("0 1 2 3");
        run("0 -> 1, 0 -> 2, 0 -> 2, 1 -> 3, 1 -> 3, 2 -> 3", true).include("0 1 2 3");
        run("A -> B, A -> C, B -> D, C -> D", true).include("A B C D");
        run("A -> B, A -> C, B -> D, C -> D, A -> D", true).include("A B C D");
        //Дополните эти тесты СВОИМИ более сложными примерами и проверьте их работоспособность.
        //Параметр метода run - это ввод. Параметр метода include - это вывод.
        //Общее число примеров должно быть не менее 20 (сейчас их 8).
    }

    @Test
    public void testGraphB() {
        run("0 -> 1", true).include("no").exclude("yes");
        run("0 -> 1, 1 -> 2", true).include("no").exclude("yes");
        run("0 -> 1, 1 -> 2, 2 -> 0", true).include("yes").exclude("no");
        //Дополните эти тесты СВОИМИ более сложными примерами и проверьте их работоспособность.
        //Параметр метода run - это ввод. Параметр метода include - это вывод.
        //Общее число примеров должно быть не менее 12 (сейчас их 3).
    }

    @Test
    public void testGraphC() {
        run("1->2, 2->3, 3->1, 3->4, 4->5, 5->6, 6->4", true)
                .include("123\n456");
        run("C->B, C->I, I->A, A->D, D->I, D->B, B->H, H->D, D->E, H->E, E->G, A->F, G->F, F->K, K->G", true)
                .include("C\nABDHI\nE\nFGK");

        // Дополнительные тесты

        // Тест 3: Граф с одной компонентой (все вершины в одном цикле)
        run("A->B, B->C, C->D, D->A", true)
                .include("ABCD");

        // Тест 4: Граф с тремя компонентами (цепочка компонент)
        run("A->B, B->A, B->C, C->D, D->C", true)
                .include("AB\nCD");

        // Тест 5: Граф с изолированными вершинами
        run("A->B, C->D, E->F, F->E", true)
                .include("AB\nCD\nEF");

        // Тест 6: Более сложный граф с несколькими компонентами
        run("1->2, 2->3, 3->1, 4->5, 5->4, 3->4, 6->7, 7->8, 8->6", true)
                .include("123\n45\n678");

        // Тест 7: Граф с вершинами в разных регистрах
        run("a->b, b->c, c->a, c->d, d->e, e->f, f->d", true)
                .include("abc\ndef");

        // Тест 8: Большой граф со сложной структурой
        run("1->2, 2->3, 3->4, 4->1, 2->5, 5->6, 6->7, 7->5, 4->8, 8->9, 9->10, 10->8", true)
                .include("1234\n567\n8910");
    }


}