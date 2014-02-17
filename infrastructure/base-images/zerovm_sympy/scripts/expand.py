#!/usr/bin/python

from sympy import *

x, y = symbols('x y')
expr = (x + y)**5
print expand(expr)
