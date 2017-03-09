import java.io.PrintWriter;
import java.util.Random;




public class CVRP {
	


	  /* Solution splitting the represented path to CVRP path */
	  public static void printSolutiontoFile(Chromosome best_chromosome) {
		int[] best_route = best_chromosome.getGenes();
		int capacity = CVRPData.VEHICLE_CAPACITY;
	    StringBuilder b = new StringBuilder();
	    b.append("1->");
	    int i=0;
	    for(i=1; i<CVRPData.NUM_NODES; i++) {
	      if(capacity - CVRPData.demand[best_route[i]-1] > 0) {
	        capacity -= CVRPData.demand[best_route[i]-1];
	        b.append((best_route[i]) + "->");
	      } else {
	        capacity = CVRPData.VEHICLE_CAPACITY - CVRPData.demand[best_route[i]-1];
	        b.append("1\n");
	        b.append("1->" + (best_route[i]) + "->");
	      }
	    }
	    b.append("1");

	    try {
	      PrintWriter file = new PrintWriter("best-solution.txt", "UTF-8");
	      file.println("login ib16118 33463");
	      file.println("name Ioannis Borektsioglou");
	      file.println("algorithm Genetic Algorithm with multiple crossover operators and multiple mutation techniques");

	      file.println("cost " + best_chromosome.getCVRPCost());
	      file.print(b.toString());
	      file.close();
	    }
	    catch (Exception ex)
	    {
	      System.err.println("3rr0r");
	    }
	  }
	


	public static void main(String[] args) {
		CVRPData.readFile("fruitybun250.vrp");
		SimpleGA ga = new SimpleGA();
		Chromosome best = ga.runGA();
		
		ga.printSolution(best);
	}
}
