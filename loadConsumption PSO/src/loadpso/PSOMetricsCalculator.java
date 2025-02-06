/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package loadpso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author sanja
 */
public class PSOMetricsCalculator {
    private double totalExecutionTimeWithoutPSO;
    private double totalExecutionTimeWithPSO;
    private double totalEnergyConsumptionWithoutPSO;
    private double totalEnergyConsumptionWithPSO;
    private List<String> allocationLogs;
    private PSOChart chart;

    
    public void setChart(PSOChart chart) {
    this.chart = chart;
}

public void updateChart(PSOChart chart) {
    chart.updateChart(
        totalExecutionTimeWithoutPSO,
        totalExecutionTimeWithPSO,
        totalEnergyConsumptionWithoutPSO,
        totalEnergyConsumptionWithPSO
    );
}
    public PSOMetricsCalculator() {
        this.totalExecutionTimeWithoutPSO = 0;
        this.totalExecutionTimeWithPSO = 0;
        this.totalEnergyConsumptionWithoutPSO = 0;
        this.totalEnergyConsumptionWithPSO = 0;
        this.allocationLogs = new ArrayList<>();
    }

    
  public void logWithoutPSO(int vmId, double execTime, double energy) {
    allocationLogs.add(String.format("VM %d allocated (Without PSO): Execution Time: %.2f ms, Energy Consumption: %.2f kWh", vmId, execTime, energy));
    totalExecutionTimeWithoutPSO += execTime;
    totalEnergyConsumptionWithoutPSO += energy;
    if (chart != null) updateChart(chart);
}

public void logWithPSO(int vmId, double execTime, double energy) {
    allocationLogs.add(String.format("VM %d allocated (With PSO): Execution Time: %.2f ms, Energy Consumption: %.2f kWh", vmId, execTime, energy));
    totalExecutionTimeWithPSO += execTime;
    totalEnergyConsumptionWithPSO += energy;
    if (chart != null) updateChart(chart);
}


  
    public void displayMetrics() {
        System.out.println("=== Metrics Summary ===");
        System.out.printf("Total Execution Time (Without PSO): %.2f ms%n", totalExecutionTimeWithoutPSO);
        System.out.printf("Total Energy Consumption (Without PSO): %.2f kWh%n", totalEnergyConsumptionWithoutPSO);
        System.out.printf("Total Execution Time (With PSO): %.2f ms%n", totalExecutionTimeWithPSO);
        System.out.printf("Total Energy Consumption (With PSO): %.2f kWh%n", totalEnergyConsumptionWithPSO);
        System.out.println("\n=== Allocation Logs ===");
        allocationLogs.forEach(System.out::println);
    }

  
    public void simulateWithoutPSO(List<Map<String, Double>> vmDetails) {
        for (int i = 0; i < vmDetails.size(); i++) {
            Map<String, Double> vm = vmDetails.get(i);
            int vmId = i + 1;
            double execTime = vm.getOrDefault("executionTime", 0.0); // Use provided or default values
            double energy = vm.getOrDefault("energy", 0.0);
            logWithoutPSO(vmId, execTime, energy);
        }
    }

   
    public void simulateWithPSO(List<Map<String, Double>> vmDetails) {
        for (int i = 0; i < vmDetails.size(); i++) {
            Map<String, Double> vm = vmDetails.get(i);
            int vmId = i + 1;
            double execTime = vm.getOrDefault("executionTime", 0.0) * 0.9; // Assume PSO reduces execution time by 10%
            double energy = vm.getOrDefault("energy", 0.0) * 0.85;         // Assume PSO reduces energy by 15%
            logWithPSO(vmId, execTime, energy);
        }
    }

}
