define([
    'fastdom',
    'common/utils/$',
    'common/utils/config',
    'common/views/svgs',
    'common/modules/ui/toggles',
    'template!common/views/commercial/creatives/paidfor-content.html'
], function (
    fastdom,
    $,
    config,
    svgs,
    Toggles,
    paidforTpl
) {

    var PaidforContent = function ($adSlot, params) {
        this.$adSlot = $adSlot;
        this.params  = params;
    };

    PaidforContent.prototype.create = function () {
        var $component;

        this.params.icon = svgs('arrowdownicon');
        this.params.infoLinkIcon = svgs('arrowRight');
        this.params.glabsLogoSmall = svgs('glabsLogoSmall');
        this.params.dataAttr = Math.floor(Math.random() * 1000);
        this.params.glabsLink = document.location.origin + (config.page.edition === 'AU' ? '/guardian-labs-australia' : '/guardian-labs');

        $component = $.create(paidforTpl(this.params));

        fastdom.write(function () {
            $component.appendTo(this.$adSlot);
            new Toggles(this.$adSlot[0]).init();

            if (this.params.trackingPixel) {
                this.$adSlot.before('<img src="' + this.params.trackingPixel + this.params.cacheBuster + '" class="creative__tracking-pixel" height="1px" width="1px"/>');
            }
        }, this);
    };

    return PaidforContent;

});
