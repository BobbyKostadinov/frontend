@()

/* Source: https://github.com/remy/polyfills/blob/master/classList.js */
(function (window, document) {

    if (typeof window.Element === "undefined" || "classList" in document.documentElement) return;

    var prototype = Array.prototype,
        push = prototype.push,
        splice = prototype.splice,
        join = prototype.join;

    /**
     * @@constructor
     */
    function DOMTokenList(el) {
        this.el = el;
        // In SVGs the className is an object instead of a string
        // https://www.w3.org/TR/SVG/single-page.html#types-InterfaceSVGStylable
        var className = el.className;
        if ('baseVal' in className) {
            className = className.baseVal;
        }
        // The className needs to be trimmed and split on whitespace
        // to retrieve a list of classes.
        var classes = className.replace(/^\s+|\s+$/g, '').split(/\s+/);
        for (var i = 0; i < classes.length; i++) {
            push.call(this, classes[i]);
        }
    };

    function set (el, string) {
        if ('baseVal' in el.className) {
            el.className.baseVal = string;
        } else {
            el.className = string;
        }
    }

    DOMTokenList.prototype = {
        add: function (token) {
            if (this.contains(token)) return;
            push.call(this, token);
            set(this.el, this.toString());
        },
        contains: function (token) {
            return this.indexOf(token) != -1;
        },
        item: function (index) {
            return this[index] || null;
        },
        remove: function (token) {
            if (!this.contains(token)) return;
            for (var i = 0; i < this.length; i++) {
                if (this[i] == token) break;
            }
            splice.call(this, i, 1);
            set(this.el, this.toString());
        },
        toString: function () {
            return join.call(this, ' ');
        },
        toggle: function (token) {
            if (!this.contains(token)) {
                this.add(token);
            } else {
                this.remove(token);
            }

            return this.contains(token);
        }
    };

    window.DOMTokenList = DOMTokenList;

    function defineElementGetter(obj, prop, getter) {
        if (Object.defineProperty) {
            Object.defineProperty(obj, prop, {
                get: getter
            });
        } else {
            obj.__defineGetter__(prop, getter);
        }
    }

    defineElementGetter(Element.prototype, 'classList', function () {
        return new DOMTokenList(this);
    });

})(window, document);
