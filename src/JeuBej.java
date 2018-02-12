
public class JeuBej {

	//attributs	
	private int score;
	private int[][] grille;
	final private int sc=100;
	private int nbPierre;

	private int[][] hauteurChute;  //hauteur de chute (en nb de cases) des minéraux -> utile pour l'animation

	int xind, yind, xind2, yind2; //entier pour proposer une aide au joueur /x=n°(colonne) et y=n°(ligne)

	//constructeur

	public JeuBej(int a){
		nbPierre=a;
		score=0;
		grille=new int[8][8];
		for (int i=0; i<8; i++){
			for(int j=0;j<8; j++){
				grille[i][j]=0;
			}
		}
		hauteurChute=new int[8][8];
		for (int i=0; i<8; i++){
			for(int j=0;j<8; j++){
				hauteurChute[i][j]=0;
			}
		}
	}

	public JeuBej(){
		this(7);
	}

	//méthodes

	//remplissage aléatoire de la grille / on obtient une grille sans alignement
	public void initialiser(){
		for (int i=0; i<8; i++){
			for(int j=0;j<8; j++){
				grille[i][j]=(int)(Math.random()*nbPierre)+1;
			}
	    }
		nouvelleGrille();
		score=0;
	}

	//méthode renvoyant true s'il y a un alignement
	public boolean alignement(){
		boolean rep=false;
		int a=0;
		
		    //travail sur les lignes
			for (int i=0; i<8;i++){
				for(int j=1; j<8;j++){
					if (grille[i][j]==grille[i][j-1]){
						a++;
					}
					else{
						if(a>=2){
							rep=true;
						}
						a=0;
					}
				}
				if ((grille[i][7]==grille[i][6])&&(a>1)){
					rep=true;
				}
				a=0;
			}

			//travail sur les colonnes
			for (int i=0; i<8;i++){
				for(int j=1; j<8;j++){
					if (grille[j][i]==grille[j-1][i]){
						a++;
					}
					else{
						if(a>=2){
							rep=true;
						}
						a=0;
					}
				}
				if ((grille[7][i]==grille[6][i])&&(a>1)){
					rep=true;
				}
				a=0;
			}
		
		return rep;
	}

	// méthode renvoyant true si il y a un alignement dans la grille
	//"passe à zéro" les cases inclues dans un alignement
	public boolean parcours(){
		
		boolean rep=false;
		int a=0;
		//construction d'une copie de la grille
		int[][] tab=new int[8][8];
		for (int i=0; i<8; i++){
			for(int j=0;j<8; j++){
				tab[i][j]=grille[i][j];
			}
		}
		
		//travail sur les lignes
		for (int i=0; i<8;i++){
			for(int j=1; j<8;j++){
				if (grille[i][j]==grille[i][j-1]){
					a++;
				}
				else{
					if(a>=2){
						for(int k=j-1-a; k<j; k++){
							grille[i][k]=0;
						}
					}
					a=0;
				}
			}
			if ((grille[i][7]==grille[i][6])&&(a>1)){
				for (int k=7-a; k<8;k++){
					grille[i][k]=0;
				}
			}
			a=0;
		}

		//travail sur les colonnes (on utilise la copie tab)
		for (int i=0; i<8;i++){
			for(int j=1; j<8;j++){
				if (tab[j][i]==tab[j-1][i]){
					a++;
				}
				else{
					if(a>=2){
						for(int k=j-1-a; k<j; k++){
							tab[k][i]=0;
						}
					}
					a=0;
				}
			}
			if ((tab[7][i]==tab[6][i])&&(a>1)){
				for (int k=7-a; k<8;k++){
					tab[k][i]=0;
				}
			}
			a=0;
		}

		// on passe à zero les cases dans un alignement vertical
		for (int i=0; i<8; i++){
			for(int j=0;j<8; j++){
				if (tab[i][j]==0){
					grille[i][j]=0;
				}
			}
		}
		
		//présence d'un alignement?
		for (int i=0; i<8; i++){
			for(int j=0;j<8; j++){
				if (grille[i][j]==0){
					rep=true;
					return rep;
				}
			}
		}
		return rep;	
	}


	//remplissage de la grille en tenant compte de la "chute des pierres"
	// on donne aux cases leur valeur 'après chute' et on précise la hauteur de chute
	//augmentation du score proportionelle au nb de pierres remplacées
	//retour: nombre de cases enlevées (utile pour le mode timed)
	public int remplissage(){
		int rep=0;
		for(int j=0; j<8; j++){
			boolean b=true;        // utile pour trouver la hauteurChute des derniers Minéraux
			for(int i=7; i>0;i--){
				if(grille[i][j]==0){
					int a=i;
					for(int k=i-1; (k>=0)&&(grille[k][j]==0); k--){
						a=k;
					}
					
					if(a>0){
						grille[i][j]=grille[a-1][j];
						grille[a-1][j]=0;
						hauteurChute[i][j]=i+1-a;     //on donne la hauteur de chute
					}
					else{
						grille[i][j]=(int)(Math.random()*nbPierre)+1;  
						score+=sc;
						rep+=1;
						if (b){
							for(int k=0; k<=i; k++){        //à partir du 1e remplissage aléatoire, les minéraux ont tous la même hauteur de chute
								hauteurChute[k][j]=i+1;
							}
							b=false;
						}	
					}
					
				}
				else{
					hauteurChute[i][j]=0;
				}
			}
			if(grille[0][j]==0){
				grille[0][j]=(int)(Math.random()*nbPierre)+1;
				score+=sc;
				rep+=1;
				if(b){hauteurChute[0][j]=1;}
				else{hauteurChute[0][j]=hauteurChute[1][j];} //cas où il y a déjà eu un remplissage aléatoire
			}
			else hauteurChute[0][j]=0;
		}
		return rep;
	}


	//donne la grille après les chutes et remplissages successifs de pierres
	public void nouvelleGrille(){
		while (parcours()){
			remplissage();
		}
	}


	//echange de 2 cases (si elles sont voisines)
	public boolean echange(int a, int b, int c, int d){
		boolean rep=false;
		if(((a==c)&&(Math.abs(b-d)<2))||(b==d&&Math.abs(a-c)<2)){
			rep=true;
			int x=grille[a][b];
			grille[a][b]=grille[c][d];
			grille[c][d]=x;
		}
		
		return rep;
	}


	//methode renvoyant true si la partie est finie (plus d echange possible)
	//donne également une indication d'echange possible si partie non finie
	public boolean partieFinie(){
		boolean rep=true;

		//echanges horizontaux
		for(int i=0;i<8;i++){
			for(int j=0; j<7;j++){
				echange(i,j,i,j+1);
				if(alignement()){
					rep=false;
					xind=j;
					yind=i;
					xind2=j+1;
					yind2=i;
				}
				echange(i,j,i,j+1);		
			}
		}
		
		//echanges verticaux (à faire uniquement si on n'a pas encore trouvé d'alignement)
		if(rep){
			for(int j=0; j<8;j++){
				for(int i=0; i<7;i++){
					echange(i,j,i+1,j);
					if(alignement()){
						rep=false;
						xind=j;
						yind=i;
						xind2=j;
						yind2=i+1;
					}
					echange(i,j,i+1,j);
				}
			}
		}
		if (rep) xind=-1;
		return rep;
	}

	public boolean partieFinie2(){
		boolean rep=true;
		//echanges verticaux
		for(int j=0; j<8;j++){
			for(int i=0; i<7;i++){
				echange(i,j,i+1,j);
				if(alignement()){
					rep=false;
					xind=j;
					yind=i;
					xind2=j;
					yind2=i+1;
				}
				echange(i,j,i+1,j);
			}
		}
		//echanges horizontaux
		if(rep){
			for(int i=0;i<8;i++){
				for(int j=0; j<7;j++){
					echange(i,j,i,j+1);
					if(alignement()){
						rep=false;
						xind=j;
						yind=i;
						xind2=j+1;
						yind2=i;
					}
					echange(i,j,i,j+1);		
				}
			}
		}
		return rep;
	}

	//on favorise cette fois les échanges des pierres du haut 
	public boolean partieFinie3(){
		boolean rep=true;
		//echanges horizontaux
		for(int i=7;i>=0;i--){
			for(int j=0; j<7;j++){
				echange(i,j,i,j+1);
				if(alignement()){
					rep=false;
					xind=j;
					yind=i;
					xind2=j+1;
					yind2=i;
				}
				echange(i,j,i,j+1);		
			}
		}
		//echanges verticaux (à faire uniquement si on n'a pas encore trouvé d'alignement)
		if(rep){
			for(int j=0; j<8;j++){
				for(int i=6; i>=0;i--){
					echange(i,j,i+1,j);
					if(alignement()){
						rep=false;
						xind=j;
						yind=i;
						xind2=j;
						yind2=i+1;
					}
					echange(i,j,i+1,j);
				}
			}
		}
		if (rep) xind=-1;
		return rep;
	}

	//pour trouver l'échange qui maximise le score (avec un horizon d'un coup)
	public boolean partieFinie4(){
		boolean rep=true;
		int scoreInitial=score;
		int scoreMax=score;
		int tab[][]=new int[8][8];//copie de la grille
		for(int i=0; i<8; i++){
			for(int j=0; j<8; j++){
				tab[i][j]=grille[i][j];
			}
		}
		//echanges horizontaux
		for(int i=0;i<8;i++){
			for(int j=0; j<7;j++){
				echange(i,j,i,j+1);
				if(alignement()){
					rep=false;
					nouvelleGrille();
					if(score>=scoreMax){
						xind=j;
						yind=i;
						xind2=j+1;
						yind2=i;
						scoreMax=score;
					}
					for(int k=0; k<8; k++){
						for(int l=0; l<8; l++){
							grille[k][l]=tab[k][l];
						}
					}
					score=scoreInitial;
				}else echange(i,j,i,j+1);		
			}
		}
		//echanges verticaux
		for(int i=0;i<8;i++){
			for(int j=0; j<7;j++){
				echange(i,j,i,j+1);
				if(alignement()){
					rep=false;
					nouvelleGrille();
					if(score>=scoreMax){
						xind=j;
						yind=i;
						xind2=j+1;
						yind2=i;
						scoreMax=score;
					}
					for(int k=0; k<8; k++){
						for(int l=0; l<8; l++){
							grille[k][l]=tab[k][l];
						}
					}
					score=scoreInitial;
				}else echange(i,j,i,j+1);		
			}
		}
		return rep;
	}


	public int donnerScore(){
		return score;
	}
	public void changerScore(int a){
		score=a;
	}

	//donne la valeur d'une case de la grille
	public int renvoyerValeur(int a, int b){
		return grille[a][b];
	}

	//renvoie la grille
	public int[][] donnerGrille(){
		return grille;
	}
	public int[][] donnerHauteurChute(){
		return hauteurChute;
	}

	//impression de la grille (pour tester la classe)
	public void impGrille(){
		for(int i=0; i<8;i++){
			for (int j=0; j<7;j++){
				System.out.print(grille[i][j]+" ");
			}
			System.out.println(grille[i][7]);
		}
	}
	public void impChute(){
		for(int i=0; i<8;i++){
			for (int j=0; j<7;j++){
				System.out.print(hauteurChute[i][j]+" ");
			}
			System.out.println(hauteurChute[i][7]);
		}
		
	}
	public static void main(String[] args){
		JeuBej partie=new JeuBej();
		partie.initialiser();
		partie.impGrille();
		System.out.println(partie.parcours());
		partie.impGrille();
		System.out.println("grille remplie");
		partie.remplissage();
		partie.impGrille();
		System.out.println("tableau des chutes");
		partie.impChute();
		System.out.println("grille après échange");
		partie.echange(0, 0, 0, 1);
		partie.impGrille();
		System.out.println("Score: "+ partie.donnerScore());
		System.out.println("Partie finie? "+partie.partieFinie()+partie.partieFinie4());
		partie.impGrille();
		System.out.println("Indication : ["+partie.yind+","+partie.xind+"]");
		
		partie.nouvelleGrille();
		System.out.println("grille après 'nouvelleGrille'");
		partie.impGrille();
		System.out.println("présence alignement? "+partie.parcours());
	}

}