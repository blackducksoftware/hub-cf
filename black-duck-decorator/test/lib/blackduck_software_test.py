'''
Created on Jul 27, 2017

@author: jfisher
'''
import sys
sys.path.append('../src/lib')
import blackduck_software as bds

from proboscis.asserts import assert_equal
from proboscis import test

@test
class TestTransformApiEndpoint(object):
    """Confirm that blackduck_software.transformApiEndpoint works correctly."""
    
    global expected
    expected = 'test.endpoint.com'
    
    @test
    def testWithPrefixHttp(self):
        prefix = 'http://'
        actual = bds.transform_api_endpoint(str(prefix + expected))
        assert_equal(actual, expected, 'Transform incorrect when prefix is "' + prefix + '"')
    
    @test    
    def testWithPrefixHttps(self):
        prefix = 'https://'
        actual = bds.transform_api_endpoint(str(prefix + expected))
        assert_equal(actual, expected, 'Transform incorrect when prefix is "' + prefix + '"')
    
    @test    
    def testWithNoPrefix(self):
        actual = bds.transform_api_endpoint(expected)
        assert_equal(actual, expected, 'Transform incorrect when no prefix')