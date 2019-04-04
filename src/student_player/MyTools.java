package student_player;

import pentago_swap.*;
import pentago_swap.PentagoBoardState.Piece;

import java.util.*;

import static pentago_swap.PentagoBoardState.BOARD_SIZE;
import static student_player.Heuristic.heuristicFunction;

public class MyTools
{
    /* Parameters */
    public static final int SUBSET_OF_BEST_MOVES        = 10; // 10 moves for depth 3
    public static int DEPTH                             = 3;
    public static int TURN_TO_INCREASE_DEPTH            = 20; // found by trial and error
    public static double COST_MULTIPLIYER_WHEN_BLACK    = 2; // White higher chance of winning: defend more if Black
    public static int POTENTIAL_WIN_COST                = Integer.MAX_VALUE/2;
    public static int WIN_COST                          = Integer.MAX_VALUE;
    public static int SINGLES_COST                      = 6;
    public static int TWO_CONSEC_COST                   = 20;
    public static int THREE_CONSEC_COST                 = 40;
    public static int FOUR_CONSEC_COST                  = 80;
    public static int FIVE_CONSEC_COST                  = Integer.MAX_VALUE/4;
    public static final int FOUR_IN_SAME_RANGE          = 20;
    public static final int SIM_TIME_LIMIT              = 1000;
    public static final int CHOOSE_MOVE_TIME_LIMIT      = 1990;
    /* Enums for all the possible winning positions*/
    public enum WinningPositions
    {
        // Diagonal Positions
        TLR, MLR, BLR, TRL, MRL, BRL,  // T = Top, M = Middle, B = Bottom , LR = going left-to-right, RL = going right-to-left

        // Horizontal and Vertical Positions
        HT, HM, HB, VL, VM, VR;  //H = horiz, V = vert, T = Top, M = Middle, B = Bottom , L = Left, R = Right

        public String toString() { return name(); }
    }

    //Store a board setup in an array for ease of access
    public static Piece[][] getCurrentBoardSetup(PentagoBoardState pbs)
    {
        //Board setup with every piece mapped to a coordinate on the board
        Piece[][] boardState = new Piece[BOARD_SIZE][BOARD_SIZE];

        for (int i = 0; i < BOARD_SIZE; i++) { //Iterate through positions on board
            for (int j = 0; j < BOARD_SIZE; j++) {
                boardState[i][j]= pbs.getPieceAt(i,j);
            }
        }
        return boardState;
    }


    public static void printCurrentBoardSetup(PentagoBoardState pbs)
    {
        for (int i = 0; i < 6; i++)
            System.out.println(Arrays.deepToString(MyTools.getCurrentBoardSetup(pbs)[i]));
    }


    public static PentagoBoardState copyCurrentBoardState(PentagoBoardState boardState)
    { return (PentagoBoardState) boardState.clone(); }

    // Find the turn number based on the number of pieces on the board
    public static int getGameTurnNumber(PentagoBoardState pbs)
    {
        int count = 0;
        for (int i = 0; i < BOARD_SIZE; i++) { //Iterate through positions on board
            for (int j = 0; j < BOARD_SIZE; j++) {
                if(pbs.getPieceAt(i,j) != Piece.EMPTY)
                    count++;
            }
        }
        return count/2 + 1;
    }

    // Convert turnNumber into player colors: turnNumber 0 = White, 1 = black
    public static String getMyPieceColor(int turnNumber)
    {
        if (turnNumber == 0) return "w";
        else return "b";
    }


    // function to sort hashmap by values
    public static HashMap<PentagoMove, Double> sortByValue(HashMap<PentagoMove, Double> hm)
    {
        // Create a list from elements of HashMap
        List<Map.Entry<PentagoMove, Double> > list =
                new LinkedList<Map.Entry<PentagoMove, Double> >(hm.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<PentagoMove, Double> >() {
            public int compare(Map.Entry<PentagoMove, Double> o1,
                               Map.Entry<PentagoMove, Double> o2)
            {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        // put data from sorted list to hashmap
        HashMap<PentagoMove, Double> temp = new LinkedHashMap<PentagoMove, Double>();
        for (Map.Entry<PentagoMove, Double> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    public static int getGaussian(double mean, double variance)
    {
        Random rand = new Random();
        int value = (int) (mean + rand.nextGaussian() * variance);
        if (value < 0) return (-1)*value; //value corresponds to an index in the array and must be >=0
        else return value;
    }

    public static int getRandom(int size)
    {
        Random rand = new Random();
        return rand.nextInt(size);
    }


    // Get the Column from a 2d array
    public static Piece[] getColumn(Piece[][] array, int index){
        Piece[] column = new Piece[array[0].length]; // Here I assume a rectangular 2D array!
        for(int i = 0; i < column.length; i++){
            column[i] = array[i][index];
        }
        return column;
    }


    public static Piece[][][] getQuads(PentagoBoardState pbs)
    {
        Piece[][] boardState = MyTools.getCurrentBoardSetup(pbs);

        // Arrays storing the cells corresponding all possible valid diagonals
        Piece[][] topLeftQuad = new Piece[BOARD_SIZE / 2][BOARD_SIZE / 2];
        Piece[][] topRightQuad = new Piece[BOARD_SIZE / 2][BOARD_SIZE / 2];
        Piece[][] bottomLeftQuad = new Piece[BOARD_SIZE / 2][BOARD_SIZE / 2];
        Piece[][] bottomRightQuad = new Piece[BOARD_SIZE / 2][BOARD_SIZE / 2];


        // append the cells forming the correspond diagonals
        for (int i = 0; i < BOARD_SIZE / 2; i++)
        {
            for (int j = 0; j < BOARD_SIZE / 2; j++)
            {
                topLeftQuad[i][j] = boardState[i][j];
                topRightQuad[i][j] = boardState[i + 3][i];
                bottomLeftQuad[i][j] = boardState[i][j + 3];
                bottomRightQuad[i][j] = boardState[i + 3][i + 3];
            }
        }

        return new Piece[][][]{topLeftQuad, topRightQuad, bottomLeftQuad, bottomRightQuad};
    }

    //Tuple class
    public static class Tuple<X, Y>
    {
        public final X x;
        public final Y y;
        public Tuple(X x, Y y) {
            this.x = x;
            this.y = y;
        }
    }

}

