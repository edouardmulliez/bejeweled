import java.io.*;  
import java.util.Scanner;

public class TableauDesScores {
	//attributs
	private int[] tab;
	private String nomFichier;
	private int nbScores;
	
	//constructeur
	public TableauDesScores(String s){
		nomFichier=s;
	}
	
	
	//écris le score sur txt
	public void sauvegarderScore(int a){
		try{
			PrintWriter out=new PrintWriter(new FileWriter(nomFichier,true));
			out.println(a);
			out.close();
		}catch(IOException e) {
		      // Typiquement, on n'a pas pu ouvrir le fichier choisi par l'utilisateur
		      System.out.println("### MESSAGE D'ERREUR : " + e.getMessage());
		      System.out.println("### AFFICHAGE DE LA PILE D'APPEL : ");
		      e.printStackTrace();
		}
		actualiserTab();
		classerTableau();
	}
	
	
	public void actualiserTab(){
		nbScores=0;
		try{
			FileReader in= new FileReader(nomFichier); //on récupère le nb de scores dans le fichier txt
			Scanner sc=new Scanner(in);
			while(sc.hasNextInt()){
				sc.nextInt();
				nbScores+=1;
			}
			in.close();
		}catch(FileNotFoundException e){
			System.err.println("Fichier non trouvé.");
		}catch(IOException e){
			System.err.println("erreur d'entrée-sortie");
		}
		
		tab=new int[nbScores];
		try{
			FileReader in= new FileReader(nomFichier);
			Scanner sc2=new Scanner(in);
			for(int i=0; i<nbScores; i++){
				tab[i]=sc2.nextInt();
			}
		}catch(FileNotFoundException e){
			System.err.println("Fichier non trouvé.");
		}catch(IOException e){
			System.err.println("erreur d'entrée-sortie");
		}	
	}
	
	public void classerTableau(){
		nbScores=tab.length;
		int maximum;
		for(int i=0; i<nbScores-1; i++){
			maximum=i;
			for(int j=i+1; j<nbScores; j++){
				if(tab[j]>tab[maximum]) maximum=j;
			}
			echanger(i, maximum);
		}
	}
	
	public void echanger(int i,int j){
		int tmp=tab[i]; tab[i]=tab[j]; tab[j]=tmp;
	}
	
	//fonction à appliquer après classement
	public boolean estMeilleurScore(int a){
		if(a>=tab[0]) return true;
		else return false;
	}
	
	public int[] renvoyerTab(){
		return tab;
	}
	
	public static void main(String[] args){
		JeuBej partie=new JeuBej();
		TableauDesScores tab=new TableauDesScores("gh");
		tab.sauvegarderScore(partie.donnerScore());
		tab.sauvegarderScore(78);
		tab.estMeilleurScore(9);
	}

}
