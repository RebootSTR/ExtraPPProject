import java.util.*;

/**
 * @author Aydar Rafikov
 */
public class Main {
    public static void main(String[] args) {
        // Проверка корректной взаимозаменяемости
        System.out.println("Вывод Моего листа");
        doSomething(new MyLinkedList<>());
        System.out.println("Вывод Array листа");
        doSomething(new ArrayList<>());

        System.out.println("Проверка сортировки");
        sortCheck();
    }

    public static void doSomething(List<String> list) {
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        list.add("5");
        printList(list);
        list.indexOf("3");
        list.remove("5");
        printList(list);
        list.addAll(Arrays.asList("99","88","77","66","55","44"));
        printList(list);
        System.out.println(list.get(1));
        System.out.println(list.contains("4"));
        printList(list.subList(2, 4));
        list.clear();
        System.out.println(list.size());
        System.out.println("----------");
    }

    public static void sortCheck() {
        MyLinkedList<Integer> list = new MyLinkedList<>();
        list.add(5);
        list.add(0);
        list.add(7);
        list.add(2);
        list.add(4);
        list.add(19);
        printList(list);
        Comparator<Integer> comparator = Comparator.comparingInt((x) -> x);
        list.sort(comparator);
        printList(list);
    }

    public static void printList(List<?> list) {
        list.forEach((x)->System.out.print(x+" "));
        System.out.println();
    }
}
