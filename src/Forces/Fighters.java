package Forces;

import DataType.Map2D;
import DataType.MyVector;
import DataType.pair;
import Destroy_or_Defeat.LogFile;
import Priority.*;
import Units.*;

import java.util.*;

abstract public class Fighters extends Attackable implements Movement, Props {

    protected int AttackDamage, UnitRange, Speed;
    protected double AttackSpeed;
    protected String Priority;
    protected int xBase, yBase, hBase, wBase;
    protected Set<String> CanTarget;
    protected ArrayList<unit> CannotTarget;
    protected ArrayList<Attackable> EnemyInMyRange;
    final int MaxBorder;
    public final int typeForce;
    public static boolean BaseIsDestroyed = false;
    public static int AttackerNum ;
    static MyVector[] Axis = new MyVector[1000000];
    static {
        for (int i = 0; i < 1000000; i++){
            Axis[i] = new MyVector();
        }
    }
    public static void setBaseIsDestroyed(){ BaseIsDestroyed = true;}
    public static int getAttackerNum () { return AttackerNum;}
    public static void dicAttackerNum() { AttackerNum--;}
    public static void setAttackerNum(int Num) {AttackerNum = Num;}

    public Fighters(int typeForce, int height, int width, int xUnit, int yUnit, int price, String type, String name, int health, double armor,
                    Map2D<Integer, Integer, pair<pair<unit, unit>, unit>> arena, int attackDamage, int unitRange, int speed,
                    double attackSpeed, String priority, int xBase, int yBase, int hBase, int wBase,
                    Set<String> canTarget, int maxBorder) {
        super(height, width, xUnit, yUnit,price, type, name, health, armor, arena);
        this.typeForce = typeForce;
        AttackDamage = attackDamage;
        UnitRange = unitRange;
        Speed = speed;
        AttackSpeed = attackSpeed;
        Priority = priority;
        this.xBase = xBase;
        this.yBase = yBase;
        this.hBase = hBase;
        this.wBase = wBase;
        CanTarget = canTarget;
        CannotTarget = new ArrayList<>();
        EnemyInMyRange = new ArrayList<>();
        MaxBorder = maxBorder;
    }
    public int getAttackDamage() {
        return AttackDamage;
    }
    @Override
    public String toString() {
        return super.toString();
    }

    boolean GameIsRunning(){
        return !(BaseIsDestroyed || AttackerNum == 0);
    }
    @Override
    public void run() {
        while(this.IsAlive  && GameIsRunning()) {
            getUnitInMyRange();
            while (!EnemyInMyRange.isEmpty()) {
                attackUnit();
                getUnitInMyRange();
            }
            if(this.Speed != 0 && GameIsRunning()) {
                int lastX = xUnit , lastY=yUnit;
                double MoveSpeed = Speed;
                try {
                    if (arena.get(xUnit, yUnit).Second != null && typeForce == 1) {
                        MoveSpeed *= ((River) arena.get(xUnit, yUnit).Second).getSpeedDown();
                    }
                }catch (Exception ignored) {}
                move();
                if(lastX != xUnit || lastY != yUnit) {
                    LogFile.WriteAction(this,this.toString(), "  < moved >  ", "From ("+lastX+","+lastY+")");
                    try {
                        Thread.sleep((int)(1000 /(MoveSpeed)));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        System.out.println(" Catch move Exception in Fighter/run");
                    }
                }
            }
        }
        try {
            this.stop();
        }catch( Exception ignored) {}
    }

    @Override
    public void getUnitInMyRange() {
        Set<Attackable> MyTempEnemy = new HashSet<>();
        Set<unit>MyTempCannotTarget = new HashSet<>();
        int CornerX = xUnit - UnitRange, CornerY = yUnit - UnitRange;
        for (int i = CornerX; i < (2 * UnitRange + height + CornerX); i++) {
            for (int j = CornerY; j < (2 * UnitRange + width + CornerY); j++) {
                pair<pair<unit, unit>, unit> obj = arena.get(i, j);
                if(obj == null){
                    continue;
                }
                if(obj.First == null ){
                    continue;
                }
                Attackable GroundForce = null, AirForce = null;
                if (obj.First.First instanceof Attackable) {
                    GroundForce = (Attackable) obj.First.First;
                }
                if (obj.First.First instanceof Valley && typeForce == 1) {
                    MyTempCannotTarget.add(obj.First.First);
                }
                if (obj.First.Second instanceof Attackable) {
                    AirForce = (Attackable) obj.First.Second;
                }

//                River river = (River) obj.Second; // not important

                try{
                    if(GroundForce != null){
                        if(((this instanceof attacker && (GroundForce instanceof defender ||
                                (GroundForce instanceof Building )))
                                || (this instanceof defender && GroundForce instanceof attacker))
                                && CanTarget.contains(GroundForce.getType())
                                && GroundForce.getIsAlive()){

                            MyTempEnemy.add(GroundForce);
                        }
                        else if (this != GroundForce){
                            MyTempCannotTarget.add(GroundForce);
                        }
                    }
                    if(AirForce != null){
                        if(((this instanceof attacker && AirForce instanceof defender)
                                || (this instanceof defender && AirForce instanceof attacker))
                                && (CanTarget.contains(AirForce.getType()))
                                && AirForce.getIsAlive()){

                            MyTempEnemy.add(AirForce);
                        }
                        else if(AirForce != this){
                            MyTempCannotTarget.add(AirForce);
                        }
                    }
                }
                catch(Exception e){
                    e.printStackTrace();
                    System.out.println(this+" catch exception in Fighter/GitUnitInMyRange"+obj);
                    System.exit(-1);
                }
            }
        }
        EnemyInMyRange.clear();
        EnemyInMyRange.addAll(MyTempEnemy);
        CannotTarget.clear();
        CannotTarget.addAll(MyTempCannotTarget);
        FilterMyEnemy();
    }

    @Override
    public void FilterMyEnemy() {
        Priority o = null;
        if(Priority.equals("Random")) {
            return;
        }
        if(Priority.equals("Damage")) {
            o = new damagePriority();
        }
        if(Priority.equals("Health")) {
            o = new healthPriority();
        }
        Filter Obj = new Filter(o);
        Obj.sort(EnemyInMyRange);
    }

    void BoomedCarInitialPlane(){
        Set<Attackable> AlreadyAttacked = new HashSet<>();
        int CornerX = xUnit - UnitRange, CornerY = yUnit - UnitRange;
        for (int i = CornerX; i < (2 * UnitRange + height + CornerX); i++) {
            for (int j = CornerY; j < (2 * UnitRange + width + CornerY); j++) {
                pair<pair<unit, unit>, unit> obj = arena.get(i, j);
                if (obj != null) {
                    unit attackUnit = obj.First.First;
                    if (obj.First.First instanceof Attackable && (!AlreadyAttacked.contains(obj.First.First)) && this != attackUnit) {
                        AlreadyAttacked.add((Attackable) attackUnit);
                        if (((Attackable) attackUnit).getIsAlive()) {
                            LogFile.WriteAction(this, this.toString(), "  < attacked >  ", attackUnit.toString() + " --> " + ((((Attackable) attackUnit).getHealth() + ((Attackable) attackUnit).getArmor()) - this.getAttackDamage()));
                            ((Attackable) attackUnit).modifyHealth(AttackDamage);
                        }
                    }
                }
            }
        }
        this.Health = 0;
        this.IsAlive = false;
        dicAttackerNum();
        LogFile.WriteAction(this,this.toString() , "  < destroyed >  " ,"");
        try{
            this.stop();
        }catch (Exception ignored){}
    }

    @Override
    public void attackUnit() {
        if(this.getUNitName().equals("BoomedCar")) {
            try {
                Thread.sleep((long) (AttackSpeed*1000));
                BoomedCarInitialPlane();
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.out.println("catch exception in Fighter/attackUnit/boomed car");
            }
        }
        else {
            Attackable attackUnit = EnemyInMyRange.get(0);
            if (attackUnit.getIsAlive()) {
                LogFile.WriteAction(this,this.toString() , "  < attacked >  ", attackUnit.toString()+" --> "+((attackUnit.getHealth() + attackUnit.getArmor()) - this.getAttackDamage()));
                attackUnit.modifyHealth(AttackDamage);
                try {
                    double s = ((1/this.AttackSpeed)*1000);
                    Thread.sleep((int)s);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    System.out.println("catch exception in Fighter/attackUnit/else/sleep");
                }
            }
        }

    }

    int getIndexMyVector(int i, int j){
        return (i/1000)*1000 + j/1000 + 1;
    }
    boolean lockColumn(int x1, int x2, int y){
        for(int i = x1; i <= x2; i++){
            if(Axis[getIndexMyVector(i, y)].lockIsFailed(i, y)){
                for(int j = i-1; j >= x1; j--){
                    Axis[getIndexMyVector(j, y)].freeLock(j, y);
                }
                return false;
            }
        }
        return true;
    }
    boolean lockRow(int x, int y1, int y2){
        for(int i = y1; i <= y2; i++){
            if(Axis[getIndexMyVector(x, i)].lockIsFailed(x, i)){
                for(int j = i-1; j >= y1; j--){
                    Axis[getIndexMyVector(x, j)].freeLock(x, j);
                }
                return false;
            }
        }
        return true;
    }
    void freeLockColumn(int x1, int x2, int y){
        for (int i = x1; i <= x2; i++){
            Axis[getIndexMyVector(i, y)].freeLock(i, y);
        }
    }
    void freeLockRow(int x, int y1, int y2){
        for (int i = y1; i <= y2; i++){
            Axis[getIndexMyVector(x, i)].freeLock(x, i);
        }
    }

     boolean checkedIfOut(int x, int y){
        return x >= MaxBorder || x < 0 || y >= MaxBorder || y < 0;
    }
     boolean checkColumn(int x1, int x2, int y){
        for(int i = x1; i <= x2; i++){
            if(checkedIfOut(i, y)) return false;
            if(arena.get(i, y) != null){
                if(this.typeForce == 1 && arena.get(i, y).First != null && arena.get(i, y).First.First != null
                        && !arena.get(i, y).First.First.getUNitName().equals("Base")){
                    return false;
                }
                if(this.typeForce == 2 && arena.get(i, y).First != null && arena.get(i, y).First.Second != null){
                    return false;
                }
            }
        }
        return true;
    }
     boolean checkRow(int x, int y1, int y2){
        for(int i = y1; i <= y2; i++) {
            if (checkedIfOut(x, i)) return false;
            if(arena.get(x, i) != null){
                if (typeForce == 1 && arena.get(x, i).First != null) {
                    if(arena.get(x, i).First.First != null && !arena.get(x, i).First.First.getUNitName().equals("Base")){
                        return false;
                    }
                    continue;
                }

                if (this.typeForce == 2 && arena.get(x, i).First != null && arena.get(x, i).First.Second != null) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void moveUp() {
        for(int i = xUnit; i <= xUnit+height-1; i++){
            pair<pair<unit, unit>, unit> EditObj = arena.remove(i, yUnit + width), DeleteObj = arena.remove(i, yUnit);
            if(EditObj == null) EditObj = new pair<>();
            if(EditObj.First == null) EditObj.First = new pair<>();
            try{
            if(typeForce == 1){
                EditObj.First.First = this;
                DeleteObj.First.First = null;
            }
            else {
                EditObj.First.Second = this;
                DeleteObj.First.Second = null;
            }
            arena.put(i, yUnit + width, EditObj);

            if(!(DeleteObj.Second == null && DeleteObj.First.First == null && DeleteObj.First.Second == null)){
                arena.put(i, yUnit, DeleteObj);
            }
            }catch (Exception ignored) {}
        }
        yUnit++;
    }
    @Override
    public void moveDown() {
        for(int i = xUnit; i <= xUnit+height-1; i++){
            pair<pair<unit, unit>, unit> EditObj = arena.remove(i, yUnit - 1), DeleteObj = arena.remove(i, yUnit+width-1);
            if(EditObj == null) EditObj = new pair<>();
            if(EditObj.First == null) EditObj.First = new pair<>();
            try{
            if(typeForce == 1){
                EditObj.First.First = this;
                DeleteObj.First.First = null;
            }
            else {
                EditObj.First.Second = this;
                DeleteObj.First.Second = null;
            }
            arena.put(i, yUnit - 1, EditObj);
            if(!(DeleteObj.Second == null && DeleteObj.First.First == null && DeleteObj.First.Second == null)) {
                arena.put(i, yUnit + width - 1, DeleteObj);
            }
        }catch (Exception ignored) {}
        }
        yUnit--;
    }
    @Override
    public void moveLeft() {
        pair<pair<unit, unit>, unit> EditObj, DeleteObj;
        for(int i = yUnit; i <= yUnit+width-1; i++){
            EditObj = arena.remove(xUnit-1, i); DeleteObj = arena.remove(xUnit+height-1, i);
            if(EditObj == null) EditObj = new pair<>();
            if(EditObj.First == null) EditObj.First = new pair<>();
            try {
                if (typeForce == 1) {
                    EditObj.First.First = this;
                    DeleteObj.First.First = null;
                } else {
                    EditObj.First.Second = this;
                    DeleteObj.First.Second = null;
                }
                arena.put(xUnit - 1, i, EditObj);

                if (!(DeleteObj.Second == null && DeleteObj.First.First == null && DeleteObj.First.Second == null)) {
                    arena.put(xUnit + height - 1, i, DeleteObj);
                }
            }catch (Exception ignored) {}
        }
        xUnit--;
    }
    @Override
    public void moveRight() {
        for(int i = yUnit; i <= yUnit+width-1; i++){
            pair<pair<unit, unit>, unit> EditObj = arena.remove(xUnit + height, i), DeleteObj = arena.remove(xUnit, i);
            if(EditObj == null) EditObj = new pair<>();
            if(EditObj.First == null) EditObj.First = new pair<>();
            try{
            if(typeForce == 1){
                EditObj.First.First = this;
                DeleteObj.First.First = null;
            }
            else {
                EditObj.First.Second = this;
                DeleteObj.First.Second = null;
            }
            arena.put(xUnit + height, i, EditObj);

            if(!(DeleteObj.Second == null && DeleteObj.First.First == null && DeleteObj.First.Second == null)) {
                arena.put(xUnit, i, DeleteObj);
            }
            }catch (Exception ignored) {}
        }
        xUnit++;
    }
}


