using System;

using Android.OS;
using Android.App;
using Android.Speech;
using Android.Widget;
using Android.Content;
using Android.Speech.Tts;

using NLog;

using Newtonsoft.Json;

using Server.JsonFormat;

namespace BajtaHack
{
    [Activity(Label = "G-Unit", MainLauncher = true)]
    public class MainActivity : Activity, TextToSpeech.IOnInitListener
    {
        #region Logger
        private static Logger logger = NLog.LogManager.GetCurrentClassLogger();
        #endregion

        #region Voice Request / Recognition
        private bool IsRecording = false;
        private int VoiceActivityId = 10;
        #endregion

        #region Rooms
        private string RoomsJson = "";
        private Room[] Rooms;
        #endregion

        #region Text-To-Speech
        private TextToSpeech Speaker;
        private string ToSpeak;
        #endregion

        protected override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);
            logger.Trace("MainActivity");

            // Set our view from the "main" layout resource
            SetContentView(Resource.Layout.Main);

            // Fetch all the rooms and devices from the server
            HandleRooms();

            // Set up the speech to text interface
            HandleSpeechToText();

            // Sets up the view event handlers
            HandleView();
        }

        /// <summary>
        /// Fetch all the rooms and devices from the server
        /// </summary>
        private async void HandleRooms()
        {
            /*Rooms = new Room[]
            {
                new Room()
                {
                    Name = "bedroom",
                    Devices = new Device[]
                    {
                        new Device()
                        {
                            Keyword = "light",
                            Name = "Light",
                            DataType = "toggle"
                        },
                        new Device()
                        {
                            Keyword = "temperature",
                            Name = "Temperature",
                            DataType = "slider"
                        }
                    }
                },
                new Room()
                {
                    Name = "kitchen",
                    Devices = new Device[]
                    {
                        new Device()
                        {
                            Keyword = "blinds",
                            Name = "Blinds",
                            DataType = "toggle"
                        },
                        new Device()
                        {
                            Keyword = "light",
                            Name = "Light",
                            DataType = "toggle"
                        }
                    }
                }
            };

            // RoomsJson = JsonConvert.SerializeObject(Rooms); */

            try
            {
                RoomsJson = await Server.Request.Get(ConfigService.Config.ServerUrl + "rooms");
                Rooms = JsonConvert.DeserializeObject<Room[]>(RoomsJson);
            }
            catch (Exception e)
            {
                logger.Error("Exception when handling rooms: " + e.Message);
            }
        }

        /// <summary>
        /// Sets up the speech to text interface
        /// </summary>
        private void HandleSpeechToText()
        {
            // Find the record btn and set up the recording
            var RecordBtn = FindViewById(Resource.Id.RecordButton);
            string rec = Android.Content.PM.PackageManager.FeatureMicrophone;

            // Check if the device has a microphone
            if (rec != "android.hardware.microphone")
            {
                logger.Warn("The device has no microphone suitable for voice recognition.");
                RecordBtn.Enabled = false;
            }
            else
            {
                logger.Debug("The device has a microphone for voice recognition.");
                RecordBtn.Enabled = true;

                RecordBtn.Click += delegate
                {
                    logger.Debug("Clicked on voice recording button for voice recognition.");

                    IsRecording = !IsRecording;
                    if (IsRecording)
                    {
                        // Create the intent and start the voice recognition activity
                        var voiceIntent = new Intent(RecognizerIntent.ActionRecognizeSpeech);
                        voiceIntent.PutExtra(RecognizerIntent.ExtraLanguageModel, RecognizerIntent.LanguageModelFreeForm);
                        
                        // voiceIntent.PutExtra(RecognizerIntent.ExtraPrompt, "Speak now");
                        
                        // Consider the voice command to be over if there is 1.5s of silence.
                        voiceIntent.PutExtra(RecognizerIntent.ExtraSpeechInputCompleteSilenceLengthMillis, 1500);
                        voiceIntent.PutExtra(RecognizerIntent.ExtraSpeechInputPossiblyCompleteSilenceLengthMillis, 1500);
                        // voiceIntent.PutExtra(RecognizerIntent.ExtraSpeechInputMinimumLengthMillis, 15000);
                        voiceIntent.PutExtra(RecognizerIntent.ExtraMaxResults, 1);
                        
                        // Set the recognition language to local default (english)
                        voiceIntent.PutExtra(RecognizerIntent.ExtraLanguage, Java.Util.Locale.Default);

                        // Start the activity
                        StartActivityForResult(voiceIntent, VoiceActivityId);
                    }
                };
            }
        }

        /// <summary>
        /// Handles all view-specific things
        /// </summary>
        private void HandleView()
        {
            TextView Title = FindViewById<TextView>(Resource.Id.TitleTextName);

            // User name goes here (fetch it from phone info?):
            Title.Text = "Janez Novak!";

            // The button that leads to detail view
            var DetailViewBtn = FindViewById(Resource.Id.DetailView);
            DetailViewBtn.Click += delegate
            {
                logger.Debug("Switching to detail view.");

                /* ===========================
                 * TODO: prevent memory leak
                 * =========================== */

                if (RoomsJson != null)
                {
                    Intent intent = new Intent(this, typeof(DetailViewActivity));
                    intent.PutExtra("rooms", RoomsJson);
                    this.StartActivity(intent);
                }
                else
                {
                    TextView ResText = FindViewById<TextView>(Resource.Id.ResponseText);
                    ResText.Text = string.Format("Error: No details to view!");
                    ResText.SetTextColor(new Android.Graphics.Color(255, 50, 50));
                }
            };

            // The button that refreshes the rooms & devices list
            var RefreshBtn = FindViewById(Resource.Id.RefreshBtn);
            RefreshBtn.Click += delegate
            {
                HandleRooms();
            };

            // Toggle voice response
            ToggleButton VoiceToggleBtn = FindViewById<ToggleButton>(Resource.Id.VoiceToggle);
            VoiceToggleBtn.Checked = ConfigService.Config.TextToSpeechEnabled;
            VoiceToggleBtn.Click += delegate
            {
                ConfigService.Config.TextToSpeechEnabled = VoiceToggleBtn.Checked;

                if (VoiceToggleBtn.Checked)
                    VoiceToggleBtn.SetBackgroundColor(new Android.Graphics.Color(15, 130, 23));
                else
                    VoiceToggleBtn.SetBackgroundColor(new Android.Graphics.Color(178, 26, 26));
            };
        }

        /// <summary>
        /// Initiates the Android speaker to read the provided string
        /// </summary>
        /// <param name="Text"></param>
        private void TextToSpeech(string Text)
        {
            ToSpeak = Text;
            logger.Debug("Speaking: " + Text);
            if (Speaker == null)
                Speaker = new TextToSpeech(this, this);

            Speaker.Speak(Text, QueueMode.Flush, null, null);
        }

        /// <summary>
        /// Called upon Android Speaker initialization
        /// </summary>
        /// <param name="status"></param>
        public void OnInit(OperationResult status)
        {
            if (status.Equals(OperationResult.Success))
            {
                logger.Trace("Android speaker init");
                Speaker.Speak(ToSpeak, QueueMode.Flush, null, null);
            }
        }

        /// <summary>
        /// Handle activity results - speech to text
        /// </summary>
        /// <param name="requestCode"></param>
        /// <param name="resultVal"></param>
        /// <param name="data"></param>
        protected async override void OnActivityResult(int requestCode, Result resultVal, Intent data)
        {
            // Call the base function
            base.OnActivityResult(requestCode, resultVal, data);

            // Check if the activity in question is the voice activity
            if (requestCode == VoiceActivityId)
            {
                // Check if the result has a suitable status code
                if (resultVal == Result.Ok)
                {
                    // Get the suitable matches
                    var matches = data.GetStringArrayListExtra(RecognizerIntent.ExtraResults);
                    if (matches.Count != 0)
                    {
                        // Voice has been recognized
                        System.Diagnostics.Debug.WriteLine("Said: " + matches[0]);

                        FindViewById<TextView>(Resource.Id.RequestText).Visibility = Android.Views.ViewStates.Visible;
                        FindViewById<TextView>(Resource.Id.RequestText).Text = "You said: " + matches[0];

                        GetResponse ServerResponse = null;
                        string Formatted = matches[0].ToLower();

                        // Hacked - turning off all lights
                        if (Formatted.StartsWith("all") && (Formatted.EndsWith("off") || Formatted.EndsWith("on")))
                            foreach (Room _room in Rooms)
                            {
                                int index1 = Formatted.IndexOf(' ');
                                int index2 = Formatted.LastIndexOf(' ');

                                string Request = _room.Name + " light" + Formatted.Substring(index2, Formatted.Length - index2);
                                ServerResponse = await Server.Request.PostCommand(Request);
                            }
                        else
                            ServerResponse = await Server.Request.PostCommand(Formatted);

                        if (ServerResponse != null)
                        {
                            // Text to speech if enabled
                            if (ConfigService.Config.TextToSpeechEnabled && ServerResponse != null)
                                TextToSpeech(ServerResponse.Speech);

                            // Update the info labels
                            TextView ResText = FindViewById<TextView>(Resource.Id.ResponseText);
                            ResText.Text = string.Format("Response: " + ServerResponse.Value);
                            ResText.SetTextColor(new Android.Graphics.Color(100, 100, 100));
                            ResText.Visibility = Android.Views.ViewStates.Visible;
                        }
                        else
                        {
                            // Something went wrong
                            TextView ResText = FindViewById<TextView>(Resource.Id.ResponseText);
                            ResText.Text = string.Format("Something went wrong.");
                            ResText.SetTextColor(new Android.Graphics.Color(255, 50, 50));
                            ResText.Visibility = Android.Views.ViewStates.Visible;
                        }
                    }
                    else
                        System.Diagnostics.Debug.WriteLine("No speech was recognised");
                }
            }
        }
    }
}