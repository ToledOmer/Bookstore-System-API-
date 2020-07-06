package bgu.spl.mics.application.passiveObjects;


public class BookStoreInfo {
    private BookInventoryInfo[] initialInventory;
    private Resources[] initialResources;
    private Services services;

    public BookInventoryInfo[] getInitialInventory() {
        return initialInventory;
    }

    public Resources[] getInitialResources() {
        return initialResources;
    }

    public Services getServices() {
        return services;
    }

}
