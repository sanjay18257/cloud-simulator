/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package loadpso;

import java.util.List;

public class RandomForestModel {
    private List<double[]> trainingData;
    private List<Double> trainingLabels;
    private double[] featureWeights;
    private final int NUM_TREES = 10;
    private final double LEARNING_RATE = 0.1;

    public void train(List<double[]> trainingData, List<Double> labels) {
        this.trainingData = trainingData;
        this.trainingLabels = labels;
        this.featureWeights = optimizeFeatureWeights();
        
        System.out.println("Model trained with " + trainingData.size() + " samples");
    }

    private double[] optimizeFeatureWeights() {
    double[] weights = new double[7];
    
    weights[0] = 0.8;   
    weights[1] = 2.0; 
    weights[2] = 1.5;   
    weights[3] = 1.2;  
    weights[4] = 0.9;  
    weights[5] = 1.8;   
    weights[6] = 1.6;  
    
  
    for (int i = 0; i < NUM_TREES * 2; i++) {
        adjustWeights(weights);
    }
    
    return weights;
}
    private void adjustWeights(double[] weights) {
        for (int i = 0; i < trainingData.size(); i++) {
            double predicted = predictWithWeights(trainingData.get(i), weights);
            double actual = trainingLabels.get(i);
            double error = actual - predicted;
            
            // Update weights based on error
            for (int j = 0; j < weights.length; j++) {
                weights[j] += LEARNING_RATE * error * trainingData.get(i)[j];
            }
        }
    }
    

    public double predict(double[] features) {
        double baseLoad = 100.0;
        double prediction = baseLoad;
        
       
        double weightedSum = 0;
        for (int i = 0; i < features.length; i++) {
            weightedSum += features[i] * featureWeights[i];
        }
        
       
        prediction *= (1 + Math.tanh(weightedSum));
        
       
        if (features[6] > 0) { 
            prediction *= 1.8;
        }
        if (features[1] > 0) { 
            prediction *= 1.5;
        }
        if (features[2] > 0) { 
            prediction *= 1.3;
        }
        
        
        prediction *= (1 + features[3] * features[5]);
        
        return prediction;
    }

    private double predictWithWeights(double[] features, double[] weights) {
        double prediction = 0;
        for (int i = 0; i < features.length; i++) {
            prediction += features[i] * weights[i];
        }
        return prediction;
    }
}