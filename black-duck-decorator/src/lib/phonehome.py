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
import cookielib
import hashlib
import json
import logging
import os
import urllib
from urllib2 import Request
import urllib2
from urlparse import urljoin

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

class PhoneHome(object):
    '''
    This class encapsulates the functionality required to send plugin usage data back to Black Duck Software
    '''
    _phone_home_server = 'https://collect.blackducksoftware.com'
    
    _auth_endpoint = '/j_spring_security_check'
    _registration_endpoint = '/api/v1/registrations'
    _version_endpoint = '/api/v1/current-version'
    
    _csrf_token_key = 'X-CSRF-TOKEN'
    _registration_resp_id_key = 'registrationId'
    
    _reg_id_key = 'regId'
    _source_key = 'source'
    _black_duck_name_key = 'blackDuckName'
    _black_duck_version_key = 'blackDuckVersion'
    _third_party_name_key = 'thirdPartyName'
    _third_party_version_key = 'thirdPartyVersion'
    _plugin_version_key = 'pluginVersion'
    _info_map_key = 'infoMap'
    
    _hub_name = 'Hub'
    
    def __init__(self, hub_url, hub_username, hub_password):
        '''
        Constructor
        
        :param str hub_url: Url of the hub instance (scheme://address[:port])
        :param str hub_username: Valid user on the hub instance
        :param str hub_password: Password for the hub user
        '''
        self.__logger = logging.getLogger(__name__)

        self.__logger.debug("creating instance of PhoneHome url: %s; username: %s, password: <redacted>", hub_url, hub_username)               
        self.__hub_url = hub_url
        self.__hub_username = hub_username
        self.__hub_password = hub_password
        
        # Create a Cookie Handler
        cj = cookielib.CookieJar()
        handlers=[
            urllib2.HTTPHandler(),
            urllib2.HTTPSHandler(),
            urllib2.HTTPCookieProcessor(cj)
            ]
        # Setup the handlers for HTTP/HTTPS requests with support for cookies
        self.__opener = urllib2.build_opener(*handlers)
            
    def call(self, source, plugin_version, third_party_name, third_party_version='N/A'):
        '''
        Attempt to send data about plugin usage to Black Duck Software.
        This should fail silently if there is an issue so that its usage is unobtrusive to the caller
        
        :param str source: Black Duck integration type (i.e. "Integrations", "Alliance Integrations")
        :param str plugin_version: Version of the plugin on which to report usage
        :param str third_party_name: Identifier of the third party software into which Black Duck is integrated
        :param str third_party_version: Version of the third party software into which Black Duck is integrated
        :rtype: None
        '''
        self.__logger.info("entering call method")
        self.__logger.debug("using source: %s; plugin version: %s; third party name: %s; third party version: %s", source, plugin_version, third_party_name, third_party_version)
        try:
            if third_party_name is None:
                return
            if plugin_version is None:
                return
            if source is None:
                return
            
            csrf_token = self._authenticate()
            registration_id = self._retrieve_registration_id(csrf_token)
            hub_version = self._retrieve_hub_version(csrf_token)                    
                    
            # Only continue if we have a registration_id and hub_version
            if registration_id is not None and hub_version is not None:
                # Build the Phone Home request
                ph_req_info = {}
                ph_req_info[PhoneHome._reg_id_key] = str(registration_id)
                ph_req_info[PhoneHome._source_key] = source
                info_map = {}
                info_map[PhoneHome._black_duck_name_key] = PhoneHome._hub_name
                info_map[PhoneHome._black_duck_version_key] = hub_version
                info_map[PhoneHome._third_party_name_key] = third_party_name
                info_map[PhoneHome._third_party_version_key] = third_party_version
                info_map[PhoneHome._plugin_version_key] = plugin_version
                ph_req_info[PhoneHome._info_map_key] = info_map
                
                ph_req_data = json.dumps(ph_req_info)
                ph_req_headers = {'Content-Type':'application/json'}
                try:
                    self.__send_request(PhoneHome._phone_home_server, ph_req_headers, ph_req_data)
                except (urllib2.HTTPError, urllib2.URLError) as e:
                    self.__logger.error("received exception: %s", e, exc_info=True)
                    pass
            else:
                self.__logger.warning("did not attempt Phone Home! registration id: %s; hub version: %s", registration_id, hub_version)
        
        except RuntimeError as e:
            # This should fail completely silent 
            self.__logger.critical("Phone Home critical failure. Received exception: %s", e, exc_info=True)
            pass
        
        self.__logger.info("exiting call method")
        
    def _authenticate(self):
        '''
        Attempt to authenticate with the Hub. If successful, csrf-token is returned.
        
        :return: Value from the HTTP Header's CSRF-TOKEN field, or None
        :rtype: str or None
        '''
        self.__logger.info("entering _authenticate method")
        # Authenticate with Hub instance
        auth_req_data = urllib.urlencode({'j_username':self.__hub_username, 'j_password':self.__hub_password})
        auth_req_headers = {'Content-Type':'application/x-www-form-urlencoded'}
        auth_resp = None
        try:
            auth_resp = self.__send_request(PhoneHome._auth_endpoint, auth_req_headers, auth_req_data)
        except (urllib2.HTTPError, urllib2.URLError) as e:
            self.__logger.error("received exception: %s", e, exc_info=True)
            pass
        
        csrf_token = None
        # Only try to get the csrf-token if the authorization response has data
        if auth_resp is not None:
            csrf_token = auth_resp.info()[PhoneHome._csrf_token_key]
        else:
            self.__logger.warning("no authorization response received; no CSRF token set")
            
        self.__logger.debug("returning csrf_token: %s", csrf_token)
        self.__logger.info("exiting _authenticate method")
        return csrf_token
    
    def _retrieve_registration_id(self, csrf_token):
        '''
        Generate the registration id used in the Phone Home Request. This will be the Hub registration id or
        a MD5 Hash of the Hub URL
        
        :param str csrf_token: The CSRF Token to include in the request to attempt to retrieve the Hub registration id
        :return: String containing the registration id to use in the Phone Home request
        :rtype: str
        '''
        self.__logger.info("entering _retrieve_registration_id method")
        # Calculate the MD5 Hash of the Hub Url
        md5 = hashlib.md5()
        md5.update(self.__hub_url)
        ph_registration_id = md5.hexdigest()
        
        # Only attempt to get the Hub registration if there is a CSRF token
        if csrf_token is not None:
            # Attempt to get registration id
            reg_req_headers = {PhoneHome._csrf_token_key:csrf_token}
            reg_data = None
            try:
                reg_resp = self.__send_request(PhoneHome._registration_endpoint, reg_req_headers)
                reg_data = json.load(reg_resp)
            except (urllib2.HTTPError, urllib2.URLError) as e:
                self.__logger.error("received exception: %s", e, exc_info=True)
                pass
            
            if reg_data and reg_data[PhoneHome._registration_resp_id_key] is not None:
                # Replace the MD5 Hash with the retrieved registration id
                ph_registration_id = reg_data[PhoneHome._registration_resp_id_key]
         
        self.__logger.debug("returning ph_registration_id: %s", ph_registration_id)
        self.__logger.info("exiting _retrieve_registration_id method")       
        return ph_registration_id
    
    def _retrieve_hub_version(self, csrf_token):
        '''
        Retrieve the version of Hub instance
        
        :param str csrf_token: The CSRF Token to include in the request to retrieve the Hub version
        :return: The Hub version or None
        :rtype: str or None
        '''
        self.__logger.info("entering _retrieve_hub_version method")
        # Get the Hub version
        ver_req_headers = {PhoneHome._csrf_token_key:csrf_token}
        hub_version = None
        try:
            ver_resp = self.__send_request(PhoneHome._version_endpoint, ver_req_headers)
            hub_version = json.load(ver_resp)
        except (urllib2.HTTPError, urllib2.URLError) as e:
            self.__logger.error("received exception: %s", e, exc_info=True)
            pass
        
        self.__logger.debug("returning hub_version: %s", hub_version)
        self.__logger.info("exiting _retrieve_hub_version method")
        return hub_version
    
    def __send_request(self, endpoint, headers, data=None):
        ''' 
        Sends a request to the Hub and returns the response
        
        :param str endpoint: The endpoint to which the request is being made
        :param dict headers: Key/Values pairs of header to add
        :param str data: Data to add to the request. Adding data makes the request a POST. Default is None which makes the request a GET
        :return: HTTP response from sending the request
        :rtype: File-like object with geturl(), info(), and getcode() methods
        :raises urllib2.HTTPError: if there is an error in the HTTP request
        :raises urllib2.URLError: if there is an error in communications
        '''
        self.__logger.info("entering __send_request method")
        request_loc = urljoin(self.__hub_url, endpoint)
        request = Request(request_loc, data, headers)
        response = self.__opener.open(request)
        self.__logger.info("exiting __send_request method")
        return response