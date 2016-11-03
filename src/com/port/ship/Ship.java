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
	private Port port; // ������� �������� ����
	private Warehouse shipWarehouse;  // ������� �������� ����� �� �������

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
	// ���������� �����
	public void stopThread() {
		stopThread = true;
	}

	public void run() {
		try {
			while (!stopThread) { // while(true)
				atSea(); // ����� ���� 1000 ms
				inPort();
			}
		} catch (InterruptedException e) {
			logger.error("� �������� ��������� ������������ � �� ���������.", e);
		} catch (PortException e) {
			logger.error("� �������� ��������� ������������ � �� ���������.", e);//!!! ���������� ���������
		}
	}

	private void atSea() throws InterruptedException {
		Thread.sleep(1000);
	}


	private void inPort() throws PortException, InterruptedException {

		boolean isLockedBerth = false; // ������������ �� ������
		Berth berth = null;
		try {
			isLockedBerth = port.lockBerth(this); // ��������� ������� � ��������
			
			if (isLockedBerth) { // ���� ������� ��������������
				berth = port.getBerth(this); // // �������� ������ �� ������� �� map
				logger.debug("������� " + name + " �������������� � ������� " + berth.getId());
				ShipAction action = getNextAction(); // ��������� ��������� �������� enum
				executeAction(action, berth); // ��������� ���������� �������� -
				             // ��� �������� � �������, ��� �������� �� �������
			} else {
				logger.debug("������� " + name + " �������� � ��������� � ������� ");
			}
		} finally {
			if (isLockedBerth){ // ���� ������� ��������������
				port.unlockBerth(this); // ����������������
				logger.debug("������� " + name + " ������ �� ������� " + berth.getId());
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

		int containersNumberToMove = conteinersCount(); // �������� ��������� ���-�� �����������
		boolean result = false;

		logger.debug("������� " + name + " ����� ��������� " + containersNumberToMove
				+ " ����������� �� ����� �����.");

		// �������� ����� ����������� (containersNumberToMove) �� ������ �������
		// (shipWarehouse) �� ����� �����. ����� �������.
		result = berth.add(shipWarehouse, containersNumberToMove);
		
		if (!result) { // ���� result �� true
			logger.debug("���������� ����������� �������� ��������/�������� �������� "
					+ name + " " + containersNumberToMove + " �����������.");
		} else {
			logger.debug("������� " + name + " �������� " + containersNumberToMove
					+ " ����������� � ����.");
			
		}
		return result;
	}

	private boolean loadFromPort(Berth berth) throws InterruptedException {
		
		int containersNumberToMove = conteinersCount();
		
		boolean result = false;

		logger.debug("������� " + name + " ����� ��������� " + containersNumberToMove
				+ " ����������� �� ������ �����.");
		
		result = berth.get(shipWarehouse, containersNumberToMove);
		
		if (result) { // ���� result - true
			logger.debug("������� " + name + " �������� " + containersNumberToMove
					+ " ����������� �� �����.");
		} else {
			logger.debug("������������ ����� �� �� ������� " + name
					+ " ��� �������� " + containersNumberToMove + " ����������� �� �����.");
		}
		
		return result;
	}

	private int conteinersCount() {
		Random random = new Random();
		return random.nextInt(20) + 1;
	}

	// ��������� ��������� �������� enum
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

	// ��������� ����� ������������
	enum ShipAction {
		LOAD_TO_PORT, LOAD_FROM_PORT
	}
}
