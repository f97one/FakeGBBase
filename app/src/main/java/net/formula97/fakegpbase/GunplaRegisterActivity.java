package net.formula97.fakegpbase;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
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
import net.formula97.fakegpbase.fragments.NewItemDialog;

import java.lang.reflect.Array;
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

    private GunplaInfo mGunplaInfo;
    private ArrayAdapter<String> scaleAdapter;
    private ArrayAdapter<String> classAapter;
    private boolean isScaleSelected = true;

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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        adView2.resume();

        etRegisterBuilderName.setText(mBuiderName);
        etRegisterFighterName.setText(mFighterName);
        spinnerScaleName.setSelection(mScaleSelectedPos, false);
        spinnerClassName.setSelection(mClassSelectedPos, false);
        // RadioButtonは、先に全てオフにする
        radioBtnNonScratch.setChecked(false);
        radioBtnPartialScratch.setChecked(false);
        radioBtnFullScratch.setChecked(false);
        switch (mScratchSelected) {
            case AppConst.NO_SCRATCH_BUILT:
                radioBtnNonScratch.setChecked(true);
                break;
            case AppConst.PATIAL_SCRATCH_BUILT:
                radioBtnPartialScratch.setChecked(true);
                break;
            case AppConst.FULL_SCRTATCH_BUILT:
                radioBtnFullScratch.setChecked(true);
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
}
