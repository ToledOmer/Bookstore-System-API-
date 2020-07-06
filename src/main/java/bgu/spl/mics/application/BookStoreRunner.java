package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner {
    public static void main(String[] args) {
        Gson gson = new Gson();
        try {
            Path path = Paths.get(args[0]);
            FileReader reader = new FileReader(path.toAbsolutePath().toString());
            BookStoreInfo bookStore = gson.fromJson(reader, BookStoreInfo.class);
            //init inventory
            Inventory.getInstance().load(bookStore.getInitialInventory());

            //init resources
            Resources[] resources = bookStore.getInitialResources();
            for (int i = 0; i < resources.length; i++) {
                ResourcesHolder.getInstance().load(resources[i].getVehicles());
            }
            //init services
            Services services = bookStore.getServices();
            int latchSum = services.getInventoryService() + services.getResourcesService() + services.getSelling()
                    + services.getLogistics() + services.getCustomers().length;

            //latch keep all the threades init before timeservice thread
            CountDownLatch latch = new CountDownLatch(latchSum);

            LinkedList<Thread> threads = new LinkedList<Thread>();
            //selling service
            for (int i = 0; i < services.getSelling(); i++) {
                Thread t = new Thread(new SellingService(latch, "sellingService" + i));
                threads.add(t);
            }
            //inventoryService
            for (int i = 0; i < services.getInventoryService(); i++) {
                Thread t = new Thread(new InventoryService(latch, "inventoryService" + i));
                threads.add(t);
            }
            //logistics service
            for (int i = 0; i < services.getLogistics(); i++) {
                Thread t = new Thread(new LogisticsService(latch, "logisticService" + i,services.getTime().getSpeed()));
                threads.add(t);
            }
            //resourcesService
            for (int i = 0; i < services.getResourcesService(); i++) {
                Thread t = new Thread(new ResourceService(latch, "resourcesService" + i));
                threads.add(t);
            }
            //ApiService
            for (Customer customer : services.getCustomers()) {
                Thread t = new Thread(new APIService(customer, latch, "apiService - " + customer.getName()));
                threads.add(t);
            }
            for (Thread t : threads) {
                t.start();
            }

            // wait until latch counted down to 0, then the timeService can start running
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //time service
            Thread timeThread = new Thread(new TimeService(services.getTime().getSpeed(), services.getTime().getDuration()), "timeService");
            timeThread.start();

            //wait to next thread to be terminated before continue the main program
            for (Thread t : threads) {
                t.join();
            }
            timeThread.join();


            //generate the customers array into Hashmap of customerId + Customer
            HashMap<Integer, Customer> customersMap = getMapOfCustomers(services.getCustomers());
            //serialize the customers Map
            toOutput(customersMap, args[1]);
            Inventory.getInstance().printInventoryToFile(args[2]);
            //serialize MoneyRegister
            MoneyRegister.getInstance().printOrderReceipts(args[3]);
            toOutput(MoneyRegister.getInstance(), args[4]);


        } catch (FileNotFoundException e) {
        } catch (InterruptedException e) {
        }
    }

    private static HashMap<Integer, Customer> getMapOfCustomers(Customer[] costomerArry) {
        HashMap<Integer, Customer> hashmap = new HashMap<>();
        for (Customer customer : costomerArry) {
            if (!hashmap.containsKey(customer.getId())) {
                hashmap.put(customer.getId(), customer);
            }

        }
        return hashmap;
    }

    private static void toOutput(Object obj, String Filename) {
        try {
            FileOutputStream fos = new FileOutputStream(Filename);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(obj);
            oos.close();
            fos.close();
        } catch (IOException ex) {
        }
    }
}



