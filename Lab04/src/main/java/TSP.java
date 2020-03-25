import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class TSP {
    private String inputFilename;
    private String outputFilename;
    private int n;
    private int[][]a;

    public TSP(String inputFilename, String outputFilename){
        this.inputFilename = inputFilename;
        this.outputFilename = outputFilename;
        readFromFile();
        printToFile();
    }

    private void readFromFile() {
        try {
            File file = new File(inputFilename);
            BufferedReader br = new BufferedReader(new FileReader(file));
            n = Integer.parseInt(br.readLine());
            a = new int[n+1][n+1];
            String line;
            for(int i=1;i<=n;i++){
                line = br.readLine();
                String []attr = line.split(",");
                for(int j=1;j<=n;j++){
                    a[i][j] = Integer.parseInt(attr[j-1]);
                }
            }
        }catch (Exception e){
            System.out.println(e.toString());
        }
    }

    private class Chromozome {
        private int[] gene;

        public Chromozome() {
            this.gene = generateGene();
        }

        public int[] getGene() {
            return this.gene;
        }

        public void setGene(int[]gene){
            this.gene = gene;
        }

        /*
         * ->> gena se va reprezenta ca o permutare de n elemente indexate de la 1 la n
         * ->> ordinea elementelor va fi aleatoare
         * */
        private int[] generateGene() {
            int[] g = new int[n + 1];
            ArrayList<Integer> alreadyAdded = new ArrayList<>();
            for (int i = 1; i <= n; i++) {
                int adn = Math.abs(new Random().nextInt() % n) + 1;
                while (alreadyAdded.contains(adn)) {
                    adn = Math.abs(new Random().nextInt() % n) + 1;
                }
                alreadyAdded.add(adn);
                g[i] = adn;
            }
            return g;
        }
        /*
         * ->> fitnessul va fi suma ponderilor
         * */
        public int getFitness() {
            int s = 0;
            for (int i = 1; i < n; i++) {
                s += a[gene[i]][gene[i + 1]];
            }
            return s;
        }

    }

    /*
    * compunere de permutari
    * */
    private Chromozome crossover(Chromozome c1, Chromozome c2){
        Chromozome c = new Chromozome();
        int [] localGene = new int[n+1];
        int [] c2gene = c2.getGene();
        int [] c1gene = c1.getGene();
        for (int i=1; i <= n; i++){
            localGene[i] = c1gene[c2gene[i]];
        }
        c.setGene(localGene);
        return c;
    }

    /*
    * interschimbare 2 molecule de adn/arn -->> reprezinta tot o permutare
    * */
    private Chromozome mutation(Chromozome c) {
        int [] localGene = c.getGene();
        int r1 = Math.abs(new Random().nextInt() % n) + 1;
        int r2 = Math.abs(new Random().nextInt() % n) + 1;
        int aux = localGene[r1];
        localGene[r1] = localGene[r2];
        localGene[r2] = aux;
        c.setGene(localGene);
        return c;
    }

    private int popSize = 1000;
    private int noIter = 100;

    private ArrayList<Chromozome> initPop(){
        ArrayList<Chromozome> list = new ArrayList<>();
        for (int i=0; i<popSize; i++) {
            list.add(new Chromozome());
        }
        return list;
    }

    /*
    * selectie de tip ruleta
    * */
    private Chromozome selection(ArrayList<Chromozome> list){
        int r = Math.abs(new Random().nextInt() % list.size());
        return list.get(r);
    }

    private Chromozome best(ArrayList<Chromozome> list) {
        Chromozome cr = list.get(0);
        int fitness = cr.getFitness();
        for (Chromozome c : list){
            if (c.getFitness() < fitness) {
                fitness =c.getFitness();
                cr = c;
            }
        }
        return cr;
    }

    /*
     * la fiecare generatie se va memora cel mai bun cromozom intr-o lista
     * se va gasi the best of the bests apoi
     * algoritm generational -> la fiecare iteratie se va actualiza intreaga populatie cu copiii
     * */

    private Chromozome geneticAlgorithm() {
        ArrayList<Chromozome> bests = new ArrayList<>();
        ArrayList<Chromozome> pop = initPop();
        for (int it = 0; it < noIter; it++) {
            ArrayList<Chromozome> newPop = new ArrayList<>();
            for (int i=0; i< pop.size(); i++) {
                Chromozome mother = selection(pop);
                Chromozome father = selection(pop);
                Chromozome crossover = crossover(mother,father);
                Chromozome mutation = mutation(crossover);
                newPop.add(mutation);
            }
            bests.add(best(newPop));
            pop = newPop;
        }
        return best(bests);
    }

    private int sume(int [] list) {
        int s = 0;
        for (int i=1; i < n; i++) {
            s+=a[list[i]][list[i+1]];
        }
        s+=a[list[n]][1];
        return s;
    }

    private void printToFile(){
        try {
            Chromozome c = geneticAlgorithm();
            StringBuilder fileContent = new StringBuilder();
            for (int i=1; i <= n; i++){
                if (i < n) {
                    fileContent.append(c.getGene()[i]).append(",");
                } else {
                    fileContent.append(c.getGene()[i]);
                }
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilename));
            writer.write(n+"\n");
            writer.write(fileContent.toString()+"\n");
            writer.write(sume(c.getGene())+"\n");
            writer.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public static void main(String [] args){
        TSP t =new TSP("C:\\Users\\alexandru.sabou\\university\\An3\\Sem2\\AI\\Lab04\\src\\main\\resources\\input.txt","C:\\Users\\alexandru.sabou\\university\\An3\\Sem2\\AI\\Lab04\\src\\main\\resources\\output.txt");
    }

}
