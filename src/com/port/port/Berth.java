package com.port.port;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import com.port.warehouse.Container;
import com.port.warehouse.Warehouse;

public class Berth { // причал

	private int id;
	private Warehouse portWarehouse; // содержит склад

	public Berth(int id, Warehouse warehouse) {
		this.id = id;
		portWarehouse = warehouse;
	}

	public int getId() {
		return id;
	}

	// ƒобавить( со склада корабл€, число контейнеров)
	// doMoveFromShip
	public boolean add(Warehouse shipWarehouse, int numberOfConteiners) throws InterruptedException {
		boolean result = false;
		Lock portWarehouseLock = portWarehouse.getLock();  // получаем блокатор склада
		boolean portLock = false;

		try{
			portLock = portWarehouseLock.tryLock(30, TimeUnit.SECONDS); // пытаемс€ вз€ть блокировку
			if (portLock) { // если вз€ли блокировку
				System.out.println(Thread.currentThread().getName() + " вз€л блокировку дл€ выгрузки на склад - метод add " + numberOfConteiners + "контейнеров");
				System.out.println("–азмер склада порта" + portWarehouse.getRealSize());
				System.out.println("–азмер склада корабл€" + Thread.currentThread().getName() + " " + shipWarehouse.getRealSize());
				int newConteinerCount = portWarehouse.getRealSize()	+ numberOfConteiners; // получаем новый размер
				       // склада с учетом новых добавленных контейнеров
				System.out.println("Ќовый размер склада, если выгрузим " + newConteinerCount);
				if (newConteinerCount <= portWarehouse.getFreeSize()) { // если новый размер - меньше
					         // числа свободного места дл€ контейнеров
					result = doMoveFromShip(shipWarehouse, numberOfConteiners);	// перегружаем контей
					     //неры с корабл€ на склад
				}
			}
		} finally{
			if (portLock) {
				portWarehouseLock.unlock();
				System.out.println(Thread.currentThread().getName() + "отдал блокировку дл€ выгрузки на склад - метод add");
			}
		}

		return result;
	}
	
	// перегружаем контейнеры с корабл€
	private boolean doMoveFromShip(Warehouse shipWarehouse, int numberOfConteiners) throws InterruptedException{
		Lock shipWarehouseLock = shipWarehouse.getLock(); // получаем блокатор
		boolean shipLock = false;
		
		try{
			shipLock = shipWarehouseLock.tryLock(30, TimeUnit.SECONDS);
			
			if (shipLock) {
				System.out.println(Thread.currentThread().getName() + " вз€л блокировку дл€ выгрузки на склад - метод doMoveFromShip " + numberOfConteiners + " контейнеров");
				System.out.println("–азмер склада порта " + portWarehouse.getRealSize());
				
				if(shipWarehouse.getRealSize() >= numberOfConteiners){ // если 
					              //размер склада корабл€ больше количества выгружаемых контенеров
					// создаем новый набор контейнеров
					List<Container> containers = shipWarehouse.getContainer(numberOfConteiners);
					portWarehouse.addContainer(containers);
					System.out.println("Ќовый размер склада порта " + portWarehouse.getRealSize() + "метод doMoveFromShip");
					System.out.println(Thread.currentThread().getName() + " выгрузил doMoveFromShip");
					System.out.println("–азмер склада корабл€ " + Thread.currentThread().getName() + " "+ shipWarehouse.getRealSize());
					return true;
				}
			}
		}finally{
			if (shipLock) {
				shipWarehouseLock.unlock();
				System.out.println(Thread.currentThread().getName() + "отдал блокировку дл€ выгрузки на склад - метод doMoveFromShip");
			}
		}
		
		return false;		
	}
	// doMoveFromPort, погрузка со склада на корабль
	public boolean get(Warehouse shipWarehouse, int numberOfConteiners) throws InterruptedException {
		boolean result = false;
		Lock portWarehouseLock = portWarehouse.getLock();	
		boolean portLock = false;

		try{
			portLock = portWarehouseLock.tryLock(30, TimeUnit.SECONDS);
			if (portLock) {
				System.out.println(Thread.currentThread().getName() + " вз€л блокировку дл€ выгрузки со склада - метод get " + numberOfConteiners + " контейнеров");
				System.out.println("–азмер склада корабл€ " + Thread.currentThread().getName() + " "+ shipWarehouse.getRealSize());
				if (numberOfConteiners <= portWarehouse.getRealSize()) {
					result = doMoveFromPort(shipWarehouse, numberOfConteiners);	
				}
			}
		} finally{
			if (portLock) {
				portWarehouseLock.unlock();
				System.out.println(Thread.currentThread().getName() + "отдал блокировку дл€ выгрузки со склада - метод get");
			}
		}

		return result;
	}
	
	private boolean doMoveFromPort(Warehouse shipWarehouse, int numberOfConteiners) throws InterruptedException{
		Lock shipWarehouseLock = shipWarehouse.getLock();
		boolean shipLock = false;
		
		try{
			shipLock = shipWarehouseLock.tryLock(30, TimeUnit.SECONDS);
			if (shipLock) {
				System.out.println(Thread.currentThread().getName() + " вз€л блокировку дл€ выгрузки со склада - метод doMoveFromPort " + numberOfConteiners + " контейнеров");
				int newConteinerCount = shipWarehouse.getRealSize() + numberOfConteiners;
				System.out.println("–азмер склада, который получитс€ после добавлени€ на корабль" + newConteinerCount);
				if(newConteinerCount <= shipWarehouse.getFreeSize()){
					List<Container> containers = portWarehouse.getContainer(numberOfConteiners);
					shipWarehouse.addContainer(containers);
					return true;
				}
			}
		}finally{
			if (shipLock) {
				shipWarehouseLock.unlock();
				System.out.println(Thread.currentThread().getName() + "отдал блокировку дл€ выгрузки со склада - метод doMoveFromPort");
			}
		}
		
		return false;		
	}
}

