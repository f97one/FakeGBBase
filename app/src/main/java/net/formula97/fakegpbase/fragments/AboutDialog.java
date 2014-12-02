package net.formula97.fakegpbase.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import net.formula97.fakegpbase.AppConst;
import net.formula97.fakegpbase.R;

/**
 * Created by f97one on 14/12/02.
 */
public class AboutDialog extends DialogFragment {

    public static final String DIALOG_TAG = AboutDialog.class.getName() + AppConst.DIALOG_FRAGMENT_SUFFIX;

    public AboutDialog() { }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.about_dialog, null);

        TextView tvAppVersion = (TextView) view.findViewById(R.id.tvAppVersion);
        tvAppVersion.setText(getVersionName());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);

        return builder.create();
    }

    private String getVersionName() {
        PackageManager packageManager = getActivity().getPackageManager();
        String versionName = "";
        try {
            PackageInfo info = packageManager.getPackageInfo(getActivity().getPackageName(), 0);
            versionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return versionName;
    }
}
