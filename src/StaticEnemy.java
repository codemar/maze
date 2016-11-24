import com.googlecode.lanterna.terminal.Terminal;

import java.util.Date;

public class StaticEnemy {
    private final char sign = '\u06DE';
    boolean direction = true;
    private Terminal.Color foreGroundColor = Terminal.Color.MAGENTA;
    private Map map;
    private int cycleCount = 0;
    private long delay = 1000;
    private final long cycleDelay = 1000;
    private final long levelDelay = 100;
    private long lastTime = new Date().getTime();
    private int yPos, xPos;
    private boolean alive = true;
    private Game game;

    public StaticEnemy(int xPos, int yPos, Map map,Game game) {
        this.yPos = yPos;
        this.xPos = xPos;
        this.map = map;
        this.game = game;
    }


    public int getyPos() {
        return yPos;
    }

    public boolean isAlive() {
        return alive;
    }

    public int getxPos() {
        return xPos;
    }


    public void cycle() { //hat einen zyklus, eine warte zeit zwischen den zyklen, cycleDElay
        if (direction) {


            switch (cycleCount) {//cyclecount wird erst stufenweise auf 3 erhöht
                case 0:
                    map.addBlock(xPos,yPos,sign,true, foreGroundColor);
                case 1:
                case 2:
                    cycleCount++;
                    delay = levelDelay;
                    break;
                case 3:
                    direction = false;
                    delay = levelDelay;
                    cycleCount--;
                    break;

            }


        } else {
            switch (cycleCount) //und dann wieder auf 0 erniedrigt
            {
                case 0:
                    direction = true;
                    delay = cycleDelay;
                    map.removeBlock(xPos,yPos);
                    break;
                case 1:
                case 2:
                    cycleCount--;
                    delay = levelDelay;
                    break;
                case 3:
                    break;
            }
        }

    }


    public void refresh() {

        long tempTime = new Date().getTime();

        if (tempTime - lastTime > delay) {


            cycle();
            draw();



            lastTime = new Date().getTime();


        }
    }

    public void draw() {
        reDraw(cycleCount+1);
        draw(cycleCount);
    }

    private void reDraw(int count)
    {
        for (int i = -count; i <= count; i++) {
            for (int j = -count; j <= count; j++) {
                map.applyBackgroundColor(xPos + i, yPos + j, Terminal.Color.BLACK);
            }
        }


    }
    private void draw(int count)
    { //zeichnet das schadensfeld, abhängig vom radius, der in cyclecount angegeben ist
        for (int i = -count; i <= count; i++) {
            for (int j = -count; j <= count; j++) {
                if (map.getBlock(xPos + i, yPos + j) != null) {
                    if (map.getBlock(xPos + i, yPos + j).getSign() != 'X') {
                        map.applyBackgroundColor(xPos + i, yPos + j, Terminal.Color.RED);
                    }
                } else {
                    map.applyBackgroundColor(xPos + i, yPos + j, Terminal.Color.RED);
                }
            }
        }
        if(count!=0) {
            map.draw(xPos, yPos, sign, foreGroundColor, Terminal.Color.RED);
        }
        else
        {
            map.draw(xPos, yPos, sign, foreGroundColor, Terminal.Color.GREEN);
        }
    }

    public void death() {

        alive = false;
        game.addPoints(40);
        map.reDraw(xPos,yPos);

    }


    public int getCycleCount() {
        return cycleCount;
    }
}




