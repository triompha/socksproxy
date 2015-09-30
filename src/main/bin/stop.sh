#!/bin/bash

cd `dirname $0`
#adjust if the shell in bin directory
BIN_DIR=`pwd`
if [ -n "`echo $BIN_DIR |grep /bin$`" ]; then
        cd ..
fi
DEPLOY_DIR=`pwd`

LOGS_DIR=${DEPLOY_DIR}/logs

GC_LOG=${LOGS_DIR}/gc.log

#kill server
PIDS=`ps aux|grep -v grep | grep "$DEPLOY_DIR" |awk '{print $2}'`
if [ -z "$PIDS" ]; then
    echo "ERROR: The $DEPLOY_DIR does not started!"
    exit 1
fi

echo -e "Stopping the $DEPLOY_DIR \c"
for PID in $PIDS ; do
        kill $PID > /dev/null 2>&1
done


COUNT=0
while [ $COUNT -lt 1 ]; do
    echo -e ".\c"
    sleep 1
    COUNT=1
    for PID in $PIDS ; do
                PID_EXIST=`ps $PID|grep $PID`
                echo $PID_EXIST
                if [ -n "$PID_EXIST" ]; then
                        COUNT=0
                        break
                fi
        done
done
echo "OK!"
echo "PID: $PIDS"
