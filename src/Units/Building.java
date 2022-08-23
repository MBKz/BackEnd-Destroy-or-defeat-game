package Units;

import DataType.Map2D;
import DataType.pair;
import Destroy_or_Defeat.LogFile;

public class Building extends Attackable {

    public Building( int height, int width,int xUnit, int yUnit, int price, String type, String name, int health, double armor
            , Map2D<Integer, Integer, pair<pair<unit, unit>, unit>> arena) {
        super(height, width, xUnit, yUnit, price,type, name, health, armor, arena);
        LogFile.WriteSize(this.toString());
    }

}