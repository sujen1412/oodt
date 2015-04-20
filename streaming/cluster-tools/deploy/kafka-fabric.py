# Future imports
from __future__ import print_function

# Python imports
import urllib2
import urlparse
import os

# Local imports
import utilities
from skeleton import setup

def kafka(kafka,scala):
    '''
    Generator to generate Kafla versions
    @param kafka - kafka version
    @param scala - Scala version
    '''
    versions = scala.split(".")
    while len(versions) > 0:
         tarball = "kafka_"+".".join(versions)+"-"+kafka+".tgz"
         yield "/".join(["kafka",kafka,tarball])
         del versions[-1]

def deploy():
    pass

def configure():
    pass

def all():
    '''
    Performs deployment of Kafka on all hosts
    '''
    try:
        print("Setting up for Kafka deployment")
        obj=setup(os.environ["APACHE_MIRROR"],kafka(os.environ["KAFKA_VERSION"],os.environ["SCALA_VERSION"]))
        deploy(obj,os.path.join(os.environ["INSTALL_DIR"],os.path.basename(obj))
    except Exception as e:
        print("Caught exception:",str(e))

if __name__ == "__main__":
    all()
