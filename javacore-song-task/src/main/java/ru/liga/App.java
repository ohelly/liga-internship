package ru.liga;

import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.NoteOff;
import com.leff.midi.event.NoteOn;
import com.leff.midi.event.meta.Tempo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.liga.songtask.domain.Note;
import ru.liga.songtask.domain.NoteSign;

import java.io.*;
import java.util.*;

public class App {
	private static Logger logger = LoggerFactory.getLogger(App.class);

	public static void main(String[] args) {
		if (args != null) {
			logger.debug("Программа запущена.\n Входные параметры: {}", Arrays.toString(args));
			readParameters(args);
		} else {
			outInvalidInputFormat();
		}
	}

	private static void readParameters(String[] args) {
		try {
			if (args[1].equals("analyze")) {
				analysisTrack(new MidiFile(new FileInputStream(args[0])));
			} else if (args[1].equals("change")
					&& args[2].equals("-trans")
					&& args[4].equals("-tempo")) {
				transposeTrack(new MidiFile(new FileInputStream(args[0])), args[0],
						Integer.parseInt(args[3]),
						Integer.parseInt(args[5]));
			} else {
				outInvalidInputFormat();
			}
		} catch (IOException e) {
			logger.info("Указан неверный путь к файлу!");
		} catch (IndexOutOfBoundsException | NumberFormatException e) {
			outInvalidInputFormat();
		}
	}

	private static void analysisTrack(MidiFile inputMidiFile) {
		List<MidiTrack> allTracks = inputMidiFile.getTracks();
		logger.trace("Получены все треки из файла");
		List<MidiTrack> trackWithWords = MidiFileUtils.getTracksWithWords(allTracks);
		logger.trace("Получены треки со словами");
		logger.trace("Анализ треков");
		for (MidiTrack trackWords : trackWithWords) {
			for (MidiTrack track : allTracks) {
				List<Note> list = MidiFileUtils.eventsToNotes(track.getEvents());
				debugLogAnalyze(allTracks, trackWithWords, trackWords, track);
				if (MidiFileUtils.comparisonWordsBetweenNotes(trackWords.getEvents(), list)) {
					logger.trace("Найден трек с мелодией");
					Analysis analysis = new Analysis(track, (Tempo) inputMidiFile.getTracks().get(0).getEvents().last(),
							inputMidiFile);
					Map<Integer, Integer> mapDuration = analysis.getMapOfDuration();
					Map<String, Integer> mapCount = analysis.getMapOfCount();
					infoLogAnalyze(analysis, mapDuration, mapCount);
				}
			}
		}
		logger.trace("Анализ трека закончен");
	}

	private static void debugLogAnalyze(List<MidiTrack> allTracks, List<MidiTrack> tracksWithWords,
										MidiTrack currTrackWords, MidiTrack currTrack) {
		logger.debug("Кол-во треков в файле: {}", allTracks.size());
		logger.debug("Кол-во треков со словами: {}", tracksWithWords.size());
		logger.debug("trackWords: {}", currTrackWords.toString());
		logger.debug("track: {}", currTrack.toString());
	}

	private static void infoLogAnalyze(Analysis analysis, Map<Integer, Integer> mapDuration, Map<String, Integer> mapCount) {
		logger.trace("Анализ трека");
		logger.info("Диапазон:");
		logger.info("\tВерхняя: {}", NoteSign.fromMidiNumber(analysis.getMaxNoteValue()));
		logger.info("\tНижняя: {}", NoteSign.fromMidiNumber(analysis.getMinNoteValue()));
		logger.info("\tДиапазон: {}", analysis.getRange());
		logger.trace("Вывод списка нот по длительностям");
		logger.info("Количество нот по длительностям:");
		mapDuration.keySet()
				.forEach(integer -> logger.info("{}мс: {}", integer, mapDuration.get(integer)));
		logger.trace("Вывод списка нот с количеством вхождений");
		logger.info("Список нот с количеством вхождений:");
		mapCount.keySet()
				.forEach(s -> logger.info("{}: {}", s, mapCount.get(s)));
	}

	private static void transposeTrack(MidiFile inputMidiFile, String name, int trans, int tempo) {
		debugLogTranspose(name, trans, tempo);
		MidiFile newMidi = MidiFileUtils.getCopyOfMidiFile(inputMidiFile);
		Transpose transpose = new Transpose(newMidi);
		for (MidiTrack track : newMidi.getTracks()) {
			logger.debug("Изменение трека: {}", track.toString());
			for (MidiEvent event : track.getEvents()) {
				tryChangeEvent(transpose, event, trans);
			}
		}
		logger.trace("Транспонирование треков закончено");
		logger.debug("Изменение скорости трека, значение: {}", tempo);
		transpose.changeSpeed(tempo);
		logger.trace("Изменение скорости трека закончено");
		try {
			String nameFile = name.substring(name.lastIndexOf(File.separator) + 1, name.lastIndexOf("."));
			File file = new File(nameFile + "-trans" + trans + "-tempo" + tempo + ".mid");
			newMidi.writeToFile(file);
			logger.trace("Файл успешно сохранен");
			infoLogTranspose(file);
		} catch (IOException e) {
			logger.debug("IO Exception при сохранении измененного файла");
			e.printStackTrace();
		}
		logger.trace("Изменение файла закончено");
	}

	private static void tryChangeEvent(Transpose transpose, MidiEvent event, int trans) {
		try {
			if (event instanceof NoteOn)
				event = transpose.getChangedNoteOn((NoteOn) event, trans);
			else if (event instanceof NoteOff)
				event = transpose.getChangedNoteOff((NoteOff) event, trans);
		} catch (TransposeNoteException e) {
			logger.debug("Невозможно транспонировать ноту");
			logger.info("Невозможно транспонировать ноту\n\tЗначение слишком большое/маленькое");
		}
	}

	private static void debugLogTranspose(String name, int trans, int tempo) {
		logger.debug("Полученные данные:");
		logger.debug("Путь к файлу: {}", name);
		logger.debug("Значение trans: {}", trans);
		logger.debug("Значение tempo: {}", tempo);
	}

	private static void infoLogTranspose(File file) {
		logger.info("Файл успешно изменен и сохранен");
		logger.info("Путь: {}", file.getName());
	}

	private static void outInvalidInputFormat() {
		logger.info("Неверный формат ввода!");
		logger.info("Пример ввода:");
		logger.info("\tДля Анализа: <path to file> analyze");
		logger.info("\tДля изменения: <path to file> change -trans <value> -tempo <value>");
	}
}
