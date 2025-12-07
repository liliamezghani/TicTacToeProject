package com.example.monprojettictactoe;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    // Constante pour le nom du fichier de sauvegarde
    private static final String FILENAME = "tournament_result.ser";

    // Éléments UI
    private TextView tvPartieInfo;
    private TextView tvScoreX;
    private TextView tvScoreO;
    private TextView tvPartiesNulles;
    private TextView tvStatus;
    private Button[] buttons = new Button[9];
    private Button btnSauvegarderTournoi;
    private Button btnRetourAccueil;
    private Button btnSettings;

    // Logique de Jeu
    private String playerSymbol; // Symbole du joueur actuel ("X" ou "O")
    private String humanPlayer; // Symbole choisi par l'utilisateur ("X" ou "O")
    private String machinePlayer; // Symbole de l'autre joueur
    private int totalGames; // Nombre total de parties dans le tournoi
    private int currentGame = 1; // Partie en cours (commence à 1)
    private String[] board = new String[9]; // Représentation de la grille (état des 9 cases)

    // Scores du Tournoi
    private int scoreX = 0;
    private int scoreO = 0;
    private int draws = 0;
    private boolean gameActive = true; // Indique si la partie actuelle est en cours

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Récupération des données passées par AccueilActivity
        Intent intent = getIntent();
        humanPlayer = intent.getStringExtra(AccueilActivity.EXTRA_SYMBOLE);
        totalGames = intent.getIntExtra(AccueilActivity.EXTRA_NB_PARTIES, 5);

        // Détermination du symbole de l'adversaire
        machinePlayer = humanPlayer.equals("X") ? "O" : "X";
        playerSymbol = humanPlayer; // Le joueur qui commence est celui choisi par l'utilisateur

        // 1. Initialisation des éléments UI
        tvPartieInfo = findViewById(R.id.tv_partie_info);
        tvScoreX = findViewById(R.id.tv_score_x);
        tvScoreO = findViewById(R.id.tv_score_o);
        tvPartiesNulles = findViewById(R.id.tv_parties_nulles);
        tvStatus = findViewById(R.id.tv_status);
        btnSauvegarderTournoi = findViewById(R.id.btn_sauvegarder_tournoi);
        btnRetourAccueil = findViewById(R.id.btn_retour_accueil);
        btnSettings = findViewById(R.id.btn_settings);

        // Masquer les éléments de fin de tournoi au début
        btnSauvegarderTournoi.setVisibility(View.GONE);
        btnRetourAccueil.setVisibility(View.GONE);

        // 2. Initialisation des boutons de la grille
        GridLayout gridLayout = findViewById(R.id.grid_layout);
        for (int i = 0; i < 9; i++) {
            // Assurez-vous que les IDs des boutons sont btn0, btn1, ... btn8
            int buttonId = getResources().getIdentifier("btn" + i, "id", getPackageName());
            buttons[i] = findViewById(buttonId);
            buttons[i].setOnClickListener(this::onButtonClick);
            board[i] = ""; // Initialiser le tableau de jeu
        }

        // 3. Configuration des boutons de fin
        btnSauvegarderTournoi.setOnClickListener(v -> sauvegarderTournoi());
        btnRetourAccueil.setOnClickListener(v -> retournerAccueil());
        btnSettings.setOnClickListener(v -> Toast.makeText(this, "Paramètres non implémentés.", Toast.LENGTH_SHORT).show()); // Placeholder

        // 4. Démarrer la première partie et mettre à jour l'affichage
        updateDisplay();
        resetBoard();
    }

    /**
     * Gère les clics sur la grille.
     */
    private void onButtonClick(View view) {
        if (!gameActive) {
            Toast.makeText(this, "La partie est terminée. Passez à la suivante.", Toast.LENGTH_SHORT).show();
            return;
        }

        Button btn = (Button) view;
        int clickedPos = -1;

        // Trouver la position du bouton cliqué
        for (int i = 0; i < 9; i++) {
            if (buttons[i] == btn) {
                clickedPos = i;
                break;
            }
        }

        // Si la case est vide, jouer le coup
        if (board[clickedPos].isEmpty()) {
            board[clickedPos] = playerSymbol;
            btn.setText(playerSymbol);
            // Définir la couleur néon appropriée pour le symbole
            if (playerSymbol.equals("X")) {
                // Néon Rose (FF4DFF)
                btn.setTextColor(Color.parseColor("#FF4DFF"));
            } else {
                // Néon Bleu (00FFFF)
                btn.setTextColor(Color.parseColor("#00FFFF"));
            }

            // Vérifier si la partie est terminée
            if (checkForWin()) {
                endGame(playerSymbol); // Victoire
            } else if (isBoardFull()) {
                endGame("draw"); // Partie nulle
            } else {
                // Changer de joueur pour le prochain tour (X -> O ou O -> X)
                switchPlayer();
            }
        }
    }

    /**
     * Inverse le joueur actuel et met à jour le statut.
     */
    private void switchPlayer() {
        playerSymbol = playerSymbol.equals("X") ? "O" : "X";
        updateDisplay();
    }

    /**
     * Vérifie toutes les combinaisons gagnantes.
     */
    private boolean checkForWin() {
        // Définir les combinaisons gagnantes
        int[][] winPositions = {
                {0, 1, 2}, {3, 4, 5}, {6, 7, 8}, // Lignes
                {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, // Colonnes
                {0, 4, 8}, {2, 4, 6}             // Diagonales
        };

        for (int[] combo : winPositions) {
            String p1 = board[combo[0]];
            String p2 = board[combo[1]];
            String p3 = board[combo[2]];

            if (!p1.isEmpty() && p1.equals(p2) && p1.equals(p3)) {
                // Code pour dessiner la ligne gagnante (visuel, non implémenté ici,
                // mais le layout tv_winning_line est là pour ça si vous voulez l'ajouter plus tard)
                return true;
            }
        }
        return false;
    }

    /**
     * Vérifie si toutes les cases sont remplies.
     */
    private boolean isBoardFull() {
        for (String s : board) {
            if (s.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Gère la fin d'une partie (victoire ou nulle).
     */
    private void endGame(String winner) {
        gameActive = false;
        String statusMessage;

        if (winner.equals("draw")) {
            draws++;
            statusMessage = "Partie Nulle!";
        } else {
            if (winner.equals("X")) {
                scoreX++;
            } else {
                scoreO++;
            }
            statusMessage = "Victoire de " + winner + "!";
        }

        tvStatus.setText(statusMessage);
        tvStatus.setTextColor(Color.parseColor("#FFFFFF"));
        Toast.makeText(this, statusMessage, Toast.LENGTH_LONG).show();

        // Si ce n'est pas la dernière partie, passer à la suivante après un délai
        if (currentGame < totalGames) {
            currentGame++;
            // On peut ajouter un délai ici avec un Handler.postDelayed si on voulait un effet d'attente
            resetBoard(); // Passe immédiatement à la partie suivante pour la démo
        } else {
            // Fin du tournoi
            endTournament();
        }
        updateDisplay();
    }

    /**
     * Réinitialise la grille pour la partie suivante.
     */
    private void resetBoard() {
        Arrays.fill(board, "");
        for (Button btn : buttons) {
            btn.setText("");
            btn.setEnabled(true);
        }

        // Le joueur qui commence est toujours celui qui a perdu la partie précédente ou le joueur humain (si première partie)
        playerSymbol = (currentGame % 2 != 0) ? humanPlayer : machinePlayer;

        gameActive = true;
        updateDisplay();
    }

    /**
     * Met à jour tous les TextViews de l'interface.
     */
    private void updateDisplay() {
        // Mise à jour de l'information de la partie
        tvPartieInfo.setText(String.format("PARTIE %d / %d", currentGame, totalGames));

        // Mise à jour des scores
        tvScoreX.setText(String.format("Joueur X %d", scoreX));
        tvScoreO.setText(String.format("%d Joueur O", scoreO)); // Format adapté à l'image
        tvPartiesNulles.setText(String.format(" PARTIES NULLES: %d", draws));

        // Mise à jour du statut du tour
        if (gameActive) {
            tvStatus.setText(String.format("C'est le tour de %s", playerSymbol));
            tvStatus.setTextColor(Color.parseColor("#C77DFF")); // Couleur néon violet
        }
    }

    /**
     * Gère la fin complète du tournoi.
     */
    private void endTournament() {
        // 1. Déterminer le vainqueur du tournoi
        String tournamentWinner;
        String finalMessage;
        int color;

        if (scoreX > scoreO) {
            tournamentWinner = "Joueur X";
            finalMessage = "VICTOIRE DU JOUEUR X";
            color = Color.parseColor("#FF4DFF"); // Rose
        } else if (scoreO > scoreX) {
            tournamentWinner = "Joueur O";
            finalMessage = "VICTOIRE DU JOUEUR O";
            color = Color.parseColor("#00FFFF"); // Bleu
        } else {
            tournamentWinner = "Égalité";
            finalMessage = "ÉGALITÉ DU TOURNOI";
            color = Color.parseColor("#C77DFF"); // Violet
        }

        // 2. Afficher le résultat final
        tvStatus.setText(finalMessage);
        tvStatus.setTextColor(color);

        // 3. Masquer la grille et afficher les options de fin
        for (Button btn : buttons) {
            btn.setEnabled(false); // Désactiver la grille
        }

        // Rendre visibles les boutons de fin de tournoi
        btnSauvegarderTournoi.setVisibility(View.VISIBLE);
        btnRetourAccueil.setVisibility(View.VISIBLE);

        // Optionnel: Afficher un AlertDialog pour les résultats
        new AlertDialog.Builder(this)
                .setTitle("Tournoi Terminé")
                .setMessage(finalMessage + "\n\nScores: X=" + scoreX + ", O=" + scoreO + ", Nulles=" + draws)
                .setPositiveButton("OK", null)
                .show();
    }

    /**
     * Sauvegarde l'objet TournamentResult dans le fichier interne par sérialisation.
     */
    private void sauvegarderTournoi() {
        // 1. Déterminer le vainqueur
        String winner;
        if (scoreX > scoreO) {
            winner = "Joueur X";
        } else if (scoreO > scoreX) {
            winner = "Joueur O";
        } else {
            winner = "Égalité";
        }

        // 2. Créer l'objet résultat
        TournamentResult result = new TournamentResult(scoreX, scoreO, draws, totalGames, winner);

        // 3. Sauvegarder
        try (FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {

            oos.writeObject(result);
            Toast.makeText(this, "Tournoi sauvegardé avec succès !", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Toast.makeText(this, "Erreur lors de la sauvegarde du tournoi.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    /**
     * Retourne à l'activité d'accueil.
     */
    private void retournerAccueil() {
        Intent intent = new Intent(MainActivity.this, AccueilActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}