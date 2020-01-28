
last = 0
l = []
n=1
for line in open('foo').read().split("\n"):
  #print(line)
  if 'DEBUG' in line  or 'WARN' in line or 'INFO' in line:
    if ':' not in line[0:5]: continue
  
    l.append(line[6:]) 
    #print(line[0:5])
    timeh, timem = line[0:5].split(':')
    #print(timeh, timem)
    time_min = int(timeh) * 60 + int(timem)
    #print(time_min)
    #if 'WARN' in line: print(time_min - last ) 
    
    if 'WARN' in line and (time_min - last > 4): 
      fname = 'trace'+str(n)
      with open(fname, 'w') as f: f.write("\n".join(l))
      l = []
      n+=1
      print(fname, line)
    
    if 'WARN' in line: 
      last = time_min
      
    #last = 
    #print(time_min)
    
