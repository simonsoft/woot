/*
 An implementation of WOOT, providing just the model and integration functions.  You need to
 wire this into your own editor and transport mechanism.

 Depends on underscore.js

 Example usage:

 // Empty model for site 1, clock value 0.
 var model = WOOT.WString(1, 0)

 // Locally insert "A" at position 0.
 // This returns an operation you can send to your peers.
 var op = model.localIntegrate("ins", "A", 0);

 The data structures of interest:

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


 // If you receive an operation:
 model.remoteIntegrate(op);

 This will update the model, possibly adding the operation to
 a local queue if it cannot be applied yet.

 Queue operations are automatically tested on any remoteIntegrate call.

 See src/test/javascript/WootModelSpec.js for more usage.

 */
define(
  [
    'underscore-min'
  ],
  function() {

    function WString(s,g) {
      this.site = s;
      this.clockValue = g;
      this.chars = [];
      this.queue = [];
    }

    WString.prototype = {

      init: function (site, initClockValue, cs, qs) {
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
        return  _.pluck(this.visible(), 'alpha').join("");
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

      idLessThan: function(a,b) {
        if (a.beginning && b.beginning || a.ending && b.ending) return false;
        else return (
          b.beginning ? false :
            a.beginning ? true :
              b.ending ? true :
                a.ending ? false :
                  (a.site < b.site) || (a.site === b.site && a.clock < b.clock) );
      },

      // The index into `chars` of the given ID; or -1 if not found.
      indexOf: function(id) {
        if (id.beginning) return 0;
        else if (id.ending) return this.chars.length;
        else return this.indexWhere(_.pluck(this.chars,'id'), function(a) {
            return (a.site === id.site && a.clock === id.clock);
          });
      },

      // The first index in `col` where `pred` is true for an element of `col`; or -1 otherwise.
      // pred : Id => Boolean
      indexWhere: function(col, pred) {
        if (false === _.isEmpty(col))
          for (var i=0; i < col.length; i++)
            if (pred(col[i])) return i;

        return -1;
      },

      // op => WString
      remoteIntegrate: function(rop) {
        // Clone to avoid sharing state between different instances of WString in the same JS interpreter
        var op = { op: rop.op, wchar: _.clone(rop.wchar) };
        console.log("INTEGRATION OF REMOTE OP ", op);
        if (this.canIntegrateOp(op)) {
          if (op.op === "ins") this.integrateIns(op.wchar, op.wchar.prev, op.wchar.next);
          else if (op.op == "del") this.hide(op.wchar.id);
          else console.log("ERR: Unrecognised op:", op.op, op);
        } else {
          console.log("Queueing");
          this.queue.push(op); // mutate
        }
        return this;
      },

      subseq: function(prev,next) {
        var from = prev.beginning ? 0 : this.indexOf(prev) + 1,
          until = next.ending ? this.chars.length : this.indexOf(next);
        return this.chars.slice(from,until);
      },

      // Id => Boolean
      exists: function(id) {
        return this.indexOf(id) != -1;
      },

      // WChar => Boolean
      canIntegrate: function(wchar) {
        return this.exists(wchar.next) && this.exists(wchar.prev);
      },

      // Op => Boolean
      canIntegrateOp: function(op) {
        return op.op == "ins" ? this.canIntegrate(op.wchar) : this.exists(op.wchar.id);
      },

      // Lowest-level primitive insert into the local character array
      ins: function(wchar, pos) {
        console.log("Insert ", wchar, " at ", pos);
        this.chars.splice(pos, 0, wchar); // mutate

        // de-queue any now valid waiting operations:
        var op = _.find(this.queue, _.bind(this.canIntegrateOp, this));

        if (op) {

          function eq(x) {
            return x.op === op.op &&
              x.wchar.id.site === op.wchar.id.site &&
              x.wchar.id.clock === op.wchar.id.clock;
          }

          this.queue = _.reject(this.queue, eq); // mutate
          this.remoteIntegrate(op);
        }

      },

      hide: function(id) {
        this.chars[this.indexOf(id)].isVisible = false; // mutate
      },

      // (WChar, Id, Id) => Unit
      integrateIns: function(wchar, before, after) {
        var s = this.subseq(before, after);
        if (_.isEmpty(s)) this.ins(wchar, this.indexOf(after));
        else {

          var L = [before].concat(_.pluck(this.reduce(s),'id'));
          L.push(after);

          var i = 1;
          while (i < (L.length-1) && this.idLessThan(L[i],wchar.id)) i++;

          this.integrateIns(wchar, L[i-1], L[i]);
        }
      },

      // List[Id] => List[Id]
      reduce: function(cs) {

        function idNotEqual(a,b) {
          return (a.beginning && b.beginning) ? false :
            (a.ending && b.ending) ? false :
              (a.site != b.site || a.clock != b.clock);
        }

        return _.filter(cs, function(c) {
          return _.every(cs, function(x) {
            return idNotEqual(x.id, c.next) && idNotEqual(x.id, c.prev);
          })
        })
      },

      // (String, String, Int) => Operation
      localIntegrate: function (op, ch, pos) {

        if (op === "ins") {

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

        }
        else if (op === "del") {
          var existingChar = this.ithVisible(pos);
          existingChar.isVisible = false; // mutate
          console.log(this.asString());
          return { op: "del", wchar: existingChar };
        }
        else {
          console.log("ERR: Unrecognized local operation ", op);
          return {};
        }


      }

    };


    return WString;
  }
);

