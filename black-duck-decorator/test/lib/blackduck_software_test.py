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