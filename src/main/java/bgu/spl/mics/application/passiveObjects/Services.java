package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.services.TimeService;

public class Services {
    private Time time;
    private Integer selling;
    private Integer inventoryService;
    private Integer logistics;
    private Integer resourcesService;
    private Customer[] customers;


    public Time getTime() {
        return time;
    }

    public Integer getSelling() {
        return selling;
    }

    public Integer getInventoryService() {
        return inventoryService;
    }

    public Integer getLogistics() {
        return logistics;
    }

    public Integer getResourcesService() {
        return resourcesService;
    }

    public Customer[] getCustomers() {
        return customers;
    }

}
