package net.formula97.fakegpbase.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import java.util.EventListener;

/**
 * メッセージ表示とその選択を呼び出し元に返送するDialogFragment。
 *
 * Created by f97one on 14/10/29.
 */
public class MessageDialogs extends DialogFragment {

    public static final String FRAGMENT_TAG = "net.formula97.fakegpbase.fragments.MessageDialogs.DIALOG_TAG";

    /**
     * 押されたボタンがPositiveButtonだった時のフラグ
     */
    public static final int PRESSED_POSITIVE = 0;
    /**
     * 押されたボタンがNegativeButtonだった時のフラグ
     */
    public static final int PRESSED_NEGATIVE = 1;

    public static final int BUTTON_NONE = 0;
    public static final int BUTTON_POSITIVE = 1;
    public static final int BUTTON_NEGATIVE = 2;
    public static final int BUTTON_BOTH = 3;
    /**
     * コールバックのインスタンス保持メンバ
     */
    private DialogsButtonSelectionCallback callback;

    private static final String titleKey = "TitleKey";
    private static final String messageKey = "MessageKey";
    private static final String buttonKindKey = "ButtonKindKey";

    /**
     * Dialogのボタンを押した時のコールバック。
     */
    public interface DialogsButtonSelectionCallback extends EventListener {

        /**
         * Dialogのボタンが押された時に呼び出される。
         *
         * @param messageBody 呼び出したDialogで表示していたメッセージ本文
         * @param which 押されたボタンの種類
         */
        public void onButtonPressed(String messageBody, int which);
    }

    public MessageDialogs() { }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        callback = null;

        if (activity instanceof DialogsButtonSelectionCallback) {
            callback = (DialogsButtonSelectionCallback) activity;
        }
    }

    /**
     * メッセージダイアログのインスタンスを取得する。
     *
     * @param title
     * @param message
     * @param buttonKind
     * @return
     */
    public static MessageDialogs getInstance(String title, String message, int buttonKind) {
        MessageDialogs d = new MessageDialogs();

        Bundle args = new Bundle();
        args.putString(titleKey, title);
        args.putString(messageKey, message);
        args.putInt(buttonKindKey, buttonKind);
        d.setArguments(args);

        return d;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        Bundle args = getArguments();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(args.getString(titleKey))
                .setMessage(args.getString(messageKey));

        switch (args.getInt(buttonKindKey)) {
            case BUTTON_NONE:
                // ボタンはないので何もしない
                break;
            case BUTTON_POSITIVE:
                makePositiveButton(builder, args.getString(messageKey));
                break;
            case BUTTON_NEGATIVE:
                makeNegativeButton(builder, args.getString(messageKey));
                break;
            case BUTTON_BOTH:
                makePositiveButton(builder, args.getString(messageKey));
                makeNegativeButton(builder, args.getString(messageKey));
                break;
        }

        return builder.create();
    }

    private void makeNegativeButton(AlertDialog.Builder builder, final String messageBody) {
        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (callback != null) {
                    callback.onButtonPressed(messageBody, PRESSED_NEGATIVE);
                }
                getDialog().dismiss();
            }
        });
    }

    private void makePositiveButton(AlertDialog.Builder builder, final String messageBody) {
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (callback != null) {
                    callback.onButtonPressed(messageBody, PRESSED_POSITIVE);
                }
                getDialog().dismiss();
            }
        });
    }
}
