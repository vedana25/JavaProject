package board;

import entity.Archer1;
import entity.Archer2;
import entity.Archer3;
import entity.Warrior1;
import entity.Warrior2;
import entity.Warrior3;

import entity.gameObject;
import gui.Table;
import gui.Table.TilePanel;

import java.util.*;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities; 
public class Board {
	//test2
	System.out.println("test2");
	public static final String RESET = "\u001B[0m";
	public static final String BLUE = "\u001B[34m";
	public static final String RED = "\u001B[31m";
	
	public static Player player1;
	public static Player player2;
	public Storage storage1;
	public Storage storage2;
	public static int round=1;
	public static int maxObj;
	public boolean isFinish=false;
	
	HashMap<Integer, gameObject> EntityMap;
	
	private static int finished=0;

	public Board() {
		this.initialize();	
	}
	//this is to test
	System.out.println("test");
	//write a line about an attack on the log
	public static void printOnlog(gameObject attacker, gameObject target, int damage) {
		if(target.getHealth()<=0) {
			System.out.println(colorName(attacker)+" killed "+colorName(target));
		}
		else if(damage>0) {
			System.out.println(colorName(attacker)+" dealt "+damage+" damages to "+colorName(target)+"(Hp: "+ target.getHealth()+")");
		}
		else System.out.println(colorName(attacker)+" healed "+-damage+" HP to "+colorName(target)+"(Hp: "+ target.getHealth()+")");
	}
	
	//when an object attacked with a skill
	public static void printOnlog(gameObject attacker, gameObject target, int damage,String skillname) {
		if(target.getHealth()<=0) {
			System.out.println(colorName(attacker)+" killed "+colorName(target));
		}
		else if(damage>0) {
			System.out.println(colorName(attacker)+" dealt "+damage+" damages to "+colorName(target)+"(Hp: "+ target.getHealth()+")"+" with: "+attacker.getSkillname());
		}
		else System.out.println(colorName(attacker)+" healed "+-damage+" HP to "+colorName(target)+"(Hp: "+ target.getHealth()+")"+" with: "+attacker.getSkillname());
	}		
	public static String colorName(gameObject object) {
		String nameWithColor;
		if(object.getPlayer()==player1) {
			nameWithColor=BLUE+object.getName()+RESET;
		}
		else nameWithColor=RED+object.getName()+RESET;
		return nameWithColor;
	}
	
	public int getRound() {
		return round;
	}
	
	public Storage getStorage1() {
		return this.storage1;
	}
	
	public Storage getStorage2() {
		return this.storage2;
	}
	public void setFinish(int isfinish) {
		finished=isfinish;
	}
	
	//Print ROUND # and Score
	public void printScore() {
		int presentRound = round+1;
		System.out.println("SCORE "+player1.getNumOfWin()+" : "+player2.getNumOfWin());
		System.out.println("Player1 HP: " + player1.getHp());
		System.out.println("Player2 HP: " + player2.getHp());
	}
	
	//GUI Version Print ROUND # and Score
	public String popupScore() {
		String message;
		int presentRound = round;
		message = "ROUND "+presentRound+
		"\nSCORE "+player1.getNumOfWin()+" : "+player2.getNumOfWin() +
		"\nPlayer1 HP: " + player1.getHp() +
		"\nPlayer2 HP: " + player2.getHp();
		return message;
	}
	
	//move all objects on the board to the storages after end of a round
	public void clearBoard(Table gameTable) {
		
		player1.setnumOfobj(0);
		player2.setnumOfobj(0);

		for(int i=0;i<10;i++) {
			for(int j=0;j<10;j++) {
				gameTable.getBoardPanel().getTilePanel(10*i+j).removeAll();
				gameTable.getBoardPanel().getTilePanel(10*i+j).setTileObject(null);
				
                SwingUtilities.invokeLater
                (
                     new Runnable()  {
                          public void run()     {
                        	  gameTable.getBoardPanel().drawBoard(gameTable.getGameBoard());			
      						          					
              		        }
                      }
                );
				
				if(BoardManager.ENTITIES_ONBOARD[i][j]!=null) {
					gameObject e=BoardManager.ENTITIES_ONBOARD[i][j];
					if(e.getPlayer()==gameTable.getGameBoard().player1) {
						int cellNum = storage1.firstEmpty();
						gameTable.getBoardPanel().getStoragePanel(e.getPlayer(), cellNum).assignChampion(gameTable.getGameBoard(), e);
						e.setCellNum(cellNum);
						e.setInstorage(true);	
						storage1.isTaken.put(cellNum,true);
						BoardManager.ENTITIES_ONBOARD[i][j]=null;
					}
					else {
						int cellNum = storage2.firstEmpty();
						gameTable.getBoardPanel().getStoragePanel(e.getPlayer(), cellNum).assignChampion(gameTable.getGameBoard(), e);
						e.setCellNum(cellNum);
						e.setInstorage(true);	
						storage2.isTaken.put(cellNum,true);
						BoardManager.ENTITIES_ONBOARD[i][j]=null;


					}
					e.setInstorage(true);
					e.setHealth(e.getMaxHealth());
				}

			}
		}
	}
	
	
	
	//judge which player won the game
	public boolean judge() {
		if(player1.getHp()==0 || player2.getHp()==0) {
			this.printScore();
			if(player1.getHp()==0 && player2.getHp()==0) {
				System.out.println("Final Result: Draw");
				JOptionPane.showMessageDialog(null, "Final Result: Draw", "Final Winner", 0);
				return true;				
			}
			else if(player1.getHp()==0) {
				System.out.println("Final Winner: Player2");
				JOptionPane.showMessageDialog(null, "Final Winner: Player2", "Final Winner", 0);
				return true;
			}
			else {
				JOptionPane.showMessageDialog(null, "Final Winner: Player1", "Final Winner", 0);
				return true;
			}
		}else {
			round++;
			System.out.println("PREPARE FOR THE NEXT ROUND!");
			JOptionPane.showMessageDialog(null, "PREPARE FOR THE NEXT ROUND!");
			return false;
		}
	}
	
	//reduce hp of lost player of an round
	public void endRound(int whoWin, Table gameTable) {
		
		if(whoWin==1) {
			player2.setHp(player2.getHp()-50);
			player1.setNumOfWin(player1.getNumOfWin()+1);
			JOptionPane.showMessageDialog(null, "Player1 Win!", round+"round Winner", 0);
			System.out.println("Player1 Win!");
			System.out.println("---------------------------------------------------------------------");
		}
		else if(whoWin==2) {
			player1.setHp(player1.getHp()-50);
			player2.setNumOfWin(player2.getNumOfWin()+1);		
			JOptionPane.showMessageDialog(null, "Player2 Win!", round+"round Winner", 0);
			System.out.println("Player2 Win!");
			System.out.println("---------------------------------------------------------------------");
		}
		else {
			player1.setHp(player1.getHp()-50);
			player2.setHp(player1.getHp()-50);
			System.out.println("Draw!");
			JOptionPane.showMessageDialog(null, "Draw!", round+"round Winner", 0);
			System.out.println("---------------------------------------------------------------------");	
		}
		
		this.clearBoard(gameTable);
		Table.p1movedObj=0;
		Table.p2movedObj=0;
		//Table.p1=0;
		//Table.p2=1;
		gameTable.getGameBoard().player1.setReady(false);
		gameTable.getGameBoard().player2.setReady(false);

	}
	
	public void initialize() {
		player1= new Player();
		player2= new Player();
		storage1= new Storage();
		storage2= new Storage();
		player1.setStorage(storage1);
		player2.setStorage(storage2);
		round=1;
		BoardManager.addAllEntity(this);
	}
	public void changeMaxObj() {
		if(round<2) {
			maxObj=3;
		}
		else if(round<3) {
			maxObj=4;
		}
		else if(round<5) {
			maxObj=5;
		}
		else maxObj=6;
	}


	
	
}
