from __future__ import print_function
import os
import urllib2
import platform

def download(retrieve,destination,force=False): 
    '''
    Feches a generic URL to disk, creating the directory if necessary
    @param retrieve - url to pull down
    @param destination - path to destination (including filename)
    '''
    if force or not os.path.exists(destination):
        print("Retrieving Kafka TarBall:",retrieve,"to",destination)
        url = urllib2.urlopen(retrieve)
        #Create temp area, if needed
        if not os.path.exists(os.path.dirname(destination)):
            print("Creating temp directory:",os.path.dirname(destination))
            os.mkdir(os.path.dirname(destination))
        with open(destination,"w") as out:
            out.write(url.read())
    else:
        print("File already exists at:",destination)
def getPackMan():
    '''
    Attempts to find package manager for os.
    @return - package manager install line
    '''
    dist = platform.linux_distribution()
    if dist is None:
        return None
    dist = dist[0].lower()
    if dist == "ubuntu":
        return "apt-get install -y"
    elif dist == "centos":
        return "yum install -y"
    return None
