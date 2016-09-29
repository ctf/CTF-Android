package com.erasmas.fragment;

import android.R.layout;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.erasmas.pojos.Key;
import com.erasmas.pojos.SolitaireCipher;
import com.erasmas.solitaire.R;
import com.erasmas.solitaire.R.id;

import java.io.IOException;
import java.util.Arrays;

public class FragmentDecrypt extends Fragment {

    private SharedPreferences keyStore;
    private Editor keyStoreEditor;
    private Key key;


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_decrypt, container, false);

        keyStore = getActivity().getPreferences(Context.MODE_PRIVATE);
        keyStoreEditor = keyStore.edit();

        // get sorted list of keys
        String[] keyLabels = getKeyList();

        // setup adapter with list of keys for dropdown menu
        ArrayAdapter<CharSequence> items = new ArrayAdapter(getActivity(), layout.simple_spinner_dropdown_item, keyLabels);
        items.setDropDownViewResource(layout.simple_spinner_dropdown_item);

        // setup dropdown with list of keys
        final Spinner dropdown = (Spinner) rootView.findViewById(id.spinnerD);
        dropdown.setAdapter(items);
        dropdown.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view, final int position, long id) {
                initSelectedKey(dropdown.getSelectedItem()); // init the key to use for encrypting
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {
                key = new Key(); // init the default key if nothing is selected
            }
        });

        final EditText plaintextBox = (EditText) rootView.findViewById(id.ciphertextD);
        final EditText ciphertextBox = (EditText) rootView.findViewById(id.plaintextD);

        rootView.findViewById(id.button_decrypt).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final String plaintext = String.valueOf(plaintextBox.getText());
                final String ciphertext = decrypt(plaintext);
                ciphertextBox.setText(ciphertext);
            }
        });

        rootView.findViewById(id.button_copy).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                final ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                final ClipData clip = ClipData.newPlainText("Ciphertext", ciphertextBox.getText().toString());
                clipboard.setPrimaryClip(clip);
            }
        });

        return rootView;
    }

    private String decrypt(final String ciphertext) {
        SolitaireCipher cipher = new SolitaireCipher(key.getDeck());
        String plaintext = cipher.decrypt(ciphertext);

        keyStoreEditor.putString(key.getLabel(), key.toString()); // save the modified key
        keyStoreEditor.commit();

        return plaintext;
    }

    private void initSelectedKey(final Object selectedItem) {
        try {
            key = Key.fromString(keyStore.getString(selectedItem.toString(), Key.DEFAULT_KEYSTRING));
            key.setLabel(selectedItem.toString());
        } catch (IOException | ClassNotFoundException | NullPointerException e) {
            key = new Key();
        }
    }

    private String[] getKeyList() {
        if (keyStore != null) {
            String[] keyLabels = keyStore.getAll().keySet().toArray(new String[0]);
            Arrays.sort(keyLabels);
            return keyLabels;
        } else {
            return new String[0];
        }
    }

}
