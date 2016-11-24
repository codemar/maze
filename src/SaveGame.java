import java.io.*;
import java.util.Properties;

/**
 * Created by Omar on 18.01.2016.
 * diese klasse kann schlicht und einfach die karte und attribute das spiels in dateien abspeichern,
 * und diese wieder laden
 */
public class SaveGame {
    private Game game;
    private int[] spawnPoint;
    private int leben;
    int points;
    private int keyCount;
    private int difficulty;
    private int[][] sSpawnPointList;
    private int[][] dSpawnPointList;
    private String[] letterList;
    private String[] actualLetterList;
    private int dEnemyCount;
    private int sEnemyCount;

    public SaveGame(Game game) {

        this.game = game;
    }

    public void save(int slot) {
        try {//speichert das level in eine spereate levelX.properties datei und alle attribute des spiels in eine saveX.properties datei
            Properties properties = new Properties();
            Reader reader = new FileReader("level.properties");
            Writer writer = new FileWriter("level" + slot + ".properties");
            properties.load(reader);
            properties.store(writer, null);

            properties.clear();
            properties = new Properties();
            writer = new FileWriter("save" + slot + ".properties");

            properties.setProperty("X", "" + game.getPlayer().getxPos());
            properties.setProperty("Y", "" + game.getPlayer().getyPos());
            properties.setProperty("L", "" + game.getPlayer().getLife());
            properties.setProperty("K", "" + game.getPlayer().getKeyCount());

            int sEnemyCount = 0;

            for (int i = 0; i < game.getStaticEnemyList().length; i++) {
                StaticEnemy enemy = game.getStaticEnemyList()[i];
                if (enemy.isAlive()) {
                    sEnemyCount++;
                    properties.setProperty("SX" + i, "" + enemy.getxPos());
                    properties.setProperty("SY" + i, "" + enemy.getyPos());
                }

            }

            properties.setProperty("SEnemyCount", "" + sEnemyCount);


            int dEnemyCount = 0;
            for (int i = 0; i < game.getDynamicEnemyList().length; i++) {
                DynamicEnemy enemy = game.getDynamicEnemyList()[i];
                if (enemy.isAlive()) {
                    dEnemyCount++;
                    properties.setProperty("DX" + i, "" + enemy.getxPos());
                    properties.setProperty("DY" + i, "" + enemy.getyPos());
                    properties.setProperty("DA" + i, "" + enemy.getLetterList());
                    properties.setProperty("DB" + i, "" + enemy.getActualLetterList());//keys wurden selber erfunden,
                }
            }

            properties.setProperty("DEnemyCount", "" + dEnemyCount);
            properties.setProperty("Difficulty", "" + game.getDifficulty());
            properties.setProperty("Points",""+ Game.getPoints());


            properties.store(writer, null);


        } catch (IOException e) {

            e.printStackTrace();
        }

    }

    public int[] getSpawnPoint() {
        return spawnPoint;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public int getLeben() {
        return leben;
    }

    public int getKeyCount() {
        return keyCount;
    }

    public int[][] getsSpawnPointList() {
        return sSpawnPointList;
    }

    public int[][] getdSpawnPointList() {
        return dSpawnPointList;
    }


    public void load(int slot) {



        try {//kopiert die lexelX.properties zur level.properties datei, damit die map davon initialisiert wird
            Writer writer = new FileWriter("level.properties"); //die attribute werden aus saveX.properties geladen und in die variablen gespeichert, damit die game klasse dieses mit den getter funktionen abfragen, und das spiel initizalisiseren kann
            Reader reader = new FileReader("level" + (slot+1) + ".properties");

            Properties prop = new Properties();

            prop.load(reader);
            prop.store(writer, null);

            prop.clear();
            prop = new Properties();

            reader = new FileReader("save" + (slot+1) + ".properties");

            prop.load(reader);

            spawnPoint = new int[2];
            dEnemyCount = Integer.parseInt(prop.getProperty("DEnemyCount"));
            sEnemyCount = Integer.parseInt(prop.getProperty("SEnemyCount"));
            dSpawnPointList = new int[dEnemyCount][2];
            letterList = new String[dEnemyCount];
            actualLetterList = new String[dEnemyCount];
            sSpawnPointList = new int[sEnemyCount][2];

            spawnPoint[0] = Integer.parseInt(prop.getProperty("X"));
            spawnPoint[1] = Integer.parseInt(prop.getProperty("Y"));
            leben = Integer.parseInt(prop.getProperty("L"));
            keyCount = Integer.parseInt(prop.getProperty("K"));




            for (int i = 0; i < sEnemyCount; i++) {
                sSpawnPointList[i][0] = Integer.parseInt(prop.getProperty("SX" + i));
                sSpawnPointList[i][1] = Integer.parseInt(prop.getProperty("SY" + i));
            }



            for (int i = 0; i < dEnemyCount; i++) {
                dSpawnPointList[i][0] = Integer.parseInt(prop.getProperty("DX"+i));
                dSpawnPointList[i][1] = Integer.parseInt(prop.getProperty("DY"+i));
                letterList[i] = (prop.getProperty("DA"+i));
                actualLetterList[i] = (prop.getProperty("DB"+i));

            }


            difficulty = Integer.parseInt(prop.getProperty("Difficulty"));
            points = Integer.parseInt(prop.getProperty("Points"));




        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    public int getPoints() {
        return points;
    }

    public int getdEnemyCount() {
        return dEnemyCount;
    }

    public int getsEnemyCount() {
        return sEnemyCount;
    }
}
