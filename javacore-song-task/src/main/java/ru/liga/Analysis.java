package ru.liga;

import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.meta.Tempo;
import com.leff.midi.event.meta.Text;
import com.leff.midi.event.meta.TrackName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.liga.songtask.domain.Note;
import ru.liga.songtask.domain.NoteSign;
import ru.liga.songtask.util.SongUtils;

import java.util.*;
import java.util.stream.Collectors;


public class Analysis {
	private static Logger logger = LoggerFactory.getLogger(Analysis.class);
	private MidiFile midiFile;
	private List<MidiTrack> allTracks;
	private MidiTrack trackWithMelody;
	private List<MidiTrack> trackWithWords;
	private Tempo tmp;

	public Analysis(MidiFile midiFile) {
		logger.debug("Запущен анализ файла");
		try {
			this.midiFile = midiFile;
			this.allTracks = midiFile.getTracks();
			this.trackWithWords = findTrackWithWords();
			logger.trace("Найден трек со словами");
			this.trackWithMelody = findMelody();
			logger.trace("Найден нужный трек с мелодией");
			this.tmp = (Tempo) midiFile.getTracks().get(0).getEvents().last();
			analysisTrack(App.eventsToNotes(trackWithMelody.getEvents())
					.stream().map(Note::sign).map(NoteSign::getMidi).toArray(Integer[]::new));
		} catch (NoSuchMelodyException e) {
			logger.debug("Не найдено ни одной подходящей мелодии");
		}
	}

	private void countOfNote(List<Note> notes) {
		logger.trace("Вывод списка нот с количеством вхождений");
		TreeMap<String, Integer> map = new TreeMap<>();
		notes.forEach(note -> putToMap(note, map, note.sign().getNoteName()));
		logger.info("\nСписок нот с количеством вхождений:");
		map.keySet().forEach(s -> logger.info("{}: {}\n", s, map.get(s)));
	}

	private void getDurationOfNote(List<Note> notes) {
		logger.trace("Вывод списка нот по длительностям");
		TreeMap<Integer, Integer> map = new TreeMap<>();
		notes.forEach(note -> putToMap(note, map,
				SongUtils.tickToMs(tmp.getBpm(), midiFile.getResolution(), note.durationTicks())));
		logger.info("Количество нот по длительностям:");
		map.keySet()
				.forEach(integer -> logger.info("{}мс: {}", integer, map.get(integer)));
		countOfNote(notes);
	}

	private <T> void putToMap(Note note, TreeMap<T, Integer> map, T ms) {
		if (map.containsKey(ms))
			map.put(ms, map.get(ms) + 1);
		else
			map.put(ms, 1);
	}

	private void analysisTrack(Integer[] notes) {
		logger.trace("Анализ трека");
		Integer max = Arrays.stream(notes).max(Integer::compare).get();
		Integer min = Arrays.stream(notes).min(Integer::compare).get();

		logger.info("Диапазон:");
		logger.info("\tВерхняя: {}", NoteSign.fromMidiNumber(max));
		logger.info("\tНижняя: {}", NoteSign.fromMidiNumber(min));
		logger.info("\tДиапазон: {}", max - min);
		getDurationOfNote(App.eventsToNotes(trackWithMelody.getEvents()));
	}

	private List<MidiTrack> findTrackWithWords() {
		logger.trace("Поиск трека со словами");
		return correctListOfTracks(allTracks.stream()
				.filter(track -> track.getEvents()
						.stream()
						.allMatch(midiEvent
								-> midiEvent instanceof Text || midiEvent instanceof TrackName))
				.collect(Collectors.toList()));
	}

	private List<MidiTrack> correctListOfTracks(List<MidiTrack> tracks) {
		logger.trace("Корректирова трека со словами");
		return tracks.stream()
				.filter(track -> track.getEvents()
						.removeIf(midiEvent -> midiEvent instanceof TrackName))
				.collect(Collectors.toList());
	}

	private MidiTrack findMelody() throws NoSuchMelodyException {
		logger.trace("Поиск пригодной мелодии");
		List<Note> list;

		for (MidiTrack wordsTrack : trackWithWords) {
			for (MidiTrack track : allTracks) {
				list = App.eventsToNotes(track.getEvents());
				if (twoTickEquals(list, wordsTrack.getEvents())) {
					logger.trace("Мелодия найдена");
					return track;
				}
			}
		}
		throw new NoSuchMelodyException();
	}

	private boolean twoTickEquals(List<Note> notes, TreeSet<MidiEvent> wordsEvent) {
		logger.trace("Сравнение двух коллекций по тикам");
		ArrayList<Long> tickFromNote = new ArrayList<>();
		ArrayList<Long> tickFromWordsEvent = new ArrayList<>();
		notes.forEach(note -> tickFromNote.add(note.startTick()));
		wordsEvent.forEach(event -> tickFromWordsEvent.add(event.getTick()));

		return tickFromNote.equals(tickFromWordsEvent);
	}
}
