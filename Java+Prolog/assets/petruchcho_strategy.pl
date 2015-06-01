:- dynamic(a/2).

a([1,1], ' '). 
a([1,2], ' '). 
a([1,3], ' '). 
a([2,1], ' '). 
a([2,2], ' '). 
a([2,3], ' '). 
a([3,1], ' '). 
a([3,2], ' '). 
a([3,3], ' '). 

switchCell('X', 'O').
switchCell('O', 'X').

far(1, 3).
far(2, 1).
far(2, 3).
far(3, 1).
far(1, 1).
far(3, 3).
far(1, 2).
far(3, 2).

move(Who, LastX, LastY, MoveNumber, -1, -1, 'Lose') :- isLose(Who), !.
move(Who, LastX, LastY, MoveNumber, -1, -1, 'Win') :- isWin(Who), !.
move(Who, LastX, LastY, MoveNumber, X, Y, 'Win this move') :- canWin(Who, X, Y), !.
move(Who, LastX, LastY, MoveNumber, X, Y, 'Continue') :- canLose(Who, X, Y), !.
move(Who, LastX, LastY, MoveNumber, X, Y, 'Continue') :- moveX(LastX, LastY, X, Y), !.
move(Who, LastX, LastY, MoveNumber, X, Y, 'Draw').

moveX(LastOX, LastOY, 2, 2) :- a([2, 2], ' ').
moveX(LastOX, LastOY, X, Y) :- far(LastOX, X), far(LastOY, Y), a([X, Y], ' ').
moveX(LastOX, LastOY, X, Y) :- a([X, Y], ' ').

valid(X, Y) :- X > 0, Y > 0, X < 4, Y < 4.

isWin(Who) :- a([X, Y], Who), stepTo(X, Y, X1, Y1, X2, Y2), valid(X1, Y1), valid(X2, Y2), a([X1, Y1], Who), a([X2, Y2], Who).
isLose(Who) :- switchCell(Who, Enemy), a([X, Y], Enemy), stepTo(X, Y, X1, Y1, X2, Y2), valid(X1, Y1), valid(X2, Y2), a([X1, Y1], Enemy), a([X2, Y2], Enemy).

stepTo(FromX, FromY, X1, Y1, X2, Y2) :- X1 is FromX - 1, X2 is FromX + 1, Y1 is FromY - 1, Y2 is FromY + 1.
stepTo(FromX, FromY, X1, FromY, X2, FromY) :- X1 is FromX + 1, X2 is FromX - 1.
stepTo(FromX, FromY, X1, Y1, X2, Y2) :- X1 is FromX - 1, X2 is FromX + 1, Y1 is FromY + 1, Y2 is FromY - 1.
stepTo(FromX, FromY, FromX, Y1, FromX, Y2) :- Y1 is FromY - 1, Y2 is FromY + 1.
stepTo(FromX, FromY, FromX, Y1, FromX, Y2) :- Y1 is FromY - 1, Y2 is FromY - 2.
stepTo(FromX, FromY, FromX, Y1, FromX, Y2) :- Y1 is FromY + 1, Y2 is FromY + 2.
stepTo(FromX, FromY, X1, FromY, X2, FromY) :- X1 is FromX + 1, X2 is FromX + 2.
stepTo(FromX, FromY, X1, FromY, X2, FromY) :- X1 is FromX - 1, X2 is FromX - 2.
stepTo(FromX, FromY, X1, Y1, X2, Y2) :- X1 is FromX - 1, X2 is FromX - 2, Y1 is FromY + 1, Y2 is FromY + 2.
stepTo(FromX, FromY, X1, Y1, X2, Y2) :- X1 is FromX - 1, X2 is FromX - 2, Y1 is FromY - 1, Y2 is FromY - 2.
stepTo(FromX, FromY, X1, Y1, X2, Y2) :- X1 is FromX + 1, X2 is FromX + 2, Y1 is FromY + 1, Y2 is FromY + 2.
stepTo(FromX, FromY, X1, Y1, X2, Y2) :- X1 is FromX + 1, X2 is FromX + 2, Y1 is FromY - 1, Y2 is FromY - 2.

canLose(Who, X, Y) :- switchCell(Who, Enemy), a([X, Y], ' '), stepTo(X, Y, X1, Y1, X2, Y2), valid(X1, Y1), valid(X2, Y2), a([X1, Y1], Enemy), a([X2, Y2], Enemy).
canWin(Who, X, Y) :- a([X, Y], ' '), stepTo(X, Y, X1, Y1, X2, Y2), valid(X1, Y1), valid(X2, Y2), a([X1, Y1], Who), a([X2, Y2], Who).