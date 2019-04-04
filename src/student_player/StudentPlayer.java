package student_player;

import boardgame.Move;

import pentago_swap.PentagoPlayer;
import pentago_swap.PentagoBoardState;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/** A player file submitted by a student. */
public class StudentPlayer extends PentagoPlayer {

    /**
     * You must modify this constructor to return your student number. This is
     * important, because this is what the code that runs the competition uses to
     * associate you with your agent. The constructor should do nothing else.
     */
    public StudentPlayer() {
        super("260707258");
    }

    /**
     * This is the primary method that you need to implement. The ``boardState``
     * object contains the current state of the game, which your agent must use to
     * make decisions.
     */
    public Move chooseMove(PentagoBoardState boardState)
    {
        Move myMove;
        PentagoBoardState pbs = MyTools.copyCurrentBoardState(boardState);

        int myTurnNumber = pbs.getTurnPlayer();
        MyTools.DEPTH = 2;  //Reset depth to default

        // 1st & 2nd turn moves strategy
        if (MyTools.getGameTurnNumber(pbs) < 3)
            myMove = MoveSelection.calculateBestMove1stOr2ndTurn(myTurnNumber,pbs);

        // 3rd move as White piece strategy
        else if (myTurnNumber == 0 && MyTools.getGameTurnNumber(pbs) == 3)
            myMove = MoveSelection.calculateBestMove3rdTurnWhite(myTurnNumber,pbs);

        else
        {
            // Increase depth after kth turn - less moves available = go deeper in tree
            if (MyTools.getGameTurnNumber(pbs) > MyTools.TURN_TO_INCREASE_DEPTH)
                MyTools.DEPTH = 3;

            myMove = MoveSelection.calculateBestMove(myTurnNumber, pbs);
        }


        // Return your move to be processed by the server.
        return myMove;
    }
}