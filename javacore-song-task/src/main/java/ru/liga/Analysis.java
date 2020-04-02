package ru.liga;

import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.meta.Tempo;
import ru.liga.songtask.domain.Note;
import ru.liga.songtask.domain.NoteSign;
import ru.liga.songtask.util.SongUtils;

import java.util.*;


public class Analysis {
	private MidiFile file;
	private MidiTrack track;
	private Tempo tmp;
	private Integer[] notesValue;
	private List<Note> notes;
	private TreeMap<Integer, Integer> mapOfDuration = new TreeMap<>();
	private TreeMap<String, Integer> mapOfCount = new TreeMap<>();

	public Analysis(MidiTrack track, Tempo tmp, MidiFile file) {
		this.track = track;
		this.tmp = tmp;
		this.notes = MidiFileUtils.eventsToNotes(track.getEvents());
		this.notesValue = notes
				.stream().map(Note::sign).map(NoteSign::getMidi).toArray(Integer[]::new);
		this.file = file;
		cacheOfDurationNotes();
		cacheOfCountNotes();
	}

	public Integer getMaxNoteValue() {
		return Arrays.stream(notesValue).max(Integer::compare).get();
	}

	public Integer getMinNoteValue() {
		return Arrays.stream(notesValue).min(Integer::compare).get();
	}

	public Integer getRange() {
		return getMaxNoteValue() - getMinNoteValue();
	}

	public Map<String, Integer> getMapOfCount() {
		return mapOfCount;
	}

	public Map<Integer, Integer> getMapOfDuration() {
		return mapOfDuration;
	}

	private void cacheOfCountNotes() {
		for (Note note : notes) {
			putToMap(mapOfCount, note.sign().getNoteName());
		}
	}

	private void cacheOfDurationNotes() {
		for (Note note : notes) {
			putToMap(mapOfDuration, SongUtils.tickToMs(tmp.getBpm(), file.getResolution(), note.durationTicks()));
		}
	}

	private <T> void putToMap(TreeMap<T, Integer> map, T ms) {
		if (map.containsKey(ms))
			map.put(ms, map.get(ms) + 1);
		else
			map.put(ms, 1);
	}
}
