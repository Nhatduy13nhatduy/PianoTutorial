package com.example.pianotutorial.features.components.paints.ondraws;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.media.MediaPlayer;

import com.example.pianotutorial.constants.GlobalVariables;
import com.example.pianotutorial.features.components.helpers.MusicUtils;
import com.example.pianotutorial.features.components.helpers.Note;
import com.example.pianotutorial.features.components.paints.MusicView;
import com.example.pianotutorial.features.components.paints.notepaints.*;
import com.example.pianotutorial.models.BeamValue;
import com.example.pianotutorial.models.Chord;
import com.example.pianotutorial.models.ChordNote;
import com.example.pianotutorial.models.Measure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotesAndMeasuresDrawer {
    private final Paint measurePaint;
    private final Paint staffPaint;
    private final Paint changedColorPaintPass;
    private final Paint changedColorPaintMiss;
    private final List<Measure> measures;
    private final MusicView musicView;
    private final List<String> correctNotes;
    private final Map<ChordNote, NoteStatus> noteStatuses;
    private MediaPlayer player;
    private boolean soundPlayed;


    public NotesAndMeasuresDrawer(List<Measure> measures, Paint measurePaint, Paint staffPaint, Paint changedColorPaintPass, Paint changedColorPaintMiss, MusicView musicView, MediaPlayer player) {
        this.measurePaint = measurePaint;
        this.staffPaint = initializePaint(staffPaint);
        this.changedColorPaintPass = initializePaint(changedColorPaintPass);
        this.changedColorPaintMiss = initializePaint(changedColorPaintMiss);
        this.measures = measures;
        this.musicView = musicView;
        this.correctNotes = new ArrayList<>(); // Initialize the list here
        this.noteStatuses = new HashMap<>();
        this.player = player;
        this.soundPlayed = false;
    }

    private Paint initializePaint(Paint paint) {
        paint.setStrokeWidth((float) 4);
        return paint;
    }

    public void setMediaPlayer(MediaPlayer player) {
        this.player = player;
    }

    public void playSound() {
        if (player != null && !player.isPlaying()) {
            player.start();
        }
    }


    public void setCorrectNoteAction(Note note, boolean isPress) {
        if (note != null) {
            if (isPress) {
                this.correctNotes.add(note.toString()); // Add the single note to the list
            } else {
                this.correctNotes.remove(note.toString()); // Remove the single note from the list
            }
        }
    }

    private static class NoteStatus {
        boolean isPassed;
        boolean isCorrect;
    }

    public void draw(Canvas canvas, int width, long startTime) {
        long currentTime = System.currentTimeMillis() - startTime;
        float staffHeight = GlobalVariables.FIXED_HEIGHT;
        float noteHeadOriginalHeight = 22;

        float currentX = GlobalVariables.CHECK_LINE_X + width - (currentTime * 0.5f * GlobalVariables.SPEED);

        if(!measures.isEmpty()){
            musicView.updateRightClefDrawer(measures.get(0).getClef());
        }

        for (int i = 0; i < measures.size(); i++) {
            Measure previousMeasure = null;
            if (i > 0) {
                previousMeasure = measures.get(i - 1);
            }

            drawMeasure(canvas, measures, measures.get(i), previousMeasure, currentX, staffHeight, noteHeadOriginalHeight);
            currentX += GlobalVariables.MEASURE_WIDTH;
        }
    }

    private void drawMeasure(Canvas canvas, List<Measure> measures, Measure measure, Measure previousMeasure, float measureStartX, float staffHeight, float noteHeadOriginalHeight) {
        float measureEndX = measureStartX + GlobalVariables.MEASURE_WIDTH + 12;

        if (previousMeasure != null && previousMeasure.getClef() != measure.getClef()) {
            Path pendingClefPath = (measure.getClef() == 0) ? GClefPaint.createPath(measureStartX + 40) : FClefPaint.createPath(measureStartX + 40);
            Paint pendingClefPaint = (measure.getClef() == 0) ? GClefPaint.create("#4D000000") : FClefPaint.create("#4D000000");
            if (measureStartX < 130) {
                pendingClefPaint.setAlpha(0);
                if (musicView != null) {
                    musicView.updateRightClefDrawer(measure.getClef());
                }
            }
            canvas.drawPath(pendingClefPath, pendingClefPaint);
        }

        drawMeasureLine(canvas, measureEndX, staffHeight, measureStartX);
        float chordPositionWithinMeasure = 0;
        List<BeamValue> chordsToBeam = measure.groupChordsIntoBeams();
        if (measure.getChords() != null) {
            // Draw any remaining beamed notes
            if (!chordsToBeam.isEmpty()) {
                drawBeamedNotes(canvas, chordsToBeam, measure, measureStartX, staffHeight, noteHeadOriginalHeight);
            }
            for (Chord chord : measure.getChords()) {
                float noteDuration = chord.getDuration();
                float xPosition = measureStartX + chordPositionWithinMeasure;
                chordPositionWithinMeasure += (GlobalVariables.MEASURE_WIDTH / GlobalVariables.TOP_SIGNATURE) * noteDuration;

                drawChord(canvas, measures, chord, xPosition, measure, staffHeight, noteHeadOriginalHeight, noteDuration, chordsToBeam);
            }
        }
    }


    private void drawBeamedNotes(Canvas canvas, List<BeamValue> chordsToBeam, Measure measure, float measureStartX, float staffHeight, float noteHeadOriginalHeight) {
        if (chordsToBeam.isEmpty()) return;

        float yPosition;
        // Tạo đường beam
        Path beamPath = new Path();

        for (BeamValue beamValue : chordsToBeam) {
            Chord startValue = beamValue.getStartChord();
            float firstXPosition = measureStartX + (GlobalVariables.MEASURE_WIDTH / GlobalVariables.TOP_SIGNATURE) * measure.currentDuration(startValue);

            Chord endValue = beamValue.getEndChord();
            float lastXPosition = measureStartX + (GlobalVariables.MEASURE_WIDTH / GlobalVariables.TOP_SIGNATURE) * measure.currentDuration(endValue);

            if (startValue.getDuration() < 1 && startValue.getDuration() >= 0.5f) {
                if (beamValue.isBeamedNoteStemUp(measure.getClef())) {
                    int highestNoteId = beamValue.findHighestBeamNoteId(measure.getClef());
                    yPosition = (measure.getClef() == 0) ? MusicUtils.convertPitchToY(highestNoteId, 0) : MusicUtils.convertPitchToY(highestNoteId, 1);
                    if (startValue.findLowestNoteIdWithoutFlip(measure.getClef()) == endValue.findLowestNoteIdWithoutFlip(measure.getClef())) {
                        beamPath.moveTo(firstXPosition - 60, yPosition + 12);
                        beamPath.lineTo(lastXPosition - 55, yPosition + 12);
                        beamPath.lineTo(lastXPosition - 55, yPosition - 12);
                        beamPath.lineTo(firstXPosition - 60, yPosition - 12);
                        beamPath.close();
                    } else {
                        beamPath.moveTo(firstXPosition - 62, yPosition + 12); // Góc dưới bên trái
                        beamPath.lineTo(lastXPosition - 57, yPosition + 12); // Góc dưới bên phải
                        beamPath.lineTo(lastXPosition - 54, yPosition - 12); // Góc trên bên phải
                        beamPath.lineTo(firstXPosition - 59, yPosition - 12); // Góc trên bên trái
                        beamPath.close(); // Đóng path để hoàn thành hình bình hành
                        Matrix matrix = new Matrix();
                        matrix.setRotate(-8, (firstXPosition + lastXPosition) / 2, yPosition);
                        if (highestNoteId == startValue.findHighestNoteIdWithoutFlip(measure.getClef())) {
                            matrix.postScale(1, -1, (firstXPosition + lastXPosition) / 2, yPosition);            // Apply the rotation to the beamPath
                            matrix.postTranslate(0, 24);
                        }
                        beamPath.transform(matrix);
                    }

                } else {
                    int lowestNoteId = beamValue.findLowestBeamNoteId(measure.getClef());
                    yPosition = (measure.getClef() == 0) ? MusicUtils.convertPitchToY(lowestNoteId - 13, 0) : MusicUtils.convertPitchToY(lowestNoteId - 13, 1);
                    if (startValue.findLowestNoteIdWithoutFlip(measure.getClef()) == endValue.findLowestNoteIdWithoutFlip(measure.getClef())) {
                        beamPath.moveTo(firstXPosition - 109, yPosition - 12);
                        beamPath.lineTo(lastXPosition - 104, yPosition - 12);
                        beamPath.lineTo(lastXPosition - 104, yPosition + 12);
                        beamPath.lineTo(firstXPosition - 109, yPosition + 12);
                        beamPath.close();
                    } else {
                        beamPath.moveTo(firstXPosition - 111, yPosition - 12);
                        beamPath.lineTo(lastXPosition - 106, yPosition - 12);
                        beamPath.lineTo(lastXPosition - 103, yPosition + 12);
                        beamPath.lineTo(firstXPosition - 108, yPosition + 12);
                        beamPath.close();
                        Matrix matrix = new Matrix();
                        matrix.setRotate(8, (firstXPosition + lastXPosition) / 2, yPosition);
                        if (lowestNoteId == startValue.findLowestNoteIdWithoutFlip(measure.getClef())) {
                            matrix.postScale(1, -1, (firstXPosition + lastXPosition) / 2, yPosition);            // Apply the rotation to the beamPath
                            matrix.postTranslate(0, -24);
                        }
                        beamPath.transform(matrix);
                    }
                }
            } else {
                if (beamValue.isBeamedNoteStemUp(measure.getClef())) {
                    int highestNoteId = beamValue.findHighestBeamNoteId(measure.getClef());
                    yPosition = MusicUtils.convertPitchToY(highestNoteId, 0);
                    if (startValue.findLowestNoteIdWithoutFlip(measure.getClef()) == endValue.findLowestNoteIdWithoutFlip(measure.getClef())) {
                        beamPath.moveTo(firstXPosition + 16, yPosition + 28);
                        beamPath.lineTo(lastXPosition + 21, yPosition + 28);
                        beamPath.lineTo(lastXPosition + 21, yPosition + 4);
                        beamPath.lineTo(firstXPosition + 16, yPosition + 4);
                        beamPath.close();

                        beamPath.moveTo(firstXPosition + 16, yPosition + 64);
                        beamPath.lineTo(lastXPosition + 21, yPosition + 64);
                        beamPath.lineTo(lastXPosition + 21, yPosition + 40);
                        beamPath.lineTo(firstXPosition + 16, yPosition + 40);
                        beamPath.close();
                    } else {
                        beamPath.moveTo(firstXPosition + 8, yPosition + 28);
                        beamPath.lineTo(lastXPosition + 15, yPosition + 28);
                        beamPath.lineTo(lastXPosition + 22, yPosition + 4);
                        beamPath.lineTo(firstXPosition + 15, yPosition + 4);
                        beamPath.close();

                        beamPath.moveTo(firstXPosition - 2, yPosition + 64);
                        beamPath.lineTo(lastXPosition + 5, yPosition + 64);
                        beamPath.lineTo(lastXPosition + 12, yPosition + 40);
                        beamPath.lineTo(firstXPosition + 5, yPosition + 40);
                        beamPath.close();

                        Matrix matrix = new Matrix();
                        matrix.setRotate(-16, (firstXPosition + lastXPosition) / 2, yPosition);
                        if (highestNoteId == startValue.findHighestNoteIdWithoutFlip(measure.getClef())) {
                            matrix.postScale(1, -1, (firstXPosition + lastXPosition) / 2, yPosition);            // Apply the rotation to the beamPath
                            matrix.postTranslate(0, 60);
                        }
                        beamPath.transform(matrix);
                    }

                } else {
                    int lowestNoteId = beamValue.findLowestBeamNoteId(measure.getClef());
                    yPosition = MusicUtils.convertPitchToY(lowestNoteId - 13, 0);
                    if (startValue.findLowestNoteIdWithoutFlip(measure.getClef()) == endValue.findLowestNoteIdWithoutFlip(measure.getClef())) {
                        beamPath.moveTo(firstXPosition - 33, yPosition - 16);
                        beamPath.lineTo(lastXPosition - 28, yPosition - 16);
                        beamPath.lineTo(lastXPosition - 28, yPosition + 8);
                        beamPath.lineTo(firstXPosition - 33, yPosition + 8);
                        beamPath.close();
                        beamPath.moveTo(firstXPosition - 33, yPosition - 52);
                        beamPath.lineTo(lastXPosition - 28, yPosition - 52);
                        beamPath.lineTo(lastXPosition - 28, yPosition - 28);
                        beamPath.lineTo(firstXPosition - 33, yPosition - 28);
                        beamPath.close();
                    } else {
                        beamPath.moveTo(firstXPosition - 39, yPosition - 16);
                        beamPath.lineTo(lastXPosition - 34, yPosition - 16);
                        beamPath.lineTo(lastXPosition - 27, yPosition + 8);
                        beamPath.lineTo(firstXPosition - 32, yPosition + 8);
                        beamPath.close();
                        beamPath.moveTo(firstXPosition - 49, yPosition - 52);
                        beamPath.lineTo(lastXPosition - 44, yPosition - 52);
                        beamPath.lineTo(lastXPosition - 37, yPosition - 28);
                        beamPath.lineTo(firstXPosition - 42, yPosition - 28);
                        beamPath.close();
                        Matrix matrix = new Matrix();
                        matrix.setRotate(16, (firstXPosition + lastXPosition) / 2, yPosition);
                        if (lowestNoteId == startValue.findLowestNoteIdWithoutFlip(measure.getClef())) {
                            matrix.postScale(1, -1, (firstXPosition + lastXPosition) / 2, yPosition);            // Apply the rotation to the beamPath
                            matrix.postTranslate(0, -60);
                        }
                        beamPath.transform(matrix);
                    }
                }
            }


            // Vẽ hình bình hành (beam)
            Paint beamPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            beamPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            beamPaint.setColor(Color.BLACK); // Chọn màu cho hình bình hành
            beamPaint.setStrokeWidth(1); // Độ dày của đường viền hình bình hành

            ChordNote chordNote = endValue.getChordNotes().get(0);
            NoteStatus noteStatus = noteStatuses.computeIfAbsent(chordNote, k -> new NoteStatus());

            if (noteStatus.isPassed) {
                beamPaint.setColor(noteStatus.isCorrect ? changedColorPaintPass.getColor() : changedColorPaintMiss.getColor());
                // Update the color of all notes in the chord if the chord is correct
                if (noteStatus.isCorrect) {
                    for (ChordNote cn : endValue.getChordNotes()) {
                        NoteStatus ns = noteStatuses.get(cn);
                        if (ns != null) {
                            ns.isCorrect = true;
                        }
                    }
                }
            }
            if (firstXPosition < GlobalVariables.CHECK_LINE_X - 160) {
                beamPaint.setAlpha(0);
            }

            canvas.drawPath(beamPath, beamPaint);
            beamPath.reset();
        }
    }

    private void drawMeasureLine(Canvas canvas, float measureEndX, float staffHeight, float measureStartX) {
        float topY = staffHeight / 2;
        float bottomY = staffHeight / 2 + 4 * (staffHeight / 8);
        Paint measureLinePaint = new Paint(measurePaint);

        if (!soundPlayed && measureStartX < GlobalVariables.CHECK_LINE_X + 40) {
            playSound();
            soundPlayed = true; // Update the flag to true to prevent future plays
        }

        // Check if the measure line has crossed the measureEndX
        if (measureEndX < GlobalVariables.CHECK_LINE_X - 160) {
            measureLinePaint.setAlpha(0);
        }

        canvas.drawLine(measureEndX, topY, measureEndX, bottomY, measureLinePaint);
    }

    private void drawChord(Canvas canvas, List<Measure> measures, Chord chord, float xPosition, Measure measure, float staffHeight, float noteHeadOriginalHeight, float noteDuration, List<BeamValue> beamValues) {
        if (!chord.getChordNotes().isEmpty()) {
            chord.setChromaticPositions();
            for (ChordNote chordNote : chord.getChordNotes()) {
                drawNote(canvas, measures, chord, chordNote, xPosition, measure, staffHeight, noteHeadOriginalHeight, noteDuration, beamValues);
            }
        } else {
            MusicUtils.drawRest(canvas, xPosition, noteDuration, System.currentTimeMillis(), musicView);
        }
    }

    private void drawNote(Canvas canvas, List<Measure> measures, Chord chord, ChordNote chordNote, float xPosition, Measure measure, float staffHeight, float noteHeadOriginalHeight, float noteDuration, List<BeamValue> beamValues) {
        int currentNote = chordNote.getNoteId();
        if (currentNote > 112) currentNote -= 112;
        else if (currentNote > 56) currentNote -= 56;

        canvas.save();
        float noteY = MusicUtils.convertPitchToY(currentNote, measure.getClef());
        canvas.translate(xPosition, noteY);

        float scaleFactor = (staffHeight / 10) / noteHeadOriginalHeight;
        canvas.scale(scaleFactor, scaleFactor);

        Paint notePaint = MusicUtils.getNotePaint(measure, chord, currentNote, chordNote);
        Path notePath = MusicUtils.getNotePath(measure, chord, currentNote, chordNote, beamValues);

        NoteStatus noteStatus = noteStatuses.computeIfAbsent(chordNote, k -> new NoteStatus());
        Paint currentNotePaint = new Paint(notePaint);

        updateNoteStatus(chord, chordNote, xPosition, measure, currentNote, noteStatus, currentNotePaint);

        if (xPosition < GlobalVariables.CHECK_LINE_X - 160) {
            currentNotePaint.setAlpha(0);
        }

        canvas.drawPath(notePath, currentNotePaint);

        drawChromaticSign(canvas, chordNote, xPosition, measure, noteStatus, noteHeadOriginalHeight);
        drawDottedNotes(canvas, currentNotePaint, noteDuration, noteHeadOriginalHeight, chordNote.getNoteId());

        canvas.restore();

        MusicUtils.drawLedgerLines(canvas, xPosition, chordNote, measure.getClef(), staffPaint, changedColorPaintPass, changedColorPaintMiss, GlobalVariables.CHECK_LINE_X - 40, noteStatus.isPassed, noteStatus.isCorrect);

        float slurDuration = chordNote.calculateTotalSlurredDuration(measures, chordNote);
        if (slurDuration != 0) {
            ChordNote targetChordNote = chordNote.findChordNoteWithTargetSlurPosition(measures, chordNote);

            float startX = xPosition + 80;
            float startY = MusicUtils.convertPitchToY(chordNote.getNoteId(), measure.getClef());
            float endX = xPosition + (GlobalVariables.MEASURE_WIDTH / GlobalVariables.TOP_SIGNATURE) * slurDuration + 40;
            float endY = MusicUtils.convertPitchToY(targetChordNote.getNoteId(), measure.getClef());
            if (MusicUtils.isUpSlur(chordNote.getNoteId(), targetChordNote.getNoteId(), measure.getClef())) {
                drawSlur(canvas, targetChordNote, startX, startY + 110, endX, endY + 110, true);
            } else {
                drawSlur(canvas, targetChordNote, startX, startY + 180, endX, endY + 180, false);
            }
        }
    }

    private void updateNoteStatus(Chord chord, ChordNote chordNote, float xPosition, Measure measure, int currentNote, NoteStatus noteStatus, Paint currentNotePaint) {
        float checkLineX = getCheckLineX(measure, currentNote, chordNote);

        if (xPosition <= checkLineX && !noteStatus.isPassed) {
            noteStatus.isPassed = true;
            noteStatus.isCorrect = correctNotes != null && correctNotes.contains(chordNote.getRealityNoteName());
        }

        if (noteStatus.isPassed) {
            currentNotePaint.setColor(noteStatus.isCorrect ? changedColorPaintPass.getColor() : changedColorPaintMiss.getColor());
            // Update the color of all notes in the chord if the chord is correct
            if (noteStatus.isCorrect) {
                for (ChordNote cn : chord.getChordNotes()) {
                    NoteStatus ns = noteStatuses.get(cn);
                    if (ns != null) {
                        ns.isCorrect = true;
                    }
                }
            }
        }
    }

    private void drawSlur(Canvas canvas, ChordNote chordNote, float startX, float startY, float endX, float endY, boolean isUpwards) {
        Paint slurPaint = new Paint();
        slurPaint.setColor(0xFF000000); // Màu đen
        slurPaint.setStyle(Paint.Style.FILL);
        slurPaint.setAntiAlias(true);

        Path slurPath = new Path();
        float length = endX - startX;
        float curvature = length / 8; // Adjust this factor to control curvature
        float controlX1 = startX + length / 4;
        float controlX2 = startX + 3 * length / 4;
        float controlY1, controlY2, lowerControlY1, lowerControlY2;

        if (isUpwards) {
            controlY1 = startY - curvature;
            controlY2 = startY - curvature;
            lowerControlY1 = startY - (curvature - 15);
            lowerControlY2 = startY - (curvature - 15);
        } else {
            controlY1 = startY + curvature;
            controlY2 = startY + curvature;
            lowerControlY1 = startY + (curvature - 15);
            lowerControlY2 = startY + (curvature - 15);
        }

        slurPath.moveTo(startX, startY);
        slurPath.cubicTo(controlX1, controlY1, controlX2, controlY2, endX, endY);
        slurPath.cubicTo(controlX2, lowerControlY2, controlX1, lowerControlY1, startX, startY);
        slurPath.close();

        NoteStatus noteStatus = noteStatuses.computeIfAbsent(chordNote, k -> new NoteStatus());
        if (noteStatus.isPassed) {
            slurPaint.setColor(noteStatus.isCorrect ? changedColorPaintPass.getColor() : changedColorPaintMiss.getColor());
        }
        if (endX < GlobalVariables.CHECK_LINE_X - 160) {
            slurPaint.setAlpha(0);
        }
        canvas.drawPath(slurPath, slurPaint);
    }

    private float getCheckLineX(Measure measure, int currentNote, ChordNote chordNote) {
        boolean isChromatic = chordNote.getNoteId() > 65 && chordNote.getNoteId() < 169 || chordNote.getNoteId() > 112;
        float checkLineX = GlobalVariables.CHECK_LINE_X - (isChromatic ? 64 : 40);

        if (!isChromatic) {
            if (measure.getClef() == 0 && MusicUtils.calculateLedgerLinesGClef(currentNote) > 0) {
                checkLineX = GlobalVariables.CHECK_LINE_X - 64;
            } else if (measure.getClef() != 0 && MusicUtils.calculateLedgerLinesFClef(currentNote) > 0) {
                checkLineX = GlobalVariables.CHECK_LINE_X - 64;
            }
        }

        return checkLineX;
    }

    private void drawChromaticSign(Canvas canvas, ChordNote chordNote, float xPosition, Measure measure, NoteStatus noteStatus, float noteHeadOriginalHeight) {
        Paint chromaticSignPaint;
        Path chromaticSignPath;
        if (chordNote.getNoteId() > 56 && chordNote.getNoteId() < 113) { // Flat sign
            chromaticSignPaint = new Paint(FlatSignPaint.create());
            chromaticSignPath = FlatSignPaint.createPath();
        } else if (chordNote.getNoteId() > 112) { // Sharp sign
            chromaticSignPaint = new Paint(SharpSignPaint.create());
            chromaticSignPath = SharpSignPaint.createPath();
        } else if (chordNote.getNoteId() <= 56 && chordNote.getChromaticPosition() > 0) {
            chromaticSignPaint = new Paint(NaturalSignPaint.create());
            chromaticSignPath = NaturalSignPaint.createPath();
        } else {
            return; // No chromatic sign to draw
        }

        if (noteStatus.isPassed) {
            chromaticSignPaint.setColor(noteStatus.isCorrect ? changedColorPaintPass.getColor() : changedColorPaintMiss.getColor());
        }
        if (xPosition < GlobalVariables.CHECK_LINE_X - 160) {
            chromaticSignPaint.setAlpha(0);
        }

        MusicUtils.drawChromaticSign(canvas, chromaticSignPaint, chromaticSignPath, noteHeadOriginalHeight, chordNote.getChromaticPosition());
    }

    private void drawDottedNotes(Canvas canvas, Paint notePaint, float noteDuration, float noteHeadOriginalHeight, int noteId) {
        int dottedNoteCount = MusicUtils.countDottedNotes(noteDuration);
        if (dottedNoteCount > 0) {
            MusicUtils.drawDottedNotes(canvas, notePaint, dottedNoteCount, noteHeadOriginalHeight, noteId);
        }
    }
}
