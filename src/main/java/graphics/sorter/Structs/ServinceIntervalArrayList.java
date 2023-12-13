package graphics.sorter.Structs;

import java.util.ArrayList;

public class ServinceIntervalArrayList  extends ArrayList<ServiceInterval> {
    @Override
    public boolean add(ServiceInterval e){
        if (e == null) {
            return false;
        }
        Integer i=0;
        while(i < this.size()){
            this.get(i);
            if(e.getStart().compareTo(this.get(i).getEnd())> 0) {
                super.add(i+1,e);
                System.out.println("lol");
                return true;
                    }
            i++;
        }
        if (this.size() == 0){
            super.add(e);
            return true;
        } else if (this.size() > 0) {
            super.add(0,e);
            return true;
        }{
            throw new ArithmeticException("Interval error");
        }

    }
}
