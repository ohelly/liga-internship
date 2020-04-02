package ru.liga;

import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class MidiFileUtilsTest {
	private static MidiFile midiFileBelle;
	private static MidiFile midiFileWreck;
	private static MidiFile midiFileUnd;
	private List<MidiTrack> trackWithWordsUnd = new ArrayList<>();
	private List<MidiTrack> trackWithWordsBelle = new ArrayList<>();
	private List<MidiTrack> trackWithWordsWreck = new ArrayList<>();
	private MidiTrack melodyUnd;
	private MidiTrack melodyBelle;
	private MidiTrack melodyWreck;

	@Before
	public void setUp() throws IOException {
		midiFileBelle = new MidiFile(new FileInputStream("src/main/resources/Belle.mid"));
		midiFileWreck = new MidiFile(new FileInputStream("src/main/resources/Wrecking Ball.mid"));
		midiFileUnd = new MidiFile(new FileInputStream("src/main/resources/Underneath Your Clothes.mid"));
		trackWithWordsUnd.add(midiFileUnd.getTracks().get(3));
		trackWithWordsBelle.add(midiFileBelle.getTracks().get(3));
		trackWithWordsWreck.add(midiFileWreck.getTracks().get(12));
		melodyUnd = midiFileUnd.getTracks().get(2);
		melodyWreck = midiFileWreck.getTracks().get(9);
		melodyBelle = midiFileBelle.getTracks().get(9);
	}

	@Test
	public void getTrackWithWordsTest() {
		assertEquals(trackWithWordsBelle, MidiFileUtils.getTracksWithWords(midiFileBelle.getTracks()));
		assertEquals(trackWithWordsUnd, MidiFileUtils.getTracksWithWords(midiFileUnd.getTracks()));
		assertEquals(trackWithWordsWreck, MidiFileUtils.getTracksWithWords(midiFileWreck.getTracks()));
	}

	@Test
	public void comparisonWordsBetweenNotesTest() {
		assertTrue(MidiFileUtils.comparisonWordsBetweenNotes(trackWithWordsBelle.get(0).getEvents(),
				MidiFileUtils.eventsToNotes(melodyBelle.getEvents())));
		assertTrue(MidiFileUtils.comparisonWordsBetweenNotes(trackWithWordsUnd.get(0).getEvents(),
				MidiFileUtils.eventsToNotes(melodyUnd.getEvents())));
		assertTrue(MidiFileUtils.comparisonWordsBetweenNotes(trackWithWordsWreck.get(0).getEvents(),
				MidiFileUtils.eventsToNotes(melodyWreck.getEvents())));
	}

	@Test
	public void getCopyOfMidiFileTest() {
		assertEquals(midiFileBelle.getTracks(), MidiFileUtils.getCopyOfMidiFile(midiFileBelle).getTracks());
		assertEquals(midiFileUnd.getTracks(), MidiFileUtils.getCopyOfMidiFile(midiFileUnd).getTracks());
		assertEquals(midiFileWreck.getTracks(), MidiFileUtils.getCopyOfMidiFile(midiFileWreck).getTracks());
	}
}