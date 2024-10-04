package org.tmotte.common.system;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.sound.midi.*;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Optional;
import java.util.List;

import org.tmotte.common.function.Except;

/**
 * Useful for printing system information about internal synthesizers, sequencers, etc.
 * Originally ripped off from a StackOverflow answer, but unrecognizable at this point.
 */
public class MediaTypes2 {

    private MediaTypes2(){}

    /** Runs the system scan and prints results to command line.
        @param args ignored
    */
    public static void main(String[] args) {
        getAll().print();
    }

    /**
     * Just represents a hierarchy of nodes
     */
    static class MyNode {
        public MyNode(String name){
            this.name=name;
        }
        String name;
        List<MyNode> children;
        public void add(MyNode child) {
            if (children==null)
                children=new ArrayList<>();
            children.add(child);
        }
        public MyNode add(String child) {
            MyNode m=new MyNode(child);
            add(m);
            return m;
        }
        public void add(Optional<MyNode> child) {
            child.ifPresent(this::add);
        }
        public void print() {
            print(System.out);
        }
        public void print(Appendable app) {
            Except.run(()->{
                print(0, app);
                app.append("******************************\n");
            });
        }

        private void print(int level, Appendable app) throws Exception {
            if (level > 0) {
                if (level==1)
                    app.append("******************************\n");
                for (int i=1; i<level; i++)
                    app.append("  ");
                app.append(name).append("\n");
            }
            if (children!=null)
                for (MyNode c: children)
                    c.print(level+1, app);
        }
    }


    /** Gets the system data as a MyNode hierarchy
        @return The hierarchy
    */
    public static MyNode getAll() {
        MyNode root=new MyNode(null);

        // Sound file types, also not remarkable:
        MyNode soundFileTypes=root.add("Sound file types:");
        for (Object s: AudioSystem.getAudioFileTypes())
            soundFileTypes.add(s.toString());

        // Ah, mixers:
        MyNode mixerNodes=root.add("Mixers:");
        for (Mixer.Info mixerInfo: AudioSystem.getMixerInfo()) {
            MyNode mixerNode=mixerNodes.add(mixerInfo.getName());
            mixerNode.add("Vendor / Version: "+mixerInfo.getVendor()+" / "+mixerInfo.getVersion());
            mixerNode.add("Description: "+mixerInfo.getDescription());
            Mixer mixer = AudioSystem.getMixer(mixerInfo);
            mixerNode.add(getLines("Source lines", mixer.getSourceLineInfo()));
            mixerNode.add(getLines("Target lines", mixer.getTargetLineInfo()));
        }

        // Midi types, rather simple:
        MyNode midiNode=root.add("Midi:");
        midiNode.add(getMidiTypes());

        // Midi devices, another interesting subject:
        MyNode midiDeviceNodes=midiNode.add("Devices:");
        for (MidiDevice.Info info: MidiSystem.getMidiDeviceInfo())
            midiDeviceNodes.add(processMidiDeviceInfo(info));

        return root;
    }

    private static Optional<MyNode> getLines(String sourceTarget, Line.Info[] names) {
        if (names.length==0)
            return Optional.empty();
        final MyNode node=new MyNode(sourceTarget+":");
        Except.run(()->{
            for (Line.Info mainInfo: names) {
                MyNode child=node.add(mainInfo.toString());

                Line line=AudioSystem.getLine(mainInfo);
                if (line instanceof DataLine) {
                    DataLine dataLine = (DataLine)line;
                    AudioFormat audioFormat = dataLine.getFormat();
                    child.add("Channels: " + audioFormat.getChannels());
                    child.add("Encoding: " + audioFormat.getEncoding());
                    child.add("Frame Rate: " +audioFormat.getFrameRate());
                    child.add("Sample Rate: " +audioFormat.getSampleRate());
                    child.add("Sample Size (bits): " +audioFormat.getSampleSizeInBits());
                    child.add("Big Endian: " +audioFormat.isBigEndian());
                    child.add("Level: " +dataLine.getLevel());
                }
                if (line instanceof Port) {
                    Port port = (Port)line;
                    Port.Info portInfo = (Port.Info)port.getLineInfo();
                    child.add("Port Name: " +portInfo.getName());
                    String pType="Unknown port type";
                    if (portInfo==Port.Info.COMPACT_DISC) pType="Compact Disc";
                    else
                    if (portInfo==Port.Info.HEADPHONE) pType="Headphone";
                    else
                    if (portInfo==Port.Info.MICROPHONE) pType="Microphone";
                    else
                    if (portInfo==Port.Info.LINE_IN) pType="Line in";
                    else
                    if (portInfo==Port.Info.LINE_OUT) pType="Line out";
                    else
                    if (portInfo==Port.Info.SPEAKER) pType="Speaker";
                    child.add("Port type: "+pType);
                }

                if (!(line instanceof Clip)) {
                    boolean needsOpen=!line.isOpen();
                    if (needsOpen) line.open();
                    Control[] controls=line.getControls();
                    if (controls.length>0) {
                        MyNode controlNode=child.add("Controls:");
                        for (Control control: line.getControls())
                            controlNode.add(control.toString());
                    }
                    if (needsOpen) line.close();
                }
            }
        });
        return Optional.of(node);
    }


    private static MyNode getMidiTypes() {
        MyNode node=new MyNode("Midi file types: ");
        for (int i: MidiSystem.getMidiFileTypes()) {
            String description = "Unknown";
            switch (i) {
                case 0:
                    description = "Single Track";
                    break;
                case 1:
                    description = "Multi Track";
                    break;
                case 2:
                    description = "Multi Song";
            }
            node.add(description);
        }
        return node;
    }

    private static MyNode processMidiDeviceInfo(MidiDevice.Info midiDeviceInfo) {
        MyNode node=new MyNode(midiDeviceInfo.getName());
        node.add("Vendor: " + midiDeviceInfo.getVendor());

        String version = midiDeviceInfo.getVersion();
        node.add("Version: " + version.replaceAll("Version ", ""));
        node.add("Description: " + midiDeviceInfo.getDescription());

        try {
            MidiDevice midiDevice = MidiSystem.getMidiDevice(midiDeviceInfo);

            int transmitters=midiDevice.getMaxTransmitters();
            String strTransmitters=
                transmitters==AudioSystem.NOT_SPECIFIED
                    ?"Not specified" :String.valueOf(transmitters);
            node.add("Maximum Transmitters: "+strTransmitters);


            int receivers=midiDevice.getMaxReceivers();
            String strReceivers=
                receivers==AudioSystem.NOT_SPECIFIED
                    ?"Not specified" :String.valueOf(receivers);
            node.add("Maximum Receivers: "+strReceivers);

        } catch(MidiUnavailableException mue) {
            throw new RuntimeException(mue);
        }
        return node;
    }

}
