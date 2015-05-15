from __future__ import print_function

import os
import types
import urllib2
import urlparse

#Fabric imports
from fabric.operations import sudo,run
from fabric.contrib.project import rsync_project
from fabric.contrib.files import exists

#Local imports
import utilities

def packages(packages, command=utilities.getPackMan()):
    '''
    @param packages - list of packages to install
    @param command - (Optional) command called to install packages 
    '''
    if command is None:
        print("Please install the following packages manually:","\n\t".join(packages))
        return
    sudo(command+" "+" ".join(packages))

def setup(mirror,url,temp="/tmp/downloads/"):
    '''
    Performs necessary setup for deployment.
    @param mirror - Apache download mirror
    @param url -  tarball url
    @param temp - (Optional) Temp directory used for downloading
    '''
    tarball=url if not isinstance(url,types.GeneratorType) else url.next()
    destination=os.path.join(temp,os.path.basename(tarball))
    retrieve=urlparse.urljoin(mirror,tarball)
    try:
        utilities.download(retrieve,destination)
    except urllib2.HTTPError, e:
        if e.code != 404 or not isinstance(url,types.GeneratorType):
            raise
        try:
            setup(mirror,url,temp)
        except StopIteration, ignore:
            raise e
    return destination

def deploy(local,remote):
    '''
    Deploys the local to remote. If not directory, extracts it.
    @param local - local directory, or file to deploy
    @param remote - remote directory to deploy into
    @param rsync - (Optional) Is this an update?
    '''
    if exists(remote):
        rsync_project(local,remote)
    else:
        run("mkdir -p "+os.path.dirname(remote)) 
        put(local,os.path.dirname(remote))
        run("tar -xzf "+remote)
