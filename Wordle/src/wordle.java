package wordle;

import java.io.*;
import java.util.*;

public class wordle {
//Como buscar archivo externo con informacion correcta
    public static void main(String[] args) {
        List<String> words = loadWordsFromFile("palabras.txt");
        if (words.isEmpty()) {
            System.out.println("Error: No se pudieron cargar las palabras.");
            return;
        }
        WordleGame game = new WordleGame(words);
        game.start();
    }

    private static List<String> loadWordsFromFile(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            return br.lines()
                    .filter(line -> line.length() == 5)
                    .map(String::toUpperCase)
                    .toList();
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}

class WordleGame {
//Declaracion de Variables fijas
    private static final int MAX_TRIES = 6;
    private static final int WORD_LENGTH = 5;
    private final List<String> fileWords;
    private final String secretWord;
    private int remainingAttempts;
    private final List<String> triesHistory;
    private final Scanner scanner;
    private final Set<Character> availableLetters;

    public WordleGame(List<String> words) {
        this.fileWords = new ArrayList<>(words);
        this.secretWord = selectRandomWord(this.fileWords);
        this.remainingAttempts = MAX_TRIES;
        this.triesHistory = new ArrayList<>();
        this.scanner = new Scanner(System.in);
        this.availableLetters = new HashSet<>();

        // Inicializar availableLetters con todas las letras del alfabeto
        for (char c = 'A'; c <= 'Z'; c++) {
            availableLetters.add(c);
        }
    }

    private String selectRandomWord(List<String> words) {// Elegir aleatorimente palabra del archivo
        Random random = new Random();
        return words.get(random.nextInt(words.size()));
    }

    public void start() {
        System.out.println(" Bienvenido a Wordle ");
        while (remainingAttempts > 0) {
            showTriesHistory();
            showAvailableLetters(); // Mostrar letras disponibles antes del intento

            String userGuess = getUserInput();
            triesHistory.add(userGuess);

            updateAvailableLetters(userGuess); // Actualizar letras disponibles

            if (userGuess.equals(secretWord)) {
                System.out.println("\n¡Felicidades! Has adivinado la palabra: " + secretWord);
                return;
            }

            System.out.println(WordleFeedback.feedBackString(userGuess, secretWord));
            remainingAttempts--;
        }
        System.out.println("\nHas perdido. La palabra secreta era: " + secretWord);
    }

    private void showTriesHistory() {// Numeros de intentos que te quedan 
        for (String attempt : triesHistory) {
            System.out.println("\n"+attempt);
        }
        System.out.println("\nTe quedan " + remainingAttempts + " intentos");
    }

    private void showAvailableLetters() {// Letras que te quedan del abecedario
        System.out.print("\nLetras disponibles: ");
        for (char i = 'A'; i <= 'Z'; i++) {
            if (availableLetters.contains(i)) {
                System.out.print(i + " ");
            }
        }
        System.out.println();
    }

    private void updateAvailableLetters(String guess) {
        for (char i : guess.toCharArray()) {
            if (!secretWord.contains(String.valueOf(i))) {
                availableLetters.remove(i); // Eliminar solo letras incorrectas
            }
        }
    }

    private String getUserInput() {// aqui es donde el usuario pone su respuesta mas asegurando tamano
        String input;
        do {
            System.out.print("Introduce una palabra de " + WORD_LENGTH + " letras: ");
            input = scanner.next().toUpperCase();
            if (input.length() != WORD_LENGTH) {
                System.out.println("Error: La palabra debe tener exactamente " + WORD_LENGTH + " letras.");
            }
        } while (input.length() != WORD_LENGTH);
        return input;
    }
}

class WordleFeedback {
// Declaracion de colores 
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";

    private static String applyColor(String letter, String color) {
        return color + letter + RESET;
    }

   public static String feedBackString(String guess, String secretWord){
    StringBuilder feedback = new StringBuilder();
    for (int i = 0; i < guess.length(); i++) {
        char guessedChar = guess.charAt(i);
        String guessedCharStr = Character.toString(guessedChar); // Convertir a String una sola vez
        if (guessedChar == secretWord.charAt(i)) {
            feedback.append(applyColor(guessedCharStr, GREEN)); // Letra correcta en posición correcta
        } else if (secretWord.contains(guessedCharStr)) {
            feedback.append(applyColor(guessedCharStr, YELLOW)); // Letra correcta en posición incorrecta
        } else {
            feedback.append(applyColor(guessedCharStr, RED)); // Letra incorrecta
        }
    }
    return feedback.toString();
}
}
