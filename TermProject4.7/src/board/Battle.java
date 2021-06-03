package board;

import java.util.ArrayList;

import javax.swing.SwingUtilities;

import entity.gameObject;
import gui.Table;

public class Battle extends Thread{
	public static Boolean isFinished=false;
	public static int whoWin=0;
	Board game;
	
	public Battle(Board game) {
		this.game= game;
	}
	public void run() {
		System.out.println("Battle");
		
		ArrayList<Thread> threadList = new ArrayList<>();
		for(int i = 0; i<10; i++)// make thread list.
		{
			for(int j=0;j<10;j++) {
				if(BoardManager.ENTITIES_ONBOARD[i][j]!=null) {
					Thread t = new Thread(BoardManager.ENTITIES_ONBOARD[i][j]);
				    threadList.add(t);
				}
			}
		}
		for(Thread t:threadList) {//start threads
			t.start();
		}
		
		
		while(true) {
			if(Board.player1.getnumOfobj()==0||Board.player2.getnumOfobj()==0) {
				if(Board.player1.getnumOfobj()==0 && (Board.player2.getnumOfobj()==0)) whoWin=3;
				else if (Board.player1.getnumOfobj()==0) whoWin=2;
				else whoWin=1;
				isFinished=true;
				break; // end if all objects of any side are dead
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
		}
		
		
		for(Thread t: threadList) {//stop all threads
			t.interrupt();
		}


	}

}
