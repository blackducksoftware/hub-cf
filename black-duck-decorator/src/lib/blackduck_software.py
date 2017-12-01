'''
Copyright (C) 2017 Black Duck Software, Inc.
http://www.blackducksoftware.com/


Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements. See the NOTICE file
distributed with this work for additional information
regarding copyright ownership. The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied. See the License for the
specific language governing permissions and limitations
under the License.
'''

from __future__ import print_function

from __builtin__ import str
import json
from logging import getLogger
import logging
import os
import platform
import re
import shutil
from subprocess import Popen, PIPE
import sys
import urllib
from urlparse import urlunparse

from phonehome import PhoneHome


cert_alias = 'BlackDuckHub' # Global for the alias of the certificate in the Store
store_pass = 'changeit' # Global for the Key Store password

# Get the numeric logging level based on the "__bds_debug_env_vbl_name" environment variable. If not set or not a valid debug level,
# set numeric debug level to None and disable logging
__bds_debug_env_vbl_name = 'BDS_DEBUG_LEVEL'
__debug_level_name = os.environ.get(__bds_debug_env_vbl_name)
__debug_level = None
if __debug_level_name is not None:
    __debug_level = getattr(logging, __debug_level_name.upper(), None)
if not isinstance(__debug_level, int):
    logging.disable(logging.CRITICAL)
else:
    logging.basicConfig(level=__debug_level)

__logging = getLogger(__name__)

def main():
    '''
    Perform the following steps:
        * Ensure the decorator is supposed to execute
        * Retrieve and validate the data
        * Retrieve the Scan Client from the configured Hub Server
        * Unpack the Scan Client into the configured directory
        * [Optional] Add certificates from the Hub Server
        * Send usage information back to Black Duck
        * Execute the Scan Client against the configured directory
    :return: Exit code from executing the Scan Client or 1 for internal errors
    :rtype: int
    '''   
    appinfo = get_application_info()
    service = find_blackduck_service(appinfo)
    if service is None:
        __logging.fatal("Black Duck Scan Service not found in VCAP_SERVICES structure")
        sys.exit("Black Duck Scan Service data not found in VCAP_SERVICES")
        
    scan_data = get_scan_data(appinfo, service)
    if validate_scan_data(scan_data) != 0:
        __logging.fatal("Invalid scan data received")
        sys.exit("Error validating scan data")
        
    scan_client_zip = retrieve_scan_client(scan_data)
    if scan_client_zip is None:
        __logging.fatal("Could not retrieve scan client from Hub")
        sys.exit("Error retrieving Black Duck Software Scan Client from Hub")
        
    scan_client_base = unpack_scan_client(scan_client_zip)
    if scan_client_base is None:
        __logging.fatal("Could not unzip scan client")
        sys.exit("Error unpacking Black Duck Software Scan Client")
        
    if scan_data['run_insecure'] is False:
        if add_certificate(scan_data['host'], scan_client_base) is False:
            __logging.fatal("Could not add Hub server certificate to key store")
            sys.exit("Error adding server certificate to key store")
    
    __logging.debug("Attempting to phone home")        
    hubloc = scan_data['host']
    if scan_data['port'] != -1:
        hubloc += ':' + str(scan_data['port'])
    hub_url = urlunparse((scan_data['scheme'], hubloc, "", "", "", ""))
    phone_home = PhoneHome(hub_url, scan_data['username'], scan_data['password'])
    phone_home.call(scan_data['integration_source'], scan_data['plugin_version'], scan_data['integration_vendor'])
            
    scan_return = run_scan(scan_client_base, scan_data, appinfo)
    sys.exit(scan_return)
    
def detect():
    appinfo = get_application_info()
    service = find_blackduck_service(appinfo)
    if service == None:
        sys.exit(1)
    print("black-duck-scan")

# Get Application Info
#
# Collect the about the current application    
def get_application_info():
    __logging.info("entering get_application_info")
    appinfo = {}
    vcap_application = json.loads(os.getenv('VCAP_APPLICATION', '{}'))
    __logging.debug("retrieved VCAP_APPLICATION: %s", vcap_application)
    appinfo['name'] = vcap_application.get('application_name')
    if appinfo['name'] == None:
        __logging.fatal("application_name not specified in VCAP_APPLICATION structure")
        sys.exit("VCAP_APPLICATION must specify application_name")
    appinfo['space_name'] = vcap_application.get('space_name')
    appinfo['space_id'] = vcap_application.get('space_id')
    appinfo['api_endpoint'] = transform_api_endpoint(vcap_application.get('cf_api'))
    __logging.debug("found appinfo['name']: %s; appinfo['space_name']: %s; appinfo['space_id']: %s; appinfo['api_endpoint']: %s", 
                    appinfo['name'], appinfo['space_name'], appinfo['space_id'], appinfo['api_endpoint'])
    __logging.info("exiting get_application_info")
    return appinfo

# Ensure the user has opted-in to the scan
def find_blackduck_service(appinfo):
    __logging.info("entering find_blackduck_service")
    vcap_services = json.loads(os.getenv('VCAP_SERVICES', '{}'))
    for service in vcap_services:
        service_instances = vcap_services[service]
        for instance in service_instances:
            tags = instance.get('tags', [])
            if 'black-duck-scan' in tags or 'black-duck-scan' == instance.get('name'):
                __logging.info("exiting find_blackduck_service")
                return instance
    __logging.info("exiting find_blackduck_service (None)")
    return None

# Retrieve Hub credentials from the VCAP_SERVICES
def get_scan_data(appinfo, service):
    '''
    Retrieve the Hub credentials and project information from the VCAP_SERVICES structure or the staging environment
    :return: Dictionary containing the Hub credentials and Hub project information
    :rtype: Dictionary
    '''
    __logging.info("entering get_scan_data")
    print("  ...Retrieving Hub credentials")
    sys.stdout.flush()
    scan_data = {}
    credentials = service.get('credentials', {})
    scan_data['scheme'] = credentials.get('scheme')
    scan_data['username'] = credentials.get('username')
    scan_data['password'] = credentials.get('password')
    scan_data['host'] = credentials.get('host')
    scan_data['port'] = credentials.get('port')
    scan_data['project_name'] = credentials.get('projectName')
    if scan_data['project_name'] is None:
        # No project name came from the binding data.
        # See if env variable was set in project manifest
        scan_data['project_name'] = os.environ.get('BLACK_DUCK_PROJECT_NAME', None)
    scan_data['project_release'] = os.environ.get('BLACK_DUCK_PROJECT_VERSION', None)
    if scan_data['project_release'] is None:
        # If not set, try as lower-case
        scan_data['project_release'] = os.environ.get('black_duck_project_version', None)
    scan_data['using_default_code_location'] = False
    scan_data['code_location'] = credentials.get('codeLocationName')
    if scan_data['code_location'] is None:
        # No code location name came from the binding data.
        # See if env variable was set in project manifest
        scan_data['code_location'] = os.environ.get('BLACK_DUCK_CODE_LOCATION', None)
        if scan_data['code_location'] is None:
            # No code location name came from the environment variable
            # Set a default code location
            scan_data['code_location'] = generate_default_code_location_name(appinfo)
            scan_data['using_default_code_location'] = True
    scan_data['run_insecure'] = credentials.get('isInsecure')
    scan_data['plugin_version'] = credentials.get('pluginVersion')
    scan_data['integration_source'] = credentials.get('integrationSource')
    scan_data['integration_vendor'] = credentials.get('integrationVendor')
    
    if __logging.isEnabledFor(logging.DEBUG):
        for key,value in scan_data.items():
            __logging.debug("found %s: %s", key, value)
    __logging.info("exiting get_scan_data")
    return scan_data

# Ensure the required elements were included in the VCAP_SERVICES
def validate_scan_data(scan_data):
    '''
    Ensure the required elements were included in the VCAP_SERVICES. Print warning or error messages based on the severity.
    :return: 0 if all items verified or only warning message(s) printed; 1 if error message(s) printed
    :rtype: int
    '''
    __logging.info("entering validate_scan_data")
    ret = 0
    if not scan_data['username']:
        eprint("ERROR! Must specify a username")
        __logging.error("username not specified")
        ret = 1
    if not scan_data['host']:
        eprint("ERROR! Must specify a host")
        __logging.error("host not specified")
        ret = 1
    if scan_data['project_release'] is None:
        eprint("WARNING! Project version NOT found. Continuing with none.", 
               "Please set applications.env.BLACK_DUCK_PROJECT_VERSION in application manifest.yml", 
               "Consult Black Duck Service Broker documentation for more detail.", sep='\n')
        __logging.warning("project_release not specified; Hub default will be used")
    if scan_data['using_default_code_location'] is True:
        eprint("WARNING! Code Location Name NOT found. Continuing with default: " + scan_data['code_location'], 
               "Please re-bind the application and add code_location to the JSON of the service specific parameters or",
               "set applications.env.BLACK_DUCK_CODE_LOCATION in application manifest.yml.", 
               "Consult Black Duck Service Broker documentation for more detail.", sep='\n')
        __logging.warning("code_location not specified; using generated code_location: %s", scan_data['code_location'])
    if scan_data['project_name'] is None:
        eprint("WARNING! Project Name NOT found. Continuing with none.", 
               "Please re-bind the application and add project_name to the JSON of the service specific parameters. or",
               "set applications.env.BLACK_DUCK_PROJECT_NAME in application manifest.yml.",
               "Consult Black Duck Service Broker documentation for more detail.", sep='\n')
        __logging.warning("project_name not specified; Hub default will be used")
    if scan_data['run_insecure'] is True:
        eprint("WARNING! ALL TLS/SSL ERRORS WILL BE IGNORED!",
               "PLEASE HAVE YOUR PCF ADMIN UNCHECK THE 'Ignore TLS/SSL errors' CHECKBOX AS PART OF THE",
               "BLACK DUCK SERVICE BROKER TILE CONFIGURATION",
               "HAVE YOUR PCF ADMIN CONSULT THE BLACK DUCK SERVICE BROKER DOCUMENTATION FOR MORE DETAIL.", sep='\n')
        __logging.warning("IGNORING TLS/SSL ERRORS")
    __logging.info("exiting validate_scan_data")
    return ret

# Execute the Scan Client
def run_scan(scan_client_base, scan_data, appinfo):
    __logging.info("entering run_scan")
    print("  ...Executing scan")
    sys_type = platform.uname()[0]
    my_env = os.environ.copy()
    my_env['BD_HUB_PASSWORD'] = str(scan_data['password'])
    if sys_type == 'Darwin':
        jre_path = os.path.join(scan_client_base, 'jre', 'Contents', 'Home')
        scan_ext = '.sh'
    elif sys_type == 'Linux':
        jre_path = os.path.join(scan_client_base, 'jre')
        scan_ext = '.sh'
    elif sys_type == 'Windows':
        jre_path = os.path.join(scan_client_base, 'jre')
        scan_ext = '.bat'
    else:
        eprint("ERROR! Unknown system type:", sys_type)
        __logging.error("OS type: %s unknown")
        return 1
    
    my_env['BDS_JAVA_HOME'] = jre_path

    scanner = os.path.join(scan_client_base, 'bin') + os.sep + 'scan.cli'  + scan_ext   
    scan_cmd = []
    scan_cmd.append(scanner)
    
    scan_cmd.append('--username')
    scan_cmd.append(str(scan_data['username']))
    
    if scan_data['project_name'] is not None:
        scan_cmd.append('--project')
        scan_cmd.append(str(scan_data['project_name']))
        
    if scan_data['project_release'] is not None:
        scan_cmd.append('--release')
        scan_cmd.append(str(scan_data['project_release']))
        
    scan_cmd.append('--host')
    scan_cmd.append(str(scan_data['host']))
    
    port_set = False
    if scan_data['port'] != -1:
        port_set = True
        scan_cmd.append('--port')
        scan_cmd.append(str(scan_data['port']))
        
    if scan_data['scheme'] is not None:
        scan_cmd.append('--scheme')
        scan_cmd.append(str(scan_data['scheme']))
        if not port_set:
            # If the scheme is HTTPS and the user did not provide a port, explicitly set to 443 or 
            # the Scan Client will default to 8080
            if scan_data['scheme'] == "https":
                scan_cmd.append('--port')
                scan_cmd.append('443')
    
    if scan_data['code_location'] is not None:
        scan_cmd.append('--name')
        scan_cmd.append(str(scan_data['code_location']))

    if scan_data['run_insecure'] is True:
        # Ignore TLS/SSL errors
        scan_cmd.append('--insecure')
        
    scan_cmd.append(sys.argv[2])
    
    print("       CF Application Name: " + appinfo['name'])
    print("       Scan Client: " + scanner)
    print("       Hub Host: " + str(scan_data['host']))
    print("       Hub Project Name: " + str(scan_data['project_name']))
    print("       Hub Project Version: " + str(scan_data['project_release']))
    print("       Hub Code Location Name: " + str(scan_data['code_location']))
    if scan_data['run_insecure'] is True:
        print("       IGNORING TLS/SSL ERRORS!")
    sys.stdout.flush()
    
    __logging.debug("running scan with command: %s", scan_cmd)
    __logging.debug("scan command using env: %s", my_env)
    run = Popen(scan_cmd, env=my_env, stdout=PIPE, stderr=PIPE)
    stdout, stderr = run.communicate()
    print(stdout)
    print(stderr)
    sys.stdout.flush()
    sys.stderr.flush()
    __logging.debug("scan command output: %s", stdout)
    __logging.error("scan command error: %s", stderr)
    __logging.info("exiting run_scan")
    return run.returncode

# Retrieve the Scan Client from the configured Hub instance
# Scan Client is retrieved as a ZIP file
def retrieve_scan_client(scan_data):
    __logging.info("entering retrieve_scan_client")
    scan_client_loc = None
    if scan_data['scheme'] is not None:
        scan_client_loc = str(scan_data['scheme']) + "://"
    else:
        scan_client_loc = "https://"
    scan_client_loc += str(scan_data['host'])
    if scan_data['port'] != -1:
        scan_client_loc += ":" + str(scan_data['port'])
    scan_client_loc += "/download/scan.cli.zip"
    print("  ...Retrieving Scan Client from " + scan_client_loc)
    sys.stdout.flush()
    try:
        __logging.debug("retrieve scan client from: %s", scan_client_loc)
        scan_client_zip, _ = urllib.urlretrieve(scan_client_loc)
    except IOError as e:
        eprint(e.errno, e.strerror, sep=': ')
        __logging.error("scan client retrieve failed: %s", e, exc_info=1)
        scan_client_zip = None
        __logging.error("scan client NOT retrieved; returning None")
        pass
    __logging.info("exiting retrieve_scan_client")
    return scan_client_zip

# Explode the retrieved Scan Client into the directory
# Invoking shell command unzip over Python's ZipFile.extractall() because file permissions
# are NOT preserved correctly with extractall().
def unpack_scan_client(scan_client_zip):
    __logging.info("entering unpack_scan_client")
    unzip_root = str(sys.argv[1])
    my_env = os.environ.copy()
    unzip_cmd = []
    unzip_cmd.append('unzip')
    unzip_cmd.append('-q')
    unzip_cmd.append(scan_client_zip)
    unzip_cmd.append('-d')
    unzip_cmd.append(unzip_root)
    print("  ...Exploding " + scan_client_zip + " to " + unzip_root)
    sys.stdout.flush()
    __logging.debug("unpacking scan client with command: %s", unzip_cmd)
    __logging.debug("unpacking scan client using env: %s", my_env)
    run = Popen(unzip_cmd, env=my_env, stdout=PIPE, stderr=PIPE)
    stdout, stderr = run.communicate()
    print(stdout)
    print(stderr)
    sys.stdout.flush()
    sys.stderr.flush()
    __logging.debug("unzip output: %s", stdout)
    __logging.error("unzip error: %s", stderr)
    
    scan_base = None
    if run.returncode == 0:
        __logging.debug("unzip successful....")
        print("  ...Exploded " + scan_client_zip)
        # Get directory with latest creation time as that should be the newly unzipped scan client
        # NOTE: There SHOULD only be one directory
        newestctime = 0
        dirs = os.listdir(unzip_root)
        __logging.debug("directories to search for newest: %s", dirs)
        for entry in dirs:
            dir_entry = os.path.join(unzip_root, entry)
            __logging.debug("directory: %s", dir_entry)
            if os.path.isdir(dir_entry) and os.stat(dir_entry).st_ctime > newestctime:
                newestctime = os.stat(dir_entry).st_ctime
                scan_base = dir_entry
                print("  ...Setting scan client base:" + scan_base)
        sys.stdout.flush()
    __logging.info("exiting unpack_scan_client")
    return scan_base

# Add a certificate to new Java Key Store for the Scan Client
def add_certificate(host, scan_client_base):
    __logging.info("entering add_certificate")
    print("  ...Adding certificate to Java Key Store")
    sys.stdout.flush()
        
    sys_type = platform.uname()[0]
    if sys_type == 'Darwin':
        jre_path = os.path.join(scan_client_base, 'jre', 'Contents', 'Home')
    elif sys_type == 'Linux':
        jre_path = os.path.join(scan_client_base, 'jre')
    elif sys_type == 'Windows':
        jre_path = os.path.join(scan_client_base, 'jre')
    else:
        eprint("ERROR! Unknown system type:", sys_type)
        __logging.error("OS type: %s unknown")
        return 1
    
    keytool = os.path.join(jre_path, 'bin', '') + 'keytool'
    __logging.debug("using keytool: %s", keytool)
    master_key_store = os.path.join(jre_path, 'lib', 'security', '') + 'cacerts'
    __logging.debug("master keystore: %s", master_key_store)
    key_store = os.path.join(jre_path, 'lib', 'security', '') + 'jssecacerts'
    __logging.debug("working keystore: %s", key_store)

    # Create a copy of the main key store so we update and use the copy
    shutil.copy2(master_key_store, key_store)

    gen_keystore_cmd = []
    gen_keystore_cmd.append(keytool)
    gen_keystore_cmd.append('-importcert')
    gen_keystore_cmd.append('-storepass')
    gen_keystore_cmd.append(store_pass)
    gen_keystore_cmd.append('-keystore')
    gen_keystore_cmd.append(key_store)
    gen_keystore_cmd.append('-alias')
    gen_keystore_cmd.append(cert_alias)
    gen_keystore_cmd.append('-noprompt')
    __logging.debug("command to import certificate: %s", gen_keystore_cmd)
    
    gen_keyprint_cmd = []
    gen_keyprint_cmd.append(keytool)
    gen_keyprint_cmd.append('-printcert')
    gen_keyprint_cmd.append('-rfc')
    gen_keyprint_cmd.append('-sslserver')
    gen_keyprint_cmd.append(host)
    __logging.debug("command to get certificate: %s", gen_keyprint_cmd)
 
    # Set up command to print the certificate (ie 'keytool -printcert -rfc -sslserver [host]')      
    run_keyprint = Popen(gen_keyprint_cmd, stdout=PIPE, stderr=PIPE)

    # Set up command to add the certificate to the key store
    # This command gets its input from the stdout of the 'run_keyprint' command and is equivalent to
    # keytool -printcert -rfc -sslserver [host] | keytool -importcert -storepass [pass] -keystore [store] -alias [alias] -noprompt
    run_keystore = Popen(gen_keystore_cmd, stdin=run_keyprint.stdout, stdout=PIPE, stderr=PIPE)
    stdout, stderr = run_keystore.communicate()
    print(stdout)
    print(stderr)
    sys.stdout.flush()
    sys.stderr.flush()
    __logging.debug("import certificate output: %s", stdout)
    __logging.error("improt certificate error: %s", stderr)
    
    __logging.debug("returning %d from importing certificate", run_keystore.returncode)
    __logging.info("exiting add_certificate")
    return run_keystore.returncode

# Generate a default code location name
# This takes the api endpoint, organization, user space and project name
# Method returns a string with the default code location name
def generate_default_code_location_name(appinfo):
    __logging.info("using generate_default_code_location_name")
    return str(transform_api_endpoint(appinfo['api_endpoint']) + '/' + appinfo['space_id'] + '/' + appinfo['space_name'] + '/' + appinfo['name'])

# Helper function to strip the http:// or https:// from the CF API Endpoint
# Input: String containing the CF API Endpoint prefixed with http:// or https://
# Return: String containing the CF API Enpoint with the http:// or https:// stripped
def transform_api_endpoint(api_endpoint):
    __logging.info("using transform_api_endpoint")
    if api_endpoint is None:
        __logging.debug("api_endpoint is None; returing None")
        return None
    return re.sub(r"http[s]?://", "", api_endpoint, count=1)

# Helper function to make outputting to stderr easier
def eprint(*args, **kwargs):
    print(*args, file=sys.stderr, **kwargs)
    sys.stderr.flush()
    
if __name__ == '__main__':
    main()