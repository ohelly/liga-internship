package ru.liga;

import com.leff.midi.MidiFile;
import com.leff.midi.event.NoteOff;
import com.leff.midi.event.NoteOn;
import com.leff.midi.event.meta.Tempo;
import org.junit.Before;
import org.junit.Test;
import ru.liga.songtask.domain.NoteSign;

import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.Assert.*;

public class TransposeTest {

	private static MidiFile midiFileBelle;
	private static MidiFile midiFileWreck;
	private static MidiFile midiFileUnd;
	private static Transpose undTranspose;
	private static Transpose belleTranspose;
	private static Transpose wreckTranspose;
	private static NoteOff off;
	private static NoteOn on;

	@Before
	public void setUp() throws IOException {
		midiFileBelle = new MidiFile(new FileInputStream("src/main/resources/Belle.mid"));
		midiFileWreck = new MidiFile(new FileInputStream("src/main/resources/Wrecking Ball.mid"));
		midiFileUnd = new MidiFile(new FileInputStream("src/main/resources/Underneath Your Clothes.mid"));
		undTranspose = new Transpose(midiFileUnd);
		belleTranspose = new Transpose(midiFileBelle);
		wreckTranspose = new Transpose(midiFileWreck);
		on = new NoteOn(1920, 1, 62, 127);
		off = new NoteOff(1920, 1, 54, 127);

	}

	@Test
	public void getChangedNoteOffTest() throws TransposeNoteException {
		assertEquals("F#3", NoteSign.fromMidiNumber(off.getNoteValue()).fullName());
		undTranspose.getChangedNoteOff(off, 4);
		assertEquals("A#3", NoteSign.fromMidiNumber(off.getNoteValue()).fullName());
	}

	@Test
	public void getChangedNoteOnTest() throws TransposeNoteException {
		assertEquals("D4", NoteSign.fromMidiNumber(on.getNoteValue()).fullName());
		undTranspose.getChangedNoteOn(on, 5);
		assertEquals("G4", NoteSign.fromMidiNumber(on.getNoteValue()).fullName());
	}

	@Test(expected = TransposeNoteException.class)
	public void getChangedNoteOnExceptionTest() throws TransposeNoteException {
		undTranspose.getChangedNoteOn(on, 100);
	}

	@Test(expected = TransposeNoteException.class)
	public void getChangedNoteOffExceptionTest() throws TransposeNoteException {
		undTranspose.getChangedNoteOff(off, 100);
	}

	@Test
	public void changeSpeedTest() {
		Tempo tmpUnd = (Tempo)midiFileUnd.getTracks().get(0).getEvents().last();
		assertEquals(83., tmpUnd.getBpm(), 1);
		undTranspose.changeSpeed(20);
		assertEquals(100., tmpUnd.getBpm(), 1);

		Tempo tmpBelle = (Tempo)midiFileBelle.getTracks().get(0).getEvents().last();
		assertEquals(83., tmpBelle.getBpm(), 1);
		belleTranspose.changeSpeed(20);
		assertEquals(100., tmpBelle.getBpm(), 1);

		Tempo tmpWreck = (Tempo)midiFileWreck.getTracks().get(0).getEvents().last();
		assertEquals(120., tmpWreck.getBpm(), 1);
		wreckTranspose.changeSpeed(20);
		assertEquals(144., tmpWreck.getBpm(), 1);
	}

}