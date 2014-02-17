# Python on ZeroVM on Docker

This Dockerfile will let you run Python inside a [ZeroVM](http://zerovm.org/) VM inside a [Docker](http://docker.io) container.

See also [Sympy on ZeroVM on Docker](https://github.com/nlothian/Acuitra/tree/master/infrastructure/base-images/zerovm_sympy)

## Why?

ZeroVM gives a fairly secure sandbox using the [Chromium NaCl](http://www.chromium.org/nativeclient) container, letting you run untrusted code with a degree of confidence

Docker gives a nice API 

## How?

### Pull

'''
sudo docker pull nlothian/zerovm_python
'''


### Build

ZeroVM has some dependencies which are not available via apt-get in Ubunutu. The build script will fetch them and build the Docker image


```
./build.sh
````

### Run

This runs the file helloworld.py, which is inserted into the Docker image at /opt/zerovm/helloworld.py

```
$  sudo docker run -t zerovm_python
Hello World
```

You can map a host directory to Docker, and then run python scripts from that

```
$sudo docker run -i -t -v /home/nick/Acuitra/infrastructure/base-images/zerovm-python/scripts/:/scripts/ zerovm_python @/scripts/ls.py /lib
['/lib/python2.7', '/lib/pkgconfig', '/lib/libpython2.7.a']
```

### Limitations

* This hasn't actually been security audited
* Could use AppArmor to limit network usage
* If you need other python libraries you'll need to package them separately and load them into ZeroVM. See [Sympy on ZeroVM on Docker](https://github.com/nlothian/Acuitra/tree/master/infrastructure/base-images/zerovm_sympy) for an example of this.
