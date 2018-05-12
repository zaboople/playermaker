package org.tmotte.pm;
import javax.sound.midi.*;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;

/** Look in java System properties for sound values. */
public class Comp1 extends MyMidi2  {
	final static int spaceVoice=91;
	final static int squareWave=80;
	final static int contraBass=43;
	final static int honkyTonkPiano=3;
	public @Override void init() {
		octave(3);
		int x=10;
		int ps=240;
		setBeatsPerMinute(70);
		instrument(contraBass);
		System.out.println((60 * 120) / 480);
		_4(C);
		_4(C);
		for (int j=0; j<4; j++) {
			_16(A);
			_8(B);
			_16(C+1);
			x4(E, G, D);
			_16(G);
			_16(A+12);
			_16(E);
		}
		_4(C);
		_16(G);
		_16(A+12);
		_16(E);
		_4(C);
		_16(C);
	}
	private static void bpmToBeatFactor(int bpm) {
		/*  factor  bpm
		    240     30
			120     60
			60     120
			30     240
			15     480
		(60 / bpm) * 120
		*/
	}
    public static void main(String args[]) throws Exception {
	    new Comp1().play(true);
    }

}
