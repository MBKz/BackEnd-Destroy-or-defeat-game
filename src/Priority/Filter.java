package Priority;

import Units.Attackable;

import java.util.ArrayList;

public class Filter {

    protected Priority o;

    public Filter(Priority o) {
        this.o = o;
    }

    public void sort(ArrayList<Attackable> MyArray) {
        ArrayList<Attackable> Temp = new ArrayList<>();
        Temp.addAll(MyArray);
        MyArray.clear();
        while (!Temp.isEmpty()){
            Attackable obj = Temp.get(0);
            for (int i = 1 ; i < Temp.size(); i++){
                if(o.compare(obj, Temp.get(i)) == -1){
                    obj = Temp.get(i);
                }
            }
            Temp.remove(obj);
            MyArray.add(obj);
        }
    }
}