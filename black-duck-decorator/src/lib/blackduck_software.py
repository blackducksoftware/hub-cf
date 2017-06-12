# blackduck_software
# Created on May 8, 2017

from __future__ import print_function
import os
import json
import platform
import sys
import urllib
from subprocess import Popen, PIPE

def main():
    appinfo = get_application_info()
    service = find_blackduck_service(appinfo)
    if service is None:
        eprint("Black Duck Scan Service data not found in VCAP_SERVICES")
        sys.exit(1)
        
    scan_data = get_scan_data(appinfo, service)
    if validate_scan_data(scan_data) != 0:
        eprint("Error validating scan data")
        sys.exit(1)
        
    scan_client_zip = retrieve_scan_client(scan_data)
    if scan_client_zip is None:
        eprint("Error retrieving Black Duck Software Scan Client from Hub")
        sys.exit(1)
        
    scan_client_base = unpack_scan_client(scan_client_zip)
    if scan_client_base is None:
        eprint("Error unpacking Black Duck Software Scan Client")
        sys.exit(1)
        
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
    appinfo = {}
    vcap_application = json.loads(os.getenv('VCAP_APPLICATION', '{}'))
    appinfo['name'] = vcap_application.get('application_name')
    if appinfo['name'] == None:
        eprint("VCAP_APPLICATION must specify application_name")
        sys.exit(1)
    return appinfo

# Ensure the user has opted-in to the scan
def find_blackduck_service(appinfo):
    vcap_services = json.loads(os.getenv('VCAP_SERVICES', '{}'))
    for service in vcap_services:
        service_instances = vcap_services[service]
        for instance in service_instances:
            tags = instance.get('tags', [])
            if 'black-duck-scan' in tags or 'black-duck-scan' == instance.get('name'):
                return instance
    return None

# Retrieve Hub credentials from the VCAP_SERVICES
def get_scan_data(appinfo, service):
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
    scan_data['project_release'] = os.environ.get('BLACK_DUCK_SCAN_VERSION', None)
    if scan_data['project_release'] is None:
        # If not set, try as lower-case
        scan_data['project_release'] = os.environ.get('black_duck_scan_version', None)
    scan_data['code_location'] = credentials.get('codeLocation')
    return scan_data

# Ensure the required elements were included in the VCAP_SERVICES
def validate_scan_data(scan_data):
    ret = 0
    if not scan_data['username']:
        eprint("ERROR! Must specify a username")
        ret = 1
    if not scan_data['host']:
        eprint("ERROR! Must specify a host")
        ret = 1
    if scan_data['project_release'] is None:
        eprint("WARNING! Project scan version NOT found. Continuing with none.", "Please set applications.env.BLACK_DUCK_SCAN_VERSION in application manifest.yml", sep='\n')
    return ret

# Execute the Scan Client
def run_scan(scan_client_base, scan_data, appinfo):
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
        
    scan_cmd.append('--insecure')
        
    scan_cmd.append(sys.argv[2])
    
    print("       CF Application Name: " + appinfo['name'])
    print("       Scan Client: " + scanner)
    print("       Hub Host: " + str(scan_data['host']))
    print("       Hub Project Name: " + str(scan_data['project_name']))
    print("       Hub Project Version: " + str(scan_data['project_release']))
    sys.stdout.flush()
    
    run = Popen(scan_cmd, env=my_env, stdout=PIPE, stderr=PIPE)
    stdout, stderr = run.communicate()
    print(stdout)
    print(stderr)
    sys.stdout.flush()
    sys.stderr.flush()
    return run.returncode

# Retrieve the Scan Client from the configured Hub instance
# Scan Client is retrieved as a ZIP file
def retrieve_scan_client(scan_data):
    scan_client_loc = None
    if scan_data['scheme'] is not None:
        scan_client_loc = str(scan_data['scheme']) + "://"
    else:
        scan_client_loc = "http://"
    scan_client_loc += str(scan_data['host'])
    if scan_data['port'] != -1:
        scan_client_loc += ":" + str(scan_data['port'])
    scan_client_loc += "/download/scan.cli.zip"
    print("  ...Retrieving Scan Client from " + scan_client_loc)
    sys.stdout.flush()
    try:
        scan_client_zip, _ = urllib.urlretrieve(scan_client_loc)
    except IOError as e:
        eprint(e.errno, e.strerror, sep=': ')
        scan_client_zip = None
        pass
    return scan_client_zip

# Explode the retrieved Scan Client into the directory
# Invoking shell command unzip over Python's ZipFile.extractall() because file permissions
# are NOT preserved correctly with extractall().
def unpack_scan_client(scan_client_zip):
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
    run = Popen(unzip_cmd, env=my_env, stdout=PIPE, stderr=PIPE)
    stdout, stderr = run.communicate()
    print(stdout)
    print(stderr)
    sys.stdout.flush()
    sys.stderr.flush()
    
    scan_base = None
    if run.returncode == 0:
        print("  ...Exploded " + scan_client_zip)
        # Get directory with latest creation time as that should be the newly unzipped scan client
        # NOTE: There SHOULD only be one directory
        newestctime = 0
        dirs = os.listdir(unzip_root)
        for entry in dirs:
            dir_entry = os.path.join(unzip_root, entry)
            if os.path.isdir(dir_entry) and os.stat(dir_entry).st_ctime > newestctime:
                newestctime = os.stat(dir_entry).st_ctime
                scan_base = dir_entry
                print("  ...Setting scan client base:" + scan_base)
        sys.stdout.flush()
    return scan_base

# Helper function to make outputting to stderr easier
def eprint(*args, **kwargs):
    print(*args, file=sys.stderr, **kwargs)
    sys.stderr.flush()
    
if __name__ == '__main__':
    main()