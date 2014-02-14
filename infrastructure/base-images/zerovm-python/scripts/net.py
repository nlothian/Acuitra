import socket

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)                 

s.connect(("www.example.com" , 80))
s.sendall("GET http://www.example.com HTTP/1.0\n\n") 
print s.recv(4096)
s.close    
