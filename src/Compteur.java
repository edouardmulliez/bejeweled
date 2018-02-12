
import java.util.Scanner;


public class Compteur implements Runnable{

	private int temps;
	private boolean actif;
	private Thread anim;
	/**
	 * @param args
	 */
	public Compteur(){
		temps=300;
		actif=false;
		anim=new Thread(this);
		if (!anim.isAlive()) {
			anim.start(); // démarrage du thread
		}
	}
	
	public void run(){
		while(true){
			if(actif){
				if(temps>0){
					temps--;
				}
				
			}
			try {
				Thread.sleep(1000);     //temps d'arrêt entre 2 images
			}catch(InterruptedException e) {
				System.out.println(e);
			}
		}
	}
	
	public void activer(){ actif=true;}
	public void desactiver(){ actif=false;}
	public void regler(int a){ temps=a;}
	public void ajouter(int a){temps+=a;}
	public int donnerTemps(){return temps;}
	public boolean actif(){return actif;}
	public boolean fini(){
		if(actif && temps==0) return true;
		else return false;
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Compteur compteur=new Compteur();
		compteur.activer();
		while(true){
			Scanner sc=new Scanner(System.in);
			String s=sc.nextLine();
			System.out.println(s+compteur.temps);
		}

	}

}
