package player;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import player.card.Card;
import player.card.Deck;

import java.io.File;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void testPlayerConstructor() {
        // No public methods to test constructor was successful.
        // Test constructor through the other methods succeeding
    }

    @Test
    void testPushCard() {
        var testPlayer1 = new Player(0, null, null, null);

        // Setup state
        // Test one card
        testPlayer1.pushCard(new Card(1));
        assertEquals("1 ", testPlayer1.getHandString());
        // Test with full deck
        testPlayer1.pushCard(new Card(2));
        testPlayer1.pushCard(new Card(3));
        testPlayer1.pushCard(new Card(4));
        assertEquals("1 2 3 4 ", testPlayer1.getHandString());
    }

    @Test
    void testPrintPlayHistory() {
        var inDeck = new Deck(1);
        var outDeck = new Deck(2);
        var judge = new PlayerJudge();
        var player = new Player(0, inDeck, outDeck, judge);

        assertDoesNotThrow(() -> {
            inDeck.pushCard(new Card(5));
            outDeck.pushCard(new Card(0));
        });
        // Set winner so player only plays one move
        judge.newWinner(0, 2);
        player.pushCard(new Card(1));
        player.pushCard(new Card(2));
        player.pushCard(new Card(3));
        player.pushCard(new Card(4));
        player.run();

        // Creates a file called "player0_output.txt" containing the play
        player.printPlayHistory();
        assertDoesNotThrow(() -> {
            StringBuilder output = new StringBuilder();
            File file = new File("player0_output.txt");
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                output.append(scanner.nextLine()).append("\n");
            }
            assertEquals("""
                    player 0 initial hand 1 2 3 4\s
                    player 0 draws a 5 from deck 1
                    player 0 discards a 1 to deck 2
                    player 0 current hand is 2 3 4 5\s
                    player 0 has informed player 0 that player 0 has won
                    player 0 exits
                    player 0 final hand: 2 3 4 5\s
                    """, output.toString());
        });
    }

    @Test
    @Timeout(value = 1000, unit = TimeUnit.MILLISECONDS)
    void testRun() {
        // Test the game playing strategy
        var inDeck = new Deck(1);
        var outDeck = new Deck(2);
        var judge = new PlayerJudge();
        var player = new Player(0, inDeck, outDeck, judge);
        var playerThread = new Thread(player);

        // Keep out deck empty, so it's easier to analyze what the player discarded
        // Initialize hand
        player.pushCard(new Card(0));
        player.pushCard(new Card(1));
        player.pushCard(new Card(2));
        player.pushCard(new Card(3));

        playerThread.start();

        assertDoesNotThrow(() -> {
            // Initialize in-deck after game starts (player should win after drawing all the 0s)
            inDeck.pushCard(new Card(0));
            inDeck.pushCard(new Card(4));
            inDeck.pushCard(new Card(0));
            inDeck.pushCard(new Card(5));
            inDeck.pushCard(new Card(6));
            inDeck.pushCard(new Card(0));
            inDeck.pushCard(new Card(7));

            // Player wins after discarding only the following cards
            assertEquals(1, outDeck.dealNextCard().getDenomination());
            assertEquals(2, outDeck.dealNextCard().getDenomination());
            assertEquals(3, outDeck.dealNextCard().getDenomination());
            assertEquals(4, outDeck.dealNextCard().getDenomination());
            assertEquals(5, outDeck.dealNextCard().getDenomination());
            assertEquals(6, outDeck.dealNextCard().getDenomination());

            playerThread.join();

            // No more cards in discard pile
            assertTrue(outDeck.isEmpty());
            // Check in-deck has one card remaining
            assertFalse(inDeck.isEmpty());
            inDeck.dealNextCard();
            assertTrue(inDeck.isEmpty());
        });
        // Player wins in six moves exactly
        assertEquals(0, judge.getWinningPlayer());
        assertEquals(6, judge.getWinningTurn());
    }
}