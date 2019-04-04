package student_player;

import pentago_swap.PentagoBoardState;
import pentago_swap.PentagoBoardState.Piece;
import student_player.MyTools.WinningPositions;

import static student_player.MyTools.WinningPositions.*;

public class HorizontalsVerticals
{
//    public enum Line  //similar structure to enum in PentagoBoardState.Piece Enum
//    {
//        HT, HM, HB, VL, VM, VR;  //H = horiz, V = vert, T = Top, M = Middle, B = Bottom , L = Left, R = Right
//
//        public String toString() { return name(); }
//    }

    // Calculate total heuristic for horizontals and verticals
    public static int checkHorizontalsAndVerticals(PentagoBoardState pbs, int studentTurnNumber)
    {
        //Get the Quads
        Piece[][][] quads = MyTools.getQuads(pbs);

        Piece[][] topLeftQuad = quads[0];
        Piece[][] topRightQuad = quads[1];
        Piece[][] bottomLeftQuad = quads[2];
        Piece[][] bottomRightQuad = quads[3];

        int opponentTurnNumber = 1-studentTurnNumber;

        //Count how sets of good strategy positions pieces I have for each diag
        //HT, HM, HB, VL, VM, VR
        int studHT = calcCostPiecesHorizontalOrVertical(HT,studentTurnNumber,topLeftQuad,topRightQuad,bottomLeftQuad,bottomRightQuad);
        int studHM = calcCostPiecesHorizontalOrVertical(HM,studentTurnNumber,topLeftQuad,topRightQuad,bottomLeftQuad,bottomRightQuad);
        int studHB = calcCostPiecesHorizontalOrVertical(HB,studentTurnNumber,topLeftQuad,topRightQuad,bottomLeftQuad,bottomRightQuad);
        int studVL = calcCostPiecesHorizontalOrVertical(VL,studentTurnNumber,topLeftQuad,topRightQuad,bottomLeftQuad,bottomRightQuad);
        int studVM = calcCostPiecesHorizontalOrVertical(VM,studentTurnNumber,topLeftQuad,topRightQuad,bottomLeftQuad,bottomRightQuad);
        int studVR = calcCostPiecesHorizontalOrVertical(VR,studentTurnNumber,topLeftQuad,topRightQuad,bottomLeftQuad,bottomRightQuad);

        //Count how sets of good strategy positions pieces Opponent have for each diag
        int oppHT = calcCostPiecesHorizontalOrVertical(HT,opponentTurnNumber,topLeftQuad,topRightQuad,bottomLeftQuad,bottomRightQuad);
        int oppHM = calcCostPiecesHorizontalOrVertical(HM,opponentTurnNumber,topLeftQuad,topRightQuad,bottomLeftQuad,bottomRightQuad);
        int oppHB = calcCostPiecesHorizontalOrVertical(HB,opponentTurnNumber,topLeftQuad,topRightQuad,bottomLeftQuad,bottomRightQuad);
        int oppVL = calcCostPiecesHorizontalOrVertical(VL,opponentTurnNumber,topLeftQuad,topRightQuad,bottomLeftQuad,bottomRightQuad);
        int oppVM = calcCostPiecesHorizontalOrVertical(VM,opponentTurnNumber,topLeftQuad,topRightQuad,bottomLeftQuad,bottomRightQuad);
        int oppVR = calcCostPiecesHorizontalOrVertical(VR,opponentTurnNumber,topLeftQuad,topRightQuad,bottomLeftQuad,bottomRightQuad);


        int heuristicInStudFavor = studHT+studHM+studHB+studVL+studVM+studVR;
        int heuristicInOppFavor = oppHT+oppHM+oppHB+oppVL+oppVM+oppVR;

        // Increase the thread worth slightly if you play as Black
//        if(studentTurnNumber == 1)
//            return heuristicInStudFavor - heuristicInOppFavor;
//        else
//            return heuristicInStudFavor - (int) Math.ceil(COST_MULTIPLIYER_WHEN_BLACK*heuristicInOppFavor);

        return heuristicInStudFavor - heuristicInOppFavor;

    }

    // Calculate cost on Horizontals and Verticals
    public static int calcCostPiecesHorizontalOrVertical(WinningPositions type, int myTurnNumber,
                                                         Piece[][] topLeft, Piece[][] topRight,
                                                         Piece[][] bottomLeft, Piece[][] bottomRight)
    {
        return  Heuristic.calcCostPiecesMiddleDiagsHorizontalsVerticals(type, myTurnNumber,
                topLeft,topRight,bottomLeft,bottomRight);
    }


    /* Check if pieces occupy strategic positions to form horizontal and vertical
     * Check every quad to test the right cells
     * Do this for every h and v (6 in total):
     *
     * Check if any of the following cells are occupied, they can form the corresponding line:
     * The cells are shown in the switch statements
     */

    public static boolean[][] checkStrategicCellsForSpecificHV(WinningPositions type, int myTurnNumber,
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
            case HT:
                /* Horizontal TOP
                 * X|X|X
                 * _|_|_
                 * _|_|_
                 */
                quad1 = checkStrategicCellForSpecificQuad(myTurnNumber,topLeft,     0,0,1,0,2,0);
                quad2 = checkStrategicCellForSpecificQuad(myTurnNumber,topRight,    0,0,1,0,2,0);
                quad3 = checkStrategicCellForSpecificQuad(myTurnNumber,bottomLeft,  0,0,1,0,2,0);
                quad4 = checkStrategicCellForSpecificQuad(myTurnNumber,bottomRight, 0,0,1,0,2,0);
                break;
            case HM:
                /* Horizontal MIDDLE
                 * _|_|_
                 * X|X|X
                 * _|_|_
                 * */
                quad1 = checkStrategicCellForSpecificQuad(myTurnNumber,topLeft,     0,1,1,1,2,1);
                quad2 = checkStrategicCellForSpecificQuad(myTurnNumber,topRight,    0,1,1,1,2,1);
                quad3 = checkStrategicCellForSpecificQuad(myTurnNumber,bottomLeft,  0,1,1,1,2,1);
                quad4 = checkStrategicCellForSpecificQuad(myTurnNumber,bottomRight, 0,1,1,1,2,1);
                break;
            case HB:
                /* Horizontal BOTTOM
                 * _|_|_
                 * _|_|_
                 * X|X|X
                 */
                quad1 = checkStrategicCellForSpecificQuad(myTurnNumber,topLeft,     0,2,1,2,2,2);
                quad2 = checkStrategicCellForSpecificQuad(myTurnNumber,topRight,    0,2,1,2,2,2);
                quad3 = checkStrategicCellForSpecificQuad(myTurnNumber,bottomLeft,  0,2,1,2,2,2);
                quad4 = checkStrategicCellForSpecificQuad(myTurnNumber,bottomRight, 0,2,1,2,2,2);
                break;
            case VL:
                /* Vertical LEFT
                 * X|_|_
                 * X|_|_
                 * X|_|_
                 * */
                quad1 = checkStrategicCellForSpecificQuad(myTurnNumber,topLeft,     0,0,0,1,0,2);
                quad2 = checkStrategicCellForSpecificQuad(myTurnNumber,topRight,    0,0,0,1,0,2);
                quad3 = checkStrategicCellForSpecificQuad(myTurnNumber,bottomLeft,  0,0,0,1,0,2);
                quad4 = checkStrategicCellForSpecificQuad(myTurnNumber,bottomRight, 0,0,0,1,0,2);
                break;
            case VM:
                /* Vertical MIDDLE
                 * _|X|_
                 * _|X|_
                 * _|X|_
                 * */
                quad1 = checkStrategicCellForSpecificQuad(myTurnNumber,topLeft,     1,0,1,1,1,2);
                quad2 = checkStrategicCellForSpecificQuad(myTurnNumber,topRight,    1,0,1,1,1,2);
                quad3 = checkStrategicCellForSpecificQuad(myTurnNumber,bottomLeft,  1,0,1,1,1,2);
                quad4 = checkStrategicCellForSpecificQuad(myTurnNumber,bottomRight, 1,0,1,1,1,2);
                break;
            case VR:
                /* Vertical Right
                 * _|_|X
                 * _|_|X
                 * _|_|X
                 * */
                quad1 = checkStrategicCellForSpecificQuad(myTurnNumber,topLeft,     2,0,2,1,2,2);
                quad2 = checkStrategicCellForSpecificQuad(myTurnNumber,topRight,    2,0,2,1,2,2);
                quad3 = checkStrategicCellForSpecificQuad(myTurnNumber,bottomLeft,  2,0,2,1,2,2);
                quad4 = checkStrategicCellForSpecificQuad(myTurnNumber,bottomRight, 2,0,2,1,2,2);
                break;
            default:
                break;
        }

        return new boolean[][]{quad1,quad2,quad3,quad4};

    }


    /* Check strategic cell for Horizontals and Verticals (both are symmetric)
     * Logic is exactly the same as for Middle Diagonals
     */
    public static boolean[] checkStrategicCellForSpecificQuad(int myTurnNumber, Piece[][] quad,
                                                                              int cell1X, int cell1Y,
                                                                              int cell2X, int cell2Y,
                                                                              int cell3X, int cell3Y)
    {
        return Diagonals.checkStrategicPositionsForMiddleDiag(myTurnNumber,quad,
                cell1X, cell1Y, cell2X, cell2Y, cell3X, cell3Y);
    }
}
