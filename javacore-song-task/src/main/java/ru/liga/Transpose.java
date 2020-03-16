package ru.liga;

import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.NoteOff;
import com.leff.midi.event.NoteOn;
import com.leff.midi.event.meta.Tempo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.liga.songtask.domain.NoteSign;

import java.io.File;
import java.io.IOException;


public class Transpose {
	private static Logger logger = LoggerFactory.getLogger(Analysis.class);
	private MidiFile midiFile;
	private MidiFile newMidiFile;
	private String name;
	private int trans;
	private float tempo;

	public Transpose(MidiFile midiFile, String name, int trans, int tempo) {
		logger.debug("Запущено изменение трека");
		this.midiFile = midiFile;
		this.name = name;
		this.trans = trans;
		this.tempo = tempo;
		logger.debug("Полученные данные:");
		logger.debug("Путь к файлу: {}", name);
		logger.debug("Значение trans: {}", trans);
		logger.debug("Значение tempo: {}", tempo);
		this.newMidiFile = transposeTracks();
		changeSpeed();
		saveNewFile();
	}

	private void saveNewFile() {
		logger.trace("Сохранение нового измененного файла");
		try {
			String nameFile = name.substring(name.lastIndexOf(File.separator) + 1, name.lastIndexOf("."));
			File file = new File(nameFile + "-trans" + trans + "-tempo" + (int) tempo + ".mid");
			newMidiFile.writeToFile(file);
			logger.trace("Файл успешно сохранен");
			logger.info("Файл успешно изменен и сохранен");
			logger.trace("Путь: {}", file.getName());
			logger.info("Путь: {}", file.getName());
		} catch (IOException e) {
			logger.debug("IO Exception, метод - saveNewFile");
			e.printStackTrace();
		}
	}

	private MidiFile transposeTracks() {
		logger.trace("Транспонирование всех треков");
		MidiFile newMidiFile = new MidiFile();
		midiFile.getTracks()
				.forEach(track -> newMidiFile.addTrack(transpose(track)));
		logger.trace("Транспонирование закончено");
		return newMidiFile;
	}

	private MidiTrack transpose(MidiTrack track) {
		MidiTrack newMidiTrack = new MidiTrack();
		track.getEvents().forEach(event -> addToNewTrack(event, newMidiTrack));
		return newMidiTrack;
	}

	private void addToNewTrack(MidiEvent event, MidiTrack track) {
		try {
			if (event instanceof NoteOn) {
				track.getEvents().add(getChangedNoteOn((NoteOn) event));
			} else if (event instanceof NoteOff)
				track.getEvents().add(getChangedNoteOff((NoteOff) event));
			else {
				track.getEvents().add(event);
			}
		} catch (TransposeNoteException e) {
			logger.debug("Невозможно транспонировать ноту");
			logger.info("Невозможно транспонировать ноту\n\tЗначение слишком большое/маленькое");
		}
	}

	private NoteOff getChangedNoteOff(NoteOff event) throws TransposeNoteException {
		NoteOff off = new NoteOff(event.getTick(), event.getDelta(),
				event.getChannel(), event.getNoteValue(),
				event.getVelocity());
		off.setNoteValue(off.getNoteValue() + trans);
		if (NoteSign.fromMidiNumber(off.getNoteValue()) == NoteSign.NULL_VALUE)
			throw new TransposeNoteException();
		return off;
	}

	private NoteOn getChangedNoteOn(NoteOn event) throws TransposeNoteException {
		NoteOn on = new NoteOn(event.getTick(), event.getDelta(),
				event.getChannel(), event.getNoteValue(),
				event.getVelocity());
		on.setNoteValue(on.getNoteValue() + trans);
		if (NoteSign.fromMidiNumber(on.getNoteValue()) == NoteSign.NULL_VALUE)
			throw new TransposeNoteException();
		return on;
	}

	private void changeSpeed() {
		logger.trace("Изменение скорости трека");
		Tempo tempo = (Tempo) newMidiFile.getTracks().get(0).getEvents().last();
		tempo.setBpm(tempo.getBpm() * (this.tempo / 100f) + tempo.getBpm());
		logger.trace("Изменение скорости трека закончено");
	}
}
