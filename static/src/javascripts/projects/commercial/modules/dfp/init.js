define([
    'Promise',
    'qwery',
    'bonzo',
    'raven',
    'common/utils/config',
    'common/utils/fastdom-promise',
    'common/modules/commercial/commercial-features',
    'commercial/modules/build-page-targeting',
    'commercial/modules/dfp/dfp-env',
    'commercial/modules/dfp/on-slot-render',
    'commercial/modules/dfp/on-slot-load',
    'commercial/modules/dfp/PrebidService',
    'commercial/modules/dfp/ophan-tracking',

    // These are cross-frame protocol messaging routines:
    'commercial/modules/messenger/get-stylesheet',
    'commercial/modules/messenger/resize',
    'commercial/modules/messenger/scroll',
    'commercial/modules/messenger/viewport',
    'commercial/modules/messenger/click',
    'commercial/modules/messenger/background'
], function (Promise, qwery, bonzo, raven, config, fastdom, commercialFeatures,
             buildPageTargeting, dfpEnv, onSlotRender, onSlotLoad, PrebidService,
             ophanTracking) {

    return init;

    function init() {

        var removeAdSlots = function () {
            bonzo(qwery(dfpEnv.adSlotSelector)).remove();
        };

        if (commercialFeatures.dfpAdvertising) {
            var initialTag = dfpEnv.sonobiEnabled ? setupSonobi() : Promise.resolve();
            return initialTag.then(setupAdvertising).catch(function(){
                // A promise error here, from a failed module load,
                // could be a network problem or an intercepted request.
                // Abandon the init sequence.
                return fastdom.write(removeAdSlots);
            });
        }

        return fastdom.write(removeAdSlots);
    }

    function setupSonobi() {
        return new Promise.resolve(require(['js!sonobi.js']));
    }

    function setupAdvertising() {

        return new Promise(function(resolve) {

            if (dfpEnv.sonobiEnabled) {
                // Just load googletag. Sonobi's wrapper will already be loaded, and googletag is already added to the window by sonobi.
                require(['js!googletag.js']);
                ophanTracking.addTag('sonobi');
            } else {
                require(['js!googletag.js']);

                if (dfpEnv.prebidEnabled) {
                    dfpEnv.prebidService = new PrebidService();
                    ophanTracking.addTag('prebid');
                } else {
                    ophanTracking.addTag('waterfall');
                }
            }

            window.googletag.cmd.push = raven.wrap({deep: true}, window.googletag.cmd.push);

            window.googletag.cmd.push(
                setListeners,
                setPageTargeting,
                resolve
            );
        });
    }

    function setListeners() {
        ophanTracking.trackPerformance(window.googletag);

        var pubads = window.googletag.pubads();
        pubads.addEventListener('slotRenderEnded', raven.wrap(onSlotRender));
        pubads.addEventListener('slotOnload', raven.wrap(onSlotLoad));
    }

    function setPageTargeting() {
        var pubads = window.googletag.pubads();
        var targeting = buildPageTargeting();
        Object.keys(targeting).forEach(function (key) {
            pubads.setTargeting(key, targeting[key]);
        });
    }
});
