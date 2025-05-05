import java.util.*;

public class DistanceModel {

    private ArrayList<HashMap<String,Double>> allEmailFeatures;
    private ArrayList<Integer> labels;
    private int n;

    // Constructor initializes fields
    public DistanceModel(int n, ArrayList<HashMap<String,Double>> allEmailFeatures, ArrayList<Integer> labels) {
        this.n = n;
        this.allEmailFeatures = allEmailFeatures;
        this.labels = labels;
    }

    // Takes in a HashMap of test features to compare to the array of trained features to return a predicted email type
    public int predict(HashMap<String,Double> testFeature) {

        if(n > allEmailFeatures.size()) {
            System.err.println("n is too large");
        }
        // Creates a Neighbors ArrayList where each neighbor holds a label (1 or 0 for spam or ham) and a distance from the test feature
        ArrayList<Neighbor> neighbors = new ArrayList<>();
        ArrayList<Integer> nearestNeighborsLabels = new ArrayList<>();
        // gets the distances from the test feature and all of the trained features to add to Neighbors ArrayList
        for (int i = 0; i < allEmailFeatures.size(); i++) {
            double distance = euclideanDistance(testFeature, allEmailFeatures.get(i));
            neighbors.add(new Neighbor(distance, labels.get(i)));
        }

        // Sorts the Neighbors ArrayList from least distance to greatest distance; useful for determining the closest n neighbors
        neighbors.sort(Comparator.comparingDouble(n -> n.distance));

        for(int i = 0; i < n; i++) {
            nearestNeighborsLabels.add(neighbors.get(i).label);
        }
        // The reason for if n > 2 is in case the user wants to run the test feature against the trained summary file instead of trained email features file
        // (Summary file only has 2 rows) we usually just run it against the email features file anyway, since it is more accurate
        if(n > 2) {
            // if the majority of the nearest neighbors are spam, return 1. if the majority are ham, return 0
            int spamLabelCount = 0;
            for(int i = 0; i < n; i++) {
                if(nearestNeighborsLabels.get(i) == 1) {
                    spamLabelCount++;
                }
            }
            if(spamLabelCount > n / 2) {
                return 1;
            }
            else {
                return 0;
            }
        }
        else {
            if(neighbors.get(0).distance < neighbors.get(1).distance) {
                return neighbors.get(0).label;
            }
            else {
                return neighbors.get(1).label;
            }
        }
    }

    // Method for determining the distance between two HashMaps of features
    private double euclideanDistance(HashMap<String,Double> p1, HashMap<String,Double> p2) {
        Set<String> wordSet1 = p1.keySet();
        Set<String> wordSet2 = p2.keySet();
        // Create a new set with elements of set1
        Set<String> combinedWords = new HashSet<>(wordSet1);
        // Adds elements of set 2 to set 1 to get a combined set of total keys (words)
        combinedWords.addAll(wordSet2);
        String[] words = combinedWords.toArray(new String[0]);
        // The formula for euclidean distance is the square root of the sum of squares of the differences
        double sum = 0.0;
        for (int i = 0; i < words.length; i++) {
            sum += Math.pow(p1.getOrDefault(words[i],0.0) - p2.getOrDefault(words[i],0.0), 2);
        }
        return Math.sqrt(sum);
    }

    // Neighbor object which holds a distance and a label
    private class Neighbor {
        double distance;
        int label;

        public Neighbor(double distance, int label) {
            this.distance = distance;
            this.label = label;
        }
    }

}