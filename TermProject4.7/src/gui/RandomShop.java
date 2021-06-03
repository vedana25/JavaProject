package gui;
import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import board.Board;
import board.BoardManager;
import board.Player;
import entity.Archer1;
import entity.Archer2;
import entity.Archer3;
import entity.Warrior1;
import entity.Warrior2;
import entity.Warrior3;
import entity.gameObject;


public class RandomShop extends JFrame implements Runnable{
	
    private static final Dimension OUTER_FRAME_DIMENSION = new Dimension(600, 200);
    private static final Dimension BACK_PANEL_DIMENSION = new Dimension(600, 150);
    private static final Dimension CHAMPION_PANEL_DIMENSION = new Dimension(70, 70);
    private static final Dimension REFRESH_PANEL_DIMENSION = new Dimension(70, 70);
    
    private final RandomFrame randomFrame1,randomFrame2;
	static boolean isSelectFinished=false;
	
    Board board;
    private Table gameTable;
    
	public static ArrayList<gameObject> randomEntities= new ArrayList<gameObject>();//12 objects
	public static ArrayList<gameObject> randomSix= new ArrayList<gameObject>();//get 6 objects from randomEntities
	
	//constructor
    public RandomShop(Board board,Table gameTable) {
    	this.gameTable=gameTable;
        this.board=board;
        
        randomFrame1= new RandomFrame(-90,board.player1);
        randomFrame2= new RandomFrame(110,board.player2);

        setDefaultLookAndFeelDecorated(true);
    }
    
    //Thread
    public void run() {
    	initialize();
		randomFrame1.openShop();
		randomFrame2.openShop();
		Scanner scn = new Scanner(System.in);
		while(!isSelectFinished) {
			if(randomFrame1.numOfChoice==3)randomFrame1.randomFrame.dispose();
			if(randomFrame2.numOfChoice==3)randomFrame2.randomFrame.dispose();
			if(randomFrame1.numOfChoice==3&&randomFrame2.numOfChoice==3) isSelectFinished=true;
			try {Thread.sleep(100);
			} catch (InterruptedException e) {}
		}
	}
    public void initialize() {
    	isSelectFinished=false;
    	obtainRandom();
    	randomFrame1.numOfChoice=0;
    	randomFrame2.numOfChoice=0;
    	randomFrame1.initialize();
    	randomSix.clear();
    	randomFrame2.initialize();
    	randomSix.clear();
    }
    
   
    
	private static void center(final JFrame frame, int i) {
        final Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        final int w = frame.getSize().width;
        final int h = frame.getSize().height;
        final int x = (dim.width - w) / 2;
        final int y = (dim.height - h) / 2 + i;
        frame.setLocation(x, y);
    }
    public void obtainRandom() {//choose random object from shopEntity
    	int size = BoardManager.shopEntity.size();
    	randomEntities.clear();
    	
		Random rng = new Random();
		
		//use LinkedHashSet to maintain insertion order
		Set<Integer> generated = new LinkedHashSet<Integer>();
		while (generated.size() < 12)
		{
		    Integer num = rng.nextInt(size);
		    // As we're adding to a set, this will automatically avoid the duplication
		    if(BoardManager.shopEntity.get(num).getSelected()==false) generated.add(num);

		}
		
		for(int i=0;i<size;i++) {
			if(generated.contains(i)) {
				randomEntities.add(BoardManager.shopEntity.get(i));
			}
		}
		Collections.shuffle(randomEntities);
		
		generated.clear();
    }
    
    
    //split 12 objects in randomEntities to 6 and 6
    public ArrayList<gameObject> getHalfObject(int num) {
    	randomSix.clear();
    	if(num==-90) {//for player1
    		for(int i=0;i<6;i++) {
        		randomSix.add(randomEntities.get(i));
        	}
    	}
    	else {//for player2
    		for(int i=0;i<6;i++) {
    			randomSix.add(randomEntities.get(i+6));
    		}
    	}
    	return randomSix;
    }
    
    
    //inner classes
    private class RandomFrame extends JPanel{
    	private final JFrame randomFrame;
    	private final JPanel backPanel;
    	private int numOfChoice=0;
    	
    	RandomFrame(int num,Player player){
    		
			this.randomFrame= new JFrame("Random Champions");
    		this.randomFrame.setLayout(new BorderLayout());
			this.backPanel = new BackPanel(num, player, this);
	        this.randomFrame.add(this.backPanel, BorderLayout.CENTER);
	        this.randomFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	        this.randomFrame.setSize(OUTER_FRAME_DIMENSION);
    	    center(this.randomFrame, num);
    	}
    	public void initialize() {
    		((BackPanel) this.backPanel).initialize();
    	}
    	public void openShop() {
    		randomFrame.setVisible(true);
    		((BackPanel) backPanel).openChampion();
    	}
    	public void closeShop() {
    		randomFrame.setVisible(false);
    	}
    }
    private class RefreshButton extends JButton{
    	int remain=5;
    	JLabel refreshLabel;
    	JLabel remainLabel;
    	RandomFrame randomframe;
    	
    	RefreshButton(RandomFrame randomframe){
    		//setLayout(new GridBagLayout());
    		setBackground(Color.decode("#33FF99"));
    		setPreferredSize(REFRESH_PANEL_DIMENSION);
    		setBorder(BorderFactory.createMatteBorder(5, 5, 5, 5, Color.decode("#33FF99")));
    		
    		refreshLabel= new JLabel("REFRESH: "+remain);
    		this.add(refreshLabel,BorderLayout.CENTER);
    		this.randomframe=randomframe;
    		
    		addMouseListener(new MouseListener() {

				@Override
				public void mouseClicked(MouseEvent e) {
					if (isRightMouseButton(e)) {}
					else if (isLeftMouseButton(e)) {
						if(remain>0) {
							remain--;
							refreshLabel.setText("REFRESH: "+remain);
							randomframe.closeShop();
							obtainRandom();
							randomframe.initialize();
							randomframe.openShop();
							
						}
						else {
							System.out.println("Used all refresh");
						}
					}
					
				}

				@Override
				public void mousePressed(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mouseExited(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}});
    		
    	}        
    }
    
	private class BackPanel extends JPanel {
		int num;
    	private final JButton refreshButton;
		private ArrayList<gameObject> randomSixEntities;
    	public ArrayList<ChampionPanel> championList= new ArrayList<>();
		
		BackPanel(int num, Player player,RandomFrame randomframe){
    		super(new GridLayout(1,7));
    	    this.refreshButton = new RefreshButton(randomframe);
			this.num=num;
			
        	for(int i=0;i<6;i++) {//don't write data in constructor!!
        		championList.add(new ChampionPanel(randomframe, num, board,player));
        		add(championList.get(i));
        	}
        	add(refreshButton);
            setPreferredSize(BACK_PANEL_DIMENSION);
            setBorder(BorderFactory.createEmptyBorder(7, 7, 7, 7));
            setBackground(Color.decode("#DC7633"));
            validate();
    	}
    	public void initialize() {//write new data for new round.
    		randomSixEntities = getHalfObject(num);
    		resetChampion(this.championList,this.randomSixEntities);
    	}
    	
    	//use instead of assignChampion
    	public void resetChampion(ArrayList<ChampionPanel> championList,ArrayList<gameObject> randomSixEntities) {
    		for(int i=0;i<6;i++) {
    			championList.get(i).setText(randomSixEntities.get(i).getName());
    			championList.get(i).Entity=randomSixEntities.get(i);
    		}
    	}
    	public void openChampion() {
    		for(ChampionPanel championpanel:championList) {
    			championpanel.setVisible(true);
    		}
    	}
    }
   
    private class ChampionPanel extends JButton{
    	gameObject Entity;
		boolean levelUp;

        ChampionPanel(RandomFrame randomframe, int num, Board board, Player player) {
            setPreferredSize(CHAMPION_PANEL_DIMENSION);
            setBorder(BorderFactory.createMatteBorder(0, 0, 0, 7, Color.decode("#DC7633")));
            JLabel championName =new JLabel();
            add(championName);
            
            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(final MouseEvent event) {
                	if (isRightMouseButton(event)) {}
                	else if (isLeftMouseButton(event)) {
                    	//Put the selected champions in to the storage
                		if(Entity.getSelected()==false) {
	                		int cellNumber=player.getStorage().firstEmpty();
	                		player.getStorage().takeIn(cellNumber, Entity);	                		
	                		Entity.setInstorage(true);	                		
	                		Entity.setPlayer(player);
	                		
	                		gameTable.getBoardPanel().getStoragePanel(player, cellNumber).assignChampion(board, Entity);
	    					randomframe.numOfChoice++;
	    					BoardManager.shopEntity.remove(Entity); //delete the object from shopEntityList
	        				Entity.setSelected(true);
	        				setVisible(false);
	        				levelUp = false;
	        				
	        				String objectName="";
	        		            Iterator<Map.Entry<String, Integer>>iterator = player.getStorage().numOfObjects.entrySet().iterator();
	        		                while (iterator.hasNext()) {
	        		                	Map.Entry<String, Integer> entry= iterator.next();
	        				    		if(entry.getValue()==3) {
			        	    		    	if(!entry.getKey().substring(entry.getKey().length()-1).equals("3")) {
			        	    		    		levelUp = true;
			        	    		    		entry.setValue(0);
			        	    		    		objectName = entry.getKey();
			        	    		    	}
	        				    		}
	        		                }
		        				
		        				if(levelUp) {
		        					System.out.println("level up");
		        					levelUp(player, objectName);
		        				}
		        				
		        		            Iterator<Map.Entry<String, Integer>>iterator2 = player.getStorage().numOfObjects.entrySet().iterator();
		        		                while (iterator2.hasNext()) {
		        		                	Map.Entry<String, Integer> entry= iterator2.next();
		        				    		if(entry.getValue()==3&&!entry.getKey().substring(entry.getKey().length()-1).equals("3")) {
				        	    		    	levelUp = true;
				        	    		    	objectName = entry.getKey();
		        				    		}
		        		                }
			        				
			        				if(levelUp) {
			        					System.out.println("level up");
			        					levelUp(player, objectName);
			        				}
		        				
                			}
                		else System.out.println("already selected");
                    }
                }

                @Override
                public void mouseExited(final MouseEvent e) {
                }

                @Override
                public void mouseEntered(final MouseEvent e) {
                }

                @Override
                public void mouseReleased(final MouseEvent e) {
                }

                @Override
                public void mousePressed(final MouseEvent e) {
                }
            });
            
            validate();
        }
        
    	public void levelUp(Player player, String name) {

	            //Remove lower-level champions from HashMap that indicates number of collected champions
	            player.getStorage().numOfObjects.put(name, 0);
	            
	            //Remove lower-level champions from Storage
	            Iterator<Map.Entry<Integer,gameObject>
	    		>innerIterator = player.getStorage().cell.entrySet().iterator();
	                while (innerIterator.hasNext()) {
	                	Map.Entry<Integer,gameObject> innerEntry= innerIterator.next();
			    		if(innerEntry.getValue().getName().equals(name)) {
			    			innerIterator.remove();   
			    			player.getStorage().isTaken.remove(innerEntry.getKey());
			    			gameTable.getBoardPanel().getStoragePanel(player, innerEntry.getKey()).removeAll();
			    		}
	                }
	                System.out.println(name);
	        	//Create a higher-level champion
	        	int newLevel = Integer.parseInt(name.substring(name.length()-1))+1;
	        	String newObjectName = name.substring(0,name.length()-1)+newLevel;
	        	gameObject newObject = getGameObject(newObjectName);
	            int cellNumber=player.getStorage().firstEmpty();
		    	
	            //Take higher-level champion into Storage
		    	player.getStorage().takeIn(cellNumber, newObject);
		    	
		    	gameTable.getGameBoard();
				//Assign icon
		    	if(player==Board.player1) {
					gameTable.getGameBoard();
					newObject.setPlayer(Board.player1);
				} else {
					gameTable.getGameBoard();
					newObject.setPlayer(Board.player2);
				}
		    	gameTable.getBoardPanel().getStoragePanel(player, cellNumber).assignChampion(board, newObject);  
		    	
		    	levelUp = false;
		    	String objectName2="";
	            Iterator<Map.Entry<String, Integer>>iterator2 = player.getStorage().numOfObjects.entrySet().iterator();
	                while (iterator2.hasNext()) {
	                	Map.Entry<String, Integer> entry= iterator2.next();
			    		if(entry.getValue()==3) {
			    			if(!entry.getKey().substring(entry.getKey().length()-1).equals("3")) levelUp = true;
    	    		    	objectName2 = entry.getKey();
			    		}
	                }
				
				if(levelUp) {
					System.out.println("level up");
					levelUp(player, objectName2);
				}
           	 
    	}          
    }
    
    gameObject getGameObject(String name) {
    	if(name.equals("Warrior1")) return new Warrior1();
    	else if(name.equals("Warrior2")) return new Warrior2();
    	else if(name.equals("Warrior3")) return new Warrior3();
    	else if(name.equals("Archer1")) return new Archer1();
    	else if(name.equals("Archer2")) return new Archer2();
    	else if(name.equals("Archer3")) return new Archer3();
    	else {
    		System.out.println(name);
    		return null;
    	}
    }
}
