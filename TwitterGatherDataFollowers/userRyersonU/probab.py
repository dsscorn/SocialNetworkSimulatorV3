import random
import subprocess
import sys
import numpy as np

keys = sys.argv[1]
values = sys.argv[2]
alGorith = sys.argv[3]
print("The type of keys is: ",type(keys))
print("The type of values is: ", type(values))
print("keys: ", keys)
print("values: ", values)
keys = keys.split(", ")
values = values.split(", ")
#values = [elem.strip('[').strip(']').split(', ') for elem in values]
values = [elem.replace("]","").replace("[","") for elem in values]

#print(values[0].strip('['))
print(values[0])
#values = values.strip('[').strip(']')
#[float(i) for i in values]


n = len(keys)

print("Algorithm is:", alGorith)

if  alGorith == "4":
   values[0] = float(values[0]) - 50
   #print("Success Sepidehhh")
   print("float(values[0]): ",float(values[0]))

choices = random.choices(
  population=[keys[0].strip('['), keys[1], keys[2].strip(']')],
  weights=[float(values[0]), float(values[1]), float(values[2])],
  k=1
)

print("Choices: ", choices)

path = "important-stuff/"

f = open(path + "outputChoice.txt","w+")
f.writelines(choices)
f.close()



"""
subprocess.call(["java", "-jar", "-Xdebug",
                 "-Xrunjdwp:transport=dt_socket,address=8000,server=y",
                 "/home/raffaele/hello.jar"])

for i in range(1, 10):
    print(i)
"""


							