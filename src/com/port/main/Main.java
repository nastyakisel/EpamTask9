package com.port.main;

import java.util.ArrayList;
import java.util.List;

import com.port.port.Port;
import com.port.ship.Ship;
import com.port.warehouse.Container;

public class Main {

	public static void main(String[] args) throws InterruptedException {
		int warehousePortSize = 15; // ������ ������ �����
		/* ������� ���� ����������� �������� � 15 ��. - ���������� ��� ������
		 * � ������� ����������, ��������� ��� ������������ � id �� 0 �� 14
		 */
		List<Container> containerList = new ArrayList<Container>(warehousePortSize);
		for (int i=0; i<warehousePortSize; i++){
			containerList.add(new Container(i));
		}
		
		// ������� ���� - � ����� ��������� � � ���������� ����������� �� 90 ����.
		Port port = new Port(2, 90);
		// ��������� ���� 15 ����������� �� ����� �����
		port.setContainersToWarehouse(containerList);
		
		/* ������� ����� ���� ����������� �� 15 ���� - � id �� 30 �� 45
		 * ��������� ��� ������� 1
		 */
		containerList = new ArrayList<Container>(warehousePortSize);
		for (int i=0; i<warehousePortSize; i++){
			containerList.add(new Container(i+30));
		}
		
		Ship ship1 = new Ship("Ship0", port, 90);
		ship1.setContainersToWarehouse(containerList);
		
		/* ������� ����� ���� ����������� �� 15 ���� - � id �� 60 �� 75
		 * ��������� ��� ������� 2
		 */
		containerList = new ArrayList<Container>(warehousePortSize);
		for (int i=0; i<warehousePortSize; i++){
			containerList.add(new Container(i+60));
		}
		Ship ship2 = new Ship("Ship1", port, 90);
		ship2.setContainersToWarehouse(containerList);
		
		
		/* ������� ����� ���� ����������� �� 15 ���� - � id �� 60 �� 75
		 * ��������� ��� ������� 2
		 */
		containerList = new ArrayList<Container>(warehousePortSize);
		for (int i=0; i<warehousePortSize; i++){
			containerList.add(new Container(i+60));
		}
		Ship ship3 = new Ship("Ship2", port, 90);
		ship3.setContainersToWarehouse(containerList);		
		
		new Thread(ship1).start();		
		new Thread(ship2).start();		
		new Thread(ship3).start();
		

		Thread.sleep(3000);
		
		ship1.stopThread();
		ship2.stopThread();
		ship3.stopThread();

	}

}

