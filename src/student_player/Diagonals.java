package student_player;

import pentago_swap.PentagoBoardState;
import pentago_swap.PentagoBoardState.Piece;

import static student_player.MyTools.*;
import static student_player.MyTools.WinningPositions.*;

public class Diagonals
{
    /*
     * Check the position of all pieces on the board that might form winning positions diagonally
     * There are 2 directions: Left To Right and Right To Left (symmetric):
     *      Left-Right: start at the Left on Top and Right on the Bottom
     *      Right-Left: opposite
     * 2 of them have size 5 (bottom and top) and 1 has size 6 (middle one - exactly through the middle of the board)
     * *Note: I consider all cells that can form the diagonal
     * e.g. for Bottom left to right diag, in each quad I consider left-right 2-cell diag: it can be swapped into the bigone)
     * */

//    public enum DiagType  //similar structure to enum in PentagoBoardState.Piece Enum
//    {
//        TLR, MLR, BLR, TRL, MRL, BRL;  // T = Top, M = Middle, B = Bottom , LR = going left-to-right, RL = going right-to-left
//
//        public String toString() { return name(); }
//    }

    // Calculate total heuristic for all diagonals
    public static int checkDiag(PentagoBoardState pbs, int studentTurnNumber)
    {
        //Get the Quads
        Piece[][][] quads = MyTools.getQuads(pbs);

        Piece[][] topLeftQuad = quads[0];
        Piece[][] topRightQuad = quads[1];
        Piece[][] bottomLeftQuad = quads[2];
        Piece[][] bottomRightQuad = quads[3];

        int opponentTurnNumber = 1-studentTurnNumber;

        //Count how sets of good strategy positions pieces I have for each diag
        //TLR, MLR, BLR, TRL, MRL, BRL
        int studTLR = calcCostPiecesTopBottomDiags(TLR,studentTurnNumber,topLeftQuad,topRightQuad,bottomLeftQuad,bottomRightQuad);
//        int studMLR = Heuristic.calcCostPiecesMiddleDiagsHorizontalsVerticals(MLR,studentTurnNumber,topLeftQuad,topRightQuad,bottomLeftQuad,bottomRightQuad);
        int studBLR = calcCostPiecesTopBottomDiags(BLR,studentTurnNumber,topLeftQuad,topRightQuad,bottomLeftQuad,bottomRightQuad);
        int studTRL = calcCostPiecesTopBottomDiags(TRL,studentTurnNumber,topLeftQuad,topRightQuad,bottomLeftQuad,bottomRightQuad);
//        int studMRL = Heuristic.calcCostPiecesMiddleDiagsHorizontalsVerticals(MRL,studentTurnNumber,topLeftQuad,topRightQuad,bottomLeftQuad,bottomRightQuad);
        int studBRL = calcCostPiecesTopBottomDiags(BRL,studentTurnNumber,topLeftQuad,topRightQuad,bottomLeftQuad,bottomRightQuad);

        //Count how sets of good strategy positions pieces Opponent have for each diag
        int oppTLR = calcCostPiecesTopBottomDiags(TLR,opponentTurnNumber,topLeftQuad,topRightQuad,bottomLeftQuad,bottomRightQuad);
//        int oppMLR = Heuristic.calcCostPiecesMiddleDiagsHorizontalsVerticals(MLR,opponentTurnNumber,topLeftQuad,topRightQuad,bottomLeftQuad,bottomRightQuad);
        int oppBLR = calcCostPiecesTopBottomDiags(BLR,opponentTurnNumber,topLeftQuad,topRightQuad,bottomLeftQuad,bottomRightQuad);
        int oppTRL = calcCostPiecesTopBottomDiags(TRL,opponentTurnNumber,topLeftQuad,topRightQuad,bottomLeftQuad,bottomRightQuad);
//        int oppMRL = Heuristic.calcCostPiecesMiddleDiagsHorizontalsVerticals(MRL,opponentTurnNumber,topLeftQuad,topRightQuad,bottomLeftQuad,bottomRightQuad);
        int oppBRL = calcCostPiecesTopBottomDiags(BRL,opponentTurnNumber,topLeftQuad,topRightQuad,bottomLeftQuad,bottomRightQuad);


//        int heuristicInStudFavor = studTLR+studMLR+studBLR+studTRL+studMRL+studBRL;
//        int heuristicInOppFavor = oppTLR+oppMLR+oppBLR+oppTRL+oppMRL+oppBRL;


        int heuristicInStudFavor = studTLR+studBLR+studTRL+studBRL;
        int heuristicInOppFavor = oppTLR+oppBLR+oppTRL+oppBRL;


        // Increase the thread worth slightly if you play as Black
//        if(studentTurnNumber == 1)
//            return heuristicInStudFavor - heuristicInOppFavor;
//        else
//            return heuristicInStudFavor - (int) Math.ceil(COST_MULTIPLIYER_WHEN_BLACK*heuristicInOppFavor);

        return heuristicInStudFavor-heuristicInOppFavor;
    }

    // Calculate cost on top/bottom Diagonals
    public static int calcCostPiecesTopBottomDiags(WinningPositions type, int myTurnNumber,
                                                   Piece[][] topLeft, Piece[][] topRight,
                                                   Piece[][] bottomLeft, Piece[][] bottomRight)
    {
        boolean[][] allQuadStrategicPositions = checkStrategicPositionsForSpecificDiag(type,myTurnNumber,
                topLeft,topRight,bottomLeft,bottomRight);

        boolean[] quad1 = allQuadStrategicPositions[0];
        boolean[] quad2 = allQuadStrategicPositions[1];
        boolean[] quad3 = allQuadStrategicPositions[2];
        boolean[] quad4 = allQuadStrategicPositions[3];


        /* 1st bool: if 2 cells are filled
         * 2nd bool - if the corner cell is filled
         * 3rd: if 1 of 2-cell-diag filled */

        int twoCellsFilledCount = 0;
        int cornerFilledCount = 0;
        int oneOfTwoCellCount = 0;

        // count the amount of two cells & 1 corner positions filled
        if (quad1 != null && quad1[0]) twoCellsFilledCount++;
        if (quad2 != null && quad2[0]) twoCellsFilledCount++;
        if (quad3 != null && quad3[0]) twoCellsFilledCount++;
        if (quad4 != null && quad4[0]) twoCellsFilledCount++;

        if (quad1 != null && quad1[1]) cornerFilledCount++;
        if (quad2 != null && quad2[1]) cornerFilledCount++;
        if (quad3 != null && quad3[1]) cornerFilledCount++;
        if (quad4 != null && quad4[1]) cornerFilledCount++;

        if (quad1 != null && quad1[2]) oneOfTwoCellCount++;
        if (quad2 != null && quad2[2]) oneOfTwoCellCount++;
        if (quad3 != null && quad3[2]) oneOfTwoCellCount++;
        if (quad4 != null && quad4[2]) oneOfTwoCellCount++;

        /* LOGIC for heuristic cost distribution:
        * 2cells = 2 consec: have 2 or more is like having 4 consec
        * 2cell+ corner = like having 3 consec
        * 2cell+ corner + 1/2cells = like having 4 consec
        * corner and 1/2cells individually is like a single
        * */
        if (twoCellsFilledCount>1)
            if(cornerFilledCount>0)
                return POTENTIAL_WIN_COST;
            else // don't add bigger boost since 4-filled doesn't guarantee the 5th slot will be available (dont want false hope)
                return FOUR_CONSEC_COST*twoCellsFilledCount+SINGLES_COST*oneOfTwoCellCount;

        if (twoCellsFilledCount == 1)
            if(cornerFilledCount > 0) // for every new cornerFilled -> new combo of 3 consec
                // for every new 1/2Filled -> new combo of 4 consec
                return TWO_CONSEC_COST*twoCellsFilledCount + THREE_CONSEC_COST*cornerFilledCount + FOUR_CONSEC_COST*oneOfTwoCellCount;
            else return TWO_CONSEC_COST+SINGLES_COST*oneOfTwoCellCount;
        else
            return SINGLES_COST*(cornerFilledCount+oneOfTwoCellCount);

    }

    /* Check if pieces occupy strategic positions to form diagonals
     * Check every quad to test the right cells
     * Do this for every legal diagonal (6 in total):
     *
     * Check if any of the following cells are occupied, they can form the corresponding diag:
     * The cells are shown in the switch statements
     */
    public static boolean[][] checkStrategicPositionsForSpecificDiag(WinningPositions type, int myTurnNumber,
                                                                     Piece[][] topLeft, Piece[][] topRight,
                                                                     Piece[][] bottomLeft, Piece[][] bottomRight)
    {
        boolean[] quad1 = new boolean[0];
        boolean[] quad2 = new boolean[0];
        boolean[] quad3 = new boolean[0];
        boolean[] quad4 = new boolean[0];

        // Find the appropriate diag type and find the strategic cells that would build it
        switch(type)
        {
            case TLR:
                 /* TOP Left-To-Right Diag:
                  * _|X|_
                  * _|_|X
                  * X|_|_
                  */
                quad1 = checkStrategicPositionsForSideDiags
                        (myTurnNumber,topLeft,      1,0,2,1,0,2 );
                quad2 = checkStrategicPositionsForSideDiags
                        (myTurnNumber,topRight,     1,0,2,1,0,2);
                quad3 = checkStrategicPositionsForSideDiags
                        (myTurnNumber,bottomLeft,   1,0,2,1,0,2);
                quad4 = checkStrategicPositionsForSideDiags
                        (myTurnNumber,bottomRight,  1,0,2,1,0,2);
                break;
            case MLR:
                /* X|_|_
                 * _|X|_
                 * _|_|X
                 * */
                quad1 = checkStrategicPositionsForMiddleDiag
                        (myTurnNumber,topLeft,      0,0,1,1,2,2);
                quad2 = checkStrategicPositionsForMiddleDiag
                        (myTurnNumber,topRight,     0,0,1,1,2,2);
                quad3 = checkStrategicPositionsForMiddleDiag
                        (myTurnNumber,bottomLeft,   0,0,1,1,2,2);
                quad4 = checkStrategicPositionsForMiddleDiag
                        (myTurnNumber,bottomRight,  0,0,1,1,2,2);
                break;
            case BLR:
                /* BOTTOM Left-To-Right Diag
                 * _|_|X
                 * X|_|_
                 * _|X|_
                 */
                quad1 = checkStrategicPositionsForSideDiags
                        (myTurnNumber,topLeft,      0,1,1,2,2,0);
                quad2 = checkStrategicPositionsForSideDiags
                        (myTurnNumber,topRight,     0,1,1,2,2,0);
                quad3 = checkStrategicPositionsForSideDiags
                        (myTurnNumber,bottomLeft,   0,1,1,2,2,0);
                quad4 = checkStrategicPositionsForSideDiags
                        (myTurnNumber,bottomRight,  0,1,1,2,2,0);
                break;
            case TRL:
                /* _|X|_
                 * X|_|_
                 * _|_|X
                 * */
                quad1 = checkStrategicPositionsForSideDiags
                        (myTurnNumber,topLeft,      1,0,0,1,2,2);
                quad2 = checkStrategicPositionsForSideDiags
                        (myTurnNumber,topRight,     1,0,0,1,2,2);
                quad3 = checkStrategicPositionsForSideDiags
                        (myTurnNumber,bottomLeft,   1,0,0,1,2,2);
                quad4 = checkStrategicPositionsForSideDiags
                        (myTurnNumber,bottomRight,  1,0,0,1,2,2);
                break;
            case MRL:
                /* _|_|X
                 * _|X|_
                 * X|_|_
                 * */
                quad1 = checkStrategicPositionsForMiddleDiag
                        (myTurnNumber,topLeft,      0,2,1,1,2,0);
                quad2 = checkStrategicPositionsForMiddleDiag
                        (myTurnNumber,topRight,     0,2,1,1,2,02);
                quad3 = checkStrategicPositionsForMiddleDiag
                        (myTurnNumber,bottomLeft,   0,2,1,1,2,0);
                quad4 = checkStrategicPositionsForMiddleDiag
                        (myTurnNumber,bottomRight,  0,2,1,1,2,0);
                break;
            case BRL:
                /* X|_|_
                 * _|_|X
                 * _|X|_
                 * */
                quad1 = checkStrategicPositionsForSideDiags
                        (myTurnNumber,topLeft,      2,1,1,2,0,0);
                quad2 = checkStrategicPositionsForSideDiags
                        (myTurnNumber,topRight,     2,1,1,2,0,0);
                quad3 = checkStrategicPositionsForSideDiags
                        (myTurnNumber,bottomLeft,   2,1,1,2,0,0);
                quad4 = checkStrategicPositionsForSideDiags
                        (myTurnNumber,bottomRight,  2,1,1,2,0,0);
                break;
            default:
                break;
        }

        return new boolean[][]{quad1,quad2,quad3,quad4};
    }

    // Check strategic cell for Middle Diags
    public static boolean[] checkStrategicPositionsForMiddleDiag(int myTurnNumber, Piece[][] quad,
                                                                 int diagCell1X, int diagCell1Y,
                                                                 int diagCell2X, int diagCell2Y,
                                                                 int diagCell3X, int diagCell3Y)
    {
        String myColor = MyTools.getMyPieceColor(myTurnNumber);
        String opponentColor = MyTools.getMyPieceColor(1-myTurnNumber);

        /*Need to store instances for multiple cases(assume I am w):
        * 0cells: _ _ _
        * 1cell: w__, _w_, __w, w_b, b_w, bw_, _wb
        * 2cells: ww_, w_w, _ww
        * 3cells: www
        * */
        // flag to see if all 3 slots are free
        boolean free =

                quad[diagCell1X][diagCell1Y].toString().equals(Piece.EMPTY)
                && quad[diagCell2X][diagCell2Y].toString().equals(Piece.EMPTY)
                && quad[diagCell3X][diagCell3Y].toString().equals(Piece.EMPTY);

        // flag to check if: w__ or __w
        boolean oneFullTwoEmptyLorR =

                quad[diagCell1X][diagCell1Y].toString().equals(myColor)
                && quad[diagCell2X][diagCell2Y].toString().equals(Piece.EMPTY)
                && quad[diagCell3X][diagCell3Y].toString().equals(Piece.EMPTY)

                || (quad[diagCell1X][diagCell1Y].toString().equals(Piece.EMPTY)
                && quad[diagCell2X][diagCell2Y].toString().equals(Piece.EMPTY)
                && quad[diagCell3X][diagCell3Y].toString().equals(myColor));

        // flag to check if: _w_
        boolean oneMiddleFullTwoEmpty =

                (quad[diagCell1X][diagCell1Y].toString().equals(Piece.EMPTY)
                && quad[diagCell2X][diagCell2Y].toString().equals(myColor)
                && quad[diagCell3X][diagCell3Y].toString().equals(Piece.EMPTY));

        // flag to check if: w_b or b_w
        boolean whiteAndBlackOnLorR =

                quad[diagCell1X][diagCell1Y].toString().equals(myColor)
                && quad[diagCell2X][diagCell2Y].toString().equals(Piece.EMPTY)
                && quad[diagCell3X][diagCell3Y].toString().equals(opponentColor)

                || (quad[diagCell1X][diagCell1Y].toString().equals(opponentColor)
                && quad[diagCell2X][diagCell2Y].toString().equals(Piece.EMPTY)
                && quad[diagCell3X][diagCell3Y].toString().equals(myColor));

        // flag to check if: bw_ or _wb
        boolean oneMiddleAndOpponentOnLorR =

                quad[diagCell1X][diagCell1Y].toString().equals(Piece.EMPTY)
                && quad[diagCell2X][diagCell2Y].toString().equals(myColor)
                && quad[diagCell3X][diagCell3Y].toString().equals(opponentColor)

                || (quad[diagCell1X][diagCell1Y].toString().equals(opponentColor)
                && quad[diagCell2X][diagCell2Y].toString().equals(myColor)
                && quad[diagCell3X][diagCell3Y].toString().equals(Piece.EMPTY));

        // flag to check if: ww_ or _ww
        boolean twoFullOneEmpty =

                (quad[diagCell1X][diagCell1Y].toString().equals(Piece.EMPTY)
                && quad[diagCell2X][diagCell2Y].toString().equals(myColor)
                && quad[diagCell3X][diagCell3Y].toString().equals(myColor))

                || (quad[diagCell1X][diagCell1Y].toString().equals(myColor)
                && quad[diagCell2X][diagCell2Y].toString().equals(myColor)
                && quad[diagCell3X][diagCell3Y].toString().equals(Piece.EMPTY));

        // flag to check if: w_w
        boolean twoFullMiddleEmpty =

                (quad[diagCell1X][diagCell1Y].toString().equals(myColor)
                && quad[diagCell2X][diagCell2Y].toString().equals(Piece.EMPTY)
                && quad[diagCell3X][diagCell3Y].toString().equals(myColor));


        // flag to check if: wwb or bww
        boolean twoFullOneOpponent =
                (quad[diagCell1X][diagCell1Y].toString().equals(Piece.EMPTY)
                && quad[diagCell2X][diagCell2Y].toString().equals(myColor)
                && quad[diagCell3X][diagCell3Y].toString().equals(myColor))

                || (quad[diagCell1X][diagCell1Y].toString().equals(myColor)
                && quad[diagCell2X][diagCell2Y].toString().equals(Piece.EMPTY)
                && quad[diagCell3X][diagCell3Y].toString().equals(myColor))

                || (quad[diagCell1X][diagCell1Y].toString().equals(myColor)
                && quad[diagCell2X][diagCell2Y].toString().equals(myColor)
                && quad[diagCell3X][diagCell3Y].toString().equals(Piece.EMPTY));


        // flag to check if: www
        boolean threeCells = quad[diagCell1X][diagCell1Y].toString().equals(myColor)
                && quad[diagCell2X][diagCell2Y].toString().equals(myColor)
                && quad[diagCell3X][diagCell3Y].toString().equals(myColor);

        return new boolean[]
                {free,
                oneFullTwoEmptyLorR,oneMiddleFullTwoEmpty,whiteAndBlackOnLorR,oneMiddleAndOpponentOnLorR,
                twoFullOneEmpty,twoFullMiddleEmpty,twoFullOneOpponent,
                threeCells};
    }

    // Check strategic cell for Side Diags
    public static boolean[] checkStrategicPositionsForSideDiags(int myTurnNumber,Piece[][] quad,
                                                                int diagCell1X,int diagCell1Y,
                                                                int diagCell2X,int diagCell2Y,
                                                                int cornerCellX, int cornerCellY)
    {
        String myColor = MyTools.getMyPieceColor(myTurnNumber);
        // flag to check if the upper diag in a quad has pieces on it
        boolean twoCellsDiag = quad[diagCell1X][diagCell1Y].toString().equals(myColor)
                && quad[diagCell2X][diagCell2Y].toString().equals(myColor);
        // flag to check if bottom left corner in quad has a piece on it
        boolean cornerCell = quad[cornerCellX][cornerCellY].toString().equals(myColor);
        // flag to check if two cell diag has 1/2 pieces missing and that it is Empty (so I can put my piece there next turn)
        boolean oneOfTwoCell =
                (quad[diagCell1X][diagCell1Y].toString().equals(myColor)
                        && quad[diagCell2X][diagCell2Y].toString().equals(Piece.EMPTY.toString()))
                        || ((quad[diagCell1X][diagCell1Y].toString().equals(Piece.EMPTY.toString()))
                        && quad[diagCell2X][diagCell2Y].toString().equals(myColor));
        return new boolean[]{twoCellsDiag, cornerCell,oneOfTwoCell};
    }


}
