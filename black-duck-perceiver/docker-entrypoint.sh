#!/bin/sh
set -e

certsBaseDir="/mnt/certs"
trustStoreDir="/blackducksoftware/perceiver/security"
trustStore="${trustStoreDir}/truststore"
jvmTrustStore="$JAVA_HOME/jre/lib/security/cacerts"

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

manageBlackDuckCerts() {
  blackDuckCertsDir="${certsBaseDir}/blackduck-certs"
  blackDuckAliasPrefix="black_duck_cert_"
    
  if [ "${BLACKDUCK_AUTHENTICATION_METHOD}" != "Insecure" ]; then
    echo "Adding BlackDuck certificates to ${blackDuckCertsDir} (INSECURE=${BLACKDUCK_INSECURE})"
    echo "Certificate: ${BLACKDUCK_CERTIFICATE}"
    echo ${BLACKDUCK_CERTIFICATE} | base64 -d > "${blackDuckCertsDir}/hub.cer"
    echo "Massaged Certificate:"
    cat ${blackDuckCertsDir}/hub.cer
    
    addAllCerts "$blackDuckCertsDir" "$blackDuckAliasPrefix"
  else
    echo "Skip adding BlackDuck certificates (INSECURE=${BLACKDUCK_INSECURE})"
  fi
  
}

if [ -d "${trustStoreDir}" ]; then
	echo "Attempting to remove trust store"
	rm -f "${trustStore}" || true
else
	echo "Creating directory for trust store"
	mkdir -p "${trustStoreDir}"
fi

# Populate the trust store with certs from the JRE trust store
if [ -f "${jvmTrustStore}" ]; then
  echo "Copying JVM trust store: ${jvmTrustStore} to: ${trustStore}"
  cp ${jvmTrustStore} ${trustStore}
else
  echo "JVM trust store does not exist at location: ${jvmTrustStore}. Continuing with empty trust store!"
fi

manageOpsmanCerts
manageHostCACerts
manageBlackDuckCerts

if [ -f "${trustStore}" ]; then
	echo "Success: Trust store: ${trustStore} created"
else
	echo "Error: Trust store: ${trustStore} not created"
fi

exec "$@"
