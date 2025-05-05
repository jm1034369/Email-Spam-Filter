import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

// This object orchestrations the reading of email files and dividing it up into individual emails for their features to be extracted
public class DatasetProcessor {

    private String textFile;
    private String cleanedFile;
    private Email[] emails;
    private String featuresFileName;
    private String summaryFileName;
    // uniqueWords is a String[] that holds all of the unique words that occur through all of the emails (useful for csv file output)
    public static String[] uniqueWords;
    public static String[] spamNGrams = {"click here","buy now","act now","limited time offer","click link", "dear customer","dear user","dear valued customer","open attachment","free access","link below","get rich quick","as seen on"};
    public static String[] wordsToIgnore = {"a","the","and","to","is","url","an","of","in","for","through","it","their","i","at","by","me","or","as","he","his","she","her","us","who","hers","him"};
    public static int largestWordSize;

    public DatasetProcessor(File textFile, String featuresFileName, String summaryFileName) throws FileNotFoundException {
        // Reads the email file; converts into a string of text
        Scanner reader = new Scanner(textFile);
        this.featuresFileName = featuresFileName;
        this.summaryFileName = summaryFileName;
        String text = "";
        reader.nextLine();
        while(reader.hasNextLine()) {
            text = text + reader.nextLine();
        }

        this.textFile = text;

        // Cleans the text file using the clean() method
        cleanedFile = clean(this.textFile);

        // Initializes other DatasetProcessor fields
        extractUniqueWords(cleanedFile);
        extractLargestWordSize();
        extractEmails(cleanedFile);

        // Writes the information gathered from the emails to both a csv file of features, and a summary csv file
        write();

    }

    // Cleans the text
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

    // Creates each email and adds it to the Email[] emails
    public void extractEmails(String file) {

        // Splits the file by comma
        String[] emailsArr = file.split(",+");
        emails = new Email[emailsArr.length];

        // Gets the first character of the next email to get the email type (Spam or Ham), then creates each email and adds it to the array
        for(int i = 0; i < emailsArr.length; i++) {
            int type;
            if(i < emails.length - 1 && (!(emailsArr[i + 1].isEmpty())) && !emailsArr[i].isEmpty() && emailsArr[i + 1].charAt(0) >= 48 && emailsArr[i + 1].charAt(0) <= 49) {
                type = Integer.parseInt(emailsArr[i + 1].charAt(0) + "");
            }
            else {
                type = 1;
            }
            String currentEmail;
            if(!emailsArr[i].isEmpty()) {
                currentEmail = emailsArr[i].substring(1);
            }
            else {
                currentEmail = emailsArr[i];
            }
            // Creates and adds the emails to the array
            if(!currentEmail.isEmpty() && (currentEmail.charAt(0) + "").equals(" ")) {
                emails[i] = new Email(i + 1, type, currentEmail.substring(1));
            }
            else {
                emails[i] = new Email(i + 1, type, currentEmail);
            }
        }
    }

    // Gets the unique words from all of the emails
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

    // Initializes the largestWordSize variable by returning the length of the largest word in uniqueWords
    public void extractLargestWordSize() {
        int largest = 0;
        for(int i = 0; i < uniqueWords.length; i++) {
            if(largest < uniqueWords[i].length()) {
                largest = uniqueWords[i].length();
            }
        }
        largestWordSize = largest;
    }

    // Creates the string of the file header; useful so we don't have to copy and paste this code for each file we write to
    public String getFileHeader() {
        StringBuilder header = new StringBuilder();
        header.append("URL Count,Special Character Word Count");
        for(int i = 0; i < spamNGrams.length; i++) {
            header.append("," + spamNGrams[i]);
        }
        for(int i = 0; i < uniqueWords.length; i++) {
            boolean containsSpecialCharacter = false;
            for (int j = 0; j < uniqueWords[i].length(); j++) {
                if (uniqueWords[i].charAt(j) > 127) {
                    containsSpecialCharacter = true;
                    break;
                }
            }
            if(!containsSpecialCharacter) {
                header.append("," + uniqueWords[i]);
            }
        }
        return header.toString();
    }

    // Writes to an email features csv file and possibly to a summary csv file if a name for a summary file is passed into DatasetProcessor
    public void write() throws FileNotFoundException {
        File emailFeatures = new File(featuresFileName);
        PrintWriter out = new PrintWriter(emailFeatures);

        out.print("Email ID,");
        out.println(getFileHeader());

        for(Email email : emails) {
            out.print(email.getEmailID() + ",");
            out.print(email.getFeatures().getUrlCount() + ",");
            out.print(email.getFeatures().getSpecialCharacterWordCount());
            for(int i = 0; i < spamNGrams.length; i++) {
                out.print("," + email.getFeatures().getNGramFrequency()[i][1]);
            }
            for(String word : uniqueWords) {
                boolean containsSpecialCharacter = false;
                for (int j = 0; j < word.length(); j++) {
                    if (word.charAt(j) > 127) {
                        containsSpecialCharacter = true;
                        break;
                    }
                }
                if(!containsSpecialCharacter) {
                    out.print("," + email.getFeatures().getWordFrequency().getOrDefault(word,0));
                }
            }
            out.println();
        }
        out.close();

        if(summaryFileName != null) {

            File emailSummary = new File(summaryFileName);
            PrintWriter out1 = new PrintWriter(emailSummary);

            out1.println(getFileHeader());
            out1.println(extractSummary(emailFeatures));
            out1.close();
        }
    }

    // Extracts the string for the summary for each feature by getting the median of the data for each feature; useful for writing to the summary file
    public String extractSummary(File emailFeatures) throws FileNotFoundException {

        StringBuilder summary = new StringBuilder();
        Scanner reader = new Scanner(emailFeatures);

        ArrayList<HashMap<String,Integer>> totalFeatures = new ArrayList<>();

        String[] tempKeys = reader.nextLine().split(",");
        ArrayList<String> keys = new ArrayList<>();
        for(int i = 0; i < tempKeys.length; i++) {
            keys.add(tempKeys[i]);
        }
        keys.remove(0);

        while(reader.hasNextLine()) {
            String[] tempValues = reader.nextLine().split(",");
            ArrayList<Integer> values = new ArrayList<>();
            for(int i = 0; i < tempValues.length; i++) {
                values.add(Integer.parseInt(tempValues[i]));
            }
            values.remove(0);

            HashMap<String, Integer> features = new HashMap<>();
            for(int i = 0; i < values.size(); i++) {
                features.put(keys.get(i),values.get(i));
            }
            totalFeatures.add(features);
        }

        Set<String> keySetTemp = totalFeatures.get(0).keySet();
        String[] keySet = keySetTemp.toArray(new String[0]);
        ArrayList<Double> averages = new ArrayList<>();
        for(int i = 0; i < keySet.length; i++) {
            double[] values = new double[totalFeatures.size()];
            for(int j = 0; j < totalFeatures.size(); j++) {

                values[j] = totalFeatures.get(j).get(keySet[i]);

            }
            //averages.add(sum / totalFeatures.size());
            Arrays.sort(values);
            int n = values.length;
            if (n % 2 == 0) {
                averages.add((values[n / 2 - 1] + values[n / 2]) / 2.0);
            } else {
                averages.add(values[n / 2]);
            }
        }
        for(int i = 0; i < averages.size(); i++) {
            if(i < averages.size() - 1) {
                summary.append(averages.get(i) + ",");
            }
            else {
                summary.append(averages.get(i));
            }
        }
        return summary.toString();
    }

    // Getters for DatasetProcessor fields
    public String getTextFile() {
        return textFile;
    }
    public String getCleanedText() {
        return cleanedFile;
    }
    public String[] getUniqueWords() {
        return uniqueWords;
    }
    public int getLargestWordSize() {
        return largestWordSize;
    }
    public Email[] getEmails() {
        return emails;
    }

}
