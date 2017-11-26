using Newtonsoft.Json;

namespace Server.JsonFormat
{
    public class GetResponse
    {
        [JsonProperty("value")]
        public string Value { get; set; }

        [JsonProperty("speech")]
        public string Speech { get; set; }
    }
}