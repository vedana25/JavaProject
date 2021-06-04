package board;
//real person player
public class Player {
	private int numOfWin = 0;
	private int hp=100;
	private int numOfobj=0; //the number of objects this player has.
	private boolean isReady=false;
	private boolean isReadyButton = false;
	private Storage storage;
	
	
	//getters and setters
	public int getHp() {
		return hp;
	}
	public void setHp(int hp) {
		this.hp = hp;
	}
	public int getnumOfobj() {
		return numOfobj;
	}
	public void setnumOfobj(int numOfobj) {
		this.numOfobj = numOfobj;
	}
	
	public void setNumOfWin(int num) {
		this.numOfWin = num;
	}
	
	public int getNumOfWin() {
		return this.numOfWin;
		
	}
	public boolean isReady() {
		return isReady;
	}
	public void setReady(boolean isReady) {
		this.isReady = isReady;
	}
	
	public boolean isReadyButton() {
		return isReadyButton;
	}
	public void setReadyButton(boolean isReadyButton) {
		this.isReadyButton = isReadyButton;
	}
	
	public Storage getStorage() {
		return storage;
	}
	public void setStorage(Storage storage) {
		this.storage = storage;
	}
}
