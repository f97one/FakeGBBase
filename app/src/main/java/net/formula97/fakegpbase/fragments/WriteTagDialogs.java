package net.formula97.fakegpbase.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import net.formula97.fakegpbase.AppConst;
import net.formula97.fakegpbase.R;

import java.util.EventListener;

/**
 * NFCタグ書き込みの表示を出すDialogFragment。<br />
 * ※実際に読み書きをするわけではない。<br />
 * Created by f97one on 14/11/10.
 */
public class WriteTagDialogs extends DialogFragment {

    /**
     * Fragment特定用のタグ
     */
    public static final String FRAGMENT_TAG = WriteTagDialogs.class + AppConst.DIALOG_FRAGMENT_SUFFIX;
    /**
     * 書き込み処理に成功した場合のフラグ
     */
    public static final int OPERATION_SUCCESS = 0;
    /**
     * 書き込み処理に失敗した場合のフラグ
     */
    public static final int OPERATION_FAILED = 1;
    /**
     * 書き込み処理をキャンセルした場合のフラグ
     */
    public static final int OPERATION_CANCELED = -1;

    private OnTagOperationListener mListener;
    private boolean tagOperation = false;

    public WriteTagDialogs() { }

    /**
     * タグ操作処理が終了した時に呼ばれるコールバックリスナ。
     */
    public interface OnTagOperationListener extends EventListener {
        /**
         * タグ操作が終了したときに呼び出される。
         *
         * @param operationCode 書き込み成功（=0）、書き込み失敗（=1）、処理中止（=-1）のいずれか
         */
        public void onTagOperation(int operationCode);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof OnTagOperationListener) {
            mListener = (OnTagOperationListener) activity;
        } else {
            mListener = null;
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (!tagOperation && mListener != null) {
            mListener.onTagOperation(OPERATION_CANCELED);
        }

        super.onDismiss(dialog);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.write_tag))
                .setMessage(R.string.please_touch);

        return builder.create();
    }
}
