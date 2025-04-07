public class EmailFeatures {
    public int wordCount;
    public int specialCharCount;
    public int uppercaseWordCount;
    public int linkCount;
    public double avgWordLength;

    public EmailFeatures(int wordCount, int specialCharCount, int uppercaseWordCount, int linkCount, double avgWordLength) {
        this.wordCount = wordCount;
        this.specialCharCount = specialCharCount;
        this.uppercaseWordCount = uppercaseWordCount;
        this.linkCount = linkCount;
        this.avgWordLength = avgWordLength;
    }
    public double[] toVector() {
        return new double[] {wordCount, specialCharCount, uppercaseWordCount, linkCount, avgWordLength};
    }
}
