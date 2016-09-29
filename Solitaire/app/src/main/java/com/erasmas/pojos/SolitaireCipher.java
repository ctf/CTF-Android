// SolitaireCipher.java
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
//   This package contains a Java implementation of the Solitaire 
// encryption algorithm, as designed by Bruce Schneier and described at: 
//     http://www.counterpane.com/solitaire.html
// as well as in Neil Stephenson's novel Cryptonomicon.
//   Solitaire is a cryptographically strong stream cipher that can
// be implemented using a deck of playing cards.  This implementation 
// is not designed for high performance -- this is a Java program, after 
// all.  Instead it is intended to be clean, portable, and easy to 
// understand.

package com.erasmas.pojos;

import java.lang.*;
import java.io.Serializable;

public final class SolitaireCipher implements Serializable, Cloneable {

  // Representation Data
  // ===================
  protected Deck keyDeck;

  // Constructors
  // ============
  public SolitaireCipher(Deck keyDeck) {
    // Effect: creates a cipher with a key based on the
    //   order of the cards in the specified deck.
    if (keyDeck != null) {
      this.keyDeck = (Deck)keyDeck.clone();
    } else this.keyDeck = new Deck(true);
  } // SolitaireCipher(Deck)

  public SolitaireCipher(String keyPhrase) {
    // Effect: creates a cipher with a key based on the 
    //   specified string.
    keyDeck = new Deck(true);

    // Shuffle deck according to key phrase. 
    for (int i = 0; i < keyPhrase.length(); i++) {
      int cut_size = getCharValue(keyPhrase.charAt(i));
      if (cut_size > 0) {
        nextKeyStream();
        keyDeck.tripleCut(1, keyDeck.count() - cut_size);
        keyDeck.cutTop(1);
      }
    }
  } // SolitaireCipher(String)

  // Generic Methods
  // ===============
  public Object clone() {
    return new SolitaireCipher(keyDeck);
  } // clone()

  public String toString() {
    // Effect: return a string that represents the state of 
    //   this cipher.
    return keyDeck.toString();
  } // toString()

  // Deck Methods
  // ============
  public Deck getDeck() {
    // Effect: return a copy of the current key deck.
    return (Deck)keyDeck.clone();
  } // getDeck()

  // Utility Methods
  // ===============
  protected static int getCardValue(byte card) {
    // Effect: returns a numeric value for the specified card.
    if (Deck.isJoker(card)) {
      return 53;
    } else if (Deck.isClub(card)) {
      return Deck.getFaceValue(card);
    } else if (Deck.isDiamond(card)) {
      return 13 + Deck.getFaceValue(card);
    } else if (Deck.isHeart(card)) {
      return 26 + Deck.getFaceValue(card);
    } else if (Deck.isSpade(card)) {
      return 39 + Deck.getFaceValue(card);
    } else {
      return 0;  // Something bad happened.
    }
  } // getCardValue(byte)

  protected static int getCharValue(char c) {
    // Effect: return the position of the specified character in
    //   the alphabet regardless of case, or zero if the character
    //   is not in the Latin alphabet.
    if (c >= 'A' && c <= 'Z') {
      return (int)c - 'A' + 1;
    } else if (c >= 'a' && c <= 'z') {
      return (int)c - 'a' + 1;
    } else {
      return 0;
    }
  } // getCharValue(char)

  protected static char getValueChar(int v) {
    // Effect: return the position of the specified character in
    //   the alphabet regardless of case, or zero if the character
    //   is not in the Latin alphabet.
    if (v >= 1 && v <= 26) {
      return (char)((v - 1) + (int)'A');
    } else {
      return '*';
    }
  } // getCharValue(char)

  // Cryptographic Methods
  // =====================
  protected int nextKeyStream() {
    // Effect: performs the basic stream encryption operation
    //   and returns the next element of the key stream.
    byte jokerA = (byte)(Deck.JOKER | Deck.JOKER_A);
    byte jokerB = (byte)(Deck.JOKER | Deck.JOKER_B);

    // Step one: Move Joker A one card down.
    keyDeck.moveDown(keyDeck.findTop(jokerA), 1);

    // Step two: Move Joker B two cards down.
    keyDeck.moveDown(keyDeck.findTop(jokerB), 2);

    // Step three: Perform a triple cut.
    int at_jokerA = keyDeck.findBottom(jokerA);
    int at_jokerB = keyDeck.findBottom(jokerB);
    keyDeck.tripleCut(Math.min(at_jokerA, at_jokerB), 
                      Math.max(at_jokerA, at_jokerB) + 1);

    // Step four: Perform a count cut.
    byte count = keyDeck.peekBottom(0);
    keyDeck.tripleCut(1, keyDeck.count() - getCardValue(count));
    keyDeck.cutTop(1);

    // Step five: Find the output card.
    byte output = keyDeck.peekTop(getCardValue(keyDeck.peekTop(0)));

    // Step six: Convert output card to a number.
    int result = getCardValue(output);
    if (result == 53) return nextKeyStream();
    else if (result > 26) result = result - 26;
    return result;
  } // nextKeyStream()

  public String decrypt(String ciphertext) {
    // Effect: returns the ciphertext corresponding to the specified
    //   plaintext, according to the current key deck ordering.
    StringBuffer buffer = new StringBuffer();

    for (int i = 0; i < ciphertext.length(); i++) {
      int plain = getCharValue(ciphertext.charAt(i));
      if (plain > 0) {
        plain -= nextKeyStream();
        if (plain < 1) plain += 26;
        buffer.append(getValueChar(plain));
      }
    }

    return buffer.toString();
  } // decrypt(String)

  public String encrypt(String plaintext) {
    // Effect: returns the ciphertext corresponding to the specified
    //   plaintext, according to the current key deck ordering.  Any
    //   string with a length that is not a multiple of five will be
    //   padded with the character 'X'.
    return encrypt(plaintext, true);
  } // encrypt(String)

  public String encrypt(String plaintext, boolean padded) {
    // Effect: returns the ciphertext corresponding to the specified
    //   plaintext, according to the current key deck ordering.
    StringBuffer buffer = new StringBuffer();

    for (int i = 0; i < plaintext.length(); i++) {
      int cipher = getCharValue(plaintext.charAt(i));
      if (cipher > 0) {
        cipher += nextKeyStream();
        if (cipher > 26) cipher -= 26;
        buffer.append(getValueChar(cipher));
      }
    }

    if (padded) {
      while (buffer.length() % 5 != 0) {
        buffer.append(encrypt("X", false));
      }
    }
    return buffer.toString();
  } // encrypt(String, boolean)

  // Module Test Method
  // ==================
  public static void main(String args[]) {
    // Effect: enciphers args[0] using args[1] as a key.  If
    //   args[1] is not supplied, a default deck is used for the key.
    //   If args[0] is not supplied, enciphers the string "SOLITAIRE".
    SolitaireCipher scIn  = new SolitaireCipher(args.length > 1 ? args[1] : "");
    SolitaireCipher scOut = (SolitaireCipher)scIn.clone();
    String plaintext = args.length > 0 ? args[0] : "SOLITAIRE";
    String ciphertext = scIn.encrypt(plaintext);
    String outputtext = scOut.decrypt(ciphertext);

    System.out.println("Plaintext:  " + plaintext);
    System.out.println("Ciphertext: " + ciphertext);
    System.out.println("Outputtext: " + outputtext);

    System.out.println();
  } // main(String[])

} // class SolitaireCipher