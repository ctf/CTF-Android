package com.erasmas.pojos;

import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Key implements Serializable {

    private Deck deck;
    private String keyString;
    private String label;

    public static final String DEFAULT_KEYSTRING = "Cryptonomicon";
    public static final Deck DEFAULT_KEY = new SolitaireCipher(DEFAULT_KEYSTRING).getDeck();
    public static final String DEFAULT_LABEL = "DEFAULT";

    public Key() {
        deck = DEFAULT_KEY;
        keyString = DEFAULT_KEYSTRING;
        label = DEFAULT_LABEL;
    }
    public Key(Deck deck) {
        this.deck = deck;
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
    }
    public Deck getDeck() {
        return deck;
    }
    public void setLabel(String label) {
        this.label = label;
    }
    public String getLabel() {
        return label;
    }

    public String toString() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            oos.close();
        } catch (IOException e) {}
        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
    }

    public static Key fromString(final String s) throws IOException , ClassNotFoundException {
        byte [] data = Base64.decode(s, Base64.DEFAULT);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
        Key key  = (Key) ois.readObject();
        ois.close();
        return key;
    }


    public void setKeyString(String keyString) {
        this.keyString = keyString;
    }
}
