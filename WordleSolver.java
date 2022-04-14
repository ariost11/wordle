import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.io.*;

public class WordleSolver {
    public static void main(String args[]) throws FileNotFoundException, IOException {
        /*
        for(int i = 0; i < 1; i++) {
            String secret = secretWord();
            System.out.println("crane");
            System.out.println(findResults("crane", "agate"));
        }
        */
        realWordle();
    }

    public static void realWordle() throws FileNotFoundException, IOException {
        Scanner reader = new Scanner(System.in);
        double numPlayed = 0;
        double totalPoints = 0;
        while(!reader.nextLine().equals("end")) {
            numPlayed++;
            totalPoints += guessWord();
        }
        reader.close();
        System.out.println("Average number of guesses: " + (totalPoints / numPlayed));
    }

    public static void playWordle() throws FileNotFoundException, IOException {
        String secretWord = secretWord();

    }

    public static String secretWord() throws FileNotFoundException, IOException {
        ArrayList<String> possibleWords = new ArrayList<>();
        FileReader file = new FileReader("solutions.txt");
        BufferedReader br = new BufferedReader(file);
        String str = br.readLine();
        while (str != null) {
            possibleWords.add(str);
            str = br.readLine();
        }
        br.close();

        Random rand = new Random();
        return possibleWords.get(rand.nextInt(possibleWords.size()));
    }

    public static String findResults(String secretWord, String attempt) {
        StringBuilder results = new StringBuilder();
        String guess = attempt.toLowerCase();
        for(int i = 0; i < 5; i++) {
            System.out.println(secretWord.indexOf(guess.charAt(i)) + " " + guess.charAt(i));
            if(secretWord.indexOf(guess.charAt(i)) == -1) { //black
                //TODO: deal with duplicates
                results.append('b');
            } else if(secretWord.charAt(i) == guess.charAt(i)) { //green
                results.append('g');
            } else { //yellow
                results.append('y');
            }
        }
        return results.toString();
    }

    public static int guessWord() throws FileNotFoundException, IOException {
        Scanner reader = new Scanner(System.in);
        System.out.print("What was your first guess? ");
        String guess = reader.nextLine();
        //String guess = "crane";
        System.out.print("What were the results of the guess? ");
        String results = reader.nextLine();

        ArrayList<String> possibleWords = new ArrayList<>();
        FileReader file = new FileReader("solutions.txt");
        BufferedReader br = new BufferedReader(file);
        String str = br.readLine();
        while (str != null) {
            possibleWords.add(str);
            str = br.readLine();
        }
        br.close();

        ArrayList<String> remove = new ArrayList<>();
        int round = 1;
        while(!results.equals("ggggg")) {
            round++;
            for(int i = 0; i < possibleWords.size(); i++) {
                String word = possibleWords.get(i);
                for(int j = 0; j < 5; j++) {
                    if(results.charAt(j) == 'b') { //if the tile is black
                        char compare = guess.charAt(j); //the character at the position that is black
                        //check for duplicates
                        int guessDup = 0; //number of characters in guess with this letter
                        int wordDup = 0; //number of the character in the word
                        int numBlack = 0; //number of characters in guess that are black with this letter
                        for(int k = 0; k < 5; k++) {
                            if(compare == guess.charAt(k)) {
                                guessDup++;
                                if(results.charAt(k) == 'b')
                                    numBlack++;
                            }
                            if(compare == word.charAt(k)) {
                                wordDup++;
                            }
                        }
                        if(guessDup == 1) {
                            if(word.indexOf(compare) != -1 && !remove.contains(word)) {
                                remove.add(word);
                            }
                        } else {
                            if((guessDup <= wordDup && word.indexOf(compare) != -1 && !remove.contains(word)) || (guessDup == numBlack && word.indexOf(compare) != -1 && !remove.contains(word))) {
                                remove.add(word);
                            }
                        }
                    } else if(results.charAt(j) == 'y') { //if the tile is yellow
                        if(word.charAt(j) == guess.charAt(j) || word.indexOf(guess.charAt(j)) == -1 && !remove.contains(word)) {
                            remove.add(word);
                        }
                    } else { //if the tile is green
                        if(word.charAt(j) != guess.charAt(j) && !remove.contains(word)) { 
                            remove.add(word);
                        }
                    }
                }
            }
            for(String word : remove) {
                possibleWords.remove(word);
            }

            HashMap<Character, Integer> frequency = new HashMap<>();
            for(String word : possibleWords) {
                for(int i = 0; i < 5; i++) {
                    if(frequency.containsKey(word.charAt(i)))
                        frequency.put(word.charAt(i), frequency.get(word.charAt(i)) + 1);
                    else 
                        frequency.put(word.charAt(i), 1);
                }
            }

            ArrayList<Character> bestChars = new ArrayList<>();
            while(!frequency.isEmpty()) {
                Character cMin = null;
                Integer iMin = null;
                for(Character key : frequency.keySet()) {
                    if(cMin == null || frequency.get(key) < iMin) {
                        cMin = key;
                        iMin = frequency.get(key);
                    }
                }
                bestChars.add(cMin);
                frequency.remove(cMin);
            }

            HashMap<String, Integer> scores = new HashMap<>();
            for(String word : possibleWords) {
                int points = 0;
                for(Character ch : bestChars) {
                    if(word.indexOf(ch) != -1) {
                        points += bestChars.indexOf(ch);
                    }
                }
                scores.put(word, points);
            }

            String nextGuess = null;
            int max = (int)Double.NEGATIVE_INFINITY;
            for(String key : scores.keySet()) {
                if(nextGuess == null || scores.get(key) > max) {
                    max = scores.get(key);
                    nextGuess = key;
                }
            }

            //equal scoring words
            ArrayList<String> equalScores = new ArrayList<>();
            for(String key : scores.keySet()) {
                if(Math.abs(scores.get(key) - scores.get(nextGuess)) <= 3) {
                    equalScores.add(key);
                }
            }

            if(equalScores.size() > 1) {
                Random rand = new Random();
                nextGuess = equalScores.get(rand.nextInt(equalScores.size()));
            }

            if(possibleWords.size() <= 5 && possibleWords.size() > 1) {
                System.out.print("There are " + possibleWords.size() + " possible solutions left. The possible options are: ");
                for(String s : possibleWords) {
                    System.out.print(s + " ");
                }
                System.out.println();
                System.out.println("You should guess " + nextGuess + ".");
            } else if (possibleWords.size() == 1) {
                System.out.println("There is only 1 possible solution left! Guess " + possibleWords.get(0) + " to win!");
                //break;
            } else {
                System.out.println("There are " + possibleWords.size() + " possible solutions left. You should guess " + nextGuess + ".");
            }
            reader = new Scanner(System.in);
            System.out.print("What did you guess next? ");
            guess = reader.nextLine();
            System.out.print("What were the results of the guess? ");
            results = reader.nextLine();
        }
        return round;
    }

    public static String firstWord() throws FileNotFoundException, IOException {
        ArrayList<String> possibleWords = new ArrayList<>();
        FileReader file = new FileReader("solutions.txt");
        BufferedReader br = new BufferedReader(file);
        String str = br.readLine();
        while (str != null) {
            possibleWords.add(str);
            str = br.readLine();
        }
        br.close();

        HashMap<Character, Integer> frequency = new HashMap<>();
            for(String word : possibleWords) {
                for(int i = 0; i < 5; i++) {
                    if(frequency.containsKey(word.charAt(i)))
                        frequency.put(word.charAt(i), frequency.get(word.charAt(i)) + 1);
                    else 
                        frequency.put(word.charAt(i), 1);
                }
            }

            ArrayList<Character> bestChars = new ArrayList<>();
            while(!frequency.isEmpty()) {
                Character cMin = null;
                Integer iMin = null;
                for(Character key : frequency.keySet()) {
                    if(cMin == null || frequency.get(key) < iMin) {
                        cMin = key;
                        iMin = frequency.get(key);
                    }
                }
                bestChars.add(cMin);
                frequency.remove(cMin);
            }

            for(Character ch : bestChars) {
                System.out.println(ch);
            }

            HashMap<String, Integer> map = new HashMap<>();
            for(String word : possibleWords) {
                int points = 0;
                for(Character ch : bestChars) {
                    if(word.indexOf(ch) != -1) {
                        points += bestChars.indexOf(ch);
                    }
                }
                map.put(word, points);
            }

            String nextGuess = null;
            int max = (int)Double.NEGATIVE_INFINITY;
            for(String key : map.keySet()) {
                if(nextGuess == null || map.get(key) > max) {
                    max = map.get(key);
                    nextGuess = key;
                }
            }
        return nextGuess;
    }
}