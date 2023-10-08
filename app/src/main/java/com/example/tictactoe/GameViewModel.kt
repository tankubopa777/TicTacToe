package com.example.tictactoe

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.delay
import kotlin.random.Random

class GameViewModel : ViewModel() {
    var state by mutableStateOf(GameState())
    
    // use targetCell to store the cell number that computer will move
    var targetCell = 0

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
                // if game is not over, do nothing
                if (!state.hasWon && !hasBoardFull()) {
                    return
                } else {
                // if game is over, reset the game
                    gameReset()
                }
                
            }
        }
    }
    private var startRound = state.startRound
    private fun gameReset() {
        boardItems.forEach { (i, _) ->
            boardItems[i] = BoardCellValue.NONE
        }
        startRound = if (startRound == BoardCellValue.CIRCLE){
            BoardCellValue.CROSS
        }
        else{
            BoardCellValue.CIRCLE
        }
        changeStartRound()

    }
    // Change turn
    private fun changeStartRound(){
        if(startRound == BoardCellValue.CIRCLE){
            state = state.copy(
                hintText = "Player 'O' turn",
                currentTurn = startRound,
                victoryType = VictoryType.NONE,
                hasWon = false
            )
        } else{
            state = state.copy(
                hintText = "Computer 'X' turn",
                currentTurn = startRound,
                victoryType = VictoryType.NONE,
                hasWon = false
            )
            computerX()
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
            computerX()
        }
    }
    private fun canWin(boardValue: BoardCellValue):Boolean{
        //Check if computer can win
        val winPatterns = mapOf(
        1 to listOf(listOf(2, 3), listOf(5, 9), listOf(4, 7)),
        2 to listOf(listOf(1, 3), listOf(5, 8)),
        3 to listOf(listOf(1, 2), listOf(5, 7), listOf(6, 9)),
        4 to listOf(listOf(1, 7), listOf(5, 6)),
        5 to listOf(listOf(2, 8), listOf(4, 6), listOf(1, 9), listOf(3, 7)),
        6 to listOf(listOf(3, 9), listOf(4, 5)),
        7 to listOf(listOf(1, 4), listOf(3, 5), listOf(8, 9)),
        8 to listOf(listOf(7, 9), listOf(2, 5)),
        9 to listOf(listOf(1, 5), listOf(3, 6), listOf(7, 8))
        )
        // loop through winPatterns to check if computer can win
        for ((cell, patterns) in winPatterns) {
            for (pattern in patterns) {
                if (pattern.all { boardItems[it] == boardValue } && boardItems[cell] == BoardCellValue.NONE) {
                    targetCell = cell
                    return true
                }
            }
        }

return false

    }
    private fun canBlock():Boolean{
        //Check if computer can block
        if(canWin(BoardCellValue.CIRCLE)){
            return true
        }
        return false
    }

    // Check if middle is free
    private fun middleFree():Boolean{
        return boardItems[5] == BoardCellValue.NONE
    }

    private fun computerX(){
        // use canWin() to check if computer can win
        if (canWin(BoardCellValue.CROSS)){
            addValueToBoard(targetCell)
        }
        // use canBlock() to check if computer can block user
        else if(canBlock()){
            addValueToBoard(targetCell)
        }
        // if middle is free then put X at middle
        else if(middleFree()){
            addValueToBoard(5) 
        }
        else{
            // if no one can win then put X at random cell
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