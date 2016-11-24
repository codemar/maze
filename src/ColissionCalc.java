import java.util.Date;

/**
 * Created by Omar on 17.01.2016.
 */
public class ColissionCalc {
    private Player player;
    private DynamicEnemy[] dEnemyList;
    private StaticEnemy[] sEnemyList;
    private long damageDelay = 0;
    private long latestTimer = new Date().getTime();

    public ColissionCalc(Player player, DynamicEnemy[] dEnemyList, StaticEnemy[] sEnemyList) {
        this.player = player;
        this.dEnemyList = dEnemyList;
        this.sEnemyList = sEnemyList;
    }

    public void refresh() {
        long tempTime = new Date().getTime();

        if (tempTime - latestTimer > damageDelay) {

            damageDelay = 0;
            checkDynamicDamage();
            checkStaticDamage();


            latestTimer = tempTime;

        }


    }

    private void damage(int amount) {
        player.damage(amount);
        damageDelay = 1000;
    }


    private void checkDynamicDamage() {
        for (DynamicEnemy dynamicEnemy : dEnemyList) {
            if(dynamicEnemy.isAlive()) {
                int[] hitBox = dynamicEnemy.getNextPos();

                if (hitBox != null) {
                    if (player.getxPos() == hitBox[0]
                            && player.getyPos() == hitBox[1]) {
                        damage(1);
                    }
                }
            }
        }


    }


    private void checkStaticDamage() {
        for (StaticEnemy staticEnemy : sEnemyList) {

            if (checkStaticRadius(staticEnemy)) {
                if (staticEnemy.getCycleCount()!=0)
                {

                    damage(1);
                }
                else
                {
                    staticEnemy.death();
                }

            }
        }


    }


    private boolean checkStaticRadius(StaticEnemy staticEnemy) {
        if (staticEnemy.isAlive()) {
            int cycle = staticEnemy.getCycleCount();
            {
                int xUnten, xOben, yUnten, yOben;

                xUnten = staticEnemy.getxPos() - cycle;
                xOben = staticEnemy.getxPos() + cycle;
                yUnten = staticEnemy.getyPos() - cycle;
                yOben = staticEnemy.getyPos() + cycle;


                if (player.getxPos() >= xUnten && player.getxPos() <= xOben) {
                    if (player.getyPos() >= yUnten && player.getyPos() <= yOben) {

                        return true;


                    }

                }

            }

        }
        return false;


    }
}
