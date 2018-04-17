package pacman.entries.pacman;

import pacman.controllers.Controller;
import pacman.controllers.examples.Legacy;
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
//    private MOVE[] movesAvailable = MOVE.values();

    /* (non-Javadoc)
     * @see pacman.controllers.Controller#getMove(pacman.game.Game, long)
     */
    public MOVE getMove(Game game, long timeDue) {
        int pacmanNodeIndex = game.getPacmanCurrentNodeIndex();
        MOVE bestMove = null;
        int bestScore = Integer.MIN_VALUE;

        if (game.getPossibleMoves(pacmanNodeIndex).length > 2 || game.wasPowerPillEaten() || ghostWasEaten(game)) {
            for (MOVE move : game.getPossibleMoves(pacmanNodeIndex)) {
                Game copy = game.copy();
                copy.updatePacMan(move);
                int score = rollout(copy);
                if (move == game.getPacmanLastMoveMade()) {
                    if (bestScore <= score) {
                        bestMove = move;
                        bestScore = score;
                    }
                } else {
                    if (bestScore < score) {
                        bestMove = move;
                        bestScore = score;
                    }
                }
            }

            return bestMove;
        }
        return moveThatIsNotBack(game.getPacmanLastMoveMade(), game.getPossibleMoves(pacmanNodeIndex));
    }

    private boolean ghostWasEaten(Game game){
        for(Constants.GHOST ghost : ghosts){
            if(game.wasGhostEaten(ghost)) return true;
        }
        return false;
    }

    private MOVE moveThatIsNotBack(MOVE pacmanPreviousMove, MOVE[] movesAvailable) {

        for (MOVE moveCandidate : movesAvailable) {
            if (pacmanPreviousMove == MOVE.RIGHT) {
                if (moveCandidate == MOVE.RIGHT || moveCandidate == MOVE.UP || moveCandidate == MOVE.DOWN)
                    return moveCandidate;
            }
            if (pacmanPreviousMove == MOVE.LEFT) {
                if (moveCandidate == MOVE.LEFT || moveCandidate == MOVE.UP || moveCandidate == MOVE.DOWN)
                    return moveCandidate;
            }
            if (pacmanPreviousMove == MOVE.UP) {
                if (moveCandidate == MOVE.RIGHT || moveCandidate == MOVE.UP || moveCandidate == MOVE.LEFT)
                    return moveCandidate;
            }
            if (pacmanPreviousMove == MOVE.DOWN) {
                if (moveCandidate == MOVE.RIGHT || moveCandidate == MOVE.LEFT || moveCandidate == MOVE.DOWN)
                    return moveCandidate;
            }
        }
        return MOVE.NEUTRAL;
    }


    private int rollout(Game game) {
        Legacy ghosts = new Legacy();
        for (int i = 0; i < 100; i++) { // simulate 100 ticks
            if (game.getPossibleMoves(game.getPacmanCurrentNodeIndex()).length > 2 || game.wasPowerPillEaten() || ghostWasEaten(game)) {
                game.updatePacMan(getBestMove(game));
            } else {
                game.updatePacMan(moveThatIsNotBack(game.getPacmanLastMoveMade(), game.getPossibleMoves(game.getPacmanCurrentNodeIndex())));
            }
            game.updateGhosts(ghosts.getMove(game, 1));
            game.updateGame();
            if (game.wasPacManEaten()) {
                return game.getScore();
            }
        }
        return evaluateState(game);
    }

    private MOVE getBestMove(Game game) {

        int pacmanNode = game.getPacmanCurrentNodeIndex();
        int closestPill = 0;
        int distanceToClosestPill = Integer.MAX_VALUE;
        Constants.GHOST closestGhost = Constants.GHOST.BLINKY;
        int closestGhostNode = 0;
        int closestGhostDistance = Integer.MAX_VALUE;

        for (Constants.GHOST ghost : ghosts) { // get closest ghost
            int ghostNode = game.getGhostCurrentNodeIndex(ghost);
            int distanceToGhost = game.getShortestPathDistance(pacmanNode, ghostNode);

            if (distanceToGhost < closestGhostDistance && distanceToGhost != -1) {
                // System.out.println(game.getShortestPathDistance(pacmanNode, ghostNode));
                closestGhostDistance = distanceToGhost;
                closestGhost = ghost;
                closestGhostNode = ghostNode;
            }
        }

        if (closestGhostDistance < 5 && game.getGhostEdibleTime(closestGhost) < 7) { // Run away from the ghost
            Map<Game, MOVE> choices = new HashMap<>();
            for (MOVE move : allMoves) {
                Game copy = game.copy();
                copy.updatePacMan(move);
                if (copy.getShortestPathDistance(copy.getPacmanCurrentNodeIndex(), closestGhostNode) > closestGhostDistance) {
                    //System.out.println("AWAAY  " + move + " " + closestGhost);
                    choices.put(copy, move);
                }
            }
            double bestScore = Integer.MIN_VALUE;
            MOVE bestMove = null;
            for (Game gameTry : choices.keySet()) {
                double currentScore = evaluateAwayFromGhosts(gameTry);
                if (bestScore < currentScore) {
                    bestScore = currentScore;
                    bestMove = choices.get(gameTry);
                }
            }
            return bestMove;
        }
        if (game.getGhostEdibleTime(closestGhost) >= 7) { // chaise closest ghost if it is edible
            for (MOVE move : allMoves) {
                Game copy = game.copy();
                copy.updatePacMan(move);
                if (copy.getShortestPathDistance(copy.getPacmanCurrentNodeIndex(), closestGhostNode) < closestGhostDistance) {
                    // System.out.println("Towards  " + move + " " + closestGhost);
                    return move;
                }
            }
        }
        for (int i = 0; i < game.getActivePillsIndices().length; i++) { // finds closest pill
            int pill = game.getActivePillsIndices()[i];
            int distanceToPill = game.getShortestPathDistance(pacmanNode, pill);
            if (distanceToPill < distanceToClosestPill) {
                distanceToClosestPill = distanceToPill;
                closestPill = pill;
            }
        }
        for (MOVE move : allMoves) { // if nothing else go towards closest pill
            Game copy = game.copy();
            copy.updatePacMan(move);
            if (copy.getShortestPathDistance(copy.getPacmanCurrentNodeIndex(), closestPill) < distanceToClosestPill) { // got closer to pill check because go towards function is bad
                return move;
            }
        }
        return MOVE.RIGHT;
    }

    private double evaluateAwayFromGhosts(Game game) {// check how much away from near ghosts is the pacman. // i think bad should make something better
        double result = 10000;
        int pacman = game.getPacmanCurrentNodeIndex();
        for (Constants.GHOST ghost : ghosts) {
            // System.out.println("distance: " + game.getShortestPathDistance(pacman, game.getGhostCurrentNodeIndex(ghost)));
            if (game.getShortestPathDistance(pacman, game.getGhostCurrentNodeIndex(ghost)) < 20) {
                result -= game.getShortestPathDistance(pacman, game.getGhostCurrentNodeIndex(ghost));
            }
        }
        return result;
    }

    private int evaluateState(Game game) {
        int score = 0;
        score += game.getPacmanNumberOfLivesRemaining() * 1000000;
        score += game.getScore();// basically try not to loose lives but if both states have the same lives pick the one with the biggest score
        return score;
    }

}
