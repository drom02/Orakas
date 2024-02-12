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
            if(e.getStart().isAfter(this.get(i).getEnd())||e.getStart().isEqual(this.get(i).getEnd() )) {
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
