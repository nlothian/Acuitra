#!/bin/bash


wget -nc http://launchpadlibrarian.net/107759734/libpgm-5.1-0_5.1.118-1~dfsg-0.1ubuntu1_amd64.deb
wget -nc http://packages.zerovm.org/apt/ubuntu/pool/main/z/zeromq3/libzmq3_4.0.1-ubuntu1_amd64.deb
wget -nc http://packages.zerovm.org/zerovm-samples/python.tar

sudo docker build -t zerovm_python .
