package com.port.warehouse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Warehouse {  // склад
	private List<Container> containerList; // содержит набор контейнеров
	private int size; 
	private Lock lock; // и блокатор

	// при создании объекта склада создаетс€ блокатор
	public Warehouse(int size) {
		containerList = new ArrayList<Container>(size);
		lock = new ReentrantLock();
		this.size = size;
	}

	public boolean addContainer(Container container) {	
		return containerList.add(container);
	}

	// добавл€ем контейнеры на склад
	// добавл€ем лист контейнеров в лист контейнеров
	public boolean addContainer(List<Container> containers) {
		boolean result = false;
		if(containerList.size() + containers.size() <= size){ // число контейнеров
			                 // не должно превышать емкости склада
			result = containerList.addAll(containers);
		}
		return result;
	}

	public Container getContainer() {
		if (containerList.size() > 0) {
			return containerList.remove(0);
		}
		return null;
	}

	// ѕолучает груз (контейнеры) со склада
	// ѕолучаем часть јррэйлиста набора контейнеров - от нулевого до amount
	// и возвращаем эту часть, и удал€ем из склада
	public List<Container> getContainer(int amount) {
		if (containerList.size() >= amount) { // если размер набора контейнеров,
			       // принадлежащих складу, больше amount
			// создаем новый набор контейнеров - груз
			List<Container> cargo = new ArrayList<Container>(containerList.subList(0, amount));
			containerList.removeAll(cargo); // удал€ем из containerList элементы,
			// которые содержатс€ в коллекции cargo
			return cargo;
		}
		return null;
	}
	
	public int getSize(){
		return size;
	}
	
	public int getRealSize(){
		return containerList.size();
	}
	
	public int getFreeSize(){
		return size - containerList.size();
	}
	
	public Lock getLock(){
		return lock;
	}	
}
