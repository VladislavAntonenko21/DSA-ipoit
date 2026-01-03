package by.it.group410971.antonenko.lesson13;

import java.util.*;

public class GraphC {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        scanner.close();

        // Разбираем граф
        Map<String, List<String>> graph = parseGraph(input);

        // Находим компоненты сильной связности
        List<List<String>> components = kosarajuSCC(graph);

        // Сортируем и выводим компоненты
        String result = formatComponents(components);
        System.out.print(result);
    }

    private static Map<String, List<String>> parseGraph(String input) {
        Map<String, List<String>> graph = new HashMap<>();

        // Убираем пробелы
        String cleanInput = input.replace(" ", "");
        String[] edges = cleanInput.split(",");

        for (String edge : edges) {
            String[] parts = edge.split("->");
            String from = parts[0];
            String to = parts[1];

            graph.computeIfAbsent(from, k -> new ArrayList<>()).add(to);
            graph.computeIfAbsent(to, k -> new ArrayList<>());
        }

        return graph;
    }

    // Алгоритм Косарайю
    private static List<List<String>> kosarajuSCC(Map<String, List<String>> graph) {
        Set<String> visited = new HashSet<>();
        Stack<String> stack = new Stack<>();

        // Первый проход DFS
        for (String vertex : graph.keySet()) {
            if (!visited.contains(vertex)) {
                dfsFirst(graph, vertex, visited, stack);
            }
        }

        // Транспонируем граф
        Map<String, List<String>> transposed = transposeGraph(graph);

        // Второй проход DFS
        visited.clear();
        List<List<String>> components = new ArrayList<>();

        while (!stack.isEmpty()) {
            String vertex = stack.pop();
            if (!visited.contains(vertex)) {
                List<String> component = new ArrayList<>();
                dfsSecond(transposed, vertex, visited, component);
                components.add(component);
            }
        }

        return components;
    }

    private static void dfsFirst(Map<String, List<String>> graph,
                                 String vertex,
                                 Set<String> visited,
                                 Stack<String> stack) {
        visited.add(vertex);

        for (String neighbor : graph.getOrDefault(vertex, new ArrayList<>())) {
            if (!visited.contains(neighbor)) {
                dfsFirst(graph, neighbor, visited, stack);
            }
        }

        stack.push(vertex);
    }

    private static Map<String, List<String>> transposeGraph(Map<String, List<String>> graph) {
        Map<String, List<String>> transposed = new HashMap<>();

        for (String vertex : graph.keySet()) {
            transposed.put(vertex, new ArrayList<>());
        }

        for (Map.Entry<String, List<String>> entry : graph.entrySet()) {
            String from = entry.getKey();
            for (String to : entry.getValue()) {
                transposed.get(to).add(from);
            }
        }

        return transposed;
    }

    private static void dfsSecond(Map<String, List<String>> graph,
                                  String vertex,
                                  Set<String> visited,
                                  List<String> component) {
        visited.add(vertex);
        component.add(vertex);

        for (String neighbor : graph.getOrDefault(vertex, new ArrayList<>())) {
            if (!visited.contains(neighbor)) {
                dfsSecond(graph, neighbor, visited, component);
            }
        }
    }

    // Сравнение строк с учетом чисел и букв
    private static int compareVertices(String a, String b) {
        // Пробуем сравнить как числа
        try {
            int numA = Integer.parseInt(a);
            int numB = Integer.parseInt(b);
            return Integer.compare(numA, numB);
        } catch (NumberFormatException e) {
            // Если не числа, сравниваем как строки
            return a.compareTo(b);
        }
    }

    // Форматирование компонент
    private static String formatComponents(List<List<String>> components) {
        // Сортируем вершины внутри каждой компоненты
        for (List<String> component : components) {
            component.sort((a, b) -> compareVertices(a, b));
        }

        // Алгоритм Косарайю дает компоненты в обратном топологическом порядке
        // Разворачиваем список для правильного порядка (истоки -> стоки)
        Collections.reverse(components);

        // Формируем результат
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < components.size(); i++) {
            for (String vertex : components.get(i)) {
                result.append(vertex);
            }
            if (i < components.size() - 1) {
                result.append("\n");
            }
        }

        return result.toString();
    }
    // Сортировка и форматирование компонент
    private static String sortAndFormatComponents(List<List<String>> components) {
        // Сортируем вершины внутри каждой компоненты
        for (List<String> component : components) {
            component.sort((a, b) -> compareVertices(a, b));
        }

        // Сортируем компоненты по нескольким критериям:
        // 1. По размеру (большие компоненты вперед)
        // 2. По первой вершине, если размеры равны
        components.sort((c1, c2) -> {
            int sizeCompare = Integer.compare(c2.size(), c1.size());
            if (sizeCompare != 0) return sizeCompare;

            // Сравниваем лексикографически по всем вершинам
            for (int i = 0; i < Math.min(c1.size(), c2.size()); i++) {
                int vertexCompare = compareVertices(c1.get(i), c2.get(i));
                if (vertexCompare != 0) return vertexCompare;
            }
            return Integer.compare(c1.size(), c2.size());
        });

        // Алгоритм Косарайю дает компоненты в обратном топологическом порядке
        // Разворачиваем список для правильного порядка (истоки -> стоки)
        Collections.reverse(components);

        // Формируем результат
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < components.size(); i++) {
            for (String vertex : components.get(i)) {
                result.append(vertex);
            }
            if (i < components.size() - 1) {
                result.append("\n");
            }
        }

        return result.toString();
    }
}