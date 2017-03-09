import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class Population {
	
	Chromosome[] population;
	Double minDistance = 0.0;
	private int[] roulette_wheel = null;
	
	public Population(int n) {
		this.population = new Chromosome[n];
		population[0] = new Chromosome();
		int added = 1,j;
		for(int i = 1; i<n; i++){
		  Chromosome x = new Chromosome();
		  double newFit = x.getCVRPCost();
		  for( j = 0; j < added; j++){
			if(newFit < population[j].getCVRPCost()){
				for(int k = added; k > j; k--){
					population[k] = new Chromosome(population[k-1].getGenes());
				}
				break;
			}
		  }
		  population[j] = new Chromosome(x.getGenes());
		  added+=1;
		}
	    this.computeFitness(population);
	}	

	
	  public Population(Chromosome[] new_pop, int size) {
		    Arrays.sort(new_pop, new Comparator<Chromosome>() {
		        @Override
		        public int compare(Chromosome c1, Chromosome c2) {
		            return c1.getCVRPCost().compareTo(c2.getCVRPCost());
		        }
		    });
		    Chromosome[] population = new Chromosome[size];
		    System.arraycopy(new_pop, 0, population, 0, size);
		    this.population = population;
		    this.minDistance = population[0].getCVRPCost();
		    this.computeFitness(population);
		  }
	  
	  private void computeFitness(Chromosome[] population) {
		    double total = 0.0;
		    double min = Double.MAX_VALUE;
		    double max = Double.MIN_VALUE;
		    for(int i=0; i<population.length; i++) {
		      total += population[i].getCVRPCost();
		    }
		    for(int i=0; i<population.length; i++) {
		      double temp_fitness = total / population[i].getCVRPCost();
//		      population[i].setFitness(total / population[i].getCVRPCost());
		      if(min > temp_fitness) {
		        min = temp_fitness;
		      }
		      if(max < temp_fitness) {
		        max = temp_fitness;
		      }
		    }
		    int total_cells = 0;
		    for(int i=0; i<population.length; i++) {
		      population[i].setFitness(((total / population[i].getCVRPCost()) - min)/(max-min));
		      population[i].setCells((int)(population[i].getFitness() * population.length));
		      if(population[i].getCells() == 0) {
		        population[i].setCells(1);
		      }
		      total_cells += population[i].getCells();
		    }

		    this.roulette_wheel = new int[total_cells];
		    int k = 0;
		    for(int i=0; i<population.length; i++) {
		      for(int j=0; j<population[i].getCells(); j++) {
		        this.roulette_wheel[k] = i;
		        k++;
		      }
		    }
		    Collections.shuffle(Arrays.asList(this.roulette_wheel));
		  }

		  public Chromosome[] getChromosomes() {
		    return this.population;
		  }

		  public double getMinDistance() {
		    return this.minDistance;
		  }

		  public int[] getRouletteWheel() {
		    return this.roulette_wheel;
		  }	
	
}
