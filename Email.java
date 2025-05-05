import java.util.ArrayList;
import java.util.Collections;

public class Email {

    private int emailID;
    private int type;
    private String[] words;
    private EmailFeatures features;

    // Constructor takes in the email number, its type (Spam or Ham), and the String of text for the email that is converted to an Array of words
    public Email(int index, int type, String emailText) {
        emailID = index;
        this.type = type;
        // Splits the email into a String[] of words by using " +", which splits with one or more spaces
        String[] temp = emailText.split(" +");
        // Sometimes, the words Array can contain a blank element, so this piece of code (until line 22) removes all of those occurrences
        ArrayList<String> tempWords = new ArrayList<>();
        for(int i = 0; i < temp.length; i++) {
            tempWords.add(temp[i]);
        }
        tempWords.removeAll(Collections.singletonList(""));
        words = tempWords.toArray(new String[0]);
        features = new EmailFeatures(words);
    }

    // Getters for email fields
    public int getEmailID() {
        return emailID;
    }
    public int getType() {
        return type;
    }
    public String[] getWords() {
        return words;
    }
    public EmailFeatures getFeatures() {
        return features;
    }

}
