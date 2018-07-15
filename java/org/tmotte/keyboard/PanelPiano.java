package org.tmotte.keyboard;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.event.*;
import java.util.*;
import java.util.function.Supplier;

/**
 * Piano renders black & white keys and plays the notes for a MIDI
 * channel.
 */
public class PanelPiano extends JPanel implements MouseListener {
    private final static int ON = 0, OFF = 1;
    private final static Color jfcBlue = new Color(204, 204, 255);
    private final static Color pink = new Color(255, 175, 175);
    private final static int kw = 34, kh = 100;
    private final static int transpose = 24;
    private final static int octaves=6;
    private final static int whiteIDs[] = { 0, 2, 4, 5, 7, 9, 11 };

    private final SynthWrapper synthWrapper;
    private final Supplier<Boolean> mouseOverSelector;
    private final java.util.List<Key>
        allKeys=new ArrayList<>(octaves * 12),
        whiteKeys = new ArrayList<>(octaves * 7),
        blackKeys = new ArrayList<>(octaves * 5);
    private Key prevKey;


    public PanelPiano(SynthWrapper synthWrapper, Supplier<Boolean> mouseOverSelector) {
        //setLayout(new BorderLayout());
        this.synthWrapper=synthWrapper;
        this.mouseOverSelector=mouseOverSelector;

        Dimension desiredSize=new Dimension(1+(42*kw), kh+1);
        setPreferredSize(desiredSize);
        setMinimumSize(desiredSize);
        setMaximumSize(desiredSize);

        for (int i = 0, x = 0; i < octaves; i++)
            for (int j = 0; j < whiteIDs.length; j++, x += kw) {
                int keyNum = i * 12 + whiteIDs[j] + transpose;
                whiteKeys.add(new Key(x, 0, kw, kh, keyNum));
            }
        int bkw=(kw/2), bkh=kh/2;
        int xoffset=bkw/2;
        for (int i = 0, x = 0; i < octaves; i++, x += kw) {
            int keyNum = i * 12 + transpose;
            blackKeys.add(new Key((x += kw)-xoffset, 0, bkw, bkh, keyNum+1));
            blackKeys.add(new Key((x += kw)-xoffset, 0, bkw, bkh, keyNum+3));
            x += kw;
            blackKeys.add(new Key((x += kw)-xoffset, 0, bkw, bkh, keyNum+6));
            blackKeys.add(new Key((x += kw)-xoffset, 0, bkw, bkh, keyNum+8));
            blackKeys.add(new Key((x += kw)-xoffset, 0, bkw, bkh, keyNum+10));
        }
        Collections.sort(whiteKeys, (key1, key2)->key1.x-key2.x);
        allKeys.addAll(whiteKeys);
        allKeys.addAll(blackKeys);
        Collections.sort(allKeys, (key1, key2)->key1.x-key2.x);

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {
                if (mouseOverSelector.get()) {
                    Key key = getKey(e.getPoint());
                    if (prevKey != null && prevKey != key)
                        prevKey.off();
                    if (key != null && prevKey != key)
                        key.on();
                    prevKey = key;
                    repaint();
                }
            }
        });
        addMouseListener(this);
    }
    public void allNotesOff() {
        for (int i = 0; i < allKeys.size(); i++)
            allKeys.get(i).setNoteState(OFF);
    }

    public @Override void mousePressed(MouseEvent e) {
        prevKey = getKey(e.getPoint());
        if (prevKey != null) {
            prevKey.on();
            repaint();
        }
    }
    public @Override void mouseReleased(MouseEvent e) {
        if (prevKey != null) {
            prevKey.off();
            repaint();
        }
    }
    public @Override void mouseExited(MouseEvent e) {
        if (prevKey != null) {
            prevKey.off();
            repaint();
            prevKey = null;
        }
    }
    public @Override void mouseClicked(MouseEvent e) { }
    public @Override void mouseEntered(MouseEvent e) { }


    private Key getKey(Point point) {
        Key closestWhite=whiteKeys.get((point.x-1) / kw);
        int whiteIndex=closestWhite.kNum-transpose;
        if (whiteIndex>0) {
            Key leftBlack=allKeys.get(whiteIndex-1);
            if (leftBlack.contains(point))
                return leftBlack;
        }
        if (whiteIndex<allKeys.size()-1) {
            Key rightBlack=allKeys.get(whiteIndex+1);
            if (rightBlack.contains(point))
                return rightBlack;
        }
        return closestWhite;
    }

    public @Override void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Dimension d = getSize();

        g2.setBackground(getBackground());
        g2.clearRect(0, 0, d.width, d.height);

        g2.setColor(Color.white);
        g2.fillRect(0, 0, 42*kw, kh);

        for (int i = 0; i < whiteKeys.size(); i++) {
            Key key = (Key) whiteKeys.get(i);
            if (key.isNoteOn()) {
                g2.setColor(synthWrapper.recording() ? pink : jfcBlue);
                g2.fill(key);
            }
            g2.setColor(Color.black);
            g2.draw(key);
        }
        for (int i = 0; i < blackKeys.size(); i++) {
            Key key = (Key) blackKeys.get(i);
            if (key.isNoteOn()) {
                g2.setColor(synthWrapper.recording() ? pink : jfcBlue);
                g2.fill(key);
                g2.setColor(Color.black);
                g2.draw(key);
            } else {
                g2.setColor(Color.black);
                g2.fill(key);
            }
        }
    }

    /**
     * Black and white keys or notes on the piano.
     */
    private class Key extends Rectangle {
        private int noteState = OFF;
        private int kNum;
        public Key(int x, int y, int width, int height, int num) {
            super(x, y, width, height);
            kNum = num;
        }
        public boolean isNoteOn() {
            return noteState == ON;
        }
        public void on() {
            setNoteState(ON);
            synthWrapper.sendNoteOn(kNum);
        }
        public void off() {
            setNoteState(OFF);
            synthWrapper.sendNoteOff(kNum);
        }
        public void setNoteState(int state) {
            noteState = state;
        }
        public String toString() {
            return kNum +" "+x+" "+width;
        }
    } // End class Key

} // End class Piano
