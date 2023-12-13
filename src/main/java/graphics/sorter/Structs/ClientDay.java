package graphics.sorter.Structs;

import java.time.LocalTime;
import java.util.ArrayList;

public class ClientDay {
    private Location location;
    private ServinceIntervalArrayList dayIntervalList = new ServinceIntervalArrayList();

    public void test(){
        ServiceInterval test2 = new ServiceInterval(LocalTime.of(10,00,00), LocalTime.of(14,00,00));
        ServiceInterval test1 = new ServiceInterval(LocalTime.of(15,00,00), LocalTime.of(16,00,00));
        ServiceInterval test3 = new ServiceInterval(LocalTime.of(8,00,00), LocalTime.of(9,00,00));
        dayIntervalList.add(test1);
        dayIntervalList.add(test2);
        dayIntervalList.add(test3);
        System.out.println("test");

    }


}
