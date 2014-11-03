package net.formula97.fakegpbase;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


public class GunplaRegisterActivity extends Activity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gunpla_register);

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
        adView2 = (AdView) findViewById(R.id.adView2);

        AdRequest adRequest = new AdRequest.Builder().build();
        adView2.loadAd(adRequest);
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
}
