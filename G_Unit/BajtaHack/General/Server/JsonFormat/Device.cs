using System.Collections.Generic;

using Newtonsoft.Json;

namespace Server.JsonFormat
{
    public class Device
    {
        [JsonProperty("pin")]
        public string Pin { get; set; }

        [JsonProperty("keyword")]
        public string Keyword { get; set; }

        [JsonProperty("name")]
        public string Name { get; set; }

        [JsonProperty("dataType")]
        public string DataType { get; set; }
    }
}
