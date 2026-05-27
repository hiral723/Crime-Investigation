package crime;

import java.util.ArrayList;
import java.util.List;

public class Person {
    public enum Role { SUSPECT, WITNESS, VICTIM }

    private String id;
    private String name;
    private String occupation;
    private String alibi;
    private Role role;
    private int suspicionLevel; // 0-100
    private List<String> statements;
    private List<String> connections;
    private boolean interrogated;
    private String secretInfo;

    public Person(String id, String name, String occupation, Role role, String alibi, String secretInfo) {
        this.id = id;
        this.name = name;
        this.occupation = occupation;
        this.role = role;
        this.alibi = alibi;
        this.secretInfo = secretInfo;
        this.suspicionLevel = 0;
        this.statements = new ArrayList<>();
        this.connections = new ArrayList<>();
        this.interrogated = false;
    }

    public void interrogate() {
        this.interrogated = true;
        this.suspicionLevel = Math.min(100, suspicionLevel + 25);
    }

    public void addStatement(String statement) {
        statements.add(statement);
    }

    public void addConnection(String connection) {
        connections.add(connection);
    }

    public void raiseSuspicion(int amount) {
        this.suspicionLevel = Math.min(100, suspicionLevel + amount);
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getOccupation() { return occupation; }
    public String getAlibi() { return alibi; }
    public Role getRole() { return role; }
    public int getSuspicionLevel() { return suspicionLevel; }
    public List<String> getStatements() { return statements; }
    public List<String> getConnections() { return connections; }
    public boolean isInterrogated() { return interrogated; }
    public String getSecretInfo() { return interrogated ? secretInfo : "??? (Interrogate first)"; }

    @Override
    public String toString() { return name + " (" + occupation + ")"; }
}
