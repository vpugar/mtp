#!/bin/bash

CURRENT_PATH=`pwd -P`
SCRIPT=`realpath -s $0`
SCRIPTPATH=`dirname $SCRIPT`

cd $SCRIPTPATH

echo "Working in $SCRIPTPATH"

docker-compose up --build
#docker-compose up --build -d

cd $CURRENT_PATH
