public class Email {
    private String rawText;
    private String label;
    private EmailFeatures features;

    public Email(String rawText, String label) {
        this.rawText = rawText;
        this.label = label;
        this.features = null;
    }

    public void extractFeatures(FeatureExtractor extractor) {
        this.features = extractor.extract(this);
    }

    public String getRawText() {
        return rawText;
    }

    public String getLabel() {
        return label;
    }

    public EmailFeatures features() {
        return features;
    }
}
