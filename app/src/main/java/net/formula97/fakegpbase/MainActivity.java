package net.formula97.fakegpbase;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import net.formula97.fakegpbase.Databases.GunplaInfo;
import net.formula97.fakegpbase.Databases.GunplaInfoModel;
import net.formula97.fakegpbase.fragments.GunplaSelectionDialogs;
import net.formula97.fakegpbase.fragments.MessageDialogs;

import java.util.List;


public class MainActivity extends Activity
        implements MessageDialogs.DialogsButtonSelectionCallback,
        GunplaSelectionDialogs.OnGunplaSelectedListener {

    private TextView tvBuilderName;
    private TextView tvFighterName;
    private TextView tvScale;
    private TextView tvClass;
    private TextView tvModelNo;
    private TextView tvGunplaName;

    private boolean mAlreadyShown = false;
    private final String KeyAlreadyShown = this.getClass().getSimpleName() + "-KeyAlreadyShown";
    private final String KeyBuilderName = this.getClass().getSimpleName() + "-KeyBuilderName";
    private final String KeyFighterName = this.getClass().getSimpleName() + "-KeyFighterName";
    private final String KeyScaleVal = this.getClass().getSimpleName() + "-KeyScaleVal";
    private final String KeyClassVal = this.getClass().getSimpleName() + "-KeyClassVal";
    private final String KeyModelNo = this.getClass().getSimpleName() + "-KeyModelNo";
    private final String KeyGunplaName = this.getClass().getSimpleName() + "-KeyGunplaName";

    private String mBuilderName;
    private String mFighterName;
    private String mScaleVal;
    private String mClassVal;
    private String mModelNo;
    private String mGunplaName;

    private NfcAdapter mNfcAdapter;

    /**
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvBuilderName = (TextView) findViewById(R.id.tvBuilderName);
        tvFighterName = (TextView) findViewById(R.id.tvFighterName);
        tvScale = (TextView) findViewById(R.id.tvScale);
        tvClass = (TextView) findViewById(R.id.tvClass);
        tvModelNo = (TextView) findViewById(R.id.tvModelNo);
        tvGunplaName = (TextView) findViewById(R.id.tvGunplaName);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        initValiables();
    }

    /**
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent setting = new Intent(this, SettingActivity.class);
                startActivity(setting);
                return true;
            case R.id.action_register_gunpla:
                Intent register = new Intent(this, GunplaRegisterActivity.class);
                startActivity(register);
                return true;
            case R.id.action_select_gunpla:
                // ガンプラ選択のDialogFragmentを表示する
                GunplaInfoModel model = new GunplaInfoModel(this);
                List<GunplaInfo> gunplaInfoList = model.findAll();
                if (gunplaInfoList == null || gunplaInfoList.size() == 0) {
                    // データ無しとして処理を中止する
                    MessageDialogs dialogs = MessageDialogs.getInstance(
                            getString(R.string.dialgo_info),
                            getString(R.string.no_gunpla_registered),
                            MessageDialogs.BUTTON_POSITIVE);
                    dialogs.show(getFragmentManager(), MessageDialogs.FRAGMENT_TAG);
                } else {
                    GunplaSelectionDialogs d = GunplaSelectionDialogs.newInstance();
                    d.show(getFragmentManager(), GunplaSelectionDialogs.FRAGMENT_TAG);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    /**
     * @see net.formula97.fakegpbase.fragments.MessageDialogs.DialogsButtonSelectionCallback#onButtonPressed(String, int)
     */
    @Override
    public void onButtonPressed(String messageBody, int which) {
        if (messageBody.equals(getString(R.string.wish_to_enable))) {
            mAlreadyShown = true;

            if (which == MessageDialogs.PRESSED_POSITIVE) {
                // 設定画面をstartActivityする
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
                } else {
                    startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                }
            }
        } else if (messageBody.equals(getString(R.string.no_nfc_present))) {
            mAlreadyShown = true;
        }
    }

    /**
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();

        parseNfcIntgen(getIntent());

        setWidgetFromField();

        // NFCアダプターが有効でない場合は、設定画面を呼ぶか否かをDialogを表示する
        if (mNfcAdapter != null) {
            if (!mNfcAdapter.isEnabled() && !mAlreadyShown) {
                MessageDialogs dialogs = MessageDialogs.getInstance(
                        getString(R.string.dialgo_info),
                        getString(R.string.wish_to_enable),
                        MessageDialogs.BUTTON_BOTH
                );
                dialogs.show(getFragmentManager(), MessageDialogs.FRAGMENT_TAG);
            } else {
                // フォアグラウンドでのNFCタグ読み込みを開始
                Intent i = new Intent(this, this.getClass());
                i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(
                        getApplicationContext(), 0 ,i, PendingIntent.FLAG_UPDATE_CURRENT);
                mNfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
            }
        } else {
            if (!mAlreadyShown) {
                MessageDialogs dialogs = MessageDialogs.getInstance(
                        getString(R.string.dialog_warn),
                        getString(R.string.no_nfc_present),
                        MessageDialogs.BUTTON_POSITIVE);
                dialogs.show(getFragmentManager(), MessageDialogs.FRAGMENT_TAG);
            }
        }
    }

    /**
     * NFCタグの中身を解析する。
     *
     * @param intent
     */
    private void parseNfcIntgen(Intent intent) {
        // やってきたIntentがnullの場合はそのまま抜ける
        if (intent != null) {
            GunplaInfoModel model = new GunplaInfoModel(this);
            NfcUtils nfcUtils = new NfcUtils();
            NfcTextRecord record;
            GunplaInfo savedInfo;

            Parcelable readTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if (readTag == null) {
                return;
            }

            String action = intent.getAction();

            if (action.equals(NfcAdapter.ACTION_NDEF_DISCOVERED)) {
                // TXTレコードの取り出しを試みる
                record = nfcUtils.readTag(intent);
                if (record != null) {
                    // TXTレコードの取り出しに成功した場合は、ガンプラ情報取得を試みる
                    savedInfo = model.findGunplaInfoByTagId(record.getText());
                    if (savedInfo != null) {
                        setGunplaInfo(savedInfo);
                    }
                }
            }
        }
    }

    /**
     * フィールドの値をウィジェットに展開する。
     */
    private void setWidgetFromField() {
        tvBuilderName.setText(mBuilderName);
        tvFighterName.setText(mFighterName);
        tvClass.setText(mClassVal);
        tvScale.setText(mScaleVal);
        tvModelNo.setText(mModelNo);
        tvGunplaName.setText(mGunplaName);
    }

    /**
     * @see android.app.Activity#onPause()
     */
    @Override
    protected void onPause() {
        super.onPause();

        // NFCタグ読み込みを中止
        if (mNfcAdapter != null) {
            mNfcAdapter.disableForegroundDispatch(this);
        }
    }

    /**
     * @see android.app.Activity#onNewIntent(android.content.Intent)
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // FLAG_ACTIVITY_LAUNCHED_FROM_HISTORYが立っている時だけ処理
        if ((intent.getFlags() & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) == 0) {
            if (intent.getAction().equals(NfcAdapter.ACTION_NDEF_DISCOVERED)) {
                // 登録済みタグ（＝フォーマット済みNFCを含む）の場合のみ処理
                setIntent(intent);
            }
        }
    }

    /**
     * ガンプラ情報をフィールドに格納する。
     *
     * @param gunplaInfo
     */
    private void setGunplaInfo(GunplaInfo gunplaInfo) {
        setToFields(gunplaInfo);
        setViewFromEntity(gunplaInfo);
    }

    /**
     * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(KeyAlreadyShown, mAlreadyShown);

        outState.putString(KeyBuilderName, tvBuilderName.getText().toString());
        outState.putString(KeyFighterName, tvFighterName.getText().toString());
        outState.putString(KeyScaleVal, tvClass.getText().toString());
        outState.putString(KeyClassVal, tvClass.getText().toString());
        outState.putString(KeyModelNo, tvModelNo.getText().toString());
        outState.putString(KeyGunplaName, tvGunplaName.getText().toString());
    }

    /**
     * @see android.app.Activity#onRestoreInstanceState(android.os.Bundle)
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mAlreadyShown = savedInstanceState.getBoolean(KeyAlreadyShown);

        mBuilderName = savedInstanceState.getString(KeyBuilderName);
        mFighterName = savedInstanceState.getString(KeyFighterName);
        mScaleVal = savedInstanceState.getString(KeyScaleVal);
        mClassVal = savedInstanceState.getString(KeyClassVal);
        mModelNo = savedInstanceState.getString(KeyModelNo);
        mGunplaName = savedInstanceState.getString(KeyGunplaName);
    }

    /**
     * ガンプラ情報から画面情報保持メンバー変数に値を格納する。
     *
     * @param gunplaInfoEntity
     */
    private void setToFields(GunplaInfo gunplaInfoEntity) {
        mBuilderName = gunplaInfoEntity.getBuilderName();
        mFighterName = gunplaInfoEntity.getFighterName();
        mClassVal = gunplaInfoEntity.getClassValue();
        mScaleVal = gunplaInfoEntity.getScaleValue();
        mModelNo = gunplaInfoEntity.getModelNo();
        mGunplaName = gunplaInfoEntity.getGunplaName();
    }

    /**
     * Bundleに出入りする変数を初期化する。
     */
    private void initValiables() {
        mBuilderName = "";
        mFighterName = "";
        mScaleVal = "";
        mClassVal = "";
        mModelNo = "";
        mGunplaName = "";
    }

    @Override
    public void onGunplaSelected(GunplaInfo info) {
        setViewFromEntity(info);
    }

    /**
     * ローカルDBから取得したガンプラ情報を画面に反映する。
     *
     * @param info ローカルDBのガンプラ情報
     */
    private void setViewFromEntity(GunplaInfo info) {
        tvBuilderName.setText(info.getBuilderName());
        tvFighterName.setText(info.getFighterName());
        tvScale.setText(info.getScaleValue());
        tvClass.setText(info.getClassValue());
        tvModelNo.setText(info.getModelNo());
        tvGunplaName.setText(info.getGunplaName());
    }
}
