import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;


public class SimpleGA {
  final static int NUM_NODES = CVRPData.NUM_NODES;

  final static int VEHICLE_CAPACITY = CVRPData.VEHICLE_CAPACITY;

  final static double MUTATION_RATE = 0.25;
  
  static double[][] EDM = null;
  
  protected ArrayList<Double> weights = new ArrayList<Double>();
  
  
  public SimpleGA(){
	  EDM = CVRPData.getEuclideanDistanceMatrix();
  }

  static void swap(int gene1, int gene2, int[] chromosome) {
    int pos1 = 0;
    int pos2 = 0;
    for(int i=0; i<chromosome.length; i++) {
      if(chromosome[i] == gene1) {
        pos1 = i;
      }
      if(chromosome[i] == gene2) {
        pos2 = i;
      }
    }
    int temp = chromosome[pos1];
    chromosome[pos1] = chromosome[pos2];
    chromosome[pos2] = temp;
  }


  /* PMX Crossover*/
  static int[][] pmxCrossover(int[] mum, int[] dad) {
    Random random = new Random();
    int begin = random.nextInt(NUM_NODES-1) + 1;
    int end = random.nextInt(NUM_NODES-begin) + begin;
    int[][] baby = new int[2][NUM_NODES];
    baby[0] = Arrays.copyOf(mum, mum.length);
    baby[1] = Arrays.copyOf(dad, dad.length);
    /*swap a susequence*/
    for(int pos=begin; pos<end; pos++) {
      int gene1 = mum[pos];
      int gene2 = dad[pos];
      swap(gene1, gene2, baby[0]);
      swap(gene1, gene2, baby[1]);
    }
    Chromosome[] babies = new Chromosome[2];
    babies[0] = new Chromosome(baby[0]);
    babies[1] = new Chromosome(baby[1]);
    return baby;
  }
  
  static int[][] cycleCrossover(int[] mum, int[] dad){
	int index1 = 1;
	int index2 = index1, nodeDad, nodeMum;
	int[][] baby = new int[2][NUM_NODES];
	int[][] added = new int[2][NUM_NODES];
	nodeDad = dad[index1];
	nodeMum = mum[index2];
	baby[0][index1] = mum[index1];
	added[0][index1] = 1;
	baby[1][index2] = dad[index2];
	added[1][index2] = 1;
	/* find the cycle and copy it to the offsprings*/
	for(int i = 1; i<mum.length; i++){
		if(mum[i] == nodeDad){
			index1 = i;
		}
		if(nodeMum == dad[i])
			index2 = i;
	}
	while(index1 != 1){
		baby[0][index1] = mum[index1];
		added[0][index1] = 1;
		baby[1][index2] = dad[index2];
		added[1][index2] = 1;
		nodeMum = mum[index2];
		nodeDad = dad[index1];
		for(int i = 1; i<mum.length; i++){
			if(mum[i] == nodeDad){
				index1 = i;
			}
			if(nodeMum == dad[i])
				index2 = i;
		}
	}
	for(int i=0; i< NUM_NODES; i++){
		if(added[0][i] == 0)
			baby[1][i] = mum[i];
		if(added[1][i] == 0)
			baby[0][i] = dad[i];
	}
    Chromosome[] babies = new Chromosome[2];
    babies[0] = new Chromosome(baby[0]);
    babies[1] = new Chromosome(baby[1]);
    return baby;
	  
  }

  static int[] crossover(int[] mum, int[] dad) {
    Random random = new Random();
    int begin = random.nextInt(NUM_NODES-1) + 1;
    int end = random.nextInt(NUM_NODES-begin) + begin;
    int[] added = new int[NUM_NODES];
    int[] baby = new int[NUM_NODES];
    for(int i=begin; i<end; i++) {
      baby[i] = mum[i];
      added[baby[i]-1] = 1;
    }
    baby[0] = 1;
    int j =1;
    for(int i=1; i<NUM_NODES; i++) {
      if(j == begin) {
        j = end;
      }
      if(added[dad[i]-1] == 0) {
        baby[j++] = dad[i];
        added[dad[i]-1] = 1;
      }
    }

    return baby;
  }

  static int[] orderedCrossover(int[] mum, int[] dad) {
    Random random = new Random();
    int begin = random.nextInt(NUM_NODES-1) + 1;
    int end = random.nextInt(NUM_NODES-begin) + begin;
    /* Keep track of added cities */
    int[] added = new int[NUM_NODES];
    int[] baby = new int[NUM_NODES];
    /* Select a subset from the first parent */
    for(int i=begin; i<end; i++) {
      baby[i] = mum[i];
      added[baby[i]-1] = 1;
    }
    /* Set start point */
    baby[0] = 1;
    /* Get genes from the other parent */
    int j = end;
    for(int i=1; i<NUM_NODES; i++) {
      if(j == NUM_NODES) {
        j = 1;
      }

      if(added[dad[i]-1] == 0) {
        baby[j++] = dad[i];
        added[dad[i]-1] = 1;
      }
    }

    return baby;
  }

  /*  Swap Mutation */
  private static Chromosome swapMutation(Chromosome x) {
    Random random = new Random();
    int a = random.nextInt(NUM_NODES-1) + 1;
    int b = random.nextInt(NUM_NODES-1) + 1;
    int[] genes = x.getGenes();
    int temp_gene = genes[a];
    genes[a] = genes[b];
    genes[b] = temp_gene;
    return new Chromosome(genes);
  }

  private static Chromosome insertMutation(Chromosome x) {
    Random random = new Random();
    int pos = random.nextInt(NUM_NODES-1) + 1;
    int gene = random.nextInt(NUM_NODES-1) + 2;
    int gene_pos = 0;
    int[] genes = x.getGenes();
    for(int i=1; i<NUM_NODES; i++) {
      if(genes[i] == gene) {
        gene_pos = i;
        break;
      }
    }

    if(gene_pos < pos) {
      for(int i=gene_pos; i<pos; i++) {
        genes[i] = genes[i+1];
      }
      genes[pos] = gene;
    } else {
      for(int i=gene_pos; i>pos; i--) {
        genes[i] = genes[i-1];
      }
      genes[pos] = gene;
    }
    return new Chromosome(genes);
  }
  
  private static Chromosome invertMutation(Chromosome x){
	    Random random = new Random();
	    int a = random.nextInt(NUM_NODES-1) + 1;
	    int b = random.nextInt(NUM_NODES-1) + 1;
	    while(b < a)
	    	b = random.nextInt(NUM_NODES-1) + 1;	
	    int[] genes = x.getGenes();
	    int[] subsequence = new int[b-a];
	    for(int i=a; i<b; i++){
	    	subsequence[i-a] = genes[i];
	    }
	    for(int i=0; i<b-a; i++){
	    	genes[i+a] = subsequence[i];
	    }
	    return new Chromosome(genes);
  }
  
  /* Get next generation with Roulette Wheel Selection and multiple crossovers */
  static Population getNextGeneration(Population population) {
    Chromosome[] initial = population.getChromosomes();
    Random random = new Random();
    int[] wheel = population.getRouletteWheel();
    
    int size = initial.length;
    List<Chromosome> list = new ArrayList<Chromosome>();

    for(int i=0; i<3*size; i++) {
	/*Selection*/
    	int random_index = random.nextInt(wheel.length - 1);
	    int p1 = wheel[random_index];
	    random_index = random.nextInt(wheel.length - 1);
	    int p2 = wheel[random_index];
	    while(p1 == p2) {
	    	random_index = random.nextInt(wheel.length - 1);
		    p2 = wheel[random_index];
	    }
	    Chromosome parent1 = population.getChromosomes()[p1];
	    Chromosome parent2 = population.getChromosomes()[p2];
	   /*Crossover*/
	    int[][] babies = cycleCrossover(parent1.getGenes(), parent2.getGenes());
	    list.add(new Chromosome(babies[0]));
	    list.add(new Chromosome(babies[1]));
	    
	    list.add(new Chromosome(orderedCrossover(parent1.getGenes(), parent2.getGenes())));
	    list.add(new Chromosome(crossover(parent1.getGenes(), parent2.getGenes())));

	    babies = pmxCrossover(parent1.getGenes(), parent2.getGenes());
	    list.add(new Chromosome(babies[0]));
	    list.add(new Chromosome(babies[1]));
    	  }

    /* Mutation */
    for(int i=0; i<list.size(); i++) {
      if(Math.random() < 0.25) {
        Chromosome mutated_chromosome = insertMutation(list.get(i));
        list.set(i, mutated_chromosome);
      }
      if(Math.random() < 0.25) {
          Chromosome mutated_chromosome = swapMutation(list.get(i));
          list.set(i, mutated_chromosome);
        }
    }
    for(int i = 0; i<size; i++){
    	Chromosome x = new Chromosome();
    	list.add(x);
    }
    int count = 0;
    for(Chromosome x: initial) {
      if(count >= 150) {
        break;
      }
      list.add(x);
      count++;
    }

    return new Population(list.toArray(new Chromosome[list.size()]), size);
  }


  /* */
  public Chromosome runGA() {
    int size = 1000;
    int generations = 3000000;
    int GA_rounds = 7;

    Population best = null;
    double cost = Double.MAX_VALUE;
    

   for(int i=0; i<GA_rounds; i++) {
     // weights.clear();
      Population population = new Population(size);
      int similar = 0;
      double last_cost = population.getMinDistance();
      for(int j=0; j<generations; j++) {
        population = getNextGeneration(population);
        if(last_cost == population.getMinDistance()) {
          similar++;
        } else {
          last_cost = population.getMinDistance();
          similar = 0;
        }
        weights.add(last_cost);
        if(j%50 == 0)
        	System.out.println("Iteration:" + j + " cost = " + last_cost);
        if(similar == 200) {
          break;
        }

      }
      if(population.getMinDistance() < cost) {
        cost = population.getMinDistance();
        best = population;
        if(i < GA_rounds-1)
         printSolution(best.getChromosomes()[0]);
      }
    }
    System.out.println("Total Best Cost: " + cost);
    return best.getChromosomes()[0];
  }
  
/*printing to output stream*/
  public static void printSolution(Chromosome best_chromosome) {
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
    b.append("1\n");

    System.out.println("login ib16118 33463");
    System.out.println("name Ioannis Borektsioglou");
    System.out.println("algorithm Genetic Algorithm with multiple crossover operators and multiple mutation techniques");

    System.out.println("cost " + best_chromosome.getCVRPCost());
    System.out.print(b.toString());
 }

/*routines used for the graphs*/
	public String getName(Chromosome best) {
		return "SimpleGA-" + best.getCVRPCost();
	}
	
	public double[] getWeightsOverTime(){
		return arrayListToMatrix(weights);
	}
	
	public double[] arrayListToMatrix(List<Double> list){
		double[] ret = new double[list.size()];
		Iterator<Double> iterator = list.iterator();

		for (int i = 0; i < list.size(); i++)
	        ret[i] = iterator.next().doubleValue();
	    
	    return ret;
	}
}
