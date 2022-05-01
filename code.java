import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;




/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/


class Vector {
    public double x;
	public double y;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

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

    public Vector(double angle) {
        this.x = Math.cos(angle);
        this.y = Math.sin(angle);
    }

    public Vector(Vector vector) {
		this.x = vector.x;
		this.y = vector.y;
	}

	public Vector rotate(double angle) {
        double nx = (x * Math.cos(angle)) - (y * Math.sin(angle));
        double ny = (x * Math.sin(angle)) + (y * Math.cos(angle));

        return new Vector(nx, ny);
    };

    public boolean equals(Vector v) {
        return v.x == x && v.y == y;
    }

    public Vector round() {
        return new Vector((int) Math.round(this.x), (int) Math.round(this.y));
    }

    public Vector truncate() {
        return new Vector((int) this.x, (int) this.y);
    }

    public double distance(Vector v) {
        return Math.sqrt((v.x - x) * (v.x - x) + (v.y - y) * (v.y - y));
    }

    public boolean inRange(Vector v, double range) {
        return (v.x - x) * (v.x - x) + (v.y - y) * (v.y - y) <= range * range;
    }
    
    public void selfAdd(Vector v) {
    	x += v.x;
    	y += v.y;
    }

    public Vector add(Vector v) {
        return new Vector(x + v.x, y + v.y);
    }

    public Vector mult(double factor) {
        return new Vector(x * factor, y * factor);
    }

    public Vector sub(Vector v) {
        return new Vector(this.x - v.x, this.y - v.y);
    }

    public double length() {
        return Math.sqrt(x * x + y * y);
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

    public double dot(Vector v) {
        return x * v.x + y * v.y;
    }

    public double angle() {
        return Math.atan2(y, x);
    }

    @Override
    public String toString() {
        return "[" + x + ", " + y + "]";
    }

    public String toIntString() {
        return (int) x + " " + (int) y;
    }

    public Vector project(Vector force) {
        Vector normalize = this.normalize();
        return normalize.mult(normalize.dot(force));
    }

    public final Vector cross(double s) {
        return new Vector(-s * y, s * x);
    }

    public Vector hsymmetric(double center) {
        return new Vector(2 * center - this.x, this.y);
    }

    public Vector vsymmetric(double center) {
        return new Vector(this.x, 2 * center - this.y);
    }

    public Vector vsymmetric() {
        return new Vector(this.x, -this.y);
    }

    public Vector hsymmetric() {
        return new Vector(-this.x, this.y);
    }

    public Vector symmetric() {
        return symmetric(new Vector(0, 0));
    }

    public Vector symmetric(Vector center) {
        return new Vector(center.x * 2 - this.x, center.y * 2 - this.y);
    }

    public boolean withinBounds(double minx, double miny, double maxx, double maxy) {
        return x >= minx && x <= maxx && y >= miny && y <= maxy;
    }

    public boolean isZero() {
        return x == 0 && y == 0;
    }

    public Vector symmetricTruncate(Vector origin) {
        return sub(origin).truncate().add(origin);
    }

    public static Vector getAvgVectorOfAllVectors(ArrayList<Monster> monstersList) {
    	Vector res = new Vector(0, 0);
		
		for(Monster m : monstersList) {
			res.selfAdd(m.pos);
		}
		
		res.x /= monstersList.size();
		res.y /= monstersList.size();
		
		return res;
	}
}


class Base{
	Vector pos;
    int health;
    int mana;
    Vector[] farDefensePoints;
    Vector[] closeDefensePoints;
    Vector[] currentDefensePoints;
    Vector[] farDefensePointsWhenHaveAttacker;
    Vector[] closeDefensePointsWhenHaveAttacker;
    Vector[] attackPoints;
    Vector[] controlMonstersAttackPoints;
    int currentAttackPointIndex = 0;

    static int RADIUS = 5000;

    public Base(int x, int y){
        this.pos = new Vector(x, y);

        calcDefensePoints();
    }

    public Base(Base base){
        if((int) base.pos.x == 0) this.pos = new Vector(Player.MAP_WIDTH, Player.MAP_HEIGHT);
        else this.pos = new Vector(0, 0);
        
        calcAttackPoints();
    }

    private void calcDefensePoints(){
        int DEFENSIVE_CIRCLE_RADIUS = 7300;

        int numberOfDefensePoints = 6;

        farDefensePoints = new Vector[numberOfDefensePoints];
        int index = 0;

        for(int i = 0; i < (numberOfDefensePoints * 4); i++){
            double angle = (Math.PI * 2) * (0.5 + i) / (numberOfDefensePoints * 4);
            int x = (int)(this.pos.x + DEFENSIVE_CIRCLE_RADIUS * Math.cos(angle));
            int y = (int)(this.pos.y + DEFENSIVE_CIRCLE_RADIUS * Math.sin(angle));
            if(x < 0 || y < 0 || x > Player.MAP_WIDTH || y > Player.MAP_HEIGHT) continue;

            farDefensePoints[index] = new Vector(x, y);
            index++;
        }

        DEFENSIVE_CIRCLE_RADIUS = 6200;

        closeDefensePoints = new Vector[numberOfDefensePoints];
        index = 0;

        for(int i = 0; i < (numberOfDefensePoints * 4); i++){
            double angle = (Math.PI * 2) * (0.5 + i) / (numberOfDefensePoints * 4);
            int x = (int)(this.pos.x + DEFENSIVE_CIRCLE_RADIUS * Math.cos(angle));
            int y = (int)(this.pos.y + DEFENSIVE_CIRCLE_RADIUS * Math.sin(angle));
            if(x < 0 || y < 0 || x > Player.MAP_WIDTH || y > Player.MAP_HEIGHT) continue;

            closeDefensePoints[index] = new Vector(x, y);
            index++;
        }
        
        DEFENSIVE_CIRCLE_RADIUS = 6500;

        numberOfDefensePoints = 4;

        farDefensePointsWhenHaveAttacker = new Vector[6];
        index = 2;

        for(int i = 0; i < (numberOfDefensePoints * 4); i++){
            double angle = (Math.PI * 2) * (0.5 + i) / (numberOfDefensePoints * 4);
            int x = (int)(this.pos.x + DEFENSIVE_CIRCLE_RADIUS * Math.cos(angle));
            int y = (int)(this.pos.y + DEFENSIVE_CIRCLE_RADIUS * Math.sin(angle));
            if(x < 0 || y < 0 || x > Player.MAP_WIDTH || y > Player.MAP_HEIGHT) continue;

            farDefensePointsWhenHaveAttacker[index] = new Vector(x, y);
            index++;
        }

        DEFENSIVE_CIRCLE_RADIUS = 5000;

        closeDefensePointsWhenHaveAttacker = new Vector[6];
        index = 2;

        for(int i = 0; i < (numberOfDefensePoints * 4); i++){
            double angle = (Math.PI * 2) * (0.5 + i) / (numberOfDefensePoints * 4);
            int x = (int)(this.pos.x + DEFENSIVE_CIRCLE_RADIUS * Math.cos(angle));
            int y = (int)(this.pos.y + DEFENSIVE_CIRCLE_RADIUS * Math.sin(angle));
            if(x < 0 || y < 0 || x > Player.MAP_WIDTH || y > Player.MAP_HEIGHT) continue;

            closeDefensePointsWhenHaveAttacker[index] = new Vector(x, y);
            index++;
        }

        currentDefensePoints = farDefensePoints;
    }

    private void calcAttackPoints(){
        int ATTACK_CIRCLE_RADIUS = RADIUS;

        int numberOfAttackPoints = 3;

        attackPoints = new Vector[numberOfAttackPoints];
        int index = 0;

        for(int i = 0; i < (numberOfAttackPoints * 4); i++){
            double angle = (Math.PI * 2) * (0.5 + i) / (numberOfAttackPoints * 4);
            int x = (int)(this.pos.x + ATTACK_CIRCLE_RADIUS * Math.cos(angle));
            int y = (int)(this.pos.y + ATTACK_CIRCLE_RADIUS * Math.sin(angle));
            if(x < 0 || y < 0 || x > Player.MAP_WIDTH || y > Player.MAP_HEIGHT) continue;

            attackPoints[index] = new Vector(x, y);
            index++;
        }
        
        ATTACK_CIRCLE_RADIUS = RADIUS - 100;

        numberOfAttackPoints = 3;

        controlMonstersAttackPoints = new Vector[numberOfAttackPoints];
        index = 0;

        for(int i = 0; i < (numberOfAttackPoints * 4); i++){
            double angle = (Math.PI * 2) * (0.5 + i) / (numberOfAttackPoints * 4);
            int x = (int)(this.pos.x + ATTACK_CIRCLE_RADIUS * Math.cos(angle));
            int y = (int)(this.pos.y + ATTACK_CIRCLE_RADIUS * Math.sin(angle));
            if(x < 0 || y < 0 || x > Player.MAP_WIDTH || y > Player.MAP_HEIGHT) continue;

            controlMonstersAttackPoints[index] = new Vector(x, y);
            index++;
        }
    }
    
    public void updateHealthAndMana(int health, int mana){
        this.health = health;
        this.mana = mana;
    }

    public boolean isMonsterInside(Vector pos){
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
            double cutYXis = monster.m * Player.MAP_WIDTH + monster.b;
            double cutXXis = (Player.MAP_HEIGHT - monster.b) / monster.m;

            if(cutYXis < Player.MAP_HEIGHT - RADIUS){
                if(cutXXis < Player.MAP_WIDTH - RADIUS){
                    //check if monster is going to myBase on the line or to enemy base
                    if(Math.abs((monster.pos.x + monster.vx) - this.pos.x) > Math.abs(monster.pos.x - this.pos.x)) return false;
                    return true;
                }
                return false;
            }
            else if(cutYXis < Player.MAP_HEIGHT){
                //check if monster is going to myBase on the line or to enemy base
                if(Math.abs((monster.pos.x + monster.vx) - this.pos.x) > Math.abs(monster.pos.x - this.pos.x)) return false;
                return true;
            }



            if(cutXXis < Player.MAP_WIDTH - RADIUS) return false;
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
    Vector pos;
    String command;
    int shieldLife;
    boolean isControlled;
    HashSet<Integer> monstersHandling;
    boolean usingSpell;

    static int DIST_PER_TURN = 800;
    static int DAMAGE_RANGE = 800;
    static int DAMAGE = 2;
    static int WIND_RANGE = 1280;
    static int CONTROL_RANGE = 2200;
    static int SHIELD_RANGE = 2200;
    public static final int WIND_PUSH_RANGE = 2200;

    public Hero(int id, Vector p, int shieldLife, boolean isControlled){
        this.id = id;
        this.pos = p;
        this.command = "";
        this.shieldLife = shieldLife;
        this.isControlled = isControlled;
        this.monstersHandling = new HashSet<>();
        this.usingSpell = false;
    }

	public boolean isAvailable() {
		return this.command == "" && !this.isControlled;
	}

	
	public Vector getDefensePoint(Base myBase) {
		
		Vector defensePoint = null;
		
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
	
	public Vector getAttackingPoint(Base opBase) {
		
		Vector defensePoint = null;

		defensePoint = opBase.attackPoints[opBase.currentAttackPointIndex];
        //if hero is in defense point then switch point
        if(Player.getDist(defensePoint, this.pos) <= Hero.DIST_PER_TURN) {
        	opBase.currentAttackPointIndex++;
        	if(opBase.currentAttackPointIndex == opBase.attackPoints.length) opBase.currentAttackPointIndex = 0;
        }
		
		return defensePoint;
	}

	public Vector getPosToGoToActionMonster(Monster closestMonster, int actionRange) {
		
//		System.err.println();
//		System.err.println("in getPosToGoToActionMonster");
//		System.err.println("closestMonster.id: " + closestMonster.id);
//		System.err.println("this.id: " + this.id);
		
		Vector pos2 = null;
		
		double dist = Player.getDist(this.pos, closestMonster.pos) - actionRange;
		
		if(dist < 0) return this.pos;
		
//		System.err.println("dist: " + dist);
		
		Vector prevGoal = null;
		Vector prevPrevPos = null;
		
		while(prevGoal == null || pos2.x - prevGoal.x > 0 || pos2.y - prevGoal.y > 0) {
			int numberOfTurnsPass = (int) Math.ceil(dist / DIST_PER_TURN);
//			System.err.println("numberOfTurnsPass: " + numberOfTurnsPass);
			
			Vector nextXTurnsPosMonster = closestMonster.getPosNextXTurns(numberOfTurnsPass);
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
		return "Hero{id: " + id + " pos: " + this.pos.toIntString() + "}"; 
	}

	
	public void resetValues(Hero savedHero) {

		this.pos = new Vector(savedHero.pos);
		this.monstersHandling = new HashSet<>();
	}

	public void resetMonstersHandling() {
		for(int i = 0; i < Player.monsters.size(); i++) {
			Monster monster = Player.monsters.get(i);
			if(monstersHandling.contains(monster.id)) {
				monster.resetValues(Player.savedMonsters.get(i));
			}
		}
	}
}

class Monster{
    int id;
    Vector pos;
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

    public Monster(int id, Vector p, int health, int vx, int vy, boolean isTargetingBase, int threatFor, Base myBase, int shieldLife, 
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

    public Vector getPosNextXTurns(int numberOfTurns) {
    	Vector newPos = new Vector(this.pos);
    	
    	boolean wasOutsideBase = false;
    	
    	while(!Player.myBase.isMonsterInside(newPos) && numberOfTurns > 0) {
    		newPos = newPos.add(new Vector(this.vx, this.vy)).symmetricTruncate(Player.symmetryOrigin);
    		numberOfTurns--;
    		wasOutsideBase = true;
		}
    	
    	if(numberOfTurns == 0) return newPos;
    	
    	if(!wasOutsideBase) return newPos.add(new Vector(this.vx * numberOfTurns, this.vy * numberOfTurns)).symmetricTruncate(Player.symmetryOrigin);
    	else {
//    		System.err.println("wasOutsideBase newPos: " + newPos);
			
			Vector velocity = Player.getVelocityPos1ToPos2(newPos, Player.myBase.pos, DIST_PER_TURN);
//			System.err.println("velocity: " + velocity);
			
			return newPos.add(velocity.mult(numberOfTurns)).symmetricTruncate(Player.symmetryOrigin);
    	}
	}

	public boolean isThreatingMyBase(){
        return isTargetingBase && threatFor == 1;
    }

    public boolean isFutureThreatToBase(Base base){
        double dist = minDistFromLineToPoint(-this.m, 1, -this.b, base.pos.x, base.pos.y);

        return base.willMonsterReachBase(this, dist);
    }

    public double minDistFromLineToPoint(double a, int b, double c, double x, double y)
    {
        // Finding the distance of line from center.
        double dist = (Math.abs(a * x + b * y + c)) / 
                        Math.sqrt(a * a + b * b);
        return dist;
    }

    private int getNumberOfTurnsToDamageBase(Base base){

        //if it inside the circle already then just calc dist to base
        double dist = Player.getDist(this.pos, base.pos);
        int add1IfControlledByMe = 0;
        if(this.isControlled && Player.monstorsUnderMyControlIDs.contains(this.id)) add1IfControlledByMe = 1;
        if(dist <= Base.RADIUS) return (int) Math.ceil((dist - DIST_TO_DAMAGE_BASE) / DIST_PER_TURN) + add1IfControlledByMe;

        dist = minDistFromLineToPoint(-this.m, 1, -this.b, base.pos.x, base.pos.y);

        //monster would never reach my base (unless op will push her with wind)
        if(!base.willMonsterReachBase(this, dist)) return Integer.MAX_VALUE;

        Vector center = base.pos;
        Vector target = this.pos;

        Vector vector = new Vector(target.x - center.x, target.y - center.y).normalize();
        Vector result = new Vector(center.x + (vector.x * Base.RADIUS), center.y + (vector.y * Base.RADIUS));
        
        return (int) (Math.ceil(Player.getDist(result, this.pos) / DIST_PER_TURN) + 
        Math.ceil((Base.RADIUS - DIST_TO_DAMAGE_BASE) / DIST_PER_TURN));
    }

	@Override
	public String toString() {
		return "Monster{" + "" +
		"id: " + id +
		", pos: " + pos +
		", handled: " + handled +
		", isControlled: " + isControlled +
		", health: " + health +
		", numberOfTurnsToDamageMyBase: " + numberOfTurnsToDamageMyBase +
		"}"; 
	}

	
	public void resetValues(Monster monster) {
		this.health = monster.health;
		this.pos = new Vector(monster.pos);
		this.handled = false;
		this.shieldLife = monster.shieldLife;
	}

	public void changePos(Monster monster) {
		this.pos = new Vector(monster.pos);
	}
}


class Player {

    static int MAP_WIDTH = 17630;
    static int MAP_HEIGHT = 9000;
    
    static Vector symmetryOrigin = new Vector(MAP_WIDTH / 2, MAP_HEIGHT / 2);

    public static int hero1CurrDefensivePointIndex = 0;
    public static int hero2CurrDefensivePointIndex = 2;
    public static int hero3CurrDefensivePointIndex = 4;

    static int FARM_RANGE = 3000;
    static int SPELL_COST = 10;

    static int nextTurnWindOpAttacker;
    static boolean thereIsOpAttacker;
    static ArrayList<Integer> monstorsUnderMyControlIDs;
    static int gameTurn;
    
    static boolean attacking = false;
    
    
    static Base myBase, opBase;
    static ArrayList<Monster> monsters, savedMonsters;
    static ArrayList<Hero> myHeros, savedMyHeros, opHeros;
    
    static int turnToStartAttack = 100;
    
    static int numberOfTurnsToSim = 15;
    
    static ArrayList<Integer> monstersIdsWhoCannotBeKilled;
    
    static Hero opAttacker;


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
                    case 0: monsters.add(new Monster(id, new Vector(x, y), health, vx, vy, isTargetingBase, threatFor, myBase, shieldLife, isControlled)); 
                    		savedMonsters.add(new Monster(id, new Vector(x, y), health, vx, vy, isTargetingBase, threatFor, myBase, shieldLife, isControlled)); break;
                    case 1: myHeros.add(new Hero(id, new Vector(x, y), shieldLife, isControlled)); 
                    		savedMyHeros.add(new Hero(id, new Vector(x, y), shieldLife, isControlled)); break;
                    case 2: opHeros.add(new Hero(id, new Vector(x, y), shieldLife, isControlled)); break;
                }
            }
            
            Think();

            showOutput(myHeros);
        }
    }
    
    
    private static void Think() {
    	//find opAttacker
        opAttacker = getOpAttacker(opHeros, myBase);
        
        Hero heroAttacker = null;
        
        int i = 0;
        
        //Attack
        //attack if gameTurn is equal or more than turnToStartAttack
        if(myBase.mana >= 320 || attacking || gameTurn > turnToStartAttack) {
        	heroAttacker = getHeroAttacker();
        	if(!inRange(myBase.pos, heroAttacker.pos, Base.RADIUS)) {
	        	heroAttacker.command = "Attacking";
	        	i++;
        	}
        	
        	attacking = true;
        }
        
        //if there is opAttacker then reduce the farm range and switch to closeDefensePoints
        if(opAttacker != null) {
        	numberOfTurnsToSim = 8;
        	whatToDoWhenHaveOpAttacker(opAttacker, (heroAttacker != null && heroAttacker.command.equals("Attacking")));
        }
        //if there is no opAttacker then increase the farm range and switch to farDefensePoints
        else if(opAttacker == null) {
        	numberOfTurnsToSim = 15;
        	whatToDoWhenNotHaveOpAttacker((heroAttacker != null && heroAttacker.command.equals("Attacking")));
        }
        

        

        monstersIdsWhoCannotBeKilled = new ArrayList<Integer>();
        
        //if one of my heros isControlled or got wind then push op hero attacker with wind
        nextTurnWindOpAttacker = shouldWindOrControlOpAttacker(heroAttacker);
        
        //Defense
        for(; i < myHeros.size(); i++){
        	
        	if((i == 0 || (heroAttacker != null && i == 1)) && opAttacker != null) {
	        	//if opAttacker can just push monster to damage my base then shield this monster
	        	Monster monsterToShield = getMonsterToShieldSoOpAttackerWontPushItToDamageMyBase(opAttacker);
	        	boolean hasShield = tryShieldMonsterFromOpAttacker(monsterToShield);
	        	if(hasShield) continue;
        	}

            //push op hero attacker with wind because the op is controlling us
            if(nextTurnWindOpAttacker > 0 && opAttacker != null && i + 1 == myHeros.size() &&
            	myBase.mana >= SPELL_COST){
            	boolean hasPushOpAttacker = false;
            	
            	if(opAttacker.shieldLife == 0) {
	                for(Hero hero : myHeros){
	                    if(!hero.isAvailable()) continue;
	                    if(!inRange(opAttacker.pos, hero.pos, Hero.WIND_RANGE)) continue;
	                    
	                    hero.command = "SPELL WIND " + " " + opBase.pos.toIntString() + " PUSH ATTACKER";
	                    hasPushOpAttacker = true;
	                    myBase.mana -= SPELL_COST;
	                    hero.usingSpell = true;
	                    break;
	                }
	                
            	}

                if(hasPushOpAttacker){
                    nextTurnWindOpAttacker--;
                    continue;
                }
                else {
                	boolean hasControlOpAttacker = false;
                	
                	if(opAttacker.shieldLife == 0) {
    	                for(Hero hero : myHeros){
    	                    if(!hero.isAvailable()) continue;
    	                    if(!inRange(opAttacker.pos, hero.pos, Hero.CONTROL_RANGE)) continue;
    	                    
    	                    hero.command = "SPELL CONTROL " + opAttacker.id + " " + opBase.pos.toIntString() + " CONTROL ATTACKER";
    	                    hasPushOpAttacker = true;
    	                    myBase.mana -= SPELL_COST;
    	                    hero.usingSpell = true;
    	                    break;
    	                }
                	}
                	
                	if(hasControlOpAttacker) {
                        nextTurnWindOpAttacker--;
                        continue;
                	}

                	
                	//maybe I need to shield myself instead
                }
            }

            //findMonsterWithSmallestNumberOfTurnsToDamageBase
            Monster closestMonster = findMonsterWithSmallestNumberOfTurnsToDamageBase(monsters);
            
            //if have no monster that going to damage my base
            if(closestMonster == null){
            	whatToDoWhenThereIsNoThreatFromMonster();
                break;
            }
            
            int smallestNumberOfTurnsToReachMonster = Integer.MAX_VALUE;
            Hero closestHero = null;
            Vector posToGoToACTIONMonster = null;
            int smallestNumberOfTurnsToMonsterWind = Integer.MAX_VALUE;
            int smallestNumberOfTurnsToMonsterControl = Integer.MAX_VALUE;

            //get closest hero to monster to kill it
            for(Hero hero : myHeros){
                if(!hero.isAvailable()) continue;
                
                Vector pos = hero.getPosToGoToActionMonster(closestMonster, Hero.DAMAGE_RANGE);
                int numberOfTurnsToReachMonster = (int) Math.ceil((getDist(hero.pos, pos) - Hero.DAMAGE_RANGE) / Hero.DIST_PER_TURN);
                if(numberOfTurnsToReachMonster < 0) numberOfTurnsToReachMonster = 0;
                
                
                
                if(numberOfTurnsToReachMonster < smallestNumberOfTurnsToReachMonster){
                    //hero won't be fast enough to kill monster
                    if((closestMonster.numberOfTurnsToDamageMyBase - numberOfTurnsToReachMonster) * Hero.DAMAGE < closestMonster.health){
                    	// check if hero can wind it
                    	pos = hero.getPosToGoToActionMonster(closestMonster, Hero.WIND_RANGE);
                    	int numberOfTurnsToReachMonsterWind = (int) Math.ceil((getDist(hero.pos, pos) - Hero.WIND_RANGE) / Hero.DIST_PER_TURN);
                    	if(numberOfTurnsToReachMonsterWind < 0) numberOfTurnsToReachMonsterWind = 0;
                        
                        if(myBase.mana >= SPELL_COST && closestMonster.shieldLife - numberOfTurnsToReachMonsterWind <= 0 &&
                        closestMonster.numberOfTurnsToDamageMyBase > numberOfTurnsToReachMonsterWind &&
                        smallestNumberOfTurnsToMonsterWind > numberOfTurnsToReachMonsterWind){
                        	smallestNumberOfTurnsToMonsterWind = numberOfTurnsToReachMonsterWind;
                        	smallestNumberOfTurnsToReachMonster = numberOfTurnsToReachMonster;
                            closestHero = hero;
                            posToGoToACTIONMonster = pos;
                        }
                        else {
                        	pos = hero.getPosToGoToActionMonster(closestMonster, Hero.CONTROL_RANGE);
                        	int numberOfTurnsToReachMonsterControl = (int) Math.ceil((getDist(hero.pos, pos) - Hero.CONTROL_RANGE) / Hero.DIST_PER_TURN);
                        	if(numberOfTurnsToReachMonsterControl < 0) numberOfTurnsToReachMonsterControl = 0;
                            
                            if(myBase.mana >= SPELL_COST && closestMonster.shieldLife - numberOfTurnsToReachMonsterControl <= 0 &&
                               closestMonster.numberOfTurnsToDamageMyBase > numberOfTurnsToReachMonsterControl + 1 &&
                               smallestNumberOfTurnsToMonsterControl > numberOfTurnsToReachMonsterControl) {
                            	smallestNumberOfTurnsToMonsterControl = numberOfTurnsToReachMonsterControl;
                            	smallestNumberOfTurnsToReachMonster = numberOfTurnsToReachMonster;
                                closestHero = hero;
                                posToGoToACTIONMonster = pos;
                            }
                        	else{
                            	if(closestMonster.numberOfTurnsToDamageMyBase > numberOfTurnsToReachMonster) {
    	                            smallestNumberOfTurnsToReachMonster = numberOfTurnsToReachMonster;
    	                            closestHero = hero;
    	                            posToGoToACTIONMonster = pos;
                            	}
                            	//if not then hero can't reach monster before monster damage base
                            }
                        }
                    }
                    else{
                        smallestNumberOfTurnsToReachMonster = numberOfTurnsToReachMonster;
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
            		if(hero.command != "" && hero.monstersHandling.contains(closestMonster.id)) {
            			hero.command = "";
            			hero.resetMonstersHandling();
	           			hero.monstersHandling = new HashSet<>();
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
            if((closestMonster.numberOfTurnsToDamageMyBase - smallestNumberOfTurnsToReachMonster) * Hero.DAMAGE < closestMonster.health){
            	//check if can reach monster and wind it
            	if(smallestNumberOfTurnsToMonsterWind < 100) {
            		if(smallestNumberOfTurnsToMonsterWind == 0 && myBase.mana >= SPELL_COST && closestMonster.shieldLife == 0 &&
            		inRange(closestHero.pos, closestMonster.pos, Hero.WIND_RANGE)) windMonster(closestHero, posToGoToACTIONMonster, smallestNumberOfTurnsToMonsterWind);
            		else goToMonsterToWindIt(closestHero, posToGoToACTIONMonster, smallestNumberOfTurnsToMonsterWind, closestMonster);
            	}
            	//check if can reach monster and control it
            	else if(smallestNumberOfTurnsToMonsterControl < 100) {
            		if(smallestNumberOfTurnsToMonsterControl == 0 && myBase.mana >= SPELL_COST && closestMonster.shieldLife == 0 &&
            		inRange(closestHero.pos, closestMonster.pos, Hero.CONTROL_RANGE)) controlMonster(closestHero, posToGoToACTIONMonster, smallestNumberOfTurnsToMonsterControl, closestMonster);
            		else goToMonsterToControlIt(closestHero, posToGoToACTIONMonster, smallestNumberOfTurnsToMonsterControl, closestMonster);
            	}
            	//This hero by himself can't kill the monster but maybe more heros will be able to
	            else{
	           		 closestHero.command = "MOVE " + posToGoToACTIONMonster.toIntString() + " CANT KILL ALONE";
	           		 
	           		int numberOfTurnsToActionMonster = closestMonster.numberOfTurnsToDamageMyBase - 1;
//	           		System.err.println("numberOfTurnsToActionMonster4: " + numberOfTurnsToActionMonster);
	           		
	           		simBestPos(closestHero, posToGoToACTIONMonster, Hero.DAMAGE_RANGE, numberOfTurnsToActionMonster, closestMonster.id);
	           		
	           		
	           		for(int j = 0; j < monsters.size(); j++) {
	           			monsters.get(j).changePos(savedMonsters.get(j));
	           			monsters.get(j).shieldLife = savedMonsters.get(j).shieldLife;
	           		}
	           		
	           		 //if all the heros tried to kill the monster but can't
	           		 //then add the monster id to monstersIdsWhoCannotBeKilled
	           		 //and start the main loop again
	           		 closestHero.monstersHandling.add(closestMonster.id);
	           		 if(i + 1 == myHeros.size()) {
	           			 System.err.println("closestMonster.id everyone cannot be killed: " + closestMonster.id);
		           		 for(Hero hero : myHeros) {
		           			 if(hero.monstersHandling.contains(closestMonster.id)) {
			           			hero.command = "";
			           			hero.resetMonstersHandling();
			           			hero.monstersHandling = new HashSet<>();
			           			if(hero.usingSpell) myBase.mana += SPELL_COST;
			           			hero.usingSpell = false;
		           			 }
		           		 }
		           		 monstersIdsWhoCannotBeKilled.add(closestMonster.id);
		           		 if(heroAttacker != null && heroAttacker.command.equals("Attacking")) i = 0;
		           		 else i = -1;
	           		 }
	            }
            }
            else{
            	
            	int rangeFromRangeToPushMonster = Base.RADIUS;
            	if(opAttacker != null && inRange(closestMonster.pos, opAttacker.pos, Hero.DIST_PER_TURN * 2 + Hero.WIND_RANGE)) rangeFromRangeToPushMonster = Base.RADIUS + 2000;
            	// if monster is inside myBase and I can wind it and
            	//the monster has more than Hero.DAMAGE life then do it

//            	if(myBase.mana >= 100 && closestMonster.shieldLife == 0 && opAttacker != null && !inRange(closestMonster.pos, opAttacker.pos, Hero.DIST_PER_TURN * 3 + Hero.WIND_RANGE) &&
//                inRange(myBase.pos, closestMonster.pos, Base.RADIUS + 1500) && closestMonster.health > Hero.DAMAGE &&
//                inRange(closestHero.pos, closestMonster.pos, Hero.CONTROL_RANGE)) controlMonsterOutOfMyBase(closestHero, posToGoToACTIONMonster, closestMonster);
            	if(myBase.mana >= 100 && closestMonster.shieldLife == 0 &&
                inRange(myBase.pos, closestMonster.pos, rangeFromRangeToPushMonster) && closestMonster.health > Hero.DAMAGE * 2 &&
                inRange(closestHero.pos, closestMonster.pos, Hero.WIND_RANGE)) windMonsterOutOfMyBase(closestHero, posToGoToACTIONMonster, smallestNumberOfTurnsToReachMonster);
                else damageMonster(closestHero, posToGoToACTIONMonster, smallestNumberOfTurnsToReachMonster, closestMonster);
            }
            
//            monsters.forEach(monster -> {
//            	System.err.println("monster: " + monster);
//            });
//            
//            myHeros.forEach(hero -> {
//            	System.err.println("hero: " + hero);
//            	System.err.println("hero.monstersHandling: " + hero.monstersHandling.toString());
//            });
        }
        
        if(heroAttacker != null && heroAttacker.command.equals("Attacking")) {
	        heroAttacker.command = "";
	        attackStrategy(heroAttacker);
	        
	        prevPosMyAttacker = heroAttacker.pos;
        }
        
        
    }
    

    private static boolean tryShieldMonsterFromOpAttacker(Monster monsterToShield) {
    	if(monsterToShield == null) return false;
    	
		System.err.println("monsterToShield: " + monsterToShield);
		
		for(Hero hero : myHeros) {
			if(!hero.isAvailable() || !inRange(hero.pos, monsterToShield.pos, Hero.SHIELD_RANGE)) continue;
			
    		hero.command = "SPELL SHIELD " + monsterToShield.id + " SHIELD!";
    		myBase.mana -= SPELL_COST;
    		monsterToShield.handled = true;
    		hero.monstersHandling.add(monsterToShield.id);
    		hero.usingSpell = true;
    		return true;
		}
		
		return false;
	}


	private static void controlMonsterOutOfMyBase(Hero closestHero, Vector posToGoToACTIONMonster,
			Monster closestMonster) {
		// TODO Auto-generated method stub
    	
    	simBestPos(closestHero, posToGoToACTIONMonster, Hero.CONTROL_RANGE, 0, -1);
		  
		closestHero.command = "SPELL CONTROL " + closestMonster.id + " " + opBase.pos.toIntString();
		myBase.mana -= SPELL_COST;
		closestHero.usingSpell = true;
	}


	private static void damageMonster(Hero closestHero, Vector posToGoToACTIONMonster,
			int smallestNumberOfTurnsToReachMonster, Monster closestMonster) {
    	//Vector getBestPosToDamageMonster = simBestPos(closestHero, posToGoToACTIONMonster, closestMonster, Hero.DAMAGE_RANGE);
    	//System.err.println("getBestPosToDamageMonster: " + getBestPosToDamageMonster);
    	
    	int numberOfTurnsToActionMonster = smallestNumberOfTurnsToReachMonster + (int) Math.ceil(closestMonster.health * 1.0 / Hero.DAMAGE);
    	
    	if(opAttacker != null) numberOfTurnsToActionMonster = Math.min(numberOfTurnsToActionMonster, 5);
    	
//    	System.err.println("closestMonster.health * 1.0 / Hero.DAMAGE: " + (closestMonster.health * 1.0 / Hero.DAMAGE));
//    	System.err.println("(int) Math.ceil(closestMonster.health * 1.0 / Hero.DAMAGE): " + ((int) Math.ceil(closestMonster.health * 1.0 / Hero.DAMAGE)));
//    	
//    	System.err.println("numberOfTurnsToActionMonster: " + numberOfTurnsToActionMonster);
//    	System.err.println("closestMonster: " + closestMonster);
//    	System.err.println("closestHero: " + closestHero);
    	
    	simBestPos(closestHero, posToGoToACTIONMonster, Hero.DAMAGE_RANGE, numberOfTurnsToActionMonster, closestMonster.id);
    	
    	for(int monsterIndex = 0; monsterIndex < monsters.size(); monsterIndex++) {
    		monsters.get(monsterIndex).changePos(savedMonsters.get(monsterIndex));
    		monsters.get(monsterIndex).shieldLife = savedMonsters.get(monsterIndex).shieldLife;
    	}
    	
    	closestHero.command = "MOVE " + posToGoToACTIONMonster.toIntString();
//    	closestHero.monstersHandling.add(closestMonster.id);
//    	closestMonster.handled = true;
	}


	private static void windMonsterOutOfMyBase(Hero closestHero, Vector posToGoToACTIONMonster,
			int smallestNumberOfTurnsToReachMonster) {
		
		simBestPos(closestHero, posToGoToACTIONMonster, Hero.WIND_RANGE, 0, -1);
		  
		for(int monsterIndex = 0; monsterIndex < monsters.size(); monsterIndex++) {
			monsters.get(monsterIndex).changePos(savedMonsters.get(monsterIndex));
			monsters.get(monsterIndex).shieldLife = savedMonsters.get(monsterIndex).shieldLife;
		}
		  
		closestHero.command = "SPELL WIND " + opBase.pos.toIntString();
		myBase.mana -= SPELL_COST;
		closestHero.usingSpell = true;
	}


	private static void goToMonsterToControlIt(Hero closestHero, Vector posToGoToACTIONMonster,
			int smallestNumberOfTurnsToMonsterControl, Monster closestMonster) {
		
		int numberOfTurnsToActionMonster = smallestNumberOfTurnsToMonsterControl;
		
		if(opAttacker != null) numberOfTurnsToActionMonster = Math.min(numberOfTurnsToActionMonster, 5);
		
//		System.err.println("before sim 3: " + closestMonster);
		simBestPos(closestHero, posToGoToACTIONMonster, Hero.CONTROL_RANGE, numberOfTurnsToActionMonster, closestMonster.id);
//    	System.err.println("after sim 3: " + closestMonster);
		
		for(int monsterIndex = 0; monsterIndex < monsters.size(); monsterIndex++) {
    		monsters.get(monsterIndex).changePos(savedMonsters.get(monsterIndex));
    		monsters.get(monsterIndex).shieldLife = savedMonsters.get(monsterIndex).shieldLife;
    	}
		
		closestHero.command = "MOVE " + posToGoToACTIONMonster.toIntString() + " WILL CONTROL MONSTER";
	}


	private static void controlMonster(Hero closestHero, Vector posToGoToACTIONMonster,
			int smallestNumberOfTurnsToMonsterControl, Monster closestMonster) {
		
		
//		System.err.println("before sim 4: " + closestMonster);
		simBestPos(closestHero, posToGoToACTIONMonster, Hero.CONTROL_RANGE, 0, -1);
//		System.err.println("after sim 4: " + closestMonster);
		
		for(int monsterIndex = 0; monsterIndex < monsters.size(); monsterIndex++) {
    		monsters.get(monsterIndex).changePos(savedMonsters.get(monsterIndex));
    		monsters.get(monsterIndex).shieldLife = savedMonsters.get(monsterIndex).shieldLife;
    	}
		
		closestHero.command = "SPELL CONTROL " + closestMonster.id + " " + opBase.pos.toIntString();
		myBase.mana -= SPELL_COST;
		closestHero.usingSpell = true;
	}


	private static void goToMonsterToWindIt(Hero closestHero, Vector posToGoToACTIONMonster,
			int smallestNumberOfTurnsToMonsterWind, Monster closestMonster) {
		
		int numberOfTurnsToActionMonster = smallestNumberOfTurnsToMonsterWind;
		
		if(opAttacker != null) numberOfTurnsToActionMonster = Math.min(numberOfTurnsToActionMonster, 5);
		
//		System.err.println("before sim 5: " + closestMonster);
		simBestPos(closestHero, posToGoToACTIONMonster, Hero.WIND_RANGE, numberOfTurnsToActionMonster, closestMonster.id);
//		System.err.println("after sim 5: " + closestMonster);
		
		for(int monsterIndex = 0; monsterIndex < monsters.size(); monsterIndex++) {
    		monsters.get(monsterIndex).changePos(savedMonsters.get(monsterIndex));
    		monsters.get(monsterIndex).shieldLife = savedMonsters.get(monsterIndex).shieldLife;
    	}
		
		closestHero.command = "MOVE " + posToGoToACTIONMonster.toIntString() + " WILL WIND MONSTER";
	}


	private static void windMonster(Hero closestHero, Vector posToGoToACTIONMonster,
			int smallestNumberOfTurnsToMonsterWind) {
//		System.err.println("before sim 6: " + closestMonster);
		simBestPos(closestHero, posToGoToACTIONMonster, Hero.WIND_RANGE, 0, -1);
//		System.err.println("after sim 6: " + closestMonster);
		
		for(int monsterIndex = 0; monsterIndex < monsters.size(); monsterIndex++) {
    		monsters.get(monsterIndex).changePos(savedMonsters.get(monsterIndex));
    		monsters.get(monsterIndex).shieldLife = savedMonsters.get(monsterIndex).shieldLife;
    	}
		
		closestHero.command = "SPELL WIND " + opBase.pos.toIntString() + " WIND MONSTER";
		myBase.mana -= SPELL_COST;
		closestHero.usingSpell = true;
	}

	
	
	
	
	
	
	

	private static Monster getMonsterToShieldSoOpAttackerWontPushItToDamageMyBase(Hero opAttacker) {
    	Monster monsterToDefend = null;
    	
		for(Monster monster : monsters) {
			Vector nextPos = stepTo(monster.pos, myBase.pos, Monster.DIST_PER_TURN);
			if(monster.shieldLife == 0 && inRange(nextPos, opAttacker.pos, Hero.WIND_RANGE) &&
			   inRange(nextPos, myBase.pos, Hero.WIND_PUSH_RANGE + Monster.DIST_TO_DAMAGE_BASE)) {
				if(monsterToDefend == null) monsterToDefend = monster;
				else return null;
			}
		}
		
		return monsterToDefend;
	}

	
    private static Vector simBestPos(Hero hero, Vector posMonsterInFuture, Monster monster, int ACTION_RANGE) {
		int numberOfSimulations = 6;
		Vector[] goalPointsToSim = new Vector[numberOfSimulations + 1];
		
		int index = 0;

		//get optional goal points
        for(int i = 0; i < numberOfSimulations; i++){
            double angle = (Math.PI * 2) * (0.5 + i) / numberOfSimulations;
            int x = (int)(posMonsterInFuture.x + ACTION_RANGE * Math.cos(angle));
            int y = (int)(posMonsterInFuture.y + ACTION_RANGE * Math.sin(angle));
            if(x < 0 || y < 0 || x > Player.MAP_WIDTH || y > Player.MAP_HEIGHT) continue;

            goalPointsToSim[index] = new Vector(x, y);
            index++;
        }
        
        goalPointsToSim[numberOfSimulations] = posMonsterInFuture;
        
        Hero savedHero = null;
        for(Hero hero2 : savedMyHeros) {
        	if(hero2.id == hero.id) {
        		savedHero = hero2;
        		break;
        	}
        }
        
        double bestSimEval = Double.MIN_VALUE;
        Vector bestPosToGo = null;
        
        boolean wasInside = false;
        
        //sim for every goalPos and find the best
        for(Vector goalPos : goalPointsToSim) {
        	System.err.println("in goalPos: " + goalPos);
        	if(goalPos == null) continue;
        	
        	wasInside = true;
        	
        	//sim and get eval
        	double eval = simBestPos(hero, goalPos, ACTION_RANGE, 1, -1);
        	
        	System.err.println("eval: " + eval);
        	
        	if(eval > bestSimEval) {
        		bestSimEval = eval;
        		bestPosToGo = goalPos;
        		System.err.println("bestSimEval: " + bestSimEval);
        		System.err.println("bestPosToGo: " + bestPosToGo);
        	}
        	
        	//reset monsters values and hero values
        	for(int i = 0; i < monsters.size(); i++) {
        		monsters.get(i).resetValues(savedMonsters.get(i));
        	}
        	
        	hero.resetValues(savedHero);
        }
        
        System.err.println("wasInside: " + wasInside);
        
        //System.err.println("end bestSimEval: " + bestSimEval);
		//System.err.println("end bestPosToGo: " + bestPosToGo);
        
        return bestPosToGo;
	}

    /*
     *  doControl();
        doShield();
        moveHeroes();
        Map<Player, Integer[]> manaGain = performCombat();
        doPush();
        moveMobs();
        shieldDecay();
        spawnNewMobs(turn);
     */
    private static double simBestPos(Hero hero, Vector goalPos, int ACTION_RANGE, int numberOfTurnsToReachActionMonster, int targetMonsterID) {

    	double addToEval = 0;
    	
    	boolean finishSim = numberOfTurnsToReachActionMonster == 0; //hero is in goalPos
    	
    	if(!finishSim) {
			//damage monsters around hero
	    	for(Monster monster : monsters) {
	    		if(monster.handled) continue;
	    		
	    		if(inRange(monster.pos, hero.pos, Hero.DAMAGE_RANGE)) {
	    			addToEval += 2;
	    			monster.health -= Hero.DAMAGE;
	    			if(monster.health <= 0) { //monster killed
	    				addToEval += 20;
	    				monster.handled = true;
	    				hero.monstersHandling.add(monster.id);
	    				if(targetMonsterID == monster.id) { //hero has killed the monster he wanted

	    				}
	    			}
	    		}
	    	}
    	}
    	else {
    		if(ACTION_RANGE == Hero.DAMAGE_RANGE) {
    			//damage monsters around hero
    	    	for(Monster monster : monsters) {
    	    		if(monster.health <= 0) continue;
    	    		
    	    		if(inRange(monster.pos, hero.pos, Hero.DAMAGE_RANGE)) {
    	    			addToEval += 2;
    	    			monster.health -= Hero.DAMAGE;
    	    			if(monster.health <= 0) { //monster killed
    	    				addToEval += 20;
    	    				monster.handled = true;
    	    				hero.monstersHandling.add(monster.id);
    	    			}
    	    		}
    	    	}
    		}
    		else if(ACTION_RANGE == Hero.WIND_RANGE) {
    			//damage monsters around hero
    	    	for(Monster monster : monsters) {
    	    		if(monster.health <= 0) continue;
    	    		
    	    		if(monster.shieldLife == 0 && inRange(monster.pos, hero.pos, Hero.WIND_RANGE)) {
    	    			addToEval += 10;
    	    			monster.handled = true;
    	    			hero.monstersHandling.add(monster.id);
    	    		}
    	    	}
    		}
    		else if(ACTION_RANGE == Hero.CONTROL_RANGE) {
    			addToEval += 10;
    			//damage monsters around hero
    	    	for(Monster monster : monsters) {
    	    		if(monster.health <= 0) continue;
    	    		
    	    		if(monster.shieldLife == 0 && inRange(monster.pos, hero.pos, Hero.CONTROL_RANGE)) {
    	    			addToEval += 10;
    	    			monster.handled = true;
    	    			hero.monstersHandling.add(monster.id);
    	    			monstorsUnderMyControlIDs.add(monster.id);
    	    			monstorsUnderMyControlIDs.add(gameTurn * 1000);
    	    		}
    	    	}
    		}
    	}
    	
    	if(finishSim) return addToEval;
    	
    	//move monsters
    	for(Monster monster : monsters) {
    		if(monster.handled || monster.health <= 0) continue;
    		
    		monster.pos = monster.getPosNextXTurns(1);
    		if(targetMonsterID == monster.id) {
    	    	goalPos = hero.getPosToGoToActionMonster(monster, ACTION_RANGE);
    		}
    		if(monster.shieldLife > 0) monster.shieldLife -= 1;
    	}
    	
    	if(hero.pos == null) System.err.println("hero.pos: " + hero.pos);
    	else if(goalPos == null) System.err.println("goalPos: " + goalPos);

    	//move hero
    	hero.pos = stepTo(hero.pos, goalPos, Hero.DIST_PER_TURN);
	
    	return addToEval + simBestPos(hero, goalPos, ACTION_RANGE, numberOfTurnsToReachActionMonster - 1, targetMonsterID) ;
    }
    
    
    
    
    
    private static void whatToDoWhenHaveOpAttacker(Hero opAttacker, boolean hasAnAttacker) {
    	//if there is opAttacker then reduce the farm range and switch to closeDefensePoints
        thereIsOpAttacker = true;
        if(!hasAnAttacker) myBase.currentDefensePoints = myBase.closeDefensePoints;
        else myBase.currentDefensePoints = myBase.closeDefensePointsWhenHaveAttacker;
        FARM_RANGE = 2000;
        if(inRange(opAttacker.pos, myBase.pos, Base.RADIUS + 1000)) FARM_RANGE = 500;
    }
    
    private static void whatToDoWhenNotHaveOpAttacker(boolean hasAnAttacker) {
    	//if there is no opAttacker then increase the farm range and switch to farDefensePoints
    	 thereIsOpAttacker = false;
         if(!hasAnAttacker) myBase.currentDefensePoints = myBase.farDefensePoints;
         else myBase.currentDefensePoints = myBase.farDefensePointsWhenHaveAttacker;
         FARM_RANGE = 3000;
    }
    
    private static int shouldWindOrControlOpAttacker(Hero heroAttacker) {
        //if one of my heros isControlled or got wind then push op hero attacker with wind
        //TODO:
        for(Hero hero : myHeros){
            if(hero.isControlled && (heroAttacker == null || heroAttacker.id != hero.id)){
                return 3;
            }
        }
    	
    	return nextTurnWindOpAttacker;
    }
    
    
    private static boolean isControllingMe = false;
    private static Vector prevPosMyAttacker = null;

	private static void attackStrategy(Hero heroAttacker) {
		
//		System.err.println("have heroAttacker");


      	//if can push monster to immedately damage opBase
    	for(Monster monster : monsters) {
    		if(monster.shieldLife == 0 && myBase.mana >= SPELL_COST &&
    				monster.health >= 2 &&
    				inRange(monster.pos, heroAttacker.pos, Hero.WIND_RANGE) &&
    				inRange(stepTo(monster.pos, opBase.pos, Hero.WIND_PUSH_RANGE), opBase.pos, Monster.DIST_TO_DAMAGE_BASE)) {
    			heroAttacker.command = "SPELL WIND " + opBase.pos.toIntString();
    			myBase.mana -= SPELL_COST;
    			return;
    		}
    	}
		
		if(heroAttacker.isControlled) isControllingMe = true;

		//if opDefenders are controlling me
		if(heroAttacker.shieldLife == 0 && isControllingMe && myBase.mana >= SPELL_COST * 3) {
			heroAttacker.command = "SPELL SHIELD " + heroAttacker.id;
			myBase.mana -= SPELL_COST;
			isControllingMe = false;
			return;
		}

		
		//if opDefenders are winding me
		if(heroAttacker.shieldLife == 0 && myBase.mana >= SPELL_COST * 3 && prevPosMyAttacker != null && getDist(prevPosMyAttacker, heroAttacker.pos) > Hero.DIST_PER_TURN + 100) {
			heroAttacker.command = "SPELL SHIELD " + heroAttacker.id;
			myBase.mana -= SPELL_COST;
			return;
		}
    	
    	//if see monster that is really inside op base then shield it
    	for(Monster monster : monsters) {
    		if(monster.shieldLife == 0 && myBase.mana >= SPELL_COST * 3 &&
    				monster.isFutureThreatToBase(opBase) &&
    				inRange(monster.pos, opBase.pos, Base.RADIUS - 1000) &&
    				monster.health >= 14 &&
    				inRange(monster.pos, heroAttacker.pos, Hero.SHIELD_RANGE)) {
    			heroAttacker.command = "SPELL SHIELD " + monster.id;
    			myBase.mana -= SPELL_COST;
    			return;
    		}
    	}

    	
    	//if can push monster inside op base then do it
    	for(Monster monster : monsters) {
    		if(monster.shieldLife == 0 && myBase.mana >= SPELL_COST * 3 &&
    				monster.health >= 5 &&
    				inRange(monster.pos, heroAttacker.pos, Hero.WIND_RANGE) &&
    				inRange(stepTo(monster.pos, opBase.pos, Hero.WIND_PUSH_RANGE), opBase.pos, Base.RADIUS - 500)) {
    			
    			Vector posToPushMonsterTo = opBase.pos;
    			
    			if(getDist(monster.pos, opBase.pos) > getDist(heroAttacker.pos, opBase.pos)) {
	    	        Vector center = heroAttacker.pos;
	    	        Vector target = opBase.pos;
	    	        
	    	        int radius = Hero.WIND_RANGE;
	
	    	        Vector vector = new Vector(target.x - center.x, target.y - center.y).normalize();
	    	        posToPushMonsterTo = new Vector(center.x + (vector.x * radius), center.y + (vector.y * radius));
    			}
    			
    			heroAttacker.command = "SPELL WIND " + posToPushMonsterTo.toIntString();
    			myBase.mana -= SPELL_COST;
    			return;
    		}
    	}

        	
    	//if see monster that going to enter op base then shield it
    	for(Monster monster : monsters) {
    		if(monster.shieldLife == 0 && myBase.mana >= SPELL_COST * 3 &&
    				monster.isFutureThreatToBase(opBase) &&
    				inRange(monster.pos, opBase.pos, Base.RADIUS) &&
    				monster.health >= 14 &&
    				inRange(monster.pos, heroAttacker.pos, Hero.SHIELD_RANGE)) {
    			heroAttacker.command = "SPELL SHIELD " + monster.id;
    			myBase.mana -= SPELL_COST;
    			return;
    		}
    	}

        //if can control monster to op base then do it
    	for(Monster monster : monsters) {
    		if(monster.shieldLife == 0 && myBase.mana >= SPELL_COST * 3 &&
    				monster.health >= 5 &&
    				!monster.isFutureThreatToBase(opBase) &&
    				!monstorsUnderMyControlIDs.contains(monster.id) &&
    				inRange(monster.pos, heroAttacker.pos, Hero.CONTROL_RANGE)) {
    			
    			int randomPos = (int) Math.floor(Math.random() * 3);
    			
    			Vector controlMonsterToPos = opBase.controlMonstersAttackPoints[randomPos];
    			
    			heroAttacker.command = "SPELL CONTROL " + monster.id + " " + controlMonsterToPos.toIntString();
    			myBase.mana -= SPELL_COST;
    			monstorsUnderMyControlIDs.add(monster.id);
    			monstorsUnderMyControlIDs.add(gameTurn * 1000);
    			return;
    		}
    	}
    	
    	//if can control enemy defender
        for(Hero opHero : opHeros) {
        	if(opHero.shieldLife == 0 && myBase.mana >= SPELL_COST * 3 &&
        		inRange(opHero.pos, heroAttacker.pos, Hero.CONTROL_RANGE) && inRange(opHero.pos, opBase.pos, Base.RADIUS + 500)) {
        		
        		heroAttacker.command = "SPELL CONTROL " + opHero.id + " " + myBase.pos.toIntString();
    			myBase.mana -= SPELL_COST;
        		return;
        	}
        }
        
        //if see monster that going to opBase then follow her but don't damage her
        for(Monster monster : monsters) {
        	if(monster.health >= 6 && opBase.isMonsterInside(monster.pos)) {
        		Vector center = monster.pos;
    	        Vector target = heroAttacker.pos;
    	        
    	        int radius = Hero.DAMAGE_RANGE + 10;

    	        Vector vector = new Vector(target.x - center.x, target.y - center.y).normalize();
    	        Vector result = new Vector(center.x + (vector.x * radius), center.y + (vector.y * radius));
        		
        		heroAttacker.command = "MOVE " + result.toIntString() + " follow";
        		return;
        	}
        }
        
    	//walk around base
        Vector attackPoint = heroAttacker.getAttackingPoint(opBase);

        heroAttacker.command = "MOVE " + attackPoint.toIntString() + " Attack Point";
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
            
            Vector defensePoint = hero.getDefensePoint(myBase);
            
            Vector bestPosToFarm = getBestPosToFarm(defensePoint);

            //move to farm monster
            if(bestPosToFarm != null) hero.command = "MOVE " + bestPosToFarm.toIntString() + " FARM";
            //move to defensive point
            else hero.command = "MOVE " + defensePoint.toIntString() + " Defense Point";
        }
	}
    
    public static Vector bestPosToFarm;
    public static int mostMonstersToFarmAtOnce;
    static ArrayList<Monster> monstersFarmed = new ArrayList<>();
    
    private static Vector getBestPosToFarm(Vector defensePoint){
    	
    	bestPosToFarm = null;
    	mostMonstersToFarmAtOnce = 0;
    	monstersFarmed = new ArrayList<>();
		
    	getBestPosToFarm(defensePoint, 0, new ArrayList<>());
    	
    	//mark monsters as farmed
    	for(Monster monster : monstersFarmed) monster.handled = true;
	    
	    return bestPosToFarm;
    }
    
    private static void getBestPosToFarm(Vector defensePoint, int i, ArrayList<Monster> res) {
    	
    	if(res.size() > mostMonstersToFarmAtOnce) {
    		mostMonstersToFarmAtOnce = res.size();
    		bestPosToFarm = Vector.getAvgVectorOfAllVectors(res);
    		monstersFarmed = new ArrayList<>();
    		for(Monster monster : res) monstersFarmed.add(monster);
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
    			if(!inRange(monster.pos, monsterInList.pos, Hero.DAMAGE_RANGE * 2)) {
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

    
	private static Monster findMonsterWithSmallestNumberOfTurnsToDamageBase(ArrayList<Monster> monsters) {
        double smallestNumberOfTurnsToDamageBase = Double.MAX_VALUE;
        Monster closestMonster = null;

        //find the monster with the smallestNumberOfTurnsToDamageBase
        for(Monster monster : monsters){
            if(monster.handled || monster.numberOfTurnsToDamageMyBase > 100 || monstersIdsWhoCannotBeKilled.contains(monster.id) ||
            monster.numberOfTurnsToDamageMyBase >= smallestNumberOfTurnsToDamageBase) continue;
            
            //TODO: if there is opAttacker then don't run away from my base if(inRange(monster.pos, myBase.pos, Base.RADIUS + 7000))

            smallestNumberOfTurnsToDamageBase = monster.numberOfTurnsToDamageMyBase;
            closestMonster = monster;
        }
        
        return closestMonster;
	}

	public static Vector getVelocityPos1ToPos2(Vector position, Vector destenation, int speed) {
		Vector v = new Vector(position, destenation);
        Vector velocity;
        if (v.lengthSquared() <= speed * speed) velocity = v;
        else velocity = v.normalize().mult(speed);
        return velocity;
	}
	
	private static Vector stepTo(Vector position, Vector destination, int speed) {
        Vector v = new Vector(position, destination);
        Vector target;
        if (v.lengthSquared() <= speed * speed) {
            target = v;
        } else {
            target = v.normalize().mult(speed);
        }
        return position.add(target);
    }
	
    private static boolean inRange(Vector pos1, Vector pos2, int range) {
    	return (pos1.x - pos2.x) * (pos1.x - pos2.x) + (pos1.y - pos2.y) * (pos1.y - pos2.y) <= range * range;
    }
    
	public static double getDist(Vector pos, Vector pos2){
        return Math.sqrt(Math.pow(pos.x - pos2.x, 2) + Math.pow(pos.y - pos2.y, 2));
    }

    private static Hero getOpAttacker(ArrayList<Hero> opHeros, Base myBase){
        for(Hero opHero : opHeros){
            //if there is opHero close to my base then return him
            if(getDist(opHero.pos, myBase.pos) <= Base.RADIUS + 2000) return opHero;
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
 
 [X] fix the sim to be not as long as hero not in goal point becuase this can be in 1 turn while the targetedMonster won't die
    instead make the sim as the smallestNumberOfTurnsToMonster
    
 [] maybe check if myAttacker can in 1 push of wind send monster which will damage opBase in the same turn
    (I think that if enemy op is standing there he can push also before the monster will damage the base but only if he knows that I am pushing if not all good with this action)

Easy:

Medium:
 [] don't farm near other hero defense point unless he is busy


 [] find the closestMonster location when reach to her and not the pos I need to be in
 	and after this try to find if there are monster near it so I can damage them toegther by standing between them

Hard:

	    					Monster closestMonster = findMonsterWithSmallestNumberOfTurnsToDamageBase(monsters);
	    		            
	    		            //if have no monster that going to damage my base
	    		            if(closestMonster == null) {
	    		            	finishSim = true;
	    		            	continue;
	    		            }
	    		            
	    		            
	    		            int smallestNumberOfTurnsToReachMonster = Integer.MAX_VALUE;
	    		            Vector posToGoToACTIONMonster = null;
	    		            int smallestNumberOfTurnsToMonsterWind = Integer.MAX_VALUE;
	    		            int smallestNumberOfTurnsToMonsterControl = Integer.MAX_VALUE;
	    		                
    		                Vector pos = hero.getPosToGoToActionMonster(closestMonster, Hero.DAMAGE_RANGE);
    		                int numberOfTurnsToReachMonster = (int) Math.ceil((getDist(hero.pos, pos) - Hero.DAMAGE_RANGE) / Hero.DIST_PER_TURN);
    		                if(numberOfTurnsToReachMonster < 0) numberOfTurnsToReachMonster = 0;
    		                
    		                
    		                
    		                if(numberOfTurnsToReachMonster < smallestNumberOfTurnsToReachMonster){
    		                    //hero won't be fast enough to kill monster
    		                    if((closestMonster.numberOfTurnsToDamageMyBase - numberOfTurnsToReachMonster) * Hero.DAMAGE < closestMonster.health){
    		                    	// check if hero can wind it
    		                    	pos = hero.getPosToGoToActionMonster(closestMonster, Hero.WIND_RANGE);
    		                    	int numberOfTurnsToReachMonsterWind = (int) Math.ceil((getDist(hero.pos, pos) - Hero.WIND_RANGE) / Hero.DIST_PER_TURN);
    		                    	if(numberOfTurnsToReachMonsterWind < 0) numberOfTurnsToReachMonsterWind = 0;
    		                        
    		                        if(myBase.mana >= SPELL_COST && closestMonster.shieldLife - numberOfTurnsToReachMonsterWind <= 0 &&
    		                        closestMonster.numberOfTurnsToDamageMyBase > numberOfTurnsToReachMonsterWind &&
    		                        smallestNumberOfTurnsToMonsterWind > numberOfTurnsToReachMonsterWind){
    		                        	smallestNumberOfTurnsToMonsterWind = numberOfTurnsToReachMonsterWind;
    		                        	smallestNumberOfTurnsToReachMonster = numberOfTurnsToReachMonster;
    		                            posToGoToACTIONMonster = pos;
    		                        }
    		                        else {
    		                        	pos = hero.getPosToGoToActionMonster(closestMonster, Hero.CONTROL_RANGE);
    		                        	int numberOfTurnsToReachMonsterControl = (int) Math.ceil((getDist(hero.pos, pos) - Hero.CONTROL_RANGE) / Hero.DIST_PER_TURN);
    		                        	if(numberOfTurnsToReachMonsterControl < 0) numberOfTurnsToReachMonsterControl = 0;
    		                            
    		                            if(myBase.mana >= SPELL_COST && closestMonster.shieldLife - numberOfTurnsToReachMonsterControl <= 0 &&
    		                               closestMonster.numberOfTurnsToDamageMyBase > numberOfTurnsToReachMonsterControl + 1 &&
    		                               smallestNumberOfTurnsToMonsterControl > numberOfTurnsToReachMonsterControl) {
    		                            	smallestNumberOfTurnsToMonsterControl = numberOfTurnsToReachMonsterControl;
    		                            	smallestNumberOfTurnsToReachMonster = numberOfTurnsToReachMonster;
    		                                posToGoToACTIONMonster = pos;
    		                            }
    		                        	else{
    		                            	if(closestMonster.numberOfTurnsToDamageMyBase > numberOfTurnsToReachMonster) {
    		    	                            smallestNumberOfTurnsToReachMonster = numberOfTurnsToReachMonster;
    		    	                            posToGoToACTIONMonster = pos;
    		                            	}
    		                            	//if not then hero can't reach monster before monster damage base
    		                            }
    		                        }
    		                    }
    		                    else{
    		                        smallestNumberOfTurnsToReachMonster = numberOfTurnsToReachMonster;
    		                        posToGoToACTIONMonster = pos;
    		                    }
    		                }
    		                
    		                
    		              //check if just damage will be enough
    		                if((closestMonster.numberOfTurnsToDamageMyBase - smallestNumberOfTurnsToReachMonster) * Hero.DAMAGE < closestMonster.health){
//    		                	//check if can reach monster and wind it
//    		                	if(smallestNumberOfTurnsToMonsterWind < 100) {
//    		                		if(smallestNumberOfTurnsToMonsterWind == 0 && myBase.mana >= SPELL_COST && closestMonster.shieldLife == 0 &&
//    		                		inRange(hero.pos, closestMonster.pos, Hero.WIND_RANGE)) windMonster(closestHero, posToGoToACTIONMonster, smallestNumberOfTurnsToMonsterWind);
//    		                		else goToMonsterToWindIt(hero, posToGoToACTIONMonster, smallestNumberOfTurnsToMonsterWind, closestMonster);
//    		                	}
//    		                	//check if can reach monster and control it
//    		                	else if(smallestNumberOfTurnsToMonsterControl < 100) {
//    		                		if(smallestNumberOfTurnsToMonsterControl == 0 && myBase.mana >= SPELL_COST && closestMonster.shieldLife == 0 &&
//    		                		inRange(hero.pos, closestMonster.pos, Hero.CONTROL_RANGE)) controlMonster(closestHero, posToGoToACTIONMonster, smallestNumberOfTurnsToMonsterControl, closestMonster);
//    		                		else goToMonsterToControlIt(hero, posToGoToACTIONMonster, smallestNumberOfTurnsToMonsterControl, closestMonster);
//    		                	}
//    		                	else {
//	    		                	//This hero by himself can't kill the monster but maybe more heros will be able to
//	    		                	finishSim = true
//    		                	}
    		                	finishSim = true;
    		                }
    		                else{
    		                	
//    		                	int rangeFromRangeToPushMonster = Base.RADIUS;
//    		                	if(opAttacker != null && inRange(closestMonster.pos, opAttacker.pos, Hero.DIST_PER_TURN * 2 + Hero.WIND_RANGE)) rangeFromRangeToPushMonster = Base.RADIUS + 2000;
//    		                	// if monster is inside myBase and I can wind it and
//    		                	//the monster has more than Hero.DAMAGE life then do it
//
////    		                	if(myBase.mana >= 100 && closestMonster.shieldLife == 0 && opAttacker != null && !inRange(closestMonster.pos, opAttacker.pos, Hero.DIST_PER_TURN * 3 + Hero.WIND_RANGE) &&
////    		                    inRange(myBase.pos, closestMonster.pos, Base.RADIUS + 1500) && closestMonster.health > Hero.DAMAGE &&
////    		                    inRange(closestHero.pos, closestMonster.pos, Hero.CONTROL_RANGE)) controlMonsterOutOfMyBase(closestHero, posToGoToACTIONMonster, closestMonster);
//    		                	if(myBase.mana >= 100 && closestMonster.shieldLife == 0 &&
//    		                    inRange(myBase.pos, closestMonster.pos, rangeFromRangeToPushMonster) && closestMonster.health > Hero.DAMAGE * 2 &&
//    		                    inRange(closestHero.pos, closestMonster.pos, Hero.WIND_RANGE)) windMonsterOutOfMyBase(closestHero, posToGoToACTIONMonster, smallestNumberOfTurnsToReachMonster);
//    		                    else damageMonster(closestHero, posToGoToACTIONMonster, smallestNumberOfTurnsToReachMonster, closestMonster);
    		                	
    		                	//move monsters
    		                	for(Monster monster2 : monsters) {
    		                		if(monster2.handled || monster2.health <= 0) continue;
    		                		
    		                		monster2.pos = monster2.getPosNextXTurns(1);
    		                		if(closestMonster.id == monster2.id) {
    		                			posToGoToACTIONMonster = hero.getPosToGoToActionMonster(monster2, Hero.DAMAGE_RANGE);
    		                		}
    		                		if(monster2.shieldLife > 0) monster2.shieldLife -= 1;
    		                	}
    		                	
    		                	if(hero.pos == null) System.err.println("hero.pos: " + hero.pos);
    		                	else if(posToGoToACTIONMonster == null) System.err.println("posToGoToACTIONMonster: " + posToGoToACTIONMonster);

    		                	//move hero
    		                	hero.pos = stepTo(hero.pos, posToGoToACTIONMonster, Hero.DIST_PER_TURN);
    		                	
    		                	double res = addToEval + simBestPos(hero, posToGoToACTIONMonster, Hero.DAMAGE_RANGE, numberOfTurnsToReachActionMonster - 1, closestMonster.id);
    		                	System.err.println("here");
    		                	closestMonster.handled = true;
    		                	hero.monstersHandling.add(closestMonster.id);
    		                	return res;
    		                }
*/
