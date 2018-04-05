package pacman.entries.pacman;

import pacman.controllers.Controller;
import pacman.game.Constants;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.internal.Node;

import java.util.*;

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getAction() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., game.entries.pacman.mypackage).
 */
public class MyPacMan2 extends Controller<MOVE> {
    private Random rnd = new Random();
    private Constants.GHOST[] ghosts = Constants.GHOST.values();
    private MOVE[] allMoves = MOVE.values();
    private Set<Node> visited = new LinkedHashSet<>();

    /* (non-Javadoc)
     * @see pacman.controllers.Controller#getMove(pacman.game.Game, long)
     */
    public MOVE getMove(Game game, long timeDue) {
        List<Node> neighbours = new LinkedList<>();
        int[] neighbourIndex = game.getNeighbouringNodes(game.getPacmanCurrentNodeIndex());
        return null;
    }

//    private int simulateMove(int depth){
//
//    }
//
//    private int evaluateState(){
//
//    }

}
