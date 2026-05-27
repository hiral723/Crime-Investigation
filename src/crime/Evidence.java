package crime;

public class Evidence {
    private String id;
    private String name;
    private String description;
    private String type; // WEAPON, DOCUMENT, PHYSICAL, DIGITAL
    private String location;
    private boolean analyzed;
    private String analysisResult;

    public Evidence(String id, String name, String description, String type, String location) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.location = location;
        this.analyzed = false;
        this.analysisResult = "";
    }

    public void analyze(String result) {
        this.analyzed = true;
        this.analysisResult = result;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getType() { return type; }
    public String getLocation() { return location; }
    public boolean isAnalyzed() { return analyzed; }
    public String getAnalysisResult() { return analysisResult; }

    @Override
    public String toString() {
        return "[" + type + "] " + name;
    }
}
