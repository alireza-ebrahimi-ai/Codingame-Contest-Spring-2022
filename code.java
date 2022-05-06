import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;


class Vector {
    public double x;
	public double y;

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
    int DEFENSIVE_CIRCLE_RADIUS;

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
        DEFENSIVE_CIRCLE_RADIUS = 7300;

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

    public void calcDefensePoints(int attackerRadius, boolean hasAnAttacker) {
    	
    	
    	if(hasAnAttacker) {
    		if(attackerRadius == -1) {
        		attackerRadius = 4800;
        	}
    		
    		DEFENSIVE_CIRCLE_RADIUS = attackerRadius + 1200;

            int numberOfDefensePoints = 4;

            Vector[] defensePoints = new Vector[6];
            int index = 2;
            
            double maxAngle = Double.MIN_VALUE;
            double minAngle = Double.MAX_VALUE;
            
            
            for(double angle = 0; angle < Math.PI * 2; angle += 0.0001) {
            	int x = (int)(this.pos.x + DEFENSIVE_CIRCLE_RADIUS * Math.cos(angle));
                int y = (int)(this.pos.y + DEFENSIVE_CIRCLE_RADIUS * Math.sin(angle));
                if(x < 2000 || y < 2000 || x > Player.MAP_WIDTH - 2000 || y > Player.MAP_HEIGHT - 2000) continue;
                
                
                maxAngle = Math.max(maxAngle, angle);
                minAngle = Math.min(minAngle, angle);
            }
            
            double add = (maxAngle - minAngle) / (numberOfDefensePoints - 1);
            
            for(int i = 2; i < 6; i++) {
            	double angle = minAngle + add * (i - 2);
            	int x = (int)(this.pos.x + DEFENSIVE_CIRCLE_RADIUS * Math.cos(angle));
                int y = (int)(this.pos.y + DEFENSIVE_CIRCLE_RADIUS * Math.sin(angle));
            	defensePoints[i] = new Vector(x, y);
            }

//            for(int i = 0; i < (numberOfDefensePoints * 4); i++){
//                double angle = (Math.PI * 2) * (0.5 + i) / (numberOfDefensePoints * 4);
//                int x = (int)(this.pos.x + DEFENSIVE_CIRCLE_RADIUS * Math.cos(angle));
//                int y = (int)(this.pos.y + DEFENSIVE_CIRCLE_RADIUS * Math.sin(angle));
//                if(x < 0 || y < 0 || x > Player.MAP_WIDTH || y > Player.MAP_HEIGHT) continue;
//
//                defensePoints[index] = new Vector(x, y);
//                index++;
//            }
            
            currentDefensePoints = defensePoints;
    	}
    	else {    
    		if(attackerRadius == -1) {
	    		attackerRadius = 6300;
	    	}
    	
    		DEFENSIVE_CIRCLE_RADIUS = attackerRadius + 1200;

            int numberOfDefensePoints = 6;

            Vector[] defensePoints = new Vector[numberOfDefensePoints];
            int index = 0;
            
            double maxAngle = Double.MIN_VALUE;
            double minAngle = Double.MAX_VALUE;
            
            
            for(double angle = 0; angle < Math.PI * 2; angle += 0.0001) {
            	int x = (int)(this.pos.x + DEFENSIVE_CIRCLE_RADIUS * Math.cos(angle));
                int y = (int)(this.pos.y + DEFENSIVE_CIRCLE_RADIUS * Math.sin(angle));
                if(x < 2000 || y < 2000 || x > Player.MAP_WIDTH - 2000 || y > Player.MAP_HEIGHT - 2000) continue;
                
                
                maxAngle = Math.max(maxAngle, angle);
                minAngle = Math.min(minAngle, angle);
            }
            
            double add = (maxAngle - minAngle) / 2;
            
            for(int i = 0; i < 3; i++) {
            	double angle = minAngle + add * i;
            	int x = (int)(this.pos.x + DEFENSIVE_CIRCLE_RADIUS * Math.cos(angle));
                int y = (int)(this.pos.y + DEFENSIVE_CIRCLE_RADIUS * Math.sin(angle));
            	defensePoints[i * 2] = new Vector(x, y);
            	defensePoints[i * 2 + 1] = new Vector(x, y);
            }
            
            
//            for(int i = 0; i < (numberOfDefensePoints * 4); i++){
//                double angle2 = (Math.PI * 2) * (0.5 + i) / (numberOfDefensePoints * 4);
//                int x = (int)(this.pos.x + DEFENSIVE_CIRCLE_RADIUS * Math.cos(angle2));
//                int y = (int)(this.pos.y + DEFENSIVE_CIRCLE_RADIUS * Math.sin(angle2));
//                if(x < 0 || y < 0 || x > Player.MAP_WIDTH || y > Player.MAP_HEIGHT) continue;
//
//                defensePoints[index] = new Vector(x, y);
//                index++;
//            }
            
            currentDefensePoints = defensePoints;
    	}
    }
    
    private void calcAttackPoints(){
        int ATTACK_CIRCLE_RADIUS = RADIUS;

        int numberOfAttackPoints = 4;

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

class Entity{
    int id;
    Vector pos;
    int shieldLife;
    boolean isControlled;
    
    public Entity(int id, Vector pos, int shieldLife, boolean isControlled) {
        this.id = id;
        this.pos = pos;
        this.shieldLife = shieldLife;
        this.isControlled = isControlled;
    }
}

class Hero extends Entity{
    
    String command;
    HashSet<Integer> monstersHandling;
    boolean usingSpell;

    static final int DIST_PER_TURN = 800;
    static final int DAMAGE_RANGE = 800;
    static final int DAMAGE = 2;
    static final int WIND_RANGE = 1280;
    static final int CONTROL_RANGE = 2200;
    static final int SHIELD_RANGE = 2200;
    public static final int WIND_PUSH_RANGE = 2200;

    public Hero(int id, Vector pos, int shieldLife, boolean isControlled){
    	super(id, pos, shieldLife, isControlled);
        this.command = "";
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
		
////		System.err.println();
////		System.err.println("in getPosToGoToActionMonster");
////		System.err.println("closestMonster.id: " + closestMonster.id);
////		System.err.println("this.id: " + this.id);
//		
//		Vector pos2 = null;
//		
//		double dist = Player.getDist(this.pos, closestMonster.pos) - actionRange;
//		
//		if(dist < 0) return this.pos;
//		
////		System.err.println("dist: " + dist);
//		
//		Vector prevGoal = null;
//		Vector prevPrevPos = null;
//		
//		while(prevGoal == null || pos2.x - prevGoal.x > 0 || pos2.y - prevGoal.y > 0) {
//			int numberOfTurnsPass = (int) Math.ceil(dist / DIST_PER_TURN);
////			System.err.println("numberOfTurnsPass: " + numberOfTurnsPass);
//			
//			Vector nextXTurnsPosMonster = closestMonster.getPosNextXTurns(numberOfTurnsPass);
////			System.err.println("nextXTurnsPosMonster: " + nextXTurnsPosMonster);
//			
//			prevPrevPos = prevGoal;
//			prevGoal = pos2;
//			pos2 = nextXTurnsPosMonster;
//			
//			dist = Player.getDist(this.pos, nextXTurnsPosMonster) - actionRange;
//			if(dist < 0) return nextXTurnsPosMonster;
//			
////			System.err.println("dist: " + dist);
////			System.err.println("prevPrevPos: " + prevPrevPos);
////			System.err.println("prevGoal: " + prevGoal);
////			System.err.println("pos2: " + pos2);
//			if(prevPrevPos != null && pos2.x - prevPrevPos.x == 0 && pos2.y - prevPrevPos.y == 0) break;
//		}
		
		
		
		int numberOfTurnsForward = 0;
		
		Vector pos = new Vector(closestMonster.pos);
		while(Player.numberOfTurnsToReach(Player.getDist(this.pos, pos) - actionRange, Hero.DIST_PER_TURN) > numberOfTurnsForward) {
			numberOfTurnsForward++;
			pos = closestMonster.getPosNextXTurns(numberOfTurnsForward);
		}
		
//		System.err.println("pos2: " + pos2);
//		System.err.println("pos: " + pos);
		
		return pos;
	}
	
	public boolean canCastShield(Entity entity) {
		return this.isAvailable() && Player.myBase.mana >= Player.SPELL_COST && entity.shieldLife == 0 && Player.inRange(this.pos, entity.pos, Hero.SHIELD_RANGE);
	}
	
	public boolean canWind(Entity entity) {
		return this.isAvailable() && Player.myBase.mana >= Player.SPELL_COST && entity.shieldLife == 0 && Player.inRange(this.pos, entity.pos, Hero.WIND_RANGE);
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

	

	
	
	
	@Override
	public String toString() {
		return "Hero{id: " + id + " pos: " + this.pos.toIntString() + "}"; 
	}
}

class Monster extends Entity{
    int health;
    int vx;
    int vy;
    boolean isTargetingBase;
    int threatFor;
    boolean handled;
    int numberOfTurnsToDamageMyBase;
    int numberOfTurnsToDamageOpBase;

    double m;
    double b;

    static int DIST_TO_DAMAGE_BASE = 300;
    static int DIST_PER_TURN = 400;

    public Monster(int id, Vector pos, int health, int vx, int vy, boolean isTargetingBase, int threatFor, int shieldLife, boolean isControlled){
    	super(id, pos, shieldLife, isControlled);
        this.health = health;
        this.vx = vx;
        this.vy = vy;
        this.isTargetingBase = isTargetingBase;
        this.threatFor = threatFor;
        this.handled = false;

        //System.err.println("vx: " + vx);
        //System.err.println("vy: " + vy);

        if(vy * vx >= 1) this.m = Math.abs(vy) * 1.0 / Math.abs(vx);
        else this.m = vy * 1.0 / vx;

        this.b = this.pos.y - this.m * this.pos.x;

        this.numberOfTurnsToDamageMyBase = getNumberOfTurnsToDamageBase(Player.myBase);
        this.numberOfTurnsToDamageOpBase = getNumberOfTurnsToDamageBase(Player.opBase);

        //System.err.println("id: " + this.id + " y = " +  this.m + "x + " + this.b);
    }

    public Vector getPosNextXTurns(int numberOfTurns) {
    	Base base = Player.myBase;
    	if(this.numberOfTurnsToDamageMyBase < 0) base = Player.opBase;
    	
    	Vector newPos = new Vector(this.pos);
    	
    	boolean wasOutsideBase = false;
    	
    	while(!base.isMonsterInside(newPos) && numberOfTurns > 0) {
    		newPos = newPos.add(new Vector(this.vx, this.vy)).symmetricTruncate(Player.symmetryOrigin);
    		numberOfTurns--;
    		wasOutsideBase = true;
		}
    	
    	
    	if(numberOfTurns == 0) return newPos;
    	
    	if(!wasOutsideBase) return newPos.add(new Vector(this.vx * numberOfTurns, this.vy * numberOfTurns)).symmetricTruncate(Player.symmetryOrigin);
    	else {
//    		System.err.println("wasOutsideBase newPos: " + newPos);
			
			Vector velocity = Player.getVelocityPos1ToPos2(newPos, base.pos, Monster.DIST_PER_TURN);
//			System.err.println("velocity: " + velocity);
			
			return newPos.add(velocity.mult(numberOfTurns)).symmetricTruncate(Player.symmetryOrigin);
    	}
	}

	public boolean isThreatingMyBase(){
        return isTargetingBase && threatFor == 1;
    }

    public boolean isFutureThreatToBase(Base base){
//        double dist = minDistFromLineToPoint(-this.m, 1, -this.b, base.pos.x, base.pos.y);
//
//        return base.willMonsterReachBase(this, dist);
    	
    	if(base.pos.x == Player.myBase.pos.x && base.pos.y == Player.myBase.pos.y) return this.numberOfTurnsToDamageMyBase < Integer.MAX_VALUE;
    	
    	return this.numberOfTurnsToDamageOpBase < Integer.MAX_VALUE;
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
        if(dist <= Base.RADIUS) return Player.numberOfTurnsToReach(dist - Monster.DIST_TO_DAMAGE_BASE, Monster.DIST_PER_TURN) + add1IfControlledByMe;

        dist = minDistFromLineToPoint(-this.m, 1, -this.b, base.pos.x, base.pos.y);

        //monster would never reach my base (unless op will push her with wind)
        if(!base.willMonsterReachBase(this, dist)) return Integer.MAX_VALUE;

        Vector center = base.pos;
        Vector target = this.pos;

        Vector vector = new Vector(target.x - center.x, target.y - center.y).normalize();
        Vector result = new Vector(center.x + (vector.x * Base.RADIUS), center.y + (vector.y * Base.RADIUS));
        
        return Player.numberOfTurnsToReach(Player.getDist(result, this.pos), DIST_PER_TURN) + 
        		Player.numberOfTurnsToReach(Base.RADIUS - Monster.DIST_TO_DAMAGE_BASE, Monster.DIST_PER_TURN);
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

    static int FARM_RANGE = 4000;
    static int SPELL_COST = 10;

    static int nextTurnWindOpAttacker;
    static boolean thereIsOpAttacker;
    static ArrayList<Integer> monstorsUnderMyControlIDs;
    static int gameTurn;
    
    static boolean attacking = false;
    
    
    static Base myBase, opBase;
    static ArrayList<Monster> monsters, savedMonsters;
    static ArrayList<Hero> myHeros, savedMyHeros, opHeros;
    
    static int turnToStartAttack = 150;
    
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
                    case 0: monsters.add(new Monster(id, new Vector(x, y), health, vx, vy, isTargetingBase, threatFor, shieldLife, isControlled)); 
                    		savedMonsters.add(new Monster(id, new Vector(x, y), health, vx, vy, isTargetingBase, threatFor, shieldLife, isControlled)); break;
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
        if(myBase.mana >= 250 || attacking || gameTurn > turnToStartAttack) {
        	heroAttacker = getHeroAttacker();
        	if(!inRange(myBase.pos, heroAttacker.pos, Base.RADIUS)) {
	        	heroAttacker.command = "Attacking";
	        	i++;
        	}
        	
        	attacking = true;
        }
        
        //if there is opAttacker then reduce the farm range and switch to closeDefensePoints
        if(opAttacker != null) {
        	whatToDoWhenHaveOpAttacker(opAttacker, (heroAttacker != null && heroAttacker.command.equals("Attacking")));
        }
        //if there is no opAttacker then increase the farm range and switch to farDefensePoints
        else if(opAttacker == null) {
        	whatToDoWhenNotHaveOpAttacker((heroAttacker != null && heroAttacker.command.equals("Attacking")));
        }
        

        

        monstersIdsWhoCannotBeKilled = new ArrayList<Integer>();
        
        //if one of my heros isControlled or got wind then push op hero attacker with wind
        Hero heroControlled = shouldWindOrControlOpAttacker(heroAttacker);
        
        if(heroControlled != null) nextTurnWindOpAttacker = 3;
        
        int herosToDefend = 0;
        
        //Defense
        for(; i < myHeros.size(); i++){
        	
        	herosToDefend++;
        	
        	if(opAttacker != null && herosToDefend == 2) {
            	int numberOfTurnsForward = 1;
            	
            	boolean canOpPushMonster = false;
            	
            	while(numberOfTurnsForward <= 2) {
                    for(Monster monster : monsters) {
                    	if(monster.handled || !myBase.isMonsterInside(monster.pos)) continue;
                    	
                    	Vector opMonsterToWind = opAttacker.getPosToGoToActionMonster(monster, Hero.WIND_RANGE);
                    	
                    	
                    	if(numberOfTurnsToReach(getDist(opAttacker.pos, opMonsterToWind) - Hero.WIND_RANGE, Hero.DIST_PER_TURN) == numberOfTurnsForward) {
                        	Vector monsterFutureLocation = monster.getPosNextXTurns(numberOfTurnsForward);
                        	
                        	canOpPushMonster = true;
                        	
                        	
                        	//check if one of my heros can reach the monster and wind it in time
                        	for(Hero hero : myHeros) {
                        		if(!hero.isAvailable()) continue;
                        		if(numberOfTurnsToReach(getDist(hero.pos, monsterFutureLocation) - Hero.WIND_RANGE, Hero.DIST_PER_TURN) <= numberOfTurnsForward) {
                        			if(hero.canWind(monster)) {
                        				hero.command = "SPELL WIND " + opBase.pos.toIntString() + " B2";
                        				myBase.mana -= SPELL_COST;
                        				hero.usingSpell = true;
                        				monster.handled = true;
                        				break;
                        			}
                        			else if(numberOfTurnsForward > 0 && monster.shieldLife - numberOfTurnsForward <= 0){
                        				hero.command = "MOVE " + monsterFutureLocation.toIntString() + " A2";
                        				monster.handled = true;
                        				break;
                        			}
                        		}
                        	}
                        	
                        	if(monster.handled) continue;
                        	
                        	//if opAttacker will wind it now I can't do anything about it
                        	if(numberOfTurnsForward == 0) continue;
                        	
                        	//check if one of my heros can reach the opAttacker windRange and wind with the monster
                        	for(Hero hero : myHeros) {
                        		if(!hero.isAvailable()) continue;
                        		// + 1 because you are moving before wind pushes, DON'T KNOW ABOUT THIS
                        		if(numberOfTurnsToReach(getDist(hero.pos, opMonsterToWind), Hero.DIST_PER_TURN) <= numberOfTurnsForward) {
                    				hero.command = "MOVE " + opMonsterToWind.toIntString() + " C2";
                    				monster.handled = true;
                    				break;
                        		}
                        	}
                    	}
                    }
            		if(canOpPushMonster) break;
            		numberOfTurnsForward++;
            	}
        	}

            //push op hero attacker with wind because the op is controlling us
            if(nextTurnWindOpAttacker > 0 && opAttacker != null && i + 1 == myHeros.size() &&
            	myBase.mana >= SPELL_COST){
            	//maybe I need to shield myself instead
            	
            	boolean hasShieldHeros = false;
            	
            	for(Hero hero : myHeros) {
            		if(hero.canCastShield(hero)) {
            			hero.command = "SPELL SHIELD " + hero.id;
	                    myBase.mana -= SPELL_COST;
	                    hero.usingSpell = true;
	                    hasShieldHeros = true;
            		}
            	}
            	
            	if(hasShieldHeros) {
            		nextTurnWindOpAttacker--;
                    continue;
            	}
            	
                if(tryPushOpAttacker()){
                    nextTurnWindOpAttacker--;
                    continue;
                }
                else {
                	if(tryControlOpAttacker()) {
                        nextTurnWindOpAttacker--;
                        continue;
                	}
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
                int numberOfTurnsToReachMonster = Player.numberOfTurnsToReach(getDist(hero.pos, pos) - Hero.DAMAGE_RANGE, Hero.DIST_PER_TURN);
                
                if(closestMonster.id == 32) System.err.println("pos: " + pos);
                if(closestMonster.id == 32) System.err.println("numberOfTurnsToReachMonster: " + numberOfTurnsToReachMonster);
                
                
                if(numberOfTurnsToReachMonster < smallestNumberOfTurnsToReachMonster){

                	//hero won't be fast enough to kill monster
                    if((closestMonster.numberOfTurnsToDamageMyBase - numberOfTurnsToReachMonster) * Hero.DAMAGE < closestMonster.health){
                    	// check if hero can wind it
                    	pos = hero.getPosToGoToActionMonster(closestMonster, Hero.WIND_RANGE);
                    	int numberOfTurnsToReachMonsterWind =  Player.numberOfTurnsToReach(getDist(hero.pos, pos) - Hero.WIND_RANGE, Hero.DIST_PER_TURN);
                        
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
                        	int numberOfTurnsToReachMonsterControl = Player.numberOfTurnsToReach(getDist(hero.pos, pos) - Hero.CONTROL_RANGE, Hero.DIST_PER_TURN);
                            
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
            
            
            //check if I have enough time to shield my hero before needing to go to kill monster
            if(heroControlled != null && ((closestMonster.numberOfTurnsToDamageMyBase - smallestNumberOfTurnsToReachMonster) - 1) * Hero.DAMAGE >= closestMonster.health) {
            	if(closestHero.canCastShield(heroControlled)) {
            		closestHero.command = "SPELL SHIELD " + heroControlled.id;
                    myBase.mana -= SPELL_COST;
                    closestHero.usingSpell = true;
                    
                    //mark the monster as handled and TODO: preform sim
                    closestMonster.handled = true;
                    closestHero.monstersHandling.add(closestMonster.id);
                    continue;
        		}
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
            	damageMonster(closestHero, posToGoToACTIONMonster, smallestNumberOfTurnsToReachMonster, closestMonster);
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
    

    private static boolean tryControlOpAttacker() {
    	if(opAttacker.shieldLife > 0) return false;
    	
        for(Hero hero : myHeros){
            if(!hero.isAvailable()) continue;
            if(!inRange(opAttacker.pos, hero.pos, Hero.CONTROL_RANGE)) continue;
            
            hero.command = "SPELL CONTROL " + opAttacker.id + " " + opBase.pos.toIntString() + " CONTROL ATTACKER";
            myBase.mana -= SPELL_COST;
            hero.usingSpell = true;
            return true;
        }
        
        return false;
	}

	private static boolean tryPushOpAttacker() {
    	if(opAttacker.shieldLife > 0) return false;
    	
        for(Hero hero : myHeros){
            if(!hero.isAvailable()) continue;
            if(!inRange(opAttacker.pos, hero.pos, Hero.WIND_RANGE)) continue;
            
            hero.command = "SPELL WIND " + " " + opBase.pos.toIntString() + " PUSH ATTACKER";
            
            myBase.mana -= SPELL_COST;
            hero.usingSpell = true;
            return true;
        }
        
        return false;
	}


	private static void damageMonster(Hero closestHero, Vector posToGoToACTIONMonster,
			int smallestNumberOfTurnsToReachMonster, Monster closestMonster) {
    	//Vector getBestPosToDamageMonster = simBestPos(closestHero, posToGoToACTIONMonster, closestMonster, Hero.DAMAGE_RANGE);
    	//System.err.println("getBestPosToDamageMonster: " + getBestPosToDamageMonster);
    	
    	int numberOfTurnsToActionMonster = smallestNumberOfTurnsToReachMonster + (int) Math.ceil(closestMonster.health * 1.0 / Hero.DAMAGE);
    	
    	if(opAttacker != null) numberOfTurnsToActionMonster = Math.min(numberOfTurnsToActionMonster, 5);
    	
    	simBestPos(closestHero, posToGoToACTIONMonster, Hero.DAMAGE_RANGE, numberOfTurnsToActionMonster, closestMonster.id);
    	
    	for(int monsterIndex = 0; monsterIndex < monsters.size(); monsterIndex++) {
    		monsters.get(monsterIndex).changePos(savedMonsters.get(monsterIndex));
    		monsters.get(monsterIndex).shieldLife = savedMonsters.get(monsterIndex).shieldLife;
    	}
    	
    	closestHero.command = "MOVE " + posToGoToACTIONMonster.toIntString();
//    	closestHero.monstersHandling.add(closestMonster.id);
//    	closestMonster.handled = true;
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
		myBase.mana -= SPELL_COST;
		closestHero.usingSpell = true;
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
		myBase.mana -= SPELL_COST;
		closestHero.usingSpell = true;
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
        myBase.calcDefensePoints((int) getDist(opAttacker.pos, myBase.pos), hasAnAttacker);
        FARM_RANGE = 2000;
        if(inRange(opAttacker.pos, myBase.pos, Base.RADIUS - 2000)) FARM_RANGE = 500;
        
        //sim oppnent to check if he can push monster into my base in the next 2 turns
        //if yes then push the monster or get close to him
        
        //1) foreach monster get her position after N turns
        //2) check if opAttacker will go to this pos will he be able to push her in the next N turns
        //3) if yes and the monster will be push to close radius to my base then check if I will be able to push it back in time
        //4) if not and the monster will not immediately damage my base and I can reach opAttacker before he wind her in the range of his wind then do it
        //5) if not then maybe try to shield this monster TODO
        
    	int numberOfTurnsForward = 0;
    	
        for(Monster monster : monsters) {
        	if(monster.handled || !myBase.isMonsterInside(monster.pos)) continue;
        	
        	Vector opMonsterToWind = opAttacker.getPosToGoToActionMonster(monster, Hero.WIND_RANGE);

        	if(numberOfTurnsToReach(getDist(opAttacker.pos, opMonsterToWind) - Hero.WIND_RANGE, Hero.DIST_PER_TURN) == numberOfTurnsForward) {
            	Vector monsterFutureLocation = monster.getPosNextXTurns(numberOfTurnsForward);
            	//check if one of my heros can reach the monster and wind it in time
            	for(Hero hero : myHeros) {
            		if(!hero.isAvailable()) continue;
            		
            		if(numberOfTurnsToReach(getDist(hero.pos, monsterFutureLocation) - Hero.WIND_RANGE, Hero.DIST_PER_TURN) <= numberOfTurnsForward) {
            			if(hero.canWind(monster)) {
            				hero.command = "SPELL WIND " + opBase.pos.toIntString() + " B";
            				myBase.mana -= SPELL_COST;
            				hero.usingSpell = true;
            				monster.handled = true;
            				hero.monstersHandling.add(monster.id);
            				break;
            			}
            			else if(numberOfTurnsForward > 0 && monster.shieldLife - numberOfTurnsForward <= 0){
            				hero.command = "MOVE " + monsterFutureLocation.toIntString() + " A";
            				monster.handled = true;
            				hero.monstersHandling.add(monster.id);
            				break;
            			}
            		}
            	}
            	
            	if(monster.handled) continue;
            	
            	//if opAttacker will wind it now I can't do anything about it
            	if(numberOfTurnsForward == 0) continue;
            	
            	//check if one of my heros can reach the opAttacker windRange and wind with the monster
            	for(Hero hero : myHeros) {
            		if(!hero.isAvailable()) continue;
            		
            		// + 1 because you are moving before wind pushes, DON'T KNOW ABOUT THIS
            		if(numberOfTurnsToReach(getDist(hero.pos, opMonsterToWind), Hero.DIST_PER_TURN) <= numberOfTurnsForward) {
        				hero.command = "MOVE " + opMonsterToWind.toIntString() + " C";
        				monster.handled = true;
        				hero.monstersHandling.add(monster.id);
        				break;
            		}
            	}
        	}
        }
    }
    
    private static void whatToDoWhenNotHaveOpAttacker(boolean hasAnAttacker) {
    	//if there is no opAttacker then increase the farm range and switch to farDefensePoints
    	 thereIsOpAttacker = false;
    	 myBase.calcDefensePoints(-1, hasAnAttacker);
         FARM_RANGE = 4000;
    }
    
    private static Hero shouldWindOrControlOpAttacker(Hero heroAttacker) {
        //if one of my heros isControlled
        //TODO: or got wind then push op hero attacker with wind
        for(Hero hero : myHeros){
            if(hero.isControlled && (heroAttacker == null || heroAttacker.id != hero.id)){
                return hero;
            }
        }
    	
    	return null;
    }
    
    
    private static boolean isControllingMe = false;
    private static Vector prevPosMyAttacker = null;

	private static void attackStrategy(Hero heroAttacker) {
		
//		System.err.println("have heroAttacker");
		
		if(heroAttacker.isControlled) {
			isControllingMe = true;
			heroAttacker.command = "WAIT CONTROLED!";
			return;
		}
		
		//if can shield monster to damage opBase for sure
    	for(Monster monster : monsters) {
    		if(monster.shieldLife == 0 && myBase.mana >= SPELL_COST &&
    				monster.health - (numberOfTurnsToReach(getDist(monster.pos, opBase.pos) - Monster.DIST_TO_DAMAGE_BASE, Monster.DIST_PER_TURN) * Hero.DAMAGE * 3) > 0 &&
    				inRange(monster.pos, heroAttacker.pos, Hero.SHIELD_RANGE)) {
    			heroAttacker.command = "SPELL SHIELD " + monster.id + " SHIELD GOAL!!";
    			myBase.mana -= SPELL_COST;
    			return;
    		}
    	}
		
      	//if can push monster to immedately damage opBase
    	for(Monster monster : monsters) {
    		if(monster.shieldLife == 0 && myBase.mana >= SPELL_COST &&
    				monster.health >= 2 &&
    				inRange(monster.pos, heroAttacker.pos, Hero.WIND_RANGE) &&
    				inRange(monster.pos, opBase.pos, Monster.DIST_TO_DAMAGE_BASE + Hero.WIND_PUSH_RANGE)) {
    			heroAttacker.command = "SPELL WIND " + opBase.pos.toIntString() + " GOAL!!";
    			myBase.mana -= SPELL_COST;
    			return;
    		}
    	}
		
		if(myBase.mana < SPELL_COST * 3) {
			if(!inRange(opBase.pos, heroAttacker.pos, Base.RADIUS + 3000)) {
				heroAttacker.command = "MOVE " + opBase.pos.toIntString();
				return;
			}
			else {
				Vector posToFarm = getBestPosToFarm(heroAttacker.pos, 4000);
				if(posToFarm != null) {
					heroAttacker.command = "MOVE " + posToFarm.toIntString() + " FARM";
					return;
				}
			}
		}
		
		
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
    				inRange(monster.pos, opBase.pos, Base.RADIUS - 1000) &&
    				monster.health - (numberOfTurnsToReach(getDist(monster.pos, opBase.pos) - Monster.DIST_TO_DAMAGE_BASE, Monster.DIST_PER_TURN) * Hero.DAMAGE) > 0 &&
    				inRange(monster.pos, heroAttacker.pos, Hero.SHIELD_RANGE)) {
    			heroAttacker.command = "SPELL SHIELD " + monster.id;
    			myBase.mana -= SPELL_COST;
    			return;
    		}
    	}

    	
    	//if can push monster inside op base then do it
    	for(Monster monster : monsters) {
    		if(monster.shieldLife == 0 && myBase.mana >= SPELL_COST * 3 &&
    				monster.health - (numberOfTurnsToReach(Base.RADIUS - Monster.DIST_TO_DAMAGE_BASE, Monster.DIST_PER_TURN) * 0.5 * Hero.DAMAGE) > 0 &&
    				inRange(monster.pos, heroAttacker.pos, Hero.WIND_RANGE) &&
    				inRange(monster.pos, opBase.pos, Base.RADIUS + Hero.WIND_PUSH_RANGE)) {
    			
    			if(getDist(monster.pos, opBase.pos) > getDist(heroAttacker.pos, opBase.pos)) {
	    	        Vector center = heroAttacker.pos;
	    	        Vector target = opBase.pos;
	    	        
	    	        int radius = Hero.WIND_RANGE;
	
	    	        Vector vector = new Vector(target.x - center.x, target.y - center.y).normalize();
	    	        Vector posToPushMonsterTo = new Vector(center.x + (vector.x * radius), center.y + (vector.y * radius));
	    	        heroAttacker.command = "SPELL WIND " + posToPushMonsterTo.toIntString() + " DOUBLE WIND!";
    			}
    			else heroAttacker.command = "SPELL WIND " + opBase.pos.toIntString();
    			
    			
    			myBase.mana -= SPELL_COST;
    			return;
    		}
    	}

        	
    	//if see monster that going to enter op base then shield it
    	for(Monster monster : monsters) {
    		if(monster.shieldLife == 0 && myBase.mana >= SPELL_COST * 3 &&
    				inRange(monster.pos, opBase.pos, Base.RADIUS) &&
    				monster.health - (numberOfTurnsToReach(Base.RADIUS - Monster.DIST_TO_DAMAGE_BASE, Monster.DIST_PER_TURN) * Hero.DAMAGE) > 0 &&
    				inRange(monster.pos, heroAttacker.pos, Hero.SHIELD_RANGE)) {
    			heroAttacker.command = "SPELL SHIELD " + monster.id;
    			myBase.mana -= SPELL_COST;
    			return;
    		}
    	}

        //if can control monster to op base then do it
    	for(Monster monster : monsters) {
    		if(monster.shieldLife == 0 && myBase.mana >= SPELL_COST * 3 &&
    				monster.health >= 6 &&
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

    private static Hero getOpAttacker(ArrayList<Hero> opHeros, Base myBase){
        for(Hero opHero : opHeros){
            //if there is opHero close to my base then return him
            if(getDist(opHero.pos, myBase.pos) <= Base.RADIUS + 2000) return opHero;
        }

        return null;
    }
    

	private static void whatToDoWhenThereIsNoThreatFromMonster() {
		
    	for(Hero hero : myHeros){
            if(!hero.isAvailable()) continue;
            
            Vector defensePoint = hero.getDefensePoint(myBase);
            
            Vector bestPosToFarm = getBestPosToFarm(hero.pos, FARM_RANGE);
            
            //Vector res = solve(hero, defensePoint);
            
            //move to farm monster
            if(bestPosToFarm != null) {
            	
                //mark monsters as farmed/handled
                for(Monster monster : monsters) {
                	if(monster.handled) continue;
                	
                	if(inRange(monster.pos, bestPosToFarm, Hero.DAMAGE_RANGE)) monster.handled = true;
                }
                
            	hero.command = "MOVE " + bestPosToFarm.toIntString() + " FARM";
            }
            //move to defensive point
            else hero.command = "MOVE " + defensePoint.toIntString() + " Defense Point";
        }
	}
	
    
    public static Vector bestPosToFarm;
    public static int mostMonstersToFarmAtOnce;
    public static ArrayList<Monster> monstersFarmed;
    
    private static Vector getBestPosToFarm(Vector center, int FARM_RANGE){
    	
    	bestPosToFarm = null;
    	mostMonstersToFarmAtOnce = 0;
    	monstersFarmed = new ArrayList<>();
		
    	getBestPosToFarm(center, FARM_RANGE, 0, new ArrayList<>(), opBase);
    	
    	//mark monsters as farmed
    	for(Monster monster : monstersFarmed) monster.handled = true;
	    
	    return bestPosToFarm;
    }
    
    private static void getBestPosToFarm(Vector center, int FARM_RANGE, int i, ArrayList<Monster> res, Base base) {
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

			if(monster.handled || monster.isFutureThreatToBase(base) || 
		        !inRange(monster.pos, center, FARM_RANGE) ||
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
    			getBestPosToFarm(center, FARM_RANGE, i + 1, res, base);
    			res.remove(res.size() - 1);
    		}
    	}
    }
    
    private static Vector solve(Hero hero, Vector defensePoint) {
    	
    	int mostMana = 0;
    	Vector bestPos = null;
    	double dist = Double.MAX_VALUE;
    	
    	
    	//move hero
    	for(Monster monster : monsters) {
    		if(monster.handled || monster.isFutureThreatToBase(opBase) || 
    		   !inRange(monster.pos, defensePoint, FARM_RANGE) ||
    		   monstorsUnderMyControlIDs.contains(monster.id)) continue;
    		
        	bestPosToFarm = null;
        	mostMonstersToFarmAtOnce = 0;
        	monstersFarmed = new ArrayList<>();
        	ArrayList<Monster> res = new ArrayList<>();
        	res.add(monster);
    		getAvgPosOfAllMonstersInRangeFromMonster(defensePoint, FARM_RANGE, 0, res, opBase);
    		
    		Vector currPos = new Vector(bestPosToFarm);
    		
    		System.err.println("currPos: " + currPos);
    		
    		int resMana = solve(stepTo(hero.pos, bestPosToFarm, Hero.DIST_PER_TURN), 0, defensePoint);
    		
    		
    		if(mostMana < resMana) {
    			mostMana = resMana;
    			bestPos = currPos;
    			dist = getDist(hero.pos, currPos);
    		}
    		
    		if(mostMana == resMana) {
    			double currDist = getDist(hero.pos, currPos);
    			if(dist > currDist) {
    				dist = currDist;
        			mostMana = resMana;
        			bestPos = currPos;
    			}
    		}
    	}
    	
    	System.err.println("END");
    	System.err.println("mostMana: " + mostMana);
    	System.err.println("bestPos: " + bestPos);
    	
    	return bestPos;
    }
    
    private static int solve(Vector heroPos, int depth, Vector defensePoint) {
    	
    	int mostManaGained = 0;
    	int manaGained = 0;
    	
    	HashSet<Integer> addHealthTo = new HashSet<>();
    	ArrayList<Vector> previousPos = new ArrayList<>();
    	
    	//get mana
    	for(Monster monster : monsters) {
    		//should also skip monsters that is handled by wind or control because they can change dir
    		if(monster.handled || monster.health <= 0) continue;
    	
    		if(inRange(heroPos, monster.pos, Hero.DAMAGE_RANGE)) {
    			manaGained++;
    			monster.health -= Hero.DAMAGE_RANGE;
    			addHealthTo.add(monster.id);
    			System.err.println("here");
    		}
    	}
    	
    	//move monsters
    	for(Monster monster : monsters) {
    		if(monster.handled || monster.health <= 0) {
    			previousPos.add(null);
    			continue;
    		}
    		
    		previousPos.add(monster.pos);
    		monster.pos = monster.getPosNextXTurns(1);
    	}
    	
    	System.err.println("manaGained: " + manaGained);

    	//move hero
    	for(Monster monster : monsters) {
    		if(monster.handled || monster.health <= 0) continue;
    		
    		if(depth + 1 == 1) {
    			int resMana = manaGained;
    			mostManaGained = Math.max(mostManaGained, resMana);
    			continue;
    		}
    		
        	bestPosToFarm = null;
        	mostMonstersToFarmAtOnce = 0;
        	monstersFarmed = new ArrayList<>();
        	ArrayList<Monster> res = new ArrayList<>();
        	res.add(monster);
    		getAvgPosOfAllMonstersInRangeFromMonster(defensePoint, FARM_RANGE, 0, res, opBase);
    		
    		int resMana = manaGained + solve(stepTo(heroPos, bestPosToFarm, Hero.DIST_PER_TURN), depth + 1, defensePoint);
    		mostManaGained = Math.max(mostManaGained, resMana);
    	}
    	
		//reset monsters info
		for(int i = 0; i < monsters.size(); i++) {
			Monster monsterToReset = monsters.get(i);
			if(addHealthTo.contains(monsterToReset.id)) monsterToReset.health += Hero.DAMAGE_RANGE;
			
			Vector prevPos = previousPos.get(i);
			if(prevPos != null) monsterToReset.pos = prevPos;
		}
		
		return mostManaGained;
    }
    
    private static void getAvgPosOfAllMonstersInRangeFromMonster(Vector center, int FARM_RANGE, int i, ArrayList<Monster> res, Base base) {
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

			if(monster.handled || monster.isFutureThreatToBase(base) || 
		        !inRange(monster.pos, center, FARM_RANGE) ||
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
    			getAvgPosOfAllMonstersInRangeFromMonster(center, FARM_RANGE, i + 1, res, base);
    			res.remove(res.size() - 1);
    		}
    	}
    }
    
    
    
	private static Monster findMonsterWithSmallestNumberOfTurnsToDamageBase(ArrayList<Monster> monsters) {
        double smallestNumberOfTurnsToDamageBase = Double.MAX_VALUE;
        Monster closestMonster = null;
        boolean isShielded = false;
        
        //if no shielded ones found the all of them
        for(Monster monster : monsters){
            if(monster.handled || monster.numberOfTurnsToDamageMyBase == Integer.MAX_VALUE || monstersIdsWhoCannotBeKilled.contains(monster.id) ||
            monster.numberOfTurnsToDamageMyBase > smallestNumberOfTurnsToDamageBase) continue;
            
            if(monster.numberOfTurnsToDamageMyBase == smallestNumberOfTurnsToDamageBase && isShielded) continue;
            
            //TODO: if there is opAttacker then don't run away from my base if(inRange(monster.pos, myBase.pos, Base.RADIUS + 7000))

            smallestNumberOfTurnsToDamageBase = monster.numberOfTurnsToDamageMyBase;
            closestMonster = monster;
            if(closestMonster.shieldLife > 0) isShielded = true;
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
	
    public static boolean inRange(Vector pos1, Vector pos2, int range) {
    	return (pos1.x - pos2.x) * (pos1.x - pos2.x) + (pos1.y - pos2.y) * (pos1.y - pos2.y) <= range * range;
    }
    
	public static double getDist(Vector pos, Vector pos2){
        return Math.sqrt(Math.pow(pos.x - pos2.x, 2) + Math.pow(pos.y - pos2.y, 2));
    }

	public static int numberOfTurnsToReach(double dist, int speed) {
		if(dist <= 0) return 0;
		
		return (int) Math.ceil(dist / speed);
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

 [] better farm - I should sim the movement of monsters so I would know:
 	1) where to go and not to the current position of them
 	2) will the monster is going out of the screen so I won't be able to earn a lot from her
 	3) choose the monster with most health
 	
 [] if opAttacker can goal monster in this very turn but I also have monster that is going to damage myBase if I won't send this very same hero to deal with her
    Solution:
	in this state I should get all the heros which can stop opAttacker and all the heros which can stop the monster and assign one to opAttacker and one to the monster

 [] should push monster only when I have no choice until then try to damage it
    (I am still afraid from opAttacker that will shield the monster so I can't do it right now, because I want to push her as quick as possible so the opAttacker won't shield it until then)
	
 [] when opAttacker is controlling one of my heros then the other hero should defend him if he can	
	
 [] the number of turns to sim should not be 5 if opAttacker is visible but it should be according to the distance from him and the actions he can make
	
 [] check if opAttacker can make double wind push - HARD	
	
Medium:

 [] find the closestMonster location when reach to her and not the pos I need to be in
 	and after this try to find if there are monster near it so I can damage them toegther by standing between them

Hard:

if(numberOfTurnsToSim <= 0) return addToEval;
    	    	
				Monster closestMonster = findMonsterWithSmallestNumberOfTurnsToDamageBase(monsters);
	            
	            //if have no monster that going to damage my base
	            if(closestMonster == null) return addToEval;
	            
	            
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
//                	//check if can reach monster and wind it
//                	if(smallestNumberOfTurnsToMonsterWind < 100) {
//                		if(smallestNumberOfTurnsToMonsterWind == 0 && myBase.mana >= SPELL_COST && closestMonster.shieldLife == 0 &&
//                		inRange(hero.pos, closestMonster.pos, Hero.WIND_RANGE)) windMonster(closestHero, posToGoToACTIONMonster, smallestNumberOfTurnsToMonsterWind);
//                		else goToMonsterToWindIt(hero, posToGoToACTIONMonster, smallestNumberOfTurnsToMonsterWind, closestMonster);
//                	}
//                	//check if can reach monster and control it
//                	else if(smallestNumberOfTurnsToMonsterControl < 100) {
//                		if(smallestNumberOfTurnsToMonsterControl == 0 && myBase.mana >= SPELL_COST && closestMonster.shieldLife == 0 &&
//                		inRange(hero.pos, closestMonster.pos, Hero.CONTROL_RANGE)) controlMonster(closestHero, posToGoToACTIONMonster, smallestNumberOfTurnsToMonsterControl, closestMonster);
//                		else goToMonsterToControlIt(hero, posToGoToACTIONMonster, smallestNumberOfTurnsToMonsterControl, closestMonster);
//                	}
//                	else {
//	                	//This hero by himself can't kill the monster but maybe more heros will be able to
//	                	finishSim = true
//                	}
                	return addToEval;
                }
                else{
                	
//                	int rangeFromRangeToPushMonster = Base.RADIUS;
//                	if(opAttacker != null && inRange(closestMonster.pos, opAttacker.pos, Hero.DIST_PER_TURN * 2 + Hero.WIND_RANGE)) rangeFromRangeToPushMonster = Base.RADIUS + 2000;
//                	// if monster is inside myBase and I can wind it and
//                	//the monster has more than Hero.DAMAGE life then do it
//
////                	if(myBase.mana >= 100 && closestMonster.shieldLife == 0 && opAttacker != null && !inRange(closestMonster.pos, opAttacker.pos, Hero.DIST_PER_TURN * 3 + Hero.WIND_RANGE) &&
////                    inRange(myBase.pos, closestMonster.pos, Base.RADIUS + 1500) && closestMonster.health > Hero.DAMAGE &&
////                    inRange(closestHero.pos, closestMonster.pos, Hero.CONTROL_RANGE)) controlMonsterOutOfMyBase(closestHero, posToGoToACTIONMonster, closestMonster);
//                	if(myBase.mana >= 100 && closestMonster.shieldLife == 0 &&
//                    inRange(myBase.pos, closestMonster.pos, rangeFromRangeToPushMonster) && closestMonster.health > Hero.DAMAGE * 2 &&
//                    inRange(closestHero.pos, closestMonster.pos, Hero.WIND_RANGE)) windMonsterOutOfMyBase(closestHero, posToGoToACTIONMonster, smallestNumberOfTurnsToReachMonster);
//                    else damageMonster(closestHero, posToGoToACTIONMonster, smallestNumberOfTurnsToReachMonster, closestMonster);
                	
                	//move monsters
                	for(Monster monster : monsters) {
                		if(monster.handled || monster.health <= 0) continue;
                		
                		monster.pos = monster.getPosNextXTurns(1);
                		if(closestMonster.id == monster.id) {
                			posToGoToACTIONMonster = hero.getPosToGoToActionMonster(monster, Hero.DAMAGE_RANGE);
                		}
                		if(monster.shieldLife > 0) monster.shieldLife -= 1;
                	}
                	
                	if(hero.pos == null) System.err.println("hero.pos: " + hero.pos);
                	else if(posToGoToACTIONMonster == null) System.err.println("posToGoToACTIONMonster: " + posToGoToACTIONMonster);

                	//move hero
                	hero.pos = stepTo(hero.pos, posToGoToACTIONMonster, Hero.DIST_PER_TURN);
                	
                	int numberOfTurnsToActionMonster = smallestNumberOfTurnsToReachMonster + (int) Math.ceil(closestMonster.health * 1.0 / Hero.DAMAGE);
                	
                	double res = addToEval + simBestPos(hero, posToGoToACTIONMonster, Hero.DAMAGE_RANGE, numberOfTurnsToActionMonster, numberOfTurnsToSim, closestMonster.id);
                	closestMonster.handled = true;
                	hero.monstersHandling.add(closestMonster.id);
                	return res;
                }
*/
