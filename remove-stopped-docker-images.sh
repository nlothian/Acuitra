sudo docker ps -a | grep Exit | awk '{print $1}' | sudo xargs docker rm
