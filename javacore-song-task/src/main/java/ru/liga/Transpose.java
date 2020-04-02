package ru.liga;

import com.leff.midi.MidiFile;
import com.leff.midi.event.NoteOff;
import com.leff.midi.event.NoteOn;
import com.leff.midi.event.meta.Tempo;
import ru.liga.songtask.domain.NoteSign;


public class Transpose {
	private MidiFile midiFile;

	public Transpose(MidiFile midiFile) {
		this.midiFile = midiFile;
	}

	public NoteOff getChangedNoteOff(NoteOff event, int trans) throws TransposeNoteException {
		event.setNoteValue(event.getNoteValue() + trans);
		if (NoteSign.fromMidiNumber(event.getNoteValue()) == NoteSign.NULL_VALUE)
			throw new TransposeNoteException();
		return event;
	}

	public NoteOn getChangedNoteOn(NoteOn event, int trans) throws TransposeNoteException {
		event.setNoteValue(event.getNoteValue() + trans);
		if (NoteSign.fromMidiNumber(event.getNoteValue()) == NoteSign.NULL_VALUE)
			throw new TransposeNoteException();
		return event;
	}

	public void changeSpeed(int tmp) {
		Tempo tempo = (Tempo) midiFile.getTracks().get(0).getEvents().last();
		tempo.setBpm(tempo.getBpm() * (tmp / 100f) + tempo.getBpm());
	}
}
