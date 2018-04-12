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
    private Set<Node> visited = new LinkedHashSet<>();

    /* (non-Javadoc)
     * @see pacman.controllers.Controller#getMove(pacman.game.Game, long)
     */
    public MOVE getMove(Game game, long timeDue) {
        Map<MOVE, Integer> moveScoreMap = new HashMap<>();
        int pacmanNodeIndex = game.getPacmanCurrentNodeIndex();
        MOVE[] moves = game.getPossibleMoves(pacmanNodeIndex);

        for (MOVE move : moves) {
            Game copy = game.copy();
            copy.updatePacMan(move);
            int score = rollout(copy);
            if (!moveScoreMap.containsKey(move)) {
                moveScoreMap.put(move, score);
            } else {
                moveScoreMap.put(move, moveScoreMap.get(move) + score);
            }
        }
        MOVE bestMove = null;
        int bestScore = Integer.MIN_VALUE;
        for (MOVE move : moveScoreMap.keySet()) {
            int score = moveScoreMap.get(move);
            System.out.println(move + "  score:  " + score);
            if (game.getPacmanLastMoveMade() == move) {
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
        System.out.println();

        return bestMove;
    }

    private int rollout(Game game) {
        for (int i = 0; i < 40; i++) {
            game.updatePacMan(getBestMove(game));
            Legacy ghots = new Legacy();
            game.updateGhosts(ghots.getMove(game, 1));
            game.updateGame();
        }
        return evaluateState(game);
    }

    public MOVE getBestMove(Game game) {

        int pacmanNode = game.getPacmanCurrentNodeIndex();
        int closestPill = 0;
        int distanceToClosestPill = Integer.MAX_VALUE;
        Constants.GHOST closestGhost = Constants.GHOST.BLINKY;
        int closestGhostNode = 0;
        int closestGhostDistance = Integer.MAX_VALUE;

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

        if (closestGhostDistance < 5 && game.getGhostEdibleTime(closestGhost) < 10) { // Run away from ghosts
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
        if (game.getGhostEdibleTime(closestGhost) > 10) {
            for (MOVE move : allMoves) {
                Game copy = game.copy();
                copy.updatePacMan(move);
                if (copy.getShortestPathDistance(copy.getPacmanCurrentNodeIndex(), closestGhostNode) < closestGhostDistance) {
                    // System.out.println("Towards  " + move + " " + closestGhost);
                    return move;
                }
            }
        }
        for (int i = 0; i < game.getActivePillsIndices().length; i++) {
            int pill = game.getActivePillsIndices()[i];
            int distanceToPill = game.getShortestPathDistance(pacmanNode, pill);
            if (distanceToPill < distanceToClosestPill) {
                distanceToClosestPill = distanceToPill;
                closestPill = pill;
            }
        }
        //System.out.println("NOMNOM");
        for (MOVE move : allMoves) {
            Game copy = game.copy();
            copy.updatePacMan(move);
            if (copy.getShortestPathDistance(copy.getPacmanCurrentNodeIndex(), closestPill) < distanceToClosestPill) {
                return move;
            }
        }
        return MOVE.RIGHT;
    }

    private double evaluateAwayFromGhosts(Game game) {
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
        int score;
        score =+ game.getPacmanNumberOfLivesRemaining()*100000;
        score =+ game.getScore();
        return score;
    }

}
