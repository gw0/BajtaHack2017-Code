using System.Collections.Generic;

using Android.OS;
using Android.App;
using Android.Widget;
using Android.Content;

using Newtonsoft.Json;

using Server.JsonFormat;

using NLog;

namespace BajtaHack
{
    [Activity(Label = "BajtaHack")]
    public class DetailViewActivity : Activity
    {
        #region Logger
        private Logger logger = LogManager.GetCurrentClassLogger();
        #endregion

        #region Expendable list view
        ExpandableListAdapter listAdapter;
        ExpandableListView expListView;
        List<string> listDataHeader;
        Dictionary<string, List<string>> listDataChild;
        int previousGroup = -1;
        #endregion

        #region Rooms
        private Room[] Rooms;
        #endregion

        public DetailViewActivity() { }

        protected override void OnCreate(Bundle savedInstanceState)
        {
            logger.Trace("DetailViewActivity");

            base.OnCreate(savedInstanceState);

            // Set our view from the "main detail" layout resource
            SetContentView(Resource.Layout.MainDetail);

            // Handles the data passed from last activity
            HandlePassedData();
            
            // Handles the view
            HandleView();

            // Expandable list
            expListView = FindViewById<ExpandableListView>(Resource.Id.lvExp);
            listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
            expListView.SetAdapter(listAdapter);
            FnClickEvents();
        }
        
        void FnClickEvents()
        {
            // Listening to child item selection
            expListView.ChildClick += delegate (object sender, ExpandableListView.ChildClickEventArgs e)
            {
                // Navigate to child (device detail view)
                Intent intent = new Intent(this, typeof(DeviceDetailViewActivity));

                List<string> ChildList = new List<string>();
                string clickedChild = "";
                if (listDataChild.TryGetValue(listDataHeader[previousGroup], out ChildList))
                    clickedChild = ChildList[e.ChildPosition];

                List<Device> AllDevices = new List<Device>();
                foreach (Room _room in Rooms)
                    foreach (Device _device in _room.Devices)
                        AllDevices.Add(_device);

                Device DeviceObject = AllDevices.Find(f => f.Name.Equals(clickedChild));
                string DeviceJson = JsonConvert.SerializeObject(DeviceObject);
                intent.PutExtra("device", DeviceJson);
                intent.PutExtra("room", listDataHeader[previousGroup]);
                intent.PutExtra("rooms", JsonConvert.SerializeObject(Rooms));
                this.StartActivity(intent);
            };

            //Listening to group expand
            //modified so that on selection of one group other opened group has been closed
            expListView.GroupExpand += delegate (object sender, ExpandableListView.GroupExpandEventArgs e) {

                if (e.GroupPosition != previousGroup)
                    expListView.CollapseGroup(previousGroup);
                previousGroup = e.GroupPosition;
            };

            //Listening to group collapse
            expListView.GroupCollapse += delegate (object sender, ExpandableListView.GroupCollapseEventArgs e) {
                // Toast.MakeText(this, "group collapsed", ToastLength.Short).Show();
            };

        }

        /// <summary>
        /// Handles all the data passed from the past activity
        /// </summary>
        private void HandlePassedData()
        {
            string RoomsJson = Intent.GetStringExtra("rooms");
            Rooms = JsonConvert.DeserializeObject<Room[]>(RoomsJson);
            
            ExpandableListView ELV = FindViewById<ExpandableListView>(Resource.Id.lvExp);

            listDataHeader = new List<string>();
            listDataChild = new Dictionary<string, List<string>>();

            int RoomIndex = 0;
            foreach (Room _room in Rooms)
            {
                listDataHeader.Add(_room.Name);
                var lstCS = new List<string>();
                foreach (Device _device in _room.Devices)
                {
                    lstCS.Add(_device.Name);
                }

                listDataChild.Add(listDataHeader[RoomIndex], lstCS);
                RoomIndex++;
            }
            
            if (RoomsJson.Length == 0)
                logger.Warn("No passed data to handle!");
        }

        /// <summary>
        /// Handles all view-specific things
        /// </summary>
        private void HandleView()
        {
            // Back button
            var BackBtn = FindViewById(Resource.Id.BackButton);
            BackBtn.Click += delegate
            {
                logger.Debug("Switching to simple view.");

                /* ===========================
                 * TODO: prevent memory leak
                 * =========================== */

                Intent intent = new Intent(this, typeof(MainActivity));
                this.StartActivity(intent);
            };

            // Turns off all lights
            Button ToggleBtnOff = FindViewById<Button>(Resource.Id.AllLightsToggleOff);
            ToggleBtnOff.Click += async delegate
            {
                logger.Debug("Switched all lights off.");
                
                foreach (Room _room in Rooms)
                {
                    string Request = _room.Name + " light off";
                    await Server.Request.PostCommand(Request);
                }
            };

            // Turns on all lights
            Button ToggleBtnOn = FindViewById<Button>(Resource.Id.AllLightsToggleOn);
            ToggleBtnOn.Click += async delegate
            {
                logger.Debug("Switched all lights on.");

                foreach (Room _room in Rooms)
                {
                    string Request = _room.Name + " light on";
                    await Server.Request.PostCommand(Request);
                }
            };
        }
    }
}