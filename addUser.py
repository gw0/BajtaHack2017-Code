import requests
import socket
import select
import arpreq

server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
serverPort = 20170
server_socket.bind(('', serverPort))
server_socket.listen(1)





read_list = [server_socket]
while(1):

    try:
        readable, writable, errored = select.select(read_list, [], [])
        for s in readable:
            if s is server_socket:
                client_socket, address = server_socket.accept()
                read_list.append(client_socket)
                print "Connection from", address , arpreq.arpreq(str(address[0]))
                
                
                
            else:
                data = s.recv(1024)
                
                #print data 
                if data:
                    #s.send(data)
    
                    
                    try:
                        data = data.split('\n')[0]
                        
                        if "|" in data:
                            raw_data = data.split('|')
                        elif "%7C" in data:
                            raw_data = data.split('%7C')

                        data = ""
                        
                        print raw_data
                        html = "-"
                        
                        if len(raw_data) > 0:    
                            for x in range(1,len(raw_data)-1):
                            
                                TCPdata = (raw_data[x]).split('=')
                                
                        

                        
                        
                    except Exception, e:
                        print("DATA PARSING ERROR: {0}".format(e))
                        
                    try:
                        s.send('HTTP/1.0 200 OK\r\n')
                        s.send('Access-Control-Allow-Origin: *\r\n')
                        s.send("Content-Type: text/html\r\n\r\n")
                        #s.send('<html><body>'+str(fb.replace("\n","<br>"))+'</body></html>')
                        s.send("blabla")
                        
                        s.close()
                        read_list.remove(s)
                    
                    except Exception, e:
                        print("SENDING ERROR: {0}".format(e))
 
    except KeyboardInterrupt:
        print 'Interrupted!'
        break
