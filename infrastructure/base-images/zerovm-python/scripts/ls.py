#!/usr/bin/python

import glob
import sys

if len(sys.argv) == 1:
    dir = ""
else:
    dir = sys.argv[1]

print glob.glob(dir + "/*")
