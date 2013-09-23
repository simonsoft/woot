(function (window, document) {

  var oldWOOT = window.WOOT, WOOT = {};

  WOOT.noConflict = function () {
    window.WOOT = oldWOOT;
    return this;
  };

  window.WOOT = WOOT;

  // TODO: initial chars
  var WString = WOOT.WString = function (siteId, initClockValue) {
    return new WOOT.WString.fn.init(siteId, initClockValue);
  };


  WString.fn = WString.prototype = {

    init: function (siteId, initClockValue) {
      this.siteId = siteId;
      this.clockValue = initClockValue;
      this.queue = [];
      this.chars = [];
    },

    genId: function() {
      return { site: this.siteId, clock: this.tick() };
    },

    endingId: function() {
      return { ending: true };
    },

    beginningId: function() {
      return { beginning: true };
    },

    tick: function () {
      this.clockValue = this.clockValue + 1;
      return this.clockValue;
    },

    visible: function() {
      return _.where(this.chars, { isVisible: true});
    },

    asString: function() {
      return  _.pluck(this.visible(), 'alpha').join();
    },

    ithVisible: function(i) {
      return this.visible()[i];
    },

    prevOf: function(visiblePos) {
      if (visiblePos == 0) return this.beginningId();
      else return this.ithVisible(visiblePos-1).id;
    },

    nextOf: function(visiblePos) {
      if (visiblePos >= this.visible().length) return this.endingId();
      else return this.ithVisible(visiblePos).id; // Not +1 because we are inserting just before this char
    },

    indexOf: function(id) {
      if (id.beginning) return 0;
      else if (id.ending) return this.chars.length;
      else return _.indexOf(_.pluck(this.chars,'id'), id);
    },

    integrate: function (op) {
      console.log("INTEGRATING ", op);
      return op;
    },

    // Generate a WChar, such as:
    // { op: "ins", alpha: "x", id: { site:1, clock: 13}, prev: { site: 1, clock: 11 }, next: { ending: true } }
    localIntegrate: function (op, pos) {
      if (op.op === "ins") {
        console.log("LOCAL INS ",op, " at ",pos);

        // Generate a new wchar:
        var prevId = this.prevOf(pos), nextId = this.nextOf(pos);
        var wchar = _.extend(op, {
          id: this.genId(),
          prev: prevId,
          next: nextId,
          isVisible: true
        });

        // Insert the wchar locally (mutate):
        this.chars.splice(this.indexOf(nextId), 0, wchar);

        console.log(this.asString());
        return wchar;

      } else if (op.op === "del") {
        console.log("LOCAL DEL ", op, " at ", pos);
        var wchar = this.ithVisible(pos);
        wchar.isVisible = false; // mutate
        console.log(this.asString());
        return wchar;
      }


    }


  };

  WString.fn.init.prototype = WString.fn;


}(window, document));
