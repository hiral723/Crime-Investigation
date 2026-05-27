# 🕵️ CRIME INVESTIGATION SIMULATOR
## OOP Java + Swing GUI — Dark Detective Theme

---

## ✅ STEP-BY-STEP: Running in VS Code

### STEP 1 — Install Prerequisites
Make sure you have these installed:
- **Java JDK 11 or higher**: https://adoptium.net/
  - After installing, verify with: `java -version` in terminal
- **VS Code**: https://code.visualstudio.com/

### STEP 2 — Install VS Code Extension
1. Open VS Code
2. Press `Ctrl+Shift+X` (Extensions panel)
3. Search for: **"Extension Pack for Java"** by Microsoft
4. Click **Install** (this installs Language Support, Debugger, and Maven tools)
5. Restart VS Code when prompted

### STEP 3 — Open the Project
1. Open VS Code
2. Click **File → Open Folder**
3. Navigate to and select the `CrimeInvestigator` folder
4. VS Code will detect it as a Java project automatically

### STEP 4 — Run the App
**Option A — Click Run (easiest):**
1. Open `src/crime/CrimeInvestigatorApp.java`
2. You'll see a ▶ **Run** button appear above the `main` method
3. Click it → the game launches!

**Option B — Terminal:**
```bash
# From inside the CrimeInvestigator folder:
mkdir -p out
javac -d out src/crime/*.java
java -cp out crime.CrimeInvestigatorApp
```

**Option C — F5 Debug:**
1. Press `F5` — VS Code uses `.vscode/launch.json` to run

---

## 🗂️ PROJECT STRUCTURE

```
CrimeInvestigator/
├── src/
│   └── crime/
│       ├── CrimeInvestigatorApp.java  ← MAIN GUI (run this)
│       ├── CrimeBoardCanvas.java      ← Interactive node graph
│       ├── Case.java                  ← Case data & scenario
│       ├── Person.java                ← Suspect/Witness/Victim
│       ├── Evidence.java              ← Evidence items
│       └── Detective.java             ← Player detective
├── .vscode/
│   ├── launch.json                    ← F5 run config
│   └── settings.json                  ← Java source paths
└── README.md                          ← This file
```

---

## 🎮 HOW TO PLAY

| Screen          | What to do                                      |
|-----------------|--------------------------------------------------|
| **BRIEFING**    | Read the case summary and note the 4 stats       |
| **CRIME BOARD** | Hover nodes to see relationships between people  |
| **SUSPECTS**    | Review all suspect files and suspicion levels    |
| **EVIDENCE LAB**| Click each item → press ANALYZE EVIDENCE        |
| **INTERROGATE** | Pick a suspect → press question buttons          |
| **ACCUSE**      | When ready, accuse the killer (one chance!)      |
| **CASE NOTES**  | Auto-logs all your actions; add your own notes   |

### 🔑 KEY CLUES TO FIND:
- Insurance policy (E003) — who benefits from the death?
- Encrypted phone (E004) — what does the last message say?
- Cloned VIP card (E005) — whose card was cloned?
- Custom bypass tool (E006) — who purchased it?

---

## 🧱 OOP CLASS DESIGN

```
Person          → represents any person (suspect/witness/victim)
Evidence        → a piece of forensic evidence
Case            → holds all persons + evidence + the culprit answer
Detective       → player class — logs notes, collects evidence
CrimeBoardCanvas → custom JPanel for the connection graph
CrimeInvestigatorApp → main JFrame with all GUI panels (CardLayout)
```

---

## 💡 TROUBLESHOOTING

| Problem | Fix |
|---------|-----|
| "javac not found" | Install JDK from adoptium.net, restart VS Code |
| Extension not detecting Java | Open a .java file, wait 10 sec for indexing |
| Window appears but blank | Resize window once — triggers repaint |
| "package crime does not exist" | Run javac from the project root, not src/ |

---

*Built with Java Swing — No external libraries required.*
