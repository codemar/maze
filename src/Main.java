import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.SwingTerminal;

import java.awt.*;
import java.io.File;

/**
 * Created by Omar on 01.01.2016.
 */
public class Main {
    private static SwingTerminal terminal = TerminalFacade.createSwingTerminal();
    private static boolean running = true;
    private static boolean playing = true;

    public static void main(String[] args) {


        terminal.enterPrivateMode();
        terminal.setCursorVisible(false);



        myScreen startScreen = new myScreen(terminal, 13, 5, new String("mAZe")); //ascii-Bild am Anfang des Spiels
        while (running) {
            mainCycle();
        }


        terminal.exitPrivateMode();


    }

    private static void mainCycle() {

        Menu menu = new Menu(terminal); //Hauptmenü. Menüs haben Ihre eigene interne Schleife in der sie den INput abfragen. Nach dieser Schleife speichern Sie
        switch (menu.enter()) {         //die gewhälte Zeile, die man dann per enter() abfragen kann.
            case 0:
                Menu playMenu = new Menu(terminal, new String[]{"Leicht", "Mittel", "Schwer", "Zurück"}, terminal.getTerminalSize().getColumns()-11, 2); //Menü mit übergebenem String Array -> hta andere Menü Einträge,
                                                                                                                                                        // die Konsequen der Auswahl ist erneut in der folgenden Switch festgelegt
                int enter = playMenu.enter();
                switch (enter) {
                    case 0:
                    case 1:
                    case 2:
                        play(false,enter+1); //der 2. parameter ist in diesem fall die schwierigkeit, und nicht der speicherslot
                        playing = true;
                        break;
                    case 3:
                        break;

                }
                break;
            case 1:
                Menu loadMenu = new Menu(terminal, new String[]{"Slot 1", "Slot 2", "Slot 3", "Zurück"}, 10, 10); //siehe playMenu
                int loadEnter = loadMenu.enter();
                switch (loadEnter) {
                    case 0:
                    case 1:
                    case 2:
                        File f = new File("level"+(loadEnter+1)+".properties");
                        if(f.exists() && !f.isDirectory()) { //abfrage ob die Datei existiert / ob der Speicherstand existiert,
                            play(true,loadEnter); // falls ja, startet die SPieleschleife, mit parameter load auf true, was heißt der Speicherstand geladen wird der in Loadenter angegeben ist.
                        }
                        else
                        {
                            terminal.applyForegroundColor(Terminal.Color.RED);
                            terminal.moveCursor(50,10);
                            String s = "Spiel existiert nicht!";
                            for(int i = 0; i<s.length();i++)
                            {
                                terminal.putCharacter(s.charAt(i)); //ausgabe der Warnmeldung, falls die Datei nicht existiert. Man muss also vorher auf den Slot gespeichert haben. um Ihn zu laden

                            }
                            try {
                                Thread.sleep(800); //kurze wartezeit, da das programm sonst direkt ins hauptmenü zurückkehrt.
                            }
                            catch(Exception e)
                            {

                            }
                        }
                        break;
                    case 3:

                        break;
                }
                break;
            case 2:
                running = false;
                break;
        }
    }


    private static void play(boolean load, int slot) //Spieleschleife, damit man direkt ins nächste level kommt.
    {

        int count = 0;

        while (playing) {
            Game game;
            if(load&&count == 0) //wenn load auf true ist und count ==0 wird geladen, man will nur bei der ersten runde des spiels laden, deshalb der count. danach geht es ja normal weiter, mit random levels
            {
                game = new Game(terminal, slot, true);
            }
            else {
                game = new Game(terminal, slot, false); //normalerweise wird einfach ein neues spiel gestartet, also load auf false. slot ist dann die schwierigkeit, und nicht der gewählte speicherslot
            }
            count++;
            switch (game.getCause()) { // nach jedem spiel kann man mit getCause den grund des verlassens, der spielinternen schleife abfragen, 0= spieler hat beendet, 1 = spieler hat gewonnen, 2= spieler hat verloren
                case 0:
                    playing = false; //verlassen der spiel-schleife, kehrt ins hauptmenü zurück
                    break;
                case 1:
                    playing = true; //in der schleife bleiben, nächstes level. diese zeile ist eigentlich redundant
                    break;
                case 2:
                    playing = false; //verlassen der schleife
                    int i = Game.getPoints();
                    String s = Integer.toString(i); //speichert die punkte und gibt sie auf dem game over screen aus
                    myScreen gameover= new myScreen(terminal,10,10,"gameover",new String[]{s}); // game over screen
                    break;


            }


        }

    }


}
