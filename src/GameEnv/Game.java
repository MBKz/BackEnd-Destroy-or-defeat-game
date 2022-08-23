package GameEnv;

import Destroy_or_Defeat.*;
import Forces.*;
import Units.*;
import DataType.*;
import org.json.simple.parser.ParseException;
import java.io.*;
import java.util.*;

public class Game extends Thread {

    static public Map2D<Integer, Integer, pair<pair<unit, unit>, unit>> arena= null ;
    private static Game game = null;
    final int points = 30000;
    final int timerGame = 100;
    protected ReadFile structure;
    protected ReadFile terrain;
    protected Player attack;
    protected Player defender;
    protected ArrayList<unit> AllUnits;
    protected FillArena UnitsInArena = new FillArena();
    protected  ConsoleKey consoleKey = new ConsoleKey();
    protected TimerGame timer = new TimerGame();
    protected ConsoleKey inputState = new ConsoleKey();

    static public Map2D<Integer, Integer, pair<pair<unit, unit>, unit>> getArena() {
        if (arena == null ){
            arena = new Map2D<>();
        }
        return arena;
    }

    public Game()  {
        arena = getArena();
      this.AllUnits = new ArrayList<>();
        FileReader structureReader = null;                                                             // structure file
        try {
            structureReader = new FileReader("./structure.json");
        } catch (FileNotFoundException e) {
            System.out.println("catch exception in Game/opening structure file");
        }
        try {
            if(structureReader.read()>0)  {
                    this.structure = new ReadFile("./structure.json",arena);
                    try {
                        AllUnits.addAll(this.structure.addBuilding());
                    } catch (IOException | ParseException e) {
                        e.printStackTrace();
                        System.out.println("catch exception in game constructor/ read structure file");
                    }
                }
                else {
                    System.out.println("you can not play without Base !");
                    System.exit(0);
                }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("catch exception in game constructor/ size structure file");
        }
        FileReader terrainReader = null;                                                                 // terrain file
        try {
            terrainReader = new FileReader("./terrain.json");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("catch exception in Game/opening terrain file");
        }
        try {
            if(terrainReader.read()>0)  {
                this.terrain = new ReadFile("./terrain.json",arena);
                try {
                    AllUnits.addAll(this.terrain.addTerrain());
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                    System.out.println("catch exception in game constructor/read terrain file ");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("catch exception in game / size terrain file");
        }
        FileReader attackerReader = null;                                                                  //attack file
        try {
            attackerReader = new FileReader("./attacker.json");
        } catch (FileNotFoundException e) {
            System.out.println("catch exception in Game/opening attack file");
            e.printStackTrace();
        }
        try {
            if(attackerReader.read()>0)  {
                this.attack = new Player("./attacker.json", points,arena);
                try {
                    AllUnits.addAll(this.attack.buy());
                    Fighters.setAttackerNum(this.attack.getUnitsNumber());
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                    System.out.println("catch exception in Game / read attack file ");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("catch exception in game / size attack file");
        }
        FileReader defenderReader = null;                                                               // defender file
        try {
             defenderReader = new FileReader("./defender.json");
        } catch (FileNotFoundException e) {
            System.out.println("catch exception in Game/opening defend file");
            e.printStackTrace();
        }
        try {
                if(defenderReader.read()>0)  {
                    this.defender = new Player("./defender.json", points,arena);
                    try {
                        AllUnits.addAll(this.defender.buy());
                    } catch (IOException | ParseException e) {
                        e.printStackTrace();
                        System.out.println("catch exception in game /read defend file");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            System.out.println("catch exception in game / size attack file");
            }
       LogFile.WriteSize(this.toString());
    }

    public static Game getGame()  {
        if (Game.game == null)   Game.game = new Game();
        return Game.game;
    }

    public void Placing() {
        for (int i = 0; i< AllUnits.size(); i++) {
            if(UnitsInArena.checkPlacement(AllUnits.get(i))) {
                UnitsInArena.fillUnitInPlace(AllUnits.get(i));
                LogFile.WriteFinalAction(AllUnits.get(i).toString()," Placesd","");
            }
            else {
                LogFile.WriteFinalAction(AllUnits.get(i).toString() , " miss placed so removed ","");
                AllUnits.remove((int)i);
            }
        }
        System.out.println(
                "_________________________________________________________________________________________________\n" +
                "\t\t\t\t\t the allocated cells in arena = " + arena.size() +  " for " + AllUnits.size() + " unit " +
                "\n_________________________________________________________________________________________________"
        );
    }

    @Override
    public void run() {
        Placing();
        startGame();
        goingGame();
    }

    public void startGame() {
        LogFile.UnitsInZoom.clear();
        inputState.start();
        timer.start();
        for (unit CurrUnit : AllUnits) {
            LogFile.UnitsInZoom.add(CurrUnit);
            if (CurrUnit instanceof Fighters)   CurrUnit.start();
        }
    }

    public void endGame() {
        timer.stop();
        for (unit allUnit : AllUnits) {
            if (allUnit instanceof UnAttackable || allUnit instanceof Building) continue;
            try {
                allUnit.stop();
            }catch (Exception ignored){}
        }
        LogFile.WriteFinalAction(" _______________________________\t\tThe End\t\t_______________________________ ","","");
        LogFile.WriteSize("The End ( total size allocated ):  ");
        System.exit(0);
    }

    boolean CheckZoom = false;
    Set<unit> MyUnits = new HashSet<>();
    pair<Integer, Integer> c1 = new pair<>() , c2 = new pair<>();


    public void goingGame(){

        while (true) {
            if (Fighters.AttackerNum == 0) {
                LogFile.WriteFinalAction("All attackers are destroyed", " Attackers Lost", "");
                endGame();
            }
            System.out.print("");
            if (Fighters.BaseIsDestroyed) {
                LogFile.WriteFinalAction("The Base Destroyed ", " Attackers Wins", " ");
                endGame();
            }
            if (timer.getTimer() == timerGame) {
                LogFile.WriteFinalAction("The Time is up", " Attackers Lost", "");
                endGame();
            }
        }
    }



    /**   #######################     inner     ###############################   **/
     class ConsoleKey extends Thread {
        char state;
        Scanner scanner = new Scanner(System.in);

        @Override
        public void run() {
            while ( ! (Fighters.BaseIsDestroyed || (Fighters.getAttackerNum() == 0) || timer.getTimer() == timerGame) ){                                         /**************     zoom option   *********************/
                state = scanner.next().charAt(0);
                if(state == 'p') {
                    System.out.println("pause");
                    Pause();
                }
                if(state == 'r'){
                    System.out.println("resume");
                    Resume();
                }
                if(state == 'z') {
                    Pause();
                    Zoom();
                    Resume();
                }
            }
            this.stop();
        }

        void Pause(){
            timer.suspend();
            for (unit allUnit : AllUnits) {
                allUnit.suspend();
            }
        }

        void Resume(){
            timer.resume();
            for (unit allUnit : AllUnits) {
                allUnit.resume();
            }
        }

        public void getUnitsInZoom(pair<Integer, Integer> c1 , pair<Integer, Integer> c2){

            for(int i = c1.First; i <= c2.First; i++) {
                for (int j = c1.Second; j <= c2.Second; j++) {
                    pair<pair<unit, unit>, unit> obj = arena.get(i, j);
                    if (obj == null){
                        continue;
                    }
                    unit GroundForce = obj.First.First, AirForce = obj.First.Second, River = obj.Second;
                    if( GroundForce instanceof Attackable) {
                        MyUnits.add(GroundForce);
                    }
                    if(AirForce != null) {
                        MyUnits.add(AirForce);
                    }
                }
            }
        }

        public void Zoom() {

            System.out.println("Enter The Targeted Area (  UpLeft Corner coordinate and  DownRight Corner coordinate  )");
            c1.First = consoleKey.scanner.nextInt();
            c1.Second = consoleKey.scanner.nextInt();  // UpLeft Corner
            c2.First = consoleKey.scanner.nextInt();
            c2.Second = consoleKey.scanner.nextInt();  // DownRight Corner
            getUnitsInZoom(c1,c2);
            System.out.println("There Is\t[ " + MyUnits.size() + " ]\tUnit(s) In The Zoomed Area .");
            if( MyUnits.size() > 0) {
                int i = 0;
                for (unit Unit : MyUnits) {
                    if(((Attackable) Unit).getIsAlive()) System.out.println( ++i + " - " + Unit.toString() + " [ Alive ]" );
                    else System.out.println( ++i + " - " + Unit.toString()+ " [ Dead ]");
                }
                try {
                    sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    System.out.println("catch exception in Game/arena/Zoom arena(!0)");
                }
            }
            else {
                System.out.println("Zooming On This Part Of The Arena Is Useless ...");
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    System.out.println("catch exception in Game/arena/Zoom arena(0)");
                }
            }
            System.out.println("Now Back To Game ...");
            consoleKey.Resume();
        }
    }

    /**   #######################     inner     ###############################   **/
    static class FillArena {

        protected final static long ArenaSize = new Player().maxBorder;

        public boolean checkPlacement(unit obj) {
            if ((obj.getX() >= ArenaSize) || (obj.getX() < 0)
                    || (obj.getY() >= ArenaSize) || (obj.getY() < 0)
                    || ((obj.getX() + obj.getHeight() -1) >= ArenaSize) || (obj.getY() + obj.getWidth() -1 >= ArenaSize)) {
                return false;
            }
            pair<pair<unit,unit>,unit> p;
            for (int i = obj.getX(); i < obj.getX() + obj.getHeight(); i++) {
                for (int j = obj.getY(); j < obj.getY() + obj.getWidth(); j++) {
                    p = arena.get(i,j);
                    if (p != null) {
                        if(obj instanceof River){
                            if(p.Second != null) {
                                System.out.print("river : [ "+p.Second+" ] here so ");
                                return false;
                            }
                            continue;
                        }
                        else if(p.First == null) {
                            continue;
                        }
                         if(obj instanceof Fighters && ((Fighters) obj).typeForce == 2){
                            if(p.First.Second != null) {
                                System.out.print("air : [ "+p.First.Second+" ] here so ");
                                return false;
                            }
                        }
                        else if(p.First.First != null ) {
                            System.out.print("ground : [ "+p.First.First+"] here so ");
                            return false;
                        }
                    }
                }
            }
            return true;
        }

        public void fillUnitInPlace(unit obj) {
            for (int i = obj.getX(); i < obj.getX() + obj.getHeight() ; i++) {
                for (int j = obj.getY() ; j < obj.getY() + obj.getWidth(); j++) {
                    pair<pair<unit, unit>, unit> p = arena.remove(i, j);
                    if(p == null){
                        p = new pair<>();
                        if(obj instanceof River) {
                            p.Second = obj;
                            arena.put(i, j, p);
                            continue;
                        }
                        p.First = new pair<>();
                        if(obj instanceof Fighters && ((Fighters) obj).typeForce == 2){
                            p.First.Second = obj;
                            arena.put(i, j, p);
                            continue;
                        }
                            p.First.First = obj;
                            arena.put(i, j, p);
                    }
                    else {
                        if(obj instanceof River){
                            p.Second = obj;
                            arena.put(i, j, p);
                            continue;
                        }
                        if(p.First == null) {
                            p.First = new pair<>();
                        }
                        if(obj instanceof Fighters && ((Fighters) obj).typeForce == 2){
                            p.First.Second = obj;
                            arena.put(i, j, p);
                            continue;
                        }
                        p.First.First = obj;
                        arena.put(i, j, p);
                    }
                }
            }
        }
    }

}