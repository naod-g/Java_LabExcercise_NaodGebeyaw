import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.*;

public class Poker extends JFrame {

    static class Card {
        String suit;
        int rank;

        Card(String suit, int rank) {
            this.suit = suit;
            this.rank = rank;
        }

        public String toString() {
            String[] r = {"2","3","4","5","6","7","8","9","10","J","Q","K","A"};
            return r[rank] + " of " + suit;
        }
    }

    private JLabel playerLabel;
    private JLabel dealerLabel;
    private JLabel resultLabel;

    private List<Card> deck;

    public Poker() {
        setTitle("High Card Poker");
        setSize(600, 450);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 1));
        createDeck();

        playerLabel = new JLabel("Player: ");
        dealerLabel = new JLabel("Dealer: ");
        resultLabel = new JLabel("Press DEAL");

        JButton dealButton = new JButton("DEAL");

        dealButton.addActionListener(e -> playGame());

        add(playerLabel);
        add(dealerLabel);
        add(dealButton);
        add(resultLabel);
    }

    private void playGame() {
        Collections.shuffle(deck);

        Card player = deck.get(0);
        Card dealer = deck.get(1);

        playerLabel.setText("Player: " + player);
        dealerLabel.setText("Dealer: " + dealer);

        if (player.rank > dealer.rank) {
            resultLabel.setText("Player wins");
        } else if (dealer.rank > player.rank) {
            resultLabel.setText("Dealer wins");
        } else {
            resultLabel.setText("Draw");
        }
    }

    private void createDeck() {
        String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};
        deck = new ArrayList<>();

        for (String s : suits) {
            for (int r = 0; r < 13; r++) {
                deck.add(new Card(s, r));
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Poker().setVisible(true);
        });
    }
}