package crime;

import java.util.*;

public class Case {
    private String caseId;
    private String title;
    private String description;
    private String crimeScene;
    private String timeOfCrime;
    private List<Person> persons;
    private List<Evidence> evidenceList;
    private String culpritId;
    private boolean solved;

    public Case(String caseId, String title, String description, String crimeScene, String timeOfCrime, String culpritId) {
        this.caseId = caseId;
        this.title = title;
        this.description = description;
        this.crimeScene = crimeScene;
        this.timeOfCrime = timeOfCrime;
        this.culpritId = culpritId;
        this.persons = new ArrayList<>();
        this.evidenceList = new ArrayList<>();
        this.solved = false;
    }

    public void addPerson(Person p) { persons.add(p); }
    public void addEvidence(Evidence e) { evidenceList.add(e); }

    public boolean checkAccusation(String suspectId) {
        if (culpritId.equals(suspectId)) {
            solved = true;
            return true;
        }
        return false;
    }

    public static Case buildDefaultCase() {
        Case c = new Case(
            "CASE-001",
            "THE WYNYARD WHISPER",
            "A renowned art dealer was found dead in his penthouse.\nThe vault was cracked, and a priceless painting — stolen.\nThree suspects. One killer. The clock is ticking.",
            "The Meridian Penthouse, Floor 42",
            "Saturday, 11:47 PM",
            "S002"
        );

        // Suspects
        Person s1 = new Person("S001", "Elara Voss", "Art Curator", Person.Role.SUSPECT,
            "Claims to have been at the gallery gala all evening.",
            "She was seen arguing with the victim over a forged provenance document.");
        s1.addStatement("I barely knew Marcus beyond professional dealings.");
        s1.addStatement("The gala had 200 witnesses. I never left.");
        s1.addConnection("Had access to the penthouse security codes.");
        s1.raiseSuspicion(20);

        Person s2 = new Person("S002", "Dorian Slate", "Private Financier", Person.Role.SUSPECT,
            "Says he was in a private poker game downtown.",
            "He owed the victim 2.3 million and had taken out a life insurance policy on him.");
        s2.addStatement("Marcus and I had business disputes, sure. But murder?");
        s2.addStatement("Check the casino logs. I was there.");
        s2.addConnection("Had motive: massive debt and insurance policy.");
        s2.raiseSuspicion(45);

        Person s3 = new Person("S003", "Mira Yuen", "Security Consultant", Person.Role.SUSPECT,
            "Claims she was running a perimeter check on another property.",
            "Her fingerprints were found on the cracked vault door.");
        s3.addStatement("I do security for multiple clients. This is a setup.");
        s3.addStatement("Someone must have worn gloves that match my prints.");
        s3.addConnection("Expert in bypassing alarm systems.");
        s3.raiseSuspicion(35);

        Person w1 = new Person("W001", "Felix Orn", "Building Concierge", Person.Role.WITNESS,
            "Was at the front desk all night.",
            "Saw a figure in a dark coat enter at 11:15 PM using a VIP pass.");
        w1.addStatement("I remember seeing someone — medium build, dark coat, rushed.");
        w1.addStatement("They used a resident VIP pass. I assumed it was legit.");

        Person victim = new Person("V001", "Marcus Hale", "Art Dealer", Person.Role.VICTIM,
            "N/A",
            "Had recently discovered a major forgery ring and was about to expose it.");
        victim.addStatement("(Found in a hidden journal) 'D.S. is dangerous. He knows I know.'");

        c.addPerson(s1);
        c.addPerson(s2);
        c.addPerson(s3);
        c.addPerson(w1);
        c.addPerson(victim);

        // Evidence
        Evidence e1 = new Evidence("E001", "Shattered Wine Glass", "Found near the body. Lipstick residue detected.", "PHYSICAL", "Living Room");
        e1.analyze("DNA matches Elara Voss — but could have been from an earlier visit.");

        Evidence e2 = new Evidence("E002", "Cracked Vault Door", "High-precision bypass tool used.", "PHYSICAL", "Study/Vault Room");
        e2.analyze("Fingerprints: Mira Yuen. Tool marks match custom bypass kit sold to one buyer.");

        Evidence e3 = new Evidence("E003", "Insurance Policy Document", "Life insurance policy — $3.2M payout.", "DOCUMENT", "Victim's Filing Cabinet");
        e3.analyze("Beneficiary: Dorian Slate. Signed 3 weeks before the murder.");

        Evidence e4 = new Evidence("E004", "Encrypted Phone", "Victim's secondary burner phone.", "DIGITAL", "Hidden under floorboard");
        e4.analyze("Last message sent to 'D': 'I have proof. Meet me or I go to the press.'");

        Evidence e5 = new Evidence("E005", "VIP Access Card (Clone)", "Cloned keycard found in maintenance shaft.", "DIGITAL", "Floor 42 Maintenance Shaft");
        e5.analyze("Clone matches Dorian Slate's registered VIP card. Cloning done 4 days prior.");

        Evidence e6 = new Evidence("E006", "Custom Bypass Tool", "Rare lock bypass kit. Serial number filed off.", "WEAPON", "Trash chute — Floor 41");
        e6.analyze("Only 3 units sold. One to a shell company linked to Dorian Slate's holdings.");

        c.addEvidence(e1);
        c.addEvidence(e2);
        c.addEvidence(e3);
        c.addEvidence(e4);
        c.addEvidence(e5);
        c.addEvidence(e6);

        return c;
    }

    public String getCaseId() { return caseId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getCrimeScene() { return crimeScene; }
    public String getTimeOfCrime() { return timeOfCrime; }
    public List<Person> getPersons() { return persons; }
    public List<Evidence> getEvidenceList() { return evidenceList; }
    public boolean isSolved() { return solved; }
    public String getCulpritId() { return culpritId; }
}
