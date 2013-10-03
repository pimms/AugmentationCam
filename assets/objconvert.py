from numpy import *
import random
import sys

#util to unravel
indexies = dict()
counter = -1
def get_index(key) :
 global indexies, counter
 if key not in indexies :
  counter += 1
  indexies[key] = counter
  return [False, counter]
 else :
  return [True, indexies[key]]

#do the loading of the obj file
def convert_obj(filename) :
 V = [] #vertex
 T = [] #texcoords
 N = [] #normals
 F = [] #face indexies

 fh = open(filename)
 for line in fh :
  if line[0] == '#' : continue
 
  line = line.strip().split(' ')
  if line[0] == 'v' : #vertex
   V.append(line[1:])
  elif line[0] == 'vt' : #tex-coord
   T.append(line[1:])
  elif line[0] == 'vn' : #normal vector
   N.append(line[1:])
  elif line[0] == 'f' : #face
   face = line[1:]
   
   if (len(face) != 4) :
    if len(face) == 3 : 
      face.append(face[-1])
    else:
      continue
   for i in range(0, len(face)) :
    face[i] = face[i].split('/')
    # OBJ indexies are 1 based not 0 based hence the -1
    # convert indexies to integer
    for j in range(0, len(face[i])) : face[i][j] = int(face[i][j]) - 1
   F.append(face)
  
 #Now we lay out all the vertex/texcoord/normal data into a flat array
 #and try to reuse as much as possible using a hash key
  
 V2 = []
 T2 = []
 N2 = []
 C2 = []
 F2 = []

 for face in F :
  for index in face :
   #print V[index[0]], T[index[1]], N[index[2]]
   key = '%s%s%s%s%s' % (V[index[0]][0], V[index[0]][1], V[index[0]][2], T[index[1]][0], T[index[1]][1])
   idx = get_index(key)
  
   if not idx[0] :
    V2.append([float(V[index[0]][0]), float(V[index[0]][1]), float(V[index[0]][2])])
    T2.append([float(T[index[1]][0]), float(T[index[1]][1])])
    N2.append([float(N[index[2]][0]), float(N[index[2]][1]), float(N[index[2]][2])])
    C2.append([random.random(), random.random(), random.random()])

   F2.append(idx[1])
 
 print len(V) * 3 * 4, 'bytes compared to', len(V2) * 3 * 4, 'bytes'
 
 #return numpy arrays
 #return [
 # array(V2, dtype=float32), 
 # array(T2, dtype=float32), 
 # array(N2, dtype=float32), 
 # array(C2, dtype=float32), 
 # array(F2, dtype=uint32)
 #]

 out = open("modfile.obj", 'w')

 # Write vertices
 for v in V2:
  out.write("v " + " ".join([str(x) for x in v]) + "\n")

 # Write texture coordinates
 for t in T2:
  out.write("vt " + " ".join([str(x) for x in t]) + "\n")

 # Write normals
 for n in N2:
  out.write("vn " + " ".join([str(x) for x in n]) + "\n")

 # Write faces
 i = 0
 for f in F2:
  if i == 0:
    out.write("f ")
  out.write(str(f) + " ")
  
  i += 1
  if i == 4:
    out.write("\n")
    i = 0

convert_obj(sys.argv[1])
