using System;
using System.IO;
using System.Net;
using System.Net.Http;
using System.Threading.Tasks;
using System.Net.Http.Headers;

using NLog;

using Newtonsoft.Json;

using Server.JsonFormat;

namespace Server
{
    public class Request
    {
        private static Logger logger = LogManager.GetCurrentClassLogger();

        public Request() { }

        public static async Task<GetResponse> PostCommand(string content)
        {
            return await Post(ConfigService.Config.ServerUrl + ConfigService.Config.Command, content);
        }

        public static async Task<GetResponse> Post(string url, string content)
        {
            try
            {
                HttpWebRequest Request = (HttpWebRequest) WebRequest.Create(url);
                Request.Method = "POST";
                // Request.ContentType = "application/json";

                // Send a request to the server
                using (var Stream = await Request.GetRequestStreamAsync())
                using (var Writer = new StreamWriter(Stream))
                {
                    await Writer.WriteAsync(content);
                }

                // Wait for a response from the server
                using (var Response = await Request.GetResponseAsync())
                using (var Reader = new StreamReader(Response.GetResponseStream()))
                {
                    string Res = await Reader.ReadToEndAsync();
                    return JsonConvert.DeserializeObject<GetResponse>(Res);
                }
            }
            catch (WebException ex)
            {
                logger.Error(ex, "WebException: " + ex.Message);
                return null;
            }
            catch (Exception ex)
            {
                logger.Error(ex, "Exception when trying to connect to server for posting: " + ex.Message);
                return null;
            }
        }

        public static async Task<string> Put(string url, string content)
        {
            try
            {
                HttpWebRequest Request = (HttpWebRequest) WebRequest.Create(url);
                Request.Method = "PUT";
                // Request.ContentType = "application/json";

                // Send a request to the server
                using (var Stream = await Request.GetRequestStreamAsync())
                using (var Writer = new StreamWriter(Stream))
                {
                    await Writer.WriteAsync(content);
                }

                // Wait for a response from the server
                using (var Response = await Request.GetResponseAsync())
                using (var Reader = new StreamReader(Response.GetResponseStream()))
                {
                    return await Reader.ReadToEndAsync();
                }
            }
            catch (WebException ex)
            {
                logger.Error(ex, "WebException: " + ex.Message);
                return null;
            }
            catch (Exception ex)
            {
                logger.Error(ex, "Exception when trying to connect to server for putting: " + ex.Message);
                return null;
            }
        }

        public static async Task<string> Get(string url)
        {
            try
            {
                // string url = ConfigService.Config.ServerUrl;
                logger.Debug("[GET] Getting server response from URL: " + url);

                HttpWebRequest Request = (HttpWebRequest) WebRequest.Create(url);
                Request.Method = "GET";

                // Wait for a response from the server
                using (var Response = await Request.GetResponseAsync())
                using (var Reader = new StreamReader(Response.GetResponseStream()))
                {
                    return await Reader.ReadToEndAsync();
                }
            }
            catch (WebException ex)
            {
                logger.Error(ex, "WebException: " + ex.Message);
                return null;
            }
            catch (Exception ex)
            {
                logger.Error(ex, "Exception when trying to connect to server for getting: " + ex.Message);
                return null;
            }
        }

        private string ObjectToJson(Wrapper RequestData)
        {
            /* ================================
             * TODO: save history to local DB
             * ================================ */
            string RequestDataString = JsonConvert.SerializeObject(RequestData);

            return RequestDataString;
        }
    }
}