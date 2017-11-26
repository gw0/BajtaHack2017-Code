using Newtonsoft.Json;

namespace Server.JsonFormat
{
    public class Room
    {
        [JsonProperty("name")]
        public string Name { get; set; }

        [JsonProperty("url")]
        public string Url { get; set; }

        [JsonProperty("devices")]
        public Device[] Devices { get; set; }
    }
}