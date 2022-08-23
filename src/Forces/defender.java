package Forces;

import DataType.*;
import Destroy_or_Defeat.*;
import Units.*;
import java.util.*;

public class defender extends Fighters {
    ArrayList<pair<Integer,Integer>> Corners = GetCorners();
    int Corner, Direction = 1;

    @Override
    public String toString() {
        return "defender: " + super.toString();
    }

    public defender(int typeForce, int height, int width, int xUnit, int yUnit, int price, String type, String name, int health, double armor,
                    Map2D<Integer, Integer, pair<pair<unit, unit>, unit>> arena, int attackDamage, int unitRange, int speed,
                    double attackSpeed, String priority, int xBase, int yBase, int hBase, int wBase,
                    Set<String> canTarget, int maxBorder) {
        super(typeForce, height, width, xUnit, yUnit,price, type, name, health, armor, arena, attackDamage, unitRange, speed,
                attackSpeed, priority, xBase, yBase, hBase, wBase, canTarget, maxBorder);
        LogFile.WriteSize(this.toString());
        Corner = initializerCorner();
    }

    ArrayList<pair<Integer,Integer>> GetCorners(){
        int dist = GetDist();
        ArrayList<pair<Integer, Integer>> Temp = new ArrayList<>();
        int XUpLeft = xBase-dist-width,YUpLeft = yBase-dist-height;
        Temp.add(new pair(XUpLeft,YUpLeft));
        int XUpRight = xBase-dist-width,YUpRight = yBase+wBase+dist;
        Temp.add(new pair(XUpRight,YUpRight));
        int XDownRight = xBase+hBase+dist,YDownRight = yBase+wBase+dist;
        Temp.add(new pair(XDownRight,YDownRight));
        int XDownLeft = xBase+hBase+dist,YDownLeft =  yBase-dist-height;
        Temp.add(new pair(XDownLeft,YDownLeft));
        return Temp;
    }

    int GetDist () {
        int Dist = 0;
        if (xUnit >= xBase && xUnit <= xBase + hBase - 1 && yUnit < yBase) {
            Dist = yBase - (yUnit + width);
        }
        if (xUnit < xBase && yUnit < yBase) {
            Dist = Math.max(xBase - (xUnit + height), yBase - (yUnit + width));
        }
        if (xUnit > xBase + hBase - 1 && yUnit < yBase) {
            Dist = Math.max(xUnit - (xBase + hBase), yBase - (yUnit + width));
        }
        if (xUnit < xBase && yUnit >= yBase && yUnit <= yBase + wBase - 1) {
            Dist = xBase - (xUnit + height);
        }
        if(xUnit < xBase && yUnit > yBase+wBase-1){
            Dist = Math.max(xBase - (xUnit + height), yUnit - (yBase + wBase));
        }
        if(xUnit >= xBase && xUnit <= xBase + hBase - 1 && yUnit > yBase+wBase-1){
            Dist = yUnit - (yBase + wBase) ;
        }
        if(xUnit > xBase + hBase - 1 && yUnit > yBase + wBase - 1 ) {
            Dist = Math.max(xUnit - (xBase + hBase), yUnit - (yBase + wBase));
        }
        if(xUnit > xBase + hBase - 1  && yUnit >= yBase && yUnit <= yBase + wBase -1){
            Dist = xUnit - (xBase + hBase);
        }
        return Dist;
    }

    int initializerCorner(){
        for(int i = 0; i < 4; i++){
            int x = Corners.get(i).First, y = Corners.get(0).Second;
            if(x == xUnit && y == yUnit){
                Corner = i + 1;
                if(Corner == 4) Corner = 0;
                if(Corner == -1) Corner = 3;
            }
        }
        int dist = GetDist();
        int XUpLeft = xBase-dist-width,YUpLeft = yBase-dist-height;
        int YUpRight = yBase+wBase+dist;
        int XDownLeft = xBase+hBase+dist;
        if ( (xUnit > XUpLeft && xUnit < XDownLeft) && (yUnit == YUpLeft) )  return 0;
        if( (xUnit == XUpLeft) && (yUnit > YUpLeft && yUnit < YUpRight)) return 1;
        if ( (xUnit > XUpLeft && xUnit < XDownLeft) && (yUnit == YUpRight) )  return 2;
        else return 3;
    }

    @Override
    public void move() {
        class moveDefender {
            synchronized void check(int x, int y){
                if(xUnit == x && yUnit == y){
                    Corner += Direction;
                }
                if(Corner == -1){
                    Corner = 3;
                }
                else if(Corner == 4){
                    Corner = 0;
                }
            }
            void move(){
                int x = Corners.get(Corner).First, y = Corners.get(Corner).Second;
                int myX = xUnit, myY = yUnit;
                if(xUnit == x){
                    if(yUnit < y){
                        boolean locked = lockColumn(myX, myX+height-1, myY+width);
                        if (locked && checkColumn(myX, myX + height - 1, myY + width)) {
                            moveUp();
                        }
                        else {
                            Direction *= -1;
                            Corner += Direction;
                        }
                        if(locked){
                            freeLockColumn(myX, myX+height-1, myY+width);
                        }
                    }
                    else {
                        boolean locked = lockColumn(myX, myX+height-1, myY-1);
                        if (locked && checkColumn(xUnit, xUnit + height - 1, yUnit - 1)) {
                            moveDown();
                        }
                        else {
                            Direction *= -1;
                            Corner += Direction;
                        }
                        if(locked){
                            freeLockColumn(myX, myX+height-1, myY-1);
                        }
                    }
                }
                else {
                    if(xUnit > x){
                        boolean locked = lockRow(myX-1, myY, myY+width-1);
                        if (locked && checkRow(xUnit - 1, yUnit, yUnit + width - 1)) {
                            moveLeft();
                        }
                        else {
                            Direction *= -1;
                            Corner += Direction;
                        }
                        if(locked){
                            freeLockRow(myX-1, myY, myY+width-1);
                        }
                    }
                    else {
                        boolean locked = lockRow(myX+height, myY, myY+width-1);
                        if (locked && checkRow(myX + height, myY, myY + width - 1)) {
                            moveRight();
                        }
                        else {
                            Direction *= -1;
                            Corner += Direction;
                        }
                        if(locked){
                            freeLockRow(myX+height, myY, myY+width-1);
                        }
                    }
                }
                check(x, y);
            }
        }
        moveDefender Obj = new moveDefender();
        Obj.move();
    }
}
