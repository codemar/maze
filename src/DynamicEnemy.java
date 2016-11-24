import com.googlecode.lanterna.terminal.Terminal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Random;

/**
 * Created by Omar on 11.01.2016.
 * Der dynamische Gegner ist eine Reihe von Buchstaben im Alphabet und hat Lücken,
 * er stirbt wenn man diese Lücken durch Tastatureingabe ausfüllt
 */
public class DynamicEnemy extends Block {
    public static final Random random = new Random();
    private final Terminal.Color color = Terminal.Color.MAGENTA;
    private final int max = 10; //minmale und maximale länge eines wurms
    private final int min = 5;
    long lastTime = new Date().getTime(); //aktuelle systemzeit wird gespeichert
    boolean alive = true;
    private char sign = 'E';
    private int yPos, xPos;
    private int playerXPos, playerYPos;
    private Waypoint waypoint;
    private Map map;
    private ArrayList<Integer> directionList;
    private ArrayList<Integer[]> lastPos;
    // actualletterlist ist die liste von buhstaben, wenn die X nicht da wären
    //letterlist ist die liste von buchstaben die angezeigt werden,
    private ArrayList<Character> letterList, actualLetterList;
    private int length;
    private int gapCount;
    private ArrayList<Integer> gapList;
    private ArrayList<Character> inputList;
    private int maxGaps;
    private int minGaps;
    private char latestChar = '\u075D';
    private Game game;
    private int delay = 100;
    private int keyCount = 0;

    public DynamicEnemy(int xPos, int yPos, Map map, Game game) {
        super('E', false, Terminal.Color.MAGENTA);
        this.game = game;
        this.yPos = yPos;
        this.xPos = xPos;
        this.map = map;
        playerXPos = game.getPlayer().getxPos(); //fragt koordinaten des spielers ab
        playerYPos = game.getPlayer().getyPos(); //wird für wegfindung gebraucht
        lastPos = new ArrayList<>(); //speichert die letzten positionen des kopfes des wurms, damit man die folgenden buchstaben zeichnen kann
        this.length = random.nextInt(max - min) + min; //sucht länge zufällig zwischen maximaler und minimaler länge raus
        actualLetterList = new ArrayList<>();
        sign = (char) (random.nextInt(25 - length) + 'A');//sign ist der erste buchstabe des wurms, der zufällig gewählt wird
        for (int i = 1; i < length; i++) {
            actualLetterList.add((char) (sign + i)); //hier werden die auf sign im alphabet folgenden buchstaben in die actualletterlist eingetragen
        }
        letterList = new ArrayList<Character>(actualLetterList);

        maxGaps = (int) (length * 0.6); //anzahl der maximalen und minmalen lücken
        minGaps = (int) (length * 0.3); //abhängig von der länge des wurms

        gapCount = random.nextInt(maxGaps - minGaps) + minGaps; //anzahl der lücken wird wieder zufällig zwischen minimum und maximum generiert
        gapList = new ArrayList<>(gapCount);
        inputList = new ArrayList<>(gapCount);

        for (int i = 0; i < gapCount; i++) {

            boolean contains = true;

            while (contains) {
                int number = random.nextInt(length - 1);//gapList beinhaltet die stellen, an denen die lücken sind
                if (!gapList.contains(number)) { //bei AXBC ist gapcount z.b. die liste mit der zahl 1
                    gapList.add(number); //wird hier zufällig generiert, darf aber nicht doppelt vorkommen, deshalb variable contains
                    contains = false;
                }
            }
        }


        Collections.sort(gapList); //die stellen werden sortiert

        for (int i = 0; i < gapCount; i++) {
            inputList.add(0, 'X'); //inputlist beinhaltet die zuletzt vom spieler eingegebenen buchstaben, wird am anfang mit X gefüllt

        }


        waypoint = new Waypoint(xPos, yPos, playerXPos, playerYPos, this.map); //neuer waypoint wird erstellt
        waypoint.pathFind(); //weg wird gefunden
        directionList = waypoint.getDirectionList(); //liste mit richtungen wird abgefragt, der wurm folgt nun den richtungen
        map.draw(xPos, yPos, this.sign, color, Terminal.Color.BLACK); //wurmkopf wird gezeichnet
    }

    public int[] getNextPos() { //fragt die "nächste" position des wurms ab, ist aber eigentlich nur die position, auf die der wurmkopf gerade "zeigt"
        if (lastPos.size() > 0) {
            int xDif = xPos - lastPos.get(0)[0];
            int yDif = yPos - lastPos.get(0)[1];

            return new int[]{xDif + xPos, yDif + yPos};

        } else {
            return null;
        }

    }

    public int getyPos() {
        return yPos;
    }

    public int getxPos() {
        return xPos;
    }

    public void refresh() { //refreshmethode des wurms
        refreshSigns();
        checkDeath();
        checkKeyEat();


        if (playerYPos != game.getPlayer().getyPos() || playerXPos != game.getPlayer().getxPos()) {
            playerXPos = game.getPlayer().getxPos();
            playerYPos = game.getPlayer().getyPos();
            waypoint.changeDirection(playerXPos, playerYPos); //wenn die position des spielers sicher verändert hat, holt sich der wurm eine neue aktuelle directionlist von seinem wegpunktfinder
            waypoint.changeStart(xPos, yPos);
            waypoint.pathFind();
            directionList = waypoint.getDirectionList();
        }
        if (directionList.size() > 0) {
            move();
        }
    }

    public void checkKeyEat() {

        int nextPos[] = getNextPos();
        if(nextPos!=null) {
            Block block = map.getBlock(nextPos[0], nextPos[1]);

            if (block != null) {
                if (block.getId() == 5) {
                    map.removeBlock(nextPos[0], nextPos[1]);
                    keyCount++;
                }
            }
        }
    }

    public void keyEat() {
        keyCount++;
    }

    public void refreshSigns() { //erneuert die zeichen, wenn der spieler etwas eingibt
        if (latestChar != Input.getLatestChar()) {
            latestChar = Input.getLatestChar();
            if (latestChar != ' ') {
                inputList.add(0, Character.toUpperCase(latestChar)); //immer in großsbuchstaben
                if (inputList.size() > gapCount) {
                    inputList.remove(gapCount);
                } //unnötige elemente werden entfernt
                draw(Terminal.Color.MAGENTA);

            }
            for (int i = 0; i < inputList.size(); i++) {
                letterList.set(gapList.get(i), inputList.get(inputList.size() - i - 1)); //letterlist wird also aktualisiert, nur elemente die in gaplist stehen




            }
            draw(Terminal.Color.MAGENTA);
        }



    }

    private void checkDeath() { //prüft ob die letterlist der actualletterlist übereinstimmt, wenn ja stirbt der wurm
        if (actualLetterList.equals(letterList)) {
            alive = false;
            unDraw();
            draw(Terminal.Color.RED);

            map.getBlock(xPos, yPos).setId(6); //id6 wird vom spieler als punkte betrachtet
            map.getBlock(xPos, yPos).setCollides(false);

            for (int i = 0; i < lastPos.size(); i++) {
                map.getBlock(lastPos.get(i)[0], lastPos.get(i)[1]).setId(6);
                map.getBlock(lastPos.get(i)[0], lastPos.get(i)[1]).setCollides(false); //wird auch für die restlichen elemente des wurms gemacht
            }

            ArrayList<Integer> list = new ArrayList<>();
            for (int i = 0; i < length; i++) {
                list.add(i); //der teil ist dafür da, einen schlüssel in der "leiche" des wurms zu erstellen, falls er einen gegessen hat
            }

            Random random = new Random();

            for (int i = 0; i < keyCount; i++)
            {
                int number = random.nextInt(list.size()-1);
                map.removeBlock(lastPos.get(number)[0],lastPos.get(number)[1]);
                map.addBlock(lastPos.get(number)[0],lastPos.get(number)[1],'S',false, Terminal.Color.YELLOW);
                map.getBlock(lastPos.get(number)[0],lastPos.get(number)[1]).setId(5);
                map.reDraw(lastPos.get(number)[0],lastPos.get(number)[1]);

                list.remove(number);
            }

        }


    }

    public boolean isAlive() {
        return alive;
    }


    public void move() {
        long tempTime = new Date().getTime();
        if (tempTime - lastTime > delay) { //wurm bewegt sich nur wenn delay abgelaufen ist


            unDraw();


            lastPos.add(0, new Integer[]{xPos, yPos});
            if (lastPos.size() > length - 1) {
                lastPos.remove(length - 1);
            }
            int dir = directionList.get(0); //nimmt die richtung aus der directionlist, die von waypoint erzeugt wurde
            directionList.remove(0);
            int x, y;
            switch (dir) {
                case 1:
                    x = 0;
                    y = -1;
                    break;
                case 2:
                    x = 1;
                    y = 0;
                    break;
                case 3:
                    x = 0;
                    y = 1;
                    break;
                case 4:
                    x = -1;
                    y = 0;
                    break;
                default:
                    x = 0;
                    y = 0;
                    break;
            }
            xPos += x;
            yPos += y;


            draw(color);

            lastTime = new Date().getTime();
        }
    }

    private void unDraw() { //methode um letzten teil des wurms beim bewegen wieder zu löschen
        map.removeBlock(xPos, yPos);
        map.reDraw(xPos, yPos);
        for (int i = 0; i < lastPos.size(); i++) {
            map.removeBlock(lastPos.get(i)[0], lastPos.get(i)[1]);
            map.reDraw(lastPos.get(i)[0], lastPos.get(i)[1]);
        }
    }

    public ArrayList<Character> getLetterList() {
        return letterList;
    }

    public ArrayList<Character> getActualLetterList() {
        return actualLetterList;
    }

    private void draw(Terminal.Color color) { // zeichnet den wurm
        for (int i = 0; i < lastPos.size(); i++) {
            Terminal.Color gapColor;
            if (gapList.contains(i)) {
                gapColor = Terminal.Color.WHITE;
            } else {
                gapColor = color;
            }

            map.draw(lastPos.get(i)[0], lastPos.get(i)[1], letterList.get(i), gapColor, Terminal.Color.BLACK);
            map.addBlock(lastPos.get(i)[0], lastPos.get(i)[1], letterList.get(i), true, gapColor);
            map.getBlock(lastPos.get(i)[0],lastPos.get(i)[1]).setId(9); // erst alle hinteren teile des wurms
        }

        map.draw(xPos, yPos, sign, color, Terminal.Color.BLACK); //dann den kopf des wurms
        map.addBlock(xPos, yPos, sign, true, color);
        map.getBlock(xPos,yPos).setId(9);

        game.drawStaticEnemies();


    }

    @Override
    public char getSign() {
        return sign;
    }
}
