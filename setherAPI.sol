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
    
    // Facebook API
    function sth_fb_getAccessToken(string key, string secret) payable returns (string _accessToken);
	
    function sth_fb_getAlbumLikes(string accessToken, string albumId) payable returns (uint32 likes);
    function sth_fb_addAlbumLike(string accessToken, string albumId) payable returns (bool success);
    function sth_fb_getAlbumComments(string accessToken, string albumId) payable returns (string[] comments);
    function sth_fb_addAlbumComment(string accessToken, string albumId, string comment) payable returns (bool success);
	
    function sth_fb_getApplicationFeed(string accessToken, string appId) payable returns (string[] feeds);
    function sth_fb_addToApplicationFeed(string accessToken, string appId, string msg) payable returns (bool success);
    function sth_fb_getApplicationInsights(string accessToken, string appId) payable returns (string[] insights);
    function sth_fb_getApplicationPosts(string accessToken, string appId) payable returns (string[] posts);
    function sth_fb_getApplicationReviews(string accessToken, string appId) payable returns (string[] reviews);
	
    function sth_fb_getCheckin(string accessToken, string checkinId) payable returns (string checkin);
    function sth_fb_getCheckinComments(string accessToken, string checkinId) payable returns (string[] comments);
	function sth_fb_addCheckinComment(string accessToken, string checkinId, string cmnt) payable returns (bool success);
	function sth_fb_getCheckinLikes(string accessToken, string checkinId) payable returns (string[] likes);
	function sth_fb_addCheckinLike(string accessToken, string checkinId) payable returns (bool success);
	
	function sth_fb_getComment(string accessToken, string cmntId) payable returns (string comment);
	function sth_fb_getCommentLikes(string accessToken, string cmntId) payable returns (string[] likes);
	function sth_fb_addCommentLike(string accessToken, string cmntId) payable returns (bool success);
	
	function sth_fb_getEvent(string accessToken, string eventId) payable returns (string evt);
	function sth_fb_getEventFeed(string accessToken, string eventId) payable returns (string[] feed);
	function sth_fb_addToEventFeed(string accessToken, string eventId, string msg) payable returns (bool success);
	function sth_fb_getEventUsers(string accessToken, string eventId, string resposeType) payable returns (string[] users);
	function sth_fb_rsvpEvent(string accessToken, string eventId, string resposeType) payable returns (bool success);
	
	function sth_fb_getFriendList(string accessToken, string listId) payable returns (string details);
	function sth_fb_getFriendListMembers(string accessToken, string listId) payable returns (string[] members);
	
	function sth_fb_getGroup(string accessToken, string groupId) payable returns (string group);
	function sth_fb_getGroupFeed(string accessToken, string groupId) payable returns (string[] feed);
	function sth_fb_addToGroupFeed(string accessToken, string groupId, string msg) payable returns (bool success);
	function sth_fb_getGroupMembers(string accessToken, string groupId) payable returns (string[] members);
	
	function sth_fb_getLink(string accessToken, string linkId) payable returns (string lnk);
	function sth_fb_getLinkComments(string accessToken, string linkId) payable returns (string[] comments);
	function sth_fb_addLinkComment(string accessToken, string linkId, string cmnt) payable returns (bool success);
	function sth_fb_getLinkLikes(string accessToken, string linkId) payable returns (string[] likes);
	function sth_fb_addLinkLike(string accessToken, string linkId) payable returns (bool success);
	
	function sth_fb_getPage(string accessToken, string pageId) payable returns (string page);
	function sth_fb_getPageFeed(string accessToken, string pageId) payable returns (string[] feed);
	function sth_fb_addToPageFeed(string accessToken, string pageId, string msg) payable returns (bool success);
	function sth_fb_getPageLinks(string accessToken, string pageId) payable returns (string[] links);
	function sth_fb_addPageLink(string accessToken, string pageId, string lnk) payable returns (bool success);
	function sth_fb_getPagePosts(string accessToken, string pageId) payable returns (string[] posts);
	function sth_fb_getPageEvents(string accessToken, string pageId) payable returns (string[] events);
	function sth_fb_addPageEvent(string accessToken, string pageId, string eventData) payable returns (bool success);
	function sth_fb_getPageCheckins(string accessToken, string pageId) payable returns (string[] checkins);
	
	function sth_fb_getPhoto(string accessToken, string photoId) payable returns (string photo);
	function sth_fb_getPhotoComments(string accessToken, string photoId) payable returns (string[] comments);
	function sth_fb_addPhotoComment(string accessToken, string photoId, string cmnt) payable returns (bool success);
	function sth_fb_getPhotoLikes(string accessToken, string photoId) payable returns (string[] likes);
	function sth_fb_addPhotoLike(string accessToken, string photoId) payable returns (bool success);
	
	function sth_fb_getPost(string accessToken, string postId) payable returns (string post);
	function sth_fb_getPostComments(string accessToken, string postId) payable returns (string[] comments);
	function sth_fb_addPostComment(string accessToken, string postId, string cmnt) payable returns (bool success);
	function sth_fb_getPostLikes(string accessToken, string postId) payable returns (string[] likes);
	function sth_fb_addPostLike(string accessToken, string postId) payable returns (bool success);
	
	function sth_fb_getStatus(string accessToken, string statusId) payable returns (string statusData);
	function sth_fb_getStatusComments(string accessToken, string statusId) payable returns (string[] comments);
	function sth_fb_addStatusComment(string accessToken, string statusId, string cmnt) payable returns (bool success);
	function sth_fb_getStatusLikes(string accessToken, string statusId) payable returns (string[] likes);
	function sth_fb_addStatusLike(string accessToken, string statusId) payable returns (bool success);
	
	function sth_fb_getUser(string accessToken, string userId) payable returns (string userData);
	function sth_fb_getUserCheckins(string accessToken, string userId) payable returns (string[] checkins);
	function sth_fb_addUserCheckin(string accessToken, string userId, string checkin) payable returns (bool success);
	function sth_fb_getUserEvents(string accessToken, string userId) payable returns (string[] events);
	function sth_fb_addUserEvent(string accessToken, string userId, string eventData) payable returns (bool success);
	function sth_fb_getUserFeed(string accessToken, string userId) payable returns (string[] feed);
	function sth_fb_addToUserFeed(string accessToken, string userId, string msg) payable returns (bool success);
	function sth_fb_getUserFriends(string accessToken, string userId) payable returns (string[] friends);
	function sth_fb_hasUserFriend(string accessToken, string friendId) payable returns (bool friend);
	function sth_fb_getUserGroups(string accessToken, string userId) payable returns (string[] groups);
	function sth_fb_getUserInterests(string accessToken, string userId) payable returns (string[] interests);
	function sth_fb_getUserLikes(string accessToken, string userId) payable returns (string[] likes);
	function sth_fb_hasUserLikedPage(string accessToken, string userId, string pageId) payable returns (bool hasLiked);
	function sth_fb_getUserLinks(string accessToken, string userId) payable returns (string[] links);
	function sth_fb_addUserLink(string accessToken, string userId, string linkData) payable returns (bool success);
	function sth_fb_getUserPosts(string accessToken, string userId) payable returns (string[] posts);
	function sth_fb_addUserPost(string accessToken, string userId, string post) payable returns (bool success);
	function sth_fb_getUserStatuses(string accessToken, string userId) payable returns (string[] statuses);
	function sth_fb_addUserStatus(string accessToken, string userId, string statusMsg) payable returns (bool success);
	
	function sth_fb_getVideo(string accessToken, string videoId) payable returns (string videoData);
	function sth_fb_getVideoComments(string accessToken, string videoId) payable returns (string[] comments);
	function sth_fb_addVideoComment(string accessToken, string videoId, string cmnt) payable returns (bool success);
	function sth_fb_getVideoLikes(string accessToken, string videoId) payable returns (string[] likes);
	function sth_fb_addVideoLike(string accessToken, string videoId) payable returns (bool success);
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
    string sether_network_name;
    
    modifier setherAPI {
        if((address(setherResolver)==0)||(getCodeSize(address(setherResolver))==0))
            sether_setNetwork(networkID_auto);

        if(address(sether) != setherResolver.getAddress())
            sether = SetherI(setherResolver.getAddress());

        _;
    }
    
     modifier coupon(string code){
        sether = SetherI(setherResolver.getAddress());
        sether.useCoupon(code);
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
    
    function sether_setNetworkName(string _network_name) internal {
        sether_network_name = _network_name;
    }
    
    function sether_getNetworkName() internal returns (string) {
        return sether_network_name;
    }
}
