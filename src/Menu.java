import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.terminal.Terminal;


/**
 * Created by Omar on 03.01.2016.
 */
public class Menu { //klasse zur erstellung der menüs
    public boolean cycle = true;
    Key.Kind kind;
    int xPos = 5;
    int yPos = 2;
    private String[] menuEntries;
    private int marker = 0;
    private Terminal terminal;
    private Input input;

    public Menu(Terminal terminal) {
        this.terminal = terminal;
        this.menuEntries = new String[]{"Neues Spiel", "Laden", "Beenden"}; //hauptmenü einträge
        this.input = new Input(this.terminal);
        init();
    }

    public Menu(Terminal terminal, String[] menuEntries, int xPos, int yPos) {//zweiter konstruktor erlaubt mehrere menüs mit anderer platzierung
        this.terminal = terminal;
        this.xPos = xPos;
        this.yPos = yPos;
        this.menuEntries = menuEntries;
        this.input = new Input(this.terminal);
        init();

    }

    private void init() {
        drawMenu(); //erst menü zeichnen
        menuCycle(); //dann eingabe abfragen und die markierung entsprechend verschieben
    }


    private void drawMenu() { //zeichnte das menü
        terminal.clearScreen();
        terminal.applyForegroundColor(Terminal.Color.WHITE);
        terminal.applyBackgroundColor(Terminal.Color.BLACK);

        for (int i = 0; i < menuEntries.length; i++) { //iteriert über einträge
            terminal.moveCursor(xPos, yPos + i * 2); //einträge haben immer eine zeile dazwischen

            for (int j = 0; j < menuEntries[i].length(); j++) { //zeichnet den eintrag aufs terminal
                terminal.putCharacter(menuEntries[i].charAt(j));
            }

        }

        drawMarker(0, true); //zeichnet den marker bei 0

    }

    void menuCycle() { //fragt input ab und ruft entsprechend methoden auf

        while (cycle) {
            kind = Input.getLatestKind();


            if (kind != null) {

                switch (kind) {
                    case ArrowUp:
                        move(true);
                        break;
                    case ArrowDown:
                        move(false);
                        break;
                    case NormalKey: {
                        if (Input.getLatestChar() == ' ') {
                            cycle = false; //damit man mit leertaste bestätigen kann
                        }
                        break;
                    }
                    case Enter:
                        cycle = false;
                        break;

                }

            }
        }
    }


    public int enter() { //entfernt den markierer, der wert marker beinhaltet nun die position die vom spieler  gewählt wurde und wird zurückgegeben. man kann also anhan dieser methode festlegen was nach der auswahl eines gewissen menüpunktes passieren soll.



        drawMarker(marker, false);

        return marker;


    }

    private void move(boolean direction) { //bewegt den cursor, entfernt die markierung und zeichnet sie neu
        if (direction) {
            if (marker != 0) {
                drawMarker(marker, false);
                marker--;
                drawMarker(marker, true);
            }
        } else {
            if (marker < (menuEntries.length - 1)) {
                drawMarker(marker, false);
                marker++;
                drawMarker(marker, true);
            }
        }


    }


    public void drawMarker(int pos, boolean add) { //wenn add == true wird der marker an menüeintrag pos gezeichnet, wenn add == false wird er von der pos entfernt
        int x = xPos;
        int y = yPos + marker * 2;
        String s = menuEntries[pos];
        int length = s.length();
        Terminal.Color color;

        if (add) {
            color = Terminal.Color.RED; //menüeintrag wird also einfach neu an die selbe position gezeichnet, aber entweder mit rotem hintergrund
        } else {
            color = Terminal.Color.BLACK; //oder der hintergrund wird entfernt
        }

        terminal.moveCursor(x, y);

        for (int i = 0; i < length; i++) {

            terminal.applyBackgroundColor(color);
            terminal.putCharacter(s.charAt(i));


        }


    }


}
