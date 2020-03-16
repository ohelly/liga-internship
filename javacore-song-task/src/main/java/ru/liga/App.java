package ru.liga;

import com.leff.midi.MidiFile;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.NoteOff;
import com.leff.midi.event.NoteOn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.liga.songtask.domain.Note;
import ru.liga.songtask.domain.NoteSign;

import java.io.*;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class App {
	private static Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args){
    	if (args != null) {
			logger.debug("Программа запущена.\n Входные параметры: {}", Arrays.toString(args));
			readParameters(args);
		}
    	else {
			logger.debug("Недостаточно вводимых параметров!");
			logger.info("Пример ввода:");
			logger.info("\tДля Анализа: <path to file> analyze");
			logger.info("\tДля изменения: <path to file> change -trans <value> -tempo <value>");
		}
    }

    public static void readParameters(String[] args) {
    	try {
			if (args[1].equals("analyze")) {
				new Analysis(new MidiFile(new FileInputStream(args[0])));
				logger.debug("Анализ трека закончен");
			}
			else if (args[1].equals("change")
					&& args[2].equals("-trans")
					&& args[4].equals("-tempo")) {
				new Transpose(new MidiFile(new FileInputStream(args[0])), args[0],
						Integer.parseInt(args[3]),
						Integer.parseInt(args[5]));
				logger.debug("Изменение трека закончено");
			}
			else {
				logger.info("Неверный формат ввода!");
				logger.info("Пример ввода:");
				logger.info("\tДля Анализа: <path to file> analyze");
				logger.info("\tДля изменения: <path to file> change -trans <value> -tempo <value>");
			}
		} catch (IOException e) {
    		logger.info("Указан неверный путь к файлу!");
		} catch (IndexOutOfBoundsException | NumberFormatException e) {
			logger.info("Неверный формат ввода!");
			logger.info("Пример ввода:");
			logger.info("\tДля Анализа: <path to file> analyze");
			logger.info("\tДля изменения: <path to file> change -trans <value> -tempo <value>");
		}
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
