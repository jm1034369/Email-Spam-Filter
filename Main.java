import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {

        File spam = new File("Learning Set - Spam.csv");
        DatasetProcessor driver = new DatasetProcessor(spam);

        //System.out.println(Arrays.toString(driver.getEmails()[0].getWords()));

        //for(int i = 0; i < driver.getEmails().length; i++) {
            //System.out.println(driver.getUniqueWords()[i]);
            //System.out.println(driver.getEmails()[34].getFeatures().getSpecialCharacterWordCount());
        //}

        //System.out.println(driver.getCleanedText());
        //System.out.println(Arrays.deepToString(driver.getEmails()[0].getFeatures().getWordFrequency()));
        //System.out.println(Arrays.deepToString(driver.getEmails()[0].getFeatures().getNGramFrequency()));
        //System.out.println(Arrays.toString(driver.getEmails()[1].getWords()));
        System.out.println(driver.getEmails()[0].getFeatures().getMeanWordSize());
        System.out.println(driver.getEmails()[0].getFeatures().getMedianWordSize());
        System.out.println(driver.getEmails()[0].getFeatures().getModeWordSize());


    }
}
