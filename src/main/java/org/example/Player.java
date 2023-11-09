package org.example;
import java.util.Queue;
import java.util.ArrayDeque;

public class Player {
    private final Deck inputdeck;
    private final Deck outputdeck;
    private Queue<Card> hand;
    private int preferredcount;

    /**
     * Constructs the Player with a given input deck, output deck and initial hand
     * @param id The deck for the player to draw cards from
     * @param od The deck for the player to discard cards to
     * @param h The initial hand of cards for the player
     */
    public Player(Deck id, Deck od, Queue<Card> h) {
        inputdeck = id;
        outputdeck = od;
        hand = h;
        preferredcount = 0;
    }

    /**
     * Draws card from input deck and adds to hand
     * Returns the denomination of the drawn card
     * @return newly drawn card's denomination
     */
    public int drawCard() {
        Card drawnCard = inputdeck.dealNextCard();
        hand.add(drawnCard);
        return drawnCard.getDenomination();
    }

    /**
     * Discards card from hand and adds to output deck
     * Returns denomination of the discarded card
     * @return newly discarded card's denomination
     */
    public int discardCard() {
        Card discardedCard = hand.remove();
        outputdeck.pushCard(discardedCard);
        return discardedCard.getDenomination();
    }


}
