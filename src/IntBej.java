import java.applet.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.JButton;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class IntBej extends Applet implements ActionListener, MouseListener, Runnable{
	private DessinGrille dessinGrille;
	private JButton commencer;
	private Choice modeDeJeu;
	private JButton indication;
	private JButton joueurVirtuel;
	private JLabel score;
	private JLabel message;
	private Panel pbas;
	private Panel phaut;
	private JeuBej partie;
	private boolean partieCommencee;
	
	//pour mode de jeu chronométré
	private boolean timed; 
	private Compteur compteur;
	
	//pour la commande des échanges
	private int x1, y1, x2, y2;
	private boolean premierClic;
	
	private Thread anim;
	private boolean animer;         //gestion de l'animation (pas d'animation si =false)
	private boolean animerVirtuel; //animation liée au joueur virtuel
	private boolean animerEchangeImpossible; //pour échange ne menant pas à un alignement
	
	private TableauDesScores tabScores, tabScoresChrono; //enregistrement des scores
	
	public  void init() { 
		partie=new JeuBej();
		tabScores=new TableauDesScores("scores.txt"); //pour la sauvegarde des scores
		tabScoresChrono=new TableauDesScores("scores-timed-mode.txt");
		anim=new Thread(this);
		
	
		
		compteur=new Compteur();
		
		//initialisation du booleen servant pour la commande des echanges
		premierClic=true;
		
		partieCommencee=false;
		animer=false;
		animerVirtuel=false;
		animerEchangeImpossible=false;
		timed=false;
		
		dessinGrille= new DessinGrille();
		commencer=new JButton("Commencer la partie");
		modeDeJeu=new Choice();
		modeDeJeu.add("mode classique");
		modeDeJeu.add("mode timed");
		indication=new JButton("Indication");
		joueurVirtuel=new JButton("Joueur virtuel");
		message=new JLabel();
		score=new JLabel("Score: ");
		pbas=new Panel();
		phaut=new Panel();
		
		setLayout(new BorderLayout());
		add(phaut, BorderLayout.NORTH);
		add(pbas, BorderLayout.SOUTH);
		add(dessinGrille, BorderLayout.CENTER);
		pbas.setLayout(new GridLayout(2,2));
		pbas.add(score);
		pbas.add(message);
		pbas.add(indication);
		pbas.add(joueurVirtuel);
		phaut.setLayout(new GridLayout(2,1));
		phaut.add(commencer);
		phaut.add(modeDeJeu);
		
		
		
		setSize(375,400);
		
		 commencer.addActionListener(this);
		 indication.addActionListener(this);
		 joueurVirtuel.addActionListener(this);
		 dessinGrille.addMouseListener(this); 
		 dessinGrille.repaint();
	}
	
	
	public void start(){
		if (!anim.isAlive()) {
			anim.start(); // démarrage du thread
			}
	}
	
	public void actionPerformed(ActionEvent evt){
		if (evt.getSource()==commencer) {
			partieCommencee=true;
			premierClic=true;
			animerVirtuel=false;
			partie.initialiser();
			
			//affichage
			dessinGrille.actualiser(partie);
			dessinGrille.eviterAnimation();//permet d'éviter une animation de chute dans le cas 
			dessinGrille.repaint();        //où la nouvelle grille est obtenue après une suppression de ligne
			score.setText("Score: "+partie.donnerScore());
			message.setText("");
			if (partie.partieFinie()) message.setText("La partie est terminée !");
			
			
			//choix du mode de partie
			if(modeDeJeu.getSelectedIndex()==1){  //choix "mode timed" sélectionné
				timed=true;
				compteur.regler(40); //temps initial réglé à 40s.
				compteur.activer();  
			}else{
				timed=false;
			}	
		}
		
		if(evt.getSource()==indication){
			if(partie.partieFinie()) message.setText("La partie est terminée.");
			else{
				dessinGrille.indiquerCase(partie.yind, partie.xind);
				dessinGrille.repaint();
			}
		}
		
		if(evt.getSource()==joueurVirtuel){
			if(!partieCommencee){       //cas où une partie n'est pas déjà en cours
				partie.initialiser();
				dessinGrille.eviterAnimation();
				dessinGrille.repaint();
				partieCommencee=true;
			}
			animerVirtuel=true;	
		}
	}
	
	public void mousePressed(MouseEvent evt) {
		animerVirtuel=false;
		if(partieCommencee && !animer){ 
			
			//récupération des'coordonées' du clic
			int x = evt.getX();
			int y = evt.getY();
			int w = dessinGrille.getSize().width; 
		    int h = dessinGrille.getSize().height;
		    int a = (int)(8*x/w);
		    int b = (int)(8*y/h);
		    
		    
		    if(premierClic){
		    	x1=a;
		    	y1=b;
		    	premierClic=false;
		    	dessinGrille.selectionnerCase(b,a); //on encadre la case sélectionnée
		    	dessinGrille.repaint();
		    }
		    else{
		    	x2=a;
		    	y2=b;
		    	premierClic=true;
		    	dessinGrille.deselectionnerCase(); //on supprime un cadre éventuel autour d'une case
		    	dessinGrille.repaint();
		    	
		    	if(((x1==x2)&&(Math.abs(y1-y2)<2))||(y1==y2&&Math.abs(x1-x2)<2)){ //on vérifie que les cases sont voisines
		    		partie.echange(y1, x1, y2, x2);
		    		
		    		if(partie.alignement()){
						message.setText(" ");//on efface l'eventuel message "cases non voisines"
		    			animer=true;           // pour déclencher l'animation
		    		}
		    		else{
		    			//on remet les cases à leur position d'origine
		    			partie.echange(y1, x1, y2, x2);
		    			animerEchangeImpossible=true; //animation
		    		}
		    			
		    	}
		    	else message.setText("Cases non voisines !");
		    }
		}    
	}
	
	public void mouseReleased(MouseEvent evt) {}
	public void mouseClicked(MouseEvent evt) {}
	public void mouseEntered(MouseEvent evt) {}
	public void mouseExited(MouseEvent evt) {}
	
	public void run(){
		
		while(true){
			//échange des cases par le joueur virtuel
			if(animerVirtuel && !partie.partieFinie()){
				x1=partie.xind; y1=partie.yind; x2=partie.xind2; y2=partie.yind2;
				partie.echange(y1, x1, y2, x2);				
			}
			
			if(animer || animerVirtuel){
				
				//animation pour l'échange
				while(dessinGrille.iterationEchange(y1, x1, y2, x2)){
					dessinGrille.repaint();
					
					if(timed) message.setText(""+compteur.donnerTemps());
					try {
						Thread.sleep(45);     //temps d'arrêt entre 2 images
					}catch(InterruptedException e) {
						System.out.println(e);
					}
				}
				try {
					Thread.sleep(300);     
				}catch(InterruptedException e) {
					System.out.println(e);
				}
				
				while(partie.alignement()){
					partie.parcours();
					int a=partie.remplissage();
					if(timed) compteur.ajouter(a); //en mode timed, on ajoute un nombre de secondes correspondant aux minéraux enlevés 
					score.setText("Score: "+partie.donnerScore());
					dessinGrille.actualiser(partie);
					while(dessinGrille.itération()){  //on ne ressort de cette boucle qu'une fois les minéraux descendus
						dessinGrille.repaint();
						
						if(timed) message.setText(""+compteur.donnerTemps()); //affichage du temps restant
						try {
							Thread.sleep(45);     //temps d'arrêt entre 2 images
						}catch(InterruptedException e) {
							System.out.println(e);
						}
						
					}
					try {
						Thread.sleep(750);     //temps d'arrêt entre 2 suppressions de ligne (permet au joueur de mieux voir ce qui se passe)
					}catch(InterruptedException e) {
						System.out.println(e);
					}
				}
				animer=false;
				if (partie.partieFinie()){
					if(timed) message.setText("Plus d'échange possible.");
					while(dessinGrille.iterationFin()){    //animation en fin de partie
						dessinGrille.repaint();
						try {
							Thread.sleep(20);     //temps d'arrêt entre 2 images
						}catch(InterruptedException e) {
							System.out.println(e);
						}
					}
					
					if(timed){  //en mode timed, si on n'a plus d'échanges possibles, on recommence avec une nouvelle grille
						int s=partie.donnerScore();
						partie.initialiser();
						partie.changerScore(s); //permet de garder le même score qu'avant (nécessaire 
						dessinGrille.actualiser(partie);//car initialiser() passe le score à 0.)
						dessinGrille.eviterAnimation();
						dessinGrille.repaint();
						
					}else{
						partieCommencee=false;
						animerVirtuel=false;
						tabScores.sauvegarderScore(partie.donnerScore()); //sauvegarde du score sur fichier txt
						if(tabScores.estMeilleurScore(partie.donnerScore())){
							message.setText("Meilleur score !!");
						}else{
							message.setText("La partie est terminée !");
						}
					}
				}
			}
			
			
			if(timed){
				if(compteur.fini()){ //(si temps écoulé)
					
					if(partieCommencee) tabScoresChrono.sauvegarderScore(partie.donnerScore()); //sauvegarde du score sur fichier txt (on enregistre une seule fois le score)
					if(tabScoresChrono.estMeilleurScore(partie.donnerScore())){
						message.setText("Temps écoulé. Meilleur score !!");
					}else{
						message.setText("Temps écoulé !");
					}
					
					partieCommencee=false;
					animerVirtuel=false;
					
					while(dessinGrille.iterationFin()){	    //animation en fin de partie
						dessinGrille.deselectionnerCase(); //efface un éventuel cadre de sélection
						dessinGrille.repaint();
						try {
							Thread.sleep(20);     //temps d'arrêt entre 2 images
						}catch(InterruptedException e) {
							System.out.println(e);
						}
					}
				}else{
					message.setText(""+compteur.donnerTemps());  //affichage du temps restant(mode timed)
				}
			}
			
			//animation pour un échange impossible (aller retour des pierres)
			if(animerEchangeImpossible){
				while(dessinGrille.iterationEchangeImpossible(y1,x1,y2,x2)){
					dessinGrille.repaint();
					try {
						Thread.sleep(45);     //temps d'arrêt entre 2 images
					}catch(InterruptedException e) {
						System.out.println(e);
					}
				}
				animerEchangeImpossible=false;
			}
			
			
			
			try {
				Thread.sleep(90);
			}catch(InterruptedException e) {
				System.out.println(e);
			}
				
		}
	}
	
	
	 public void destroy() {
		 removeAll(); 
	 }
		   
	  
}
