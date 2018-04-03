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
    private Constants.GHOST[] ghosts = Constants.GHOST.values();
    private MOVE[] allMoves=MOVE.values();

    /* (non-Javadoc)
     * @see pacman.controllers.Controller#getMove(pacman.game.Game, long)
     */
    public MOVE getMove(Game game, long timeDue) {

        int pacmanNode = game.getPacmanCurrentNodeIndex();
        int closestPill = 0;
        int distanceToClosestPill = Integer.MAX_VALUE;
        Constants.GHOST closestGhost = Constants.GHOST.BLINKY;
        int closestGhostNode = 0;
        int closestGhostDistance = Integer.MAX_VALUE;

        for (int i = 0; i < game.getActivePillsIndices().length; i++) {
            int pill = game.getActivePillsIndices()[i];
            int distanceToPill = game.getShortestPathDistance(pacmanNode, pill);
            if(distanceToPill < distanceToClosestPill){
                distanceToClosestPill = distanceToPill;
                closestPill = pill;
            }
        }
        for (Constants.GHOST ghost : ghosts) {
            int ghostNode = game.getGhostCurrentNodeIndex(ghost);
            int distanceToGhost = game.getShortestPathDistance(pacmanNode, ghostNode);

            if (distanceToGhost < closestGhostDistance && distanceToGhost != -1) {
                // System.out.println(game.getShortestPathDistance(pacmanNode, ghostNode));
                closestGhostDistance = distanceToGhost;
                closestGhost = ghost;
                closestGhostNode = ghostNode;
            }
            //System.out.println("closestGhostDistance " + closestGhostDistance);
        }

        if (closestGhostDistance < 10 && game.getGhostEdibleTime(closestGhost) < 10) {
            for(MOVE move :allMoves) {
                Game copy = game.copy();
                copy.updatePacMan(move);
                if(copy.getShortestPathDistance(copy.getPacmanCurrentNodeIndex(),closestGhostNode) > closestGhostDistance){
                    System.out.println("AWAAY  " + move + " " + closestGhost);
                    return move;
                }
            }

        }
        if (game.getGhostEdibleTime(closestGhost) > 10) {
            for(MOVE move :allMoves) {
                Game copy = game.copy();
                copy.updatePacMan(move);
                if(copy.getShortestPathDistance(copy.getPacmanCurrentNodeIndex(),closestGhostNode) < closestGhostDistance){
                    System.out.println("Towards  " + move + " " + closestGhost);
                    return move;
                }
            }
        }
        //System.out.println("NOMNOM");
        return game.getNextMoveTowardsTarget(pacmanNode, closestPill, game.getPacmanLastMoveMade(), Constants.DM.PATH);
    }

    private List<Constants.GHOST> ghosts() {
        List<Constants.GHOST> ghosts = new LinkedList<>();
        ghosts.add(Constants.GHOST.BLINKY);
        ghosts.add(Constants.GHOST.PINKY);
        ghosts.add(Constants.GHOST.INKY);
        ghosts.add(Constants.GHOST.SUE);
        return ghosts;
    }
}