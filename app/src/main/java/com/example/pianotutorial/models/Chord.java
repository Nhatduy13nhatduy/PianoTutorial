package com.example.pianotutorial.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Chord {
    private int id;

    private float duration;

    private int measureId;

    private int position;

    private int clef;

    private List<ChordNote> chordNotes;

    public Chord() {
        id = 0;
        duration = 0;
        measureId = 0;
        position = 0;
        int clef = 0;
        chordNotes = new ArrayList<>();
    }

    public Chord(int id, float duration, int measureId, int position, int clef, List<ChordNote> chordNotes) {
        this.id = id;
        this.duration = duration;
        this.measureId = measureId;
        this.position = position;
        this.chordNotes = chordNotes;
        this.clef = clef;
        setChromaticPositions();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public int getMeasureId() {
        return measureId;
    }

    public void setMeasureId(int measureId) {
        this.measureId = measureId;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public List<ChordNote> getChordNotes() {
        return chordNotes;
    }

    public void setChordNotes(List<ChordNote> chordNotes) {
        this.chordNotes = chordNotes;
    }

    public int getClef() {
        return clef;
    }

    public void setClef(int clef) {
        this.clef = clef;
    }

    public void setChromaticPositions() {
        int chromaticCounter = 0;

        for (ChordNote note : chordNotes) {
            boolean matchFound = false;

            if (note.getNoteId() > 56) {
                chromaticCounter++;
                note.setChromaticPosition(chromaticCounter);
            } else {
                int adjustedNoteId = note.getNoteId();

                for (ChordNote otherNote : chordNotes) {
                    if (otherNote.getNoteId() > 56) {
                        int tempNoteId = otherNote.getNoteId();
                        while (tempNoteId > 56) {
                            tempNoteId -= 56;
                        }
                        if (adjustedNoteId == tempNoteId) {
                            chromaticCounter++;
                            matchFound = true;
                            break;
                        }
                    }
                }

                if (matchFound) {
                    note.setChromaticPosition(chromaticCounter);
                } else {
                    note.setChromaticPosition(0); // Reset to 0 if no match is found
                }
            }
        }
    }

    public boolean isStemUp() {
        int middleLinePitch = (clef == 0) ? 28 : 16; // 28 : B4 (G clef), 16 : B3 (F clef)
        int belowMiddleLine = 0;
        int aboveMiddleLine = 0;

        // Count belowMiddleLine, aboveMiddleLine
        if (chordNotes.isEmpty()) {
            return false;
        } else {
            for (ChordNote note : chordNotes) {
                int adjustedNoteId = adjustedNoteId(note.getNoteId());
                if (adjustedNoteId < middleLinePitch) {
                    belowMiddleLine++;
                } else if (adjustedNoteId > middleLinePitch) {
                    aboveMiddleLine++;
                }
            }

            if (belowMiddleLine > aboveMiddleLine) {
                return true; // Stem up
            } else if (aboveMiddleLine > belowMiddleLine) {
                return false; // Stem down
            } else {

                int farthestNotePitch = adjustedNoteId(chordNotes.get(0).getNoteId());
                for (ChordNote note : chordNotes) {
                    int adjustedNoteId = adjustedNoteId(note.getNoteId());
                    if (Math.abs(adjustedNoteId - middleLinePitch) > Math.abs(farthestNotePitch - middleLinePitch)) {
                        farthestNotePitch = adjustedNoteId;
                    }
                }
                return farthestNotePitch < middleLinePitch;
            }
        }
    }

    public int findLowestNote() {
        if (chordNotes.isEmpty()) {
            return -1; // or throw an exception if you prefer
        }

        int lowestNoteId = adjustedNoteId(chordNotes.get(0).getNoteId());
        for (ChordNote note : chordNotes) {
            int adjustedNoteId = adjustedNoteId(note.getNoteId());
            if (adjustedNoteId < lowestNoteId) {
                lowestNoteId = adjustedNoteId;
            }
        }
        return lowestNoteId;
    }

    // Find the note with the highest noteId
    public int findHighestNote() {
        if (chordNotes.isEmpty()) {
            return -1; // or throw an exception if you prefer
        }

        int highestNoteId = adjustedNoteId(chordNotes.get(0).getNoteId());
        for (ChordNote note : chordNotes) {
            int adjustedNoteId = adjustedNoteId(note.getNoteId());
            if (adjustedNoteId > highestNoteId) {
                highestNoteId = adjustedNoteId;
            }
        }
        return highestNoteId;
    }

    public boolean hasMultipleNotes() {
        return chordNotes.size() > 1;
    }

    public List<Integer> getFlipNotes(boolean isStemUp) {
        List<Integer> flipNotes = new ArrayList<>();
        if (chordNotes.isEmpty()) {
            return flipNotes; // Return empty list if no chord notes
        }

        List<Integer> noteIds = new ArrayList<>();
        for (ChordNote note : chordNotes) {
            noteIds.add(adjustedNoteId(note.getNoteId()));
        }

        if (isStemUp) {
            Collections.sort(noteIds);
        } else {
            noteIds.sort(Collections.reverseOrder());
        }

        int previousNoteId = noteIds.get(0);
        boolean previousFlipped = false;

        for (int i = 1; i < noteIds.size(); i++) {
            int currentNoteId = noteIds.get(i);
            if (Math.abs(currentNoteId - previousNoteId) == 1 && !previousFlipped) {
                flipNotes.add(currentNoteId);
                previousFlipped = true;
            } else {
                previousFlipped = false;
            }
            previousNoteId = currentNoteId;
        }

        return flipNotes;
    }


    public int findLowestNoteIdWithoutFlip(boolean isStemUp) {
        List<Integer> flipNotes = getFlipNotes(isStemUp);
        int lowestNoteId = findLowestNote();

        // If the lowestNoteId is in flipNotes and has a preceding note
        if (flipNotes.contains(lowestNoteId)) {
            return lowestNoteId + 1;
        }
        return lowestNoteId; // Otherwise, return the original lowestNoteId
    }

    public int findHighestNoteIdWithoutFlip(boolean isStemUp) {
        List<Integer> flipNotes = getFlipNotes(isStemUp);
        int highestNoteId = findHighestNote();

        // If the highestNoteId is in flipNotes and has a preceding note
        if (flipNotes.contains(highestNoteId)) {
            return highestNoteId - 1;
        }
        return highestNoteId; // Otherwise, return the original highestNoteId
    }

    public int adjustedNoteId(int noteId) {
        while (noteId > 56) {
            noteId -= 56;
        }
        return noteId;
    }

}
