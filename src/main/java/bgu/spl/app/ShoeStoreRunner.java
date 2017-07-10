package bgu.spl.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import com.google.gson.Gson;

import bgu.spl.app.active.ManagementService;
import bgu.spl.app.active.SellingService;
import bgu.spl.app.active.ShoeFactoryService;
import bgu.spl.app.active.TimeService;
import bgu.spl.app.active.WebsiteClientService;
import bgu.spl.app.passive.DiscountSchedule;
import bgu.spl.app.passive.PurchaseSchedule;
import bgu.spl.app.passive.ShoeStorageInfo;
import bgu.spl.app.passive.Store;

public class ShoeStoreRunner {


	public static void main(String[] args) throws IOException {			
		
		Gson gson = new Gson();
		int counter = 1;
		
		try {
			File read = new File(args[0]);
			BufferedReader bufferedReader = new BufferedReader(new FileReader(read));
			DataObject file = gson.fromJson(bufferedReader, DataObject.class);
			
			
			counter += file.services.customers.length;
			counter += file.services.sellers;
			counter += file.services.factories;
			
			CountDownLatch initiazlizationLatch = new CountDownLatch(counter);
			
			ShoeStorageInfo [] inventory = new ShoeStorageInfo[file.initialStorage.length];
			for(int i = 0; i < file.initialStorage.length; i++) {
				inventory [i] = new ShoeStorageInfo(file.initialStorage[i].shoeType, file.initialStorage[i].amount, 0); 
			}
			
			Store store = Store.getInstance();
			store.load(inventory);
			
			System.out.println("Store completed...");
			
			TimeService timer = new TimeService("TIMER", file.services.time.speed, file.services.time.duration, initiazlizationLatch);
			
			System.out.println("Timer completed...");
			
			WebsiteClientService[] customers = new WebsiteClientService[file.services.customers.length];
			
			for(int i = 0; i < file.services.customers.length; i++){
				ArrayList <PurchaseSchedule> purchaseSchedule = new ArrayList<>();
				Set <String> whishList = new HashSet <String>();
				for(int j = 0; j < file.services.customers[i].purchaseSchedule.length; j++){
					PurchaseSchedule temp = new PurchaseSchedule(file.services.customers[i].purchaseSchedule[j].shoeType, file.services.customers[i].purchaseSchedule[j].tick);
					purchaseSchedule.add(temp);
				}
				for(int k = 0; k < file.services.customers[i].getWishList().length; k++){
					String shoeType = file.services.customers[i].wishList[k];
					whishList.add(shoeType);
				}
				customers[i] = new WebsiteClientService(file.services.customers[i].name, purchaseSchedule, whishList, initiazlizationLatch);
			}
			
			System.out.println("All customers completed...");
			
			ArrayList<DiscountSchedule> discountSchedule = new ArrayList<>();
			for(int i= 0; i< file.services.manager.discountSchedule.length; i++){
				DiscountSchedule ds = new DiscountSchedule(file.services.manager.discountSchedule[i].getShoeType(), file.services.manager.discountSchedule[i].getTick(), file.services.manager.discountSchedule[i].getAmount());
				discountSchedule.add(ds);
			}
			ManagementService manager = new ManagementService("MANAGER", discountSchedule, initiazlizationLatch);
			
			System.out.println("Manager completed...");
			
			SellingService[] sellers = new SellingService[file.services.sellers];
			for(int i = 0; i < file.services.sellers; i++){
				String name = "Seller " + (i+1);
				sellers[i] = new SellingService(name, initiazlizationLatch);
			}
			
			System.out.println("sellers completed...");
			
			ShoeFactoryService[] factories = new ShoeFactoryService[file.services.factories];
			for(int i = 0; i< file.services.factories; i++){
				String name = "Factory " + (i+1);
				factories[i] = new ShoeFactoryService(name, initiazlizationLatch);
			}
			
			
			
			int threadCounter = 0;
			Thread [] threads = new Thread[counter +1];
			for(int i = 0 ; i < customers.length ; i ++){
				Thread temp = new Thread(customers[i]);
				threads[threadCounter] = temp;
				threadCounter++;

			}
			
			for(int i = 0 ; i < sellers.length ; i ++){
				Thread temp = new Thread(sellers[i]);
				threads[threadCounter] = temp;
				threadCounter++;

			}
			
			for(int i = 0 ; i < factories.length ; i ++){
				Thread temp = new Thread(factories[i]);
				threads[threadCounter] = temp;
				threadCounter++;

			}
			
			Thread temp = new Thread(manager);
			Thread temp2 = new Thread(timer);
			threads[threadCounter] = temp;
			threads[threadCounter+1] = temp2;
			
			for( int i = 0 ; i < threads.length; i++){
				threads[i].start();
			}
			
			
			for (int i=0; i < threads.length; i++){
				try {
					threads[i].join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			Store.getInstance().print();
		
		} catch (FileNotFoundException e) {
			System.out.println("File Not Found! Program terminating...");
			e.printStackTrace();
		}
	}
			

	public class DataObject {
		public initialStorage [] initialStorage;
		public services services;
		public initialStorage[] getInitialStorage() {
			return initialStorage;
		}
		public void setInitialStorage(initialStorage[] initialStorage) {
			this.initialStorage = initialStorage;
		}
		public services getServices() {
			return services;
		}
		public void setServices(services services) {
			this.services = services;
		}

	}
	
	public class initialStorage {

		public String shoeType;
		public int amount;
		
		public String getShoeType() {
			return shoeType;
		}
		
		public void setShoeType(String shoeType) {
			this.shoeType = shoeType;
		}
		public int getAmount() {
			return amount;
		}
		public void setAmount(int amount) {
			this.amount = amount;
		}

	}
	
	public class services {
		public 	time time;
		public manager manager;
		public int factories;
		public int sellers;
		public customers[] customers;
		public time getTime() {
			return time;
		}
		public void setTime(time time) {
			this.time = time;
		}
		public manager getManager() {
			return manager;
		}
		public void setManager(manager manager) {
			this.manager = manager;
		}
		public int getFactories() {
			return factories;
		}
		public void setFactories(int factories) {
			this.factories = factories;
		}
		public int getSellers() {
			return sellers;
		}
		public void setSellers(int sellers) {
			this.sellers = sellers;
		}
		public customers[] getCustomers() {
			return customers;
		}
		public void setCustomers(customers[] customers) {
			this.customers = customers;
		}

	}
	
	public class customers {
		public String name;
		public String [] wishList;
		public purchaseSchedule[] purchaseSchedule;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String[] getWishList() {
			return wishList;
		}
		public void setWishList(String[] wishList) {
			this.wishList = wishList;
		}
		public purchaseSchedule[] getPurchaseSchedule() {
			return purchaseSchedule;
		}
		public void setPurchaseSchedule(purchaseSchedule[] purchaseSchedule) {
			this.purchaseSchedule = purchaseSchedule;
		}


	}
	
	public class purchaseSchedule {
		public String shoeType;
		public int tick;
		public String getShoeType() {
			return shoeType;
		}
		public void setShoeType(String shoeType) {
			this.shoeType = shoeType;
		}
		public int getTick() {
			return tick;
		}
		public void setTick(int tick) {
			this.tick = tick;
		}

	}
	
	public class manager {
		public discountSchedule[] discountSchedule;

		public discountSchedule[] getDiscountSchedule() {
			return discountSchedule;
		}

		public void setDiscountSchedule(discountSchedule[] discountSchedule) {
			this.discountSchedule = discountSchedule;
		}

	}
	
	public class discountSchedule {
		public String shoeType;
		public int amount;	
		public int tick;
		public String getShoeType() {
			return shoeType;
		}
		public void setShoeType(String shoeType) {
			this.shoeType = shoeType;
		}
		public int getAmount() {
			return amount;
		}
		public void setAmount(int amount) {
			this.amount = amount;
		}
		public int getTick() {
			return tick;
		}
		public void setTick(int tick) {
			this.tick = tick;
		}

	}
	
	public class time {
		public int speed;	
		public int duration;
		public int getSpeed() {
			return speed;
		}
		public void setSpeed(int speed) {
			this.speed = speed;
		}
		public int getDuration() {
			return duration;
		}
		public void setDuration(int duration) {
			this.duration = duration;
		}

	}
}
