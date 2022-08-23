package DataType;

import java.util.Vector;

public class MyVector {
    private final Vector<pair<Integer, Integer>> Axis = new Vector<>();
    public synchronized boolean lockIsFailed(int x, int y){
        if(Axis.contains(new pair<>(x, y))){
            return true;
        }
        Axis.add(new pair<>(x, y));
        return false;
    }
    public synchronized boolean freeLock(int x, int y){
        if(Axis.contains(new pair<>(x, y))){
            Axis.remove(new pair<>(x, y));
            return true;
        }
        System.out.println(x+" "+y);
        return false;
    }
}