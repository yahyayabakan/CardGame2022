package structures.units;

import structures.basic.Unit;


/*This is the IroncliffGuardian Class which Can be summoned anywhere on the board
It has the provoke ability.
Provoke: If an enemy unit can attack and is adjacent to any unit with provoke, then it can
only choose to attack the provoke units. Enemy units cannot move when
provoked.

*/
public class IroncliffGuardian extends Unit {
    public IroncliffGuardian(){
        super.hasProvoked = true;
    }
}
