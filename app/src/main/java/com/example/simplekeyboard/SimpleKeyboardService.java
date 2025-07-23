
package com.example.simplekeyboard;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;

public class SimpleKeyboardService extends InputMethodService implements KeyboardView.OnKeyboardActionListener {

    private KeyboardView kv;
    private Keyboard keyboardLetters;
    private Keyboard keyboardSymbols;
    private Keyboard keyboardEmojis;
    private boolean isCaps = false;
    private int layoutState = 0; // 0=letters, 1=symbols, 2=emojis

    @Override
    public View onCreateInputView() {
        kv = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard_view, null);
        keyboardLetters = new Keyboard(this, R.xml.keyboard_layout_letters);
        keyboardSymbols = new Keyboard(this, R.xml.keyboard_layout_symbols);
        keyboardEmojis = new Keyboard(this, R.xml.keyboard_layout_emojis);
        kv.setKeyboard(keyboardLetters);
        kv.setOnKeyboardActionListener(this);
        return kv;
    }

    private void switchLayout(int state) {
        layoutState = state;
        switch (state) {
            case 0:
                kv.setKeyboard(keyboardLetters);
                break;
            case 1:
                kv.setKeyboard(keyboardSymbols);
                break;
            case 2:
                kv.setKeyboard(keyboardEmojis);
                break;
        }
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        InputConnection ic = getCurrentInputConnection();
        switch(primaryCode){
            case Keyboard.KEYCODE_DELETE:
                ic.deleteSurroundingText(1, 0);
                break;
            case Keyboard.KEYCODE_SHIFT:
                isCaps = !isCaps;
                keyboardLetters.setShifted(isCaps);
                kv.invalidateAllKeys();
                break;
            case -10: // switch to symbols
                switchLayout(1);
                break;
            case -11: // long press for emoji
                if (layoutState == 1) {
                    switchLayout(2);
                } else {
                    switchLayout(1);
                }
                break;
            case Keyboard.KEYCODE_MODE_CHANGE:
                switchLayout(0);
                break;
            case Keyboard.KEYCODE_DONE:
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                break;
            default:
                char code = (char) primaryCode;
                if (Character.isLetter(code) && isCaps) {
                    code = Character.toUpperCase(code);
                }
                ic.commitText(String.valueOf(code), 1);
        }
    }

    @Override public void onPress(int primaryCode) {}
    @Override public void onRelease(int primaryCode) {}
    @Override public void onText(CharSequence text) {}
    @Override public void swipeLeft() {}
    @Override public void swipeRight() {}
    @Override public void swipeDown() {}
    @Override public void swipeUp() {}
}
