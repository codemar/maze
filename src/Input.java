import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.terminal.Terminal;

/**
 * Created by Omar on 04.01.2016.
 * Inputklasse zum abfragen der zuletzt eingegebenen tatsten.
 * wird oft im programm gebraucht, wird deshalb einfach in dieser klasse mit static funktionen
 * zusammengefastt
 */
public class Input { //
    private static Terminal terminal;
    private static Key latestKey;
    private static char latestChar;

    public Input(Terminal terminal) {
        Input.terminal = terminal;
    }

    public static Key.Kind getLatestKind() {
        latestKey = terminal.readInput();

        if (latestKey != null) {

            if (latestKey.getKind() == Key.Kind.NormalKey) {
                latestChar = latestKey.getCharacter();
            }
            return latestKey.getKind();

        }
        else
        {
            return null;
        }

    }

    public static char getLatestChar()
    {
        return latestChar;
    }
}

