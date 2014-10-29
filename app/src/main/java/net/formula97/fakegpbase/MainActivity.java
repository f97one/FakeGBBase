package net.formula97.fakegpbase;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import net.formula97.fakegpbase.fragments.MessageDialogs;


public class MainActivity extends Activity implements MessageDialogs.DialogsButtonSelectionCallback {

    private TextView tvBuilderName;
    private TextView tvFighterName;
    private TextView tvScale;
    private TextView tvClass;
    private TextView tvModelNo;
    private TextView tvGunplaName;

    protected NfcAdapter mNfcAdapter;

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
        // NFCアダプターが有効でない場合は、設定画面を呼ぶか否かをDialogを表示する
        if (mNfcAdapter != null) {
            if (!mNfcAdapter.isEnabled()) {
                MessageDialogs dialogs = MessageDialogs.getInstance(
                        getString(R.string.dialgo_info),
                        getString(R.string.wish_to_enable),
                        MessageDialogs.BUTTON_BOTH
                );
                dialogs.show(getFragmentManager(), MessageDialogs.FRAGMENT_TAG);
            }
        } else {
            MessageDialogs dialogs = MessageDialogs.getInstance(
                    getString(R.string.dialog_warn),
                    getString(R.string.no_nfc_present),
                    MessageDialogs.BUTTON_POSITIVE);
            dialogs.show(getFragmentManager(), MessageDialogs.FRAGMENT_TAG);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            Intent i = new Intent(this, SettingActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onButtonPressed(String messageBody, int which) {
        if (messageBody.equals(getString(R.string.wish_to_enable)) &&
                which == MessageDialogs.PRESSED_POSITIVE) {
            // 設定画面をstartActivityする
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
            } else {
                startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
            }
        }
    }
}
