package net.formula97.fakegpbase;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import net.formula97.fakegpbase.Databases.GunplaInfo;
import net.formula97.fakegpbase.Databases.GunplaInfoModel;
import net.formula97.fakegpbase.fragments.NewItemDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class GunplaRegisterActivity extends Activity implements AdapterView.OnItemSelectedListener,
        NewItemDialog.OnInputCompleteListener {

    private EditText etRegisterBuilderName;
    private EditText etRegisterFighterName;
    private Spinner spinnerScaleName;
    private Spinner spinnerClassName;
    private RadioGroup radioGroup1;
    private RadioButton radioBtnNonScratch;
    private RadioButton radioBtnPartialScratch;
    private RadioButton radioBtnFullScratch;
    private EditText etRegisterModelName;
    private EditText etRegisterGunplaName;
    private AdView adView2;

    private String mBuiderName;
    private String mFighterName;
    private int mScaleSelectedPos;
    private String mScaleVal;
    private int mClassSelectedPos;
    private String mClassVal;
    private int mScratchSelected;
    private String mModelName;
    private String mGunplaName;
    private String mTagId;

    private GunplaInfo mGunplaInfo;
    private ArrayAdapter<String> scaleAdapter;
    private ArrayAdapter<String> classAapter;
    private boolean isScaleSelected = true;
    private boolean stopTagRead = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gunpla_register);

        initView();

        adView2 = (AdView) findViewById(R.id.adView2);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView2.loadAd(adRequest);

        initVariables();

        String[] scales = getResources().getStringArray(R.array.scale_list);
        List<String> scaleList = new ArrayList<>(Arrays.asList(scales));
        scaleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, scaleList);
        scaleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerScaleName.setAdapter(scaleAdapter);
        spinnerScaleName.setOnItemSelectedListener(this);

        String[] classes = getResources().getStringArray(R.array.class_list);
        List<String> classList = new ArrayList<>(Arrays.asList(classes));
        classAapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, classList);
        classAapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClassName.setAdapter(classAapter);
        spinnerClassName.setOnItemSelectedListener(this);
    }

    private void initView() {
        etRegisterBuilderName = (EditText) findViewById(R.id.etRegisterBuilderName);
        etRegisterFighterName = (EditText) findViewById(R.id.etRegisterFighterName);
        spinnerScaleName = (Spinner) findViewById(R.id.spinnerScaleName);
        spinnerClassName = (Spinner) findViewById(R.id.spinnerClassName);
        radioGroup1 = (RadioGroup) findViewById(R.id.radioGroup1);
        radioBtnNonScratch = (RadioButton) findViewById(R.id.radioBtnNonScratch);
        radioBtnPartialScratch = (RadioButton) findViewById(R.id.radioBtnPartialScratch);
        radioBtnFullScratch = (RadioButton) findViewById(R.id.radioBtnFullScratch);
        etRegisterModelName = (EditText) findViewById(R.id.etRegisterModelName);
        etRegisterGunplaName = (EditText) findViewById(R.id.etRegisterGunplaName);
    }

    /**
     * 変数を初期化する。
     */
    private void initVariables() {
        // ビルダー名とファイター名の初期値はPreferenceの値とする
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

        mBuiderName = pref.getString(AppConst.PREF_KEY_BUILDER_NAME, "");
        mFighterName = pref.getString(AppConst.PREF_KEY_FIGHTER_NAME, "");
        mScaleSelectedPos = 0;
        mScaleVal = "";
        mClassSelectedPos = 0;
        mClassVal = "";
        mScratchSelected = 0;
        mModelName = "";
        mGunplaName = "";
        mTagId = "";

        mGunplaInfo = new GunplaInfo();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gunpla_register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_submit:
                // SQLiteへのデータセーブ

                return true;
            case R.id.action_write_tag:
                // NFCタグ書き込み


                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        adView2.resume();

        etRegisterBuilderName.setText(mBuiderName);
        etRegisterFighterName.setText(mFighterName);

        // スケールとグレードは、保存している値とArrayAdapterにある値がマッチ
        // しなかった時に、ArrayAdapterに値を挿入する
        boolean matched = false;
        for (int i = 0; i < scaleAdapter.getCount(); i++) {
            if (mScaleVal.equals(scaleAdapter.getItem(i))) {
                matched = true;
                break;
            }
        }
        if (!matched && !TextUtils.isEmpty(mScaleVal)) {
            scaleAdapter.insert(mScaleVal, mScaleSelectedPos);
        }

        matched = false;
        for (int i = 0; i < classAapter.getCount(); i++) {
            if (mClassVal.equals(classAapter.getItem(i))) {
                matched = true;
                break;
            }
        }
        if (!matched && !TextUtils.isEmpty(mClassVal)) {
            classAapter.insert(mClassVal, mClassSelectedPos);
        }
        spinnerScaleName.setSelection(mScaleSelectedPos, false);
        spinnerClassName.setSelection(mClassSelectedPos, false);
        switch (mScratchSelected) {
            case AppConst.NO_SCRATCH_BUILT:
                radioBtnNonScratch.setChecked(true);
                break;
            case AppConst.PARTIAL_SCRATCH_BUILT:
                radioBtnPartialScratch.setChecked(true);
                break;
            case AppConst.FULL_SCRATCH_BUILT:
                radioBtnFullScratch.setChecked(true);
                break;
        }
        etRegisterModelName.setText(mModelName);
        etRegisterGunplaName.setText(mGunplaName);
    }

    @Override
    protected void onPause() {
        super.onPause();

        adView2.pause();
    }

    @Override
    protected void onDestroy() {
        adView2.destroy();

        super.onDestroy();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selItem = (String) parent.getAdapter().getItem(position);

        if (parent.getAdapter().equals(scaleAdapter)) {
            mScaleSelectedPos = position;
            mScaleVal = selItem;
            isScaleSelected = true;
        } else if (parent.getAdapter().equals(classAapter)) {
            mClassSelectedPos = position;
            mClassVal = selItem;
            isScaleSelected = false;
        }

        if (selItem.equals(getString(R.string.other))) {
            // 選択アイテムが「その他」だった場合は、入力ダイアログを表示する
            NewItemDialog dialog = new NewItemDialog();
            dialog.show(getFragmentManager(), NewItemDialog.FRAGMENT_TAG);
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onInputComplered(String inputItem) {
        if (isScaleSelected) {
            scaleAdapter.insert(inputItem, mScaleSelectedPos);
            spinnerScaleName.setSelection(mScaleSelectedPos, false);
            scaleAdapter.notifyDataSetChanged();
        } else {
            classAapter.insert(inputItem, mClassSelectedPos);
            spinnerClassName.setSelection(mClassSelectedPos, false);
            classAapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    private GunplaInfo makeGunplaInfo(String tagId) {
        GunplaInfo info = new GunplaInfo();

        info.setBuilderName(etRegisterBuilderName.getText().toString());
        info.setFighterName(etRegisterFighterName.getText().toString());
        info.setClassValue((String) spinnerClassName.getSelectedItem());
        info.setScaleValue((String) spinnerScaleName.getSelectedItem());
        int scratch = 0;
        if (radioBtnNonScratch.isChecked()) {
            scratch = AppConst.NO_SCRATCH_BUILT;
        } else if (radioBtnPartialScratch.isChecked()) {
            scratch = AppConst.PARTIAL_SCRATCH_BUILT;
        } else if (radioBtnFullScratch.isChecked()) {
            scratch = AppConst.FULL_SCRATCH_BUILT;
        }
        info.setScratchBuiltLevel(scratch);
        info.setModelNo(etRegisterModelName.getText().toString());
        info.setGunplaName(etRegisterGunplaName.getText().toString());

        info.setTagId(tagId);

        return info;
    }
}
