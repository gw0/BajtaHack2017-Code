using Android.OS;
using Android.App;
using Android.Widget;
using Android.Content;

using Server.JsonFormat;

using Newtonsoft.Json;

using NLog;

namespace BajtaHack
{
    [Activity(Label = "BajtaHack")]
    public class DeviceDetailViewActivity : Activity, SeekBar.IOnSeekBarChangeListener
    {
        #region Logger
        Logger logger = LogManager.GetCurrentClassLogger();
        #endregion

        #region Other vars
        private TextView _textView;
        private Device _device;
        private int SeekBarStartTrack;
        #endregion

        public DeviceDetailViewActivity() { }

        protected override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);

            logger.Trace("DeviceDetailViewActivity");
            
            // Set our view from the "main detail" layout resource
            SetContentView(Resource.Layout.DeviceDetail);

            // Gets the focused device info through the intent passed by the previous activity
            string DeviceJson = Intent.GetStringExtra("device");
            _device = JsonConvert.DeserializeObject<Device>(DeviceJson);

            // Handles everything view-specific
            HandleView();
            
            // Handles the data passed from last activity
            HandlePassedData();
        }

        /// <summary>
        /// Handles all the data passed from the past activity
        /// </summary>
        private async void HandlePassedData()
        {
            LinearLayout Layout = FindViewById<LinearLayout>(Resource.Id.DetailLinearLayout);
            Layout.SetPadding(20, 20, 20, 20);

            // Device name
            TextView _textViewRoom = new TextView(this);
            _textViewRoom.Text = _device.Name;
            _textViewRoom.SetTypeface(null, Android.Graphics.TypefaceStyle.Bold);
            _textViewRoom.SetTextSize(Android.Util.ComplexUnitType.Sp, 24);

            Layout.AddView(_textViewRoom);
            
            switch (_device.DataType)
            {
                case "slider":
                    // Device data
                    SeekBar _seekBar = new SeekBar(this);
                    _seekBar.SetMinimumWidth(1000);

                    _textView = new TextView(this);
                    _textView.SetMinimumWidth(1000);

                    float value = 0;
                    _seekBar.Progress = (int) value;
                    string Response = (await Server.Request.PostCommand(Intent.GetStringExtra("room") + " " + _device.Keyword)).Value;
                    
                    float.TryParse(Response.Substring(1, Response.Length-3), out value);

                    _seekBar.Progress = (int) value;
                    _textView.Text = string.Format("Value: {0}", (int) value);

                    // Assign this class as a listener for the SeekBar events
                    _seekBar.SetOnSeekBarChangeListener(this);
                    
                    Layout.AddView(_seekBar);
                    Layout.AddView(_textView);
                    break;
                case "toggle":
                    // Device data
                    ToggleButton _toggleButton = new ToggleButton(this);

                    string _room = Intent.GetStringExtra("room");
                    string CheckString = (await Server.Request.PostCommand(string.Format("{0} {1}", _room, _device.Keyword))).Value;
                    _toggleButton.Checked = (CheckString.Equals("on"));

                    _toggleButton.CheckedChange += async delegate
                    {
                        // Post to server
                        string Toggle = (_toggleButton.Checked) ? "on" : "off";

                        string Formatted = string.Format("{0} {1} {2}", _room, _device.Keyword, Toggle);
                        logger.Debug("Sending put to server: " + Formatted);
                        await Server.Request.PostCommand(Formatted);
                    };

                    Layout.AddView(_toggleButton);
                    break;
            }
        }

        /// <summary>
        /// Called when the seek-bar progress changes
        /// </summary>
        /// <param name="seekBar"></param>
        /// <param name="progress"></param>
        /// <param name="fromUser"></param>
        public void OnProgressChanged(SeekBar seekBar, int progress, bool fromUser)
        {
            if (fromUser)
            {
                _textView.Text = string.Format("Value: {0}", progress);
            }
        }

        /// <summary>
        /// Called when seek-bar tracking starts
        /// </summary>
        /// <param name="seekBar"></param>
        public void OnStartTrackingTouch(SeekBar seekBar)
        {
            SeekBarStartTrack = seekBar.Progress;
        }

        /// <summary>
        /// Called when seek-bar tracking stops
        /// </summary>
        /// <param name="seekBar"></param>
        public async void OnStopTrackingTouch(SeekBar seekBar)
        {
            EditText et = new EditText(this);
            AlertDialog.Builder ad = new AlertDialog.Builder(this);
            ad.SetTitle("Enter password");
            ad.SetView(et);

            // OK button
            ad.SetPositiveButton("OK", async (c, ev) =>
            {
                if (et.Text.Equals(ConfigService.Config.Password))
                {
                    logger.Debug("Password accepted");
                    // Send put to server to regulate data
                    string Formatted = string.Format("{0} {1} {2}", Intent.GetStringExtra("room"), _device.Keyword, seekBar.Progress);
                    logger.Debug("Sending put to server: " + Formatted);
                    await Server.Request.Put(ConfigService.Config.ServerUrl + ConfigService.Config.Command, Formatted);
                }
                else
                {
                    seekBar.Progress = SeekBarStartTrack;
                    logger.Debug("Password rejected");
                }
            });

            // Cancel button
            ad.SetNegativeButton("Cancel", (c, ev) =>
            {
                seekBar.Progress = SeekBarStartTrack;
                logger.Debug("Clicked cancel");
            });
            ad.Show();
        }

        /// <summary>
        /// Handles everything view-specific
        /// </summary>
        private void HandleView()
        {
            // Back button
            var BackBtn = FindViewById(Resource.Id.BackButton);
            BackBtn.Click += delegate
            {
                logger.Debug("Switching to detail view.");

                /* ===========================
                 * TODO: prevent memory leak
                 * =========================== */

                Intent intent = new Intent(this, typeof(DetailViewActivity));
                intent.PutExtra("rooms", Intent.GetStringExtra("rooms"));
                this.StartActivity(intent);
            };
        }
    }
}