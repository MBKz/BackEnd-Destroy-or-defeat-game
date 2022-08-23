package GameEnv;

import DataType.*;
import Forces.Fighters;
import Forces.attacker;
import Forces.defender;
import Units.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import Destroy_or_Defeat.LogFile;
import java.io.*;
import java.util.*;

public class Player {

    protected double points;

    protected final int maxBorder = 1000;

    protected Map2D<Integer, Integer, pair<pair<unit, unit>, unit>> arena;

    protected String fileName;

    protected ArrayList<Fighters> unitsArray;

    protected Set<String> CanTarget = new HashSet<>();

    Player(){}

    Player(String fileName, int points,Map2D<Integer, Integer, pair<pair<unit, unit>, unit>> arena) {
        this.fileName = fileName;
        this.points = points;
        this.arena = arena;
        this.unitsArray = new ArrayList<>();
        LogFile.WriteSize(this.toString());
    }

    public ArrayList<Fighters> buy() throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        Object obj;
        try (Reader reader = new FileReader(fileName)) {
            obj = parser.parse(reader);
        }
        JSONArray  jsonArray = (JSONArray) obj;
        for (Object o : jsonArray) {
            CanTarget.clear();
            JSONObject json = (JSONObject) o;
            String name = (String) json.get("Name");
            String type = (String) json.get("Type");
            String priority = (String) json.get("Priority");
            long price = (Long) json.get("Price");
            long typeForce = (Long) json.get("TypeForce");
            JSONObject coordinates = (JSONObject) json.get("Coordinates");
            long xUnit = (Long) coordinates.get("x");
            long yUnit = (Long) coordinates.get("y");
            long radios = (Long) coordinates.get("radios");
            long height = 2 * radios + 1;
            long width = 2 * radios + 1;
            JSONObject coordinatesBase = (JSONObject) json.get("CoordinatesBase");
            long xBase = (Long) coordinatesBase.get("x");
            long yBase = (Long) coordinatesBase.get("y");
            long hradios = (Long) coordinatesBase.get("radios");
            long hBase = 2 * hradios + 1;
            long wBase = 2 * hradios + 1;
            JSONArray canTarget = (JSONArray) json.get("CanTarget");
            for (Object value : canTarget) {
                CanTarget.add((String) value);
            }
            JSONObject attackInformation = (JSONObject) json.get("AttackInformation");
            long health = (Long) attackInformation.get("Health");
            double armor = (Double) attackInformation.get("Armor");
            long speed = (Long) attackInformation.get("Speed");
            long unitRange = (Long) attackInformation.get("UnitRange");
            double attackSpeed = (Double) attackInformation.get("AttackSpeed");
            long attackDamage = (Long) attackInformation.get("AttackDamage");
            if (fileName.equals("./attacker.json")) {
                unitsArray.add(new attacker((int) typeForce, (int) height, (int) width, (int) xUnit,
                        (int) yUnit, (int) price, type, name, (int) health, armor, arena,
                        (int) attackDamage, (int) unitRange, (int) speed, attackSpeed, priority,
                        (int) xBase, (int) yBase, (int) hBase, (int) wBase, CanTarget, maxBorder));
            } else {
                unitsArray.add(new defender((int) typeForce, (int) height, (int) width, (int) xUnit,
                        (int) yUnit, (int) price, type, name, (int) health, armor, arena,
                        (int) attackDamage, (int) unitRange, (int) speed, attackSpeed, priority,
                        (int) xBase, (int) yBase, (int) hBase, (int) wBase, CanTarget, maxBorder));
            }
        }

        return unitsArray;
    }

    public int getUnitsNumber() {
        return unitsArray.size();
    }
}
