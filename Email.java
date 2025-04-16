import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;

public class Email {

    private int emailID;
    private String[] words;
    private EmailFeatures features;

    public Email(int index, String emailText) {
        emailID = index;
        String[] temp = emailText.split(" +");
        ArrayList<String> tempWords = new ArrayList<>();
        for(int i = 0; i < temp.length; i++) {
            tempWords.add(temp[i]);
        }
        tempWords.removeAll(Collections.singletonList(""));
        words = tempWords.toArray(new String[0]);

        features = new EmailFeatures(emailID, words);
    }

    public String[] getWords() {
        return words;
    }

    public EmailFeatures getFeatures() {
        return features;
    }

}
