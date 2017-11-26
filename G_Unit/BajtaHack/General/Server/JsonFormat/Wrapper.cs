using System;
using System.Collections.Generic;
using System.Text;

namespace Server.JsonFormat
{
    public class Wrapper
    {
        /* ============
         * TODO: dopolni
         * ============ */
        public enum DataType
        {
            Temperature,
            CO2,
            Humidity
        }

        public string SensorId { get; set; }
        public DataType Data { get; set; }
    }
}
