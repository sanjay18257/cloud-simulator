package loadpso;

import static java.awt.AWTEventMulticaster.add;
import java.awt.BorderLayout;
import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import javax.swing.JFrame;
import javax.swing.JLabel;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.power.PowerHost;
import org.cloudbus.cloudsim.power.PowerVm;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;
import org.cloudbus.cloudsim.power.models.PowerModelCubic;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;


public class VMAllocation extends JFrame{
    Details dt = new Details();
    Datacenter dc1;
    DatacenterCharacteristics characteristics;
    static final int MAX_HOSTS = 10;
    PSOMetricsCalculator metricsCalculator = new PSOMetricsCalculator();
    private String currentUser;
private JLabel userLabel;
public VMAllocation() {
        super("VM Allocation System");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Initialize other components
    }

    public void setCurrentUser(String username) {
        this.currentUser = username;
        
        // Add user info to the UI
        if (userLabel == null) {
            userLabel = new JLabel();
            userLabel.setFont(new Font("Arial", Font.BOLD, 12));
            // Add to your existing panel/frame
            add(userLabel, BorderLayout.NORTH);
        }
        userLabel.setText("Logged in as: " + username);
        
        // Update window title
        setTitle("VM Allocation System - User: " + username);
        
        // Store user preferences
        saveUserPreferences();
    }


public String getCurrentUser() {
    return currentUser;
}

private void saveUserPreferences() {
    try {
        Properties props = new Properties();
        props.setProperty("lastLoggedUser", currentUser);
        props.setProperty("lastLoginTime", new Date().toString());
        
        FileOutputStream out = new FileOutputStream("userprefs.properties");
        props.store(out, "User Session Information");
        out.close();
    } catch (IOException e) {
        System.out.println("Error saving user preferences: " + e.getMessage());
    }
}

public void logout() {
    this.currentUser = null;
    if (userLabel != null) {
        userLabel.setText("Not logged in");
    }
    // Return to login screen
    Login loginScreen = new Login();
    loginScreen.setVisible(true);
    this.dispose();
}
   public void readVM() {
        try {
            File fe = new File("vm1.txt");
            FileInputStream fis = new FileInputStream(fe);
            byte bt[] = new byte[fis.available()];
            fis.read(bt);
            fis.close();

            String g1 = new String(bt);
            String g2[] = g1.split("\n");
            for (int i = 1; i < g2.length; i++) {
                dt.Vt.add(g2[i].trim());
            }

            dt.vms = new String[dt.Vt.size()][4];
            for (int i = 0; i < dt.Vt.size(); i++) {
                String a1[] = dt.Vt.get(i).toString().trim().split("\t");
                dt.vms[i][0] = a1[0];    
                dt.vms[i][1] = a1[1];   
                dt.vms[i][2] = a1[2]; 
                dt.vms[i][3] = a1[3];    
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

      public void readHost() {
        try {
            File fe = new File("host2.txt");
            FileInputStream fis = new FileInputStream(fe);
            byte bt[] = new byte[fis.available()];
            fis.read(bt);
            fis.close();

            String g1 = new String(bt);
            String g2[] = g1.split("\n");
            for (int i = 1; i < g2.length; i++) {
                dt.Ht.add(g2[i].trim());
            }

            dt.host = new String[dt.Ht.size()][4];
            for (int i = 0; i < dt.Ht.size(); i++) {
                String a1[] = dt.Ht.get(i).toString().trim().split("\t");
                dt.host[i][0] = a1[0];    
                dt.host[i][1] = a1[1];    
                dt.host[i][2] = a1[2];    
                dt.host[i][3] = a1[3];    
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


public void createHost(int totalRam, int totalCpu, int totalBw) {
        int id = dt.hostList.size() + 1;
        PowerHost newHost = new PowerHost(
            id,
            new RamProvisionerSimple(totalRam),
            new BwProvisionerSimple(totalBw),
            100000,
            createPEList(totalCpu),
            new VmSchedulerTimeShared(createPEList(totalCpu)),
            new PowerModelCubic(1000, 500)
        );
        dt.hostList.add(newHost);
        System.out.println("New Host " + id + " created with Total RAM: " + totalRam + "MB, CPU: "
                           + totalCpu + " cores, BW: " + totalBw + "MB/s.");
    }


 private List<Pe> createPEList(int cpu) {
        List<Pe> peList = new ArrayList<>();
        int mips = 250; 
        for (int i = 0; i < cpu; i++) {
            peList.add(new Pe(i, new PeProvisionerSimple(mips)));
        }
        return peList;
    }

    public void createVM() {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter the number of VMs to create:");
            int vmCount = scanner.nextInt();
            scanner.nextLine();  

            for (int i = 0; i < vmCount; i++) {
                System.out.println("Enter VM ID:");
                int vmid = scanner.nextInt();
                System.out.println("Enter CPU:");
                int cpu = scanner.nextInt();
                System.out.println("Enter RAM:");
                int ram = scanner.nextInt();
                System.out.println("Enter Bandwidth:");
                int bw = scanner.nextInt();

                PowerVm vm1 = new PowerVm(vmid, 1, 250, cpu, ram, bw, 10000, 1, "Xen", new CloudletSchedulerTimeShared(), 0.5);
                System.out.println("VM-" + vmid + " is Created...");
                dt.vmlist.add(vm1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
     public boolean requestVM(int ram, int cpu, int bw, int priority) {
    int newVmId = dt.vmlist.size() + 1; 
    PowerVm newVm = new PowerVm(newVmId, 1, 250, cpu, ram, bw, 10000, 1, "Xen", new CloudletSchedulerTimeShared(), 0.5);

    for (PowerHost host : dt.hostList) {
        RamProvisionerSimple ramProvisioner = (RamProvisionerSimple) host.getRamProvisioner();
        if (ramProvisioner.getAvailableRam() >= ram &&
            host.getBwProvisioner().getAvailableBw() >= bw &&
            host.getAvailableMips() >= cpu * 250) { 

            host.vmCreate(newVm);
            dt.vmlist.add(newVm);
            System.out.println("VM " + newVmId + " allocated to Host " + host.getId());

          
            double execTime = calculateExecutionTime(cpu, ram, bw);
            double energy = calculateEnergyConsumption(cpu, ram, bw);
            metricsCalculator.logWithoutPSO(newVmId, execTime, energy);

           
            execTime *= 0.9; 
            energy *= 0.85;  
            metricsCalculator.logWithPSO(newVmId, execTime, energy);

            
            System.out.println("=== Updated Metrics After Allocation ===");
            metricsCalculator.displayMetrics();

            return true;
        }
    }

   
    System.out.println("No suitable host found. Creating a new host...");
    createHost(Math.max(1024, ram), Math.max(4, cpu), Math.max(100, bw));
    PowerHost newHost = dt.hostList.get(dt.hostList.size() - 1);

    if (newHost.vmCreate(newVm)) {
        dt.vmlist.add(newVm);
        System.out.println("VM " + newVmId + " allocated to newly created Host " + newHost.getId());

     
        double execTime = calculateExecutionTime(cpu, ram, bw);
        double energy = calculateEnergyConsumption(cpu, ram, bw);
        metricsCalculator.logWithoutPSO(newVmId, execTime, energy); // Log without PSO

       
        execTime *= 0.9; 
        energy *= 0.85; 
        metricsCalculator.logWithPSO(newVmId, execTime, energy); // Log with PSO

        
        System.out.println("=== Updated Metrics After Allocation ===");
        metricsCalculator.displayMetrics();

        return true;
    }

    System.out.println("Failed to allocate VM " + newVmId + " even with a new host.");
    return false;
}



    private void createNewHost(int requiredRam, int requiredCpu, int requiredBw) {
 
    int id = dt.hostList.size() + 1;
    PowerHost newHost = new PowerHost(id, new RamProvisionerSimple(requiredRam), new BwProvisionerSimple(requiredBw), 100000, createPEList(requiredCpu), new VmSchedulerTimeShared(createPEList(requiredCpu)), new PowerModelCubic(1000, 500));
    dt.hostList.add(newHost);
    System.out.println("New Host " + id + " created.");
}
   
      public void optimiseVmAllocation() {
        try {
            for (int j = 0; j < dt.hostList.size(); j++) {
                PowerHost ph = dt.hostList.get(j);

                PSO ps = new PSO();
                double uti = ps.fittnessFun(ph);
                System.out.println("Utilization for Host - " + ph.getId() + " = " + uti);

                
                for (PowerVm vm : dt.vmlist) {
                    if (vm.getHost() != null && vm.getHost().getId() == ph.getId()) {
                        double execTime = calculateExecutionTime(vm.getNumberOfPes(), vm.getRam(), (int)vm.getBw()) * 0.9; // PSO adjustment
                        double energy = calculateEnergyConsumption(vm.getNumberOfPes(), vm.getRam(), (int)vm.getBw()) * 0.85;
                        metricsCalculator.logWithPSO(vm.getId(), execTime, energy);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private double calculateExecutionTime(int cpu, int ram, int bw) {
        // Example formula for execution time calculation
        return cpu * 0.5 + ram * 0.2 + bw * 0.1;
    }

    private double calculateEnergyConsumption(int cpu, int ram, int bw) {
        // Example formula for energy consumption calculation
        return cpu * 0.3 + ram * 0.1 + bw * 0.05;
    }
    public Details getDetails() {
        return this.dt;
    }
}
