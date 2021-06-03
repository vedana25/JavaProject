package entity;
import board.Board;

//common properties of all warriors
public class Warrior extends gameObject{
	int skilldamage; //varies along the level

	public Warrior() {
		setRange(1);
		setAtktime(750);
		setSkillname("Strike");
		setSkilltime(3);
	}

	public void skill(gameObject target) {
		target.setHealth(target.getHealth()-skilldamage);
		Board.printOnlog(this, target, skilldamage, this.getSkillname());
	}
	
}
