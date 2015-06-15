package com.petruchcho.javaprolog.field;

import com.petruchcho.javaprolog.strategy.XOAbstractStrategy;

public class TicTacToeHelper {

    public static boolean isDraw(Field field) {
        return isDraw(field.getField());
    }

    private static boolean isDraw(XOAbstractStrategy.Player[][] board) {
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 3; ++col) {
                if (board[row][col] == null) {
                    return false;  // an empty cell found, not draw, exit
                }
            }
        }
        return true;  // no empty cell, it's a draw
    }

    public static boolean hasWon(XOAbstractStrategy.Player player, Field field) {
        boolean hasWon = false;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                hasWon |= hasWon(player, i, j, field.getField());
            }
        }
        return hasWon;
    }

    /**
     * Return true if the player with "theSeed" has won after placing at
     * (currentRow, currentCol)
     */
    private static boolean hasWon(XOAbstractStrategy.Player theSeed, int currentRow, int currentCol, XOAbstractStrategy.Player[][] board) {
        return (board[currentRow][0] == theSeed         // 3-in-the-row
                && board[currentRow][1] == theSeed
                && board[currentRow][2] == theSeed
                || board[0][currentCol] == theSeed      // 3-in-the-column
                && board[1][currentCol] == theSeed
                && board[2][currentCol] == theSeed
                || currentRow == currentCol            // 3-in-the-diagonal
                && board[0][0] == theSeed
                && board[1][1] == theSeed
                && board[2][2] == theSeed
                || currentRow + currentCol == 2  // 3-in-the-opposite-diagonal
                && board[0][2] == theSeed
                && board[1][1] == theSeed
                && board[2][0] == theSeed);
    }

}
