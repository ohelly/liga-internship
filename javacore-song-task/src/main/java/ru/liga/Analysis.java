package ru.liga;

import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.meta.Tempo;
import com.leff.midi.event.meta.Text;
import com.leff.midi.event.meta.TrackName;
import ru.liga.songtask.domain.Note;
import ru.liga.songtask.domain.NoteSign;
import ru.liga.songtask.util.SongUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

public class Analysis {
	private MidiFile midiFile;
	private List<MidiTrack> allTracks;
	private MidiTrack trackWithMelody;
	private MidiTrack trackWithWords;

	public Analysis(MidiFile midiFile) {
		this.midiFile = midiFile;
		this.allTracks = midiFile.getTracks();
		this.trackWithWords = findTrackWithWords(allTracks);
		this.trackWithMelody = findMelody(allTracks, trackWithWords);
		analysisTrack(App.eventsToNotes(trackWithMelody.getEvents()), midiFile);
	}

	private static void countOfNote(List<Note> notes) {
		TreeMap<String, Integer> map = new TreeMap<>();
		String name;
		System.out.printf("\nСписок нот с количеством вхождений:\n");
		for (Note note : notes) {
			name = note.sign().getNoteName();
			if (map.containsKey(name))
				map.put(name, map.get(name) + 1);
			else
				map.put(name, 1);
		}
		for (String key : map.keySet()) {
			System.out.printf("%s: %d\n", key, map.get(key));
		}
	}

	private static void getDurationOfNote(List<Note> notes, MidiFile midiFile) {
		Tempo tmp = (Tempo) midiFile.getTracks().get(0).getEvents().last();
		TreeMap<Integer, Integer> map = new TreeMap<>();
		int ms;

		for (Note note : notes) {
			ms = SongUtils.tickToMs(tmp.getBpm(), midiFile.getResolution(), note.durationTicks());
			if (map.containsKey(ms))
				map.put(ms, map.get(ms) + 1);
			else
				map.put(ms, 1);
		}
		System.out.printf("Количество нот по длительностям:\n");
		for (Integer key : map.keySet()) {
			System.out.printf("%dмс: %d\n", key, map.get(key));
		}
		countOfNote(notes);
	}

	private void analysisTrack(List<Note> notes, MidiFile midiFile) {
		int max = Integer.MIN_VALUE;
		int min = Integer.MAX_VALUE;
		int temp;

		for (Note note : notes) {
			temp = note.sign().getMidi();
			max = Math.max(max, temp);
			min = Math.min(min, temp);
		}
		System.out.printf("Диапазон:\n\tВерхняя: %s\n\tНижняя: %s\n\tДиапазон: %d\n\n",
				NoteSign.fromMidiNumber(max), NoteSign.fromMidiNumber(min), max - min);
		getDurationOfNote(notes, midiFile);
	}

	private MidiTrack findTrackWithWords(List<MidiTrack> tracks) {
		TreeSet<MidiEvent> events;
		boolean flag;

		for (MidiTrack track : tracks) {
			events = track.getEvents();
			flag = true;
			for (MidiEvent ev : events) {
				if (!(ev instanceof Text || ev instanceof TrackName)) {
					flag = false;
					break;
				}
			}
			if (flag) {
				tracks.remove(track);
				track.getEvents().remove(track.getEvents().first());
				return track;
			}
		}
		return null;
	}

	private MidiTrack findMelody(List<MidiTrack> tracks, MidiTrack wordsTrack) {
		TreeSet<MidiEvent> wordsEvent = wordsTrack.getEvents();
		List<Note> list;

		for (MidiTrack track : tracks) {
			list = App.eventsToNotes(track.getEvents());
			if (twoTickEquals(list, wordsEvent))
				return track;
		}
		return null;
	}

	private boolean twoTickEquals(List<Note> notes, TreeSet<MidiEvent> wordsEvent) {
		ArrayList<Long> tickFromNote = new ArrayList<>();
		ArrayList<Long> tickFromWordsEvent = new ArrayList<>();

		for (Note note : notes) {
			tickFromNote.add(note.startTick());
		}
		for (MidiEvent ev : wordsEvent) {
			tickFromWordsEvent.add(ev.getTick());
		}

		return tickFromNote.equals(tickFromWordsEvent);
	}
}
