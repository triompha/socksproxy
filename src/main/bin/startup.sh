#!/bin/bash
# main class and args
MAIN_CLASS="com.triompha.socksproxy.SocksServer"
JAVA_ARGS="-server -Xms256m -Xmx256m -XX:NewSize=100m -XX:+UseConcMarkSweepGC -XX:CMSInitiatingOccupancyFraction=58 -XX:PermSize=64m -XX:MaxPermSize=64m -XX:ThreadStackSize=512"

#JAVA_ARGS="${JAVA_ARGS}  -Dsun.rmi.transport.tcp.readTimeout=5000  -Dsun.rmi.dgc.server.gcInterval=3600000 -Dsun.rmi.dgc.client.gcInterval=3600000 -Dsun.rmi.server.exceptionTrace=true"

cd `dirname $0`
BIN_DIR=`pwd`
if [ -n "`echo $BIN_DIR |grep /bin$`" ]; then
	cd ..
fi
DEPLOY_DIR=`pwd`
LOGS_DIR=${DEPLOY_DIR}/logs
GC_LOG=${LOGS_DIR}/gc.log
JAVA_ARGS="${JAVA_ARGS}  -Xloggc:${GC_LOG}"

#set classpath
LIB_DIR=${DEPLOY_DIR}/lib/*

CLASSPATH=$CLASSPATH:${LIB_DIR}

#check if the server has bean started
PIDS=`ps  aux|grep -v grep | grep "$DEPLOY_DIR" | awk '{print $2}'`
if [ -n "$PIDS" ]; then
    echo "ERROR: The $DEPLOY_DIR already started!"
    echo "PID: $PIDS"
    exit 1
fi

if [ ! -d $LOGS_DIR ]; then
        mkdir $LOGS_DIR
fi
#start the server
echo -e "Starting the $DEPLOY_DIR \c"
nohup java -cp ${CLASSPATH} ${JAVA_ARGS} ${MAIN_CLASS} ${DEPLOY_DIR}/../  1>>${LOGS_DIR}/stdout.log 2>>${LOGS_DIR}/stderr.log&

#check if the server start normally , wait for 10 second
COUNT=0
SECOND=10
while [ $COUNT -lt 1 ]; do
    echo -e ".\c"
    sleep 1
    SECOND=$[SECOND-1];
    COUNT=`ps  aux|grep -v grep | grep "$DEPLOY_DIR" | awk '{print $2}'| wc -l`
    if [ $COUNT -gt 0 ]; then
        break
    fi
    if [ $SECOND -lt 1 ]; then
	break
    fi
done

PIDS=`ps  aux|grep -v grep | grep "$DEPLOY_DIR" | awk '{print $2}'`

if [ -n "$PIDS" ]; then
    echo "OK!"
    echo "PID: $PIDS"
    echo "STDOUT: $LOGS_DIR/stdout.log"
    exit
fi
echo "ERROR!"
echo "STDERROR: $LOGS_DIR/stderr.log"



