// <SETHER_API>
/*
Copyright (c) 2015-2017 MWARE SOLUTIONS SRL

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/

pragma solidity ^0.4.0;

pragma solidity ^0.4.0;

contract SetherI {
    address public cbAddress;
    
    function useCoupon(string _coupon);
    function setCustomGasPrice(uint _gasPrice);
}

contract SetherResolverI {
    function getAddress() returns (address _addr);
}

contract usingSether {
    uint8 constant networkID_auto = 0;
    uint8 constant networkID_mainnet = 1;
    uint8 constant networkID_testnet = 2;
    uint8 constant networkID_morden = 2;
    uint8 constant networkID_consensys = 161;
    
    SetherResolverI setherResolver;
    SetherI sether;
    
    modifier setherAPI {
        if((address(setherResolver)==0)||(getCodeSize(address(setherResolver))==0))
            sether_setNetwork(networkID_auto);

        if(address(sether) != setherResolver.getAddress())
            sether = SetherI(setherResolver.getAddress());

        _;
    }
    
     modifier coupon(string code){
        oraclize = SetherI(setherResolver.getAddress());
        oraclize.useCoupon(code);
        _;
    }
    
    function sether_setNetwork(uint8 networkID) internal returns(bool) {
        if (getCodeSize(0x1d3B2638a7cC9f2CB3D298A3DA7a90B67E5506ed)>0) { //mainnet
            setherResolver = SetherResolverI(0x1d3B2638a7cC9f2CB3D298A3DA7a90B67E5506ed);
            sether_setNetworkName("eth_mainnet");
            return true;
        }
        if (getCodeSize(0xc03A2615D5efaf5F49F60B7BB6583eaec212fdf1)>0){ //ropsten testnet
            setherResolver = SetherResolverI(0xc03A2615D5efaf5F49F60B7BB6583eaec212fdf1);
            sether_setNetworkName("eth_ropsten3");
            return true;
        }
        if (getCodeSize(0xB7A07BcF2Ba2f2703b24C0691b5278999C59AC7e)>0){ //kovan testnet
            setherResolver = SetherResolverI(0xB7A07BcF2Ba2f2703b24C0691b5278999C59AC7e);
            sether_setNetworkName("eth_kovan");
            return true;
        }
        if (getCodeSize(0x146500cfd35B22E4A392Fe0aDc06De1a1368Ed48)>0){ //rinkeby testnet
            setherResolver = SetherResolverI(0x146500cfd35B22E4A392Fe0aDc06De1a1368Ed48);
            sether_setNetworkName("eth_rinkeby");
            return true;
        }
        if (getCodeSize(0x6f485C8BF6fc43eA212E93BBF8ce046C7f1cb475)>0){ //ethereum-bridge
            setherResolver = SetherResolverI(0x6f485C8BF6fc43eA212E93BBF8ce046C7f1cb475);
            return true;
        }
        if (getCodeSize(0x20e12A1F859B3FeaE5Fb2A0A32C18F5a65555bBF)>0){ //ether.camp ide
            setherResolver = SetherResolverI(0x20e12A1F859B3FeaE5Fb2A0A32C18F5a65555bBF);
            return true;
        }
        if (getCodeSize(0x51efaF4c8B3C9AfBD5aB9F4bbC82784Ab6ef8fAA)>0){ //browser-solidity
            setherResolver = SetherResolverI(0x51efaF4c8B3C9AfBD5aB9F4bbC82784Ab6ef8fAA);
            return true;
        }
        return false;
    }
    
    function __callback(bytes32 myid, string result) {
    }
    
    function sether_useCoupon(string code) setherAPI internal {
        sether.useCoupon(code);
    }
    
    function getCodeSize(address _addr) constant internal returns(uint _size) {
        assembly {
            _size := extcodesize(_addr)
        }
    }
}
