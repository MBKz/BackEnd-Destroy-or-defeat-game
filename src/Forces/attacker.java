package Forces;

import DataType.*;
import Destroy_or_Defeat.*;
import Units.*;
import java.util.*;

public class attacker extends Fighters {
    LinkedList<pair<Integer, Integer>> ThePath;
    int DistToBase;
    public attacker(int typeForce, int height, int width, int xUnit, int yUnit, int price, String type, String name, int health, double armor,
                    Map2D<Integer, Integer, pair<pair<unit, unit>, unit>> arena, int attackDamage, int unitRange, int speed,
                    double attackSpeed, String priority, int xBase, int yBase, int hBase, int wBase, Set<String> canTarget, int maxBorder) {
        super(typeForce, height, width, xUnit, yUnit,price, type, name, health, armor, arena, attackDamage, unitRange, speed,
                attackSpeed, priority, xBase, yBase, hBase, wBase, canTarget, maxBorder);
        LogFile.WriteSize(this.toString());
        ThePath = new LinkedList<>();
        DistToBase = (Math.max(Math.abs(xUnit-xBase), Math.abs(yUnit-yBase)) / 10)*10;
    }

    @Override
    public String toString() {
        return "attacker: " + super.toString();
    }

    @Override
    public void move() {
        class GetPath{
            final pair<Integer, Integer> EndOfPath = new pair<>(xBase, yBase);
            final LinkedList<pair<Integer, Integer>> Q = new LinkedList<>();
            final Map2D<Integer, Integer, Boolean> Visited = new Map2D<>();
            final Map2D<Integer, Integer, pair<Integer, Integer>> Parent = new Map2D<>();
            int dist(pair<Integer, Integer> P){
                return Math.max(Math.abs(P.First-xBase), Math.abs(P.Second-yBase));
            }

            LinkedList<pair<Integer, Integer>> getPath(){
                Q.addLast(new pair<>(xUnit, yUnit));
                Visited.put(xUnit, yUnit, true);
                pair<Integer, Integer> Current = null;
                while (!Q.isEmpty()){
                    Current = Q.removeFirst();

                    if(Current.First.equals(EndOfPath.First) && Current.Second.equals(EndOfPath.Second)){
                        break;
                    }
                    if(DistToBase > 0 && dist(Current) <= DistToBase){
                        EndOfPath.clone(Current);
                        DistToBase -= 10;
                        break;
                    }

                    final int xUp = Current.First, yUp = Current.Second + 1,
                            xDown = Current.First, yDown = Current.Second - 1,
                            xLeft = Current.First - 1, yLeft = Current.Second,
                            xRight = Current.First + 1, yRight = Current.Second;

                    if(lockColumn(xUp, xUp+height-1, yUp)) {
                        if (checkColumn(xUp, xUp + height - 1, yUp) && Visited.get(xUp, yUp) == null) {
                            Q.addLast(new pair<>(xUp, yUp));
                            Visited.put(xUp, yUp, true);
                            Parent.put(xUp, yUp, Current);
                        }
                        freeLockColumn(xUp, xUp+height-1, yUp);
                    }
                    if(lockColumn(xDown, xDown+height-1, yDown)) {
                        if (checkColumn(xDown, xDown + height - 1, yDown) && Visited.get(xDown, yDown) == null) {
                            Q.addLast(new pair<>(xDown, yDown));
                            Visited.put(xDown, yDown, true);
                            Parent.put(xDown, yDown, Current);
                        }
                        freeLockColumn(xDown, xDown+height-1, yDown);
                    }
                    if(lockRow(xLeft, yLeft, yLeft+width-1)) {
                        if (checkRow(xLeft, yLeft, yLeft + width - 1) && Visited.get(xLeft, yLeft) == null) {
                            Q.addLast(new pair<>(xLeft, yLeft));
                            Visited.put(xLeft, yLeft, true);
                            Parent.put(xLeft, yLeft, Current);
                        }
                        freeLockRow(xLeft, yLeft, yLeft+width-1);
                    }
                    if(lockRow(xRight, yRight, yRight+width-1)) {
                        if (checkRow(xRight, yRight, yRight + width - 1) && Visited.get(xRight, yRight) == null) {
                            Q.addLast(new pair<>(xRight, yRight));
                            Visited.put(xRight, yRight, true);
                            Parent.put(xRight, yRight, Current);
                        }
                        freeLockRow(xRight, yRight, yRight+width-1);
                    }
                }
                if(Parent.get(EndOfPath.First, EndOfPath.Second) == null) {
                    return new LinkedList<>();
                }
                LinkedList<pair<Integer, Integer>> Solution = new LinkedList<>();
                Solution.addFirst(EndOfPath);
                pair<Integer, Integer> Cur = new pair<>(EndOfPath.First, EndOfPath.Second);
                while (Parent.get(Cur.First, Cur.Second) != null){
                    Cur = Parent.get(Cur.First, Cur.Second);
                    Solution.addFirst(Cur);
                }
                Solution.removeFirst();
                return Solution;
            }
        }
        GetPath Obj = new GetPath();
        if(ThePath.size() == 0) {
            ThePath.addAll(Obj.getPath());
        }
        if(ThePath.size() == 0){
            return;
        }
        pair<Integer, Integer> Point = new pair<>(ThePath.getFirst());
        ThePath.removeFirst();
        if(Point.First == xUnit){
            if(Point.Second < yUnit){
                if(lockColumn(xUnit, xUnit+height-1, Point.Second)) {
                    if (checkColumn(xUnit, xUnit + height - 1, Point.Second)) {
                        moveDown();
                        freeLockColumn(xUnit, xUnit+height-1, Point.Second);
                    }
                    else{
                        freeLockColumn(xUnit, xUnit+height-1, Point.Second);
                        ThePath.clear();
                        ThePath.addAll(Obj.getPath());
                        move();
                    }
                }
                else{
                    ThePath.clear();
                    ThePath.addAll(Obj.getPath());
                    move();
                }
            }
            else { // Point.Second = yUnit
                if(lockColumn(xUnit, xUnit+height-1, Point.Second + width - 1)) {
                    if (checkColumn(xUnit, xUnit + height - 1, Point.Second + width - 1)) {
                        moveUp();
                        freeLockColumn(xUnit, xUnit + height - 1, Point.Second + width - 1);
                    }
                    else {
                        freeLockColumn(xUnit, xUnit + height - 1, Point.Second + width - 1);
                        ThePath.clear();
                        ThePath.addAll(Obj.getPath());
                        move();
                    }
                }
                else {
                    ThePath.clear();
                    ThePath.addAll(Obj.getPath());
                    move();
                }
            }
        }
        else{
            if(Point.First < xUnit) {
                if(lockRow(Point.First, yUnit, yUnit + width - 1)) {
                    if (checkRow(Point.First, yUnit, yUnit + width - 1)) {
                        moveLeft();
                        freeLockRow(Point.First, yUnit, yUnit + width - 1);
                    }
                    else {
                        freeLockRow(Point.First, yUnit, yUnit + width - 1);
                        ThePath.clear();
                        ThePath.addAll(Obj.getPath());
                        move();
                    }
                }
                else {
                    ThePath.clear();
                    ThePath.addAll(Obj.getPath());
                    move();
                }
            }
            else {
                if(lockRow(Point.First + height - 1, yUnit, yUnit + width - 1)) {
                    if (checkRow(Point.First + height - 1, yUnit, yUnit + width - 1)) {
                        moveRight();
                        freeLockRow(Point.First + height - 1, yUnit, yUnit + width - 1);
                    }
                    else {
                        freeLockRow(Point.First + height - 1, yUnit, yUnit + width - 1);
                        ThePath.clear();
                        ThePath.addAll(Obj.getPath());
                        move();
                    }
                }
                else {
                    ThePath.clear();
                    ThePath.addAll(Obj.getPath());
                    move();
                }
            }
        }
    }
}
