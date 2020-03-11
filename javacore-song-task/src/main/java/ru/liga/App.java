package ru.liga;

import com.leff.midi.MidiFile;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.NoteOff;
import com.leff.midi.event.NoteOn;
import ru.liga.songtask.domain.Note;
import ru.liga.songtask.domain.NoteSign;
import java.io.*;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class App {
    public static void main(String[] args){
    	try {
			if (args[1].equals("analyze")) {
				new Analysis(new MidiFile(new FileInputStream(args[0])));
			} else if ((args[1].equals("change")) && (args.length == 6)) {
				new Transpose(new MidiFile(new FileInputStream(args[0])), args);
			} else {
				System.out.println("Bad Arguments");
			}
		} catch (Exception e) {
			System.out.println("Bad Arguments");
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
