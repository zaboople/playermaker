package org.tmotte.keyboard;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.util.*;
import java.util.function.IntSupplier;

/**
 * Piano renders black & white keys and plays the notes for a MIDI
 * channel.
 */
public class PanelPiano extends JComponent {

    // Constants:
    public final static int ACTION_MOUSE_OVER=0, ACTION_MOUSE_CLICK=1, ACTION_CLICK_ON_CLICK_OFF=2;
    private final static Color jfcBlue = new Color(204, 204, 255);
    private final static Color pink = new Color(255, 175, 175);
    private final static int kw = 34, kh = 100;
    private final static int octaves=6;
    private final static int whiteIDs[] = { 0, 2, 4, 5, 7, 9, 11 };

    // Final objects:
    private final SynthWrapper synthWrapper;
    private final java.util.List<Key>
        allKeys=new ArrayList<>(octaves * 12),
        whiteKeys = new ArrayList<>(octaves * 7),
        blackKeys = new ArrayList<>(octaves * 5);

    // Changeable state:
    private Key prevKey;
    private int pianoTriggerAction=ACTION_MOUSE_OVER;
    private boolean mute = false;
    private int transpose = 24;


    public PanelPiano(SynthWrapper synthWrapper) {
        //setLayout(new BorderLayout());
        this.synthWrapper=synthWrapper;

        Dimension desiredSize=new Dimension(1+(42*kw), kh+1);
        setPreferredSize(desiredSize);
        setMinimumSize(desiredSize);
        setMaximumSize(desiredSize);

        for (int i = 0, x = 0; i < octaves; i++)
            for (int j = 0; j < whiteIDs.length; j++, x += kw) {
                int keyNum = i * 12 + whiteIDs[j];
                whiteKeys.add(new Key(x, 0, kw, kh, keyNum));
            }
        int bkw=(kw/2), bkh=kh/2;
        int xoffset=bkw/2;
        for (int i = 0, x = 0; i < octaves; i++, x += kw) {
            int keyNum = i * 12;
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

        addMouseMotionListener(myMouseMoveListener);
        addMouseListener(myMouseListener);
        addKeyListener(MyKeyListener);
    }

    /////////////////////////////////////////
    // PUBLIC METHODS EXPOSED FOR PnlMain: //
    /////////////////////////////////////////

    public void setPianoTriggerAction(int action) {
        pianoTriggerAction=action;
        if (pianoTriggerAction!=ACTION_CLICK_ON_CLICK_OFF) {
            allNotesOff();
            mute=false;
            repaint();
        }
    }

    public void flipMute() {
        mute=!mute;
        for (Key key: allKeys) key.flipMute();
    }

    public boolean isMuted() {
        return mute;
    }

    /** Transposes the keyboard by octave-1 (i.e. 1 is lowest) octaves. */
    public void setOctave(int octave) {
        assert(octave > 0);
        transpose=(octave-1) * 12;
    }

    public void allNotesOff() {
        for (Key key: allKeys) key.off();
    }

    //////////////////////
    // SWING OVERRIDES: //
    //////////////////////

    public @Override boolean isFocusable() {
        return true;
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
            g2.setColor(key==prevKey && hasFocus() ?Color.GREEN :Color.black);
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
                g2.setColor(key==prevKey && hasFocus() ?Color.GREEN :Color.black);
                g2.fill(key);
            }
        }
    }

    ////////////////////
    // PRIVATE LOGIC: //
    ////////////////////

    /**
     * Black and white keys or notes on the piano.
     */
    private class Key extends Rectangle {
        private boolean noteOn = false;
        private int kNum;
        public Key(int x, int y, int width, int height, int num) {
            super(x, y, width, height);
            kNum = num;
        }
        public boolean isNoteOn() {
            return noteOn;
        }
        public void flip() {
            if (isNoteOn()) off();
            else on();
        }
        public void flipMute() {
            if (isNoteOn()) {
                if (mute)
                    synthWrapper.sendNoteOff(kNum + transpose);
                else
                    on();
            }
        }
        public void on() {
            noteOn=true;
            if (!mute)
                synthWrapper.sendNoteOn(kNum + transpose);
        }
        public void off() {
            noteOn =false;
            synthWrapper.sendNoteOff(kNum + transpose);
        }
        public String toString() {
            return kNum +" "+x+" "+width;
        }
    } // End class Key


    private MouseMotionListener myMouseMoveListener = new MouseMotionAdapter() {
        public @Override void mouseMoved(MouseEvent e) {
            if (pianoTriggerAction==ACTION_MOUSE_OVER) {
                Key key = getKey(e.getPoint());
                if (prevKey != null && prevKey != key)
                    prevKey.off();
                if (key != null && !key.isNoteOn())
                    key.on();
                prevKey = key;
                repaint();
            }
        }
    };

    private MouseListener myMouseListener = new MouseListener() {
        public @Override void mousePressed(MouseEvent e) {
            prevKey = getKey(e.getPoint());
            if (prevKey != null) {
                if (isClickOnClickOff())
                    prevKey.flip();
                else
                    prevKey.on();
                repaint();
            }
        }
        public @Override void mouseReleased(MouseEvent e) {
            if (prevKey != null) {
                if (!isClickOnClickOff())
                    prevKey.off();
                repaint();
            }
        }
        public @Override void mouseExited(MouseEvent e) {
            if (prevKey != null) {
                if (!isClickOnClickOff())
                    prevKey.off();
                repaint();
            }
        }
        public @Override void mouseClicked(MouseEvent e) { }
        public @Override void mouseEntered(MouseEvent e) { }
    };

    private KeyListener MyKeyListener = new KeyAdapter() {
        public @Override void keyPressed(KeyEvent e) {
            int keyCode=e.getKeyCode();
            final boolean
                bLeft=keyCode==KeyEvent.VK_LEFT,
                bRight=keyCode==KeyEvent.VK_RIGHT,
                bSpace=keyCode==KeyEvent.VK_SPACE;
            if (bLeft || bRight) {
                int keyIndex=prevKey==null
                    ?0
                    :(prevKey.kNum + (bLeft ?-1 :+1));
                if (keyIndex<0)
                    keyIndex=allKeys.size()-1;
                else
                if (keyIndex>=allKeys.size())
                    keyIndex=0;
                prevKey=allKeys.get(keyIndex);
            }
            else
            if (bSpace) {
                if (prevKey==null)
                    prevKey=allKeys.get(0);
                prevKey.flip();
            }
            repaint();
        }
    };

    private boolean isClickOnClickOff() {
        return pianoTriggerAction==ACTION_CLICK_ON_CLICK_OFF;
    }

    private Key getKey(Point point) {
        Key closestWhite=whiteKeys.get((point.x-1) / kw);
        int whiteIndex=closestWhite.kNum;
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


} // End class Piano
