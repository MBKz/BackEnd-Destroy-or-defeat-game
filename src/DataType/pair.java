package DataType;

import java.util.Objects;

public class pair<first, second>{
    public first First;
    public second Second;
    public pair(first First, second Second){
        this.First = First;
        this.Second = Second;
    }
    public pair(){
        First = null;
        Second = null;
    }
    public pair(pair<first, second> P){
        First = P.First;
        Second = P.Second;
    }

    public void clone(pair<first, second> P){
        First = P.First;
        Second = P.Second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        pair<?, ?> pair = (pair<?, ?>) o;
        return First.equals(pair.First) &&
                Second.equals(pair.Second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(First, Second);
    }

    @Override
    public String toString() {
        return "pair{" +
                "First=" + First +
                ", Second=" + Second +
                '}';
    }
}