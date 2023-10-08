package com.example.tictactoe

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.delay
import kotlin.random.Random

class GameViewModel : ViewModel() {
    var state by mutableStateOf(GameState())
    var winCell = 0
    val boardItems: MutableMap<Int, BoardCellValue> = mutableMapOf(
        1 to BoardCellValue.NONE,
        2 to BoardCellValue.NONE,
        3 to BoardCellValue.NONE,
        4 to BoardCellValue.NONE,
        5 to BoardCellValue.NONE,
        6 to BoardCellValue.NONE,
        7 to BoardCellValue.NONE,
        8 to BoardCellValue.NONE,
        9 to BoardCellValue.NONE,
    )

    fun onAction(action: UserAction) {
        when (action) {
            is UserAction.BoardTapped -> {
                addValueToBoard(action.cellNo)
            }

            UserAction.PlayAgainButtonClicked -> {
                gameReset()
            }
        }
    }
    private var beginTurn = state.beginTurn
    private fun gameReset() {
        boardItems.forEach { (i, _) ->
            boardItems[i] = BoardCellValue.NONE
        }
        beginTurn = if (beginTurn == BoardCellValue.CIRCLE){
            BoardCellValue.CROSS
        }
        else{
            BoardCellValue.CIRCLE
        }
        changeBeginTurn()

    }
    private fun changeBeginTurn(){
        if(beginTurn == BoardCellValue.CIRCLE){
            state = state.copy(
                hintText = "Player 'O' turn",
                currentTurn = beginTurn,
                victoryType = VictoryType.NONE,
                hasWon = false

            )
        } else{
            state = state.copy(
                hintText = "Computer turn",
                currentTurn = beginTurn,
                victoryType = VictoryType.NONE,
                hasWon = false
            )
            computerMove() //Perform computerMove() after change turn to CROSS
        }
    }

    private fun addValueToBoard(cellNo: Int) {
        //Check cell at cellNo empty?
        if (boardItems[cellNo] != BoardCellValue.NONE) {
            return
        }
        //Player O turn performs by click on cell
        if (state.currentTurn == BoardCellValue.CIRCLE) {
            boardItems[cellNo] = BoardCellValue.CIRCLE
            state = if (checkForVictory(BoardCellValue.CIRCLE)) {
                state.copy(
                    hintText = "Player 'O' Won",
                    playerCircleCount = state.playerCircleCount + 1,
                    currentTurn = BoardCellValue.NONE,
                    hasWon = true
                )
            } else if (hasBoardFull()) {
                state.copy(
                    hintText = "Game Draw",
                    drawCount = state.drawCount + 1
                )
            } else {
                state.copy(
                    hintText = "Computer turn",
                    currentTurn = BoardCellValue.CROSS
                )

            }
        //Player X turn //Perform by computerMove()
        } else if (state.currentTurn == BoardCellValue.CROSS) {
            boardItems[cellNo] = BoardCellValue.CROSS
            state = if (checkForVictory(BoardCellValue.CROSS)) {
                state.copy(
                    hintText = "Computer Won",
                    playerCrossCount = state.playerCrossCount + 1,
                    currentTurn = BoardCellValue.NONE,
                    hasWon = true
                )
            } else if (hasBoardFull()) {
                state.copy(
                    hintText = "Game Draw",
                    drawCount = state.drawCount + 1
                )
            } else {
                state.copy(
                    hintText = "Player 'O' turn",
                    currentTurn = BoardCellValue.CIRCLE
                )
            }
        }
        //Computer will action only when X turn
        if(!hasBoardFull() && state.currentTurn == BoardCellValue.CROSS){
            computerMove()
        }
    }
    private fun canWin(boardValue: BoardCellValue):Boolean{
        //target winCell
        //[1] >> [2][3], [5][9], [4][7]
        //[2] >> [1][3], [5][8]
        //[3] >> [1][2],[5][7], [6][9]
        //[4] >> [1][7], [5][6]
        //[5] >> [2][8], [4][6], [1],[9], [3][7]
        //[6] >> [3][9], [4][5]
        //[7] >> [1][4], [3][5], [8][9]
        //[8] >> [7][9], [2][5]
        //[9] >> [7][8], [1][5], [3][6]
        when {
            ((boardItems[2] == boardValue && boardItems[3] == boardValue)||
                    (boardItems[5] == boardValue && boardItems[9] == boardValue)||
                        (boardItems[4] == boardValue && boardItems[7] == boardValue)) && boardItems[1] == BoardCellValue.NONE -> {
                winCell = 1
                return true
            }
            ((boardItems[1] == boardValue && boardItems[3] == boardValue)||
                (boardItems[5] == boardValue && boardItems[8] == boardValue)) && boardItems[2] == BoardCellValue.NONE -> {
                winCell = 2
                return true
            }
            ((boardItems[1] == boardValue && boardItems[2] == boardValue)||
                    (boardItems[5] == boardValue && boardItems[7] == boardValue)||
                    (boardItems[6] == boardValue && boardItems[9] == boardValue)) && boardItems[3] == BoardCellValue.NONE -> {
                winCell = 3
                return true
            }
            ((boardItems[1] == boardValue && boardItems[7] == boardValue)||
                    (boardItems[5] == boardValue && boardItems[6] == boardValue)) && boardItems[4] == BoardCellValue.NONE -> {
                winCell = 4
                return true
            }
            ((boardItems[2] == boardValue && boardItems[8] == boardValue)||
                    (boardItems[4] == boardValue && boardItems[6] == boardValue)||
                    (boardItems[1] == boardValue && boardItems[9] == boardValue)||
                    (boardItems[3] == boardValue && boardItems[7] == boardValue)) && boardItems[5] == BoardCellValue.NONE -> {
                winCell = 5
                return true
            }
            ((boardItems[3] == boardValue && boardItems[9] == boardValue)||
                    (boardItems[4] == boardValue && boardItems[5] == boardValue)) && boardItems[6] == BoardCellValue.NONE -> {
                winCell = 6
                return true
            }
            ((boardItems[1] == boardValue && boardItems[4] == boardValue)||
                    (boardItems[3] == boardValue && boardItems[5] == boardValue)||
                    (boardItems[8] == boardValue && boardItems[9] == boardValue)) && boardItems[7] == BoardCellValue.NONE -> {
                winCell = 7
                return true
            }
            ((boardItems[7] == boardValue && boardItems[9] == boardValue)||
                    (boardItems[2] == boardValue && boardItems[5] == boardValue)) && boardItems[8] == BoardCellValue.NONE -> {
                winCell = 8
                return true
            }
            ((boardItems[1] == boardValue && boardItems[5] == boardValue)||
                    (boardItems[3] == boardValue && boardItems[6] == boardValue)||
                    (boardItems[7] == boardValue && boardItems[8] == boardValue)) && boardItems[9] == BoardCellValue.NONE -> {
                winCell = 9
                return true
            }
            else -> return false
        }
    }
    private fun canBlock():Boolean{
        //Computer's turn so we call canWin() with CIRCLE to check where to block them
        if(canWin(BoardCellValue.CIRCLE)){
            return true
        }
        return false
    }
    private fun middleFree():Boolean{
        return boardItems[5] == BoardCellValue.NONE
    }
    private fun computerMove(){
        if (canWin(BoardCellValue.CROSS)){
            //after canWin() return true it also located winCell
            addValueToBoard(winCell)
        }
        else if(canBlock()){
            addValueToBoard(winCell)
        }
        else if(middleFree()){
            addValueToBoard(5) //Computer put X in 5th cell
        }
        else{
            //computer random putting X at a empty cell
            var randomCell = 0
            while(true){
                randomCell = Random.nextInt(1,10)
                if(boardItems[randomCell]==BoardCellValue.NONE){
                    addValueToBoard(randomCell)
                    break
                }
            }

        }
    }

    private fun checkForVictory(boardValue: BoardCellValue): Boolean {
        when {
            boardItems[1] == boardValue && boardItems[2] == boardValue && boardItems[3] == boardValue -> {
                state = state.copy(victoryType = VictoryType.HORIZONTAL1)
                return true
            }

            boardItems[4] == boardValue && boardItems[5] == boardValue && boardItems[6] == boardValue -> {
                state = state.copy(victoryType = VictoryType.HORIZONTAL2)
                return true
            }

            boardItems[7] == boardValue && boardItems[8] == boardValue && boardItems[9] == boardValue -> {
                state = state.copy(victoryType = VictoryType.HORIZONTAL3)
                return true
            }

            boardItems[1] == boardValue && boardItems[4] == boardValue && boardItems[7] == boardValue -> {
                state = state.copy(victoryType = VictoryType.VERTICAL1)
                return true
            }

            boardItems[2] == boardValue && boardItems[5] == boardValue && boardItems[8] == boardValue -> {
                state = state.copy(victoryType = VictoryType.VERTICAL2)
                return true
            }

            boardItems[3] == boardValue && boardItems[6] == boardValue && boardItems[9] == boardValue -> {
                state = state.copy(victoryType = VictoryType.VERTICAL3)
                return true
            }

            boardItems[1] == boardValue && boardItems[5] == boardValue && boardItems[9] == boardValue -> {
                state = state.copy(victoryType = VictoryType.DIAGONAL1)
                return true
            }

            boardItems[3] == boardValue && boardItems[5] == boardValue && boardItems[7] == boardValue -> {
                state = state.copy(victoryType = VictoryType.DIAGONAL2)
                return true
            }

            else -> return false
        }
    }

    public fun hasBoardFull(): Boolean {
        return !boardItems.containsValue(BoardCellValue.NONE)
    }
}