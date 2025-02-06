/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package loadpso;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;

public class PSOChart {
    private DefaultCategoryDataset dataset;
    private JFreeChart chart;
    private ChartPanel chartPanel;

    public PSOChart() {
        dataset = new DefaultCategoryDataset();
        chart = ChartFactory.createBarChart(
                "PSO Metrics Comparison",  
                "Metric",                 
                "Value",                 
                dataset,             
                PlotOrientation.VERTICAL, 
                true,                    
                true,                  
                false                  
        );
        chartPanel = new ChartPanel(chart);
    }

    public JPanel getChartPanel() {
        return chartPanel;
    }

    public void updateChart(double execTimeWithoutPSO, double execTimeWithPSO, double energyWithoutPSO, double energyWithPSO) {
        dataset.setValue(execTimeWithoutPSO, "With traditional algorithms", "Execution Time (ms)");
        dataset.setValue(execTimeWithPSO, "With PSO", "Execution Time (ms)");
        dataset.setValue(energyWithoutPSO, "With traditional algorithms", "Energy Consumption (kWh)");
        dataset.setValue(energyWithPSO, "With PSO", "Energy Consumption (kWh)");
    }
}
