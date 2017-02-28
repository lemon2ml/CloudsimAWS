# CloudsimAWS
Projeto da Disciplina de Sistemas Distribuídos do curso de Bacharelado em Ciência da Computação - UFRPE 2016.2


#### Prerequisites
* JDK 7 or later
* Apache Maven 3.2 or later

Installation instructions for Java 7 and Maven can be found here:

[Java 7 Install] (http://docs.oracle.com/javase/7/docs/webnotes/install/)

[Apache Maven Install] (http://maven.apache.org/download.cgi#Installation)

You should also have installed [Eclipse IDE Java EE Developers](https://www.eclipse.org/downloads/) and SVN and GIT clients. If you’re unfamiliar with CloudSim itself, you can look at [these examples](http://www.cloudbus.org/cloudsim/examples.html).

#### Install (Setup)
```
git clone https://github.com/saulobr88/CloudsimAWS.git
cd CloudsimAWS
mvn clean install
```

The same step by step in here [Cloudsim and Cloudsimex](http://nikgrozev.com/2014/06/08/cloudsim-and-cloudsimex-part-1/)

#### About the project
Project of the Distributed Systems Discipline of the Bachelor's Degree in Computer Science - UFRPE 2016.2

It presents 4 simulation experiments using Cloudsim, 7 Instances of AWS (Amazon Web Services EC2) T2 model are created (Nano, Micro, Small, Medium, Large, XLarge, 2XLarge).

###### The experiments short description:

1. Each VM has 1 (one) Cloudlet to itself
2. Each VM has 7 (seven) Cloudlets to itself
3. Each VM has 14 (fourteen) Cloudlets to itself
4. Each VM has 7 (seven) Cloudlets to itself, but in this case the cloudlets uses total number of cores present in VM, using 100% of processing power of each VM

The Cloudlet profile is
- Total lengh: 900000000 (MIPS);
- Total MB in disk: 300;
- Total MB in memory: 300;

The Host (Physical Machine) in simulation is
- 21 Cores of processing
- RAM = 68 * 1024; // host memory (MB)
- Storage = 1000000; // host storage
- Bandwidth = 10000;
