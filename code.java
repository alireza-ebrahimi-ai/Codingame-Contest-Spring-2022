import java.util.ArrayList;
import java.util.Scanner;




/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/


class Vector {
    public final double x, y;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Vector other = (Vector) obj;
        if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x)) return false;
        if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y)) return false;
        return true;
    }

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector(Vector a, Vector b) {
        this.x = b.x - a.x;
        this.y = b.y - a.y;
    }
    
    public double lengthSquared() {
        return x * x + y * y;
    }
    
    public Vector normalize() {
        double length = length();
        if (length == 0)
            return new Vector(0, 0);
        return new Vector(x / length, y / length);
    }
    
    public double length() {
        return Math.sqrt(x * x + y * y);
    }
    
    public Vector mult(double factor) {
        return new Vector(x * factor, y * factor);
    }
}

class Pos<T>{
    T x;
    T y;

    public Pos(T x, T y){
        this.x = x;
        this.y = y;
    }
    
    @SuppressWarnings("unchecked")
	public Pos(Pos<Integer> pos) {
		this.x = (T) pos.x;
		this.y = (T) pos.y;
	}

    public boolean equals(Pos<Integer> other){
        return this.x == other.x && this.y == other.y;
    }

    @Override
    public String toString(){
        return "x: " + x + ", y: " + y;
    }

    
    public static Pos<Integer> getAvgPosOfAllPos(ArrayList<Monster> monstersList) {
		Pos<Integer> res = new Pos<>(0, 0);
		
		for(Monster m : monstersList) {
			res.x += m.pos.x;
			res.y += m.pos.y;
		}
		
		res.x /= monstersList.size();
		res.y /= monstersList.size();
		
		return res;
	}
}

class Base{
    Pos<Integer> pos;
    int health;
    int mana;
    Pos<Integer>[] farDefensePoints;
    Pos<Integer>[] closeDefensePoints;
    Pos<Integer>[] currentDefensePoints;
    Pos<Integer>[] farDefensePointsWhenHaveAttacker;
    Pos<Integer>[] closeDefensePointsWhenHaveAttacker;
    Pos<Integer>[] attackPoints;
    int currentAttackPointIndex = 0;

    static int RADIUS = 5000;

    public Base(int x, int y){
        this.pos = new Pos<Integer>(x, y);

        calcDefensePoints();
    }

    public Base(Base base){
        if((int) base.pos.x == 0) this.pos = new Pos<Integer>(Player.MAX_WIDTH, Player.MAX_HEIGHT);
        else this.pos = new Pos<Integer>(0, 0);
        
        calcAttackPoints();
    }

    private void calcDefensePoints(){
        int DEFENSIVE_CIRCLE_RADIUS = 7300;

        int numberOfDefensePoints = 6;

        farDefensePoints = new Pos[numberOfDefensePoints];
        int index = 0;

        for(int i = 0; i < (numberOfDefensePoints * 4); i++){
            double angle = (Math.PI * 2) * (0.5 + i) / (numberOfDefensePoints * 4);
            int x = (int)(this.pos.x + DEFENSIVE_CIRCLE_RADIUS * Math.cos(angle));
            int y = (int)(this.pos.y + DEFENSIVE_CIRCLE_RADIUS * Math.sin(angle));
            if(x < 0 || y < 0 || x > Player.MAX_WIDTH || y > Player.MAX_HEIGHT) continue;

            farDefensePoints[index] = new Pos<Integer>(x, y);
            index++;
        }

        DEFENSIVE_CIRCLE_RADIUS = 6200;

        closeDefensePoints = new Pos[numberOfDefensePoints];
        index = 0;

        for(int i = 0; i < (numberOfDefensePoints * 4); i++){
            double angle = (Math.PI * 2) * (0.5 + i) / (numberOfDefensePoints * 4);
            int x = (int)(this.pos.x + DEFENSIVE_CIRCLE_RADIUS * Math.cos(angle));
            int y = (int)(this.pos.y + DEFENSIVE_CIRCLE_RADIUS * Math.sin(angle));
            if(x < 0 || y < 0 || x > Player.MAX_WIDTH || y > Player.MAX_HEIGHT) continue;

            closeDefensePoints[index] = new Pos<Integer>(x, y);
            index++;
        }
        
        DEFENSIVE_CIRCLE_RADIUS = 7300;

        numberOfDefensePoints = 4;

        farDefensePointsWhenHaveAttacker = new Pos[6];
        index = 2;

        for(int i = 0; i < (numberOfDefensePoints * 4); i++){
            double angle = (Math.PI * 2) * (0.5 + i) / (numberOfDefensePoints * 4);
            int x = (int)(this.pos.x + DEFENSIVE_CIRCLE_RADIUS * Math.cos(angle));
            int y = (int)(this.pos.y + DEFENSIVE_CIRCLE_RADIUS * Math.sin(angle));
            if(x < 0 || y < 0 || x > Player.MAX_WIDTH || y > Player.MAX_HEIGHT) continue;

            farDefensePointsWhenHaveAttacker[index] = new Pos<Integer>(x, y);
            index++;
        }

        DEFENSIVE_CIRCLE_RADIUS = 6200;

        closeDefensePointsWhenHaveAttacker = new Pos[6];
        index = 2;

        for(int i = 0; i < (numberOfDefensePoints * 4); i++){
            double angle = (Math.PI * 2) * (0.5 + i) / (numberOfDefensePoints * 4);
            int x = (int)(this.pos.x + DEFENSIVE_CIRCLE_RADIUS * Math.cos(angle));
            int y = (int)(this.pos.y + DEFENSIVE_CIRCLE_RADIUS * Math.sin(angle));
            if(x < 0 || y < 0 || x > Player.MAX_WIDTH || y > Player.MAX_HEIGHT) continue;

            closeDefensePointsWhenHaveAttacker[index] = new Pos<Integer>(x, y);
            index++;
        }

        currentDefensePoints = farDefensePoints;
    }

    private void calcAttackPoints(){
        int DEFENSIVE_CIRCLE_RADIUS = RADIUS;

        int numberOfDefensePoints = 6;

        attackPoints = new Pos[numberOfDefensePoints];
        int index = 0;

        for(int i = 0; i < (numberOfDefensePoints * 4); i++){
            double angle = (Math.PI * 2) * (0.5 + i) / (numberOfDefensePoints * 4);
            int x = (int)(this.pos.x + DEFENSIVE_CIRCLE_RADIUS * Math.cos(angle));
            int y = (int)(this.pos.y + DEFENSIVE_CIRCLE_RADIUS * Math.sin(angle));
            if(x < 0 || y < 0 || x > Player.MAX_WIDTH || y > Player.MAX_HEIGHT) continue;

            attackPoints[index] = new Pos<Integer>(x, y);
            index++;
        }
    }
    
    public void updateHealthAndMana(int health, int mana){
        this.health = health;
        this.mana = mana;
    }

    public boolean isMonsterInside(Pos<Integer> pos){
        return Player.getDist(this.pos, pos) <= RADIUS;
    }

    public boolean willMonsterReachBase(Monster monster, double dist){
        if(dist > RADIUS) return false;

        if(this.pos.x == 0){
            double cutYXis = monster.b;
            double cutXXis = -monster.b / monster.m;

            if(cutYXis > RADIUS){
                if(cutXXis > RADIUS){
                    //check if monster is going to myBase on the line or to enemy base
                    if(Math.abs((monster.pos.x + monster.vx) - this.pos.x) > Math.abs(monster.pos.x - this.pos.x)) return false;
                    return true;
                }
                return false;
            }
            else if(cutYXis > 0){
                //check if monster is going to myBase on the line or to enemy base
                if(Math.abs((monster.pos.x + monster.vx) - this.pos.x) > Math.abs(monster.pos.x - this.pos.x)) return false;
                return true;
            }

            
            if(cutXXis > RADIUS) return false;
        }
        else{
            double cutYXis = monster.m * Player.MAX_WIDTH + monster.b;
            double cutXXis = (Player.MAX_HEIGHT - monster.b) / monster.m;

            if(cutYXis < Player.MAX_HEIGHT - RADIUS){
                if(cutXXis < Player.MAX_WIDTH - RADIUS){
                    //check if monster is going to myBase on the line or to enemy base
                    if(Math.abs((monster.pos.x + monster.vx) - this.pos.x) > Math.abs(monster.pos.x - this.pos.x)) return false;
                    return true;
                }
                return false;
            }
            else if(cutYXis < Player.MAX_HEIGHT){
                //check if monster is going to myBase on the line or to enemy base
                if(Math.abs((monster.pos.x + monster.vx) - this.pos.x) > Math.abs(monster.pos.x - this.pos.x)) return false;
                return true;
            }



            if(cutXXis < Player.MAX_WIDTH - RADIUS) return false;
        }

        //check if monster is going to myBase on the line or to enemy base
        if(Math.abs((monster.pos.x + monster.vx) - this.pos.x) > Math.abs(monster.pos.x - this.pos.x)) return false;
        return true;
    }

    @Override
    public String toString(){
        return "Base{" + pos.toString() + ", health: " + health + ", mana: " + mana + "}";
    }
}

class Hero{
    int id;
    Pos<Integer> pos;
    String command;
    int shieldLife;
    boolean isControlled;
    int monsterHandling;
    boolean usingSpell;

    static int DIST_PER_TURN = 800;
    static int DAMAGE_RANGE = 800;
    static int DAMAGE = 2;
    static int WIND_RANGE = 1280;
    static int CONTROL_RANGE = 2200;
    static int SHIELD_RANGE = 2200;

    public Hero(int id, Pos<Integer> p, int shieldLife, boolean isControlled){
        this.id = id;
        this.pos = p;
        this.command = "";
        this.shieldLife = shieldLife;
        this.isControlled = isControlled;
        this.monsterHandling = -1;
        this.usingSpell = false;
    }

	public boolean isAvailable() {
		return this.command == "" && !this.isControlled;
	}

	
	public Pos<Integer> getDefensePoint(Base myBase) {
		
		Pos<Integer> defensePoint = null;
		
		int addToID = 0;
		if(myBase.pos.x != 0) addToID = 3;
		
        if(this.id == 0 + addToID){
            defensePoint = myBase.currentDefensePoints[Player.hero1CurrDefensivePointIndex];
            //if hero is in defense point then switch point
            if(Player.getDist(defensePoint, this.pos) == 0) {
            	Player.hero1CurrDefensivePointIndex++;
            	if(Player.hero1CurrDefensivePointIndex == 2) Player.hero1CurrDefensivePointIndex = 0;
            }
        }
        else if(this.id == 1 + addToID){
            defensePoint = myBase.currentDefensePoints[Player.hero2CurrDefensivePointIndex];
            //if hero is in defense point then switch point
            if(Player.getDist(defensePoint, this.pos) == 0) {
            	Player.hero2CurrDefensivePointIndex++;
            	if(Player.hero2CurrDefensivePointIndex == 4) Player.hero2CurrDefensivePointIndex = 2;
            }
        }
        else if(this.id == 2 + addToID){
        	defensePoint = myBase.currentDefensePoints[Player.hero3CurrDefensivePointIndex];
            //if hero is in defense point then switch point
            if(Player.getDist(defensePoint, this.pos) == 0) {
            	Player.hero3CurrDefensivePointIndex++;
            	if(Player.hero3CurrDefensivePointIndex == 6) Player.hero3CurrDefensivePointIndex = 4;
            }
        }
		
		return defensePoint;
	}
	
	public Pos<Integer> getAttackingPoint(Base opBase) {
		
		Pos<Integer> defensePoint = null;

		defensePoint = opBase.attackPoints[opBase.currentAttackPointIndex];
        //if hero is in defense point then switch point
        if(Player.getDist(defensePoint, this.pos) == 0) {
        	opBase.currentAttackPointIndex++;
        	if(opBase.currentAttackPointIndex == opBase.attackPoints.length) opBase.currentAttackPointIndex = 0;
        }
		
		return defensePoint;
	}

	public Pos<Integer> getPosToGoToActionMonster(Monster closestMonster, int actionRange) {
		
//		System.err.println();
//		System.err.println("in getPosToGoToActionMonster");
//		System.err.println("closestMonster.id: " + closestMonster.id);
//		System.err.println("this.id: " + this.id);
		
		Pos<Integer> pos2 = null;
		
		double dist = Player.getDist(this.pos, closestMonster.pos) - actionRange;
		
		if(dist < 0) return this.pos;
		
//		System.err.println("dist: " + dist);
		
		Pos<Integer> prevGoal = null;
		Pos<Integer> prevPrevPos = null;
		
		while(prevGoal == null || pos2.x - prevGoal.x > 0 || pos2.y - prevGoal.y > 0) {
			int numberOfTurnsPass = (int) Math.floor(dist / DIST_PER_TURN);
//			System.err.println("numberOfTurnsPass: " + numberOfTurnsPass);
			
			Pos<Integer> nextXTurnsPosMonster = closestMonster.getPosNextXTurns(numberOfTurnsPass);
//			System.err.println("nextXTurnsPosMonster: " + nextXTurnsPosMonster);
			
			prevPrevPos = prevGoal;
			prevGoal = pos2;
			pos2 = nextXTurnsPosMonster;
			
			dist = Player.getDist(this.pos, nextXTurnsPosMonster) - actionRange;
			if(dist < 0) return nextXTurnsPosMonster;
			
//			System.err.println("dist: " + dist);
//			System.err.println("prevPrevPos: " + prevPrevPos);
//			System.err.println("prevGoal: " + prevGoal);
//			System.err.println("pos2: " + pos2);
			if(prevPrevPos != null && pos2.x - prevPrevPos.x == 0 && pos2.y - prevPrevPos.y == 0) break;
		}
		
		return pos2;
	}
	
	public boolean canCastShield() {
		return !this.isControlled && Player.myBase.mana >= Player.SPELL_COST && this.shieldLife == 0;
	}
	
	@Override
	public String toString() {
		return "Hero{id: " + id + "}"; 
	}
}

class Monster{
    int id;
    Pos<Integer> pos;
    int health;
    int vx;
    int vy;
    boolean isTargetingBase;
    int threatFor;
    boolean handled;
    int numberOfTurnsToDamageMyBase;
    int shieldLife;
    boolean isControlled;

    double m;
    double b;

    static int DIST_TO_DAMAGE_BASE = 300;
    static int DIST_PER_TURN = 400;

    public Monster(int id, Pos<Integer> p, int health, int vx, int vy, boolean isTargetingBase, int threatFor, Base myBase, int shieldLife, 
    boolean isControlled){
        this.id = id;
        this.pos = p;
        this.health = health;
        this.vx = vx;
        this.vy = vy;
        this.isTargetingBase = isTargetingBase;
        this.threatFor = threatFor;
        this.shieldLife = shieldLife;
        this.isControlled = isControlled;
        this.handled = false;

        //System.err.println("vx: " + vx);
        //System.err.println("vy: " + vy);

        if(vy * vx >= 1) this.m = Math.abs(vy) * 1.0 / Math.abs(vx);
        else this.m = vy * 1.0 / vx;

        this.b = this.pos.y - this.m * this.pos.x;

        this.numberOfTurnsToDamageMyBase = getNumberOfTurnsToDamageBase(myBase);

        //System.err.println("id: " + this.id + " y = " +  this.m + "x + " + this.b);
    }

    public Pos<Integer> getPosNextXTurns(int numberOfTurns) {
    	Pos<Integer> newPos = new Pos<>(this.pos);
    	
    	boolean wasOutsideBase = false;
    	
    	while(!Player.myBase.isMonsterInside(newPos) && numberOfTurns > 0) {
    		newPos.x = newPos.x + this.vx;
    		newPos.y = newPos.y + this.vy;
    		numberOfTurns--;
    		wasOutsideBase = true;
		}
    	
    	if(numberOfTurns == 0) return newPos;
    	
    	if(!wasOutsideBase) {
//    		System.err.println("!wasOutsideBase newPos: " + newPos);
			newPos.x = newPos.x + this.vx * numberOfTurns;
			newPos.y = newPos.y + this.vy * numberOfTurns;
    	}
    	else {
//    		System.err.println("wasOutsideBase newPos: " + newPos);
			
			Pos<Double> velocity = Player.getVelocityPos1ToPos2(newPos, Player.myBase.pos, DIST_PER_TURN);
//			System.err.println("velocity: " + velocity);
			
			newPos.x = (int) Math.round(newPos.x - velocity.x * numberOfTurns);
			newPos.y = (int) Math.round(newPos.y - velocity.y * numberOfTurns);
    	}
    	
//		System.err.println("final newPos: " + newPos);
    	return newPos;
	}

	public boolean isThreatingMyBase(){
        return isTargetingBase && threatFor == 1;
    }

    public boolean isFutureThreatToBase(Base base){
        double dist = minDistFromLineToPoint(-this.m, 1, -this.b, base.pos.x, base.pos.y);

        return base.willMonsterReachBase(this, dist);
    }

    public double minDistFromLineToPoint(double a, int b, double c, int x, int y)
    {
        // Finding the distance of line from center.
        double dist = (Math.abs(a * x + b * y + c)) / 
                        Math.sqrt(a * a + b * b);
        return dist;
    }

    private int getNumberOfTurnsToDamageBase(Base base){

        //if it inside the circle already then just calc dist to base
        double dist = Player.getDist(this.pos, base.pos);
        if(dist <= Base.RADIUS) return (int) Math.ceil((dist - DIST_TO_DAMAGE_BASE) / DIST_PER_TURN);

        dist = minDistFromLineToPoint(-this.m, 1, -this.b, base.pos.x, base.pos.y);

        //monster would never reach my base (unless op will push her with wind)
        if(!base.willMonsterReachBase(this, dist)) return Integer.MAX_VALUE;

        Pos<Integer> center = base.pos;
        Pos<Integer> target = this.pos;

        Pos<Integer> vector = new Pos<Integer>(target.x - center.x, target.y - center.y);
        double length = Math.sqrt(Math.pow(vector.x, 2) + Math.pow(vector.y, 2));
        Pos<Double> normal = new Pos<Double>(vector.x / length, vector.y / length);
        Pos<Double> result = new Pos<Double>(center.x + (normal.x * Base.RADIUS), center.y + (normal.y * Base.RADIUS));
        
        return (int) (Math.ceil(Player.getDistDoubleInteger(result, this.pos) / DIST_PER_TURN) + 
        Math.ceil((Base.RADIUS - DIST_TO_DAMAGE_BASE) / DIST_PER_TURN));
    }

	@Override
	public String toString() {
		return "Monster{" + "" +
		"id: " + id +
		", pos: " + pos +
		", handled: " + handled +
		", isControlled: " + isControlled +
		", numberOfTurnsToDamageMyBase: " + numberOfTurnsToDamageMyBase +
		"}"; 
	}
}


class Player {

    static int MAX_WIDTH = 17630;
    static int MAX_HEIGHT = 9000;

    public static int hero1CurrDefensivePointIndex = 0;
    public static int hero2CurrDefensivePointIndex = 2;
    public static int hero3CurrDefensivePointIndex = 4;

    static int FARM_RANGE = 3000;
    static int SPELL_COST = 10;

    static int nextTurnWindOpAttacker;
    static boolean thereIsOpAttacker;
    static ArrayList<Integer> monstorsUnderMyControlIDs;
    static int gameTurn;
    
    
    static Base myBase, opBase;
    static ArrayList<Monster> monsters, savedMonsters;
    static ArrayList<Hero> myHeros, savedMyHeros, opHeros;
    
    static int turnToStartAttack = 230;


    @SuppressWarnings("unused")
	public static void main(String args[]) {
        @SuppressWarnings("resource")
		Scanner in = new Scanner(System.in);
        int baseX = in.nextInt(); // The corner of the map representing your base
        int baseY = in.nextInt();
        int heroesPerPlayer = in.nextInt(); // Always 3

        myBase = new Base(baseX, baseY);
        opBase = new Base(myBase);

        gameTurn = 0;
        monstorsUnderMyControlIDs = new ArrayList<Integer>();

        // game loop
        while (true) {
            for (int i = 0; i < 2; i++) {
                int health = in.nextInt(); // Each player's base health
                int mana = in.nextInt(); // Ignore in the first league; Spend ten mana to cast a spell
                if(i == 0) myBase.updateHealthAndMana(health, mana);
                else opBase.updateHealthAndMana(health, mana);
            }

            gameTurn++;

            monsters = new ArrayList<>();
            savedMonsters = new ArrayList<>();
            myHeros = new ArrayList<>();
            savedMyHeros = new ArrayList<>();
            opHeros = new ArrayList<>();

            

            int entityCount = in.nextInt(); // Amount of heros and monsters you can see
            for (int i = 0; i < entityCount; i++) {
                int id = in.nextInt(); // Unique identifier
                int type = in.nextInt(); // 0=monster, 1=your hero, 2=opponent hero
                int x = in.nextInt(); // Position of this entity
                int y = in.nextInt();
                int shieldLife = in.nextInt(); // Count down until shield spell fades
                boolean isControlled = in.nextInt() == 1; // Equals 1 when this entity is under a control spell
                int health = in.nextInt(); // Remaining health of this monster
                int vx = in.nextInt(); // Trajectory of this monster
                int vy = in.nextInt();
                boolean isTargetingBase = in.nextInt() == 1; // 0=monster with no target yet, 1=monster targeting a base
                int threatFor = in.nextInt(); // Given this monster's trajectory, is it a threat to 1=your base, 2=your opponent's base, 0=neither
            
            
                switch(type){
                    case 0: monsters.add(new Monster(id, new Pos<Integer>(x, y), health, vx, vy, isTargetingBase, threatFor, myBase, shieldLife, isControlled)); 
                    		savedMonsters.add(new Monster(id, new Pos<Integer>(x, y), health, vx, vy, isTargetingBase, threatFor, myBase, shieldLife, isControlled)); break;
                    case 1: myHeros.add(new Hero(id, new Pos<Integer>(x, y), shieldLife, isControlled)); 
                    		savedMyHeros.add(new Hero(id, new Pos<Integer>(x, y), shieldLife, isControlled)); break;
                    case 2: opHeros.add(new Hero(id, new Pos<Integer>(x, y), shieldLife, isControlled)); break;
                }
            }
            
            Think();

            showOutput(myHeros);
        }
    }
    
    
    private static void Think() {
    	//find opAttacker
        Hero opAttacker = getOpAttacker(opHeros, myBase);
        
        //if there is opAttacker then reduce the farm range and switch to closeDefensePoints
        if(opAttacker != null){
            thereIsOpAttacker = true;
            if(gameTurn < turnToStartAttack) myBase.currentDefensePoints = myBase.closeDefensePoints;
            else myBase.currentDefensePoints = myBase.closeDefensePointsWhenHaveAttacker;
            FARM_RANGE = 2000;
        }
        //if there is no opAttacker then increase the farm range and switch to farDefensePoints
        else if(opAttacker == null){
            thereIsOpAttacker = false;
            if(gameTurn < turnToStartAttack) myBase.currentDefensePoints = myBase.farDefensePoints;
            else myBase.currentDefensePoints = myBase.farDefensePointsWhenHaveAttacker;
            FARM_RANGE = 3000;
        }
        
        int i = 0;
        
        Hero heroAttacker = null;
        
        //Attack
        //attack if gameTurn is equal or more than turnToStartAttack
        if(gameTurn >= turnToStartAttack) {
        	heroAttacker = getHeroAttacker();
        	attackStrategy(heroAttacker);
            i++;
        }
        

        ArrayList<Integer> monstersIdsWhoCannotBeKilled = new ArrayList<Integer>();

        //if one of my heros isControlled or got wind then push op hero attacker with wind
        //TODO:
        for(Hero hero : myHeros){
            if(hero.isControlled && (heroAttacker == null || heroAttacker.id != hero.id)){
                nextTurnWindOpAttacker = 3;
                break;
            }
        }
        
        //Defense
        for(; i < myHeros.size(); i++){

            //push op hero attacker with wind because the op is controlling us
            if(nextTurnWindOpAttacker > 0 && opAttacker != null && i == 0 &&
            	myBase.mana >= SPELL_COST){
            	boolean hasPushOpAttacker = false;
            	
            	if(opAttacker.shieldLife == 0) {
	                for(Hero hero : myHeros){
	                    if(!hero.isAvailable()) continue;
	                    if(!inRange(opAttacker.pos, hero.pos, Hero.WIND_RANGE)) continue;
	                    
	                    hero.command = "SPELL WIND " + " " + opBase.pos.x + " " + opBase.pos.y + " PUSH ATTACKER";
	                    hasPushOpAttacker = true;
	                    break;
	                }
	                
            	}

                if(hasPushOpAttacker){
                	myBase.mana -= SPELL_COST;
                    nextTurnWindOpAttacker--;
                    continue;
                }
                else {
                	boolean hasControlOpAttacker = false;
                	
                	if(opAttacker.shieldLife == 0) {
    	                for(Hero hero : myHeros){
    	                    if(!hero.isAvailable()) continue;
    	                    if(!inRange(opAttacker.pos, hero.pos, Hero.CONTROL_RANGE)) continue;
    	                    
    	                    hero.command = "SPELL CONTROL " + opAttacker.id + " " + opBase.pos.x + " " + opBase.pos.y + " CONTROL ATTACKER";
    	                    hasPushOpAttacker = true;
    	                    break;
    	                }
                	}
                	
                	if(hasControlOpAttacker) {
                    	myBase.mana -= SPELL_COST;
                        nextTurnWindOpAttacker--;
                        continue;
                	}

                	
                	//maybe I need to shield myself instead
                }
            }

            //findMonsterWithSmallestNumberOfTurnsToDamageBase
            Monster closestMonster = findMonsterWithSmallestNumberOfTurnsToDamageBase(monsters, monstersIdsWhoCannotBeKilled);

            //if have no monster that going to damage my base
            if(closestMonster == null){
            	whatToDoWhenThereIsNoThreatFromMonster();
                break;
            }
            
            int smallestNumberOfTurnsToMonster = Integer.MAX_VALUE;
            Hero closestHero = null;
            Pos<Integer> posToGoToACTIONMonster = null;
            int smallestNumberOfTurnsToMonsterWind = Integer.MAX_VALUE;
            int smallestNumberOfTurnsToMonsterControl = Integer.MAX_VALUE;

            //get closest hero to monster to kill it
            for(Hero hero : myHeros){
                if(!hero.isAvailable()) continue;
                
                Pos<Integer> pos = hero.getPosToGoToActionMonster(closestMonster, Hero.DAMAGE_RANGE);
                int numberOfTurnsToReachMonster = (int) Math.ceil((getDist(hero.pos, pos) - Hero.DAMAGE_RANGE) / Hero.DIST_PER_TURN);
                
                if(numberOfTurnsToReachMonster < smallestNumberOfTurnsToMonster){
                    //hero won't be fast enough to kill monster
                    if((closestMonster.numberOfTurnsToDamageMyBase - numberOfTurnsToReachMonster) * Hero.DAMAGE < closestMonster.health){
                    	// check if hero can wind it
                    	pos = hero.getPosToGoToActionMonster(closestMonster, Hero.WIND_RANGE);
                    	int numberOfTurnsToReachMonsterWind = (int) Math.round((getDist(hero.pos, pos) - Hero.WIND_RANGE) / Hero.DIST_PER_TURN);
                    	if(numberOfTurnsToReachMonsterWind < 0) numberOfTurnsToReachMonsterWind = 0;
                        
                        if(myBase.mana >= SPELL_COST && closestMonster.shieldLife - numberOfTurnsToReachMonsterWind <= 0 &&
                        closestMonster.numberOfTurnsToDamageMyBase > numberOfTurnsToReachMonsterWind
                        && smallestNumberOfTurnsToMonsterWind > numberOfTurnsToReachMonsterWind){
                        	smallestNumberOfTurnsToMonsterWind = numberOfTurnsToReachMonsterWind;
                        	smallestNumberOfTurnsToMonster = numberOfTurnsToReachMonster;
                            closestHero = hero;
                            posToGoToACTIONMonster = pos;
                        }
                        else {
                        	pos = hero.getPosToGoToActionMonster(closestMonster, Hero.CONTROL_RANGE);
                        	int numberOfTurnsToReachMonsterControl = (int) Math.round((getDist(hero.pos, pos) - Hero.CONTROL_RANGE) / Hero.DIST_PER_TURN);
                        	if(numberOfTurnsToReachMonsterControl < 0) numberOfTurnsToReachMonsterControl = 0;
                            
                            if(myBase.mana >= SPELL_COST && closestMonster.shieldLife - numberOfTurnsToReachMonsterControl <= 0 &&
                               closestMonster.numberOfTurnsToDamageMyBase > numberOfTurnsToReachMonsterControl + 1 &&
                               smallestNumberOfTurnsToMonsterControl > numberOfTurnsToReachMonsterControl) {
                            	smallestNumberOfTurnsToMonsterControl = numberOfTurnsToReachMonsterControl;
                            	smallestNumberOfTurnsToMonster = numberOfTurnsToReachMonster;
                                closestHero = hero;
                                posToGoToACTIONMonster = pos;
                            }
                        	else{
                            	if(closestMonster.numberOfTurnsToDamageMyBase > numberOfTurnsToReachMonster) {
    	                            smallestNumberOfTurnsToMonster = numberOfTurnsToReachMonster;
    	                            closestHero = hero;
    	                            posToGoToACTIONMonster = pos;
                            	}
                            	//if not then hero can't reach monster before monster damage base
                            }
                        }
                    }
                    else{
                        smallestNumberOfTurnsToMonster = numberOfTurnsToReachMonster;
                        closestHero = hero;
                        posToGoToACTIONMonster = pos;
                    }
                }
            }
            

            if(closestHero == null){ //there is no hero that can stop the monster from damaging the base
            	System.err.println("closestMonster.id cannot be killed: " + closestMonster.id);
            	monstersIdsWhoCannotBeKilled.add(closestMonster.id);
            	
            	//if there are already heros that tried to kill the monster
            	for(Hero hero : myHeros) {
            		if(hero.command != "" && hero.monsterHandling == closestMonster.id) {
            			hero.command = "";
	           			hero.monsterHandling = -1;
	           			if(hero.usingSpell) myBase.mana += SPELL_COST;
	           			hero.usingSpell = false;
            			i--;
            		}
            	}
            	
                //re-search what hero can do
                i--;
                continue;
            }
            
            //check if just damage will be enough
            if((closestMonster.numberOfTurnsToDamageMyBase - smallestNumberOfTurnsToMonster) * Hero.DAMAGE < closestMonster.health){
            	//check if can reach monster and wind it
            	if(smallestNumberOfTurnsToMonsterWind == 0 && myBase.mana >= SPELL_COST && closestMonster.shieldLife == 0 &&
            	inRange(closestHero.pos, closestMonster.pos, Hero.WIND_RANGE)) {
            		closestHero.command = "SPELL WIND " + opBase.pos.x + " " + opBase.pos.y + " WIND MONSTER";
            		closestMonster.handled = true;
            		myBase.mana -= SPELL_COST;
            		closestHero.monsterHandling = closestMonster.id;
            		closestHero.usingSpell = true;
            	}
            	//I will be able to cast wind but not now so until then go to her 
            	else if(smallestNumberOfTurnsToMonsterWind < 100 && myBase.mana >= SPELL_COST && closestMonster.shieldLife - smallestNumberOfTurnsToMonster <= 0) {
            		closestHero.command = "MOVE " + posToGoToACTIONMonster.x + " " + posToGoToACTIONMonster.y + " WILL WIND MONSTER";
            		closestMonster.handled = true;
            		closestHero.monsterHandling = closestMonster.id;
            	}
            	else if(smallestNumberOfTurnsToMonsterWind > 100 &&
            	smallestNumberOfTurnsToMonsterControl == 0 && myBase.mana >= SPELL_COST && closestMonster.shieldLife == 0 &&
                inRange(closestHero.pos, closestMonster.pos, Hero.CONTROL_RANGE)) {
            		closestHero.command = "SPELL CONTROL " + closestMonster.id + " " + opBase.pos.x + " " + opBase.pos.y;
            		closestMonster.handled = true;
            		myBase.mana -= SPELL_COST;
            		closestHero.monsterHandling = closestMonster.id;
            		closestHero.usingSpell = true;
            	}
            	//I will be able to cast control but not now so until then go to her 
            	else if(smallestNumberOfTurnsToMonsterWind > 100 && smallestNumberOfTurnsToMonsterControl < 100 && myBase.mana >= SPELL_COST && closestMonster.shieldLife - smallestNumberOfTurnsToMonster <= 0) {
            		closestHero.command = "MOVE " + posToGoToACTIONMonster.x + " " + posToGoToACTIONMonster.y + " WILL CONTROL MONSTER";
            		closestMonster.handled = true;
            		closestHero.monsterHandling = closestMonster.id;
            	}
	            else{
	            	
	            	//This hero by himself can't kill the monster but maybe more heros will be able to
	           		 closestHero.command = "MOVE " + posToGoToACTIONMonster.x + " " + posToGoToACTIONMonster.y + " CANT KILL ALONE";
	           		 //remove the life from monster that the hero will damage
	           		 closestMonster.health -= (closestMonster.numberOfTurnsToDamageMyBase - smallestNumberOfTurnsToMonster) * Hero.DAMAGE;
	           		 //if all the heros tried to kill the monster but can't
	           		 //then add the monster id to monstersIdsWhoCannotBeKilled
	           		 //and start the main loop again
	           		 closestHero.monsterHandling = closestMonster.id;
	           		 if(i + 1 == myHeros.size()) {
	           			 System.err.println("closestMonster.id everyone cannot be killed: " + closestMonster.id);
		           		 for(Hero hero : myHeros) {
		           			 if(hero.monsterHandling == closestMonster.id) {
			           			 hero.command = "";
			           			 hero.monsterHandling = -1;
			           			 if(hero.usingSpell) myBase.mana += SPELL_COST;
			           			 hero.usingSpell = false;
		           			 }
		           		 }
		           		 monstersIdsWhoCannotBeKilled.add(closestMonster.id);
		           		 i = -1;
	           		 }
	           		 continue;
	            }
            }
            else{
            	// if monster is inside myBase and I can wind it and
            	//the monster has more than Hero.DAMAGE life then do it
                if(myBase.mana >= 150 && closestMonster.shieldLife == 0 &&
                myBase.isMonsterInside(closestMonster.pos) && closestMonster.health > Hero.DAMAGE &&
                inRange(closestHero.pos, closestMonster.pos, Hero.WIND_RANGE)){
                    closestHero.command = "SPELL WIND " + opBase.pos.x + " " + opBase.pos.y;
                    myBase.mana -= SPELL_COST;
                    closestMonster.handled = true;
                    closestHero.monsterHandling = closestMonster.id;
                    closestHero.usingSpell = true;
                }
                else{
                	
                	//Pos<Integer> getBestPosToDamageMonster = simBestPos(closestHero, posToGoToACTIONMonster, closestMonster, Hero.DAMAGE_RANGE);
                	
                	closestHero.monsterHandling = closestMonster.id;
                	closestHero.command = "MOVE " + posToGoToACTIONMonster.x + " " + posToGoToACTIONMonster.y;
                	closestMonster.handled = true;
                }
            }
        }
    }
    
    
//    private static Pos<Integer> simBestPos(Hero hero, Pos<Integer> posMonsterInFuture, Monster monster, int ACTION_RANGE) {
//		int numberOfSimulations = 6;
//		Pos<Integer>[] goalPointsToSim = new Pos[numberOfSimulations];
//		
//		int index = 0;
//
//		//get optional goal points
//        for(int i = 0; i < (numberOfSimulations * 4); i++){
//            double angle = (Math.PI * 2) * (0.5 + i) / (numberOfSimulations * 4);
//            int x = (int)(posMonsterInFuture.x + ACTION_RANGE * Math.cos(angle));
//            int y = (int)(posMonsterInFuture.y + ACTION_RANGE * Math.sin(angle));
//            if(x < 0 || y < 0 || x > Player.MAX_WIDTH || y > Player.MAX_HEIGHT) continue;
//
//            goalPointsToSim[index] = new Pos<Integer>(x, y);
//            index++;
//        }
//        
//        Hero savedHero = null;
//        for(Hero hero2 : savedMyHeros) {
//        	if(hero2.id == hero.id) {
//        		savedHero = hero2;
//        		break;
//        	}
//        }
//        
//        double bestSimEval = Double.MIN_VALUE;
//        Pos<Integer> bestPosToGo = null;
//        
//        //sim for every goalPos and find the best
//        for(Pos<Integer> goalPos : goalPointsToSim) {
//        	//sim and get eval
//        	double eval = simBestPos(hero, goalPos, ACTION_RANGE, 0);
//        	
//        	if(eval > bestSimEval) {
//        		bestSimEval = eval;
//        		bestPosToGo = goalPos;
//        	}
//        	
//        	//reset monsters values and hero values
//        	for(int i = 0; i < monsters.size(); i++) {
//        		monsters.get(i).resetValues(savedMonsters.get(i));
//        	}
//        	
//        	hero.resetValues(savedHero);
//        }
//        
//        return bestPosToGo;
//	}
//
//    /*
//     *  doControl();
//        doShield();
//        moveHeroes();
//        Map<Player, Integer[]> manaGain = performCombat();
//        doPush();
//        moveMobs();
//        shieldDecay();
//        spawnNewMobs(turn);
//     */
//    private static double simBestPos(Hero hero, Pos<Integer> goalPos, int ACTION_RANGE, double currentEval) {
//    	//move hero
//    	
//    	
//		//damage monsters around hero
//    	
//    	//move monsters
//	}
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    




	


	


	private static void attackStrategy(Hero heroAttacker) {
    	
    	if(!heroAttacker.isAvailable()) return;
        	
    	//if see monster that going to enter op base then shield it
    	for(Monster monster : monsters) {
    		if(monster.shieldLife == 0 && myBase.mana >= SPELL_COST &&
    		monster.isFutureThreatToBase(opBase) &&
    		monster.health >= 14 &&
    		inRange(monster.pos, heroAttacker.pos, Hero.SHIELD_RANGE) &&
    		inRange(monster.pos, opBase.pos, Base.RADIUS)) {
    			heroAttacker.command = "SPELL SHIELD " + monster.id;
    			myBase.mana -= 10;
    			break;
    		}
    	}
        	
        if(!heroAttacker.isAvailable()) return;
        
    	//walk around base
        Pos<Integer> attackPoint = heroAttacker.getAttackingPoint(opBase);

        heroAttacker.command = "MOVE " + attackPoint.x + " " + attackPoint.y + " Attack Point";
	}


	private static Hero getHeroAttacker() {
    	for(Hero hero : myHeros) {
    		if(hero.id == 0 || hero.id == 3)
    			return hero;
    	}
    	
    	return null;
	}


	private static void whatToDoWhenThereIsNoThreatFromMonster() {
    	for(Hero hero : myHeros){
            if(!hero.isAvailable()) continue;

            Pos<Integer> defensePoint = hero.getDefensePoint(myBase);
            
            Pos<Integer> bestPosToFarm = getBestPosToFarm(defensePoint);

            //move to farm monster
            if(bestPosToFarm != null) hero.command = "MOVE " + bestPosToFarm.x + " " + bestPosToFarm.y + " FARM";
            //move to defensive point
            else hero.command = "MOVE " + defensePoint.x + " " + defensePoint.y + " Defense Point";
        }
	}
    
    public static Pos<Integer> bestPosToFarm;
    public static int mostMonstersToFarmAtOnce;
    
    private static Pos<Integer> getBestPosToFarm(Pos<Integer> defensePoint){
    	
    	bestPosToFarm = null;
    	mostMonstersToFarmAtOnce = 0;
		
    	getBestPosToFarm(defensePoint, 0, new ArrayList<>());
	    
	    return bestPosToFarm;
    }
    
    private static void getBestPosToFarm(Pos<Integer> defensePoint, int i, ArrayList<Monster> res) {
    	
    	if(res.size() > mostMonstersToFarmAtOnce) {
    		mostMonstersToFarmAtOnce = res.size();
    		bestPosToFarm = Pos.getAvgPosOfAllPos(res);
    	}
    	
    	for(; i < monsters.size(); i++) {
    		Monster monster = monsters.get(i);
    		
			//if have this monster already
			if(res.contains(monster)) continue;

    		if(monster.handled || monster.isFutureThreatToBase(opBase) || 
	        !inRange(monster.pos, defensePoint, FARM_RANGE) ||
	        monstorsUnderMyControlIDs.contains(monster.id)) continue;
    		
    		boolean addMonster = true;
    		
    		for(Monster monsterInList : res) {
    			if(!inRange(monster.pos, monsterInList.pos, Hero.DAMAGE_RANGE * 2 - 5)) {
    				addMonster = false;
    				break;
    			}
    		}
    		
    		if(addMonster) {
    			res.add(monster);
    			getBestPosToFarm(defensePoint, i + 1, res);
    			res.remove(res.size() - 1);
    		}
    	}
    }

    
	private static Monster findMonsterWithSmallestNumberOfTurnsToDamageBase(ArrayList<Monster> monsters, ArrayList<Integer> monstersIdsWhoCannotBeKilled) {
        double smallestNumberOfTurnsToDamageBase = Double.MAX_VALUE;
        Monster closestMonster = null;

        //find the monster with the smallestNumberOfTurnsToDamageBase
        for(Monster monster : monsters){
            if(monster.handled || monster.numberOfTurnsToDamageMyBase > 100 || monstersIdsWhoCannotBeKilled.contains(monster.id)
            || monstorsUnderMyControlIDs.contains(monster.id) ||
            monster.numberOfTurnsToDamageMyBase >= smallestNumberOfTurnsToDamageBase) continue;

            smallestNumberOfTurnsToDamageBase = monster.numberOfTurnsToDamageMyBase;
            closestMonster = monster;
        }
        
        return closestMonster;
	}

	public static Pos<Double> getVelocityPos1ToPos2(Pos<Integer> posToFindVel, Pos<Integer> posToGo, int DIST_PER_TURN) {
		Vector v = new Vector(new Vector(posToFindVel.x, posToFindVel.y), new Vector(posToGo.x, posToGo.y));
        Vector velocity;
        if (v.lengthSquared() <= DIST_PER_TURN * DIST_PER_TURN) velocity = v;
        else velocity = v.normalize().mult(DIST_PER_TURN);
        Pos<Double> res = new Pos<Double>(0.0, 0.0);
        res.x = velocity.x;
        res.y = velocity.y;
        return res;
        
//		int deltaX = posToFindVel.x - posToGo.x;
//		int deltaY = posToFindVel.y - posToGo.y;
		
//		double angle = Math.atan(Math.abs(deltaY) * 1.0 / Math.abs(deltaX));
//		System.err.println("angle: " + Math.toDegrees(angle));
//		System.err.println("Math.cos(angle): " + Math.cos(angle));
//		System.err.println("Math.sin(angle): " + Math.sin(angle));
//		System.err.println("Math.cos(angle) * DIST_PER_TURN: " + (Math.cos(angle) * DIST_PER_TURN));
//		System.err.println("Math.sin(angle) * DIST_PER_TURN: " + (Math.sin(angle) * DIST_PER_TURN));
		
//		Pos<Double> res = new Pos<Double>(Math.cos(angle) * DIST_PER_TURN, Math.sin(angle) * DIST_PER_TURN);
//		
//		if(deltaX > 0) res.x *= -1;
//		if(deltaY > 0) res.y *= -1;
//		
//		return res;
	}
	
    private static boolean inRange(Pos<Integer> pos1, Pos<Integer> pos2, int range) {
    	return (pos1.x - pos2.x) * (pos1.x - pos2.x) + (pos1.y - pos2.y) * (pos1.y - pos2.y) <= range * range;
    }
    
	public static double getDist(Pos<Integer> pos, Pos<Integer> pos2){
        return Math.sqrt(Math.pow(pos.x - pos2.x, 2) + Math.pow(pos.y - pos2.y, 2));
    }

    public static double getDistDoubleInteger(Pos<Double> pos, Pos<Integer> pos2){
        return Math.sqrt(Math.pow(pos.x - pos2.x, 2) + Math.pow(pos.y - pos2.y, 2));
    }

    private static Hero getOpAttacker(ArrayList<Hero> opHeros, Base myBase){
        for(Hero opHero : opHeros){
            //if there is opHero close to my base then return him
            if(getDist(opHero.pos, myBase.pos) <= Base.RADIUS + 3000) return opHero;
        }

        return null;
    }

    public static void showOutput(ArrayList<Hero> heros){

        //update monstorsUnderMyControlIDs arrayList
        for(int i = monstorsUnderMyControlIDs.size() - 1; i > -1; i-=2){
            if(monstorsUnderMyControlIDs.get(i) == gameTurn * 1000 - 1000){
                monstorsUnderMyControlIDs.remove(i);
                monstorsUnderMyControlIDs.remove(i - 1);
            }
        }

        for(Hero hero : heros) {
        	if(hero.command == "") hero.command = "WAIT";
        	System.out.println(hero.command);
        }
    }
}



/*

TODO:

Easy:
 [X] Put the heros in defensive circle around the base
 [X] the control is happening twice on same monster sometimes but shouldn't, because the vx and vy of the monster is not updated
    I should save the id of the monster I control which are not inside my base and not allow my heros to control them again

Medium:
 [X] make 2 defense points for each hero which the hero will go between them when standing in defense point
 [] maybe push monsters with wind out of the base to get more wild mana
 [X] don't move toward the current location of the monster but toward the closest location where the hero will damage her
 [X] what I think that I can't stop monster is just with 1 hero but I need also to check if I can stop it with 2 or even 3 (depending if I have attacker or not)
 [] don't farm near other hero defense point unless he is busy
 [] if op attacker can wind monster to damage my base right away and I can wind or shield her then do it

 [] find the closestMonster location when reach to her and not the pos I need to be in
 	and after this try to find if there are monster near it so I can damage them toegther by standing between them

Hard:
 [X] Farm mana
 [X] Farm mana from 2 or more monster in same time by standing between them
 [] don't allow monsters to get near attackers at all! (https://www.codingame.com/share-replay/624283053)

 [] Think of attacking strategy

*/
