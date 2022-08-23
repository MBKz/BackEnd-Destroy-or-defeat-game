package Destroy_or_Defeat;

import GameEnv.TimerGame;
import Units.Attackable;
import Units.unit;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

public class LogFile {

    static String date = java.time.LocalDate.now().toString();

    public static Set<unit> UnitsInZoom = new HashSet<>();

    public static void WriteSize(String log) {
        try ( BufferedWriter Writer = new BufferedWriter(new FileWriter(date + ".txt", true))) {
            String Data = (LocalTime.parse("19:34:50.9")
                    + "\t" + log + "\t"
                    + String.valueOf(((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/1000000.0))
                    + "\t" + "MB");
            Writer.write((String) Data + "\n");
        } catch (IOException ignored) {
        }
    }

    public static void WriteAction(Attackable currunit, String u1, String log, String u2) {
        String Data = u1+log+u2 ;
        if(UnitsInZoom.contains(currunit)){
            System.out.println(Data);
        }
        try ( BufferedWriter Writer = new BufferedWriter(new FileWriter("Game_Log.txt", true))) {
            Writer.write((String) Data + "\n");
        } catch (IOException ignored) {
        }
    }

    public static void WriteFinalAction(String u1,String log,String u2) {
        String Data = u1+log+u2 ;
        System.out.println(Data);
        try ( BufferedWriter Writer = new BufferedWriter(new FileWriter("Game_Log.txt", true))) {
            Writer.write((String) Data + "\n");
        } catch (IOException ignored) {
        }
    }
}
