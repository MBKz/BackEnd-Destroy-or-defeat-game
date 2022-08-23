package Units;

import Destroy_or_Defeat.LogFile;

public class Valley extends UnAttackable{

    public Valley(int height, int width, int xUnit, int yUnit,int price, String type, String name) {
        super(height, width, xUnit, yUnit,price, type, name);
        LogFile.WriteSize(this.toString());
    }
}