
public class JoueurVirtuel {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		JeuBej partie=new JeuBej(200);
		int[] scores= new int[5000];
		
		for(int j=0; j<10; j++){
			for(int i=0; i<5000; i++){
				partie.initialiser();
				while(!partie.partieFinie()){
					partie.echange(partie.yind-1, partie.xind-1, partie.yind2-1, partie.xind2-1);
					partie.nouvelleGrille();
				}
				scores[i]=partie.donnerScore();
			}
			
			int somme=0;
			for(int i=0; i<5000; i++){
				somme+=scores[i];
			}
			System.out.println(somme/5000);
		}
	}

}
