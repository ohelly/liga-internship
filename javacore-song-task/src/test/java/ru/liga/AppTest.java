package ru.liga;

import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;
import org.junit.Before;
import org.junit.Test;
import ru.liga.tests.TestTranspose;
import ru.liga.tests.TestsAnalyze;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class AppTest
{
   /* private static MidiFile midiFileBelle;
    private static MidiFile midiFileWreck;
    private static MidiFile midiFileUnd;
    private static TestsAnalyze testsAnalyze;
    private static TestTranspose testsTranspose;

    private List<MidiTrack> trackWithWordsUnd = new ArrayList<>();
    private List<MidiTrack> trackWithWordsBelle = new ArrayList<>();
    private List<MidiTrack> trackWithWordsWreck = new ArrayList<>();
    private MidiTrack melodyUnd;
    private MidiTrack melodyBelle;
    private MidiTrack melodyWreck;

    @Before
    public void setUp() {
        try {
            midiFileBelle = new MidiFile(new FileInputStream("src/main/resources/Belle.mid"));
            midiFileWreck = new MidiFile(new FileInputStream("src/main/resources/Wrecking Ball.mid"));
            midiFileUnd = new MidiFile(new FileInputStream("src/main/resources/Underneath Your Clothes.mid"));
            trackWithWordsUnd.add(midiFileUnd.getTracks().get(3));
            trackWithWordsBelle.add(midiFileBelle.getTracks().get(3));
            trackWithWordsWreck.add(midiFileWreck.getTracks().get(12));
            melodyUnd = midiFileUnd.getTracks().get(2);
            melodyWreck = midiFileWreck.getTracks().get(9);
            melodyBelle = midiFileBelle.getTracks().get(9);
            testsAnalyze = new TestsAnalyze();
            testsTranspose = new TestTranspose();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @Test
    public void testFindTrackWithWords(){
        assertEquals(trackWithWordsUnd, testsAnalyze.findTrackWithWords(midiFileUnd.getTracks()));
        assertEquals(trackWithWordsBelle, testsAnalyze.findTrackWithWords(midiFileBelle.getTracks()));
        assertEquals(trackWithWordsWreck, testsAnalyze.findTrackWithWords(midiFileWreck.getTracks()));
    }

    @Test
    public void testCorrectListOfTracks(){
        assertEquals(trackWithWordsBelle.get(0).getEventCount() - 1,
                testsAnalyze.correctListOfTracks(trackWithWordsBelle).get(0).getEventCount());
        assertEquals(trackWithWordsWreck.get(0).getEventCount() - 1,
                testsAnalyze.correctListOfTracks(trackWithWordsWreck).get(0).getEventCount());
        assertEquals(trackWithWordsUnd.get(0).getEventCount() - 1,
                testsAnalyze.correctListOfTracks(trackWithWordsUnd).get(0).getEventCount());
    }

    @Test
    public void testFindMelody() throws NoSuchMelodyException {
        assertEquals(melodyWreck,
                testsAnalyze.findMelody(testsAnalyze.correctListOfTracks(trackWithWordsWreck),
                        midiFileWreck.getTracks()));
        assertEquals(melodyBelle,
                testsAnalyze.findMelody(testsAnalyze.correctListOfTracks(trackWithWordsBelle),
                        midiFileBelle.getTracks()));
        assertEquals(melodyUnd,
                testsAnalyze.findMelody(testsAnalyze.correctListOfTracks(trackWithWordsUnd),
                        midiFileUnd.getTracks()));
    }

    @Test(expected = NoSuchMelodyException.class)
    public void testFindMelodyException() throws NoSuchMelodyException {
        testsAnalyze.findMelody(trackWithWordsWreck, midiFileWreck.getTracks());
        testsAnalyze.findMelody(trackWithWordsBelle, midiFileBelle.getTracks());
        testsAnalyze.findMelody(trackWithWordsUnd, midiFileUnd.getTracks());
    }

    @Test
    public void testTwoTickEquals(){
        assertTrue(testsAnalyze.twoTickEquals(App.eventsToNotes(melodyBelle.getEvents()),
                testsAnalyze.correctListOfTracks(trackWithWordsBelle).get(0).getEvents()));
        assertTrue(testsAnalyze.twoTickEquals(App.eventsToNotes(melodyWreck.getEvents()),
                testsAnalyze.correctListOfTracks(trackWithWordsWreck).get(0).getEvents()));
        assertTrue(testsAnalyze.twoTickEquals(App.eventsToNotes(melodyUnd.getEvents()),
                testsAnalyze.correctListOfTracks(trackWithWordsUnd).get(0).getEvents()));
    }
    /*
    На данном этапе понял,
    что сделал не тестабильный код,
    класс Transpose практиечки нельзя затестить
     */
}
