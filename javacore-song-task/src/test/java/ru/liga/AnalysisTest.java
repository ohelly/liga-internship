package ru.liga;

import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.meta.Tempo;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.Assert.*;

public class AnalysisTest {

	private static Analysis undAnalysis;
	private static Analysis wreckAnalysis;
	private static Analysis belleAnalysis;

	@Before
	public void setUp() throws IOException {
		MidiFile midiFileBelle = new MidiFile(new FileInputStream("src/main/resources/Belle.mid"));
		MidiFile midiFileWreck = new MidiFile(new FileInputStream("src/main/resources/Wrecking Ball.mid"));
		MidiFile midiFileUnd = new MidiFile(new FileInputStream("src/main/resources/Underneath Your Clothes.mid"));
		MidiTrack melodyUnd = midiFileUnd.getTracks().get(2);
		MidiTrack melodyWreck = midiFileWreck.getTracks().get(9);
		MidiTrack melodyBelle = midiFileBelle.getTracks().get(9);
		undAnalysis = new Analysis(melodyUnd, (Tempo) midiFileUnd.getTracks().get(0).getEvents().last(), midiFileUnd);
		wreckAnalysis = new Analysis(melodyWreck, (Tempo) midiFileWreck.getTracks().get(0).getEvents().last(), midiFileWreck);
		belleAnalysis = new Analysis(melodyBelle, (Tempo) midiFileBelle.getTracks().get(0).getEvents().last(), midiFileBelle);
	}

	@Test
	public void getMaxNoteValueTest() {
		assertEquals(72, (int)undAnalysis.getMaxNoteValue());
		assertEquals(68, (int)belleAnalysis.getMaxNoteValue());
		assertEquals(70, (int)wreckAnalysis.getMaxNoteValue());
	}

	@Test public void getMinNoteValueTest() {
		assertEquals(56, (int)undAnalysis.getMinNoteValue());
		assertEquals(46, (int)belleAnalysis.getMinNoteValue());
		assertEquals(53, (int)wreckAnalysis.getMinNoteValue());
	}

	@Test
	public void getRangeTest() {
		assertEquals(16, (int)undAnalysis.getRange());
		assertEquals(22, (int)belleAnalysis.getRange());
		assertEquals(17, (int)wreckAnalysis.getRange());
	}

	@Test
	public void getMapOfCountTest() {
		assertEquals(9, undAnalysis.getMapOfCount().size());
		assertEquals(12, belleAnalysis.getMapOfCount().size());
		assertEquals(7, wreckAnalysis.getMapOfCount().size());
	}

	@Test
	public void getMapOfDurationTest() {
		assertEquals(40, undAnalysis.getMapOfDuration().size());
		assertEquals(12, belleAnalysis.getMapOfDuration().size());
		assertEquals(8, wreckAnalysis.getMapOfDuration().size());
	}
}