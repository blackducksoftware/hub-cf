#!/bin/sh
set -e

certsBaseDir="/mnt/certs"
trustStoreDir="/blackducksoftware/image-facade/security"
trustStore="${trustStoreDir}/truststore"

addAllCerts() {
	if [ ! $# == 2 ]; then
		echo "Usage: $0 dir_to_certs cert_alias_prefix"
		return
	fi
	
	certsDir=$1
	certsAliasPrefix=$2
	
	if [ -d "${certsDir}" ]; then
		n=0
		for cert in $(ls ${certsDir}/*); do
			certAlias="${certsAliasPrefix}${n}"
			echo "Adding ${cert} to trust store with alias ${certAlias}"
			keytool -importcert -keystore "${trustStore}" -storepass changeit -noprompt -trustcacerts -alias ${certAlias} -file ${cert}
			n=$((${n}+1))
		done
	fi
}

manageHostCACerts() {
	hostCACertsDir="${certsBaseDir}/host-ca-certs"
	hostCAAliasPrefix="host_ca_cert_"
	
	addAllCerts "$hostCACertsDir" "$hostCAAliasPrefix"
}

manageOpsmanCerts() {
	opsmanCertsDir="${certsBaseDir}/opsman-certs"
	opsmanAliasPrefix="opsman_cert_"
	
	addAllCerts "$opsmanCertsDir" "$opsmanAliasPrefix"
}

if [ -d "${trustStoreDir}" ]; then
	echo "Attempting to remove trust store"
	rm -f "${trustStore}" || true
else
	echo "Creating directory for trust store"
	mkdir -p "${trustStoreDir}"
fi
	
manageOpsmanCerts
manageHostCACerts

if [ -f "${trustStore}" ]; then
	echo "Success: Trust store: ${trustStore} created"
else
	echo "Error: Trust store: ${trustStore} not created"
fi

#if [ -f "${trustStore}" ]; then
#	echo "Generating options to use trust store"
#	TS_OPTS="-Djava.net.ssl.trustStore=${trustStore}"
#else
#	echo "Error: Trust Store: ${trustStore} not created."
#	TS_OPTS=""
#fi

exec "$@"
