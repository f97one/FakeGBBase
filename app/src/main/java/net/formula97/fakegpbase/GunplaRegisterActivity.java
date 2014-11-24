package net.formula97.fakegpbase;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import net.formula97.fakegpbase.Databases.GunplaInfo;
import net.formula97.fakegpbase.Databases.GunplaInfoModel;
import net.formula97.fakegpbase.fragments.GunplaSelectionDialogs;
import net.formula97.fakegpbase.fragments.MessageDialogs;
import net.formula97.fakegpbase.fragments.NewItemDialog;
import net.formula97.fakegpbase.fragments.WriteTagDialogs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;


public class GunplaRegisterActivity extends Activity implements AdapterView.OnItemSelectedListener,
        NewItemDialog.OnInputCompleteListener, WriteTagDialogs.OnTagOperationListener,
        MessageDialogs.DialogsButtonSelectionCallback, GunplaSelectionDialogs.OnGunplaSelectedListener {

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

    private String mBuilderName;
    private String mFighterName;
    private int mScaleSelectedPos;
    private String mScaleVal;
    private int mClassSelectedPos;
    private String mClassVal;
    private int mScratchSelected;
    private String mModelName;
    private String mGunplaName;
    private String mTagId;

    private NfcAdapter mNfcAdapter;
    private GunplaInfo mGunplaInfo;
    private ArrayAdapter<String> scaleAdapter;
    private ArrayAdapter<String> classAapter;
    private boolean isScaleSelected = true;
    private boolean stopTagRead = false;
    private Tag mReadTag;

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

        // NFC Adapterの初期化
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

    }

    /**
     * ViewIDの取得を行う。
     */
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

        // InputFilterの設置
        etRegisterBuilderName.setFilters(StringUtils.makeFilter(AppConst.INPUT_FILTER_ALL_CAPS_WITH_NUMBER, 0));
        etRegisterFighterName.setFilters(StringUtils.makeFilter(AppConst.INPUT_FILTER_ALL_CAPS_WITH_NUMBER, 0));
        etRegisterModelName.setFilters(StringUtils.makeFilter(AppConst.INPUT_FILTER_ALL_CAPS_WITH_NUMBER_AND_SYMBOLS, 0));
        etRegisterGunplaName.setFilters(StringUtils.makeFilter(AppConst.INPUT_FILTER_ALL_CAPS_WITH_NUMBER_AND_SYMBOLS, 0));
    }

    /**
     * 変数を初期化する。
     */
    private void initVariables() {
        // ビルダー名とファイター名の初期値はPreferenceの値とする
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

        mBuilderName = pref.getString(AppConst.PREF_KEY_BUILDER_NAME, "");
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
        mReadTag = null;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gunpla_register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        GunplaInfoModel model = new GunplaInfoModel(this);

        switch(item.getItemId()) {
            case R.id.action_submit:
                // SQLiteへのデータセーブ

                if (TextUtils.isEmpty(mTagId)) {
                    mTagId = AppConst.TAGLESS_TAG_ID_PREFIX + model.makeInitialTagId();
                }
                GunplaInfo entity = makeGunplaInfo(mTagId);
                model.save(entity);
                
                return true;
            case R.id.action_write_tag:
                // NFCタグ書き込みのダイアログを出す
                // タグ読み込み中止フラグを立てる
                stopTagRead = true;
                if (mNfcAdapter != null && mNfcAdapter.isEnabled()) {
                    WriteTagDialogs writeTagDialogs = new WriteTagDialogs();
                    writeTagDialogs.show(getFragmentManager(), WriteTagDialogs.FRAGMENT_TAG);
                } else {
                    Toast.makeText(this, getString(R.string.nfc_not_enabled), Toast.LENGTH_LONG).show();
                    stopTagRead = false;
                }
                return true;
            case R.id.action_reset_view:
                // UIの値を初期化する
                MessageDialogs dialogs = MessageDialogs.getInstance(
                        getString(R.string.dialgo_info),
                        getString(R.string.confirm_init_data),
                        MessageDialogs.BUTTON_BOTH);
                dialogs.show(getFragmentManager(), MessageDialogs.FRAGMENT_TAG);
                return true;

            case R.id.action_edit:
                // データの有無を確認する
                List<GunplaInfo> gunplaInfoList = model.findAll();
                if (gunplaInfoList == null || gunplaInfoList.size() == 0) {
                    // データ無しとして処理を中止する
                    MessageDialogs d1 = MessageDialogs.getInstance(
                            getString(R.string.dialgo_info),
                            getString(R.string.no_gunpla_registered),
                            MessageDialogs.BUTTON_POSITIVE);
                    d1.show(getFragmentManager(), MessageDialogs.FRAGMENT_TAG);
                } else {
                    GunplaSelectionDialogs d = GunplaSelectionDialogs.newInstance();
                    d.show(getFragmentManager(), GunplaSelectionDialogs.FRAGMENT_TAG);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        adView2.resume();

        // IntentからNFCの読み取り結果を取り出して処理
        parseNfcIntent(getIntent());

        // フィールドの値をUIへ反映する
        setDataToWidgets();

        // NFCの読み込みを有効にする
        if (mNfcAdapter != null && mNfcAdapter.isEnabled()) {
            Intent i = new Intent(this, this.getClass());
            i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    getApplicationContext(), 0 ,i, PendingIntent.FLAG_UPDATE_CURRENT);
            mNfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
        }

    }

    /**
     * IntentからNFCに関する情報を取り出して処理する。
     * @param intent Activityが受け取ったIntent
     */
    private void parseNfcIntent(Intent intent) {

        // やってきたIntentがnullの場合はそのまま抜ける
        if (intent == null) {
            return;
        } else {
            GunplaInfoModel model = new GunplaInfoModel(this);
            String initialTagId = model.makeInitialTagId();
            NfcUtils nfcUtils = new NfcUtils();
            NfcTextRecord record = null;
            GunplaInfo savedInfo = null;

            mReadTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if (mReadTag == null) {
                return;
            }

            String action = intent.getAction();

            if (action.equals(NfcAdapter.ACTION_NDEF_DISCOVERED)) {
                if (stopTagRead) {
                    // 書き込みモードの処理
                    // 一旦タグを読み込み
                    record = nfcUtils.readTag(intent);
                    if (record != null) {
                        savedInfo = model.findGunplaInfoByTagId(record.getText());
                        if (savedInfo != null) {
                            // 「すでにNFCタグが他のデータを持っているが、上書きしてよいか？」と確認する
                            MessageDialogs dialogs = MessageDialogs.getInstance(
                                    getString(R.string.dialog_warn),
                                    getString(R.string.confirm_write_tag),
                                    MessageDialogs.BUTTON_BOTH);
                            dialogs.show(getFragmentManager(), MessageDialogs.FRAGMENT_TAG);
                        }
                    } else {
                        // TXTレコードがないので書き込みを行う
                        writeToTag(initialTagId, nfcUtils);
                    }

                } else {
                    // 読み込みモード
                    record = nfcUtils.readTag(intent);
                    if (record != null) {
                        savedInfo = model.findGunplaInfoByTagId(record.getText());
                        setInfoToFields(savedInfo);
                        setInfoToWidgets(savedInfo);
                    }

                }
            } else if (isValidTag(action) && stopTagRead) {
                // 未使用タグ、かつ書き込みモードの場合は、そのまま書き込み処理に入る
                writeToTag(initialTagId, nfcUtils);
            }
        }

    }

    /**
     * フィールドにセットされた値をUIへ反映する。
     */
    private void setDataToWidgets() {
        etRegisterBuilderName.setText(mBuilderName);
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

        if (mNfcAdapter != null) {
            mNfcAdapter.disableForegroundDispatch(this);
        }
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
    public void onInputCompleted(String inputItem) {
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

        String action = intent.getAction();
        if ((intent.getFlags() & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) == 0) {
            if (action.equals(NfcAdapter.ACTION_NDEF_DISCOVERED)) {
                // 書き込みまで行うか否かは、onResume()で判断させる
                setIntent(intent);
            } else if (isValidTag(action) && stopTagRead) {
                // 未使用タグ、かつ書き込みモードの場合は、そのまま書き込み処理に入るが、
                // 実際の処理はonResumeへ回す
                setIntent(intent);
            }
        }
    }

    private boolean isValidTag(String action) {
        return action.equals(NfcAdapter.ACTION_TECH_DISCOVERED)
                || action.equals(NfcAdapter.ACTION_TAG_DISCOVERED);
    }

    /**
     * NFCタグに書き込む。
     *
     * @param initialTagId NFCタグに書き込むタグID
     * @param utils NfcUtilsのインスタンス
     */
    private void writeToTag(String initialTagId, NfcUtils utils) {
        if (mReadTag == null) {
            return;
        }

        mTagId = initialTagId;
        NdefRecord[] ndefRecords = new NdefRecord[]{
                utils.toNdefRecord(Locale.JAPANESE.getLanguage(), mTagId, true)
        };
        NdefMessage ndefMessage = new NdefMessage(ndefRecords);
        utils.writeTag(mReadTag, ndefMessage, wroteCompCallbacks);
    }

    /**
     * ウィジェットの入力値から、ガンプラ情報を作成する。
     *
     * @param tagId NFCタグに書き込んでいるタグID
     * @return ガンプラ情報のインスタンス
     */
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

    /**
     * ガンプラ情報を画面へ展開する。
     *
     * @param info 取得したガンプラ情報
     */
    private void setInfoToWidgets(GunplaInfo info) {
        mTagId = info.getTagId();

        etRegisterBuilderName.setText(info.getBuilderName());
        etRegisterFighterName.setText(info.getFighterName());
        // スクラッチビルド
        switch (info.getScratchBuiltLevel()) {
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
        etRegisterModelName.setText(info.getModelNo());
        etRegisterGunplaName.setText(info.getGunplaName());

        // スケールとグレードは選択肢にない場合がある
        List<String> scales = new ArrayList<>(
                Arrays.asList(getResources().getStringArray(R.array.scale_list)));
        List<String> grades = new ArrayList<>(
                Arrays.asList(getResources().getStringArray(R.array.class_list)));

        if (!scales.contains(info.getScaleValue())) {
            mScaleSelectedPos = scales.size() - 1;
            isScaleSelected = true;
            onInputCompleted(info.getScaleValue());
        } else {
            spinnerScaleName.setSelection(scales.indexOf(info.getScaleValue()), false);
        }
        if (grades.contains(info.getClassValue())) {
            mClassSelectedPos = grades.size() - 1;
            isScaleSelected = false;
            onInputCompleted(info.getClassValue());
        } else {
            spinnerClassName.setSelection(grades.indexOf(info.getClassValue()), false);
        }
    }

    /**
     * 取得したガンプラ情報をフィールドに展開する。
     *
     * @param info 取得したガンプラ情報
     */
    private void setInfoToFields(GunplaInfo info) {
        mTagId = info.getTagId();

        mBuilderName = info.getBuilderName();
        mFighterName = info.getFighterName();
        mModelName = info.getModelNo();
        mScaleVal = info.getScaleValue();
        mClassVal = info.getClassValue();
        mScratchSelected = info.getScratchBuiltLevel();
        mGunplaName = info.getGunplaName();
    }

    @Override
    public void onTagOperation(int operationCode) {
        String logtag = this.getClass().getSimpleName() + "#onTagOperation";
        String result = "";
        switch (operationCode) {
            case WriteTagDialogs.OPERATION_SUCCESS:
                result = "OPERATION_SUCCESS";
                break;
            case WriteTagDialogs.OPERATION_FAILED:
                result = "OPERATION_FAILED";
                break;
            case WriteTagDialogs.OPERATION_CANCELED:
                result = "OPERATION_CANCELED";
                break;
        }

        Log.i(logtag, "Received : " + result);

        // タグ読み込み中止を取りやめ
        stopTagRead = false;
    }

    private NfcUtils.OnWroteCallback wroteCompCallbacks = new NfcUtils.OnWroteCallback() {
        @Override
        public void onSuccess() {
            GunplaInfo info = makeGunplaInfo(mTagId);
            GunplaInfoModel model = new GunplaInfoModel(GunplaRegisterActivity.this);
            model.save(info);

            DialogFragment f = (DialogFragment) getFragmentManager().findFragmentByTag(WriteTagDialogs.FRAGMENT_TAG);
            if (f != null) {
                f.dismiss();
            }

            mTagId = "";
        }

        @Override
        public void onFailed(String errorMessage, Throwable e) {
            DialogFragment f = (DialogFragment) getFragmentManager().findFragmentByTag(WriteTagDialogs.FRAGMENT_TAG);
            if (f != null) {
                f.dismiss();
            }

            Toast.makeText(GunplaRegisterActivity.this, errorMessage, Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public void onButtonPressed(String messageBody, int which) {
        if (messageBody.equals(getString(R.string.confirm_init_data))) {
            if (which == MessageDialogs.PRESSED_POSITIVE) {
                // 表示を初期化する
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                etRegisterBuilderName.setText(preferences.getString(AppConst.PREF_KEY_BUILDER_NAME, ""));
                etRegisterFighterName.setText(preferences.getString(AppConst.PREF_KEY_FIGHTER_NAME, ""));
                spinnerClassName.setSelection(0, false);
                spinnerScaleName.setSelection(0, false);
                radioBtnNonScratch.setChecked(true);
                etRegisterModelName.setText("");
                etRegisterGunplaName.setText("");

                // タグIDを初期化
                mTagId = "";
            }
        }

        if (messageBody.equals(getString(R.string.confirm_write_tag))) {
            if (which == MessageDialogs.PRESSED_POSITIVE) {
                // 書き込みを行う
                GunplaInfoModel model = new GunplaInfoModel(this);
                // 初期タグIDの生成
                String initialTagId = model.makeInitialTagId();
                writeToTag(initialTagId, new NfcUtils());
            }
        }
    }

    @Override
    public void onGunplaSelected(GunplaInfo info) {
        setInfoToFields(info);
        setInfoToWidgets(info);
    }
}
