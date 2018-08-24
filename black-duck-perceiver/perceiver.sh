#!/bin/bash

dir=/opt/perceiver

jar_name=perceiver.jar

if [ -z "$1" ]; then
  arg1="/etc/perceiver/perceiver.yaml"
else
  arg1=$1
fi

java -Djavax.net.ssl.trustStore=/etc/trust/cftruststore -jar $dir/$jar_name $arg1

