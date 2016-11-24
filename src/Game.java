import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.terminal.Terminal;

import java.util.Date;
/*
Diese Klasse enthät alle für das Spiel relevante Objekte



 */

public class Game {
    public  Map map;
    private static boolean playing = true;
    SaveGame saveGame = new SaveGame(this);
    private Terminal terminal;
    private Player player;
    private int difficulty;
    private DynamicEnemy[] dynamicEnemyList;
    private StaticEnemy[] staticEnemyList;
    private String[] menuEntries = new String[]{"Fortsetzen", "Spiel speichern","Legende","Hilfe", "Zurück zum Hauptmenü"};
    private ColissionCalc damage;
    private long lastTime = new Date().getTime();
    private long delay = 10; //dieser delay kann aus performance gründen vom korrektor erhöht werden
    private static int points = 0;
    private int direction = 0;
    private boolean load;
    private int cause;


    public Game(Terminal terminal, int difficulty, boolean load) { //wenn das spiel geladen werden soll, dient der parameter difficulty als slotangabe, da die schwierigkeit im gespeicherten spiel bereits vorgegebn ist.
        this.load = load;
        if (load) {

            saveGame.load(difficulty); //laden des spiels aus der entsprecheneden levelX.properties datei. speichert das level in level.properties und lädt die ganzen werte (leben, gegnerzahl und position, usw.) aus der datei saveX.properties
            this.difficulty = saveGame.getDifficulty();
            this.terminal = terminal;

            init();


        } else {
            this.terminal = terminal;
            this.difficulty = difficulty;

            init();
        }
    }

    public int getCause() {
        return cause; //warum wurde das spiel beendet?
    }

    public void stop(int cause) {
        this.cause = cause; //stoppt das spiel, speichert den grund des beendens
        playing = false;
    }

    public void addPoints(int amount) {
        points += amount; //die punkte des spieler werden hiermit erhöht. points ist static, da die punkte ja erst am ende von mehreren spielen angezeigt werden, die variable muss also über mehrere objekte gleich sein
    }

    public DynamicEnemy[] getDynamicEnemyList() {
        return dynamicEnemyList;
    }

    public StaticEnemy[] getStaticEnemyList() {
        return staticEnemyList;
    }

    public void init() {

        int width, height, nrIn, nrOut, keys, sTraps, dTraps, leben;
        switch (difficulty) { //festlegen der parameter für den generator, abhängig vom schwierigkeitsgrad
            case 1:
            default:
                width = 100;
                height = 30;
                nrIn = 1;
                nrOut = 3;
                keys = 2;
                sTraps = 1;
                dTraps = 2;
                leben = 3;
                break;
            case 2:
                width = 100;
                height = 60;
                nrIn = 1;
                nrOut = 2;
                keys = 2;
                sTraps = 3;
                dTraps = 4;
                leben = 3;
                break;
            case 3:
                width = 200;
                height = 60;
                nrIn = 1;
                nrOut = 1;
                keys = 1;
                sTraps = 5;
                dTraps = 6;
                leben = 3;
                break;

        }





        /*
        Der folgende Teil ist etwas unschön programmiert, das Programm erstellt die Gegnerobjekte und die Map anders, wenn man von einer Datei lädt, als wenn nicht
        Das sieht man an den if(!load) oder if(load). Ich hätte diese bestimmt zusammenfassen können, aber eine bestimmte reihenfolge der definitionen muss vorhanden sein,
        Ich hab leider vergessen was ich beachten muss, deshalb hab ich mich dafür entschieden den code so unschön zu lassen.
        Er funktioniert aber dennoch einwandfrei
        */



        if(!load) { //wenn nicht geladen wird entspricht die gegneranzahl den nach schwierigkeitsgrad festgelegten werten
            this.dynamicEnemyList = new DynamicEnemy[dTraps];
            this.staticEnemyList = new StaticEnemy[sTraps];
        }
        else
        { //wenn geladen wird entspricht die anzahl der anzahl der noch lebenden gegner aus dem spiel, das geladen wird.
            this.dynamicEnemyList = new DynamicEnemy[saveGame.getdEnemyCount()];
            this.staticEnemyList = new StaticEnemy[saveGame.getsEnemyCount()];
        }

        if (!load) { //wenn nicht geladen wird, wird ein neues level in "level.properties" generiert.
            Generate.generate(width, height, nrIn, nrOut, keys, sTraps, dTraps);
        }
        //wenn geladen wird, ist level.properties schon vom SaveGame objekt erstellt/überschrieben worden
        map = new Map(width, height, nrIn, sTraps, dTraps, this.terminal);

        int[] spawnPoint;
        int[][] dSpawnPointList;
        int[][] sSpawnPointList;

        if (load) {
            spawnPoint = saveGame.getSpawnPoint(); //wenn geladen wird ist der spawnpont die letzte position, an der der spieler war. diese ist in der saveX.properties gespeichertund kann über diese funktion abgerufen wreden
            dSpawnPointList = saveGame.getdSpawnPointList(); //analog dazu die Gegnerspawns
            sSpawnPointList = saveGame.getsSpawnPointList();

        } else { //wenn nicht geladen wird, wählt die klasse Map einen zufälligen eingang aus, den man mit getSpawn abfragen kann
            spawnPoint = map.getSpawn();
            dSpawnPointList = map.getdSpawnPointList(); //dynamische und statische spawnpoints können wir von Map mit diesen methoden abfragen
            sSpawnPointList = map.getsSpawnPointList();
        }

        if(load)
        {
            leben = saveGame.getLeben(); //ansonsten der Wert der oben festgelegt wird
            points = saveGame.getPoints(); //anonsten 0
        }

        for (int i = 0; i < (sSpawnPointList.length); i++) {
            staticEnemyList[i] = new StaticEnemy(sSpawnPointList[i][0], sSpawnPointList[i][1], map, this);
        } //erstellt nurn alle statischen gegner

        player = new Player(terminal, leben, spawnPoint[0], spawnPoint[1], map, this); //erstellt den player

        if(load)
        {
            player.setKeyCount(saveGame.getKeyCount()); //der spieler kriegt seine schlüssel, die er schon gesammelt hat, wieder

        }

        for (int i = 0; i < (dSpawnPointList.length); i++) {
            dynamicEnemyList[i] = new DynamicEnemy(dSpawnPointList[i][0], dSpawnPointList[i][1], map, this);
        } //dynamische gegner werden initialisiert


        damage = new ColissionCalc(player, dynamicEnemyList, staticEnemyList); //diese klasse berechnet kollisionen zwischen spieler und den gegnern, und schadet ihn eventuell


        map.drawMap(); //map wird gezeichnet.
        playCycle(); //geht in den spielzykklus
    }

    public Player getPlayer() {
        return player;
    }

    public void drawStaticEnemies() { //eigene funktion zum erneuten zeichnen der gegner, da sonst der player oder die dynamishen gegner das feld des statischen gegner "wegradieren würden"
        for (StaticEnemy staticEnemy : staticEnemyList) {
            if (staticEnemy.isAlive()) {
                staticEnemy.draw();
            }
        }
    }


    public void playCycle() {
        playing = true;
        while (playing) {
            long tempTime = new Date().getTime(); //aktuelle zeit wird abgerufen
            if (tempTime - lastTime > delay) { //im intervall delay wird die if durchgegangen


                getInput(); //eingabe wird abgerufen

                player.refresh(direction); //ab hier werden alle gegner und der player "refresht". sie werden also je nach bewegung neu gezeichnet


                for (int i = 0; i < dynamicEnemyList.length; i++) {
                    if(dynamicEnemyList[i]!=null) {
                        if (dynamicEnemyList[i].isAlive()) {
                            dynamicEnemyList[i].refresh();
                        }
                    }


                }
                for (int i = 0; i < staticEnemyList.length; i++) {
                    if(staticEnemyList[i]!=null) {
                        if (staticEnemyList[i].isAlive()) {
                            staticEnemyList[i].refresh();
                        }
                    }
                }

                damage.refresh(); //schadensabfrage hat auch eine refresh methode

            }

        }


    }

    private void getInput() {
        Key.Kind kind = Input.getLatestKind();

        if (kind != null) {
            switch (kind) {
                case Escape:
                    Menu menu = new Menu(terminal, menuEntries, 5, 5);
                    switch (menu.enter()) {
                        case 0:
                            break;
                        case 1:

                            Menu saveMenu = new Menu(terminal, new String[]{"Slot 1", "Slot 2", "Slot 3", "Zurück"}, 10, 10); //speichermenü
                            int enter = saveMenu.enter(); //selbe funktionsweise wie loadmenü/ alle anderen menüs
                            switch (enter) {
                                case 0:
                                case 1:
                                case 2:
                                    map.saveMap();// speichert die map ab(die schlüssel können sich auf der karte verändern, deshalb muss diese mitgespeichert werden, man kann nicht die neu generierte verwenden)
                                    saveGame.save(enter + 1);//attribute des spielfelds werden gespeichert, in den slot den der spieler ausgewählt hat
                                    break;

                                case 3:
                                    break;


                            }
                            break;
                        case 2:
                            myScreen legendScreen = new myScreen(terminal,5,2,"legende");//legende wird gezeichnet
                            break;

                        case 3:
                            myScreen helpScreen = new myScreen(terminal,0,1,"hilfe");//hilfefenster wird gezeichnet
                            break;
                        case 4:

                            stop(0);
                            break;
                    }
                    map.drawMap();
                    direction = 0;
                    break;
                case ArrowUp: //direction ist die richtung die der spieler läuft, wird beim refresh des spielers übergeben
                    direction = 1;
                    break;
                case ArrowRight:
                    direction = 2;
                    break;
                case ArrowDown:
                    direction = 3;
                    break;
                case ArrowLeft:
                    direction = 4;
                    break;
                default:
                    direction = 0;
                    break;


            }
        } else {
            direction = 0;
        }


    }

    public int getDifficulty() {
        return difficulty;
    }

    public static int getPoints() {
        return points;
    }
}

