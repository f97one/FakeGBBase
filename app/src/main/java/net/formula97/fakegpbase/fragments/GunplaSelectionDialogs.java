package net.formula97.fakegpbase.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import net.formula97.fakegpbase.AppConst;
import net.formula97.fakegpbase.Databases.GunplaInfo;
import net.formula97.fakegpbase.Databases.GunplaInfoModel;
import net.formula97.fakegpbase.R;

import java.util.EventListener;
import java.util.List;

/**
 * Created by f97one on 14/11/03.
 */
public class GunplaSelectionDialogs extends DialogFragment {

    public static final String FRAGMENT_TAG = GunplaSelectionDialogs.class.getName()
            + AppConst.DIALOG_FRAGMENT_SUFFIX;

    private OnGunplaSelectedListener mListener;

    public interface OnGunplaSelectedListener extends EventListener {
        public void onGunplaSelected(GunplaInfo info);
    }

    public GunplaSelectionDialogs() { }

    public static GunplaSelectionDialogs newInstance() {
        GunplaSelectionDialogs dialogs = new GunplaSelectionDialogs();

        return dialogs;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof OnGunplaSelectedListener) {
            mListener = (OnGunplaSelectedListener) activity;
        } else {
            mListener = null;
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        ListView listView = new ListView(getActivity());
        GunplaInfoModel model = new GunplaInfoModel(getActivity());
        List<GunplaInfo> infos = model.findAll();
        GunplaInfoAdapter adapter = new GunplaInfoAdapter(getActivity(), R.layout.gunpla_list_dialog, infos);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mListener != null) {
                    mListener.onGunplaSelected((GunplaInfo) parent.getAdapter().getItem(position));
                }
                getDialog().dismiss();
            }
        });

        builder.setView(listView);

        return builder.create();
    }
}
