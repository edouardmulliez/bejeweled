import java.awt.*;

@SuppressWarnings("serial")
public class DessinGrille extends Canvas{
	
	private int[][] positionVerticale;  //position verticale des minéraux (utile pour l'animation) 
	private int[][] positionHorizontale;
	private boolean[][] caseSelectionnee; //pour dessiner un cadre autour de la 1e case sélectionnée
	private boolean[][] caseIndiquee; // pour dessiner un cadre autour de la case indiquee
	private final int pas=1;  // pas pour la descente des pierres
	private Image[] image, imageAlEchelle;
	private int[][] typeMineraux;
	
	private boolean sensAller; //sert pour l'animation pour un échange impossible
								//=true si on est dans le sens aller, false pour le sens retour

	
	// attributs ajoutés pour l'anti-clignottement ("double-buffering") :
	  protected Image imgCachee;          
	  protected Dimension dimImgCachee;   
	
	public DessinGrille(){
		typeMineraux=new int[8][8];
		image=new Image[7];
		imageAlEchelle=new Image[7];
		positionVerticale=new int[8][8];    //un minérai dans la ligne i est à la positionVerticale 10*i
		positionHorizontale=new int[8][8];
		caseSelectionnee=new boolean[8][8];
		caseIndiquee=new boolean[8][8];
		sensAller=true;
		for(int i=0; i<8; i++){
			for(int j=0; j<8; j++){
				positionHorizontale[i][j]=10*j;
				positionVerticale[i][j]=10*i;
				typeMineraux[i][j]=0;
				caseSelectionnee[i][j]=false;
				caseIndiquee[i][j]=false;
			}
		}
		
		//récupération des images de minéeaux
		image[0]=Toolkit.getDefaultToolkit().getImage("minerai1.jpg");
		image[1]=Toolkit.getDefaultToolkit().getImage("minerai2.jpg");
		image[2]=Toolkit.getDefaultToolkit().getImage("minerai3.jpg");
		image[3]=Toolkit.getDefaultToolkit().getImage("minerai4.jpg");
		image[4]=Toolkit.getDefaultToolkit().getImage("minerai5.jpg");
		image[5]=Toolkit.getDefaultToolkit().getImage("minerai6.jpg");
		image[6]=Toolkit.getDefaultToolkit().getImage("minerai7.jpg");
	}
	
	public void paint(Graphics g)  {
		// Recuperation des dimensions du canvas
	    int w = getSize().width; 
	    int h = getSize().height;
	    
	    g.setColor(Color.black);
	    g.fillRect(0,0,w,h);
	    
	    for(int i=0; i<7; i++){
	    	imageAlEchelle[i]=image[i].getScaledInstance(w/8, h/8, 0); //images mises à l'échelle
	    }
	    MediaTracker imgCheck = new MediaTracker(this);
    	for(int i=0; i<7; i++) imgCheck.addImage(imageAlEchelle[i], 0);
    	try {
			imgCheck.waitForID(0);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// AFFICHAGE SI CHARGEMENT IMAGE OK
		if (!imgCheck.isErrorID(0)){
			for (int i=0; i<8; i++){
		    	for(int j=0; j<8; j++){
		    		if(typeMineraux[i][j]>0){
		    			g.drawImage(imageAlEchelle[typeMineraux[i][j]-1],positionHorizontale[i][j]*w/80, positionVerticale[i][j]*h/80,this);
		    		}
		    		if(caseIndiquee[i][j]){
		    			g.setColor(Color.CYAN);
		    			g.drawRect(j*w/8, i*h/8, 9*w/80, 9*h/80);
		    		}
		    		if(caseSelectionnee[i][j]){
		    			g.setColor(Color.white);
		    			g.drawRect(j*w/8, i*h/8, 9*w/80, 9*h/80);
		    		}
		    	}
		    }
		}
	}
	
	//
	public void actualiser(JeuBej partie){
		for(int i=0; i<8; i++){
			for(int j=0; j<8; j++){
				typeMineraux[i][j]=partie.donnerGrille()[i][j];
				positionVerticale[i][j]=10*(i-partie.donnerHauteurChute()[i][j]);
				positionHorizontale[i][j]=10*j;
			}
		}
	}
	    
	//pour l'animation (chute) ; true si animation non finie
	public boolean itération(){
		boolean rep=false;
		for (int i=0; i<8; i++){
	    	for(int j=0; j<8; j++){
	    		if(positionVerticale[i][j]<10*i){
	    			positionVerticale[i][j]+=pas;
	    			rep=true;
	    		}
	    	}
		}
		return rep;
	}
	
	//pour l'animation (échange)
	public boolean iterationEchange(int i, int j, int k, int l){
		boolean rep=false;
		int a, b;
		if(i==k){ //échange horizontal
			if(j<l){
				a=j; b=l;
			}
			else{
				a=l; b=j;
			}
			if(positionHorizontale[i][a]<10*b){
				rep=true;
				positionHorizontale[i][a]+=pas;
				positionHorizontale[i][b]-=pas;
			}
		}
		else{ //échange vertical (j==l)
			if(i<k){
				a=i; b=k;
			}else{
				a=k; b=i;
			}
			if(positionVerticale[a][j]<10*b){
				rep=true;
				positionVerticale[a][j]+=pas;
				positionVerticale[b][j]-=pas;
			}
		}
		return rep;
	}
	
	public boolean iterationEchangeImpossible(int i, int j, int k, int l){
		boolean rep=true;
		int a, b;
		if(i==k){ //échange horizontal
			if(j<l){
				a=j; b=l;
			}
			else{
				a=l; b=j;
			}
			
			if(sensAller){
				if(positionHorizontale[i][a]<10*b){
					positionHorizontale[i][a]+=pas;
					positionHorizontale[i][b]-=pas;
				}else{
					sensAller=false;
				}
			}
			if(!sensAller){
				if(positionHorizontale[i][a]>10*a){
					positionHorizontale[i][a]-=pas;
					positionHorizontale[i][b]+=pas;
				}else{
					rep=false;
					sensAller=true;
				}
			}
		}
		else{ //échange vertical (j==l)
			if(i<k){
				a=i; b=k;
			}else{
				a=k; b=i;
			}
			if(sensAller){
				if(positionVerticale[a][j]<10*b){
					positionVerticale[a][j]+=pas;
					positionVerticale[b][j]-=pas;
				}else{
					sensAller=false;
				}
			}
			if(!sensAller){
				if(positionVerticale[a][j]>10*a){
					positionVerticale[a][j]-=pas;
					positionVerticale[b][j]+=pas;
				}else{
					rep=false;
					sensAller=true;
				}
			}	
		}
		return rep;
	}
	
	//pour l'animation en fin de partie
	public boolean iterationFin(){
		boolean rep=false;
		for (int i=0; i<8; i++){
	    	for(int j=0; j<8; j++){
	    		positionVerticale[i][j]+=pas;
	    	}
		}
		if(positionVerticale[0][0]<82) rep=true; //vrai tant que les minéraux du haut ne sont pas encore sortis de l'écran
		return rep;
	}
	
	//met directement les minéraux à leur positionVerticale post-chute
	public void eviterAnimation(){
		for(int i=0; i<8; i++){
			for(int j=0; j<8; j++){
				positionVerticale[i][j]=10*i;
			}
		}
	}
	
	public void selectionnerCase(int i, int j){
		caseSelectionnee[i][j]=true;
	}
	public void indiquerCase(int i, int j){
		caseIndiquee[i][j]=true;
	}
	public void deselectionnerCase(){
		for(int i=0; i<8;i++){
			for(int j=0; j<8; j++){
				caseSelectionnee[i][j]=false;
				caseIndiquee[i][j]=false;
			}
		}
	}
	
	
	//pour éviter le clignotement
	public void update(Graphics g) {
	    Dimension dimCanvas = getSize();                 
	    if ( imgCachee==null || dimImgCachee.width!=dimCanvas.width || dimImgCachee.height!=dimCanvas.height ) {
	      imgCachee = createImage(dimCanvas.width, dimCanvas.height);
	      dimImgCachee = dimCanvas;                     
	    }
	    paint(imgCachee.getGraphics());               
	    g.drawImage(imgCachee, 0, 0, null);
	}
}
