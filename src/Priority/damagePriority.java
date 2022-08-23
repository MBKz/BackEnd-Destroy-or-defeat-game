package Priority;

import Forces.Fighters;

public class damagePriority extends Priority {
    @Override
    public int compare(Object o1, Object o2) {
        if(((Fighters)o1).getAttackDamage() < ((Fighters)o2).getAttackDamage()){
            return-1;
        }
        else if(((Fighters)o1).getHealth() > ((Fighters)o2).getHealth()){
            return 1;
        }
        return 0;
    }
}
