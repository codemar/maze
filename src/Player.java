import com.googlecode.lanterna.terminal.Terminal;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Omar on 02.01.2016.
 */
public class Player extends Block {
    private final char finalSign = '\u263A';
    private Terminal terminal;
    private Map map;
    private int life;
    private int xPos;
    private int yPos;
    private int keyCount = 0;
    private Terminal.Color color;
    private char sign = finalSign;
    private ArrayList<Integer[]> lastPos;
    private Game game;
    private boolean blinking = false;
    private long latestBlinkTime = new Date().getTime();
    private long blinkTime;




    public Player(Terminal terminal, int life, int xPos, int yPos, Map map, Game game) {
        this.game = game;
        this.map = map;
        this.xPos = xPos;
        this.yPos = yPos;
        this.life = life;
        this.terminal = terminal;
        lastPos = new ArrayList<>();
        switch(life)
        {
            case 1:
                color = Terminal.Color.RED;
                break;
            case 2:
                color = Terminal.Color.YELLOW;//frabe variiert jenach leben des spielers
                break;
            case 3:
                color = Terminal.Color.GREEN;
                break;
            default:
                color = Terminal.Color.GREEN;
                break;
        }

        map.addBlock(xPos, yPos, sign, true, color);
        draw();
    }


    public void incKey() {
        keyCount++;
    } //wird aufgereufen wenn der spieler ein schlüssel findet

    public void damage(int amount) {
        life -= amount;
        if (life <= 0) {
            game.stop(2);
        }

        switch (life) {
            case 2:
                color = Terminal.Color.YELLOW;
                break;
            case 1:
                color = Terminal.Color.RED;
                break;
        }

        map.addBlock(xPos, yPos, sign, true, color);
        game.addPoints(-20);

        blinking = true;
        blinkTime = new Date().getTime();


    }


    public void blink() { //das typische "blinken" des spielers wenn dieser schaden nimmt, das man aus vielen videospielen kennt.
        long tempTime = new Date().getTime();

        if (blinking && tempTime - latestBlinkTime > 200) {
            if (sign != ' ') {
                sign = ' ';
                map.removeBlock(xPos, yPos);
                map.addBlock(xPos, yPos, ' ', true, color); //zeichnet eine leerstelle statt des zeichens
                draw();
            } else {
                sign = finalSign;
                map.removeBlock(xPos, yPos);
                map.addBlock(xPos, yPos, finalSign, true, color); //zeichnet nach dem delay(200) wieder das zeichen des spielers
                draw();
            }

            latestBlinkTime = new Date().getTime();

            if (tempTime - blinkTime > 1000) {
                sign = '\u263A';
                blinking = false;
            }
        }

    }


    public int getyPos() {
        return yPos;
    }

    public int getxPos() {
        return xPos;
    }

    public void refresh(int direction) {
        int x = 0;
        int y = 0;

            switch (direction) {
                case 1:
                    if (yPos > 0) { //bewegung des spielers
                        y = -1;
                    }
                    break;
                case 2:
                    if (xPos < map.getWidth() - 1) {
                        x = 1;
                    }
                    break;
                case 3:
                    if (yPos < map.getHeight() - 1) {
                        y = 1;
                    }
                    break;
                case 4:
                    if (xPos > 0) {
                        x = -1;
                    }
                    break;
        }

        if (!(x == 0 && y == 0)) {
            x += xPos;
            y += yPos;
            checkBlock(x, y);
            if (map.begehbar(x, y)) { //nur wenn die koordinate auch begehbar ist
                move(x, y);
                scroll(); //ruft die scroll funktion auf, damit das bild weiter scrollt, falls der spieler das spielfeld verlässt
            }
        }
        if (keyCount > 0) {
            checkKeyEat();
        }
        blink();
    }

    public int getLife() {
        return life;
    }

    public int getKeyCount() {
        return keyCount;
    }

    private void scroll() {

        map.scrollX(xPos / terminal.getTerminalSize().getColumns());
        map.scrollY(yPos / terminal.getTerminalSize().getRows());

    }

    private void checkKeyEat() { //überprüft ob ein wurm den schlüssel, der am spieler dran hängt gegessen hat
        for (int i = 0; i < game.getDynamicEnemyList().length; i++) {
            DynamicEnemy dn = game.getDynamicEnemyList()[i];
            if (dn.isAlive()) {
                for (int j = 0; j < keyCount; j++) {
                    if(lastPos.size()>j) {
                        if (dn.getxPos() == lastPos.get(j)[0] && dn.getyPos() == lastPos.get(j)[1]) {
                            keyCount--;
                            dn.keyEat();
                        }
                    }
                }
            }
        }
    }

    private void checkBlock(int x, int y) {
        Block block = map.getBlock(x, y);

        if (block != null) {
            switch (block.getId()) {
                case 2: //spieler am ausgang, falls er einen schlüssel hat endet das spiel und er bekommt punkte
                    if (keyCount > 0) {
                        game.addPoints(50);
                        game.stop(1);
                    }
                    break;
                case 5:
                    game.addPoints(20);
                    incKey(); //spieler findet einen schlüssel
                    map.removeBlock(x, y);
                    break;
                case 6: game.addPoints(15);

            }
        }
    }


    public void move(int x, int y) {
        for (int i = 0; i < keyCount; i++) {

            if(lastPos.size()>i) {
                map.removeBlock(lastPos.get(i)[0], lastPos.get(i)[1]);
            }


        }
        lastPos.add(0, new Integer[]{xPos, yPos});
        if (lastPos.size() > 5) {
            lastPos.remove(5);

        }
        map.removeBlock(xPos, yPos);
        unDraw();
        this.xPos = x;
        this.yPos = y;
        map.addBlock(x, y, '\u263A', true, color);
        draw();
        for (int i = 0; i < keyCount; i++) {

            if(lastPos.size()>i) {
                map.addBlock(lastPos.get(i)[0], lastPos.get(i)[1], 'S', false, Terminal.Color.YELLOW);
                map.getBlock(lastPos.get(i)[0], lastPos.get(i)[1]).setId(10);
            }


        }


    }

    public void draw() {
        for (int i = 0; i < keyCount; i++) {
            if(lastPos.size()>i) {
                map.draw(lastPos.get(i)[0], lastPos.get(i)[1], 'S', Terminal.Color.YELLOW, Terminal.Color.BLACK);
            }

        }
        map.draw(xPos, yPos, sign, color, Terminal.Color.BLACK);

        game.drawStaticEnemies();
    }


    public void unDraw() {
        map.reDraw(xPos, yPos);
        for (int i = 0; i < keyCount; i++) {
            if(lastPos.size()>i+1) {
                map.reDraw(lastPos.get(i + 1)[0], lastPos.get(i + 1)[1]);
            }

        }
    }

    public void setKeyCount(int keyCount) {
        this.keyCount = keyCount;
    }
}
