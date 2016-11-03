package com.port.port;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import com.port.warehouse.Container;
import com.port.warehouse.Warehouse;

public class Berth { // ������

	private int id;
	private Warehouse portWarehouse; // �������� �����

	public Berth(int id, Warehouse warehouse) {
		this.id = id;
		portWarehouse = warehouse;
	}

	public int getId() {
		return id;
	}

	// ��������( �� ������ �������, ����� �����������)
	// doMoveFromShip
	public boolean add(Warehouse shipWarehouse, int numberOfConteiners) throws InterruptedException {
		boolean result = false;
		Lock portWarehouseLock = portWarehouse.getLock();  // �������� �������� ������
		boolean portLock = false;

		try{
			portLock = portWarehouseLock.tryLock(30, TimeUnit.SECONDS); // �������� ����� ����������
			if (portLock) { // ���� ����� ����������
				System.out.println(Thread.currentThread().getName() + " ���� ���������� ��� �������� �� ����� - ����� add " + numberOfConteiners + "�����������");
				System.out.println("������ ������ �����" + portWarehouse.getRealSize());
				System.out.println("������ ������ �������" + Thread.currentThread().getName() + " " + shipWarehouse.getRealSize());
				int newConteinerCount = portWarehouse.getRealSize()	+ numberOfConteiners; // �������� ����� ������
				       // ������ � ������ ����� ����������� �����������
				System.out.println("����� ������ ������, ���� �������� " + newConteinerCount);
				if (newConteinerCount <= portWarehouse.getFreeSize()) { // ���� ����� ������ - ������
					         // ����� ���������� ����� ��� �����������
					result = doMoveFromShip(shipWarehouse, numberOfConteiners);	// ����������� ������
					     //���� � ������� �� �����
				}
			}
		} finally{
			if (portLock) {
				portWarehouseLock.unlock();
				System.out.println(Thread.currentThread().getName() + "����� ���������� ��� �������� �� ����� - ����� add");
			}
		}

		return result;
	}
	
	// ����������� ���������� � �������
	private boolean doMoveFromShip(Warehouse shipWarehouse, int numberOfConteiners) throws InterruptedException{
		Lock shipWarehouseLock = shipWarehouse.getLock(); // �������� ��������
		boolean shipLock = false;
		
		try{
			shipLock = shipWarehouseLock.tryLock(30, TimeUnit.SECONDS);
			
			if (shipLock) {
				System.out.println(Thread.currentThread().getName() + " ���� ���������� ��� �������� �� ����� - ����� doMoveFromShip " + numberOfConteiners + " �����������");
				System.out.println("������ ������ ����� " + portWarehouse.getRealSize());
				
				if(shipWarehouse.getRealSize() >= numberOfConteiners){ // ���� 
					              //������ ������ ������� ������ ���������� ����������� ����������
					// ������� ����� ����� �����������
					List<Container> containers = shipWarehouse.getContainer(numberOfConteiners);
					portWarehouse.addContainer(containers);
					System.out.println("����� ������ ������ ����� " + portWarehouse.getRealSize() + "����� doMoveFromShip");
					System.out.println(Thread.currentThread().getName() + " �������� doMoveFromShip");
					System.out.println("������ ������ ������� " + Thread.currentThread().getName() + " "+ shipWarehouse.getRealSize());
					return true;
				}
			}
		}finally{
			if (shipLock) {
				shipWarehouseLock.unlock();
				System.out.println(Thread.currentThread().getName() + "����� ���������� ��� �������� �� ����� - ����� doMoveFromShip");
			}
		}
		
		return false;		
	}
	// doMoveFromPort, �������� �� ������ �� �������
	public boolean get(Warehouse shipWarehouse, int numberOfConteiners) throws InterruptedException {
		boolean result = false;
		Lock portWarehouseLock = portWarehouse.getLock();	
		boolean portLock = false;

		try{
			portLock = portWarehouseLock.tryLock(30, TimeUnit.SECONDS);
			if (portLock) {
				System.out.println(Thread.currentThread().getName() + " ���� ���������� ��� �������� �� ������ - ����� get " + numberOfConteiners + " �����������");
				System.out.println("������ ������ ������� " + Thread.currentThread().getName() + " "+ shipWarehouse.getRealSize());
				if (numberOfConteiners <= portWarehouse.getRealSize()) {
					result = doMoveFromPort(shipWarehouse, numberOfConteiners);	
				}
			}
		} finally{
			if (portLock) {
				portWarehouseLock.unlock();
				System.out.println(Thread.currentThread().getName() + "����� ���������� ��� �������� �� ������ - ����� get");
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
				System.out.println(Thread.currentThread().getName() + " ���� ���������� ��� �������� �� ������ - ����� doMoveFromPort " + numberOfConteiners + " �����������");
				int newConteinerCount = shipWarehouse.getRealSize() + numberOfConteiners;
				System.out.println("������ ������, ������� ��������� ����� ���������� �� �������" + newConteinerCount);
				if(newConteinerCount <= shipWarehouse.getFreeSize()){
					List<Container> containers = portWarehouse.getContainer(numberOfConteiners);
					shipWarehouse.addContainer(containers);
					return true;
				}
			}
		}finally{
			if (shipLock) {
				shipWarehouseLock.unlock();
				System.out.println(Thread.currentThread().getName() + "����� ���������� ��� �������� �� ������ - ����� doMoveFromPort");
			}
		}
		
		return false;		
	}
}

