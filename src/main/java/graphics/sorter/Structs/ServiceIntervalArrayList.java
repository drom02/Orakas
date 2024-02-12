package graphics.sorter.Structs;

import java.util.ArrayList;

public class ServiceIntervalArrayList extends ArrayList<ServiceInterval> {
    @Override
    public boolean add(ServiceInterval e){
        if (e == null) {
            return false;
        }
        if (this.size() == 0){
            super.add(e);
            return true;
        }
        Integer i=0;
        int lastIndex = 0;
        while(i < this.size()){
            this.get(i);
            if(e.getStart().compareTo(this.get(i).getEnd())>= 0) {
                lastIndex = i+1;
                    }
            i++;
        }
            super.add(lastIndex,e);
            return true;


        /*
        else if (this.size() > 0) {
           super.add(0,e);
            return true;
        }{
            throw new ArithmeticException("Interval error");
        }
         */


    }
}
