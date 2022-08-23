package Units;

import DataType.*;
import org.json.simple.*;
import org.json.simple.parser.*;
import Destroy_or_Defeat.*;
import java.io.*;
import java.util.*;


public class ReadFile {

    Map2D<Integer, Integer, pair<pair<unit, unit>, unit>> arena;
    String fileName;
    ArrayList<unit> unitsArray;

    public ReadFile(String fileName,Map2D<Integer, Integer, pair<pair<unit, unit>, unit>> arena) {
        this.fileName = fileName;
        this.arena = arena;
        this.unitsArray = new ArrayList<>();
        LogFile.WriteSize(this.toString());
    }
    public ArrayList<unit> addTerrain() throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        Object obj;
        try (Reader reader = new FileReader(fileName)) {
            obj = parser.parse(reader);
        }
        JSONArray jsonArray = (JSONArray) obj;
        for(int i=0;i<jsonArray.size();i++) {
            JSONObject json = (JSONObject) jsonArray.get(i);
            String name = (String) json.get("Name");
            String type = (String) json.get("Type");
            long price = (Long) json.get("Price");
            JSONObject coordinates = (JSONObject) json.get("Coordinates");
            long xUnit = (Long) coordinates.get("x");
            long yUnit = (Long) coordinates.get("y");
            long height = (Long) coordinates.get("height");
            long width = (Long) coordinates.get("width");
            if (name.equals("river")) {
                double speedDown = (Double) json.get("SpeedDown");
                unitsArray.add(new River((int) height, (int) width, (int) xUnit, (int) yUnit, (int) price, type, name, speedDown));
            }
            else
                unitsArray.add(new Valley((int) height, (int) width, (int) xUnit, (int) yUnit, (int) price, type, name));
        }
        return unitsArray;
    }
    public ArrayList<unit> addBuilding() throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        Object obj;
        try (Reader reader = new FileReader(fileName)) {
            obj = parser.parse(reader);
        }
        JSONArray  jsonArray = (JSONArray) obj;
        for(int i=0;i<jsonArray.size();i++) {
            JSONObject json = (JSONObject) jsonArray.get(i);
            String name = (String) json.get("Name");
            String type = (String) json.get("Type");
            long price = (Long) json.get("Price");
            JSONObject coordinates = (JSONObject) json.get("Coordinates");
            long xUnit = (Long) coordinates.get("x");
            long yUnit = (Long) coordinates.get("y");
            long radios = (Long) coordinates.get("radios");
            long height = 2 * radios + 1;
            long width = 2 * radios + 1;
            JSONObject attackInformation = (JSONObject) json.get("AttackInformation");
            long health = (Long) attackInformation.get("Health");
            double armor = (Double) attackInformation.get("Armor");
            unitsArray.add(new Building( (int) height, (int) width,(int) xUnit, (int) yUnit, (int) price, type, name, (int) health, armor, arena));

        }
        return unitsArray;
    }

}
