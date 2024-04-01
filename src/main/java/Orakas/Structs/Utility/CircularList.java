package Orakas.Structs.Utility;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class CircularList<E> extends LinkedList<E> {
    public CircularList() {
    }
    public CircularList(Collection<? extends E> c) {
        this();
        addAll(c);
    }

    public E previous(E current){
        return getCircular(indexOf(current)-1);
    }
    public  E next(E current){
        return getCircular(indexOf(current)+1);
    }
   public E getCircular(int index){
       if(index==this.size()){
           return this.getFirst();
       } else if (index == -1) {
           return this.getLast();
       }
      return this.get(index);
}
}
