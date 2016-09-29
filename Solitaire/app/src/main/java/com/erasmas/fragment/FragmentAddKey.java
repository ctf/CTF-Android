package com.erasmas.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.erasmas.pojos.Deck;
import com.erasmas.pojos.Key;
import com.erasmas.pojos.SolitaireCipher;
import com.erasmas.solitaire.R.id;
import com.erasmas.solitaire.R.layout;

public class FragmentAddKey extends Fragment {

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

        final View rootView = inflater.inflate(layout.fragment_add_key, container, false);

        final SharedPreferences keyStore = getActivity().getPreferences(Context.MODE_PRIVATE);
        final Editor keyStoreEditor = keyStore.edit();


        rootView.findViewById(id.button_save_key).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // todo: provide other ways to generate decks/keys

                // get the keyString and label
                final String keyLabel = ((EditText) rootView.findViewById(id.key_label)).getText().toString();
                final String keyString = ((EditText) rootView.findViewById(id.key_string)).getText().toString();

                // create the key and save it with the given label
                Deck deck = new SolitaireCipher(keyString).getDeck();
                Key key = new Key(deck);
                key.setLabel(keyLabel);
                key.setKeyString(keyString);

                keyStoreEditor.putString(keyLabel, key.toString());
                keyStoreEditor.commit();

                // return to keys page
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.beginTransaction().replace(id.content_frame, new FragmentKeys()).commit();
            }
        });


        return rootView;
    }


}
