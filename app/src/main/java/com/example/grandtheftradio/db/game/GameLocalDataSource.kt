package com.example.grandtheftradio.db.game

import com.example.grandtheftradio.db.fromDomain
import com.example.grandtheftradio.db.toDomain
import com.example.grandtheftradio.entities.Game

class GameLocalDataSource(private val gameDao: GameDao) {
    fun addGame(game: Game){
        gameDao.addGame(game.fromDomain())
    }

    fun getAllGames() : List<Game> {
        val gamesLocal = gameDao.getGames()
        val gamesDomain = mutableListOf<Game>()
        gamesLocal.forEach {
            val game = getGame(it.gameName)
           gamesDomain.add(game)
        }
        return gamesDomain
    }

    fun getGame(name: String) : Game {
        val game = gameDao.getGame(name)
        return game.toDomain()
    }
}