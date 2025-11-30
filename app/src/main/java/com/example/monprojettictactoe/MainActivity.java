package com.example.monprojettictactoe;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    // Éléments d'interface utilisateur (UI)
    private TextView tvPartieInfo, tvScoreX, tvScoreO, tvScoreNulles;
    private Button[] buttons = new Button[9]; // Tableau pour gérer les 9 boutons de la grille

    // État du jeu et du tournoi
    private String playerSymbol;
    private int maxGames;
    private int currentGame = 1;
    private String currentPlayer = "X";
    private boolean gameActive = true;

    // Scores
    private int scoreX = 0;
    private int scoreO = 0;
    private int scoreDraws = 0;

    // État du plateau: 0 = vide, 1 = X, 2 = O
    private int[] gameState = {0, 0, 0, 0, 0, 0, 0, 0, 0};

    // Combinaisons gagnantes (indices dans le tableau gameState)
    private int[][] winPositions = {
            {0, 1, 2}, {3, 4, 5}, {6, 7, 8}, // Lignes
            {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, // Colonnes
            {0, 4, 8}, {2, 4, 6}             // Diagonales
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Récupération des données de l'AccueilActivity
        Intent intent = getIntent();
        playerSymbol = intent.getStringExtra("PLAYER_SYMBOL");
        maxGames = intent.getIntExtra("MAX_GAMES", 5);

        // 2. Initialisation des composants UI (TextViews)
        tvPartieInfo = findViewById(R.id.tv_partie_info);
        tvScoreX = findViewById(R.id.tv_score_x);
        tvScoreO = findViewById(R.id.tv_score_o);
        tvScoreNulles = findViewById(R.id.tv_score_nulles);

        // 3. Initialisation des 9 boutons de la grille
        for (int i = 0; i < 9; i++) {
            String buttonID = "btn" + i;
            // Récupère l'ID de la ressource (R.id.btn0, R.id.btn1, etc.) dynamiquement
            int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
            buttons[i] = findViewById(resID);
        }

        // Mise à jour initiale de l'affichage
        updateUI();
    }

    // Méthode appelée lors du clic sur une case (définie dans XML via android:onClick="onGridClick")
    public void onGridClick(View view) {
        if (!gameActive) return; // Ignore si la partie est déjà terminée

        Button clickedBtn = (Button) view;
        int tappedTag = Integer.parseInt(clickedBtn.getTag().toString()); // Récupère le tag (0 à 8)

        if (gameState[tappedTag] != 0) {
            return; // Case déjà prise, on ne fait rien
        }

        // 1. Jouer le coup
        gameState[tappedTag] = currentPlayer.equals("X") ? 1 : 2;
        clickedBtn.setText(currentPlayer);

        // 2. Vérifier le résultat
        checkWinner();
    }

    /**
     * Vérifie si un joueur a gagné ou si c'est un match nul.
     */
    private void checkWinner() {
        boolean hasWinner = false;

        // 1. Vérifier les combinaisons gagnantes
        for (int[] winPos : winPositions) {
            if (gameState[winPos[0]] == gameState[winPos[1]] &&
                    gameState[winPos[1]] == gameState[winPos[2]] &&
                    gameState[winPos[0]] != 0) {
                hasWinner = true;
                break;
            }
        }

        if (hasWinner) {
            gameActive = false; // Arrêter la partie
            // Mise à jour du score
            if (currentPlayer.equals("X")) scoreX++; else scoreO++;
            showRoundResult("Victoire du joueur " + currentPlayer + " !");
        } else {
            // 2. Vérifier match nul
            boolean isDraw = true;
            for (int state : gameState) {
                if (state == 0) isDraw = false; // Si une case est vide, ce n'est pas un match nul
            }

            if (isDraw) {
                gameActive = false;
                scoreDraws++;
                showRoundResult("Partie Nulle !");
            } else {
                // 3. Changer de joueur pour le prochain coup
                currentPlayer = currentPlayer.equals("X") ? "O" : "X";
            }
        }
    }

    /**
     * Affiche le résultat de la manche et prépare le passage à la suivante.
     */
    private void showRoundResult(String message) {
        updateUI(); // Met à jour les scores affichés

        if (currentGame < maxGames) {
            // Tournoi en cours : proposer de passer à la partie suivante
            new AlertDialog.Builder(this)
                    .setTitle("Fin de la Partie " + currentGame)
                    .setMessage(message + "\n\nCliquer sur Suivant pour commencer la partie " + (currentGame + 1) + ".")
                    .setCancelable(false)
                    .setPositiveButton("Suivant", (dialog, which) -> resetBoard())
                    .show();
        } else {
            // Fin du tournoi
            endTournament();
        }
    }

    /**
     * Réinitialise le plateau de jeu pour une nouvelle manche.
     */
    private void resetBoard() {
        currentGame++;
        gameActive = true;
        Arrays.fill(gameState, 0); // Remet l'état à vide
        currentPlayer = "X";       // Le joueur X recommence (convention)

        // Efface les symboles des boutons
        for (Button btn : buttons) {
            btn.setText("");
        }
        updateUI();
    }

    /**
     * Met à jour les TextViews des scores et de l'indicateur de partie.
     */
    private void updateUI() {
        tvPartieInfo.setText("Partie " + currentGame + " / " + maxGames);
        tvScoreX.setText("Score du joueur X: " + scoreX);
        tvScoreO.setText("Score du Joueur O: " + scoreO);
        tvScoreNulles.setText("Nombre de parties nulles: " + scoreDraws);
    }

    /**
     * Gère la fin du tournoi, détermine le vainqueur et propose la sauvegarde.
     */
    private void endTournament() {
        String tournoiWinner;
        if (scoreX > scoreO) tournoiWinner = "Joueur X";
        else if (scoreO > scoreX) tournoiWinner = "Joueur O";
        else tournoiWinner = "Égalité";

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("FIN DU TOURNOI");
        builder.setMessage("RÉSULTAT FINAL : " + tournoiWinner + "\n\nScore final X: " + scoreX + "\nScore final O: " + scoreO + "\nParties nulles: " + scoreDraws);
        builder.setCancelable(false); // Force l'utilisateur à choisir une action

        // Bouton 1: Sauvegarder et Revenir
        builder.setPositiveButton("Sauvegarder le tournoi", (dialog, which) -> {
            saveTournamentData(tournoiWinner);
            finish(); // Ferme l'activité et revient à AccueilActivity
        });

        // Bouton 2: Revenir sans sauvegarder
        builder.setNegativeButton("Revenir à l'accueil", (dialog, which) -> {
            finish(); // Ferme l'activité et revient à AccueilActivity
        });

        builder.show();
    }

    /**
     * Sérialise et enregistre les résultats du tournoi dans un fichier interne.
     */
    private void saveTournamentData(String winner) {
        try {
            // Création de l'objet de résultat à sérialiser
            TournamentResult result = new TournamentResult(scoreX, scoreO, scoreDraws, maxGames, winner);

            // Ouvrir le fichier en écriture
            FileOutputStream fos = openFileOutput("last_tournament.ser", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            // Écriture de l'objet dans le fichier
            oos.writeObject(result);

            // Fermeture des flux
            oos.close();
            fos.close();

            Toast.makeText(this, "Tournoi sauvegardé avec succès !", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Erreur lors de la sauvegarde", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}