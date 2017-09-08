'''
Created on Sep 8, 2017

@author: jfisher
'''
import cookielib
import hashlib
import json
import urllib
from urllib2 import Request
import urllib2
from urlparse import urljoin


class PhoneHome(object):
    '''
    This static class encapsulates the functionality required to send plugin usage data back to Black Duck Software
    '''
    _phoneHomeServer = 'https://collect.blackducksoftware.com'
    
    _authEndpoint = '/j_spring_security_check'
    _registrationEndpoint = '/api/v1/registrations'
    _versionEndpoint = '/api/v1/current-version'
    
    _csrfTokenKey = 'X-CSRF-TOKEN'
    _registrationRespIdKey = 'registrationId'
    
    _regIdKey = 'regId'
    _sourceKey = 'source'
    _blackDuckNameKey = 'blackDuckName'
    _blackDuckVersionKey = 'blackDuckVersion'
    _thirdPartyNameKey = 'thirdPartyName'
    _thirdPartyVersionKey = 'thirdPartyVersion'
    _pluginVersionKey = 'pluginVersion'
    _infoMapKey = 'infoMap'
    
    _hubName = 'Hub'
    
    @staticmethod    
    def call(hubUrl, hubUsername, hubPassword, source, pluginVersion, thirdPartyName, thirdPartyVersion='N/A'):
        '''
        Attempt to send data about plugin usage to Black Duck Software.
        This should fail silently if there is an issue so that its usage is unobtrusive to the caller
        
        :param hubUrl URL of the hub instance (scheme://address[:port])
        :param hubUsername Valid user of the hub instance
        :param hubPassword Valid password for the user
        :param source Black Duck integration type (i.e. "Integrations", "Alliance Integrations"
        :param pluginVersion Version of the plugin on which to report usage
        :param thirdPartyName Identifier of the third party software into which Black Duck is integrated
        :param thirdPartyVersion Version of the third party software into which Black Duck is integrated
        :type hubUrl: string
        :type hubUsername: string
        :type hubPassword: string
        :type source: string
        :type pluginVersion: string
        :type thirdPartyName: string
        :type thirdPartyVersion: string
        '''
        try:
            if thirdPartyName is None or not thirdPartyName:
                return
            if pluginVersion is None or not pluginVersion:
                return
            if source is None or not source:
                return

            # Create a Cookie Handler
            cj = cookielib.CookieJar()
            handlers=[
                urllib2.HTTPHandler(),
                urllib2.HTTPSHandler(),
                urllib2.HTTPCookieProcessor(cj)
                ]
            # Setup the handlers for HTTP/HTTPS requests with support for cookies
            opener = urllib2.build_opener(*handlers)
        
            # Authenticate with Hub instance
            authLoc = urljoin(hubUrl, PhoneHome._authEndpoint)
            authReq = Request(authLoc)
            authData = urllib.urlencode({'j_username':hubUsername, 'j_password':hubPassword})
            authReq.add_data(authData)
            authReq.add_header('Content-Type', 'application/x-www-form-urlencoded')
            authResp = None
            try:
                authResp = opener.open(authReq)
            except (urllib2.HTTPError, urllib2.URLError):
                pass
            
            # Only continue if the authorization response has data
            if authResp is not None:
                csrfToken = authResp.info()[PhoneHome._csrfTokenKey]
                
                # Only continue if there was a CSRF token in the response
                if csrfToken is not None:
                    # Attempt to get registration id
                    regLoc = urljoin(hubUrl, PhoneHome._registrationEndpoint)
                    regReq = Request(regLoc)
                    regReq.add_header(PhoneHome._csrfTokenKey, csrfToken)
                    regData = None
                    try:
                        regResp = opener.open(regReq)
                        regData = json.load(regResp)
                    except (urllib2.HTTPError, urllib2.URLError):
                        pass
                    
                    phRegId = None
                    if regData and regData[PhoneHome._registrationRespIdKey] is not None:
                        phRegId = regData[PhoneHome._registrationRespIdKey]
                    else:
                        md5 = hashlib.md5()
                        md5.update(hubUrl)
                        phRegId = md5.hexdigest()
                    
                    # Get the Hub version
                    verLoc = urljoin(hubUrl, PhoneHome._versionEndpoint)
                    verReq = Request(verLoc)
                    verReq.add_header(PhoneHome._csrfTokenKey, csrfToken)
                    verData = None
                    try:
                        verResp = opener.open(verReq)
                        verData = json.load(verResp)
                    except (urllib2.HTTPError, urllib2.URLError):
                        pass
                    
                    # Only continue if we have a phRegId and verData
                    if phRegId is not None and verData is not None:
                        # Build the Phone Home request
                        phReqInfo = {}
                        phReqInfo[PhoneHome._regIdKey] = str(phRegId)
                        phReqInfo[PhoneHome._sourceKey] = source
                        infoMap = {}
                        infoMap[PhoneHome._blackDuckNameKey] = PhoneHome._hubName
                        infoMap[PhoneHome._blackDuckVersionKey] = verData
                        infoMap[PhoneHome._thirdPartyNameKey] = thirdPartyName
                        infoMap[PhoneHome._thirdPartyVersionKey] = thirdPartyVersion
                        infoMap[PhoneHome._pluginVersionKey] = pluginVersion
                        phReqInfo[PhoneHome._infoMapKey] = infoMap
                        
                        phReqData = json.dumps(phReqInfo)
                        phReq = Request(PhoneHome._phoneHomeServer)
                        phReq.add_data(phReqData)
                        phReq.add_header('Content-Type', 'application/json')
                        try:
                            opener.open(phReq)
                        except (urllib2.HTTPError, urllib2.URLError):
                            pass
            
        except RuntimeError:
            # This should fail completely silent 
            pass