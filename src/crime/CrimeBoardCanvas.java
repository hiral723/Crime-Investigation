package crime;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;

/**
 * Interactive "crime board" canvas — nodes for suspects/evidence with
 * red-string connections, hover tooltips, and a radial layout.
 */
public class CrimeBoardCanvas extends JPanel {

    private static final Color BG        = new Color(12, 8, 8);
    private static final Color STRING_C  = new Color(180, 30, 30, 160);
    private static final Color NODE_SUS  = new Color(200, 40, 40);
    private static final Color NODE_EVI  = new Color(200, 140, 20);
    private static final Color NODE_WIT  = new Color(30, 160, 160);
    private static final Color NODE_VIC  = new Color(140, 40, 200);
    private static final Color TEXT_C    = new Color(230, 220, 210);
    private static final Font  FONT_NODE = new Font("Courier New", Font.BOLD, 10);
    private static final Font  FONT_TIP  = new Font("Courier New", Font.PLAIN, 11);

    private List<Node> nodes = new ArrayList<>();
    private List<int[]> edges = new ArrayList<>(); // [fromIdx, toIdx]
    private int hoveredIdx = -1;
    private Point mousePos = new Point();

    public CrimeBoardCanvas(Case c) {
        setBackground(BG);
        setPreferredSize(new Dimension(800, 500));
        buildGraph(c);
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseMoved(MouseEvent e) {
                mousePos = e.getPoint();
                hoveredIdx = -1;
                for (int i = 0; i < nodes.size(); i++) {
                    if (nodes.get(i).contains(mousePos)) { hoveredIdx = i; break; }
                }
                repaint();
            }
        });
    }

    private void buildGraph(Case c) {
        // We lay out in a radial pattern; positions set during paintComponent using actual size
        // Store persons and evidence as nodes with relative polar coords [angle, radius-fraction]
        List<Person> persons = c.getPersons();
        List<Evidence> evidences = c.getEvidenceList();

        // Centre = victim
        nodes.add(new Node(persons.stream().filter(p->p.getRole()==Person.Role.VICTIM).findFirst().orElse(persons.get(0)),
                           0, 0, NODE_VIC));

        // Suspects around inner ring
        List<Person> suspects = new ArrayList<>();
        for (Person p : persons) if (p.getRole()==Person.Role.SUSPECT) suspects.add(p);
        for (int i=0;i<suspects.size();i++) {
            double angle = 2*Math.PI*i/suspects.size() - Math.PI/2;
            nodes.add(new Node(suspects.get(i), angle, 0.32, NODE_SUS));
        }

        // Witnesses mid ring
        List<Person> witnesses = new ArrayList<>();
        for (Person p : persons) if (p.getRole()==Person.Role.WITNESS) witnesses.add(p);
        for (int i=0;i<witnesses.size();i++) {
            double angle = 2*Math.PI*i/Math.max(witnesses.size(),1) + Math.PI/4;
            nodes.add(new Node(witnesses.get(i), angle, 0.45, NODE_WIT));
        }

        // Evidence outer ring
        for (int i=0;i<evidences.size();i++) {
            double angle = 2*Math.PI*i/evidences.size() + Math.PI/6;
            nodes.add(new Node(evidences.get(i), angle, 0.68, NODE_EVI));
        }

        // Edges: victim ↔ each suspect
        for (int i=1;i<=suspects.size();i++) edges.add(new int[]{0,i});
        // Edges: suspect 2 (Dorian) ↔ some evidence (E003, E004, E005, E006)
        // Just connect everyone to a couple of evidence nodes for drama
        int susEnd = 1 + suspects.size() + witnesses.size();
        for (int ei = susEnd; ei < nodes.size(); ei++) {
            // each evidence connects to suspect closest in angle
            for (int si=1;si<=suspects.size();si++) {
                if ((ei - susEnd) % suspects.size() == (si-1)) edges.add(new int[]{si, ei});
            }
            edges.add(new int[]{0, ei}); // victim to evidence
        }
    }

    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Background grid
        g2.setColor(new Color(255,255,255,8));
        for (int x=0;x<getWidth();x+=40) g2.drawLine(x,0,x,getHeight());
        for (int y=0;y<getHeight();y+=40) g2.drawLine(0,y,getWidth(),y);

        int cx = getWidth()/2, cy = getHeight()/2;
        double maxR = Math.min(cx, cy) * 0.88;

        // Compute node screen positions
        for (Node n : nodes) n.computePos(cx, cy, maxR);

        // Draw edges (strings)
        g2.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
            0, new float[]{6,4}, 0));
        for (int[] edge : edges) {
            Node a = nodes.get(edge[0]), b = nodes.get(edge[1]);
            boolean highlight = hoveredIdx==edge[0] || hoveredIdx==edge[1];
            g2.setColor(highlight ? new Color(255, 80, 80, 220) : STRING_C);
            g2.drawLine(a.sx, a.sy, b.sx, b.sy);
            // Pin dot
            g2.setColor(new Color(200,50,50,120));
            g2.fillOval(a.sx-2,a.sy-2,5,5);
            g2.fillOval(b.sx-2,b.sy-2,5,5);
        }

        // Draw nodes
        for (int i=0;i<nodes.size();i++) {
            nodes.get(i).draw(g2, i==hoveredIdx, FONT_NODE, TEXT_C);
        }

        // Tooltip
        if (hoveredIdx >= 0) {
            Node n = nodes.get(hoveredIdx);
            String[] lines = n.getTooltip();
            int pad=8, lineH=16;
            int tw = 0;
            for (String l:lines) tw=Math.max(tw, g2.getFontMetrics(FONT_TIP).stringWidth(l));
            int th = lines.length*lineH + pad*2;
            int tx = mousePos.x+14, ty = mousePos.y-10;
            if (tx+tw+pad*2 > getWidth()) tx = mousePos.x-tw-pad*2-14;
            if (ty+th > getHeight()) ty = getHeight()-th-4;

            g2.setColor(new Color(20,10,10,220));
            g2.fillRoundRect(tx,ty,tw+pad*2,th,6,6);
            g2.setColor(n.color);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(tx,ty,tw+pad*2,th,6,6);
            g2.setFont(FONT_TIP);
            for (int li=0;li<lines.length;li++) {
                g2.setColor(li==0 ? n.color : new Color(200,190,180));
                g2.drawString(lines[li], tx+pad, ty+pad+lineH*(li+1)-2);
            }
        }

        g2.dispose();
    }

    // ── Inner node class ─────────────────────────────────────────────────────
    static class Node {
        Object data;
        double angle, radiusFrac;
        Color color;
        int sx, sy; // screen pos

        Node(Object data, double angle, double radiusFrac, Color color) {
            this.data=data; this.angle=angle; this.radiusFrac=radiusFrac; this.color=color;
        }

        void computePos(int cx, int cy, double maxR) {
            sx = cx + (int)(Math.cos(angle)*maxR*radiusFrac);
            sy = cy + (int)(Math.sin(angle)*maxR*radiusFrac);
        }

        boolean contains(Point p) {
            int dx=p.x-sx, dy=p.y-sy;
            return dx*dx+dy*dy<=22*22;
        }

        String getLabel() {
            if (data instanceof Person) return ((Person)data).getName().split(" ")[0].toUpperCase();
            return ((Evidence)data).getName().length()>12
                ? ((Evidence)data).getName().substring(0,12)+"…"
                : ((Evidence)data).getName();
        }

        String[] getTooltip() {
            if (data instanceof Person) {
                Person p = (Person)data;
                return new String[]{
                    "◉ " + p.getName().toUpperCase(),
                    p.getOccupation(),
                    "ROLE: " + p.getRole().name(),
                    "ALIBI: " + p.getAlibi().substring(0,Math.min(40,p.getAlibi().length()))+"…",
                    "SUSPICION: " + p.getSuspicionLevel() + "%",
                    "INTERROGATED: " + (p.isInterrogated()?"YES":"NO")
                };
            } else {
                Evidence e = (Evidence)data;
                return new String[]{
                    "◆ " + e.getName().toUpperCase(),
                    "TYPE: " + e.getType(),
                    "LOCATION: " + e.getLocation(),
                    "ANALYZED: " + (e.isAnalyzed()?"YES":"NO"),
                    e.isAnalyzed() ? e.getAnalysisResult().substring(0,Math.min(45,e.getAnalysisResult().length()))+"…" : ""
                };
            }
        }

        void draw(Graphics2D g2, boolean hovered, Font font, Color textColor) {
            int r = hovered ? 20 : 16;
            // Glow
            if (hovered) {
                for (int gr=r+10;gr>r;gr-=2) {
                    g2.setColor(new Color(color.getRed(),color.getGreen(),color.getBlue(),(int)(40*(r+10-gr)/10.0)));
                    g2.fillOval(sx-gr,sy-gr,gr*2,gr*2);
                }
            }
            // Fill
            g2.setColor(new Color(color.getRed()/4, color.getGreen()/4, color.getBlue()/4, 200));
            g2.fillOval(sx-r, sy-r, r*2, r*2);
            // Border
            g2.setColor(color);
            g2.setStroke(new BasicStroke(hovered?2.5f:1.5f));
            g2.drawOval(sx-r, sy-r, r*2, r*2);
            // Icon
            String icon = (data instanceof Person)
                ? (((Person)data).getRole()==Person.Role.VICTIM ? "V" : (((Person)data).getRole()==Person.Role.WITNESS?"W":"S"))
                : "E";
            g2.setFont(new Font("Courier New",Font.BOLD,10));
            g2.setColor(color);
            g2.drawString(icon, sx-4, sy+4);
            // Label below
            g2.setFont(font);
            g2.setColor(textColor);
            String lbl = getLabel();
            int lw = g2.getFontMetrics().stringWidth(lbl);
            g2.drawString(lbl, sx-lw/2, sy+r+13);
        }
    }
}
