import java.util.Comparator;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        StudentManagement sm = new StudentManagement();

        List<Student> list = List.of(
                new Student(3, "Malek", 21),
                new Student(1, "Sara", 20),
                new Student(2, "Yassine", 22)
        );

        sm.displayStudents(list, s -> System.out.println(s));

        sm.displayStudentsByFilter(list, s -> s.getAge() > 20, s -> System.out.println("Filtré: " + s));

        System.out.println(sm.returnStudentsNames(list, Student::getNom));

        sm.sortStudentsById(list, Comparator.comparingInt(Student::getId))
                .forEach(System.out::println);
    }
}
