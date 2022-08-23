package Units;

import DataType.Map2D;
import DataType.pair;
import Destroy_or_Defeat.LogFile;
import Forces.Fighters;
import Forces.attacker;

abstract public class Attackable extends unit {

    protected int Health;

    protected double Armor;

    protected Map2D<Integer, Integer, pair<pair<unit, unit>, unit>> arena;

    protected boolean IsAlive = true;

    public Attackable(int height, int width, int xUnit, int yUnit,int price, String type, String name, int health, double armor,
                       Map2D<Integer, Integer, pair<pair<unit, unit>, unit>> arena) {
        super(height, width, xUnit, yUnit, price, type, name);
        Health = health;
        Armor = armor;
        this.arena = arena;
    }

    public int getHealth() {
        return Health;
    }

    public double getArmor() {return Armor;}

    public boolean getIsAlive() {
        return IsAlive;
    }

    @Override
    public String toString() {
        return
                super.toString() +
                " | Health=" + Health ;
    }

    public void modifyHealth(int damage) {
        Health += (-1 * damage) + Armor;
        if(Health <= 0)
        {
            IsAlive = false;
            if(this instanceof attacker) Fighters.dicAttackerNum();
            if(this instanceof Building && getUNitName().equals("Base")) {Fighters.setBaseIsDestroyed();}
            if(this.getUNitName().equals("Bridge")) {
                for(int i=xUnit; i<xUnit+height; i++) {
                    for(int j=yUnit; j<yUnit+width; j++) {
                        pair<pair<unit,unit>,unit> p =arena.remove(i,j);
                        p.First.First = null;
                        if( (p.First.Second != null) || (p.Second != null) ) arena.put(i, j, p);
                    }
                }
            }
            LogFile.WriteAction(this,this.toString() , "  < destroyed >  " ,"");
            if (this instanceof Fighters) {
                try {
                    this.stop();
                }catch (Exception ignored){}
            }
        }
    }
}

