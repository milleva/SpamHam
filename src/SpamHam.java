package spamham;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class SpamHam {
    private static int totalHamWordCount;
    private static int totalSpamWordCount;
    private final static String SPAM_PATH = "spamcount.txt";
    private final static String HAM_PATH  = "hamcount.txt";
    private final static String MESSAGE_PATH = "hamesim.txt";

    private static void initializeTotalWordCounts()throws FileNotFoundException{
        Scanner hamLukija = new Scanner(new File(HAM_PATH));
        Scanner spamLukija = new Scanner(new File(SPAM_PATH));
        int hamWordCount = 0;
        int spamWordCount = 0;
        while (hamLukija.hasNext()) {
            int occurences = hamLukija.nextInt();
            String word = hamLukija.next();
            hamWordCount += occurences;
        }
        while (spamLukija.hasNext()) {
            int occurences = spamLukija.nextInt();
            String word = spamLukija.next();
            spamWordCount += occurences;
        }
        totalHamWordCount = hamWordCount;
        totalSpamWordCount = spamWordCount;
    }

    private static List<String> readMessage(String file) throws IOException {
        String teksti = new String(Files.readAllBytes(Paths.get(file)));
        return Arrays.asList(teksti.split("\\s+"));
    }

    private static Map<String, Integer> readOccurences(String file)
            throws FileNotFoundException {
        Map<String, Integer> counts;
        try (Scanner lukija = new Scanner(new File(file))) {
            counts = new HashMap<>();
            while (lukija.hasNext()) {
                int occurences = lukija.nextInt();
                String word = lukija.next();
                counts.put(word, occurences);
            }
        }

        return counts;
    }

    private static Map<String, Double> spamProbabilities(Map<String, Integer> spam){//P(word = w | spam)
        Map<String, Double> probabilities = new HashMap<>();
        for(String word : spam.keySet()){
            probabilities.put(word, (1.0*spam.get(word)/totalSpamWordCount));
        }
        return probabilities;
    }
    private static Map<String, Double> hamProbabilities(Map<String, Integer> ham){//P(word = w | spam)
        Map<String, Double> probabilities = new HashMap<>();
        for(String word : ham.keySet()){
            probabilities.put(word, (1.0*ham.get(word)/totalHamWordCount));
        }
        return probabilities;
    }

    private static double returnProbability(String word, Map<String, Double> probabilities){
        if(probabilities.keySet().contains(word)) return probabilities.get(word);
        return 0.0000001;
    }

    private static double spamicity(String messagePath, Map<String, Double> hamProbabilities,
                                   Map<String, Double> spamProbabilities) throws IOException {
        List<String> words = readMessage(MESSAGE_PATH);
        double logR = Math.log(1);//P(spam)/P(ham) = 1 cuz p(spam)=p(ham) ////=0
        for(String word : words){
            double increment = Math.log(returnProbability(word, spamProbabilities))-
                    Math.log(returnProbability(word, hamProbabilities));
            logR += increment;
        }
        double R = Math.exp(logR);

        return R / (R+1);
    }



    public static void main(String[] args) throws FileNotFoundException, IOException {
        Map<String, Integer> spamOccurrences = readOccurences(SPAM_PATH);
        Map<String, Integer> hamOccurrences = readOccurences(HAM_PATH);
        initializeTotalWordCounts();


        Map<String, Double> hamProbabilities = hamProbabilities(hamOccurrences);
        Map<String, Double> spamProbabilities = spamProbabilities(spamOccurrences);

        System.out.println(spamicity(MESSAGE_PATH, hamProbabilities, spamProbabilities));

    }
}
