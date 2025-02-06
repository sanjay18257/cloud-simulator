    /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package loadpso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;

/**
 *
 * @author sanja
 */
public class QNPSOMonitor {
    public static void printOptimizationResults(double[] optimizedAllocation, 
                                              List<Vm> vmList, 
                                              List<Host> hostList) {
        System.out.println("\n====== QNPSO Optimization Results ======");
        
        
        double avgUtilization = calculateAverageUtilization(optimizedAllocation, vmList, hostList);
        System.out.printf("Average Host Utilization: %.2f%%\n", avgUtilization * 100);
        
      
        double loadVariance = calculateLoadVariance(optimizedAllocation, hostList.size());
        System.out.printf("Load Distribution Variance: %.4f\n", loadVariance);
        
    
        double energyConsumption = calculateEnergyConsumption(optimizedAllocation, vmList, hostList);
        System.out.printf("Estimated Energy Consumption: %.2f kWh\n", energyConsumption);
        
       
        double slaCompliance = calculateSLACompliance(optimizedAllocation, vmList, hostList);
        System.out.printf("SLA Compliance Rate: %.2f%%\n", slaCompliance * 100);
        
        System.out.println("\n=== VM Allocation Map ===");
        printAllocationMap(optimizedAllocation, vmList, hostList);
    }
    
    private static void printAllocationMap(double[] allocation, List<Vm> vmList, List<Host> hostList) {
        Map<Integer, List<Integer>> hostToVMs = new HashMap<>();
        
        for (int i = 0; i < allocation.length; i++) {
            int hostIndex = (int)(allocation[i] * hostList.size());
            hostToVMs.computeIfAbsent(hostIndex, k -> new ArrayList<>()).add(i);
        }
        
        hostToVMs.forEach((hostId, vms) -> {
            System.out.printf("Host %d -> VMs: %s\n", hostId, vms.toString());
        });
    }
    private static double calculateAverageUtilization(double[] allocation, List<Vm> vmList, List<Host> hostList) {
    double[] hostUtilization = new double[hostList.size()];
    
    for (int i = 0; i < allocation.length; i++) {
        int hostIndex = (int)(allocation[i] * hostList.size());
        Vm vm = vmList.get(i);
        hostUtilization[hostIndex] += vm.getMips() / hostList.get(hostIndex).getTotalMips();
    }
    
    return Arrays.stream(hostUtilization).average().orElse(0.0);
}

private static double calculateLoadVariance(double[] allocation, int numHosts) {
    int[] hostLoad = new int[numHosts];
    for (double alloc : allocation) {
        hostLoad[(int)(alloc * numHosts)]++;
    }
    
    double mean = Arrays.stream(hostLoad).average().orElse(0.0);
    return Arrays.stream(hostLoad)
        .mapToDouble(load -> Math.pow(load - mean, 2))
        .average()
        .orElse(0.0);
}

private static double calculateEnergyConsumption(double[] allocation, List<Vm> vmList, List<Host> hostList) {
    double totalEnergy = 0.0;
    double[] hostUtilization = new double[hostList.size()];
    
  
    for (int i = 0; i < allocation.length; i++) {
        int hostIndex = (int)(allocation[i] * hostList.size());
        Vm vm = vmList.get(i);
        hostUtilization[hostIndex] += vm.getMips() / hostList.get(hostIndex).getTotalMips();
    }
    
   
    for (int i = 0; i < hostList.size(); i++) {
        double utilization = hostUtilization[i];
        
        double k = 0.7; 
        double pMax = 250.0; 
        double power = k * pMax + (1-k) * pMax * utilization;
        totalEnergy += power;
    }
    
    return totalEnergy / 1000.0; 
}

private static double calculateSLACompliance(double[] allocation, List<Vm> vmList, List<Host> hostList) {
    int compliantVMs = 0;
    
    for (int i = 0; i < allocation.length; i++) {
        int hostIndex = (int)(allocation[i] * hostList.size());
        Vm vm = vmList.get(i);
        Host host = hostList.get(hostIndex);
        
        
        if (host.getAvailableMips() >= vm.getMips() &&
            host.getRamProvisioner().getAvailableRam() >= vm.getRam() &&
            host.getBwProvisioner().getAvailableBw() >= vm.getBw()) {
            compliantVMs++;
        }
    }
    
    return (double) compliantVMs / vmList.size();
}


}
