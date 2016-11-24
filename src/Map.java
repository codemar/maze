import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalSize;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;

/**
 * Created by Omar on 02.01.2016.
 */
public class Map {

    private final String path = "level.properties";  //Um ein anderes Level auszuprobieren ändert man diesen String auf den entsprechenden dateinamen.
    private TerminalSize terminalSize;
    private int[][] dSpawnPointList;
    private int[][] sSpawnPointList;
    private int[][] spawnPointList;
    private int[] spawnPoint;
    private int xScrolling = 0;
    private int yScrolling = 0;
    private Block[][] map = null;
    private int width, height, nrIn;
    private Terminal terminal;
    private int terminalX, terminalY;
    private int xOffset;
    private int yOffset;

    public Map(int width, int height, int nrIn, int sTraps, int dTraps, Terminal terminal) {


        this.terminal = terminal;
        this.width = width;
        this.height = height;
        this.nrIn = nrIn;


        map = new Block[width][height];
        spawnPointList = new int[nrIn][2];
        dSpawnPointList = new int[dTraps][2];
        sSpawnPointList = new int[sTraps][2];
        spawnPoint = new int[2];

        init();
        drawMap();

    }


    private void init() {//initialisieren der map aus der level.properties datei

        File file = new File(path);

        terminalSize = terminal.getTerminalSize();
        terminalX = terminalSize.getColumns();
        terminalY = terminalSize.getRows();


        try {
            Scanner scanner = new Scanner(file); //scanner um die datei auszulesen
            int spawnCount = 0; //diese counts existieren, da die gegner und der spieler nicht als block erstellt werden. die koordinaten die in der properties datei für den spawn der gegner/ des spieler festgelegt wurden müsse nalso gespeichert und an die entsprechenden klassen vergeben werden. die counts geben die position im array an
            int sEnemyCount = 0;
            int dEnemyCount = 0;
            while (scanner.hasNext()) {
                String s = scanner.nextLine(); //zeile für zeile wird verarbeitet
                if (!((s.charAt(0) == '#') || s.charAt(0) == 'W' || s.charAt(0) == 'H')) { //einträge width, height und kommentare der properties datei werden ignoriert
                    String ss = s.substring(0, s.length() - 2); //nimmt die werte vor dem "="
                    String[] coordinates = ss.split(","); //teilt sie in einzelne coordinaten, da x und y koordinate in der datei durch, getrennt werden
                    int xPos = Integer.parseInt(coordinates[0]); //x und y koordinaten werden zugewiesen
                    int yPos = Integer.parseInt(coordinates[1]);
                    int value = Character.getNumericValue(s.charAt(s.length() - 1)); //der wert des eintrags, der immer das letzte zeichen ist

                    Block block;
                    switch (value) { //hier wird noch value differenziert
                        case 0:
                            block = new Block('░', true, Terminal.Color.WHITE, value); //wand block
                            break;
                        case 1:
                            block = null; //eingang, die koordinaten werden in die spawnpointlist gespeichert und der counter inkrementiert, damit der nächste mögliche spawnpoint nicht den aktuellen überschreibt
                            spawnPointList[spawnCount][0] = xPos;
                            spawnPointList[spawnCount][1] = yPos;
                            spawnCount++;
                            break;
                        case 2:
                            block = new Block('□', true, Terminal.Color.RED, value); //ausgänge werden als block erstellt

                            break;
                        case 3:
                            block = null;
                            sSpawnPointList[sEnemyCount][1] = yPos; //die spawnpoints werden wieder in einer liste gespeichert
                            sSpawnPointList[sEnemyCount][0] = xPos;
                            sEnemyCount++;
                            break;
                        case 4:
                            dSpawnPointList[dEnemyCount][0] = xPos; //wie statische gegner
                            dSpawnPointList[dEnemyCount][1] = yPos;
                            dEnemyCount++;
                            block = null;
                            break;
                        case 5:
                            block = new Block('S', false, Terminal.Color.YELLOW, value); //schlüssel werden als block erstellt
                            break;
                        default:
                            block = new Block(' ', false); //nur zur sicherheit
                            break;
                    }

                    map[xPos][yPos] = block; //block wird in das 2d array gespeichert
                }
            }
        } catch (FileNotFoundException e) {
        }


        Random random = new Random();
        int spawn = random.nextInt(nrIn);

        int xSpawn = spawnPointList[spawn][0]; //aus den spawnpoints wird ein zufälliger spawnpoint ausgewählt
        int ySpawn = spawnPointList[spawn][1];//ich habe mich dazu entschieden eingänge nicht zu zeichnen, da sie keine bedeutung im spiel haben



        xScrolling = xSpawn / terminalX; //die Scrolling werte speichern wie oft nach oben oder zur seite gescrollt werden muss
        yScrolling = ySpawn / terminalY; //scrollen heißt dabei um eine Terminallänge nach rechts verschieben

        spawnPoint[0] = xSpawn; //spawnpointkoordinaten werden gespeichert, spawnpoint kann dann mit getspawnpoint() abgerufen werden
        spawnPoint[1] = ySpawn;

    }


    public void removeBlock(int xPos, int yPos) { //entfernt den block bei xpos,ypos

        map[xPos][yPos] = null;

    }


    public void addBlock(int xPos, int yPos, char sign, boolean collides, Terminal.Color foreGroundColor) {
        map[xPos][yPos] = new Block(sign, collides, foreGroundColor); //man kann einen block manuell hinzufügen lassen.


    }


    public int[][] getdSpawnPointList() {
        return dSpawnPointList;
    }

    public int[][] getsSpawnPointList() {
        return sSpawnPointList;
    }

    public int[] getSpawn() {


        return spawnPoint;

    }

    public Block getBlock(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) { //gibt den block bei x,y zurück
            return map[x][y];
        } else {
            return null;
        }
    }


    public void saveMap() { //aktualisiert die properties datei auf den neusten stand, wird vor dem speichern aufgerufen
        try {
            Properties prop = new Properties();


            for (int i = 0; i < map.length; i++) {
                for (int j = 0; j < map[i].length; j++) {
                    if (map[i][j] != null) {
                        {
                            int id = map[i][j].getId();
                            if(id>=0&&id<=5)
                            {

                                prop.setProperty(i + "," + j, "" + id);
                            }
                        }


                    }

                }


            }

            Writer writer = new FileWriter("level.properties");
            prop.store(writer, null);
        } catch (Exception e) {

        }


    }

    public int getWidth() {
        return width;
    }


    public int getHeight() {
        return height;
    }

    public boolean begehbar(int x, int y) { //man kann abfragen ob die koodinate begehbar ist, wichtig für wegfindung der gegner, und für die spielerbewegung
        if (x < 0 || x >= width)
            return false;
        if (y < 0 || y >= height)
            return false;
        if (map[x][y] != null) {
            return !map[x][y].isCollides(); //nicht alle blöcke auf der map sind unbegehbar, deshalb abfrage nach isCollides
        } else {
            return true;
        }

    }

    public void reDraw(int x, int y) { //zeichnet den block an der stelle x,y neu, ist wichtig da man nur blöcke die sich verändern zeichnen darf.
        if (x >= 0 && x < width && y >= 0 && y < height) {
            Block block = map[x][y];
            if (block == null) {
                draw(x, y, ' ', Terminal.Color.BLACK, Terminal.Color.BLACK);
            } else {
                draw(x, y, block.getSign(), block.getForeGroundcolor(), block.getBackGroundColor());
            }
        }
    }

    public void applyBackgroundColor(int x, int y, Terminal.Color backGroundColor) { //selbe methode, diesmal mit eigener hintergrundfarbe, wird vom statischen objekt für den schadensbereich verwendet.
        if (x >= 0 && x < width && y >= 0 && y < height) {

            Block block = map[x][y];
            if (block == null) {
                draw(x, y, ' ', Terminal.Color.BLACK, backGroundColor);
            } else {
                draw(x, y, block.getSign(), block.getForeGroundcolor(), backGroundColor);
            }
        }
    }


    public void draw(int x, int y, char c, Terminal.Color foreGroundColor, Terminal.Color backGroundColor) {
        if (x >= xOffset && x < (xOffset + terminalX)) {//zeichnet die angegeben sachen auf terminal, nützlich da scrolling mit einberechnet ist. die 2 methoden über dieser rufen diese methode auf, damit ist immer garantiert dass dinge nur gezeichnet werdenm wenn sie auch sichtbar sin
            if (y >= yOffset && y < (yOffset + terminalY)) {
                terminal.moveCursor(x - xOffset, y - yOffset);
                terminal.applyForegroundColor(foreGroundColor);
                terminal.applyBackgroundColor(backGroundColor);
                terminal.putCharacter(c);
            }
        }
    }


    public void scrollX(int xScroll) { //scrollt die karte horizontal
        if (xScroll != xScrolling) {
            xScrolling = xScroll;
            drawMap();
        }

    }

    public void scrollY(int yScroll) { //scrollt die karte vertikal
        if (yScroll != yScrolling) {
            yScrolling = yScroll;
            drawMap();
        }
    }


    public void drawMap() { //zeichnet die karte

        terminal.clearScreen();
        terminal.applyBackgroundColor(Terminal.Color.BLACK);
        int x, y;

        if (width > terminalX) {
            x = terminalX;
        } else {
            x = width;
        }

        if (height > terminalY) {
            y = terminalY;
        } else {
            y = height;
        }

        xOffset = xScrolling * terminalX; //offset für das scrolling wird hier berechnet
        yOffset = yScrolling * terminalY;


        for (int j = 0; j < y; j++) {
            for (int i = 0; i < x; i++) {
                if((i+xOffset)<map.length&&(j+yOffset)<map[i+xOffset].length) { //offsets werden beim zugriff auf das 2d array mit einberechnet, beim schreiben aufs terminal aber nicht
                    if (map[i + xOffset][j + yOffset] != null) {

                        this.terminal.moveCursor(i, j);

                        this.terminal.applyForegroundColor(map[i + xOffset][j + yOffset].getForeGroundcolor());
                        this.terminal.putCharacter(map[i + xOffset][j + yOffset].getSign());

                    }
                }
            }
        }

//        }

    }

}
