package org.tmotte.keyboard;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.event.*;
import javax.sound.midi.*;
import java.util.*;
import java.util.function.Consumer;
import java.io.File;
import java.io.IOException;

import org.tmotte.common.swang.GridBug;
import org.tmotte.common.function.Except;
import org.tmotte.common.midi.MetaInstrument;

/**
 * This was adapted from an utterly horrible piece of work distributed by the once-mighty
 * Sun Microsystems and has been cleaned up reasonably well.
 */
public class PanelMain {

    public static void startApplication(SynthWrapper synthWrapper) {
		// Get main frame ready:
        JFrame f = new JFrame("Midi Synthesizer");
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });
        f.addKeyListener(new KeyAdapter() {
	        public @Override void keyReleased(KeyEvent k) {if (k.getKeyCode()==KeyEvent.VK_ESCAPE) System.exit(0);}
        });
        final PanelMain pnlMain = new PanelMain(f, synthWrapper);

		// Handle errors so that we know they happened:
	    Thread.setDefaultUncaughtExceptionHandler(
	    	new Thread.UncaughtExceptionHandler() {
		        public void uncaughtException(Thread t, Throwable e){
			        pnlMain.handleError(e);
		        }
	    	}
	    );

		// Build up the main panel and get rolling:
        f.pack();
        f.setVisible(true);
        pnlMain.jtfInstrument.requestFocusInWindow();
    }

	// State / Model:
	private final SynthWrapper synthWrapper;
    private final Map<String, Integer> instrumentNameToIndex=new HashMap<>();
    private final java.util.List<String> instrumentNames=new ArrayList<>(512);
    private boolean useDefaultInstruments=true;

	// Controls:
    private JPanel pnlPiano;
    private JSlider sliderVolume, sliderPressureVibrato, sliderBend, sliderReverb;
    private final JCheckBox mouseOverCB = new JCheckBox("Mouse over (not click)", true);
    private final JLabel lblError=new JLabel("Error");

    private JComboBox<String> jcbChannel;
    private JButton btnRecord, btnPlay, btnSave, btnDelete, btnOpen;
    private final static String[] tblTrackColNames = { "Track #", "Channel #", "Track name" };
    private TableTrackModel tblTrackModel;
    private JTable tblTrack;
    private JScrollPane sptblTrack;
    private Optional<JFileChooser> saveFileChooser=Optional.empty();

    private JLabel lblInstrument;
    private final JTextField jtfInstrument=new JTextField();
    private JScrollPane jspInstrument;
    private final DefaultListModel<String> dlfInstrument=new DefaultListModel<>();
    private JList<String> jlInstrument=new JList<>(dlfInstrument);
    private MetaInstruments metaInstruments;


    public PanelMain(Container c, SynthWrapper synthWrapper) {
	    this.synthWrapper=synthWrapper;
	    metaInstruments=synthWrapper.getMetaInstruments();

	    create();
	    layout(c);
	    listen();

	    searchForInstrument();
	    setChannelSliderValues();
        tblTrackChanged();
        btnPlay.setEnabled(tblTrackModel.getRowCount()>0);
	}

    private void create() {
        jcbChannel = new JComboBox<>();
		int count=synthWrapper.getChannelCount();
        for (int i = 1; i <= count; i++)
            jcbChannel.addItem("Channel " + String.valueOf(i));

        sliderVolume = createSlider("Velocity (Volume)");
        sliderPressureVibrato = createSlider("Pressure (Vibrato)");
        sliderReverb = createSlider("Reverb");
		// create a slider with a 14-bit range of values for pitch-bend
        sliderBend = create14BitSlider("Bend");
        lblError.setForeground(Color.RED);
        lblError.setVisible(false);

		lblInstrument=new JLabel("Instrument: ");
		jlInstrument.setVisibleRowCount(28);
		jlInstrument.setLayoutOrientation(JList.VERTICAL_WRAP);
		jspInstrument=new JScrollPane(jlInstrument);

		jtfInstrument.setText("*");

        btnRecord = createButton("Record", true);
        btnPlay = createButton("Play", false);
        btnSave = createButton("Save...", false);
        btnDelete = createButton("Delete", false);
        btnOpen = createButton("Open...", true);

        tblTrack = new JTable(tblTrackModel = new TableTrackModel());
        for (String s: tblTrackColNames)
            tblTrack.getColumn(s).setMinWidth(65);
        sptblTrack = new JScrollPane(tblTrack);
        sptblTrack.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        Dimension prefer=new Dimension(5*65, 130);
		sptblTrack.setMinimumSize(prefer);
		sptblTrack.setMaximumSize(prefer);
		sptblTrack.setPreferredSize(prefer);
        tblTrack.doLayout();

		pnlPiano=new PanelPiano(synthWrapper, ()->mouseOverCB.isSelected());
	}
    private static JButton createButton(String name, boolean state) {
        JButton b = new JButton(name);
        //b.setFont(new Font("serif", Font.PLAIN, 10));
        b.setEnabled(state);
        b.setPreferredSize(new Dimension(90, 30));
        return b;
    }

    private JFileChooser getFileChooser() {
	    return saveFileChooser.orElseGet(()->{
            JFileChooser fc = new JFileChooser(
	            new File(System.getProperty("user.dir"))
            );
            fc.setAcceptAllFileFilterUsed(true);
            saveFileChooser=Optional.of(fc);
            return fc;
        });
    }

    private class TableTrackModel extends AbstractTableModel {
        public int getColumnCount() { return tblTrackColNames.length; }
        public int getRowCount() { return synthWrapper.getTrackCount();}
        public Object getValueAt(int row, int col) {
            switch (col) {
                case 0: return row;
                case 1: return synthWrapper.getTrackChannel(row);
                case 2: return synthWrapper.getTrackName(row);
                default: throw new RuntimeException("Unexpected column: "+col);
            }
        }
        public String getColumnName(int col) {return tblTrackColNames[col]; }
        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }
        public boolean isCellEditable(int row, int col) {
            return col==1 || col==2;
        }
        public void setValueAt(Object val, int row, int col) {
            if (col == 1)
                synthWrapper.setTrackChannel(row, (Integer) val);
            else if (col == 2)
                synthWrapper.setTrackName(row, (String) val);
        }
    }


    private JSlider createSlider(String name) {
	    return createSlider(name, false);
    }
    private JSlider create14BitSlider(String name) {
	    return createSlider(name, true);
	}
    private JSlider createSlider(String name, boolean fourteenBit) {
        JSlider slider = fourteenBit
	        ?new JSlider(JSlider.HORIZONTAL, 0, 16383, 8192)
	        :new JSlider(JSlider.HORIZONTAL, 0, 127, 64);
        TitledBorder tb = new TitledBorder(new LineBorder(Color.LIGHT_GRAY, 1));
        slider.setBorder(tb);
        tb.setTitle(name + " = "+ (fourteenBit ?8192 :64));
        return slider;
    }



	/////////////
	// LAYOUT: //
	/////////////

	private void layout(Container c){
        GridBug gb=new GridBug(c);
		gb.setInsets(2);

		// Piano:
        gb.anchor(gb.NORTHWEST);
        //gb.setFill(gb.BOTH);
        gb.weightXY(1.0, 0.0);
        gb.add(pnlPiano);

		// Various controls in middle:
        gb.weightXY(1, 0);
        gb.setFill(gb.HORIZONTAL);
        gb.addY(layoutCenterControls());

		// Instrument textbox:
		gb
			.insetBottom(0)
			.addY(lblInstrument)
			.insetTop(0)
			.weightXY(1,0)
			.setFill(gb.HORIZONTAL)
			.addY(jtfInstrument);

		// Instrument List
		gb.setFill(gb.BOTH)
			.weightXY(0, 1)
			.insetBottom(2)
	        .addY(jspInstrument);
    }


    private Container layoutCenterControls() {
        return new GridBug(new JPanel())
            .anchor(GridBug.NORTHWEST)
            .fill(GridBug.HORIZONTAL)
            .weightY(1)
            .weightX(.33)
            .add(layoutLeftControls())
            .insetLeft(20)
            .weightX(.33)
	        .addX(layoutPanelRecord())
	        .getContainer();
    }

    private Container layoutLeftControls(){
        return new GridBug(new JPanel())
            .anchor(GridBug.NORTHWEST)
            .fill(GridBug.HORIZONTAL)
            .weightX(1)
            .add(layoutSliders())
            .weightY(1)
            .addY(mouseOverCB)
            .addY(lblError)
            .getContainer();
    }

    private Container layoutSliders() {
        return new GridBug(new JPanel())
            .anchor(GridBug.NORTHWEST)
            .fill(GridBug.HORIZONTAL)
            .weightX(1)
            .gridX(0)
            .add(sliderVolume)
            .addX(sliderPressureVibrato)
            .gridX(0)
            .weightY(1)
            .addY(sliderReverb)
            .addX(sliderBend)
            .getContainer();
    }

    private Container layoutPanelRecord() {
        return new GridBug(new JPanel())
            .anchor(GridBug.NORTHWEST)
            .fill(GridBug.NONE)
            .add(getRecordLeftColumn())
            .weightXY(0, 1).insetTop(5).addX(sptblTrack)
            .fill(GridBug.VERTICAL)
            .weightXY(1, 0).insetLeft(5).addX(
	            new GridBug(new JPanel())
		            .insets(0)
		            .anchor(GridBug.NORTHWEST)
		            .fill(GridBug.NONE)
		            .add(btnDelete)
		            .fill(GridBug.BOTH)
		            .weightXY(1,1)
		            .addY(new JPanel())
		            .anchor(GridBug.SOUTHWEST)
		            .fill(GridBug.NONE)
		            .insetBottom(5)
		            .addY(btnOpen)
		            .getContainer()
            )
            .getContainer();
    }

    private Container getRecordLeftColumn() {
        return new GridBug(new JPanel())
            .anchor(GridBug.NORTHWEST)
            .fill(GridBug.NONE)
            .insetTop(5)
            .insetLeft(5)
            .insetRight(5)
            .weightX(1)
            .add(jcbChannel)
            .addY(btnRecord)
            .addY(btnPlay)
            .insetBottom(5)
            .addY(btnSave)
            .getContainer();
    }


	/////////////
	// LISTEN: //
	/////////////

	private void listen() {
        jcbChannel.addActionListener(e->jcbChannelChanged());
		jlInstrument.addListSelectionListener(x-> {if (!x.getValueIsAdjusting()) instrumentPicked();});
		jtfInstrument.getDocument().addDocumentListener(new DocumentListener(){
	        public @Override void insertUpdate(DocumentEvent e)  {searchForInstrument();}
	        public @Override void removeUpdate(DocumentEvent e)  {searchForInstrument();}
	        public @Override void changedUpdate(DocumentEvent e) {searchForInstrument();}
		});
        btnRecord.addActionListener(e -> clickRecord());
        btnPlay.addActionListener(e -> clickPlay());
        btnSave.addActionListener(e -> clickSave());
        btnDelete.addActionListener(e -> clickDelete());
        btnOpen.addActionListener(e -> clickOpen());
        sliderReverb.addChangeListener(e -> sliderChanged(sliderReverb));
        sliderVolume.addChangeListener(e -> sliderChanged(sliderVolume));
        sliderPressureVibrato.addChangeListener(e -> sliderChanged(sliderPressureVibrato));
        sliderBend.addChangeListener(e -> sliderChanged(sliderBend));

        synthWrapper.setSequenceEndCallback(
	        ()->playbackComplete()
        );
		tblTrack.getSelectionModel().addListSelectionListener(
		    (ListSelectionEvent e) -> {
			    if (!e.getValueIsAdjusting())
				    trackSelected();
		    }
		);
	}

    private void sliderChanged(JSlider slider) {
	    setChannelSliderValues();
        setSliderTitle(slider);
        slider.repaint();
    }

    private void setSliderTitle(JSlider slider) {
        int value = slider.getValue();
        TitledBorder tb = (TitledBorder) slider.getBorder();
        String s = tb.getTitle();
        tb.setTitle(s.substring(0, s.indexOf('=')+1) + s.valueOf(value));
    }

    private void jcbChannelChanged() {
	    getSelectedInstrument().ifPresent(instr->{
			synthWrapper.instrumentChannelChange(instr, jcbChannel.getSelectedIndex());
	        setChannelSliderValues();
        });
    }


    private void clickRecord() {
        boolean record = btnRecord.getText().startsWith("Record");
        Optional<MetaInstrument> optInstrument=getSelectedInstrument();
        if (record) {
	        optInstrument.ifPresent(metaInstr->{
		        if (synthWrapper.getTrackCount()>0)
			        synthWrapper.playBack();
	            synthWrapper.startRecord(metaInstr);
	            btnRecord.setText("Stop record");
	            btnPlay.setEnabled(false);
	            btnSave.setEnabled(false);
            });
        } else {
            synthWrapper.stopRecord(optInstrument);
	        if (synthWrapper.getTrackCount()>1)
	            synthWrapper.stopPlayback();
            tblTrackChanged();
            tblTrack.sizeColumnsToFit(0);
            btnRecord.setText("Record");
            btnPlay.setEnabled(true);
            btnSave.setEnabled(true);
            btnSave.setText("Save...");
        }
    }
    private void clickPlay() {
        if (btnPlay.getText().startsWith("Play")) {
            synthWrapper.playBack();
            btnPlay.setText("Stop play");
            btnRecord.setEnabled(false);
        } else {
            synthWrapper.stopPlayback();
            playbackComplete();
        }
    }
    private void playbackComplete() {
	    // This is on the synthesizer thread and won't bubble up to the
	    // uncaught exception handler because... shrug. I guess that thing
	    // eats errors.
	    try {
		    getSelectedInstrument().ifPresent(instr-> {
			    // Things tend to go awry after playback, so this will try to reset the mindset:
				synthWrapper.instrumentChannelChange(instr, jcbChannel.getSelectedIndex());
				setChannelSliderValues();
			});
	        btnPlay.setText("Play");
	        btnRecord.setEnabled(true);
        } catch (Exception e) {
	        handleError(e);
        }
    }
    private void clickSave() {
        Except.run(()->{
            JFileChooser fc = getFileChooser();
            if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                synthWrapper.saveMidiFile(fc.getSelectedFile());
                btnSave.setText("<html><i>Save...</i></html>");
            }
        });
    }
    private void clickDelete() {
	    Optional.of(tblTrack.getSelectedRows())
		    .filter(array->array.length>0)
		    .ifPresent(array->{
			    for (int i=array.length-1; i>=0; i--)
				    synthWrapper.deleteTrack(array[i]);
			    tblTrackChanged();
		    });
    }
    private void clickOpen() {
        Except.run(()->{
            JFileChooser fc = getFileChooser();
            if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
	            synthWrapper.openSequence(fc.getSelectedFile());
                btnSave.setText("<html><i>Save...</i></html>");
                btnPlay.setEnabled(true);
                tblTrackChanged();
            }
        });
    }

	private void trackSelected() {
		btnDelete.setEnabled(tblTrack.getSelectedRows().length>0);
	}
	private void tblTrackChanged() {
        tblTrack.tableChanged(new TableModelEvent(tblTrackModel));
	}




	private void searchForInstrument() {
		dlfInstrument.clear();
		metaInstruments.searchByName(
			jtfInstrument.getText().trim(),
			(String displayName) -> dlfInstrument.addElement(displayName)
		);
		if (dlfInstrument.size()>0) jlInstrument.setSelectedIndex(0);
	}

	private void instrumentPicked() {
		getSelectedInstrument().ifPresent(instr -> {
			lblInstrument.setText("Instrument: "+instr.displayName+"  -  Bank: "+instr.getBank()+" Program: "+instr.getProgram());
			int currChannel=jcbChannel.getSelectedIndex();
			int newChannel=synthWrapper.suggestChannel(instr, currChannel);
			synthWrapper.instrumentChannelChange(instr, newChannel);
			if (currChannel!=newChannel)
				jcbChannel.setSelectedIndex(newChannel);
			else
				setChannelSliderValues();
		});
	}

	private void setChannelSliderValues() {
        MetaChannel cc=synthWrapper.getChannel();
        cc.setVolume(sliderVolume.getValue());
        cc.setPressure(sliderPressureVibrato.getValue());
        cc.setBend(sliderBend.getValue());
        cc.setReverb(sliderReverb.getValue());
	}

    private Optional<MetaInstrument> getSelectedInstrument() {
	    return Optional.ofNullable(jlInstrument.getSelectedValue())
		    .map(metaInstruments::get);
    }

    private void handleError(Throwable e) {
	    e.printStackTrace();
	    lblError.setText("Error: "+e.getMessage()+" (see stdout for more detail)");
	    lblError.setVisible(true);
    }

    public void close() {
	    synthWrapper.close();
    }

}
