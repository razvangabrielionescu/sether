var http = require('http'),
	PropertiesReader = require('properties-reader'),
    httpProxy = require('http-proxy');

var properties = PropertiesReader(__dirname + '/proxy.config');

var portiaBackend = properties.get('proxy.portia.backend');
var spongeBackend = properties.get('proxy.sponge.backend');
var portiaWSBackend = properties.get('proxy.portia.ws.backend');
var spongeWSBackend = properties.get('proxy.sponge.ws.backend');
var socialUrl = properties.get('proxy.social.url');
var selfUrl = properties.get('proxy.self.url');
var listenPort = properties.get('proxy.listen.port');

var proxy = httpProxy.createProxyServer({ws: true});

var server = http.createServer(function(req, res) {
  console.log('Request: '+req.url);

  if (req.url.startsWith("/api") || req.url.startsWith("/server_capabilities")
							|| req.url.startsWith("/ws") || req.url.startsWith("/proxy")) {
		proxy.web(req, res, { target: portiaBackend });
  } else {
		if (req.url.startsWith("/static")) {
			  req.url = req.url.replace('/static', '/sponge/webui');
			  proxy.web(req, res, { target: selfUrl });
		} else if (req.url.startsWith("/assets")) {
			  req.url = req.url.replace('/assets', '/sponge/webui/assets');
			  proxy.web(req, res, { target: selfUrl });
		} else if (req.url.startsWith("/social")) {
              //req.url = req.url.replace("/social", "");
              proxy.web(req, res, {target: socialUrl});
        } else {
			  proxy.web(req, res, { target: spongeBackend });
		}
  }
});

server.on('upgrade', function (req, socket, head) {
  console.log('Websocket upgrade '+req.url+" "+req.headers.host);
  if (req.url.startsWith("/ws")) {
	console.log('Proxy ws to WebUI');
	proxy.ws(req, socket, head, {target: portiaWSBackend});
  } else {
	console.log('Proxy ws to Sponge');
	proxy.ws(req, socket, head, {target: spongeWSBackend});
  }
});

proxy.on('error', function(e) {
  console.log('Error: Maybe some server isn\'t up '+e);
});

console.log("Proxy listening on port "+listenPort);
server.listen(listenPort, "0.0.0.0");

