package crime;

import java.util.ArrayList;
import java.util.List;

public class Detective {
    private String name;
    private String badge;
    private int notesCount;
    private List<String> caseNotes;
    private List<Evidence> collectedEvidence;
    private List<Person> interviewedPersons;
    private String currentAccusation;

    public Detective(String name, String badge) {
        this.name = name;
        this.badge = badge;
        this.notesCount = 0;
        this.caseNotes = new ArrayList<>();
        this.collectedEvidence = new ArrayList<>();
        this.interviewedPersons = new ArrayList<>();
        this.currentAccusation = null;
    }

    public void addNote(String note) {
        notesCount++;
        caseNotes.add("[NOTE #" + notesCount + "] " + note);
    }

    public void collectEvidence(Evidence e) {
        if (!collectedEvidence.contains(e)) {
            collectedEvidence.add(e);
            addNote("Collected evidence: " + e.getName());
        }
    }

    public void interviewPerson(Person p) {
        if (!interviewedPersons.contains(p)) {
            interviewedPersons.add(p);
        }
        p.interrogate();
        addNote("Interrogated: " + p.getName());
    }

    public void makeAccusation(String suspectName) {
        this.currentAccusation = suspectName;
        addNote("ACCUSATION MADE: " + suspectName);
    }

    public String getName() { return name; }
    public String getBadge() { return badge; }
    public List<String> getCaseNotes() { return caseNotes; }
    public List<Evidence> getCollectedEvidence() { return collectedEvidence; }
    public List<Person> getInterviewedPersons() { return interviewedPersons; }
    public String getCurrentAccusation() { return currentAccusation; }
}
