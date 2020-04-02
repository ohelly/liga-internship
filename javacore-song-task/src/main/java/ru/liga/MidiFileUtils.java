package ru.liga;

import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.NoteOff;
import com.leff.midi.event.NoteOn;
import com.leff.midi.event.meta.Text;
import com.leff.midi.event.meta.TrackName;
import ru.liga.songtask.domain.Note;
import ru.liga.songtask.domain.NoteSign;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.TreeSet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

public class MidiFileUtils {
	public static List<MidiTrack> getTracksWithWords(List<MidiTrack> tracks) {
		return (tracks.stream()
				.filter(track -> track.getEvents()
						.stream()
						.allMatch(midiEvent
								-> midiEvent instanceof Text || midiEvent instanceof TrackName))
				.collect(Collectors.toList()));
	}

	public static List<MidiTrack> deleteTrackNameFromTracks(List<MidiTrack> tracks) {
		return tracks.stream()
				.filter(track -> track.getEvents()
						.removeIf(midiEvent -> midiEvent instanceof TrackName))
				.collect(Collectors.toList());
	}

	public static boolean comparisonWordsBetweenNotes(TreeSet<MidiEvent> wordsEvent, List<Note> notes) {
		ArrayList<Long> tickFromNote = new ArrayList<>();
		ArrayList<Long> tickFromWordsEvent = new ArrayList<>();
		notes.forEach(note -> tickFromNote.add(note.startTick()));
		wordsEvent.forEach(event -> tickFromWordsEvent.add(event.getTick()));
		return tickFromNote.equals(tickFromWordsEvent);
	}

	public static MidiFile getCopyOfMidiFile(MidiFile file) {
		MidiFile newMidiFile = new MidiFile();
		file.getTracks().forEach(newMidiFile::addTrack);
		return newMidiFile;
	}

	/**
	 * Этот метод, чтобы вы не афигели переводить эвенты в ноты
	 *
	 * @param events эвенты одного трека
	 * @return список нот
	 */
	public static List<Note> eventsToNotes(TreeSet<MidiEvent> events) {
		List<Note> vbNotes = new ArrayList<>();

		Queue<NoteOn> noteOnQueue = new LinkedBlockingQueue<>();
		for (MidiEvent event : events) {
			if (event instanceof NoteOn || event instanceof NoteOff) {
				if (isEndMarkerNote(event)) {
					NoteSign noteSign = NoteSign.fromMidiNumber(extractNoteValue(event));
					if (noteSign != NoteSign.NULL_VALUE) {
						NoteOn noteOn = noteOnQueue.poll();
						if (noteOn != null) {
							long start = noteOn.getTick();
							long end = event.getTick();
							vbNotes.add(
									new Note(noteSign, start, end - start));
						}
					}
				} else {
					noteOnQueue.offer((NoteOn) event);
				}
			}
		}
		return vbNotes;
	}

	private static Integer extractNoteValue(MidiEvent event) {
		if (event instanceof NoteOff) {
			return ((NoteOff) event).getNoteValue();
		} else if (event instanceof NoteOn) {
			return ((NoteOn) event).getNoteValue();
		} else {
			return null;
		}
	}

	private static boolean isEndMarkerNote(MidiEvent event) {
		if (event instanceof NoteOff) {
			return true;
		} else if (event instanceof NoteOn) {
			return ((NoteOn) event).getVelocity() == 0;
		} else {
			return false;
		}

	}
}
