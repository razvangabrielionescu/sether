import Ember from 'ember';

const { keys } = Object;

export default Ember.Service.extend({
    isFastBoot: Ember.computed.reads('_fastBoot.isFastBoot'),

    _fastBoot: Ember.computed(function() {
        let owner = Ember.getOwner(this);
        return owner.lookup('service:fastboot');
    }),

    _document: Ember.computed(function() {
        return document;
    }),

    _documentCookies: Ember.computed(function() {
        let all = this.get('_document.cookie').split(';');
        let filtered = this._filterDocumentCookies(Ember.A(all));

        return filtered.reduce((acc, cookie) => {
            if (!Ember.isEmpty(cookie)) {
                let [key, value] = cookie;
                acc[key.trim()] = value.trim();
            }
            return acc;
        }, {});
    }).volatile(),

    _fastBootCookies: Ember.computed(function() {
        let fastBootCookiesCache = this.get('_fastBootCookiesCache');

        if (!fastBootCookiesCache) {
            let fastBootCookies = this.get('_fastBoot.request.cookies');
            fastBootCookiesCache = Ember.A(keys(fastBootCookies)).reduce((acc, name) => {
                let value = fastBootCookies[name];
                acc[name] = { value };
                return acc;
            }, {});
            this.set('_fastBootCookiesCache', fastBootCookiesCache);
        }

        return this._filterCachedFastBootCookies(fastBootCookiesCache);
    }).volatile(),

    read(name) {
        let all;
        if (this.get('_isFastBoot')) {
            all = this.get('_fastBootCookies');
        } else {
            all = this.get('_documentCookies');
        }

        if (name) {
            return this._decodeValue(all[name]);
        } else {
            Ember.A(keys(all)).forEach((name) => (all[name] = this._decodeValue(all[name])));
            return all;
        }
    },

    write(name, value, options = {}) {
        Ember.assert('Cookies cannot be set to be HTTP-only as those cookies would not be ' +
            'accessible by the Ember.js application itself when running in the browser!',
            !options.httpOnly);
        Ember.assert("Cookies cannot be set as signed as signed cookies would not be " +
            "modifyable in the browser as it has no knowledge of the express server's signing key!",
            !options.signed);
        Ember.assert('Cookies cannot be set with both maxAge and an explicit' +
            ' expiration time!', Ember.isEmpty(options.expires) || Ember.isEmpty(options.maxAge));
        value = this._encodeValue(value);

        if (this.get('_isFastBoot')) {
            this._writeFastBootCookie(name, value, options);
        } else {
            this._writeDocumentCookie(name, value, options);
        }
    },

    clear(name, options = {}) {
        Ember.assert('Expires or Max-Age options cannot be set when clearing cookies',
            Ember.isEmpty(options.expires) && Ember.isEmpty(options.maxAge));

        options.expires = new Date('1970-01-01');
        this.write(name, null, options);
    },

    _writeDocumentCookie(name, value, options = {}) {
        let serializedCookie = this._serializeCookie(name, value, options);
        this.set('_document.cookie', serializedCookie);
    },

    _writeFastBootCookie(name, value, options = {}) {
        let responseHeaders = this.get('_fastBoot.response.headers');
        let serializedCookie = this._serializeCookie(...arguments);

        if (!Ember.isEmpty(options.maxAge)) {
            options.maxAge *= 1000;
        }

        this._cacheFastBootCookie(...arguments);

        responseHeaders.append('set-cookie', serializedCookie);
    },

    _cacheFastBootCookie(name, value, options = {}) {
        let fastBootCache = this.getWithDefault('_fastBootCookiesCache', {});
        let cachedOptions = Ember.merge({}, options);

        if (cachedOptions.maxAge) {
            let expires = new Date();
            expires.setSeconds(expires.getSeconds() + options.maxAge);
            cachedOptions.expires = expires;
            delete cachedOptions.maxAge;
        }

        fastBootCache[name] = { value, options: cachedOptions };
        this.set('_fastBootCookiesCache', fastBootCache);
    },

    _filterCachedFastBootCookies(fastBootCookiesCache) {
        let { path: requestPath, protocol } = this.get('_fastBoot.request');

        // cannot use deconstruct here
        let host = this.get('_fastBoot.request.host');

        return Ember.A(keys(fastBootCookiesCache)).reduce((acc, name) => {
            let { value, options } = fastBootCookiesCache[name];
            options = options || {};

            let { path: optionsPath, domain, expires, secure } = options;

            if (optionsPath && requestPath.indexOf(optionsPath) !== 0) {
                return acc;
            }

            if (domain && host.indexOf(domain) + domain.length !== host.length) {
                return acc;
            }

            if (expires && expires < new Date()) {
                return acc;
            }

            if (secure && protocol !== 'https') {
                return acc;
            }

            acc[name] = value;
            return acc;
        }, {});
    },

    _encodeValue(value) {
        if (Ember.isNone(value)) {
            return '';
        } else {
            return encodeURIComponent(value);
        }
    },

    _decodeValue(value) {
        if (Ember.isNone(value)) {
            return value;
        } else {
            return decodeURIComponent(value);
        }
    },

    _filterDocumentCookies(unfilteredCookies) {
        return unfilteredCookies.map((c) => c.split('='))
            .filter((c) => Ember.isPresent(c[0]) && Ember.isPresent(c[1]));
    },

    _serializeCookie(name, value, options = {}) {
        let cookie = `${name}=${value}`;

        if (!Ember.isEmpty(options.domain)) {
            cookie = `${cookie}; domain=${options.domain}`;
        }
        if (Ember.typeOf(options.expires) === 'date') {
            cookie = `${cookie}; expires=${options.expires.toUTCString()}`;
        }
        if (!Ember.isEmpty(options.maxAge)) {
            cookie = `${cookie}; max-age=${options.maxAge}`;
        }
        if (options.secure) {
            cookie = `${cookie}; secure`;
        }
        if (!Ember.isEmpty(options.path)) {
            cookie = `${cookie}; path=${options.path}`;
        }

        return cookie;
    }
});
