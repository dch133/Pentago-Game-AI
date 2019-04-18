package student_player;

import pentago_swap.PentagoBoardState;
import pentago_swap.PentagoBoardState.Piece;
import pentago_swap.PentagoBoardState.Quadrant;
import pentago_swap.PentagoMove;


import java.util.*;

import static student_player.Heuristic.heuristicFunction;
import static student_player.MyTools.*;

public class MoveSelection
{

    /* Alpha-Beta Pruning Algorithm: initial call structure:  alphabeta(origin, depth, −∞, +∞, TRUE) */
    private static int alphabeta(int studentPlayerTurnNumber, PentagoBoardState pbs, int depth, int α, int β, boolean maximizingPlayer)
    {
        if (pbs.gameOver()) return Heuristic.checkIfWonOrLost(pbs,studentPlayerTurnNumber);
        if (depth == 0)
        {
            /* return the heuristic value of node */
            int cost =heuristicFunction(pbs, studentPlayerTurnNumber);
            return cost;
        }
        int value;

        ArrayList<PentagoMove> legalMoves = pbs.getAllLegalMoves();

        if (maximizingPlayer) {
            value = Integer.MIN_VALUE;


            for (PentagoMove nextMove: legalMoves)
            {
                //create a new boardState with the new move applied
                PentagoBoardState newPbs = MyTools.copyCurrentBoardState(pbs);
                newPbs.processMove(nextMove);

                // update the value for current board state based on the new move applied
                value = Math.max(value, alphabeta(studentPlayerTurnNumber, newPbs , depth- 1, α, β, false));
                α = Math.max(α, value);
                if (α >= β) break; /* β cut-off */
            }
            return value;
        } else
        {
            value = Integer.MAX_VALUE;
            for (PentagoMove nextMove: legalMoves)
            {
                PentagoBoardState newPbs = MyTools.copyCurrentBoardState(pbs);
                newPbs.processMove(nextMove);
                value = Math.min(value, alphabeta(studentPlayerTurnNumber, newPbs, depth - 1, α, β, true));
                β = Math.min(β, value);
                if (α >= β) break; /* α cut-off */
            }
            return value;
        }
    }

    private static ArrayList<PentagoMove> filterBestLegalMoves(int studentPlayerTurnNumber, PentagoBoardState pbs)
    {
        ArrayList<PentagoMove> legalMoves = pbs.getAllLegalMoves();
        Collections.shuffle(legalMoves);
        ArrayList<PentagoMove> bestLegalMoves = new ArrayList<>();

        for (PentagoMove nextMove: legalMoves)
        {
            //create a new boardState with the new move applied
            PentagoBoardState newPbs = MyTools.copyCurrentBoardState(pbs);
            newPbs.processMove(nextMove);

            boolean isGameLost = newPbs.gameOver() && Heuristic.checkIfWonOrLost(newPbs,studentPlayerTurnNumber) < 0;
            boolean isGameWon = newPbs.gameOver() && Heuristic.checkIfWonOrLost(newPbs,studentPlayerTurnNumber) == WIN_COST;
            //int heuristic = Heuristic.heuristicFunction(newPbs,studentPlayerTurnNumber);
            //boolean isPositiveHeuristic = heuristic>0;
            if (isGameWon) // If you win, return right away
            {
//                System.out.println("FOUND WINNING MOVE!!!");
                bestLegalMoves = new ArrayList<>();
                bestLegalMoves.add(nextMove);
                return bestLegalMoves;
            }
            if (isGameLost) //|| isPositiveHeuristic)
                continue;

            bestLegalMoves.add(nextMove);
        }
        return  simulateMove(pbs,studentPlayerTurnNumber,bestLegalMoves);

    }

    public static PentagoMove calculateBestMove(int turnNumber, PentagoBoardState pbs)
    {
//        ArrayList<PentagoMove> legalMoves = pbs.getAllLegalMoves();
        HashMap<PentagoMove, Double> valueOfMoves = new HashMap<>();
        PentagoMove bestMove; // best move chosen to return

        long start = System.currentTimeMillis(); // Start timer

        ArrayList<PentagoMove> legalMoves = filterBestLegalMoves(turnNumber,pbs);

        if (legalMoves.size() == 1) return legalMoves.get(0); //don run alphabeta on 1 element


//        System.out.println("SIZE: "+legalMoves.size());
        for (PentagoMove currentMove :legalMoves)
        {
            // Avoid running out of time and play randomly if necessary
            if (System.currentTimeMillis() - start > CHOOSE_MOVE_TIME_LIMIT)
            {
//                System.out.println("TIME LIMIT EXCEEDED: Playing Best Available Move");
                break;
            }


            PentagoBoardState newPbs = copyCurrentBoardState(pbs);
            newPbs.processMove(currentMove);
            valueOfMoves.put(currentMove, (double) alphabeta(turnNumber, newPbs,MyTools.DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE, true));
        }

//        System.out.println("NEW SIZE: "+valueOfMoves.size());

        // Sort the moves based on best heuristic value
        valueOfMoves = sortByValue(valueOfMoves);

        //The last move in the list is the move with the highest heuristic
        int bestMoveIndex = valueOfMoves.keySet().size()-1 > 0 ? valueOfMoves.keySet().size()-1 : 0;
        bestMove = (PentagoMove) valueOfMoves.keySet().toArray()[bestMoveIndex];

        // If you have the winning move, go for it
//        if ((int) valueOfMoves.values().toArray()[bestMoveIndex] == Integer.MAX_VALUE)
//        {
//            //bestMove = (PentagoMove) valueOfMoves.keySet().toArray()[bestMoveIndex];
//            //Since you have at least 1 winning move, you can randomly choose from them
//            bestMove = chooseRandomMoveToReturn(valueOfMoves);
//        }
//        else //If you don't have a winning move pick the best possible move given heuristics within range of SUBSET_OF_BEST_MOVES
//            bestMove = chooseMoveToReturn(valueOfMoves); //choose a good move with gaussian distribution

        //TESTING
//        PentagoBoardState test = copyCurrentBoardState(pbs);
//        test.processMove(bestMove);
//        printCurrentBoardSetup(test);

        return bestMove;

    }

    // Get a subset of most profitable moves to pick from (similar to beam-search's beam)
    private static ArrayList<PentagoMove> getFilteredBestMovesList(HashMap<PentagoMove, Double> movesAndCost,
                                                          int size, double cutOffThresh)
    {
        ArrayList<PentagoMove> topKBestMoves = new ArrayList<>(); //store best moves
        Object[] possibleMoves = movesAndCost.keySet().toArray(); //get a list of moves

        if (possibleMoves.length < size)
        {
            for (int i = 0; i < possibleMoves.length; i++)
            {
                topKBestMoves.add((PentagoMove) possibleMoves[i]);
            }
            return topKBestMoves;
        }


        // keep track of how many moves we put,and not to go over the limit
        for (int i = possibleMoves.length-1; i > possibleMoves.length-1-size ; i--)
        {   //Don't consider moves less than the threshold value given: 0 -> only consider moves that are beneficial to me
            PentagoMove move = (PentagoMove) possibleMoves[i];
            if (movesAndCost.get(move) > cutOffThresh)
            {
                topKBestMoves.add((PentagoMove) possibleMoves[i]);
            }
        }
        return topKBestMoves;
    }



    // Pick one of the best moves with gaussian probability around the value with highest heuristic
    private static PentagoMove chooseMoveToReturn(HashMap<PentagoMove, Double> valueOfMoves)
    {
        ArrayList<PentagoMove> bestKMoves = getFilteredBestMovesList(valueOfMoves, SUBSET_OF_BEST_MOVES,0);
        if (bestKMoves.size() != 0) //at least 1 move is good for me, I can pick one with Normal distribution
        {
            int moveChosenIndex = getGaussian(0,bestKMoves.size());
            return  (PentagoMove) valueOfMoves.keySet().toArray()[moveChosenIndex];
        }
        else
        {   /*Else if all moves are cost negative (bad for you), pick the least bad one for you
              The last move in the list is the move with the highest heuristic */
            int bestMoveIndex = valueOfMoves.keySet().size()-1;
            return  (PentagoMove) valueOfMoves.keySet().toArray()[bestMoveIndex];
        }
    }

    // Choose a moves randomly based on a subset of moves
    private static PentagoMove chooseRandomMoveToReturn(HashMap<PentagoMove, Double> valueOfMoves)
    {
        ArrayList<PentagoMove> bestKMoves = getFilteredBestMovesList(valueOfMoves, SUBSET_OF_BEST_MOVES,WIN_COST-1);
        return (PentagoMove) valueOfMoves.keySet().toArray()[getRandom(bestKMoves.size())];
    }


    /*
    * Special Starter White Piece Strategy to for optimal game play (similarly in all directions with horiz,vert,diag)
    * 1st move      2nd move    3rd move (white)
    * _|_|_         _|_|_         _|_|_
    * _|x|_         _|x|_         _|x|_
    * _|_|_         _|_|_         _|x|_
    *
    * _|_|_         _|_|_         _|_|_
    * _|_|_         _|x|_         _|x|_
    * _|_|_         _|_|_         _|_|_
    * */

    /* Apply same 1st and 2nd move strategy whether you are black or white */
    public static PentagoMove calculateBestMove1stOr2ndTurn(int turnNumber, PentagoBoardState pbs)
    {
        Piece topLeft = pbs.getPieceAt(1,1);
        Piece topRight = pbs.getPieceAt(4,1);
        Piece btmLeft = pbs.getPieceAt(1,4);
        Piece btmRight = pbs.getPieceAt(4,4);

        ArrayList<int[]> availCenterCells = new ArrayList<>(); //store coord of free cells

        if (topLeft.compareTo(Piece.EMPTY) == 0) availCenterCells.add(new int[]{1,1});
        if (topRight.compareTo(Piece.EMPTY) == 0) availCenterCells.add(new int[]{4,1});
        if (btmLeft.compareTo(Piece.EMPTY) == 0) availCenterCells.add(new int[]{1,4});
        if (btmRight.compareTo(Piece.EMPTY) == 0) availCenterCells.add(new int[]{4,4});

        return buildAMove(turnNumber, availCenterCells);
    }

    /* Strategy for 2nd and 3rd move varies whether you are black or white */
    public static PentagoMove calculateBestMove3rdTurnWhite(int turnNumber, PentagoBoardState pbs)
    {
        Piece topLeft           = pbs.getPieceAt(1,1);       // get the piece in the middle of a quad
        boolean isTopLeftWhite  = topLeft.compareTo(Piece.WHITE) == 0; // flag if the piece is white
        Piece topRight          = pbs.getPieceAt(4,1);
        boolean isTopRightWhite = topRight.compareTo(Piece.WHITE) == 0;
        Piece btmLeft           = pbs.getPieceAt(1,4);
        boolean isBtmLeftWhite  = btmLeft.compareTo(Piece.WHITE) == 0;
        Piece btmRight          = pbs.getPieceAt(4,4);
        boolean isBtmRightWhite = btmRight.compareTo(Piece.WHITE) == 0;

        // keep track of all white cells in the middle of a quad (at most 2 at this point)
        boolean[] flag = new boolean[]{isTopLeftWhite, isTopRightWhite, isBtmLeftWhite, isBtmRightWhite};
        ArrayList<int[]> availCells = new ArrayList<>(); //store coord of cells that help form a line (as in pic above)

        //Want to store all the pieces next to the white piece in the center
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
            {
                if (isTopLeftWhite)
                    if (pbs.getPieceAt(i,j).compareTo(Piece.EMPTY) == 0) //only consider empty cells
                        availCells.add(new int[]{i,j});

                if (isTopRightWhite)
                    if (pbs.getPieceAt(i+3,j).compareTo(Piece.EMPTY) == 0)
                        availCells.add(new int[]{i+3,j});

                if (isBtmLeftWhite)
                    if (pbs.getPieceAt(i,j+3).compareTo(Piece.EMPTY) == 0)
                        availCells.add(new int[]{i,j+3});

                if (isBtmRightWhite)
                    if (pbs.getPieceAt(i+3,j+3).compareTo(Piece.EMPTY) == 0)
                        availCells.add(new int[]{i+3,j+3});
            }
//        MyTools.printCurrentBoardSetup(pbs);
        return buildAMove(turnNumber, availCells);
    }


    private static PentagoMove buildAMove(int playerTurnNumber, ArrayList<int[]> availCells)
    {
        Random rand = new Random();
        int[] randomCellCoords = availCells.get(rand.nextInt(availCells.size()));       // random cell coord
        Quadrant randSwap1 = Quadrant.values()[rand.nextInt(Quadrant.values().length)]; // random quad 1
        Quadrant randSwap2 = Quadrant.values()[rand.nextInt(Quadrant.values().length)]; // random quad 2;

        //Make sure you swap distinct quads (can't swap the same one)
        while (randSwap2.compareTo(randSwap1) == 0)
            randSwap2 = Quadrant.values()[rand.nextInt(Quadrant.values().length)]; // random swap

        return new PentagoMove(randomCellCoords[0],randomCellCoords[1],randSwap1, randSwap2, playerTurnNumber);

    }


    // Run Monte Carlo - Style simulations to weed out worst moves
    public static ArrayList<PentagoMove> simulateMove(PentagoBoardState pbs, int myTurnNumber, ArrayList<PentagoMove> legalMoves)
    {
        long start = System.currentTimeMillis(); // Start timer

        HashMap<PentagoMove, Tuple> bestLegalMoves = new HashMap<>(); // Store Move and fraction of games won/total sims
        boolean simOver = false;
        double simCount = 1.0;

        // Default Policy
        for (PentagoMove nextMove : legalMoves)
        {
            // If 1st time simulation initialise the fraction 0/0
            if(!bestLegalMoves.containsKey(nextMove))
                bestLegalMoves.put(nextMove, new Tuple<>(0.0, 0.0));

            //create a new boardState with the new move applied
            PentagoBoardState newPbs = MyTools.copyCurrentBoardState(pbs);
            newPbs.processMove(nextMove);

            //simulate until the end
            while (!newPbs.gameOver()) newPbs.processMove((PentagoMove) newPbs.getRandomMove());

            boolean isGameWon = Heuristic.checkIfWonOrLost(newPbs,myTurnNumber)>0;

            // update the fraction of games won/total games simulated
            double numerator   = (double) bestLegalMoves.get(nextMove).x;
            double denominator = (double) bestLegalMoves.get(nextMove).y;


            if (isGameWon)
                bestLegalMoves.put(nextMove, new Tuple<>(numerator + 1, denominator+1));
            else bestLegalMoves.put(nextMove, new Tuple<>(numerator, denominator+1));
        }

        double maxUCT = -1;
        PentagoMove nextMoveToSim = null;

        // Use upper confidence trees to choose on which move you simulate next
        while(true)
        {
            simCount++;
            for (PentagoMove nextMove : bestLegalMoves.keySet())
            {
                // Avoid running out of time and play randomly if necessary
                if (System.currentTimeMillis() - start > SIM_TIME_LIMIT)
                {
//                    System.out.println("TIME UP FOR SIMS: Ran "+ simCount);
                    simOver = true;
                    break;
                }

                Tuple values = bestLegalMoves.get(nextMove);
                double uctValue = ((double) values.x / (double) values.y) + Math.sqrt(2 * Math.log(simCount) / (double) values.y);
                if (maxUCT < uctValue)
                {
                    maxUCT = uctValue;
                    nextMoveToSim = nextMove;
                }
            }

            if (simOver) break; //get out of the while loop

            //create a new boardState with the new move applied
            PentagoBoardState newPbs = MyTools.copyCurrentBoardState(pbs);
            newPbs.processMove(nextMoveToSim);

            //simulate until the end
            while (!newPbs.gameOver()) newPbs.processMove((PentagoMove) newPbs.getRandomMove());

            boolean isGameWon = Heuristic.checkIfWonOrLost(newPbs,myTurnNumber)>0;

            // update the fraction of games won/total games simulated
            double numerator   = (double) bestLegalMoves.get(nextMoveToSim).x;
            double denominator = (double) bestLegalMoves.get(nextMoveToSim).y;

            if (isGameWon)
                    bestLegalMoves.put(nextMoveToSim, new Tuple<>(numerator + 1, denominator+1));
                else bestLegalMoves.put(nextMoveToSim, new Tuple<>(numerator, denominator+1));

            // reset the check
            nextMoveToSim = null;
            maxUCT = -1;

        }

        // Get the map of moves to fraction of game won
        HashMap<PentagoMove, Double> movesAndFraction = new HashMap<>();
        for (PentagoMove nextMove : bestLegalMoves.keySet())
        {
            double numerator   = (double) bestLegalMoves.get(nextMove).x;
            double denominator = (double) bestLegalMoves.get(nextMove).y;
            movesAndFraction.put(nextMove,numerator/denominator);
        }

        movesAndFraction = sortByValue(movesAndFraction); //sort in increasing order

//        System.out.println("FINISHED SIMULATING: Running alpha-beta pruning on the best moves from simulation");

        // Return the best K-moves
        return  getFilteredBestMovesList(movesAndFraction,SUBSET_OF_BEST_MOVES,Integer.MIN_VALUE/2);



    }

}
