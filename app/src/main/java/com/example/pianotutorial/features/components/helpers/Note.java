package com.example.pianotutorial.features.components.helpers;
import androidx.annotation.NonNull;

import java.util.Objects;

public class Note implements Comparable<Note> {
    private NotePitch notePitch;
    private int octave;
    private int noteId;

    public Note(int absolutePitch) {
        int relativePitch = absolutePitch % 12;

        this.notePitch = NotePitch.fromCode(relativePitch);
        this.octave = (absolutePitch / 12) - 1; //scientifically octaves start from -1
    }

    public Note(NotePitch relativePitch, int octave) {
        this.notePitch = relativePitch;
        this.octave = octave;
    }

    public int getAbsolutePitch() {
        // octaves start at -1 so octave + 1
        return ((octave + 1) * 12) + notePitch.getPitchCode();
    }

    public NotePitch getNotePitch() {
        return notePitch;
    }

    public void setNotePitch(NotePitch notePitch) {
        this.notePitch = notePitch;
    }

    public int getOctave() {
        return octave;
    }

    public void setOctave(int octave) {
        this.octave = octave;
    }

    @NonNull
    @Override
    public String toString() {
        if (Objects.equals(this.notePitch.getLabel(), "C#")){
            return "C"+this.getOctave()+"#";
        }
        else if (Objects.equals(this.notePitch.getLabel(), "D#")){
            return "D"+this.getOctave()+"#";
        }
        else if (Objects.equals(this.notePitch.getLabel(), "F#")){
            return "F"+this.getOctave()+"#";
        }else if (Objects.equals(this.notePitch.getLabel(), "G#")){
            return "G"+this.getOctave()+"#";
        }
        else if (Objects.equals(this.notePitch.getLabel(), "A#")){
            return "A"+this.getOctave()+"#";
        }
        else{
            return this.notePitch.getLabel() + this.getOctave();

        }
    }

    public int getNoteId() {
        return noteId;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    /**
     * Note position relative to the staff.
     * Ex: C and #C have the same position on the staff so they both have position 0
     */
    public int getPosition() {
        return notePitch.getPosition();
    }

    public Note nextWholeNote() {
        if (this.notePitch == NotePitch.E || this.notePitch == NotePitch.B || isSharp()) {
            return new Note(this.getAbsolutePitch() + 1);
        } else {
            return new Note(this.getAbsolutePitch() + 2);
        }
    }


    public Note previousWholeNote() {
        if(this.notePitch == NotePitch.F || this.notePitch == NotePitch.C || isSharp()) {
            return new Note(this.getAbsolutePitch() - 1);
        } else {
            return new Note(this.getAbsolutePitch() - 2);
        }
    }

    public boolean isSharp() {
        return notePitch.equals(NotePitch.C_SHARP) ||
                notePitch.equals(NotePitch.D_SHARP) ||
                notePitch.equals(NotePitch.F_SHARP) ||
                notePitch.equals(NotePitch.G_SHARP) ||
                notePitch.equals(NotePitch.A_SHARP);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return octave == note.octave &&
                notePitch == note.notePitch;
    }

    @Override
    public int hashCode() {
        return Objects.hash(notePitch, octave);
    }

    public boolean isGreaterThan(Note otherNote){
        return compareTo(otherNote) > 0;
    }

    @Override
    public int compareTo(@NonNull Note otherNote) {
        if(otherNote.getOctave() > this.getOctave()){
            return -1;
        }
        else if(otherNote.getOctave() < this.getOctave()){
            return 1;
        }
        else {
            return this.getNotePitch().compareTo(otherNote.getNotePitch());
        }
    }
}
