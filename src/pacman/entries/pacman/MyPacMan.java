package pacman.entries.pacman;

import pacman.controllers.Controller;
import pacman.game.Constants;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getAction() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., game.entries.pacman.mypackage).
 */
public class MyPacMan extends Controller<MOVE> {
    private Random rnd = new Random();
    private List<Constants.GHOST> ghosts = ghosts();

    /* (non-Javadoc)
     * @see pacman.controllers.Controller#getMove(pacman.game.Game, long)
     */
    public MOVE getMove(Game game, long timeDue) {

        int pacmanNode = game.getPacmanCurrentNodeIndex();
        int[] neighbourNodes = game.getNeighbouringNodes(pacmanNode);
        int nextNode = 0;
        game.getActivePillsIndices();
        for (int i = 0; i < game.getActivePillsIndices().length; i++) {
            nextNode = game.getActivePillsIndices()[i];
        }
        for(Constants.GHOST ghost : ghosts){
            if(game.getGhostEdibleTime(ghost)>10){
                return game.getApproximateNextMoveTowardsTarget(pacmanNode, game.getGhostCurrentNodeIndex(ghost), game.getPacmanLastMoveMade(), Constants.DM.PATH);
            }
        }
        return game.getApproximateNextMoveTowardsTarget(pacmanNode, nextNode, game.getPacmanLastMoveMade(), Constants.DM.PATH);
    }

    private List<Constants.GHOST> ghosts(){
        List<Constants.GHOST> ghosts = new LinkedList<>();
        ghosts.add(Constants.GHOST.BLINKY);
        ghosts.add(Constants.GHOST.PINKY);
        ghosts.add(Constants.GHOST.INKY);
        ghosts.add(Constants.GHOST.SUE);
        return ghosts;
    }
}