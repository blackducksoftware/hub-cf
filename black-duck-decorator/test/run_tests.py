# blackduck software
# This REQUIRES the Proboscis module be installed for Python
# Use 'sudo pip install proboscis'
'''
Created on Jul 26, 2017

@author: jfisher
'''

def run_tests():
    from proboscis import TestProgram
    from lib import blackduck_software_test
    
    # Run Proboscis and exit.
    TestProgram().run_and_exit()
    
if __name__ == '__main__':
    run_tests()