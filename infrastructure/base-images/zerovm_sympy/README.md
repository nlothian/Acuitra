# sympy on ZeroVM on Docker

This Docker image lets you run [sympy](sympy.org) inside a [ZeroVM](http://zerovm.org/) VM inside a [Docker](http://docker.io) container.

See [ZeroVM-Python](https://github.com/nlothian/Acuitra/tree/master/infrastructure/base-images/zerovm-python) for some reasoning behind this. 

Importantly, for sympy usage this gives you some protection against malicious code.

## How?

### Pull

'''
sudo docker pull nlothian/zerovm_sympy
'''


### Build

'''
sudo docker build -t zerovm_sympy .
'''

### Run

This runs the file testsympy.py, which is inserted into the Docker image at /opt/zerovm/testsympy.py

```
$sudo docker run -t zerovm_sympy
x*(x + 2*y)
```

You can map a host directory to Docker, and then run python scripts from that

```
$sudo docker run -i -t -v /home/nick/Acuitra/infrastructure/base-images/zerovm_sympy/scripts/:/scripts/ zerovm_sympy @/scripts/expand.py
x**5 + 5*x**4*y + 10*x**3*y**2 + 10*x**2*y**3 + 5*x*y**4 + y**5
```


