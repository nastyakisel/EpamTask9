package com.port.port;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

import com.port.ship.Ship;
import com.port.warehouse.Container;
import com.port.warehouse.Warehouse;



public class Port {
private final static Logger logger = Logger.getRootLogger();
	
	private BlockingQueue<Berth> berthList; // очередь причалов
	private Warehouse portWarehouse; // хранилище порта
	
	private Map<Ship, Berth> usedBerths; // какой корабль у какого причала стоит

	public Port(int berthSize, int warehouseSize) {
		portWarehouse = new Warehouse(warehouseSize); // создаем пустое хранилище
		berthList = new ArrayBlockingQueue<Berth>(berthSize); // создаем очередь причалов
		for (int i = 0; i < berthSize; i++) { // заполн€ем очередь причалов непосредственно самими причалами
			berthList.add(new Berth(i, portWarehouse));
		}
		usedBerths = new HashMap<Ship, Berth>(); // создаем объект, который будет
		// хранить св€зь между кораблем и причалом
		logger.debug("ѕорт создан.");
	}
	
	// ƒобавл€ем лист контейнеров а аррайлист контейнеров склада
	public void setContainersToWarehouse(List<Container> containerList){
		portWarehouse.addContainer(containerList);
	}

	
	/* св€зываем корабль с причалом - один корабль - один причал
	 * швартуем корабль
	 */
	public boolean lockBerth(Ship ship) {
		Berth berth;
		try {
			berth = berthList.take(); // получаем причал из очереди-аррайлиста
			                  // в очереди - на один причал меньше
			usedBerths.put(ship, berth); // и св€зываем данный корабль с причалом
		} catch (InterruptedException e) {
			logger.debug(" ораблю " + ship.getName() + " отказано в швартовке.");
			return false;
		}		
		return true;
	}
	
	// удал€ем корабль от причала
	public boolean unlockBerth(Ship ship) {
		Berth berth = usedBerths.get(ship); // получаем причал по кораблю из map
		
		try {
			berthList.put(berth); // кладем причал обратно в очередь
			usedBerths.remove(ship); // удал€ем корабль-причал их map
		} catch (InterruptedException e) {
			logger.debug(" орабль " + ship.getName() + " не смог отшвартоватьс€.");
			return false;
		}		
		return true;
	}
	
	public Berth getBerth(Ship ship) throws PortException {
		
		Berth berth = usedBerths.get(ship); // получаем причал по кораблю из map
		if (berth == null){ // если в map нет такого объекта
			throw new PortException("Try to use Berth without blocking.");
		}
		return berth;		
	}
}
