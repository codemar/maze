import com.googlecode.lanterna.terminal.Terminal;

/**
 * Created by Omar on 02.01.2016.
 */
class Block {
    private char sign;
    private boolean collides;
    private Block direction;
    private Terminal.Color backGroundColor;
    private Terminal.Color foreGroundcolor;
    private int x;
    private int y;
    private int cost = 0;
    private int id;


    public int getId() {
        return id;
    }

    public Block()
    {

    }
    public Block(int x,int y,Block direction)
    {
        this.direction = direction;
        this.y = y;
        this.x = x;
    }

    public Block getDirection() {
        return direction;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public Block(char sign, boolean collides, Terminal.Color foreGroundcolor,Terminal.Color backGroundColor) {
        this.sign = sign;
        this.collides = collides;
        this.foreGroundcolor = foreGroundcolor;
        this.backGroundColor = backGroundColor;
    }
    public Block(char sign, boolean collides, Terminal.Color foreGroundcolor) {
        this.sign = sign;
        this.collides = collides;
        this.foreGroundcolor = foreGroundcolor;
        this.backGroundColor = Terminal.Color.BLACK;
    }

    public Block(char sign, boolean collides) {
        this.sign = sign;
        this.collides = collides;
        backGroundColor = Terminal.Color.BLACK;
        this.foreGroundcolor = Terminal.Color.WHITE;
    }
    public Block(char sign, boolean collides, Terminal.Color foreGroundcolor, int id) {
        this.sign = sign;
        this.collides = collides;
        this.foreGroundcolor = foreGroundcolor;
        this.backGroundColor = Terminal.Color.BLACK;
        this.id = id;
    }

    public char getSign() {
        return sign;
    }

    public void setCollides(boolean collides) {
        this.collides = collides;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Terminal.Color getBackGroundColor() {
        return backGroundColor;
    }

    public Terminal.Color getForeGroundcolor() {
        return foreGroundcolor;
    }

    public boolean isCollides() {
        return collides;
    }
}
