package ru.liga.tests;

import com.leff.midi.MidiTrack;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.meta.Text;
import com.leff.midi.event.meta.TrackName;
import ru.liga.App;
import ru.liga.NoSuchMelodyException;
import ru.liga.songtask.domain.Note;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class TestsAnalyze {
	/*public List<MidiTrack> findTrackWithWords(List<MidiTrack> allTracks) {
		return correctListOfTracks(allTracks.stream()
				.filter(track -> track.getEvents()
						.stream()
						.allMatch(midiEvent
								-> midiEvent instanceof Text || midiEvent instanceof TrackName))
				.collect(Collectors.toList()));
	}

	public List<MidiTrack> correctListOfTracks(List<MidiTrack> tracks) {
		return tracks.stream()
				.filter(track -> track.getEvents()
						.removeIf(midiEvent -> midiEvent instanceof TrackName))
				.collect(Collectors.toList());
	}

	public MidiTrack findMelody(List<MidiTrack> trackWithWords, List<MidiTrack> allTracks) throws NoSuchMelodyException {
		List<Note> list;

		for (MidiTrack wordsTrack : trackWithWords) {
			for (MidiTrack track : allTracks) {
				list = App.eventsToNotes(track.getEvents());
				if (twoTickEquals(list, wordsTrack.getEvents())) {
					return track;
				}
			}
		}
		throw new NoSuchMelodyException();
	}

	public boolean twoTickEquals(List<Note> notes, TreeSet<MidiEvent> wordsEvent) {
		ArrayList<Long> tickFromNote = new ArrayList<>();
		ArrayList<Long> tickFromWordsEvent = new ArrayList<>();
		notes.forEach(note -> tickFromNote.add(note.startTick()));
		wordsEvent.forEach(event -> tickFromWordsEvent.add(event.getTick()));

		return tickFromNote.equals(tickFromWordsEvent);
	}*/
}
