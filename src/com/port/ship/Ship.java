package com.port.ship;

import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import com.port.port.Berth;
import com.port.port.Port;
import com.port.port.PortException;
import com.port.warehouse.Container;
import com.port.warehouse.Warehouse;


public class Ship implements Runnable {

	private final static Logger logger = Logger.getRootLogger();
	private volatile boolean stopThread = false;

	private String name;
	private Port port; // корабль содержит порт
	private Warehouse shipWarehouse;  // корабль содержит склад на корабле

	public Ship(String name, Port port, int shipWarehouseSize) {
		this.name = name;
		this.port = port;
		shipWarehouse = new Warehouse(shipWarehouseSize);
	}

	public void setContainersToWarehouse(List<Container> containerList) {
		shipWarehouse.addContainer(containerList);
	}

	public String getName() {
		return name;
	}
	// остановить поток
	public void stopThread() {
		stopThread = true;
	}

	public void run() {
		try {
			while (!stopThread) { // while(true)
				atSea(); // поток спит 1000 ms
				inPort();
			}
		} catch (InterruptedException e) {
			logger.error("С кораблем случилась неприятность и он уничтожен.", e);
		} catch (PortException e) {
			logger.error("С кораблем случилась неприятность и он уничтожен.", e);//!!! переписать сообщение
		}
	}

	private void atSea() throws InterruptedException {
		Thread.sleep(1000);
	}


	private void inPort() throws PortException, InterruptedException {

		boolean isLockedBerth = false; // заброкирован ли причал
		Berth berth = null;
		try {
			isLockedBerth = port.lockBerth(this); // связываем корабль с причалом
			
			if (isLockedBerth) { // если успешно пришвартовался
				berth = port.getBerth(this); // // получаем причал по кораблю из map
				logger.debug("Корабль " + name + " пришвартовался к причалу " + berth.getId());
				ShipAction action = getNextAction(); // рандомное получение значения enum
				executeAction(action, berth); // рандомное выполнение действия -
				             // или выгрузка с корабля, или погрузка на корабль
			} else {
				logger.debug("Кораблю " + name + " отказано в швартовке к причалу ");
			}
		} finally {
			if (isLockedBerth){ // если успешно пришвартовался
				port.unlockBerth(this); // отшвартовываемся
				logger.debug("Корабль " + name + " отошел от причала " + berth.getId());
			}
		}
		
	}

	private void executeAction(ShipAction action, Berth berth) throws InterruptedException {
		switch (action) {
		case LOAD_TO_PORT:
 				loadToPort(berth);
			break;
		case LOAD_FROM_PORT:
				loadFromPort(berth);
			break;
		}
	}

	private boolean loadToPort(Berth berth) throws InterruptedException {

		int containersNumberToMove = conteinersCount(); // получаем рандомное кол-во контейнеров
		boolean result = false;

		logger.debug("Корабль " + name + " хочет загрузить " + containersNumberToMove
				+ " контейнеров на склад порта.");

		// добавить число контейнеров (containersNumberToMove) со склада корабля
		// (shipWarehouse) на склад порта. Метод причала.
		result = berth.add(shipWarehouse, containersNumberToMove);
		
		if (!result) { // если result не true
			logger.debug("Невозможно осуществить операцию выгрузки/погрузки кораблем "
					+ name + " " + containersNumberToMove + " контейнеров.");
		} else {
			logger.debug("Корабль " + name + " выгрузил " + containersNumberToMove
					+ " контейнеров в порт.");
			
		}
		return result;
	}

	private boolean loadFromPort(Berth berth) throws InterruptedException {
		
		int containersNumberToMove = conteinersCount();
		
		boolean result = false;

		logger.debug("Корабль " + name + " хочет загрузить " + containersNumberToMove
				+ " контейнеров со склада порта.");
		
		result = berth.get(shipWarehouse, containersNumberToMove);
		
		if (result) { // если result - true
			logger.debug("Корабль " + name + " загрузил " + containersNumberToMove
					+ " контейнеров из порта.");
		} else {
			logger.debug("Недостаточно места на на корабле " + name
					+ " для погрузки " + containersNumberToMove + " контейнеров из порта.");
		}
		
		return result;
	}

	private int conteinersCount() {
		Random random = new Random();
		return random.nextInt(20) + 1;
	}

	// рандомное получение значения enum
	private ShipAction getNextAction() {
		Random random = new Random();
		int value = random.nextInt(4000);
		if (value < 1000) {
			return ShipAction.LOAD_TO_PORT;
		} else if (value < 2000) {
			return ShipAction.LOAD_FROM_PORT;
		}
		return ShipAction.LOAD_TO_PORT;
	}

	// вложенный класс перечисления
	enum ShipAction {
		LOAD_TO_PORT, LOAD_FROM_PORT
	}
}
