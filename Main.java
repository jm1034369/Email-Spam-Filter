import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {

        // *** Training Code ***
        String spamFeaturesName = "spamEmailFeaturesTraining.csv";
        String spamSummaryName = "spamFeaturesSummary";
        String hamFeaturesName = "hamEmailFeaturesTraining.csv";
        String hamSummaryName = "hamFeaturesSummary";
        // Creates spam and ham features to train the model and prints them to a csv file
        File trainFileSpam = new File("spam_training.csv");
        DatasetProcessor trainerSpam = new DatasetProcessor(trainFileSpam,spamFeaturesName,spamSummaryName);
        File trainFileHam = new File("ham_training.csv");
        DatasetProcessor trainerHam = new DatasetProcessor(trainFileHam,hamFeaturesName,hamSummaryName);



        // *** Test Code ***
        // Reads the csv files to convert them to an ArrayList of HashMaps for DistanceModel
        File spamFeatures = new File(spamFeaturesName);
        File hamFeatures = new File(hamFeaturesName);
        File testFile = new File("ham_and_spam_test.csv");
        Scanner reader1 = new Scanner(spamFeatures);
        Scanner reader2 = new Scanner(hamFeatures);
        // Generate the features for the test dataset
        DatasetProcessor tester = new DatasetProcessor(testFile,"testEmailFeatures.csv",null);

        // Creates HashMap ArrayList as well as label ArrayList to later add values to for DistanceModel to train it
        ArrayList<HashMap<String,Double>> emailFeatures = new ArrayList<>();
        ArrayList<Integer> labels = new ArrayList<>();

        // For spam dataset
        // Removes file header to store as a key ArrayList and removes and all EmailID values to make the HashMap usable
        String[] tempKeys1 = reader1.nextLine().split(",");
        ArrayList<String> keys1 = new ArrayList<>();
        for(int i = 0; i < tempKeys1.length; i++) {
            keys1.add(tempKeys1[i]);
        }
        keys1.remove(0);

        // Converts each row to Hashmap using the keys1 ArrayList and adds it to the emailFeatures ArrayList
        while(reader1.hasNextLine()) {
            String[] tempValues = reader1.nextLine().split(",");
            ArrayList<Double> values = new ArrayList<>();
            for(int i = 0; i < tempValues.length; i++) {
                values.add(Double.parseDouble(tempValues[i]));
            }
            values.remove(0);

            HashMap<String, Double> features = new HashMap<>();
            for(int i = 0; i < values.size(); i++) {
                features.put(keys1.get(i),values.get(i));
            }
            emailFeatures.add(features);
            labels.add(1);
        }

        // For ham dataset
        // Removes file header to store as a key ArrayList and removes and all EmailID values to make the HashMap usable
        String[] tempKeys2 = reader2.nextLine().split(",");
        ArrayList<String> keys2 = new ArrayList<>();
        for(int i = 0; i < tempKeys2.length; i++) {
            keys2.add(tempKeys2[i]);
        }
        keys2.remove(0);

        // Converts each row to Hashmap using the keys2 ArrayList and adds it to the emailFeatures ArrayList
        while(reader2.hasNextLine()) {
            String[] tempValues = reader2.nextLine().split(",");
            ArrayList<Double> values = new ArrayList<>();
            for(int i = 0; i < tempValues.length; i++) {
                values.add(Double.parseDouble(tempValues[i]));
            }
            values.remove(0);

            HashMap<String, Double> features = new HashMap<>();
            for(int i = 0; i < values.size(); i++) {
                features.put(keys2.get(i),values.get(i));
            }
            emailFeatures.add(features);
            labels.add(0);
        }

        // Creates the distance model using the new HashMap ArrayList of all email features as well as the labels ArrayList
        // n is number of (nearest neighbors) DistanceModel will use to determine spam or ham
        DistanceModel model = new DistanceModel(11,emailFeatures,labels);

        // Converts test data features from csv to HashMap to use in DistanceModel to get distance
        File testFeatures = new File("testEmailFeatures.csv");
        Scanner reader3 = new Scanner(testFeatures);

        // Removes file header to store as a key ArrayList and removes and all EmailID values to make the HashMap usable
        String[] tempKeys3 = reader3.nextLine().split(",");
        ArrayList<String> keys3 = new ArrayList<>();
        for(int i = 0; i < tempKeys3.length; i++) {
            keys3.add(tempKeys3[i]);
        }
        keys3.remove(0);

        // Count variables for later calculations
        int correctCount = 0;
        int falsePosCount = 0;
        int falseNegCount = 0;
        int truePosCount = 0;
        int trueNegCount = 0;
        int i = 0;
        // Creation of prediction file to write prediction results to
        File predictions = new File("Predictions.csv");
        PrintWriter out = new PrintWriter(predictions);
        out.println("EmailID,Actual,Predicted");
        // Converts each row to Hashmap using the keys3 ArrayList and runs it through DistanceModel predict method to return 1 or 0
        while(reader3.hasNextLine()) {
            String[] tempValues = reader3.nextLine().split(",");
            ArrayList<Double> values = new ArrayList<>();
            for(int j = 0; j < tempValues.length; j++) {
                values.add(Double.parseDouble(tempValues[j]));
            }
            values.remove(0);

            HashMap<String, Double> features = new HashMap<>();
            for(int j = 0; j < values.size(); j++) {
                features.put(keys3.get(j),values.get(j));
            }
            // Call to predict method
            int predictedType = model.predict(features);
            int actualType = tester.getEmails()[i].getType();
            if(predictedType == 1 && actualType == 1) {
                truePosCount++;
                correctCount++;
            }
            else if(predictedType == 0 && actualType == 0) {
                trueNegCount++;
                correctCount++;
            }
            else if(predictedType == 1 && actualType == 0) {
                falsePosCount++;
            }
            else if(predictedType == 0 && actualType == 1) {
                falseNegCount++;
            }
            // Prints results to Predictions.csv and to console
            out.println(tester.getEmails()[i].getEmailID() + "," + actualType + "," + predictedType);
            System.out.println(tester.getEmails()[i].getEmailID() + " Actual: " + actualType + " Predicted: " + predictedType);
            i++;
        }
        out.close();
        // Prints final calculations (Accuracy, False Positive Rate, False Negative Rate, Classification Error)
        System.out.println("\nClassification Accuracy: " + (((double)correctCount) / tester.getEmails().length) * 100 + "%");
        System.out.println("False Positive Rate: " + (((double)falsePosCount) / (falsePosCount + trueNegCount)) * 100 + "%");
        System.out.println("False Negative Rate: " + (((double)falseNegCount) / (falseNegCount + truePosCount)) * 100 + "%");
        System.out.println("Classification Error: " +  (((double)(falsePosCount + falseNegCount))/ tester.getEmails().length) * 100 + "%");
    }
}
