package net.formula97.fakegpbase.fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import net.formula97.fakegpbase.Databases.GunplaInfo;
import net.formula97.fakegpbase.R;

import java.util.List;

/**
 * Created by f97one on 14/11/03.
 */
public class GunplaInfoAdapter extends ArrayAdapter<GunplaInfo> {

    private List<GunplaInfo> gunplaInfoList;
    private int resourceId;
    private Context mContext;

    public GunplaInfoAdapter(Context context, int resource, List<GunplaInfo> gunplaInfos) {
        super(context, resource, gunplaInfos);

        this.mContext = context;
        this.resourceId = resource;
        this.gunplaInfoList = gunplaInfos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(resourceId, null);
        }

        TextView modelName = (TextView) convertView.findViewById(R.id.listview_model_name);
        TextView gunplaName = (TextView) convertView.findViewById(R.id.listview_gunpla_name);
        TextView scale = (TextView) convertView.findViewById(R.id.listview_scale);
        TextView grade = (TextView) convertView.findViewById(R.id.listview_grade);

        modelName.setText(gunplaInfoList.get(position).getModelNo());
        gunplaName.setText(gunplaInfoList.get(position).getGunplaName());
        scale.setText(gunplaInfoList.get(position).getScaleValue());
        grade.setText(gunplaInfoList.get(position).getClassValue());

        return convertView;
    }
}
