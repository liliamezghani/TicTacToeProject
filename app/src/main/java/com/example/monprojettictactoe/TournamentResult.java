package com.example.monprojettictactoe;

import java.io.Serializable;

// Cette classe implémente Serializable pour être sauvegardée dans un fichier
public class TournamentResult implements Serializable {
    private int scoreX;
    private int scoreO;
    private int drawCount;
    private int totalGames;
    private String winner;

    public TournamentResult(int scoreX, int scoreO, int drawCount, int totalGames, String winner) {
        this.scoreX = scoreX;
        this.scoreO = scoreO;
        this.drawCount = drawCount;
        this.totalGames = totalGames;
        this.winner = winner;
    }

    // Getters pour récupérer les infos
    public int getScoreX() { return scoreX; }
    public int getScoreO() { return scoreO; }
    public int getDrawCount() { return drawCount; }
    public int getTotalGames() { return totalGames; }
    public String getWinner() { return winner; }
}