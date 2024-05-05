package Orakas.controllers.Utils;

import Orakas.Structs.TimeStructs.ServiceInterval;
/*
Class is used  for management of selected interval.
 */
public class SelectedInterval {
    public ServiceInterval get() {
        return interval;
    }

    public void set(ServiceInterval interval) {
        this.interval = interval;
    }

    private ServiceInterval interval = null;
}
