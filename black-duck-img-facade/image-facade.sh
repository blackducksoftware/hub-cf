#!/bin/bash

dir=/opt/image-facade

jar_name=image-facade.jar

if [ -z "$1" ]; then
  arg1="/etc/image-facade/image-facade.yaml"
else
  arg1=$1
fi

java -Djavax.net.ssl.trustStore=/etc/trust/cftruststore -jar $dir/$jar_name $arg1

