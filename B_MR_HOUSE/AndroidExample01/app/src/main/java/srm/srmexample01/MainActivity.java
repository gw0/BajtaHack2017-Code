package srm.srmexample01;

import android.app.job.JobScheduler;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import srm.srmlib.SRMClient;

public class MainActivity extends AppCompatActivity  {
    private static String LOG_TAG = "srm.srmexample01";

    private SRMClient mClientB1 = null;
    private SRMClient mClientB3 = null;
    private SRMClient mClientLed1 = null;
    private SRMClient mClientLed2 = null;
    private SRMClient mClientLed3 = null;
    private SRMClient mClientLed4 = null;
    private SRMClient mClientLed5 = null;
    private SRMClient mClientLed6 = null;
    private SRMClient mButton = null;
    private SRMClient mClientTempHumI2C = null;

    private TextView tvLedState;
    private Button btnLedOn;
    private Button btnLedOff;
    private Button btnLed2On;
    private Button btnLed3Off;
    private Button btnLed4On;
    private Button btnLed3On;
    private Button btnLed2Off;
    private Button btnLed5On;
    private Button btnLed5Off;
    private Button btnUpperPlus;
    private Button btnUpperMinus;
    private Button btnLowerPlus;
    private Button btnLowerMinus;
    private Button btnLed4Off;
    private Button btnLed6On;
    private Button btnLed6Off;

    private TextView tvLed2State;
    private String urlModuleB1="https://b1.srm.bajtahack.si:56100/";
    private String urlModuleB3="https://b3.srm.bajtahack.si:56300/";
    private TextView tvLed3State;
    private TextView tvLed4State;
    private TextView tvLed5State;

    private TextView tvLed6State;
    private Timer mTimer;

    private TextView tvUpperBoundState;
    private TextView tvLowerBoundState;
    private TextView tvTempState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize main layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Initialize Setup component
        new SetupTask().execute();



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
        
         tvLed2State = (TextView) findViewById(R.id.tv_led2_state);
         tvLed2State.setOnClickListener(ledListener);
         btnLed2On = (Button) findViewById(R.id.btn_led2_on);
         btnLed2On.setOnClickListener(ledListener);
         btnLed2Off = (Button) findViewById(R.id.btn_led2_off);
         btnLed2Off.setOnClickListener(ledListener);

         tvTempState = (TextView)findViewById(R.id.tv_temp_title);

         tvLed3State = (TextView) findViewById(R.id.tv_led3_state);
         tvLed3State.setOnClickListener(ledListener);
         btnLed3On = (Button) findViewById(R.id.btn_led3_on);
         btnLed3On.setOnClickListener(ledListener);
         btnLed3Off = (Button) findViewById(R.id.btn_led3_off);
         btnLed3Off.setOnClickListener(ledListener);

        tvLed4State = (TextView) findViewById(R.id.tv_led4_state);
        tvLed4State.setOnClickListener(ledListener);
        btnLed4On = (Button) findViewById(R.id.btn_led4_on);
        btnLed4On.setOnClickListener(ledListener);
        btnLed4Off = (Button) findViewById(R.id.btn_led4_off);
        btnLed4Off.setOnClickListener(ledListener);

        tvLed5State = (TextView) findViewById(R.id.tv_led5_state);
        tvLed5State.setOnClickListener(ledListener);
        btnLed5On = (Button) findViewById(R.id.btn_led5_on);
        btnLed5On.setOnClickListener(ledListener);
        btnLed5Off = (Button) findViewById(R.id.btn_led5_off);
        btnLed5Off.setOnClickListener(ledListener);

        tvLed6State = (TextView) findViewById(R.id.tv_led6_state);
        tvLed6State.setOnClickListener(ledListener);
        btnLed6On = (Button) findViewById(R.id.btn_led6_on);
        btnLed6On.setOnClickListener(ledListener);
        btnLed6Off = (Button) findViewById(R.id.btn_led6_off);
        btnLed6Off.setOnClickListener(ledListener);


        tvUpperBoundState = (TextView) findViewById(R.id.tv_upper_bound_state);
        btnUpperPlus = (Button) findViewById(R.id.btn_upper_bound_plus);
        btnUpperPlus.setOnClickListener(upperLowerListener);
        btnUpperMinus = (Button) findViewById(R.id.btn_upper_bound_minus);
        btnUpperMinus.setOnClickListener(upperLowerListener);

        tvLowerBoundState = (TextView) findViewById(R.id.tv_lower_bound_state);
        btnLowerPlus = (Button) findViewById(R.id.btn_lower_bound_plus);
        btnLowerPlus.setOnClickListener(upperLowerListener);
        btnLowerMinus = (Button) findViewById(R.id.btn_lower_bound_minus);
        btnLowerMinus.setOnClickListener(upperLowerListener);


        tvUpperBoundState.setText("30");
        tvLowerBoundState.setText("20");
        // Initial state
    }


    View.OnClickListener upperLowerListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_upper_bound_plus:
                    double val1 = Double.parseDouble(tvUpperBoundState.getText().toString());
                    val1 += 1;
                    tvUpperBoundState.setText(""+ val1);
                    break;
                case R.id.btn_upper_bound_minus:
                    double val2 = Double.parseDouble(tvUpperBoundState.getText().toString());
                    val2 -= 1;
                    tvUpperBoundState.setText(""+ val2);
                    break;
                case R.id.btn_lower_bound_plus:
                    double val3 = Double.parseDouble(tvLowerBoundState.getText().toString());
                    val3 += 1;
                    tvLowerBoundState.setText(""+ val3);
                    break;
                case R.id.btn_lower_bound_minus:
                    double val4 = Double.parseDouble(tvLowerBoundState.getText().toString());
                    val4 -= 1;
                    tvLowerBoundState.setText(""+ val4);
                    break;
            }
        }
    };
 
 
 
 
 
 

    /** Abstract handler for tasks. */
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

            }
        }
    }

    /** Handle tasks for Setup component. */
    private class SetupTask extends HandlerTask {
        URL url;
        String username;
        String password;
        int ledGpio1;
        int ledGpio2;
        int ledGpio3;
        int ledGpio4;
        int ledGpio5;
        int ledGpio6;
        int buttonGpio;
        int tmphumI2C;

        int httpsCheck;
        boolean verbose;



        @Override
        protected void onPreExecute() {
      
            this.username = "" ;
            this.password = "" ;
            this.ledGpio1 = 17 ;
            this.ledGpio2 = 23 ;
            this.ledGpio3 = 18 ;
            this.ledGpio4 = 27 ;
            this.ledGpio5 = 21 ;
            this.ledGpio6 = 22 ;
            this.buttonGpio = 24;
            this.tmphumI2C = 1;



            this.httpsCheck = SRMClient.HTTPS_BASIC;
            this.verbose = true;



        }

        @Override
        protected Boolean doInBackground(Integer... viewIds) {
            String data;
            try {
                Log.d(LOG_TAG, "Initialize...");

                        // Create SRM manager
                        URL urlB1 = SRMClient.urlBuilder(new URL(urlModuleB1), null, this.username, this.password, null, null, null, null, null);
                        URL urlB3 = SRMClient.urlBuilder(new URL (urlModuleB3), null, this.username, this.password, null, null, null, null, null);

                        // Reset configuration just in case
                        //mClientB1.reboot(true);

                        // Initialize LED light controller
                        Log.d(LOG_TAG, "Initialize LED light controller...");

                        URL ledUrl = SRMClient.urlBuilder(urlB3, null, username, password, null, null, String.format("/phy/gpio/%d/value", ledGpio1), null, null);
                        mClientLed1 = new SRMClient(ledUrl, httpsCheck, null, null, null, verbose);

                        URL ledUrl2 = SRMClient.urlBuilder(urlB3, null, username, password, null, null, String.format("/phy/gpio/%d/value", ledGpio2), null, null);
                        mClientLed2 = new SRMClient(ledUrl2, httpsCheck, null, null, null, verbose);

                        URL ledUrl3 = SRMClient.urlBuilder(urlB3, null, username, password, null, null, String.format("/phy/gpio/%d/value", ledGpio3), null, null);
                        mClientLed3 = new SRMClient(ledUrl3, httpsCheck, null, null, null, verbose);

                        URL ledUrl4 = SRMClient.urlBuilder(urlB3, null, username, password, null, null, String.format("/phy/gpio/%d/value", ledGpio4), null, null);
                        mClientLed4 = new SRMClient(ledUrl4, httpsCheck, null, null, null, verbose);

                        URL ledUrl5 = SRMClient.urlBuilder(urlB3, null, username, password, null, null, String.format("/phy/gpio/%d/value", ledGpio5), null, null);
                        mClientLed5 = new SRMClient(ledUrl5, httpsCheck, null, null, null, verbose);

                        URL ledUrl6 = SRMClient.urlBuilder(urlB3, null, username, password, null, null, String.format("/phy/gpio/%d/value", ledGpio6), null, null);
                        mClientLed6 = new SRMClient(ledUrl6, httpsCheck, null, null, null, verbose);


                        Log.d(LOG_TAG, "Initialize button controller...");

                        URL buttonUrl = SRMClient.urlBuilder(urlB1, null, username, password, null, null, String.format("/phy/gpio/%d/value", buttonGpio), null, null);
                        mButton = new SRMClient(buttonUrl, httpsCheck, null, null, null, verbose);

                        // Initialize buzzer controller
                        Log.d(LOG_TAG, "Initialize buzzer controller...");

                        URL temphumI2CUrl= SRMClient.urlBuilder(urlB1, null, username, password, null, null, String.format("/phy/i2c/%d/slaves/%d/value", tmphumI2C,64), null, null);
                        mClientTempHumI2C = new SRMClient(temphumI2CUrl, httpsCheck, null, null, null, verbose);

                        mTimer= new Timer();
                        mTimer.scheduleAtFixedRate(new TimerTask() {
                            @Override
                            public void run()
                            {
                                //read temp senzor
                                try {
                                    String value =  mClientTempHumI2C.get(null).content;
                                    value = value.replace("\"","" ).trim();
                                    Log.d("aplikacija","teperatura je " + value);
                                    int temp = (int) Long.parseLong(value, 16);
                                    Log.d("aplikacija","teperatura je " + temp);
                                    double tempre = ((temp/65536.0 ) * 165 )- 40 ;
                                    final double tempreFinal = tempre;
                                    Log.d("aplikacija","teperatura je " + tempre +"C");
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            tvTempState.setText(String.format("%.2f °C", tempreFinal));
                                        }
                                    });

                                    double upperBound = Double.parseDouble(tvUpperBoundState.getText().toString());
                                    double lowerBound = Double.parseDouble(tvLowerBoundState.getText().toString());

                                    if(tempre > upperBound) {
                                        // AC on
                                        mClientLed2.put(null, "0");
                                        mClientLed1.put(null, "1");
                                    } else if(tempre < lowerBound) {
                                        mClientLed2.put(null, "1");
                                        mClientLed1.put(null, "0");
                                    } else {
                                        mClientLed2.put(null, "0");
                                        mClientLed1.put(null, "0");
                                    }

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        },0,1500);





            } catch (Exception e) {
                Log.e(LOG_TAG, "Exception: " + e.toString(), e);
                this.exception = e;
                return false;
            }
            return true;
        }
    }

    /** Handle tasks for Led component. */
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
                    //led 1
                    case R.id.tv_led_state:
                        Log.d(LOG_TAG, "Refresh LED light state...");
                        this.data = mClientLed1.get(null).content;
                        break;

                    case R.id.btn_led_on:
                        Log.d(LOG_TAG, "Turn LED light on...");
                        this.data = "1";
                        mClientLed1.put(null, this.data);
                        break;

                    case R.id.btn_led_off:
                        Log.d(LOG_TAG, "Turn LED light off...");
                        this.data = "0";
                        mClientLed1.put(null, this.data);
                        break;
                        //led 2

                        case R.id.tv_led2_state:
                            Log.d(LOG_TAG, "Refresh LED light state...");
                            this.data = mClientLed2.get(null).content;
                            break;

                        case R.id.btn_led2_on:
                            Log.d(LOG_TAG, "Turn LED light on...");
                            this.data = "1";
                            mClientLed2.put(null, this.data);
                            break;

                        case R.id.btn_led2_off:
                            Log.d(LOG_TAG, "Turn LED light off...");
                            this.data = "0";
                            mClientLed2.put(null, this.data);
                            break;

                            //led 3
                     case R.id.tv_led3_state:
                         Log.d(LOG_TAG, "Refresh LED light state...");
                         this.data = mClientLed3.get(null).content;
                         break;

                     case R.id.btn_led3_on:
                         Log.d(LOG_TAG, "Turn LED light on...");
                         this.data = "1";
                         mClientLed3.put(null, this.data);
                         break;

                     case R.id.btn_led3_off:
                         Log.d(LOG_TAG, "Turn LED light off...");
                         this.data = "0";
                         mClientLed3.put(null, this.data);
                         break;

                    //led 4
                    case R.id.tv_led4_state:
                        Log.d(LOG_TAG, "Refresh LED light state...");
                        this.data = mClientLed4.get(null).content;
                        break;

                    case R.id.btn_led4_on:
                        Log.d(LOG_TAG, "Turn LED light on...");
                        this.data = "1";
                        mClientLed4.put(null, this.data);
                        break;

                    case R.id.btn_led4_off:
                        Log.d(LOG_TAG, "Turn LED light off...");
                        this.data = "0";
                        mClientLed4.put(null, this.data);
                        break;

                    //led 5
                    case R.id.tv_led5_state:
                        Log.d(LOG_TAG, "Refresh LED light state...");
                        this.data = mClientLed5.get(null).content;
                        break;

                    case R.id.btn_led5_on:
                        Log.d(LOG_TAG, "Turn LED light on...");
                        this.data = "1";
                        mClientLed5.put(null, this.data);
                        break;

                    case R.id.btn_led5_off:
                        Log.d(LOG_TAG, "Turn LED light off...");
                        this.data = "0";
                        mClientLed5.put(null, this.data);
                        break;

                    //led 6
                    case R.id.tv_led6_state:
                        Log.d(LOG_TAG, "Refresh LED light state...");
                        this.data = mClientLed6.get(null).content;
                        break;

                    case R.id.btn_led6_on:
                        Log.d(LOG_TAG, "Turn LED light on...");
                        this.data = "1";
                        mClientLed6.put(null, this.data);
                        break;

                    case R.id.btn_led6_off:
                        Log.d(LOG_TAG, "Turn LED light off...");
                        this.data = "0";
                        mClientLed6.put(null, this.data);
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

    private void setAllButtonsEnabled(boolean enabled) {
        btnLedOn.setEnabled(enabled);
        btnLed2On.setEnabled(enabled);
        btnLed3Off.setEnabled(enabled);
        btnLed4On.setEnabled(enabled);
        btnLed3On.setEnabled(enabled);
        btnLed2Off.setEnabled(enabled);
        btnLed5On.setEnabled(enabled);
        btnLed5Off.setEnabled(enabled);
        btnUpperPlus.setEnabled(enabled);
        btnUpperMinus.setEnabled(enabled);
        btnLowerPlus.setEnabled(enabled);
        btnLowerMinus.setEnabled(enabled);
        btnLed4Off.setEnabled(enabled);
        btnLed6On.setEnabled(enabled);
        btnLedOff.setEnabled(enabled);
        btnLed6Off.setEnabled(enabled);
    }
}
