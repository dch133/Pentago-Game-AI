package student_player;

import pentago_swap.PentagoBoardState;
import pentago_swap.PentagoBoardState.Piece;
import static student_player.MyTools.*;

import static pentago_swap.PentagoBoardState.BOARD_SIZE;


public class Heuristic
{

    /* Heuristic Calculator */
    public static int heuristicFunction(PentagoBoardState pbs, int myTurnNumber)
    {
        int horizontalCost = checkHorizontal(pbs,myTurnNumber);
        int verticalCost = checkVertical(pbs,myTurnNumber);
        int diagonalCost = Diagonals.checkDiag(pbs,myTurnNumber);

        //Check if winning position
        if (horizontalCost == Integer.MAX_VALUE|| verticalCost == Integer.MAX_VALUE || diagonalCost == Integer.MAX_VALUE)
            return POTENTIAL_WIN_COST; //Next move is a potential winning position: give it high priority

        return horizontalCost+verticalCost+diagonalCost;

//        int horizontalAndVerticalCost = HorizontalsVerticals.checkHorizontalsAndVerticals(pbs,myTurnNumber);
//        int diagonalsCost = Diagonals.checkDiag(pbs,myTurnNumber);
//
//        return horizontalAndVerticalCost+diagonalsCost;
    }

    public static int checkIfWonOrLost(PentagoBoardState pbs, int studentPlayerNumber)
    {
        // Check if game is over
        int winner = pbs.getWinner();
        if (winner == studentPlayerNumber)
            return WIN_COST; // I wins
        else
            return -1*WIN_COST; // opponent wins
    }

    /*
    * Check the position of all pieces on the board that might form winning positions horizontally
    * 3 types of horizontal: top, middle, bottom
    *
    * */
    public static int checkHorizontal(PentagoBoardState pbs, int studentTurnNumber)
    {
        Piece[][] boardState = MyTools.getCurrentBoardSetup(pbs);

        Piece[] topRow1       = boardState[0]; //top
        Piece[] middleRow2    = boardState[1]; //middle
        Piece[] bottomRow3    = boardState[2]; //bottom
        Piece[] topRow4       = boardState[3]; //top
        Piece[] middleRow5    = boardState[4]; //middle
        Piece[] bottomRow6    = boardState[5]; //bottom

        //Combine the rows into an array since any element from either row can potentially match with one from the other row
        Piece[] topRow = new Piece[BOARD_SIZE*2];
        Piece[] middleRow = new Piece[BOARD_SIZE*2];
        Piece[] bottomRow = new Piece[BOARD_SIZE*2];
        // append both sets of rows that correspond to the same level in the quad
        for (int i = 0; i < BOARD_SIZE; i++)
        {
            topRow[i] = topRow1[i];
            middleRow[i] = middleRow2[i];
            bottomRow[i] = bottomRow3[i];
            topRow[i+6] = topRow4[i];
            middleRow[i+6] = middleRow5[i];
            bottomRow[i+6] = bottomRow6[i];
        }
        int opponentTurnNumber = 1-studentTurnNumber;

        //Count how sets of consecutive pieces I have horizontally
        // TR = Top Row     MR = Middle Row     BR = Bottom Row
        int studConsecTR = studentCalcCostConsecPieceHorizOrVert(topRow,studentTurnNumber);
        int studConsecMR = studentCalcCostConsecPieceHorizOrVert(middleRow,studentTurnNumber);
        int studConsecBR = studentCalcCostConsecPieceHorizOrVert(bottomRow,studentTurnNumber);

        //Count how sets of consecutive pieces opponent has horizontally
        int oppConsecTR = opponentCalcCostConsecPieceHorizOrVert(topRow,opponentTurnNumber,false);
        int oppConsecMR = opponentCalcCostConsecPieceHorizOrVert(middleRow,opponentTurnNumber,false);
        int oppConsecBR = opponentCalcCostConsecPieceHorizOrVert(bottomRow,opponentTurnNumber,false);

        // If we found a winning/losing position return it right away
        if (studConsecTR == Integer.MAX_VALUE || studConsecMR == Integer.MAX_VALUE || studConsecBR == Integer.MAX_VALUE)
            return Integer.MAX_VALUE;

        int heuristicInStudFavor = studConsecTR+studConsecMR+studConsecBR;
        int heuristicInOppFavor = oppConsecTR+oppConsecMR+oppConsecBR;
        return heuristicInStudFavor-(int) COST_MULTIPLIER_WHEN_BLACK *heuristicInOppFavor;

//        return heuristicInStudFavor-heuristicInOppFavor;
    }

    /*
     * Check the position of all pieces on the board that might form winning positions vertically
     * 3 types of vertical: left, middle, right
     *
     * */
    public static int checkVertical(PentagoBoardState pbs, int studentTurnNumber)
    {
        Piece[][] boardState = MyTools.getCurrentBoardSetup(pbs);

        Piece[] leftCol1      = MyTools.getColumn(boardState,0); //left
        Piece[] middleCol2    = MyTools.getColumn(boardState,1); //middle
        Piece[] rightCol3     = MyTools.getColumn(boardState,2); //right
        Piece[] leftCol4      = MyTools.getColumn(boardState,3); //left
        Piece[] middleCol5    = MyTools.getColumn(boardState,4); //middle
        Piece[] rightCol6     = MyTools.getColumn(boardState,5); //right

        //Combine the cols into an array just like the rows
        Piece[] leftCol = new Piece[BOARD_SIZE*2];
        Piece[] middleCol = new Piece[BOARD_SIZE*2];
        Piece[] rightCol = new Piece[BOARD_SIZE*2];
        // append both sets of rows that correspond to the same level in the quad
        for (int i = 0; i < BOARD_SIZE; i++)
        {
            leftCol[i] = leftCol1[i];
            middleCol[i] = middleCol2[i];
            rightCol[i] = rightCol3[i];
            leftCol[i+6] = leftCol4[i];
            middleCol[i+6] = middleCol5[i];
            rightCol[i+6] = rightCol6[i];
        }
        int opponentTurnNumber = 1-studentTurnNumber;

        //Count how sets of consecutive pieces I have vertically
        // LC = Left Column     MC = Middle Column     RC = Right Column
        int studConsecLC = studentCalcCostConsecPieceHorizOrVert(leftCol,studentTurnNumber);
        int studConsecMC = studentCalcCostConsecPieceHorizOrVert(middleCol,studentTurnNumber);
        int studConsecRC = studentCalcCostConsecPieceHorizOrVert(rightCol,studentTurnNumber);

        //Count how sets of consecutive pieces opponent has vertically
        int oppConsecLC = opponentCalcCostConsecPieceHorizOrVert(leftCol,opponentTurnNumber,false);
        int oppConsecMC = opponentCalcCostConsecPieceHorizOrVert(middleCol,opponentTurnNumber,false);
        int oppConsecRC = opponentCalcCostConsecPieceHorizOrVert(rightCol,opponentTurnNumber,false);

        // If we found a winning/losing position return it right away
        if (studConsecLC == Integer.MAX_VALUE || studConsecMC == Integer.MAX_VALUE || studConsecRC == Integer.MAX_VALUE)
            return Integer.MAX_VALUE;

        int heuristicInStudFavor = studConsecLC+studConsecMC+studConsecRC;
        int heuristicInOppFavor = oppConsecLC+oppConsecMC+oppConsecRC;
        return heuristicInStudFavor-(int) COST_MULTIPLIER_WHEN_BLACK *heuristicInOppFavor;
    }


    /*
     * Check the position of all pieces on the board and count how many are there in strategic positions
     * 2 types: Vertical & Horizontal
     * Check for singles, twos, threes, and four pieces together
     * */
    private static int[] getConsecutiveHorizOrVertPieces(Piece[] boardRange, int myTurnNumber)
    {
        String myColor = MyTools.getMyPieceColor(myTurnNumber);
        boolean found1 = false;     // flag if found 1 in a row
        boolean found2 = false;     // flag if found 2 in a row
        boolean found3 = false;     // flag if found 3 in a row
        boolean found4 = false;     // flag if found 4 in a row
        boolean found5 = false;     // flag if found 5 in a row

        int singles = 0;            //keep track of 1 in a row
        int twos    = 0;            //keep track of 2 in a row
        int threes  = 0;            //keep track of 3 in a row
        int fours   = 0;            //keep track of 4 in a row
        int fives   = 0;            //keep track of 5 in a row (if one set appears return right away)

        for (int i = 0; i < boardRange.length; i++)
        {
            //If I'm NOT at the last slot on the board and I found my piece
            //Keep track of how many I found consecutively
            if (boardRange[i].toString().equals(myColor) && i+1 != boardRange.length)
            {
                singles+=singles;
                if (!found1)        found1 = true;
                else if (!found2)   found2 = true;
                else if (!found3)   found3 = true;
                else if (!found4)   found4 = true;
                else if (!found5)   found5 = true;

            }
            // If I AM at the last slot on the board and I found my piece
            // Calculate the corresponding cost I accumulated
            else if(boardRange[i].toString().equals(myColor) && i+1 == boardRange.length)
            {
                if (found5)         return new int[]{0,0,0,0,1};
                else if (found4)    fours++;
                else if (found3)    threes++;
                else if (found2)    twos++;
            }
            // If no piece was found, I am at the end of a set of consecutive pieces
            // Calculate the corresponding cost I accumuated so far
            else
            {
                if (found5)  return new int[]{0,0,0,0,1};
                else if (found4)
                {
                    fours++;
                    found4 = false;
                    found3 = false;
                    found2 = false;
                    found1 = false;
                }
                else if (found3)
                {
                    threes++;
                    found3 = false;
                    found2 = false;
                    found1 = false;
                }
                else if (found2)
                {
                    twos++;
                    found2 = false;
                    found1 = false;
                }
                else found1 = false;
            }
        }
        return new int[]{singles,twos,threes,fours,fives};
    }

    private static int studentCalcCostConsecPieceHorizOrVert(Piece[] boardRange, int myTurnNumber)
    {
        int[] result = getConsecutiveHorizOrVertPieces(boardRange,myTurnNumber);

        int singles = result[0];            //keep track of 1 in a row
        int twos    = result[1];            //keep track of 2 in a row
        int threes  = result[2];            //keep track of 3 in a row
        int fours   = result[3];            //keep track of 4 in a row
        int fives   = result[4];            //keep track of 5 in a row (if not 0, return INFINITY)

        if (fives > 0) return POTENTIAL_WIN_COST;
        return SINGLES_COST*singles+TWO_CONSEC_COST*twos+THREE_CONSEC_COST*threes+FOUR_CONSEC_COST*fours;
    }

    private static int opponentCalcCostConsecPieceHorizOrVert(Piece[] boardRange, int myTurnNumber, boolean considerSwaps)
    {
        int[] opponentResult = getConsecutiveHorizOrVertPieces(boardRange,myTurnNumber);
        int[] studentResult = getConsecutiveHorizOrVertPieces(boardRange,1-myTurnNumber);

        int opSingles = opponentResult[0];            //keep track of 1 in a row
        int opTwos    = opponentResult[1];            //keep track of 2 in a row
        int opThrees  = opponentResult[2];            //keep track of 3 in a row
        int opFours   = opponentResult[3];            //keep track of 4 in a row
        int opFives   = opponentResult[4];            //keep track of 5 in a row (if not 0, return INFINITY)

        //TODO compare with opponent: e.g. if WHITE & have more consec than BLACK = less dangerous
        int studSingles = studentResult[0];           //keep track of 1 in a row
        int studTwos    = studentResult[1];           //keep track of 2 in a row
        int studThrees  = studentResult[2];           //keep track of 3 in a row
        int studFours   = studentResult[3];           //keep track of 4 in a row
        int studFives   = studentResult[4];           //keep track of 5 in a row (if not 0, return INFINITY)

        if (opFives > 0) return POTENTIAL_WIN_COST;
        if (opFours > 1) return FOUR_CONSEC_COST*opFours;
        if (opThrees > 1) return THREE_CONSEC_COST*opThrees;
        if (opTwos > 1) return TWO_CONSEC_COST*opTwos;
        if (opSingles > 4) return FOUR_IN_SAME_RANGE*opSingles; // more than 3 of opponent's pieces on the same row is dangerous
        else
            return SINGLES_COST*opSingles + TWO_CONSEC_COST * opTwos
                    + 2*THREE_CONSEC_COST * opThrees + 2*FOUR_CONSEC_COST * opFours;
    }


    // Calculate cost on Middle Diagonals/ Horizontal/ Verticals
    public static int calcCostPiecesMiddleDiagsHorizontalsVerticals(WinningPositions type, int myTurnNumber,
                                                                    Piece[][] topLeft, Piece[][] topRight,
                                                                    Piece[][] bottomLeft, Piece[][] bottomRight)
    {
        boolean[][] allQuadStrategicPositions;

        // If Horizontal/Vertical do default case else do Middle Diag case
        switch (type)
        {
            case MRL:
            case MLR:
                allQuadStrategicPositions =
                        Diagonals.checkStrategicPositionsForSpecificDiag
                                (type,myTurnNumber, topLeft,topRight,bottomLeft,bottomRight);
                break;
                default:
                    allQuadStrategicPositions =
                            HorizontalsVerticals.checkStrategicCellsForSpecificHV
                                    (type,myTurnNumber,topLeft,topRight,bottomLeft,bottomRight);
                    break;
        }

        boolean[] quad1 = allQuadStrategicPositions[0];
        boolean[] quad2 = allQuadStrategicPositions[1];
        boolean[] quad3 = allQuadStrategicPositions[2];
        boolean[] quad4 = allQuadStrategicPositions[3];

        int free                        = countStrategicPositions(quad1,quad2,quad3,quad4, 0);
        int _1Full2EmptyLorR            = countStrategicPositions(quad1,quad2,quad3,quad4, 1);
        int _1MiddleFull2Empty          = countStrategicPositions(quad1,quad2,quad3,quad4, 2);
        int whiteAndBlackOnLorR         = countStrategicPositions(quad1,quad2,quad3,quad4, 3);
        int _1MiddleAndOpponentOnLorR   = countStrategicPositions(quad1,quad2,quad3,quad4, 4);
        int _2Full1Empty                = countStrategicPositions(quad1,quad2,quad3,quad4, 5);
        int _2FullMiddleEmpty           = countStrategicPositions(quad1,quad2,quad3,quad4, 6);
        int _2FullOneOpponent           = countStrategicPositions(quad1,quad2,quad3,quad4, 7);
        int _3Full                      = countStrategicPositions(quad1,quad2,quad3,quad4, 8);

        int basicCost = //Cost of each possible 3slot state separately
                THREE_CONSEC_COST*_3Full
                        +TWO_CONSEC_COST*(_2Full1Empty+_2FullOneOpponent) + (TWO_CONSEC_COST-5)*_2FullMiddleEmpty
                        +SINGLES_COST*(_1Full2EmptyLorR)+(SINGLES_COST+5)*(_1MiddleFull2Empty)
                        +(SINGLES_COST-5)*(whiteAndBlackOnLorR)+(SINGLES_COST-3)*(_1MiddleAndOpponentOnLorR);

        int comboWith3Full = //combine a 3full with 2s and 1s
                FIVE_CONSEC_COST*(_3Full*(_2Full1Empty+_2FullOneOpponent))
                        +(FIVE_CONSEC_COST-5)*(_3Full*_2FullMiddleEmpty)
                        +FOUR_CONSEC_COST*_3Full*(_1Full2EmptyLorR+_1MiddleFull2Empty)
                        +(FOUR_CONSEC_COST-5)*_3Full*(_1MiddleAndOpponentOnLorR+whiteAndBlackOnLorR)
                ;
        int comboWith2Full =  //combine a 2 with 2s and 1s (7)
                FOUR_CONSEC_COST*(_2Full1Empty*_2FullOneOpponent)
                        +(FOUR_CONSEC_COST-5)*_2FullMiddleEmpty*(_2Full1Empty+_2FullOneOpponent)        // 2 inst. of 2s
                        +THREE_CONSEC_COST*(_2Full1Empty)*(_1Full2EmptyLorR+_1MiddleFull2Empty)         // 1 inst. of 2s 00_
                        +THREE_CONSEC_COST*_2FullOneOpponent*(_1Full2EmptyLorR+_1MiddleFull2Empty)      // 1 inst. of 2s 00X
                        +(THREE_CONSEC_COST-5)*_2FullMiddleEmpty*(_1Full2EmptyLorR+_1MiddleFull2Empty)  // 1 inst. of 2s 0_0
                        +(THREE_CONSEC_COST-5-2)*_2FullMiddleEmpty*whiteAndBlackOnLorR;                 // 1 inst. of 2s 0_0 with 0_X
        // Calculate Heuristic given strategic positions available
        //1)If we have 3s
        if(_3Full>1) //You can do 5 right away and no one can block you!!
            return POTENTIAL_WIN_COST;

        return comboWith3Full+comboWith2Full+basicCost;

    }

    // Count the number of specific strategic positions in all quads
    private static int countStrategicPositions(boolean[] quad1, boolean[] quad2, boolean[] quad3, boolean[] quad4, int index)
    {
        int count = 0;
        if (quad1 != null && quad1[index]) count++;
        if (quad2 != null && quad2[index]) count++;
        if (quad3 != null && quad3[index]) count++;
        if (quad4 != null && quad4[index]) count++;

        return count;
    }



}
