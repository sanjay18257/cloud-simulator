/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package loadpso;

import org.cloudbus.cloudsim.CloudletScheduler;
import org.cloudbus.cloudsim.power.PowerVm;

/**
 *
 * @author sanja
 */
 class CustomPowerVm extends PowerVm {
    private int priority;

    public CustomPowerVm(int id, int userId, double mips, int numberOfPes, int ram, int bw, long size, 
            int cloudletSchedulerId, String vmm, CloudletScheduler cloudletScheduler, double schedulingInterval, 
            int priority) {
        super(id, userId, mips, numberOfPes, ram, bw, size, cloudletSchedulerId, vmm, cloudletScheduler, schedulingInterval);
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }
}
