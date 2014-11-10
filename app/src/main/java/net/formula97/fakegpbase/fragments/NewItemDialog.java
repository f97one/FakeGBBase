package net.formula97.fakegpbase.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import net.formula97.fakegpbase.AppConst;
import net.formula97.fakegpbase.R;

import java.util.EventListener;

/**
 * アイテム追加用DialogFragment。<br />
 * Created by f97one on 14/11/08.
 */
public class NewItemDialog extends DialogFragment {

    /**
     * Fragment特定用のタグ
     */
    public static final String FRAGMENT_TAG = NewItemDialog.class.getName() + AppConst.DIALOG_FRAGMENT_SUFFIX;
    /**
     * DialogFragmentにセットしているEditTextのResId
     */
    public static final int EDITTEXT_ID = 0x7fff1001;

    /**
     * アイテム入力終了を通知するイベントリスナ。
     */
    public interface OnInputCompleteListener extends EventListener {
        /**
         * アイテム入力終了後に呼び出される。
         *
         * @param inputItem 入力したアイテム
         */
        public void onInputCompleted(String inputItem);
    }

    private OnInputCompleteListener mListener;

    public NewItemDialog() { }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof OnInputCompleteListener) {
            mListener = (OnInputCompleteListener) activity;
        } else {
            mListener = null;
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final EditText et = new EditText(getActivity());
        et.setId(EDITTEXT_ID);
        et.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    if (mListener != null) {
                        mListener.onInputCompleted(et.getText().toString());
                    }
                    getDialog().dismiss();
                    return true;
                }

                return false;
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.input_new))
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getDialog().dismiss();
                    }
                })
                .setView(et);

        return builder.create();
    }
}
