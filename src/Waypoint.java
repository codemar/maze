import java.util.ArrayList;

/**
 * Created by Omar on 12.01.2016.
 * Hier ist der A* algorithmus implementiert
 */
public class Waypoint {
    private ArrayList<Block> openList, closedlist;
    private ArrayList<Integer> directionList;
    private Block zielBlock;
    private int currentBlock;
    private boolean find;
    private int xStart, yStart;
    private Map map;

    public Waypoint(int xStart, int yStart, int xZiel, int yZiel, Map map) {
        this.xStart = xStart;
        this.yStart = yStart;
        this.map = map;
        zielBlock = new Block(xZiel, yZiel, null);
        currentBlock = 0;


    }

    public void pathFind() {//der algorithmus fügt alle blöcke, die den startblock umgeben und begehbar sind,  in die openlist hinzu, und packt den startblock selbst in die closedlist. dann berechnte es fürr die die anderen blöcke den schätzwert, und beim block mit dem niedrigsten schätzwert werden die gerade beschriebenen operationen eifnach wiederholt.
        openList = new ArrayList<>();
        closedlist = new ArrayList<>();
        directionList = new ArrayList<>();
        openList.add(new Block(xStart, yStart, null)); //startblock am anfang  inder openlist
        calcGuess(openList.get(0)); //schätzwert der startblock, redundant
        currentBlock = 0;
        find = true;


        while (find) {
            if (openList.size() != 0) {
                if (!(openList.get(currentBlock).getX() == zielBlock.getX()) ||
                        !(openList.get(currentBlock).getY() == zielBlock.getY())) {


                    checkBlock(1, 0);
                    checkBlock(-1, 0);
                    checkBlock(0, 1);
                    checkBlock(0, -1);

                    closedlist.add(openList.get(currentBlock));
                    openList.remove(currentBlock);

                    int leastCost = 0;

                    for (int i = 0; i < openList.size(); i++) {
                        if (leastCost == 0) {
                            leastCost = openList.get(i).getCost();
                            currentBlock = i;
                        } else {
                            if (leastCost > openList.get(i).getCost()) {
                                leastCost = openList.get(i).getCost();
                                currentBlock = i;
                            }
                        }

                    }
                } else {
                    closedlist.add(openList.get(currentBlock));
                    currentBlock = closedlist.size()-1;
                    createDirections();
                }

            } else {
                int lastCost = 0;
                for (int i = 0; i < closedlist.size(); i++) {
                    if (closedlist.get(i).getCost() < lastCost || lastCost == 0) {
                        lastCost = closedlist.get(i).getCost();

                        currentBlock = i;
                    }
                }
                createDirections();
            }
        }
    }


    public void createDirections() { //hier wird die liste mit den richtungen für den wurm erstellt, die er dann durchgeht, und somit den spieler findet
        Block block = closedlist.get(currentBlock);
        int xDir, yDir;

        while (block.getDirection() != null) {
            xDir = block.getX() - block.getDirection().getX();
            yDir = block.getY() - block.getDirection().getY();
            int dir;

            if (xDir != 0) {
                if (xDir == 1) {
                    dir = 2;
                } else {
                    dir = 4; //umrechnung von xdir und ydir in dir, 1=oben 2= rechts,3=unten,4=links
                }
            } else {
                if (yDir == 1) {
                    dir = 3;
                } else {
                    dir = 1;
                }
            }

            directionList.add(0, dir);
            block = block.getDirection();
        }
        find = false;

    }

    public void changeDirection(int x, int y) {
        zielBlock = new Block(x, y, null); //man kann das ziel verändern, falls es sich wo anderers hin bewegt hat


    }

    public void changeStart(int x, int y) {
        this.xStart = x;
        this.yStart = y; //man kann auch den start bewegen, falls sich der wurm selber bewegt hat

    }

    public ArrayList<Integer> getDirectionList() {
        return directionList;
    }

    private void checkBlock(int xOffset, int yOffset) {//überprüft den block auf begehbarkeit,


        int x = openList.get(currentBlock).getX() + xOffset;
        int y = openList.get(currentBlock).getY() + yOffset;


        if (!contains(openList, x, y)) { //überprüft auch ob der block schon mal berechnet wurde
            if (!contains(closedlist, x, y)) {
                if (map.begehbar(x, y)) {
                    Block block = new Block(x, y, openList.get(currentBlock));
                    calcGuess(block); //fügt den block der openlist zu, berehnet auch einen schätzwert der "kosten" die der wurm von diesem block bis zum ziehl hat
                    openList.add(block);

                }
            }

        }

    }


    public boolean contains(ArrayList<Block> list, int x, int y) { //methode um zu überprüfen, ob die angegebenen koordinaten schon in einer der listen ist
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getX() == x && list.get(i).getY() == y) {
                return true;
            }


        }
        return false;
    }


    public void calcGuess(Block b) {
        int xDif = Math.abs(b.getX() - zielBlock.getX());
        int yDif = Math.abs(b.getY() - zielBlock.getY()); //gibt einen schätzwert des abstands von diesem block bis zum ziel, hindernisse werden dabei nicht beachtet.

        b.setCost(xDif + yDif);
    }


}
