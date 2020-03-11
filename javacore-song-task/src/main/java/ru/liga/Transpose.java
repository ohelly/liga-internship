package ru.liga;

import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.NoteOff;
import com.leff.midi.event.NoteOn;
import com.leff.midi.event.meta.Tempo;
import ru.liga.songtask.domain.NoteSign;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.TreeSet;


public class Transpose {
	private MidiFile midiFile;
	private MidiFile newMidiFile;
	private String name;
	private int trans;
	private float tempo;

	public Transpose(MidiFile midiFile, String[] args) throws Exception {
		try {
			this.midiFile = midiFile;
			this.name = args[0];
			if (args[2].equals("-trans"))
				this.trans = Integer.parseInt(args[3]);
			else if (args[4].equals("-trans"))
				this.trans = Integer.parseInt(args[5]);
			else
				throw new Exception("Enter value for -trans");
			if (args[2].equals("-tempo"))
				this.tempo = Integer.parseInt(args[3]);
			else if (args[4].equals("-tempo"))
				this.tempo = Integer.parseInt(args[5]);
			else
				throw new Exception("Enter value for -tempo");
			this.newMidiFile = transposeTracks();
			changeSpeed();
			saveNewFile();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	private void saveNewFile() {
		try {
			String nameFile = name.substring(name.lastIndexOf(File.separator) + 1, name.lastIndexOf("."));
			File file = new File(nameFile + "-trans" + trans + "-tempo" + (int)tempo + ".mid");
			System.out.println(file.getAbsolutePath());
			newMidiFile.writeToFile(file);
		}catch (IOException e) {
			e.printStackTrace();
		}
	}

	private MidiFile transposeTracks() throws Exception {
		MidiFile newMidiFile = new MidiFile();
		List<MidiTrack> midiTracks = midiFile.getTracks();
		for (MidiTrack track : midiTracks) {
			newMidiFile.addTrack(transpose(track));
		}
		return newMidiFile;
	}

	private MidiTrack transpose(MidiTrack track) throws Exception {
		MidiTrack newMidiTrack = new MidiTrack();
		TreeSet<MidiEvent> events = track.getEvents();

		for (MidiEvent event : events) {
			if (event instanceof NoteOn) {
				newMidiTrack.getEvents()
						.add(getChangedNoteOn((NoteOn)event));
			}
			else if (event instanceof NoteOff) {
				newMidiTrack.getEvents()
						.add(getChangedNoteOff((NoteOff)event));
			}
			else {
				newMidiTrack.getEvents().add(event);
			}
		}
		return newMidiTrack;
	}

	private NoteOff getChangedNoteOff(NoteOff event) throws Exception {
		NoteOff off = new NoteOff(event.getTick(), event.getDelta(),
				event.getChannel(), event.getNoteValue(),
				event.getVelocity());
		off.setNoteValue(off.getNoteValue() + trans);
		if (NoteSign.fromMidiNumber(off.getNoteValue()) == NoteSign.NULL_VALUE)
			throw new Exception("Невозможно транспонировать ноту");
		return off;
	}

	private NoteOn getChangedNoteOn(NoteOn event) throws Exception {
		NoteOn on = new NoteOn(event.getTick(), event.getDelta(),
				event.getChannel(), event.getNoteValue(),
				event.getVelocity());
		on.setNoteValue(on.getNoteValue() + trans);
		if (NoteSign.fromMidiNumber(on.getNoteValue()) == NoteSign.NULL_VALUE)
			throw new Exception("Невозможно транспонировать ноту");
		return on;
	}

	private void changeSpeed() {
		Tempo tempo = (Tempo) newMidiFile.getTracks().get(0).getEvents().last();
		tempo.setBpm(tempo.getBpm() * (this.tempo / 100f) + tempo.getBpm());
	}
}
