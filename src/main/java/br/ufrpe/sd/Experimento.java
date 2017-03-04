package br.ufrpe.sd;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

/**
 * Um exemplo simples mostrando como criar um datacenter com um host e executar uma CloudLet nele.
 */
public class Experimento {
	
	private static List<Cloudlet> cloudletList;
	private static List<Vm> vmlist;
	private static int escolha = 1;
	private static boolean sair = false;
	
	/*
	 * O método main da classe
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		
		showHello();
		showMenu();
		
	}
	
	private static void simulacao(){
		Log.printLine("Iniciando o Experimento ...");
		try {
			/*
			 * 1 - Primeiro passo: Inicializar o pacote CloudSim. Deve ser invocado antes de criar as entidades da nuvem.
			 */
			int num_user = 1; // número de usuários da nuvem
			Calendar calendar = Calendar.getInstance(); // Calendar possui os campos de data e hora atual.
 			boolean trace_flag = false; // trace de eventos
 			
 			/*
 			 * Inicio da utilização do CloudSim
 			 * O método init inicializa as coleções Java utilizada pelo Framework
 			 */
 			CloudSim.init(num_user, calendar, trace_flag);
 			
 			/*
 			 * 2 - Segundo passo: Criar os dataCenters
 			 * é preciso haver pelo menos um datacenter para usar o CloudSim
 			 */
 			Datacenter datacenter0 = createDatacenter("Datacenter_0");
 			
 			/*
 			 * 3 - Terceiro passo: Criar o Broker
 			 */
 			DatacenterBroker broker = createBroker();
 			int brokerId = broker.getId();
 			
 			/*
 			 * 4 - Criar a(s) máquina(s) virtual(ais)
 			 */
 			vmlist = createVMs(brokerId);
			// submit vm list to the broker
			broker.submitVmList(vmlist);
			
			/*
			 * 5 - Quinto passo: Criar a(s) Cloudlet(s)
			 */
			switch (escolha) {
			case 1:
				cloudletList = createCloudlets1(brokerId, vmlist );
				break;
			case 2:
				cloudletList = createCloudlets2(brokerId, vmlist );
				break;
			case 3:
				cloudletList = createCloudlets3(brokerId, vmlist );
				break;
			case 4:
				cloudletList = createCloudlets4(brokerId, vmlist );
				break;
			default:
				cloudletList = createCloudlets1(brokerId, vmlist );
				break;
			}
			
			// submit cloudlet list to the broker
			broker.submitCloudletList(cloudletList);
			
			/*
			 * 6 - Sexto passo: Executa a simulação
			 */
			CloudSim.startSimulation();

			CloudSim.stopSimulation();
			
			/*
			 * 7 - Setimo passo (passo Final): Exibe os resultados quando a simulação terminar
			 */
			List<Cloudlet> newList = broker.getCloudletReceivedList();
			printCloudletList(newList);
			
			// Caso seja preciso saber o custo médio de execução
			//Log.printLine("Custo total da execução: $ "+ costPerHour(newList) );

			Log.printLine("Experimento "+escolha+" finalizado!\n======\n");
			
		} catch (Exception e) {
			e.printStackTrace();
			Log.printLine("Erros indesejados aconteceram");
		}
	}
	
	/*
	 * *******************
	 * Métodos auxiliares
	 * *******************
	 */
	/**
	 * Cria o datacenter.
	 *
	 * @param name the name
	 *
	 * @return the datacenter
	 */
	private static Datacenter createDatacenter(String name) {
		// Aqui estão os passos para a criação de um Datacenter:
		// 1. Precisamos criar uma lista que armazene as máquinas (hosts)
		List<Host> hostList = new ArrayList<Host>();

		// 2. Uma máquina contem uma ou mais PEs (Process Entity) ou CPU/cores.
		List<Pe> peList = new ArrayList<Pe>();
		//int mips = 1000;
		int mips = 30000; // 113,093 MIPS at 3.2 GHz | Intel Core i7 3630QM - Com 4 cores (4*28274 ~= 113,093)

		// 3. Cria as PEs e adiciona na lista.
		// 20 núcleos é o necessário para comportar com folga todas as instancias T2 
		for (int i=0; i<21; i++){
			peList.add(new Pe(i, new PeProvisionerSimple(mips))); // precisamos guardar os ids das PEs e MIPs.
		}
		
		// 4. Criar o Host (máquina física) com o próprio id e lista de PEs e adiciona eles na lista.
		// of machines
		int hostId = 0;
		//int ram = 2048; // host memory (MB)
		int ram = 68 * 1024; // host memory (MB)
		long storage = 1000000; // host storage
		int bw = 10000;
		
		hostList.add(
			new Host(
				hostId,
				new RamProvisionerSimple(ram),
				new BwProvisionerSimple(bw),
				storage,
				peList,
				new VmSchedulerTimeShared(peList)
			)
		); // This is our machine
		
		// 5. Criar o objeto DatacenterCharacteristics
		String arch = "x86"; // system architecture
		String os = "Linux"; // operating system
		String vmm = "Xen";
		double time_zone = -3.0; // time zone this resource located
		double cost = 3.0; // the cost of using processing in this resource
		double costPerMem = 0.05; // the cost of using memory in this resource
		double costPerStorage = 0.001; // the cost of using storage in this
		
		double costPerBw = 0.0; // the cost of using bw in this resource
		LinkedList<Storage> storageList = new LinkedList<Storage>(); // we are not adding SAN
													// devices by now
		DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
				arch, os, vmm, hostList, time_zone, cost, costPerMem,
				costPerStorage, costPerBw);
	
		// 6. Finalmente, precisamos criar o objeto Datacenter.
		Datacenter datacenter = null;
		try {
			datacenter = new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), storageList, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return datacenter;
	}
	
	/**
	 * Creates the broker.
	 *
	 * @return the datacenter broker
	 */
	private static DatacenterBroker createBroker() {
		DatacenterBroker broker = null;
		try {
			broker = new DatacenterBroker("Broker");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return broker;
	}
	
	/**
	 * Prints the Cloudlet objects.
	 *
	 * @param list list of Cloudlets
	 */
	private static void printCloudletList(List<Cloudlet> list) {
		int size = list.size();
		Cloudlet cloudlet;

		String indent = "    ";
		Log.printLine();
		Log.printLine("========== OUTPUT ==========");
		Log.printLine("Cloudlet ID" + indent + "STATUS" + indent
				+ "Data center ID" + indent + "VM ID" + indent + "Time" + indent
				+ "Start Time" + indent + "Finish Time");

		DecimalFormat dft = new DecimalFormat("###.##");
		for (int i = 0; i < size; i++) {
			cloudlet = list.get(i);
			Log.print(indent + cloudlet.getCloudletId() + indent + indent);

			if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
				Log.print("SUCCESS");

				Log.printLine(indent + indent + cloudlet.getResourceId()
						+ indent + indent + indent + cloudlet.getVmId()
						+ indent + indent
						+ dft.format(cloudlet.getActualCPUTime()) + indent
						+ indent + dft.format(cloudlet.getExecStartTime())
						+ indent + indent
						+ dft.format(cloudlet.getFinishTime()));
			}
		}
	}
	
	/*
	 * Para saber o custo do experimento em um ambiente real
	 * @param list lista de cloudlets
	 * 
	 * @param vms  Número de VMs que executam as cloudlets
	 * 
	 * @return double
	 * 
	 */
	private static double costPerHour(List<Cloudlet> list, int vms) {
		double costTotal = 0;
		int size = list.size();
		Cloudlet cloudlet;
		double costperHour = 0.085;
		int vmId[];
		vmId = new int[vms];
		
		for (int j=0; j<vms; j++){
			vmId[j] = 0;
		}
		
		for (int i = 0; i < size; i++){
			cloudlet = list.get(i);
			if (vmId[cloudlet.getVmId()]==0){
				vmId[cloudlet.getVmId()] = 1;
				double tempoinicial = cloudlet.getExecStartTime();
				double tempofinal   = cloudlet.getFinishTime();
				double tempo = tempofinal - tempoinicial;
				double horas = tempo / 3600000; // milissegundo para hora
				
				if (horas < 1 ){
					costTotal = costTotal + costperHour; 
				} else if ( (1 < horas) && (horas < 2) ){
					costTotal = costTotal + 2*costperHour; 
				} else if ( (2 < horas) && (horas < 3) ){
					costTotal = costTotal + 3*costperHour;
				} else {
					costTotal = costTotal + horas*costperHour;
				}
			}
		} // Fim do for i
		
		return costTotal;
	}
	
	/*
	 * Método alternativo
	 */
	private static double costPerHour(List<Cloudlet> list){
		int vms = list.size();
		return costPerHour(list, vms);
	}
	
	
	/*
	 * Métodos para criação das instancias T2
	 */
	private static List<Vm> createVMs(int brokerId){
		List<Vm> vmlist = new ArrayList<Vm>();
		int total = 7;
		for (int i=0; i<total; i++){
			vmlist.add( createT2(i, brokerId, i) );
		}
		return vmlist;
	}
	
	private static Vm createT2 (int id, int brokerId, int tipo){
		// VM T2 Instance
		int vmid = id;
		int mips = 28274;
		long size = 10000; // image size (MB)
		long bw = 1000;
		int ram = 512; // vm memory (MB)
		int pesNumber = 1; // number of cpus
		String vmm = "Xen"; // VMM name
		
		switch (tipo){
			case 1: // micro
				ram = 1024;
				pesNumber = 1;
				break;
			case 2: // small
				ram = 2048;
				pesNumber = 1;
				break;
			case 3: // medium
				ram = 4096;
				pesNumber = 2;
				break;
			case 4: // large
				ram = 8192;
				pesNumber = 2;
				break;
			case 5: // xlarge
				ram = 16384;
				pesNumber = 4;
				break;
			case 6: // 2xlarge
				ram = 32768;
				pesNumber = 8;
				break;
			default: // Nano
				ram = 512;
				pesNumber = 1;
		}
		
		// create VM
		return new Vm(vmid, brokerId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared());
	}
	
	/*
	 * Métodos para criação das cloudlets
	 */
	private static List<Cloudlet> createCloudlets1(int brokerId, List<Vm> vmlist){
		List<Cloudlet> list = new ArrayList<Cloudlet>();
		int total = vmlist.size();
		
		for (int i=0; i<total; i++){
				list.add( createCloudlet(brokerId, i) );
		}
		
		return list;
	}
	
	private static List<Cloudlet> createCloudlets2(int brokerId, List<Vm> vmlist){
		List<Cloudlet> list = new ArrayList<Cloudlet>();
		int total = vmlist.size();
		
		for (int i=0; i<total; i++){
			for (int j=0;j<total; j++) list.add( createCloudlet(brokerId, i, j) );
		}
		
		return list;
	}
	
	private static List<Cloudlet> createCloudlets3(int brokerId, List<Vm> vmlist){
		List<Cloudlet> list = new ArrayList<Cloudlet>();
		int total = vmlist.size();
		int totalJ = total * 2;
		
		for (int i=0; i<total; i++){
			for (int j=0;j<totalJ; j++) list.add( createCloudlet(brokerId, i, j) );
		}
		
		return list;
	}
	
	private static List<Cloudlet> createCloudlets4(int brokerId, List<Vm> vmlist){
		List<Cloudlet> list = new ArrayList<Cloudlet>();
		int total = vmlist.size();
		int cores = 1;
		
		for (int i=0; i<total; i++){
			switch (i){
			case 3: // medium
				cores = 2;
				break;
			case 4: // large
				cores = 2;
				break;
			case 5: // xlarge
				cores = 4;
				break;
			case 6: // 2xlarge
				cores = 8;
				break;
			default: // Nano, micro, small
				cores = 1;
			}
			
			for (int j=0;j<total; j++){
				list.add( createCloudlet(brokerId, i, j, cores) );
			}
		}
		
		return list;
	}
	
	private static Cloudlet createCloudlet(int brokerId, int vmId, int cloudletId, int cores){
		
		//Perfil da cloudlet
		long length = 900000000;
		long fileSize = 300;
		long outputSize = 300;
		int pesNumber = cores; // number of cpus
		UtilizationModel utilizationModel = new UtilizationModelFull();
		
		Cloudlet cloudlet = 
                new Cloudlet(cloudletId, length, pesNumber, fileSize, 
                        outputSize, utilizationModel, utilizationModel, 
                        utilizationModel);
		cloudlet.setUserId(brokerId);
	
		cloudlet.setVmId(vmId);
		
		return cloudlet;
	}
	
	private static Cloudlet createCloudlet(int brokerId, int vmId){
		int cloudletId = vmId;
		int cores = 1;
		return createCloudlet(brokerId, vmId, cloudletId, cores);
	}
	
	private static Cloudlet createCloudlet(int brokerId, int vmId, int cloudletId){
		int cores = 1;
		return createCloudlet(brokerId, vmId, cloudletId, cores);
	}
	
	private static void showHello(){
		String texto = "==========\n"
		 + "O seguinte experimento executa um conjunto de cloudlets (tarefas / aplicações) em um ambiente simulando as instâncias da Amazon (AWS EC2)\n"
		 + "São criadas 7 Instâncias do modelo T2 da AWS:\n"
		 + "- VmId 0 = t2.nano\n- VmId 1 = t2.micro\n- VmId 2 = t2.small\n"
		 + "- VmId 3 = t2.medium\n- VmId 4 = t2.large\n- VmId 5 = t2.xlarge\n"
		 + "- VmId 6 = t2.2xlarge";
		System.out.println(texto);
	}
	
	private static void showMenu(){
		String menu = "==========\n"
				+ "Escolha o experimento para ser executado: \n"
				+ "1 - Uma cloudlet para cada VM\n"
				+ "2 - 7 Cloudlets para cada VM\n"
				+ "3 - 14 Cloudlets para cada VM\n"
				+ "4 - 7 Cloudlets para cada VM, onde cada Cloudlet precisa do total de Cores da VM\n"
				+ "0 - Sair do programa\n"
				+ "Entre com a escolha [Default:1]";
		Scanner ler = new Scanner(System.in);
		while (!sair){
			System.out.println(menu);
			try {
				escolha = ler.nextInt();
				System.out.println("Escolha = "+escolha);
				switch (escolha) {
				case 0:
					sair = true;
					System.out.println("Encerrando o programa");
					System.exit(0);
					break;
				case 1:
					simulacao();
					break;
				case 2:
					simulacao();
					break;
				case 3:
					simulacao();
					break;
				case 4:
					simulacao();
					break;
				default:
					System.out.println("opcao nao encontrada");
					break;
				}
			} catch (Exception e) {
					//e.printStackTrace();
					System.out.println("Entrada em formato errado");
					escolha = 1;
			} finally {
				ler.nextLine();
			}

		}
	}
	
	
}
