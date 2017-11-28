package srm.srmexample01;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import srm.srmlib.SRMClient;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {
    private static String LOG_TAG = "srm.srmexample01";

    private SRMClient mClient = null;
    private SRMClient mLed = null;
    private SRMClient mButton = null;
    private SRMClient mBuzzer = null;

    private EditText etSetupUrl;
    private EditText etSetupUsername;
    private EditText etSetupPassword;
    private EditText etLedGpio;
    private EditText etButtonGpio;
    private EditText etBuzzerPwm;
    private EditText etBuzzerPeriod;
    private Button btnSetupInit;
    private Button btnSetupReboot;
    private TextView tvLedState;
    private Button btnLedOn;
    private Button btnLedOff;
    private TextView tvButtonState;
    private Button btnButtonRefresh;
    private ImageView srmImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize main layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.layout_main).setOnTouchListener(this);

        // Initialize Setup component
        etSetupUrl = (EditText) findViewById(R.id.et_setup_url);
        etSetupUsername = (EditText) findViewById(R.id.et_setup_username);
        etSetupPassword = (EditText) findViewById(R.id.et_setup_password);
        etLedGpio = (EditText) findViewById(R.id.et_led_gpio);
        etButtonGpio = (EditText) findViewById(R.id.et_button_gpio);
        etBuzzerPwm = (EditText) findViewById(R.id.et_buzzer_pwm);
        etBuzzerPeriod = (EditText) findViewById(R.id.et_buzzer_period);
        srmImageView = (ImageView) findViewById(R.id.srm_image);

        View.OnClickListener setupListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SetupTask().execute(view.getId());
            }
        };
        btnSetupInit = (Button) findViewById(R.id.btn_setup_init);
        btnSetupInit.setOnClickListener(setupListener);
        btnSetupReboot = (Button) findViewById(R.id.btn_setup_reboot);
        btnSetupReboot.setOnClickListener(setupListener);

        // Initialize LED component
        View.OnClickListener ledListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new LedTask().execute(view.getId());
            }
        };
        tvLedState = (TextView) findViewById(R.id.tv_led_state);
        tvLedState.setOnClickListener(ledListener);
        btnLedOn = (Button) findViewById(R.id.btn_led_on);
        btnLedOn.setOnClickListener(ledListener);
        btnLedOff = (Button) findViewById(R.id.btn_led_off);
        btnLedOff.setOnClickListener(ledListener);

        // Initialize Button component
        View.OnClickListener buttonListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ButtonTask().execute(view.getId());
            }
        };
        tvButtonState = (TextView) findViewById(R.id.tv_button_state);
        tvButtonState.setOnClickListener(buttonListener);
        btnButtonRefresh = (Button) findViewById(R.id.btn_button_refresh);
        btnButtonRefresh.setOnClickListener(buttonListener);

        // Initialize Buzzer component
        View.OnClickListener buzzerListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new BuzzerTask().execute(view.getId());
            }
        };
//        tvBuzzerState = (TextView) findViewById(R.id.tv_buzzer_state);
//        tvBuzzerState.setOnClickListener(buzzerListener);
//        btnBuzzerOn = (Button) findViewById(R.id.btn_buzzer_on);
//        btnBuzzerOn.setOnClickListener(buzzerListener);
//        btnBuzzerOff = (Button) findViewById(R.id.btn_buzzer_off);
//        btnBuzzerOff.setOnClickListener(buzzerListener);

        // Initial state
        setAllButtonsEnabled(false);  // disable all buttons
        btnSetupInit.setEnabled(true);

        initPermissions();
    }

    // permissions has to be added programmatically because the definition in android manifest
    // is not enough in newer versions of Android (e.g. v. 6 and above)
    private void initPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                Log.d(LOG_TAG, "Permission..");
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        // Hide virtual keyboard on touch outside of text fields
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return false;
    }

    /**
     * Abstract handler for tasks.
     */
    private abstract class HandlerTask extends AsyncTask<Integer, Void, Boolean> {
        String data;
        Exception exception;

        @Override
        protected void onPreExecute() {
            this.data = getString(R.string.state_unknown);
            this.exception = null;
            setAllButtonsEnabled(false);  // disable all buttons
            String message = "Executing action...";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPostExecute(Boolean done) {
            if (done) {  // completed successfully
                setAllButtonsEnabled(true);  // enable all buttons again

            } else {  // something went wrong
                String message = "Something went wrong!\n" + String.valueOf(this.exception);
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                btnSetupInit.setEnabled(true);
            }
        }
    }

    /**
     * Handle tasks for Setup component.
     */
    protected class SetupTask extends HandlerTask {
        URL url;
        String username;
        String password;
        int ledGpio;
        int buttonGpio;
        int buzzerPwm;
        int buzzerPeriod;
        int httpsCheck;
        boolean verbose;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // Parameters
            try {
                this.url = new URL(etSetupUrl.getText().toString());
            } catch (MalformedURLException e) {
                e.getMessage();
            }
            this.username = etSetupUsername.getText().toString();
            this.password = etSetupPassword.getText().toString();
            this.ledGpio = Integer.parseInt(etLedGpio.getText().toString());
            this.buttonGpio = Integer.parseInt(etButtonGpio.getText().toString());
            this.buzzerPwm = Integer.parseInt(etBuzzerPwm.getText().toString());
            ;
            this.buzzerPeriod = Integer.parseInt(etBuzzerPeriod.getText().toString());
            this.httpsCheck = SRMClient.HTTPS_BASIC;
            this.verbose = true;

            // Set unknown states
            tvLedState.setText(getString(R.string.state_unknown));
            tvButtonState.setText(getString(R.string.state_unknown));
//            tvBuzzerState.setText(getString(R.string.state_unknown));
        }

        @Override
        protected Boolean doInBackground(Integer... viewIds) {
            String data;
            try {
                switch (viewIds[0]) {
                    case R.id.btn_setup_init:
                        Log.d(LOG_TAG, "Initialize...");

                        // Create SRM manager
//                        URL url = SRMClient.urlBuilder(this.url, null, this.username, this.password, null, null, null, null, null);
                        URL url = SRMClient.urlBuilder(this.url, this.url.getProtocol(), "", "", null, this.url.getPort(), null, null, null);
                        mClient = new SRMClient(url, this.httpsCheck, null, null, null, this.verbose);

                        // Reset configuration just in case
                        //mClient.reboot(true);

                        // Initialize LED light controller
                        Log.d(LOG_TAG, "Initialize LED light controller...");
                        mClient.post("/phy/gpio/alloc", String.valueOf(ledGpio));
                        data = "{\"dir\": \"out\", \"mode\": \"floating\", \"irq\": \"none\", \"debouncing\": 0}";
                        mClient.put(String.format("/phy/gpio/%d/cfg/value", ledGpio), data);

                        URL ledUrl = SRMClient.urlBuilder(url, null, username, password, null, null, String.format("/phy/gpio/%d/value", ledGpio), null, null);
                        mLed = new SRMClient(ledUrl, httpsCheck, null, null, null, verbose);

                        // Initialize button controller
                        Log.d(LOG_TAG, "Initialize button controller...");
                        mClient.post("/phy/gpio/alloc", String.valueOf(buttonGpio));
                        data = "{\"dir\": \"in\", \"mode\": \"floating\", \"irq\": \"none\", \"debouncing\": 0}";
                        mClient.put(String.format("/phy/gpio/%d/cfg/value", buttonGpio), data);

                        URL buttonUrl = SRMClient.urlBuilder(url, null, username, password, null, null, String.format("/phy/gpio/%d/value", buttonGpio), null, null);
                        mButton = new SRMClient(buttonUrl, httpsCheck, null, null, null, verbose);

                        SRMUtil.executeImageRetrieval("/sys/file/1/value", mClient, srmImageView, MainActivity.this, new MyCallback() {
                            @Override
                            public void executeActionOnFinish() {
                                // On finish make sound for 2 seconds :-)
                                try {
                                    new LedTask().execute(R.id.btn_led_on);
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } finally {
                                    new LedTask().execute(R.id.btn_led_off);
                                }
                            }
                        });

                        break;
                    case R.id.btn_setup_reboot:
                        Log.d(LOG_TAG, "Rebooting...");
                        mClient.reboot(true);
                        break;

                    default:
                        Log.e(LOG_TAG, "Unknown event!");
                        return false;
                }

            } catch (Exception e) {
                Log.e(LOG_TAG, "Exception: " + e.toString(), e);
                this.exception = e;
                return false;
            }
            return true;
        }
    }

    /**
     * Handle tasks for Led component.
     */
    private class LedTask extends HandlerTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            tvLedState.setText(this.data);  // set unknown state
        }

        @Override
        protected Boolean doInBackground(Integer... viewIds) {
            try {
                switch (viewIds[0]) {
                    case R.id.tv_led_state:
                        Log.d(LOG_TAG, "Refresh LED light state...");
                        this.data = mLed.get(null).content;
                        break;

                    case R.id.btn_led_on:
                        Log.d(LOG_TAG, "Turn LED light on...");
                        this.data = "1";
                        mLed.put(null, this.data);
                        break;

                    case R.id.btn_led_off:
                        Log.d(LOG_TAG, "Turn LED light off...");
                        this.data = "0";
                        mLed.put(null, this.data);
                        break;

                    default:
                        Log.e(LOG_TAG, "Unknown event!");
                        return false;
                }

            } catch (Exception e) {
                Log.e(LOG_TAG, "Exception: " + e.toString(), e);
                this.exception = e;
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean done) {
            super.onPostExecute(done);

            tvLedState.setText(this.data.trim());  // set new state
        }
    }

    /**
     * Handle tasks for Button component.
     */
    private class ButtonTask extends HandlerTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            tvButtonState.setText(this.data);  // set unknown state
        }

        @Override
        protected Boolean doInBackground(Integer... viewIds) {
            try {
                switch (viewIds[0]) {
                    case R.id.tv_button_state:
                    case R.id.btn_button_refresh:
                        Log.d(LOG_TAG, "Refresh button state...");
                        this.data = mButton.get(null).content;
                        break;

                    default:
                        Log.e(LOG_TAG, "Unknown event!");
                        return false;
                }

            } catch (Exception e) {
                Log.e(LOG_TAG, "Exception: " + e.toString(), e);
                this.exception = e;
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean done) {
            super.onPostExecute(done);

            tvButtonState.setText(this.data.trim());  // set new state
        }
    }

    /**
     * Handle tasks for Buzzer component.
     */
    private class BuzzerTask extends HandlerTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

//            tvBuzzerState.setText(this.data);  // set unknown state
        }

        @Override
        protected Boolean doInBackground(Integer... viewIds) {
            try {
                switch (viewIds[0]) {
//                    case R.id.tv_buzzer_state:
//                        Log.d(LOG_TAG, "Refresh buzzer state...");
//                        this.data = mBuzzer.get(null).content;
//                        break;
//
//                    case R.id.btn_buzzer_on:
//                        Log.d(LOG_TAG, "Turn buzzer on...");
//                        this.data = "300";
//                        mBuzzer.put(null, this.data);
//                        break;
//
//                    case R.id.btn_buzzer_off:
//                        Log.d(LOG_TAG, "Turn buzzer off...");
//                        this.data = "0";
//                        mBuzzer.put(null, this.data);
//                        break;

                    default:
                        Log.e(LOG_TAG, "Unknown event!");
                        return false;
                }

            } catch (Exception e) {
                Log.e(LOG_TAG, "Exception: " + e.toString(), e);
                this.exception = e;
                return false;
            }
//            return true;
        }

        @Override
        protected void onPostExecute(Boolean done) {
            super.onPostExecute(done);

//            tvBuzzerState.setText(this.data.trim());  // set new state
        }
    }

    private void setAllButtonsEnabled(boolean enabled) {
        btnSetupInit.setEnabled(enabled);
        btnSetupReboot.setEnabled(enabled);
        btnLedOn.setEnabled(enabled);
        btnLedOff.setEnabled(enabled);
        btnButtonRefresh.setEnabled(enabled);
        // btnBuzzerOn.setEnabled(enabled);
//        btnBuzzerOff.setEnabled(enabled);
    }
}
