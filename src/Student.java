public class Student {
    private int id;
    private String nom;
    private int age;

    // Constructeur sans paramètres
    public Student() {}

    // Constructeur avec paramètres
    public Student(int id, String nom, int age) {
        this.id = id;
        this.nom = nom;
        this.age = age;
    }

    // Getters & Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    // Redéfinition toString
    @Override
    public String toString() {
        return "Student{id=" + id + ", nom='" + nom + "', age=" + age + "}";
    }
}
