/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package loadpso;

/**
 *
 * @author sanja
 */
import java.util.Random;

public class QNPSO {
    private final int numParticles;
    private final int dimensions;
    private final int maxIterations;
    
   
    private double[][] positions;
    private double[][] velocities;
    private double[][] personalBest;
    private double[] globalBest;
    
    
    private double[][] quantumStates;
    private double[][] quantumPhases;
    private double entanglementMatrix[][];
    
   
    private double[][] synapticWeights;
    private double[][] hebbianConnections;
    private double[] homeostaticScaling;
    
    
    private static final double QUANTUM_THETA = 0.8;
    private static final double HEBBIAN_RATE = 0.1;
    private static final double HOMEOSTATIC_RATE = 0.05;
    private static final double INERTIA_WEIGHT = 0.7;
    private static final double COGNITIVE_FACTOR = 1.5;
    private static final double SOCIAL_FACTOR = 1.5;
    
    private final Random random;

    public QNPSO(int numParticles, int dimensions, int maxIterations) {
        this.numParticles = numParticles;
        this.dimensions = dimensions;
        this.maxIterations = maxIterations;
        
        this.positions = new double[numParticles][dimensions];
        this.velocities = new double[numParticles][dimensions];
        this.personalBest = new double[numParticles][dimensions];
        this.globalBest = new double[dimensions];
        
        this.quantumStates = new double[numParticles][dimensions];
        this.quantumPhases = new double[numParticles][dimensions];
        this.entanglementMatrix = new double[numParticles][numParticles];
        
        this.synapticWeights = new double[numParticles][dimensions];
        this.hebbianConnections = new double[numParticles][numParticles];
        this.homeostaticScaling = new double[numParticles];
        
        this.random = new Random();
        
        initialize();
    }

    private void initialize() {
        // Initialize particles
        for (int i = 0; i < numParticles; i++) {
            for (int d = 0; d < dimensions; d++) {
                positions[i][d] = random.nextDouble();
                velocities[i][d] = random.nextDouble() * 0.1;
                personalBest[i][d] = positions[i][d];
                quantumStates[i][d] = random.nextDouble();
                quantumPhases[i][d] = random.nextDouble() * 2 * Math.PI;
                synapticWeights[i][d] = random.nextDouble();
            }
            homeostaticScaling[i] = 1.0;
        }
        
        
        for (int i = 0; i < numParticles; i++) {
            for (int j = 0; j < numParticles; j++) {
                entanglementMatrix[i][j] = i == j ? 1.0 : random.nextDouble() * 0.1;
                hebbianConnections[i][j] = random.nextDouble() * 0.1;
            }
        }
    }

    public double[] optimize(FitnessFunction fitnessFunction) {
        double bestFitness = Double.POSITIVE_INFINITY;
        
        for (int iteration = 0; iteration < maxIterations; iteration++) {
            
            updateQuantumStates();
            applyQuantumInterference();
            
           
            updateSynapticWeights();
            applyHebbianLearning();
            updateHomeostaticScaling();
        
            updateParticles(fitnessFunction);
            
           
            double iterationBest = updateBestSolutions(fitnessFunction);
            
            if (iterationBest < bestFitness) {
                bestFitness = iterationBest;
            }
        }
        
        return globalBest;
    }

    private void updateQuantumStates() {
        for (int i = 0; i < numParticles; i++) {
            for (int d = 0; d < dimensions; d++) {
                double phase = quantumPhases[i][d];
                quantumStates[i][d] = Math.cos(phase) * Math.exp(-Math.abs(positions[i][d] - globalBest[d]) / QUANTUM_THETA);
                quantumPhases[i][d] += random.nextDouble() * Math.PI;
            }
        }
    }

    private void applyQuantumInterference() {
        for (int i = 0; i < numParticles; i++) {
            for (int j = 0; j < numParticles; j++) {
                if (i != j) {
                    double interference = entanglementMatrix[i][j] * 
                        Math.cos(quantumPhases[i][0] - quantumPhases[j][0]);
                    positions[i][0] += interference * 0.1;
                }
            }
        }
    }

    private void updateSynapticWeights() {
        for (int i = 0; i < numParticles; i++) {
            for (int d = 0; d < dimensions; d++) {
                synapticWeights[i][d] *= (1 + HEBBIAN_RATE * 
                    (positions[i][d] - personalBest[i][d]));
                synapticWeights[i][d] = Math.max(0.0, Math.min(1.0, synapticWeights[i][d]));
            }
        }
    }

    private void applyHebbianLearning() {
        for (int i = 0; i < numParticles; i++) {
            for (int j = 0; j < numParticles; j++) {
                if (i != j) {
                    hebbianConnections[i][j] += HEBBIAN_RATE * 
                        (positions[i][0] * positions[j][0]);
                }
            }
        }
    }

    private void updateHomeostaticScaling() {
        for (int i = 0; i < numParticles; i++) {
            double activity = 0;
            for (int d = 0; d < dimensions; d++) {
                activity += Math.abs(positions[i][d] - personalBest[i][d]);
            }
            homeostaticScaling[i] += HOMEOSTATIC_RATE * (1.0 - activity);
            homeostaticScaling[i] = Math.max(0.5, Math.min(2.0, homeostaticScaling[i]));
        }
    }

    private void updateParticles(FitnessFunction fitnessFunction) {
        for (int i = 0; i < numParticles; i++) {
            for (int d = 0; d < dimensions; d++) {
               
                velocities[i][d] = INERTIA_WEIGHT * velocities[i][d] +
                    COGNITIVE_FACTOR * random.nextDouble() * (personalBest[i][d] - positions[i][d]) +
                    SOCIAL_FACTOR * random.nextDouble() * (globalBest[d] - positions[i][d]);
                
               
                velocities[i][d] *= quantumStates[i][d] * synapticWeights[i][d] * homeostaticScaling[i];
                
             
                positions[i][d] += velocities[i][d];
                positions[i][d] = Math.max(0.0, Math.min(1.0, positions[i][d]));
            }
        }
    }

    private double updateBestSolutions(FitnessFunction fitnessFunction) {
        double iterationBest = Double.POSITIVE_INFINITY;
        
        for (int i = 0; i < numParticles; i++) {
            double fitness = fitnessFunction.evaluate(positions[i]);
            
            if (fitness < fitnessFunction.evaluate(personalBest[i])) {
                System.arraycopy(positions[i], 0, personalBest[i], 0, dimensions);
            }
            
            if (fitness < iterationBest) {
                iterationBest = fitness;
                System.arraycopy(positions[i], 0, globalBest, 0, dimensions);
            }
        }
        
        return iterationBest;
    }
}