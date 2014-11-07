package net.formula97.fakegpbase;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.gson.Gson;

import net.formula97.fakegpbase.Databases.GunplaInfo;
import net.formula97.fakegpbase.fragments.MessageDialogs;


public class MainActivity extends Activity implements MessageDialogs.DialogsButtonSelectionCallback {

    private TextView tvBuilderName;
    private TextView tvFighterName;
    private TextView tvScale;
    private TextView tvClass;
    private TextView tvModelNo;
    private TextView tvGunplaName;

    private boolean mAlreadyShown = false;
    private final String KeyAlreadyShown = "KeyAlreadyShown";
    private final String KeyGunplaInfoJson = "KeyGunplaInfoJson";

    private String mBuilderName;
    private String mFighterName;
    private String mScaleVal;
    private String mClassVal;
    private String mModelNo;
    private String mGunplaName;
    private String mGunplaInfoJson;
    private GunplaInfo gunplaInfo;

    protected NfcAdapter mNfcAdapter;

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
                // TODO ガンプラ選択のDialogFragmentを表示する処理を書く

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

        if ("android.nfc.action.NDEF_DISCOVERY".equals(intent.getAction())) {
            byte[] tagRawData = intent.getByteArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        }
    }

    /**
     * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(KeyAlreadyShown, mAlreadyShown);

        // ウィジェット類の値格納
        if (!TextUtils.isEmpty(mGunplaInfoJson)) {
            outState.putString(KeyGunplaInfoJson, mGunplaInfoJson);
        }
    }

    /**
     * @see android.app.Activity#onRestoreInstanceState(android.os.Bundle)
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mAlreadyShown = savedInstanceState.getBoolean(KeyAlreadyShown);

        // ウィジェット類の値復元
        mGunplaInfoJson = savedInstanceState.getString(KeyGunplaInfoJson);
        if (!TextUtils.isEmpty(mGunplaInfoJson)) {
            // JSONを保存していた場合は、JSONから変数の内容を復元する
            Gson gson = new Gson();
            gunplaInfo = gson.fromJson(mGunplaInfoJson, GunplaInfo.class);

            mBuilderName = gunplaInfo.getBuilderName();
            mFighterName = gunplaInfo.getFighterName();
            mClassVal = gunplaInfo.getClassValue();
            mScaleVal = gunplaInfo.getScaleValue();
            mModelNo = gunplaInfo.getModelNo();
            mGunplaName = gunplaInfo.getGunplaName();
        }
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
        mGunplaInfoJson = "";
    }
}
