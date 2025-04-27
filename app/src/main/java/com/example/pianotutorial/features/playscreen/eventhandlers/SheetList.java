package com.example.pianotutorial.features.playscreen.eventhandlers;

import com.example.pianotutorial.models.Sheet;

import java.util.ArrayList;
import java.util.List;

public class SheetList {
    public List<Sheet> sheetList() {
        List<Sheet> sheetList = new ArrayList<>();
        int songId1 = 1;
        int topSignature1 = 4;
        int bottomSignature1 = 4;
        int keySignature1 = 2;
        String rightSymbol1 = "F:F3#G3A3B3-9_0.5 F:D3#C3_0.5 F4#D4-2_0.5 E4#D4_0.5 F:A3_2/D5_2 C5#_2/B4_2 A4_2/B4_2 C5#_2/D5F5#_2 C5#E5_2/B4D5_2 A4C5#_2/G4B4_2 F4A4_2/G4B4_2 A4C5#_2/D4_1 F4_1 A4_1 G4_1/F4_1 D4_1 F4_1 E4_1/D4_1 B3_1 D4_1 A4_1/G4_1 B4_1 A4_1 G4_1/F4_1 D4_1 E4_1 C5#_1/D5_1 F5#_1 A5_1 A4_1/B4_1 G4_1 A4_1 F4_1/D4_1 D5_1 C5#_1 P_1/D5_0.5 C5#_0.5 D5_0.5 D4_0.5 C4_0.5 A4_0.5 E4_0.5 F4_0.5/D4_0.5 D5_0.5 C5#_0.5 B4_0.5 C5#_0.5 F5#_0.5 A5_0.5 B5_0.5/G5_0.5 F5#_0.5 E5_0.5 G5_0.5 F5#_0.5 E5_0.5 D5_0.5 C5#_0.5/B4_0.5 A4_0.5 G4_0.5 F4_0.5 E4_0.5 G4_0.5 F4_0.5 E4_0.5/D4_0.5 E4_0.5 F4_0.5 G4_0.5 A4_0.5 E4_0.5 A4_0.5 G4_0.5/F4_0.5 B4_0.5 A4_0.5 G4_0.5 A4_0.5 G4_0.5 F4_0.5 D4_0.5/D4_0.5 B3_0.5 B4_0.5 C5#_0.5 D5_0.5 C5#_0.5 B4_0.5 C4_0.5/G4_0.5 F4_0.5 E4_0.5 B4_0.5 A4_0.5 B4_0.5 A4_0.5 G4_0.5/F4_1 F5#_1 E5_2/P_1 D5_1 F5#_2/B5_2 A5_2/B5_2 C6_2/D6_1 D5_1 C5#_2/P_1 B4_1 D5_2/D5_2 P_1 D5_1/D5_1 F5#_1 E5_1 A5_1";
        String leftSymbol1 = "F5_0.25 E5_0.25 P_0.25 P_0.5 F:F3#F3bF3_0.75 P_1 F4_0.25 E4C4D4F4_0.25 P_0.5/P_4/P_4/P_4/P_4/P_4/P_4/P_4/P_4/P_4/P_4/P_4/P_4/P_4/P_4/P_4/P_4/P_4/P_4/P_4/P_4/P_4/P_4/P_4/P_4/P_4/P_4/P_4/P_4/P_4/P_4/P_4/P_4";
        String sheetFile1 = "https://firebasestorage.googleapis.com/v0/b/pianoaiapi.appspot.com/o/Midi%2Ff1d4cb7b-9e3b-445e-a3e7-f97fc78e5434_Sao_Sang.mid?alt=media&token=fb758635-1027-43cc-bbff-1a0db24177bb";
        Sheet sheet1 = new Sheet(1, songId1, "Song 1", topSignature1, bottomSignature1, 1, "Piano", sheetFile1, keySignature1, rightSymbol1, leftSymbol1);
        sheetList.add(sheet1);

        // Data for second Sheet
        int songId2 = 2;
        int topSignature2 = 3;
        int bottomSignature2 = 4;
        int keySignature2 = 1;
        String sheetFile2 = "";
        String rightSymbol2 = "E:F3#D3C3#E3_1.0 E:F3B3A3_1.0 D:G3B3A3_0.75 D:D3#F3#_0.5";
        String leftSymbol2 = "F4_0.5 P_0.5 F:G4_0.5 C4_0.5 D5_1.0";

        Sheet sheet2 = new Sheet(2, songId2, "Song 2", topSignature2, bottomSignature2, 1, "Piano", sheetFile2, keySignature2, rightSymbol2, leftSymbol2);
        sheetList.add(sheet2);

        // Data for third Sheet
        int songId3 = 3;
        int topSignature3 = 4;
        int bottomSignature3 = 4;
        int keySignature3 = 0;
        String sheetFile3 = "";
        String rightSymbol3 = "F:G3A3_1.0 F:D3B3A3_1.5 E:C3D3_0.5";
        String leftSymbol3 = "C4_0.25 F4_0.75 G4_0.5 P_0.25 F4_1.0";

        Sheet sheet3 = new Sheet(3, songId3, "Song 3", topSignature3, bottomSignature3, 1, "Piano", sheetFile3, keySignature3, rightSymbol3, leftSymbol3);
        sheetList.add(sheet3);

        return sheetList;
    }
}
