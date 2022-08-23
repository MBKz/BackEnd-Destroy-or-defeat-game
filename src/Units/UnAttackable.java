package Units;

import Destroy_or_Defeat.LogFile;

abstract public class UnAttackable extends unit{

    public UnAttackable(int height, int width, int xUnit, int yUnit, int price, String type, String name) {
        super(height, width, xUnit, yUnit,price, type, name);
        LogFile.WriteSize(this.toString());
    }
}
