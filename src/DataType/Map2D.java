package DataType;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Map2D<Key1, Key2, Value>{
    private final Map<Key1, Map<Key2, Value>> Mp = new ConcurrentHashMap<>(new ConcurrentHashMap<>());

    public void put(Key1 key1, Key2 key2, Value value){
        Mp.computeIfAbsent(key1, k -> new ConcurrentHashMap<>());

        Mp.get(key1).put(key2, value);
    }
    public Value get(Key1 x, Key2 y){
        Map<Key2, Value> TempMap = Mp.get(x);
        if(TempMap == null){
            return null;
        }
        return TempMap.get(y);
    }
    public Value remove(Key1 K1, Key2 K2){
        if(get(K1, K2) == null) return null;
        Map<Key2, Value> Temp = Mp.get(K1);
        Value value = Temp.remove(K2);
        if(Temp.size() == 0){
            Mp.remove(K1);
        }
        return value;
    }
    public int size(){
        Set<Key1> S = Mp.keySet();
        int Sum = 0;
        for(Key1 K : S){
            Sum += Mp.get(K).entrySet().size();
        }
        return Sum;
    }
}
