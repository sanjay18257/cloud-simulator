
package loadpso;

import java.util.List;
import java.util.ArrayList;
import org.cloudbus.cloudsim.power.PowerHost;
import org.cloudbus.cloudsim.power.PowerVm;

/**
 *
 * @author admin
 */
public class Details 
{
    public static String vms[][];
    public static String host[][];
    
    public static ArrayList Vt=new ArrayList();
    public static ArrayList Ht=new ArrayList();
    
   
     public static List<PowerVm> vmlist = new ArrayList<PowerVm>();
    public static List<PowerHost> hostList = new ArrayList<PowerHost>();

    public static double Velocity[][];
    public static double Position[][];
    public static ArrayList request = new ArrayList();
    public static ArrayList population = new ArrayList();
    public static ArrayList initialpop = new ArrayList();
    public static int pop = 8;
    public static double pbest[] = new double[pop];
    public static double gbest   = 200;
    public static String psobest;
    public static List<PowerVm> allVM = new ArrayList<PowerVm>();
    public static ArrayList newList = new ArrayList();
}
