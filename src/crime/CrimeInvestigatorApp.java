package crime;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class CrimeInvestigatorApp extends JFrame {

    // ── Palette ────────────────────────────────────────────────────────────────
    static final Color BG_DARK      = new Color(10, 10, 18);
    static final Color BG_MID       = new Color(18, 18, 30);
    static final Color BG_PANEL     = new Color(22, 22, 36);
    static final Color ACCENT_RED   = new Color(220, 38, 38);
    static final Color ACCENT_AMBER = new Color(245, 158, 11);
    static final Color ACCENT_TEAL  = new Color(20, 184, 166);
    static final Color ACCENT_PURPLE= new Color(139, 92, 246);
    static final Color TEXT_PRIMARY = new Color(230, 230, 240);
    static final Color TEXT_DIM     = new Color(120, 120, 150);
    static final Color BORDER_COLOR = new Color(50, 50, 80);
    static final Color HIGHLIGHT    = new Color(255, 255, 255, 15);

    // ── Fonts ──────────────────────────────────────────────────────────────────
    static final Font FONT_MONO   = new Font("Courier New", Font.BOLD, 12);
    static final Font FONT_TITLE  = new Font("Courier New", Font.BOLD, 20);
    static final Font FONT_LABEL  = new Font("Courier New", Font.BOLD, 11);
    static final Font FONT_SMALL  = new Font("Courier New", Font.PLAIN, 10);
    static final Font FONT_HUGE   = new Font("Courier New", Font.BOLD, 28);

    // ── State ─────────────────────────────────────────────────────────────────
    private Case currentCase;
    private Detective detective;

    // ── UI panels ─────────────────────────────────────────────────────────────
    private JPanel mainContent;
    private CardLayout cardLayout;
    private JLabel statusLabel;
    private JLabel timerLabel;
    private int secondsElapsed = 0;
    private javax.swing.Timer clockTimer;
    private JTextArea notesArea;

    public CrimeInvestigatorApp() {
        currentCase = Case.buildDefaultCase();
        detective   = new Detective("DET. R. CROSS", "BADGE #7741");

        setTitle("CRIME INVESTIGATION SIM — " + currentCase.getTitle());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setMinimumSize(new Dimension(1100, 700));
        setLocationRelativeTo(null);

        // Darken the title bar where possible
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); } catch (Exception ignored) {}

        buildUI();
        startClock();
        setVisible(true);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  BUILD ROOT UI
    // ══════════════════════════════════════════════════════════════════════════
    private void buildUI() {
        JPanel root = new NoisePanel();
        root.setLayout(new BorderLayout(0, 0));
        root.setBackground(BG_DARK);
        setContentPane(root);

        root.add(buildTopBar(),    BorderLayout.NORTH);
        root.add(buildSidebar(),   BorderLayout.WEST);
        root.add(buildMainArea(),  BorderLayout.CENTER);
        root.add(buildStatusBar(), BorderLayout.SOUTH);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  TOP BAR
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0,0, new Color(30,0,0), getWidth(),0, new Color(10,10,25));
                g2.setPaint(gp);
                g2.fillRect(0,0,getWidth(),getHeight());
                g2.setColor(ACCENT_RED);
                g2.fillRect(0, getHeight()-2, getWidth(), 2);
            }
        };
        bar.setPreferredSize(new Dimension(0, 60));
        bar.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        // Left: case badge
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);

        JLabel caseIdLabel = new JLabel("◈ " + currentCase.getCaseId());
        caseIdLabel.setFont(FONT_LABEL);
        caseIdLabel.setForeground(ACCENT_RED);

        JLabel titleLabel = new JLabel("  //  " + currentCase.getTitle().toUpperCase());
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(TEXT_PRIMARY);

        left.add(caseIdLabel);
        left.add(titleLabel);

        // Right: detective + timer
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 0));
        right.setOpaque(false);

        timerLabel = new JLabel("00:00:00");
        timerLabel.setFont(FONT_MONO);
        timerLabel.setForeground(ACCENT_AMBER);

        JLabel detLabel = new JLabel(detective.getName() + "  " + detective.getBadge());
        detLabel.setFont(FONT_LABEL);
        detLabel.setForeground(TEXT_DIM);

        right.add(timerLabel);
        right.add(detLabel);

        bar.add(left,  BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  SIDEBAR NAV
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildSidebar() {
        JPanel side = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(BG_MID);
                g2.fillRect(0,0,getWidth(),getHeight());
                g2.setColor(BORDER_COLOR);
                g2.fillRect(getWidth()-1,0,1,getHeight());
            }
        };
        side.setPreferredSize(new Dimension(190, 0));
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        String[][] navItems = {
            {"◉", "BRIEFING",      "BRIEF"},
            {"⬡", "CRIME BOARD",   "BOARD"},
            {"◈", "SUSPECTS",      "SUSPECTS"},
            {"◆", "EVIDENCE LAB",  "EVIDENCE"},
            {"▣", "INTERROGATE",   "INTERROGATE"},
            {"✦", "ACCUSE",        "ACCUSE"},
            {"◎", "CASE NOTES",    "NOTES"},
        };

        Color[] navColors = {ACCENT_TEAL, ACCENT_PURPLE, ACCENT_RED, ACCENT_AMBER, ACCENT_TEAL, ACCENT_RED, TEXT_DIM};

        ButtonGroup bg = new ButtonGroup();
        boolean first = true;
        for (int i = 0; i < navItems.length; i++) {
            JToggleButton btn = buildNavButton(navItems[i][0], navItems[i][1], navItems[i][2], navColors[i]);
            bg.add(btn);
            side.add(btn);
            side.add(Box.createRigidArea(new Dimension(0, 2)));
            if (first) { btn.setSelected(true); first = false; }
        }

        // Bottom: solved count
        side.add(Box.createVerticalGlue());
        JLabel statsLabel = new JLabel("<html><center>EVIDENCE<br>" +
            currentCase.getEvidenceList().size() + " ITEMS</center></html>");
        statsLabel.setFont(FONT_SMALL);
        statsLabel.setForeground(TEXT_DIM);
        statsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        side.add(statsLabel);

        return side;
    }

    private JToggleButton buildNavButton(String icon, String label, String card, Color accent) {
        JToggleButton btn = new JToggleButton() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (isSelected()) {
                    g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 30));
                    g2.fillRect(0,0,getWidth(),getHeight());
                    g2.setColor(accent);
                    g2.fillRect(0,0,3,getHeight());
                } else if (getModel().isRollover()) {
                    g2.setColor(HIGHLIGHT);
                    g2.fillRect(0,0,getWidth(),getHeight());
                }
                g2.setFont(new Font("Courier New", Font.BOLD, 13));
                g2.setColor(isSelected() ? accent : TEXT_DIM);
                g2.drawString(icon + "  " + label, 18, getHeight()/2 + 5);
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(190, 44));
        btn.setMaximumSize(new Dimension(190, 44));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.addActionListener(e -> {
            cardLayout.show(mainContent, card);
            setStatus("Viewing: " + label);
        });
        return btn;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  MAIN CARD AREA
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildMainArea() {
        cardLayout  = new CardLayout();
        mainContent = new JPanel(cardLayout);
        mainContent.setBackground(BG_DARK);

        mainContent.add(buildBriefingPanel(),     "BRIEF");
        mainContent.add(buildCrimeBoardPanel(),   "BOARD");
        mainContent.add(buildSuspectsPanel(),     "SUSPECTS");
        mainContent.add(buildEvidencePanel(),     "EVIDENCE");
        mainContent.add(buildInterrogatePanel(),  "INTERROGATE");
        mainContent.add(buildAccusePanel(),       "ACCUSE");
        mainContent.add(buildNotesPanel(),        "NOTES");

        cardLayout.show(mainContent, "BRIEF");
        return mainContent;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  BRIEFING PANEL
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildBriefingPanel() {
        JPanel p = new JPanel(new BorderLayout(20, 20)) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawScanlines(g, getWidth(), getHeight());
            }
        };
        p.setBackground(BG_DARK);
        p.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Big case title
        JLabel big = new JLabel("<html><center>⚠ CLASSIFIED ⚠<br><br>" +
            currentCase.getTitle() + "</center></html>");
        big.setFont(FONT_HUGE);
        big.setForeground(ACCENT_RED);
        big.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel descBox = darkBox(BORDER_COLOR);
        descBox.setLayout(new BorderLayout());
        descBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_RED, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JTextArea desc = new JTextArea(
            "INCIDENT REPORT\n" +
            "────────────────────────────────\n\n" +
            currentCase.getDescription() + "\n\n" +
            "CRIME SCENE : " + currentCase.getCrimeScene() + "\n" +
            "TIME OF CRIME: " + currentCase.getTimeOfCrime() + "\n\n" +
            "SUSPECTS ON FILE : " + currentCase.getPersons().stream()
                .filter(x -> x.getRole() == Person.Role.SUSPECT).count() + "\n" +
            "EVIDENCE ITEMS   : " + currentCase.getEvidenceList().size() + "\n\n" +
            ">>> USE THE LEFT PANEL TO NAVIGATE THE INVESTIGATION <<<\n" +
            ">>> GATHER EVIDENCE, INTERROGATE SUSPECTS, THEN ACCUSE <<<"
        );
        desc.setFont(FONT_MONO);
        desc.setForeground(TEXT_PRIMARY);
        desc.setBackground(BG_PANEL);
        desc.setEditable(false);
        desc.setWrapStyleWord(true);
        desc.setLineWrap(true);
        descBox.add(desc, BorderLayout.CENTER);

        // Stats row
        JPanel stats = new JPanel(new GridLayout(1, 4, 10, 0));
        stats.setOpaque(false);
        stats.add(statCard("SUSPECTS",  "3",    ACCENT_RED));
        stats.add(statCard("WITNESSES", "1",    ACCENT_AMBER));
        stats.add(statCard("EVIDENCE",  "6",    ACCENT_TEAL));
        stats.add(statCard("VICTIM",    "1",    ACCENT_PURPLE));

        p.add(big, BorderLayout.NORTH);
        p.add(descBox, BorderLayout.CENTER);
        p.add(stats, BorderLayout.SOUTH);
        return p;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  CRIME BOARD (web of strings visual)
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildCrimeBoardPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG_DARK);

        JLabel header = sectionHeader("◈ CRIME BOARD — CONNECTION MAP", ACCENT_PURPLE);
        wrapper.add(header, BorderLayout.NORTH);

        CrimeBoardCanvas canvas = new CrimeBoardCanvas(currentCase);
        wrapper.add(canvas, BorderLayout.CENTER);

        JLabel hint = new JLabel("  HOVER OVER NODES TO SEE DETAILS  //  CONNECTIONS SHOW RELATIONSHIPS", SwingConstants.CENTER);
        hint.setFont(FONT_SMALL);
        hint.setForeground(TEXT_DIM);
        wrapper.add(hint, BorderLayout.SOUTH);
        return wrapper;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  SUSPECTS PANEL
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildSuspectsPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(BG_DARK);
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        p.add(sectionHeader("◈ SUSPECT FILES", ACCENT_RED), BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(0, 2, 12, 12));
        grid.setBackground(BG_DARK);

        for (Person person : currentCase.getPersons()) {
            grid.add(buildPersonCard(person));
        }

        JScrollPane scroll = new JScrollPane(grid);
        scroll.setBorder(null);
        scroll.setBackground(BG_DARK);
        scroll.getViewport().setBackground(BG_DARK);
        scroll.getVerticalScrollBar().setUI(new DarkScrollBarUI());
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    private JPanel buildPersonCard(Person person) {
        Color roleColor = person.getRole() == Person.Role.SUSPECT ? ACCENT_RED
                        : person.getRole() == Person.Role.WITNESS ? ACCENT_AMBER : ACCENT_PURPLE;

        JPanel card = new JPanel(new BorderLayout(8, 8)) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(BG_PANEL);
                g2.fillRect(0,0,getWidth(),getHeight());
                g2.setColor(roleColor);
                g2.fillRect(0,0,getWidth(),3);
                // corner mark
                g2.setColor(new Color(roleColor.getRed(),roleColor.getGreen(),roleColor.getBlue(),40));
                g2.fillRect(getWidth()-40,0,40,40);
            }
        };
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(14, 14, 14, 14)
        ));

        // Header
        JPanel hdr = new JPanel(new BorderLayout());
        hdr.setOpaque(false);
        JLabel nameLabel = new JLabel(person.getName().toUpperCase());
        nameLabel.setFont(new Font("Courier New", Font.BOLD, 14));
        nameLabel.setForeground(TEXT_PRIMARY);
        JLabel roleLabel = new JLabel(person.getRole().name());
        roleLabel.setFont(FONT_SMALL);
        roleLabel.setForeground(roleColor);
        hdr.add(nameLabel, BorderLayout.WEST);
        hdr.add(roleLabel, BorderLayout.EAST);

        // Body
        JTextArea body = new JTextArea(
            "OCCUPATION : " + person.getOccupation() + "\n" +
            "ALIBI      : " + person.getAlibi() + "\n" +
            "INTERROGATED: " + (person.isInterrogated() ? "YES" : "NO") + "\n\n" +
            "STATEMENT  : " + (person.getStatements().isEmpty() ? "None on file"
                             : person.getStatements().get(0))
        );
        body.setFont(FONT_SMALL);
        body.setForeground(TEXT_DIM);
        body.setBackground(BG_PANEL);
        body.setEditable(false);
        body.setWrapStyleWord(true);
        body.setLineWrap(true);

        // Suspicion bar
        JPanel susBar = buildSuspicionBar(person.getSuspicionLevel(), roleColor);

        card.add(hdr,    BorderLayout.NORTH);
        card.add(body,   BorderLayout.CENTER);
        card.add(susBar, BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildSuspicionBar(int level, Color color) {
        JPanel bar = new JPanel(new BorderLayout(6, 0));
        bar.setOpaque(false);
        JLabel lbl = new JLabel("SUSPICION");
        lbl.setFont(FONT_SMALL);
        lbl.setForeground(TEXT_DIM);

        JPanel track = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(BG_DARK);
                g2.fillRoundRect(0,4,getWidth(),8,4,4);
                int filled = (int)(getWidth() * level / 100.0);
                g2.setColor(color);
                g2.fillRoundRect(0,4,filled,8,4,4);
            }
        };
        track.setOpaque(false);
        track.setPreferredSize(new Dimension(0, 16));

        JLabel pct = new JLabel(level + "%");
        pct.setFont(FONT_SMALL);
        pct.setForeground(color);

        bar.add(lbl,   BorderLayout.WEST);
        bar.add(track, BorderLayout.CENTER);
        bar.add(pct,   BorderLayout.EAST);
        return bar;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  EVIDENCE PANEL
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildEvidencePanel() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(BG_DARK);
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        p.add(sectionHeader("◆ EVIDENCE LAB", ACCENT_AMBER), BorderLayout.NORTH);

        // Split: list left, detail right
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setDividerLocation(320);
        split.setBorder(null);
        split.setBackground(BG_DARK);
        split.setDividerSize(4);

        // List
        DefaultListModel<Evidence> model = new DefaultListModel<>();
        currentCase.getEvidenceList().forEach(model::addElement);

        JList<Evidence> list = new JList<>(model);
        list.setBackground(BG_PANEL);
        list.setForeground(TEXT_PRIMARY);
        list.setFont(FONT_MONO);
        list.setSelectionBackground(new Color(50, 30, 0));
        list.setSelectionForeground(ACCENT_AMBER);
        list.setCellRenderer(new EvidenceCellRenderer());
        list.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));

        JScrollPane listScroll = new JScrollPane(list);
        listScroll.setBorder(null);
        listScroll.getVerticalScrollBar().setUI(new DarkScrollBarUI());

        // Detail
        JPanel detail = new JPanel(new BorderLayout(0, 10));
        detail.setBackground(BG_PANEL);
        detail.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));

        JLabel detTitle = new JLabel("SELECT AN ITEM TO INSPECT");
        detTitle.setFont(new Font("Courier New", Font.BOLD, 14));
        detTitle.setForeground(ACCENT_AMBER);

        JTextArea detBody = new JTextArea("────────────────────────────\nNo evidence selected.\n\nClick an item from the list\nto view its details and\nrun analysis.");
        detBody.setFont(FONT_MONO);
        detBody.setForeground(TEXT_DIM);
        detBody.setBackground(BG_PANEL);
        detBody.setEditable(false);
        detBody.setWrapStyleWord(true);
        detBody.setLineWrap(true);

        JButton analyzeBtn = buildActionButton("▶ ANALYZE EVIDENCE", ACCENT_AMBER);
        analyzeBtn.setEnabled(false);

        detail.add(detTitle,  BorderLayout.NORTH);
        detail.add(new JScrollPane(detBody), BorderLayout.CENTER);
        detail.add(analyzeBtn, BorderLayout.SOUTH);

        // Wire up
        list.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && list.getSelectedValue() != null) {
                Evidence ev = list.getSelectedValue();
                detTitle.setText("◆ " + ev.getName().toUpperCase());
                String info = "TYPE     : " + ev.getType() + "\n" +
                              "LOCATION : " + ev.getLocation() + "\n\n" +
                              "DESCRIPTION:\n" + ev.getDescription() + "\n\n" +
                              (ev.isAnalyzed() ? "ANALYSIS COMPLETE:\n>>> " + ev.getAnalysisResult()
                                              : "[ NOT YET ANALYZED ]");
                detBody.setText(info);
                analyzeBtn.setEnabled(!ev.isAnalyzed());
            }
        });

        analyzeBtn.addActionListener(e -> {
            Evidence ev = list.getSelectedValue();
            if (ev != null && !ev.isAnalyzed()) {
                ev.analyze(ev.getAnalysisResult().isEmpty() ? "Standard forensic analysis complete. Cross-reference with suspect profiles." : ev.getAnalysisResult());
                detective.collectEvidence(ev);
                detBody.setText(detBody.getText().replace("[ NOT YET ANALYZED ]",
                    "ANALYSIS COMPLETE:\n>>> " + ev.getAnalysisResult()));
                analyzeBtn.setEnabled(false);
                list.repaint();
                setStatus("Evidence analyzed: " + ev.getName());
                appendNote("Analyzed: " + ev.getName() + " → " + ev.getAnalysisResult());
                JOptionPane.showMessageDialog(this,
                    "ANALYSIS COMPLETE\n\n" + ev.getAnalysisResult(),
                    "LAB RESULTS", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        split.setLeftComponent(listScroll);
        split.setRightComponent(detail);
        p.add(split, BorderLayout.CENTER);
        return p;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  INTERROGATION PANEL
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildInterrogatePanel() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(BG_DARK);
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        p.add(sectionHeader("▣ INTERROGATION ROOM", ACCENT_TEAL), BorderLayout.NORTH);

        // Person selector
        List<Person> persons = currentCase.getPersons();
        JComboBox<Person> personBox = new JComboBox<>(persons.toArray(new Person[0]));
        personBox.setBackground(BG_PANEL);
        personBox.setForeground(ACCENT_TEAL);
        personBox.setFont(FONT_MONO);
        personBox.setBorder(BorderFactory.createLineBorder(ACCENT_TEAL));
        styleComboBox(personBox);

        JPanel selectRow = new JPanel(new BorderLayout(10, 0));
        selectRow.setOpaque(false);
        JLabel selectLbl = new JLabel("SUBJECT : ");
        selectLbl.setFont(FONT_MONO);
        selectLbl.setForeground(TEXT_DIM);
        selectRow.add(selectLbl, BorderLayout.WEST);
        selectRow.add(personBox, BorderLayout.CENTER);

        // Transcript area
        JTextArea transcript = new JTextArea();
        transcript.setFont(FONT_MONO);
        transcript.setForeground(ACCENT_TEAL);
        transcript.setBackground(new Color(0, 18, 18));
        transcript.setEditable(false);
        transcript.setWrapStyleWord(true);
        transcript.setLineWrap(true);
        transcript.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        transcript.setText("> INTERROGATION TERMINAL READY\n> SELECT SUBJECT AND BEGIN\n\n");

        JScrollPane transcriptScroll = new JScrollPane(transcript);
        transcriptScroll.setBorder(BorderFactory.createLineBorder(ACCENT_TEAL, 1));
        transcriptScroll.getVerticalScrollBar().setUI(new DarkScrollBarUI());

        // Question buttons
        JPanel questions = new JPanel(new GridLayout(2, 3, 8, 8));
        questions.setOpaque(false);
        String[] qs = {"WHERE WERE YOU?", "DO YOU KNOW THE VICTIM?", "EXPLAIN YOUR ALIBI", "ANY ENEMIES?", "REVEAL SECRET INFO", "CROSS EXAMINE"};
        for (String q : qs) {
            JButton qb = buildActionButton(q, ACCENT_TEAL);
            qb.addActionListener(e -> {
                Person selected = (Person) personBox.getSelectedItem();
                if (selected == null) return;
                detective.interviewPerson(selected);
                transcript.append("\n[DETECTIVE] " + q + "\n");
                String response = getResponse(selected, q);
                transcript.append("[" + selected.getName().toUpperCase() + "] " + response + "\n");
                transcript.setCaretPosition(transcript.getDocument().getLength());
                setStatus("Interrogating: " + selected.getName());
            });
            questions.add(qb);
        }

        JPanel bottom = new JPanel(new BorderLayout(0,8));
        bottom.setOpaque(false);
        bottom.add(questions, BorderLayout.CENTER);

        p.add(selectRow,        BorderLayout.NORTH);
        p.add(transcriptScroll, BorderLayout.CENTER);
        p.add(bottom,           BorderLayout.SOUTH);
        return p;
    }

    private String getResponse(Person p, String q) {
        switch (q) {
            case "WHERE WERE YOU?":        return p.getAlibi();
            case "DO YOU KNOW THE VICTIM?":return p.getStatements().isEmpty() ? "No comment." : p.getStatements().get(0);
            case "EXPLAIN YOUR ALIBI":     return p.getStatements().size()>1 ? p.getStatements().get(1) : p.getAlibi();
            case "ANY ENEMIES?":           return "That is not something I'm prepared to discuss.";
            case "REVEAL SECRET INFO":     return p.getSecretInfo();
            case "CROSS EXAMINE":          return p.getConnections().isEmpty() ? "Nothing to add." : p.getConnections().get(0);
            default:                       return "...";
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  ACCUSE PANEL
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildAccusePanel() {
        JPanel p = new JPanel(new BorderLayout(0, 20));
        p.setBackground(BG_DARK);
        p.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel warn = new JLabel("<html><center>⚠ WARNING ⚠<br>THIS ACTION IS IRREVERSIBLE</center></html>");
        warn.setFont(new Font("Courier New", Font.BOLD, 18));
        warn.setForeground(ACCENT_RED);
        warn.setHorizontalAlignment(SwingConstants.CENTER);
        p.add(warn, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(BG_DARK);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8);
        gbc.fill   = GridBagConstraints.HORIZONTAL;
        gbc.gridx  = 0; gbc.gridy = 0; gbc.gridwidth = 2;

        JLabel instrLbl = new JLabel("SELECT YOUR PRIME SUSPECT AND MAKE THE ACCUSATION:", SwingConstants.CENTER);
        instrLbl.setFont(FONT_LABEL);
        instrLbl.setForeground(TEXT_DIM);
        center.add(instrLbl, gbc);

        List<Person> suspects = new ArrayList<>();
        for (Person person : currentCase.getPersons()) {
            if (person.getRole() == Person.Role.SUSPECT) suspects.add(person);
        }

        gbc.gridy = 1; gbc.gridwidth = 1;
        for (Person s : suspects) {
            JPanel sCard = new JPanel(new BorderLayout(8,0));
            sCard.setBackground(BG_PANEL);
            sCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(10,12,10,12)
            ));
            JLabel sName = new JLabel("◉ " + s.getName());
            sName.setFont(new Font("Courier New", Font.BOLD, 13));
            sName.setForeground(TEXT_PRIMARY);

            JLabel sOcc = new JLabel(s.getOccupation());
            sOcc.setFont(FONT_SMALL);
            sOcc.setForeground(TEXT_DIM);

            JButton accuseBtn = buildActionButton("ACCUSE", ACCENT_RED);
            accuseBtn.addActionListener(e -> makeAccusation(s));

            sCard.add(sName, BorderLayout.WEST);
            sCard.add(sOcc, BorderLayout.CENTER);
            sCard.add(accuseBtn, BorderLayout.EAST);

            gbc.gridx = 0; gbc.gridwidth = 2;
            center.add(sCard, gbc);
            gbc.gridy++;
        }

        p.add(center, BorderLayout.CENTER);

        JTextArea hint = new JTextArea(
            "DETECTIVE'S CHECKLIST BEFORE ACCUSING:\n" +
            "  [ ] Analyzed all 6 pieces of evidence\n" +
            "  [ ] Interrogated all suspects\n" +
            "  [ ] Cross-referenced connections\n" +
            "  [ ] Checked insurance documents\n" +
            "  [ ] Traced the VIP access card clone\n\n" +
            "THINK CAREFULLY. A WRONG ACCUSATION ENDS THE CASE."
        );
        hint.setFont(FONT_SMALL);
        hint.setForeground(TEXT_DIM);
        hint.setBackground(BG_PANEL);
        hint.setEditable(false);
        hint.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(10,10,10,10)
        ));
        p.add(hint, BorderLayout.SOUTH);
        return p;
    }

    private void makeAccusation(Person suspect) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "ACCUSE " + suspect.getName().toUpperCase() + "?\n\nThis will close the case.",
            "FINAL ACCUSATION", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        detective.makeAccusation(suspect.getId());
        boolean correct = currentCase.checkAccusation(suspect.getId());
        clockTimer.stop();

        if (correct) {
            String msg = "CASE CLOSED — CORRECT ACCUSATION!\n\n" +
                "CULPRIT: " + suspect.getName() + "\n" +
                "EVIDENCE: Dorian Slate cloned his own VIP card,\n" +
                "bypassed the vault using a custom kit he purchased\n" +
                "via a shell company, and killed Marcus Hale to\n" +
                "silence him AND collect the insurance payout.\n\n" +
                "TIME: " + timerLabel.getText() + "\n\n" +
                "EXCELLENT DETECTIVE WORK, " + detective.getName();
            JOptionPane.showMessageDialog(this, msg, "✦ CASE SOLVED ✦", JOptionPane.INFORMATION_MESSAGE);
            setStatus("CASE SOLVED — CULPRIT: " + suspect.getName());
        } else {
            String msg = "WRONG ACCUSATION.\n\n" +
                suspect.getName() + " is NOT the killer.\n\n" +
                "The real culprit has escaped.\nCase marked UNSOLVED.\n\n" +
                "INVESTIGATE MORE THOROUGHLY NEXT TIME.";
            JOptionPane.showMessageDialog(this, msg, "✗ WRONG ACCUSATION", JOptionPane.ERROR_MESSAGE);
            setStatus("WRONG ACCUSATION — CASE FAILED");
        }
        appendNote("ACCUSATION: " + suspect.getName() + " → " + (correct ? "CORRECT" : "WRONG"));
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  NOTES PANEL
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildNotesPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(BG_DARK);
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        p.add(sectionHeader("◎ DETECTIVE'S CASE NOTES", TEXT_DIM), BorderLayout.NORTH);

        notesArea = new JTextArea("> CASE NOTES WILL APPEAR HERE AS YOU INVESTIGATE\n\n");
        notesArea.setFont(FONT_MONO);
        notesArea.setForeground(new Color(180, 255, 180));
        notesArea.setBackground(new Color(5, 15, 5));
        notesArea.setEditable(false);
        notesArea.setWrapStyleWord(true);
        notesArea.setLineWrap(true);
        notesArea.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JScrollPane scroll = new JScrollPane(notesArea);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(0,80,0)));
        scroll.getVerticalScrollBar().setUI(new DarkScrollBarUI());

        JPanel inputRow = new JPanel(new BorderLayout(8,0));
        inputRow.setOpaque(false);
        JTextField input = new JTextField();
        input.setFont(FONT_MONO);
        input.setBackground(new Color(5,20,5));
        input.setForeground(new Color(180,255,180));
        input.setCaretColor(new Color(180,255,180));
        input.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0,100,0)),
            BorderFactory.createEmptyBorder(5,8,5,8)
        ));
        input.setToolTipText("Type a note and press Enter or Add Note");

        JButton addBtn = buildActionButton("ADD NOTE", new Color(0,150,80));
        Runnable addNote = () -> {
            String text = input.getText().trim();
            if (!text.isEmpty()) {
                appendNote(text);
                input.setText("");
            }
        };
        addBtn.addActionListener(e -> addNote.run());
        input.addActionListener(e -> addNote.run());

        inputRow.add(input, BorderLayout.CENTER);
        inputRow.add(addBtn, BorderLayout.EAST);

        p.add(scroll, BorderLayout.CENTER);
        p.add(inputRow, BorderLayout.SOUTH);
        return p;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  STATUS BAR
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(8, 8, 15));
        bar.setPreferredSize(new Dimension(0, 28));
        bar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1,0,0,0, BORDER_COLOR),
            BorderFactory.createEmptyBorder(0, 16, 0, 16)
        ));

        statusLabel = new JLabel("▶ INVESTIGATION INITIATED — GOOD LUCK, DETECTIVE");
        statusLabel.setFont(FONT_SMALL);
        statusLabel.setForeground(ACCENT_TEAL);

        JLabel copy = new JLabel("CRIME SIM v1.0  //  OOP JAVA  //  SWING GUI");
        copy.setFont(FONT_SMALL);
        copy.setForeground(TEXT_DIM);

        bar.add(statusLabel, BorderLayout.WEST);
        bar.add(copy, BorderLayout.EAST);
        return bar;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  HELPERS
    // ══════════════════════════════════════════════════════════════════════════
    private JLabel sectionHeader(String text, Color color) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Courier New", Font.BOLD, 15));
        lbl.setForeground(color);
        lbl.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0,0,1,0, BORDER_COLOR),
            BorderFactory.createEmptyBorder(0,0,10,0)
        ));
        wrapper.add(lbl, BorderLayout.WEST);
        wrapper.setBorder(BorderFactory.createEmptyBorder(0,0,14,0));
        return lbl;
    }

    private JPanel statCard(String label, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(BG_PANEL);
                g.fillRect(0,0,getWidth(),getHeight());
                g.setColor(color);
                ((Graphics2D)g).fillRect(0,0,getWidth(),2);
            }
        };
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(12,12,12,12)
        ));
        JLabel v = new JLabel(value, SwingConstants.CENTER);
        v.setFont(new Font("Courier New", Font.BOLD, 32));
        v.setForeground(color);
        JLabel l = new JLabel(label, SwingConstants.CENTER);
        l.setFont(FONT_SMALL);
        l.setForeground(TEXT_DIM);
        card.add(v, BorderLayout.CENTER);
        card.add(l, BorderLayout.SOUTH);
        return card;
    }

    private JPanel darkBox(Color border) {
        JPanel box = new JPanel();
        box.setBackground(BG_PANEL);
        box.setBorder(BorderFactory.createLineBorder(border));
        return box;
    }

    private JButton buildActionButton(String text, Color color) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(color.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 60));
                } else {
                    g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 25));
                }
                g2.fillRect(0,0,getWidth(),getHeight());
                g2.setColor(color);
                g2.drawRect(0,0,getWidth()-1,getHeight()-1);
                g2.setFont(new Font("Courier New", Font.BOLD, 11));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText()))/2;
                int y = (getHeight() + fm.getAscent())/2 - 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        btn.setForeground(color);
        btn.setFont(new Font("Courier New", Font.BOLD, 11));
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width + 20, 32));
        return btn;
    }

    private void setStatus(String msg) {
        statusLabel.setText("▶ " + msg.toUpperCase());
    }

    private void appendNote(String note) {
        if (notesArea != null) {
            notesArea.append("[" + timerLabel.getText() + "] " + note + "\n");
            notesArea.setCaretPosition(notesArea.getDocument().getLength());
        }
        detective.addNote(note);
    }

    private void startClock() {
        clockTimer = new javax.swing.Timer(1000, e -> {
            secondsElapsed++;
            int h = secondsElapsed / 3600;
            int m = (secondsElapsed % 3600) / 60;
            int s = secondsElapsed % 60;
            timerLabel.setText(String.format("%02d:%02d:%02d", h, m, s));
        });
        clockTimer.start();
    }

    private void drawScanlines(Graphics g, int w, int h) {
        g.setColor(new Color(255,255,255,4));
        for (int y = 0; y < h; y += 4) g.drawLine(0, y, w, y);
    }

    private void styleComboBox(JComboBox<?> box) {
        box.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean sel, boolean foc) {
                super.getListCellRendererComponent(l,v,i,sel,foc);
                setBackground(sel ? new Color(0,40,40) : BG_PANEL);
                setForeground(ACCENT_TEAL);
                setFont(FONT_MONO);
                setBorder(BorderFactory.createEmptyBorder(4,8,4,8));
                return this;
            }
        });
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  MAIN
    // ══════════════════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        SwingUtilities.invokeLater(CrimeInvestigatorApp::new);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  INNER CLASS: Noise background panel
    // ══════════════════════════════════════════════════════════════════════════
    static class NoisePanel extends JPanel {
        private BufferedImage noise;
        NoisePanel() {
            setOpaque(true);
            setBackground(BG_DARK);
        }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (noise == null || noise.getWidth() != getWidth() || noise.getHeight() != getHeight()) {
                noise = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
                Random rnd = new Random(42);
                for (int x=0;x<getWidth();x++)
                    for (int y=0;y<getHeight();y++) {
                        int v = rnd.nextInt(30);
                        noise.setRGB(x,y, new Color(v,v,v,8).getRGB());
                    }
            }
            g.drawImage(noise,0,0,null);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  INNER CLASS: Evidence cell renderer
    // ══════════════════════════════════════════════════════════════════════════
    static class EvidenceCellRenderer extends DefaultListCellRenderer {
        @Override public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean sel, boolean foc) {
            super.getListCellRendererComponent(l,v,i,sel,foc);
            Evidence ev = (Evidence) v;
            setText((ev.isAnalyzed() ? "✓ " : "○ ") + ev.getName());
            setBackground(sel ? new Color(40,30,0) : (i%2==0 ? BG_PANEL : BG_MID));
            setForeground(ev.isAnalyzed() ? ACCENT_AMBER : TEXT_DIM);
            setFont(FONT_MONO);
            setBorder(BorderFactory.createEmptyBorder(6,10,6,10));
            return this;
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  INNER CLASS: Dark scrollbar
    // ══════════════════════════════════════════════════════════════════════════
    static class DarkScrollBarUI extends BasicScrollBarUI {
        @Override protected void configureScrollBarColors() {
            thumbColor = new Color(60,60,90);
            trackColor = BG_DARK;
        }
        @Override protected JButton createIncreaseButton(int o) { return zeroButton(); }
        @Override protected JButton createDecreaseButton(int o) { return zeroButton(); }
        private JButton zeroButton() {
            JButton b = new JButton(); b.setPreferredSize(new Dimension(0,0)); return b;
        }
    }
}
