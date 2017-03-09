import java.util.Arrays;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Random;

public class Chromosome {
	final static int NUM_NODES = CVRPData.NUM_NODES;
  	private int[] genes = new int[NUM_NODES];
  	private Double fitness = 0.0;
  	private Double cvrpCost = 0.0;
  	private int cells = 0;
  
  	public Chromosome(){
  		
  		this.genes = getPermutation();
  		this.cvrpCost = computeCVRPCost(this.genes);
  	}
	
    public Chromosome(int[] genes) {
        this.genes = Arrays.copyOf(genes, genes.length);
  		this.cvrpCost = computeCVRPCost(genes);
      }
    
    public Chromosome(Chromosome c) {
        this.genes = Arrays.copyOf(c.genes, c.genes.length);
        this.cvrpCost = c.cvrpCost;
      }
  	
    public Double getFitness() {
        return this.fitness;
      }

      public void setFitness(double fitness) {
        this.fitness = fitness;
      }
      
      public Double getCVRPCost() {
          return this.cvrpCost;
        }

        public void setCVRPcost(double cvrpCost) {
          this.cvrpCost = cvrpCost;
        }
      
      public int[] getGenes() {
        return this.genes;
      }
      
      public void setGenes(int[] genes) {
    	  this.genes = Arrays.copyOf(genes, genes.length);
      }
      
      public void setCells(int n) {
        this.cells = n;
      }

      public int getCells() {
        return this.cells;
      }
  	
		
	public static int[] getPermutation(){
	    int[] chromosome = new int[NUM_NODES];
	    ArrayList<Integer> nodes = new ArrayList<Integer>();
			for(int i = 2; i < NUM_NODES+1; i++){
				nodes.add(i);
			}
      chromosome[0] = 1;
      int pos = 1;
      Random rand = new Random();
      while(nodes.size() > 0){
			  int getNodeVal = rand.nextInt(nodes.size());
			  int i = nodes.get(getNodeVal);
        nodes.remove(getNodeVal);
        chromosome[pos] = i;
        pos++;
      }

		return chromosome;
	}
	
	  public static double computeCVRPCost(int[] g) {
		    int capacity = CVRPData.VEHICLE_CAPACITY;
		    double cost = 0.0;
		    for(int i=0; i<g.length-1; i++) {
		      if(capacity - CVRPData.demand[g[i+1]-1] > 0) {
		        capacity -= CVRPData.demand[g[i+1]-1];
		        cost += SimpleGA.EDM[g[i]-1][g[i+1]-1];
		      } else {
		        capacity = CVRPData.VEHICLE_CAPACITY - CVRPData.demand[g[i+1]-1];
		        cost += SimpleGA.EDM[g[i]-1][0];
		        cost += SimpleGA.EDM[0][g[i+1]-1];
		      }
		    }
		    cost += SimpleGA.EDM[g[g.length-1]-1][0];
		    return cost;
		  }
}
