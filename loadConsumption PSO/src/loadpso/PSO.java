
package loadpso;
import java.util.Random;
import org.cloudbus.cloudsim.power.PowerHost;
import org.cloudbus.cloudsim.power.PowerVm;
import org.cloudbus.cloudsim.power.PowerVmAllocationPolicySimple;
        
/**
 *
 * @author admin
 */
public class PSO 
{
    Details dt=new Details();
    double weight=0.1;
    double c1=1;
    double c2=1;
    double x_max = 4.0;
    double v_max=4.0;
    double x_min = -0.4;
    double v_min=-4.0;
    int iter=50;
    
    PSO()
    {
        
    }
    
    public void applyPSO()
    {
        try
        {
            Random rn=new Random();
            
            
            dt.Velocity=new double[dt.pop][dt.request.size()];
            dt.Position=new double[dt.pop][dt.request.size()];
            
            for(int it=0;it<iter;it++)
            {
                int rk[][]=new int[dt.pop][dt.request.size()];
            
                for(int i=0;i<dt.request.size();i++)
                {
                    dt.Position[0][i]=x_min + (x_max - x_min) *rn.nextDouble() ;
                    dt.Velocity[0][i]=v_min + (v_max - v_min) *rn.nextDouble() ;                
                }
            
                double de[][]=new double[dt.request.size()][3];
                for(int i=0;i<dt.request.size();i++)
                {
                    de[i][0]=dt.Position[0][i];
                    de[i][1]=i;
                    de[i][2]=i;
                }
                for(int i=0;i<dt.request.size();i++)
                {
                    for(int j=i+1;j<dt.request.size();j++)
                    {
                        if(de[i][0]>de[j][0])
                        {
                            double t1=de[i][0];
                            de[i][0]=de[j][0];
                            de[j][0]=t1;
                        
                            double t2=de[i][1];
                            de[i][1]=de[j][1];
                            de[j][1]=t2;
                        }
                    }
                }
           
                for(int i=0;i<dt.request.size();i++)
                {
                    int k1=(int)de[i][1];
                    int k2=(int)de[i][2];
                    
                    rk[0][k1]=(k2%dt.host.length)+1;               
                }
                
                for(int pi=0;pi<dt.pop;pi++)
                {
                    String g1[]=dt.population.get(pi).toString().split("#");
                    double Cexe=0;
                    
                    for(int i=0;i<g1.length;i++)
                    {                       
                        String g2[]=dt.request.get(i).toString().split("#");
                        double dur=Double.parseDouble(g2[1]);
                        double res=Double.parseDouble(g2[2])+Double.parseDouble(g2[3])+Double.parseDouble(g2[4]);
                        if(res==0)
                            res=1;
                        Cexe=Cexe+(Double.parseDouble(g1[i])*(dur/res));
                        Cexe=Cexe+(dt.Position[pi][i]-dt.Velocity[pi][i]);
                    }
                   
                    dt.pbest[pi]=Cexe;
                    if(dt.gbest<Cexe)
                    {
                        dt.psobest=dt.population.get(pi).toString();
                        dt.gbest=Cexe;
                    }
                }
                
                for(int i=0;i<dt.pop-1;i++)
                {
                    for(int j=0;j<dt.request.size();j++)
                    {
                        dt.Velocity[i+1][j]=weight*dt.Velocity[i][j]+c1*rn.nextDouble()*(dt.pbest[i]-dt.Position[i][j])+c2*rn.nextDouble()*(dt.gbest-dt.Position[i][j]);
                        dt.Position[i+1][j]=dt.Position[i][j]+dt.Velocity[i][j];
                    }
                }
                
                
            } 
            
            System.out.println("gbest final "+dt.gbest);
            System.out.println("pso best "+dt.psobest);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public double fittnessFun(PowerHost ph)
    {
        double uti=0;
        try
        {
           
            PowerVmAllocationPolicySimple ps=new PowerVmAllocationPolicySimple(dt.hostList);
            int h=0;
            for(int i=0;i<dt.vmlist.size();i++)
            {               
                PowerVm vm=dt.vmlist.get(i);
                if(!dt.allVM.contains(vm))
                {
                    boolean bool=ps.allocateHostForVm(vm, ph);
                    
                    if(bool)
                    {
                        
                        uti=ph.getUtilizationOfRam()+ph.getUtilizationOfBw()+ph.getUtilizationOfCpuMips();
                        dt.allVM.add(vm);
                        dt.newList.add(vm.getId()+"#"+ph.getId());
                        h++;
                    }
                    else
                    {
                        System.out.println("VM - "+vm.getId()+" is migrated");
                        break;
                    }
                }
                
            }
            //System.out.println(ph.getId()+" : "+uti);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return uti;
    }
}
