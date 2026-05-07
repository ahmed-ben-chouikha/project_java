package edu.connexion3a36.rankup.ui.components;

import java.util.*;

/**
 * Generates random quiz challenges for reCAPTCHA verification
 * Includes multiple choice, text input, and image grid selection
 */
public class RecaptchaQuiz {

    public enum QuizType {
        MULTIPLE_CHOICE,
        TEXT_INPUT,
        GRID_SELECT
    }

    private QuizType type;
    private String question;
    private List<String> options;
    private String correctAnswer;
    private List<String> imageLabels;
    private List<String> correctImages;

    public RecaptchaQuiz() {
        // Randomly select quiz type
        QuizType[] types = QuizType.values();
        this.type = types[new Random().nextInt(types.length)];

        // Generate appropriate quiz based on type
        switch (this.type) {
            case MULTIPLE_CHOICE:
                generateMultipleChoiceQuiz();
                break;
            case TEXT_INPUT:
                generateTextInputQuiz();
                break;
            case GRID_SELECT:
                generateGridSelectQuiz();
                break;
        }
    }

    /**
     * Generate multiple choice quiz
     */
    private void generateMultipleChoiceQuiz() {
        String[] questions = {
            "What is the capital of France?",
            "Which planet is closest to the Sun?",
            "What is 7 + 5?",
            "Who wrote 'Romeo and Juliet'?",
            "What is the largest ocean on Earth?",
            "What color is the sky on a clear day?",
            "How many sides does a triangle have?",
            "What is the chemical symbol for Gold?",
            "In what year did World War II end?",
            "What is the smallest prime number?"
        };

        Map<String, List<String>> qAndA = new HashMap<>();
        qAndA.put("What is the capital of France?", Arrays.asList("Paris", "London", "Berlin", "Madrid"));
        qAndA.put("Which planet is closest to the Sun?", Arrays.asList("Mercury", "Venus", "Earth", "Mars"));
        qAndA.put("What is 7 + 5?", Arrays.asList("12", "11", "13", "10"));
        qAndA.put("Who wrote 'Romeo and Juliet'?", Arrays.asList("Shakespeare", "Marlowe", "Jonson", "Bacon"));
        qAndA.put("What is the largest ocean on Earth?", Arrays.asList("Pacific", "Atlantic", "Indian", "Arctic"));
        qAndA.put("What color is the sky on a clear day?", Arrays.asList("Blue", "Red", "Yellow", "Green"));
        qAndA.put("How many sides does a triangle have?", Arrays.asList("3", "4", "5", "6"));
        qAndA.put("What is the chemical symbol for Gold?", Arrays.asList("Au", "Ag", "Fe", "Cu"));
        qAndA.put("In what year did World War II end?", Arrays.asList("1945", "1944", "1946", "1943"));
        qAndA.put("What is the smallest prime number?", Arrays.asList("2", "1", "3", "0"));

        Map<String, String> answers = new HashMap<>();
        answers.put("What is the capital of France?", "Paris");
        answers.put("Which planet is closest to the Sun?", "Mercury");
        answers.put("What is 7 + 5?", "12");
        answers.put("Who wrote 'Romeo and Juliet'?", "Shakespeare");
        answers.put("What is the largest ocean on Earth?", "Pacific");
        answers.put("What color is the sky on a clear day?", "Blue");
        answers.put("How many sides does a triangle have?", "3");
        answers.put("What is the chemical symbol for Gold?", "Au");
        answers.put("In what year did World War II end?", "1945");
        answers.put("What is the smallest prime number?", "2");

        String selectedQuestion = questions[new Random().nextInt(questions.length)];
        this.question = selectedQuestion;

        // Shuffle options
        List<String> opts = new ArrayList<>(qAndA.get(selectedQuestion));
        Collections.shuffle(opts);
        this.options = opts;
        this.correctAnswer = answers.get(selectedQuestion);
    }

    /**
     * Generate text input quiz
     */
    private void generateTextInputQuiz() {
        Map<String, String> qAndA = new LinkedHashMap<>();
        qAndA.put("What is the color of an apple (commonly)?", "red");
        qAndA.put("How many fingers do humans have?", "10");
        qAndA.put("What animal barks?", "dog");
        qAndA.put("What is H2O?", "water");
        qAndA.put("What metal is liquid at room temperature?", "mercury");
        qAndA.put("How many legs does a spider have?", "8");
        qAndA.put("What gas do plants absorb?", "carbon dioxide");
        qAndA.put("What is the opposite of hot?", "cold");
        qAndA.put("How many strings does a guitar have?", "6");
        qAndA.put("What fruit is yellow?", "banana");

        int randomIndex = new Random().nextInt(qAndA.size());
        String selectedQuestion = (String) qAndA.keySet().toArray()[randomIndex];

        this.question = selectedQuestion;
        this.correctAnswer = qAndA.get(selectedQuestion);
    }

    /**
     * Generate grid selection quiz (image selection)
     */
    private void generateGridSelectQuiz() {
        Map<String, List<String>> imageQuizzes = new LinkedHashMap<>();

        imageQuizzes.put("Select all images with cars:",
            Arrays.asList("🚗", "🐱", "🚗", "🌳", "🚗", "🏠", "🌲", "🚗", "🌼"));

        imageQuizzes.put("Select all images with trees:",
            Arrays.asList("🚗", "🌳", "🐱", "🌳", "🏠", "🌳", "🌼", "🐕", "🌳"));

        imageQuizzes.put("Select all images with animals:",
            Arrays.asList("🐱", "🌳", "🐕", "🚗", "🦁", "🏠", "🐘", "🌼", "🐦"));

        imageQuizzes.put("Select all images with flowers:",
            Arrays.asList("🌼", "🚗", "🌻", "🐱", "🌷", "🏠", "🌹", "🌳", "🌸"));

        int randomIndex = new Random().nextInt(imageQuizzes.size());
        String selectedQuestion = (String) imageQuizzes.keySet().toArray()[randomIndex];

        this.question = selectedQuestion;
        List<String> images = imageQuizzes.get(selectedQuestion);
        this.imageLabels = images;

        // Determine correct answers based on question
        this.correctImages = new ArrayList<>();
        if (selectedQuestion.contains("cars")) {
            for (String img : images) {
                if (img.equals("🚗")) {
                    correctImages.add(img);
                }
            }
        } else if (selectedQuestion.contains("trees")) {
            for (String img : images) {
                if (img.equals("🌳")) {
                    correctImages.add(img);
                }
            }
        } else if (selectedQuestion.contains("animals")) {
            for (String img : images) {
                if (img.matches("[🐱🐕🦁🐘🐦]")) {
                    correctImages.add(img);
                }
            }
        } else if (selectedQuestion.contains("flowers")) {
            for (String img : images) {
                if (img.matches("[🌼🌻🌷🌹🌸]")) {
                    correctImages.add(img);
                }
            }
        }
    }

    /**
     * Validate text input answer (case-insensitive)
     */
    public boolean validateAnswer(String answer) {
        return answer.trim().equalsIgnoreCase(correctAnswer);
    }

    /**
     * Validate image selection
     */
    public boolean validateImageSelection(List<String> selected) {
        return selected.size() == correctImages.size() &&
               selected.containsAll(correctImages);
    }

    /**
     * Get quiz type
     */
    public QuizType getType() {
        return type;
    }

    /**
     * Get quiz question
     */
    public String getQuestion() {
        return question;
    }

    /**
     * Get options for multiple choice
     */
    public List<String> getOptions() {
        return options;
    }

    /**
     * Get image labels for grid selection
     */
    public List<String> getImageLabels() {
        return imageLabels;
    }
}

