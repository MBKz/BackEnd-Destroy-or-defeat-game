package Units;

import Destroy_or_Defeat.LogFile;

public class River extends UnAttackable{

    protected double SpeedDown;

    public River(int height, int width, int xUnit, int yUnit, int price, String type, String name, double SpeedDown) {
        super(height, width, xUnit, yUnit,price, type, name);
        LogFile.WriteSize(this.toString());
        this.SpeedDown = SpeedDown;
    }

    public double getSpeedDown() {
        return SpeedDown;
    }
}