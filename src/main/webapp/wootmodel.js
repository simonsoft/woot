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


  /*
   Wire formats:

   ID
   {
    site: N,
    clock: N
   }

   WCHAR
   {
    id: ID,
    alpha: "X",
    isVisible: true,
    prev: ID,
    next: ID
   }

   OPERATION:
    {
      op: "ins" (or "del"),
      wchar: WCHAR
    }

  */

  WString.fn = WString.prototype = {

    init: function (site, initClockValue, cs, qs) {
      this.site = site;
      this.clockValue = initClockValue;
      this.chars = cs || [];  // Stores WCHAR structures
      this.queue = qs || [];  // Stores OPERATION structures
    },

    genId: function() {
      return { site: this.site, clock: this.tick() };
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

    remoteIntegrate: function(op) {
      console.log("INTEGRATION OF REMOTE OP ", op);
      if (op.op === "ins" && this.canIntegrate(op.wchar)) this.integrateIns(op.wchar, op.wchar.prev, op.wchar.next)
      else if (op.op === "del" && this.canIntegrateId(op.char.id)) this.hide(op.char.id)
      else {
        console.log("Queueing");
        this.queue.push(op); // mutate
      }
      return op;
    },

    subseq: function(prev,next) {
      var from = prev === this.beginningId() ? 0 : this.indexOf(prev) + 1,
          until = next === this.endingId() ? this.chars.length : this.indexOf(next);
        return this.chars.slice(from,until);
    },

    // TODO: this does not work. index of is -1 for a site/clock that exists in char
    canIntegrateId: function(id) {
      console.log("Checking can integrate on ",id, " results in ", this.indexOf(id));
      return this.indexOf(id) != -1;
    },

    canIntegrate: function(wchar) {
      return this.canIntegrateId(wchar.next) && this.canIntegrateId(wchar.prev);
    },

    ins: function(wchar, pos) {
      console.log("Insert ", wchar, " at ", pos);
      this.chars.splice(pos, 0, wchar); // mutate

      console.log(this.chars);
      console.log(this.asString());
      // TODO: try to dequeue

    },

    hide: function(id) {
      this.chars[this.indexOf(id)].isVisible = false; // mutate
    },

    // mutate
    integrateIns: function(wchar, prev, next) {
     console.log("INTEGRATE INS");
      var s = this.subseq(prev,next);
      if (_.isEmpty(s)) this.ins(wchar, this.indexOf(next));
      else {
        console.log("IT'S A BIT MORE COMPLICATED"); //TODO
      }
    },

    // Generate a WChar, such as:
    // { op: "ins", alpha: "x", id: { site:1, clock: 13}, prev: { site: 1, clock: 11 }, next: { ending: true } }
    localIntegrate: function (op, ch, pos) {
      if (op === "ins") {
        console.log("LOCAL INS ", ch, " at ", pos);

        // Generate a new wchar:
        var prevId = this.prevOf(pos), nextId = this.nextOf(pos);
        var newChar = {
          alpha: ch,
          id: this.genId(),
          prev: prevId,
          next: nextId,
          isVisible: true
        };

        // Insert the wchar locally (mutate):
        this.chars.splice(this.indexOf(nextId), 0, newChar);

        console.log(this.asString());
        return { op: "ins", wchar: newChar };

      } else if (op === "del") {
        console.log("LOCAL DEL ", ch, " at ", pos);
        var existingChar = this.ithVisible(pos);
        existingChar.isVisible = false; // mutate
        console.log(this.asString());
        return existingChar;
      }

    }

  };

  WString.fn.init.prototype = WString.fn;

}(window, document));
