// Deck.java
// Copyright (C) 1999  Jeff Gold
//   This program is free software; you can redistribute it and/or 
// modify it under the terms of the GNU General Public License as 
// published by the Free Software Foundation; either version 2 of the 
// License, or (at your option) any later version.
//   This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//   You should have received a copy of the GNU General Public License
// along with this program (see License.txt); if not, see
//     http://www.gnu.org 
// or write to the Free Software Foundation, Inc., 59 Temple Place, 
// Suite 330, Boston, MA  02111-1307  USA
//   NOTE: the Solitaire encryption algorithm is strong cryptography.
// That means the security it affords is based on the secrecy of the 
// key rather than secrecy of the algorithm itself.  That also means 
// that this program and programs derived from it may be treated as 
// a munition for the purpose of export regulation in the United States 
// and other countries.  You are encouraged to seek competent legal 
// counsel before distributing copies of this program.
// 
//   Deck is a class that represents a deck of playing cards.  A card 
// is represented by a byte.  The highest four bits represent the suit 
// and the lowest four represent the value within that suit.  To 
// generate a specific card, use "Deck.[SUIT] | Deck.[VALUE]".
// So the ace of spaces would be "Deck.SPADES | Deck.ACE", the jack
// of diamonds would be "Deck.DIAMONDS | Deck.JACK", and so on.  The
// jokers have a separate suit, and can be specified individually as
// "Deck.JOKER | Deck.JOKER_A" and "Deck.JOKER | Deck.JOKER_B".  For the
// sake of consistency, the joker with the bigger symbol should be 
// considered Joker B.

package com.erasmas.pojos;

import java.lang.*;
import java.io.Serializable;
import java.util.Random;


public class Deck implements Serializable, Cloneable {
  // Constants
  // =========
  // Card Masking Constants
  protected static final byte MASK_SUIT  = (byte)0xF0;
  protected static final byte MASK_VALUE = (byte)0x0F;
  protected static final byte MASK_BLACK = (byte)0x20;
  protected static final byte MASK_RED   = (byte)0x40;
  protected static final byte MASK_JOKER = (byte)0x80;

  // Card Suit Constants
  public static final byte CLUBS    = (byte)0x20;
  public static final byte DIAMONDS = (byte)0x40;
  public static final byte HEARTS   = (byte)0x50;
  public static final byte SPADES   = (byte)0x30;
  public static final byte JOKER    = (byte)0x90;

  // Card Value Constants
  public static final byte ACE   = (byte)0x01;
  public static final byte DEUCE = (byte)0x02;
  public static final byte THREE = (byte)0x03;
  public static final byte FOUR  = (byte)0x04;
  public static final byte FIVE  = (byte)0x05;
  public static final byte SIX   = (byte)0x06;
  public static final byte SEVEN = (byte)0x07;
  public static final byte EIGHT = (byte)0x08;
  public static final byte NINE  = (byte)0x09;
  public static final byte TEN   = (byte)0x0A;
  public static final byte JACK  = (byte)0x0B;
  public static final byte QUEEN = (byte)0x0C;
  public static final byte KING  = (byte)0x0D;
  public static final byte JOKER_A  = (byte)0x0E;
  public static final byte JOKER_B  = (byte)0x0F;

  // Representation Data
  // ===================
  protected byte cards[]; // The contents of the deck.
  protected int marker;   // Indicates the top.

  // Constructors
  // ============
  public Deck() {
    // Effect: create a deck of cards in bridge sorted order
    //   with jokers at the end.
    this(true);
  } // Deck()

  public Deck(boolean useJokers) {
    // Effect: create a deck of cards in bridge sorted order.
    //   Jokers will be included only if useJokers is true.
    marker = useJokers? 54 : 52;
    cards  = new byte[54];
    
    // Place the cards in order.
    for (int i = 0; i < 4; i++) {
      // Determine suit (using bridge ordering).
      byte suit = JOKER;
      switch (i) {
      case 0:  suit = CLUBS;     break;
      case 1:  suit = DIAMONDS;  break;
      case 2:  suit = HEARTS;    break;
      case 3:  suit = SPADES;    break;
      default:
        // This should not happen.
        break;
      }

      for (int j = 0; j < 13; j++) {
        cards[(marker - 1) - ((13 * i) + j)] =
          (byte)(suit | (j+1));
      } // for j
    } // for i

    // Place the jokers
    cards[useJokers ? 1 : marker + 1] = JOKER | JOKER_A;
    cards[useJokers ? 0 : marker]     = JOKER | JOKER_B;
  } // Deck()

  public Deck(byte cards[]) {
    // Effect: create a deck of cards with the order and
    //   content specified by the cards array.
    marker = cards.length;
    this.cards = new byte[marker];
    for (int i = 0; i < cards.length; i++) {
      this.cards[i] = cards[i];
    } // for i
  }

  protected Deck(Deck d) {
    // Effect: create a deck of cards identical to the 
    //   specified deck.
    marker = d.marker;
    cards  = new byte[d.cards.length];
    for (int i = 0; i < 54; i++) {
      cards[i] = d.cards[i];
    } // for i
  } // Deck(Deck)

  // Generic Methods
  // ===============
  public Object clone() {
    // Effect: returns a deck that is identical to this one.
    return new Deck(this);
  } // clone()

  public String toString() {
    // Effect: returns a string that represents the cards in
    //   this deck, included from the bottom of the deck to 
    //   the top.  This is meant to correspond to how a 
    //   person might look through an actual deck of cards
    //   after turning it over.
    StringBuffer buffer = new StringBuffer();
    for (int i = 0; i < marker; i++) {
      buffer.append(cardString(cards[i]));

      // Add line breaks after every thirteen cards.
      if (i % 13 == 12) 
        buffer.append("\n");
      else buffer.append(" ");
    } // for i

    // Add a final linefeed if necessary.
    if (marker % 13 != 0)
      buffer.append("\n");

    return buffer.toString();
  } // toString()

  // Deck Manipulation Methods (Mutators)
  // =========================
  public void shuffle() {
    // Effect: changes the order of the cards to a random
    //   pattern.  
    shuffle(new Random());
  } // shuffle()

  public void shuffle(Random rand) {
    // Effect: changes the order of the cards to a random
    //   pattern using the specified random number generator.
    for (int i = 0; i < marker; i++) {
      int swap = Math.abs(rand.nextInt() % marker);
      byte buffer = cards[i];
      cards[i] = cards[swap];
      cards[swap] = buffer;
    } // for i
  } // shuffle(Random)

  public byte deal() {
    // Effect: return the top card and remove it from the deck
    //   if any cards are left in the deck.  Otherwise return zero.
    return (marker > 0) ? cards[--marker] : (byte)0;
  } // deal()

  public void collect() {
    // Effect: restore cards that have been dealt to the deck.
    marker = cards.length;
  } // collect()

  protected void basicCut(int size, int limit) {
    // Effect: switch cards below the specified position with 
    //   the cards above, up to the specified limit.
    // A size of zero or limit will not change the deck.
    if (size > 0 && size < limit) {
      byte buffer[] = new byte[limit];
      // First move the lower cards up.
      for (int i = 0; i < size; i++) {
        buffer[i + (limit - size)] = cards[i];      
      } // for i

      // Then move the upper cards down.
      for (int i = size; i < limit; i++) {
        buffer[i - size] = cards[i];
      } // for i

      // Then replace the original deck.
      for (int i = 0; i < limit; i++) {
        cards[i] = buffer[i];
      } // for i
    } // if position
  } // basicCut(int, int)

  public void cutTop(int size) {
    // Effect: switch cards below the specified position with the
    //   cards above.
    // A size of zero or marker will not change the deck.
    basicCut(marker - size, marker);
  } // cutTop(int)

  public void cutBottom(int size) {
    // Effect: switch cards below the specified position with the
    //   cards above.
    // A size of zero or marker will not change the deck.
    basicCut(size, marker);
  } // cutBottom(int)

  public void tripleCut(int low, int high) {
    // Requires: 0 < low < high < this.count()
    // Effect: swaps the cards above the highest of the
    //   specified positions with cards below the lowest.
    if ((0 <= low) && (low <= high) && (high <= marker)) {
      basicCut(low, high);
      basicCut(high, marker);
    } // if low && high
  } // tripleCut();

  public void moveDown(int position) {
    // Effect: swaps the card at the specified position with
    //   the card below it, wrapping around the end of the 
    //   deck.
    moveDown(position, 1);
  } // moveDown(int)

  public void moveDown(int position, int count) {
    // Effect: swaps the card at the specified position with
    //   the card below it, wrapping around the end of the 
    //   deck.  If count is greater than zero then this
    //   operation is repeated that many times.
    int index = (marker - 1) - position;
    while (count > 0) {
      if (index > 0 && index < marker) {
        byte buffer = cards[index];
        cards[index] = cards[index - 1];
        cards[index - 1] = buffer;
        index--;
      } else if (index == 0) {
        byte buffer;
        for (int i = 0; i < marker - 2; i++) {
          buffer = cards[i];
          cards[i] = cards[i + 1];
          cards[i + 1] = buffer;
          index++;
        } // for i
      } // if position

      count--;
    } // while (count > 0)
  } // moveDown(int)

  // Deck Inspection Methods (Non-Mutators)
  // =======================
  public int count() {
    // Effect: return the number of cards in this deck.
    return marker;
  } // count()

  public byte peekTop(int position) {
    // Effect: return the card at the specified position, but do
    //   not remove it from the deck.  The top card has position
    //   zero, the next card position one, and so on.
    if (position >= 0 && position < marker) {
      return cards[(marker - 1) - position];
    } else return (byte)0;
  } // peekTop(int)

  public byte peekBottom(int position) {
    // Effect: return the card at the specified position, but do
    //   not remove it from the deck.  The top card has position
    //   zero, the next card position one, and so on.
    if (position >= 0 && position < marker) {
      return cards[position];
    } else return (byte)0;
  } // peekBottom(int)

  public int findTop(byte card) {
    // Effect: return the position of the specified card if it can
    //   be found in the deck.  Otherwise return a negative number.
    int i = 0;
    while (i < marker) {
      if (cards[i] == card) {
        return (marker - 1) - i;
      } // if (cards[i] == card)
      i++;
    } // while (i < marker)

    return -1;
  } // findTop(byte)

  public int findBottom(byte card) {
    // Effect: return the position of the specified card if it can
    //   be found in the deck.  Otherwise return a negative number.
    int i = 0;
    while (i < marker) {
      if (cards[i] == card) {
        return i;
      } // if (cards[i] == card)
      i++;
    } // while (i < marker)

    return -1;
  } // findBottom(byte)

  // Card Methods
  // ============
  public static String
  cardString(byte card) {
    // Effect: return a string that represenets the specified
    //   card.
    StringBuffer buffer = new StringBuffer();

    // Mark the card value.
    switch (card & MASK_VALUE) {
    case ACE:
      buffer.append("A");
      break;
    case DEUCE:
      buffer.append("2");
      break;
    case THREE:
      buffer.append("3");
      break;
    case FOUR:
      buffer.append("4");
      break;
    case FIVE:
      buffer.append("5");
      break;
    case SIX:
      buffer.append("6");
      break;
    case SEVEN:
      buffer.append("7");
      break;
    case EIGHT:
      buffer.append("8");
      break;
    case NINE:
      buffer.append("9");
      break;
    case TEN:
      buffer.append("0");
      break;
    case JACK:
      buffer.append("J");
      break;
    case QUEEN:
      buffer.append("Q");
      break;
    case KING:
      buffer.append("K");
      break;
    case JOKER_A:
      buffer.append("A");
      break;
    case JOKER_B:
      buffer.append("B");
      break;
    default:
      // This should not happen.
      break;
    } // switch (card & MASK_VALUE)

    // Mark the card suit.
    switch (card & MASK_SUIT) {
    case CLUBS:
      buffer.append("C");
      break;
    case DIAMONDS:
      buffer.append("D");
      break;
    case HEARTS:
      buffer.append("H");
      break;
    case SPADES:
      buffer.append("S");
      break;
    case JOKER:
      buffer.append("J");
      break;
    default:
      break;
    } // switch (card & MASK_SUIT)

    return buffer.toString();
  } // cardString(byte)

  // Suit Methods
  public static boolean 
  isClub(byte card)    { return (card & MASK_SUIT) == CLUBS;    }
  public static boolean 
  isDiamond(byte card) { return (card & MASK_SUIT) == DIAMONDS; }
  public static boolean 
  isHeart(byte card)   { return (card & MASK_SUIT) == HEARTS;   }
  public static boolean 
  isSpade(byte card)   { return (card & MASK_SUIT) == SPADES;   }

  // Type Methods
  public static boolean 
  isBlack(byte card)   { return (card & MASK_BLACK) != (byte)0; }
  public static boolean
  isRed(byte card)     { return (card & MASK_RED)   != (byte)0; }
  public static boolean
  isJoker(byte card)   { return (card & MASK_JOKER) != (byte)0; }

  // Joker Methods
  public static boolean 
  isJokerA(byte card) { return card == (JOKER | JOKER_A); }
  public static boolean 
  isJokerB(byte card) { return card == (JOKER | JOKER_B); }

  // Value Methods
  public static boolean 
  isAce(byte card)   { return (card & MASK_VALUE) == ACE;   }
  public static boolean 
  isDuece(byte card) { return (card & MASK_VALUE) == DEUCE; }
  public static boolean 
  isTwo(byte card) { return isDuece(card); }
  public static boolean 
  isThree(byte card) { return (card & MASK_VALUE) == THREE;  }
  public static boolean 
  isFour(byte card)  { return (card & MASK_VALUE) == FOUR;  }
  public static boolean 
  isFive(byte card)  { return (card & MASK_VALUE) == FIVE;  }
  public static boolean 
  isSix(byte card)   { return (card & MASK_VALUE) == SIX;   }
  public static boolean 
  isSeven(byte card) { return (card & MASK_VALUE) == SEVEN; }
  public static boolean 
  isEight(byte card) { return (card & MASK_VALUE) == EIGHT; }
  public static boolean 
  isNine(byte card)  { return (card & MASK_VALUE) == NINE;  }
  public static boolean 
  isTen(byte card)   { return (card & MASK_VALUE) == TEN;   }
  public static boolean 
  isJack(byte card)  { return (card & MASK_VALUE) == JACK;  }
  public static boolean 
  isQueen(byte card) { return (card & MASK_VALUE) == QUEEN; }
  public static boolean 
  isKing(byte card)  { return (card & MASK_VALUE) == KING;  }

  public static int 
  getFaceValue(byte card) {return (int)(card & MASK_VALUE); }

  // Module Test Method
  // ==================
  public static void main(String args[]) {
    // Effect: executes a test of this module.
    Deck d = new Deck(false);
    System.out.println("Default Deck:");
    System.out.println(d.toString());
    System.out.println(); // */

    d.shuffle();
    System.out.println("Shuffled Deck:");
    System.out.println(d.toString());
    System.out.println(); // */
  } // main(String[])
} // class Deck