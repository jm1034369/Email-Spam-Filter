import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class DatasetProcessor {

    private String textFile;
    private String cleanedFile;
    private Email[] emails;
    public static String[] uniqueWords;
    public static String[] spamNGrams = {"click here","buy now","act now","limited time offer","click link", "dear customer","dear user","dear valued customer","open attachment","free access","link below"};
    public static String[] wordsToIgnore = {"a","the","and","to","is","url"};
    public static int largestWordSize;

    public DatasetProcessor(File textFile) throws FileNotFoundException {
        Scanner reader = new Scanner(textFile);
        String text = "";

        //remove first line of the text (email,label)
        reader.nextLine();
        while(reader.hasNextLine()) {
            text = text + reader.nextLine();
        }
        this.textFile = text;
        cleanedFile = clean(this.textFile);

        extractUniqueWords(cleanedFile);
        largestWordSize = 0;
        for(int i = 0; i < uniqueWords.length; i++) {
            if(largestWordSize < uniqueWords[i].length()) {
                largestWordSize = uniqueWords[i].length();
            }
        }
        extractEmails(cleanedFile);



    }

    public String clean(String text) {

        //convert all text to lower case
        String cleanedText = text.toLowerCase();

        //remove all "NUMBER" and "_" occurrences
        cleanedText = cleanedText.replaceAll("number ", "");
        cleanedText = cleanedText.replaceAll("number", " ");
        cleanedText = cleanedText.replaceAll("_", " ");

        //return the cleaned String of the text file
        return cleanedText;
    }

    public void extractEmails(String file) {

        String[] emailArr = file.split(",.");
        emails = new Email[emailArr.length];
        for(int i = 0; i < emailArr.length; i++) {
            if((emailArr[i].charAt(0) + "").equals(" ")) {
                emails[i] = new Email(i + 1, emailArr[i].substring(1));
            }
            else {
                emails[i] = new Email(i + 1, emailArr[i]);
            }

        }

    }

    public void extractUniqueWords(String file) {
        file = file.replaceAll(",.","");
        file = file.replaceAll("url", "");
        String[] totalWords = file.split(" +");
        ArrayList<String> uniqueWordsList = new ArrayList<>();
        uniqueWordsList.add(totalWords[0]);
        for(int i = 1; i < totalWords.length; i++) {
            if(!(uniqueWordsList.contains(totalWords[i]))) {
                uniqueWordsList.add(totalWords[i]);
            }
        }
        uniqueWordsList.remove("");
        ArrayList<String> stopWords = new ArrayList<>();
        for(int i = 0; i < wordsToIgnore.length; i++) {
            stopWords.add(wordsToIgnore[i]);
        }
        uniqueWordsList.removeAll(stopWords);
        uniqueWords = new String[uniqueWordsList.size()];
        for(int i = 0; i < uniqueWords.length; i++) {
            uniqueWords[i] = uniqueWordsList.get(i);
        }
        Arrays.sort(uniqueWords);
    }

    public String getTextFile() {
        return textFile;
    }
    public String getCleanedText() {
        return cleanedFile;
    }
    public String[] getUniqueWords() {
        return uniqueWords;
    }
    public Email[] getEmails() {
        return emails;
    }

}
