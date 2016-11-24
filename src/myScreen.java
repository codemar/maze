import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.*;

/**
 * Created by Omar on 20.01.2016.
 * Klasse um ein Bild aus einer txt file zu zeichnen
 * Beispiel = Erstes Bild das man sieht, wenn man das programm startet, game over bildschirm
 * Um diese Klasse besser zu verstehen kann man sich die .txt dateien angucken und sehen wie die klasse sie ins spiel übersetzt
 * ist aber eigentlich unwichtig
 */


public class myScreen {
    private Terminal terminal;
    private int x;
    private int y;
    private String fileName;
    private String[] params;


    public myScreen(Terminal terminal, int x, int y, String fileName) {
        this.terminal = terminal;
        this.x = x;
        this.y = y;
        this.fileName = fileName;

        draw();
    }

    public myScreen(Terminal terminal, int x, int y, String fileName, String[] params) { // alternativer konstruktor, da man eventuell nicht nur eine txt ausgeben möchte, sondern auch werte aus dem programm. (z.b. die punktzahl nach dem verlieren)
        this.terminal = terminal;
        this.x = x;
        this.y = y;
        this.fileName = fileName;
        this.params = params;


        draw();
    }



    private void draw() {

        Input input = new Input(terminal);

        terminal.clearScreen();
        terminal.applyForegroundColor(Terminal.Color.WHITE);
        terminal.applyBackgroundColor(Terminal.Color.BLACK);


        File file = new File("res/" + fileName);

        try {
            InputStream istream = new FileInputStream(file);


            terminal.clearScreen();
            terminal.applyForegroundColor(Terminal.Color.WHITE);
            Reader reader = new InputStreamReader(istream);
            terminal.moveCursor(x, y);
            int d;
            char c;
            while ((d = reader.read()) != -1) { //liest txt datei aus und schreibt sie auf das terminal
                c = (char) d;
                switch (c) { //c wird überprüft auf gewisse symbole, die ich festelegt habe

                    case '%': //% = zeilenumbruch
                        terminal.moveCursor(x, ++y);
                        break;
                    case '$': //mit $ in der txt kann man einen parameter ausgeben lassen, den man beim erstellen des myScreen objekts übergeben hat
                        c = (char) reader.read();
                        d=Character.getNumericValue(c);
                        String s = params[d];
                        for (int i = 0; i < s.length(); i++) {
                            terminal.putCharacter(s.charAt(i));
                        }
                        break;
                    case '&': //& gefolgt mit einem der buchstaben ändert die farbe ab der entsprechenden stelle in der txt datei
                        Terminal.Color color;
                        c = (char) reader.read();
                        switch (c) {

                            case 'R':
                                color = Terminal.Color.RED;
                                break;
                            case 'Y':
                                color = Terminal.Color.YELLOW;
                                break;
                            case 'G':
                                color = Terminal.Color.GREEN;
                                break;
                            case 'B':
                                color = Terminal.Color.BLUE;
                                break;
                            case 'M':
                                color = Terminal.Color.MAGENTA;
                                break;
                            default:
                                color = Terminal.Color.WHITE;
                                break;
                        }
                        terminal.applyForegroundColor(color);
                        break;
                    default:
                        terminal.putCharacter(c);
                        break;
                }
            }
        } catch (IOException e) {

        }


        boolean loop = true;
        while (loop) {
            if (Input.getLatestKind() == Key.Kind.Enter) { //mit enter geht das programm weiter
                loop = false;
            } else {
                try {
                    Thread.sleep(100); //performance sparen
                } catch (Exception e) {

                }
            }
        }


    }
}



