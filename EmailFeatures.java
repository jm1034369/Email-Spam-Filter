import java.util.HashMap;

// This object is responsible for extracting the features of a single email
public class EmailFeatures {

    private String[] words;

    private int urlCount;
    private int specialCharacterWordCount;
    private String[][] nGramFrequency;
    private HashMap<String, Integer> wordFrequency;

    // Constructor for EmailFeatures; initializes each value
    public EmailFeatures(String[] words) {

        this.words = words;

        urlCount = extractUrlCount();
        wordFrequency = extractWordFrequency();
        nGramFrequency = extractNGramFrequency(DatasetProcessor.spamNGrams);
        specialCharacterWordCount = extractSpecialCharacterWordCount();

    }

    // Extracts the url count
    public int extractUrlCount() {
        int count = 0;
        for(int i = 0; i < words.length; i++) {
            if(words[i].equals("url")) {
                count++;
            }
        }
        return count;
    }

    // Extracts the word frequency in the format of a HashMap
    public HashMap<String, Integer> extractWordFrequency() {
        HashMap<String, Integer> wordFreq = new HashMap<>();
        for(String word : words) {
            wordFreq.put(word, (wordFreq.getOrDefault(word, 0) + 1));
        }
        return wordFreq;
    }

    // Extracts the spam n-gram frequency in the format of a 2D String array
    public String[][] extractNGramFrequency(String[] nGrams) {
        String[][] nGramFrequency = new String[nGrams.length][2];
        for(int i = 0; i < nGrams.length; i++) {
            nGramFrequency[i][0] = nGrams[i];
            String[] nGram = nGrams[i].split(" ");
            int count = 0;
            for(int j = 0; j < words.length; j++) {
                boolean exists = false;
                if(nGram[0].equals(words[j])) {
                    exists = true;
                    if(nGram.length + j > words.length) {
                        break;
                    }
                    for(int k = 1; k < nGram.length; k++) {
                        if(!(nGram[k].equals(words[j + k]))) {
                            exists = false;
                            j = j + k + 1;
                            break;
                        }
                    }
                }
                if(exists == true) {
                    count++;
                    j = j + nGram.length;
                }
            }
            nGramFrequency[i][1] = count + "";
        }
        return nGramFrequency;
    }

    // Extracts the number of words that contain a special character
    public int extractSpecialCharacterWordCount() {
        int count = 0;
        for(int i = 0; i < words.length; i++) {
            for (int j = 0; j < words[i].length(); j++) {
                if (words[i].charAt(j) > 127) {
                    count++;
                    break;
                }
            }
        }
        return count;
    }

    // Getters for each individual email feature
    public int getUrlCount() {
        return urlCount;
    }
    public int getSpecialCharacterWordCount() {
        return specialCharacterWordCount;
    }
    public HashMap<String, Integer> getWordFrequency() {
        return wordFrequency;
    }
    public String[][] getNGramFrequency() {
        return nGramFrequency;
    }
}
