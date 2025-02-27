/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.GNS;

import ai.abstraction.pathfinding.PathFinding;
import rts.GameState;
import rts.PhysicalGameState;
import rts.ResourceUsage;
import rts.UnitAction;
import rts.units.Unit;
import rts.units.UnitType;
import util.XMLWriter;

import java.util.Random;

/**
 *
 * @author santi
 */
public class Build extends AbstractAction {
    UnitType type;
    int x,y;
    PathFinding pf;
    
    public Build(Unit u, UnitType a_type, int a_x, int a_y, PathFinding a_pf) {
        super(u);
        type = a_type;
        x = a_x;
        y = a_y;
        pf = a_pf;
    }

    public boolean completed(GameState gs) {
        PhysicalGameState pgs = gs.getPhysicalGameState();
        Unit u = pgs.getUnitAt(x, y);
        if (u!=null) return true;
        return false;
    }
    
    
    public boolean equals(Object o)
    {
        if (!(o instanceof Build)) return false;
        Build a = (Build)o;
        if (type != a.type) return false;
        if (x != a.x) return false;
        if (y != a.y) return false;
        if (pf.getClass() != a.pf.getClass()) return false;
        
        return true;
    }
    

    public void toxml(XMLWriter w)
    {
        w.tagWithAttributes("Build","unitID=\""+unit.getID()+"\" type=\""+type.name+"\" x=\""+x+"\" y=\""+y+"\" pathfinding=\""+pf.getClass().getSimpleName()+"\"");
        w.tag("/Build");
    }    
    
    public UnitAction execute(GameState gs, ResourceUsage ru) {
        PhysicalGameState pgs = gs.getPhysicalGameState();
//        System.out.println("findPathToAdjacentPosition to " + unit.getX() + "," + unit.getY() + " from Build: (" + x + "," + y + ")");
        UnitAction move = pf.findPathToAdjacentPosition(unit, x+y*pgs.getWidth(), gs, ru);
//        System.out.println("Move: " + move);
        if (move!=null) {
            if (gs.isUnitActionAllowed(unit, move)) return move;
            return null;
        }
       
        // build:
        UnitAction ua = null;
        if (x == unit.getX() &&
            y == unit.getY()-1) ua = new UnitAction(UnitAction.TYPE_PRODUCE, UnitAction.DIRECTION_UP,type);
        if (x == unit.getX()+1 &&
            y == unit.getY()) ua = new UnitAction(UnitAction.TYPE_PRODUCE, UnitAction.DIRECTION_RIGHT,type);
        if (x == unit.getX() &&
            y == unit.getY()+1) ua = new UnitAction(UnitAction.TYPE_PRODUCE, UnitAction.DIRECTION_DOWN,type);
        if (x == unit.getX()-1 &&
            y == unit.getY()) ua = new UnitAction(UnitAction.TYPE_PRODUCE, UnitAction.DIRECTION_LEFT,type);
        if (ua!=null && gs.isUnitActionAllowed(unit, ua)) return ua;        
        
//        System.err.println("Build.execute: something weird just happened " + unit + " builds at " + x + "," + y);
        int m = new Random().nextInt(5);
        if (m == 4) return null;
        UnitAction m1 = new UnitAction(UnitAction.TYPE_MOVE, m);
        UnitAction m2 = new UnitAction(UnitAction.TYPE_MOVE, (m + 1) % 4);
        UnitAction m3 = new UnitAction(UnitAction.TYPE_MOVE, (m + 2) % 4);
        UnitAction m4 = new UnitAction(UnitAction.TYPE_MOVE, (m + 3) % 4);
        if (gs.isUnitActionAllowed(unit, m1)) return m1;
        if (gs.isUnitActionAllowed(unit, m2)) return m2;
        if (gs.isUnitActionAllowed(unit, m3)) return m3;
        if (gs.isUnitActionAllowed(unit, m4)) return m4;
        return null;
    } 
}
