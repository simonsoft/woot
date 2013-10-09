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

    function silence() {}

    var trace = silence;
    var err = console.log;
    var warn = function() { if (console && console.log) console.log.apply(console, arguments); };

    WString.prototype = {

      init: function (site, initClockValue, cs, qs) {
        this.site = site;
        this.clockValue = initClockValue;
        this.chars = cs || [];  // Stores WCHAR structures
        this.queue = qs || [];  // Stores OPERATION structures
      },

      genId: function() {
        return { site: this.site, clock: this.tick() };
      },

      endingId: { ending: true },

      beginningId: { beginning: true },

      tick: function () {
        this.clockValue = this.clockValue + 1;
        return this.clockValue;
      },

      visible: function() {
        return _.where(this.chars, { isVisible: true});
      },

      text: function() {
        return  _.pluck(this.visible(), 'alpha').join("");
      },

      ithVisible: function(i) {
        return this.visible()[i];
      },

      prevOf: function(visiblePos) {
        if (visiblePos == 0) return this.beginningId;
        else return this.ithVisible(visiblePos-1).id;
      },

      nextOf: function(visiblePos) {
        if (visiblePos >= this.visible().length) return this.endingId;
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

      visibleIndexOf: function(id) {
        return this.indexOf(id, this.visible());
      },

      // The index into `chars` (by default) of the given ID; or -1 if not found.
      indexOf: function(id, col) {
        if (id.beginning) return 0;
        else {
          var cs = col || this.chars;
          if (id.ending) return cs.length;
          else return this.indexWhere(_.pluck(cs, 'id'), function(a) {
            return (a.site === id.site && a.clock === id.clock);
          });
        }
      },

     tryDequeue: function() {
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

      // The first index in `col` where `pred` is true for an element of `col`; or -1 otherwise.
      indexWhere: function(col, pred) {
        if (false === _.isEmpty(col))
          for (var i=0; i < col.length; i++)
            if (pred(col[i])) return i;

        return -1;
      },

      // Integrate rop into this WString, calling function f after integration if integration happened.
      remoteIntegrate: function(rop, f) {
        trace("Integration of remote op: ", rop);

        // Clone to avoid sharing state between different instances of WString in the same JS interpreter
        // Also, push the function into the operation in case it is queued for later.
        var op = _.extend(_.clone(rop),
          { wchar: _.clone(rop.wchar) },
          f && _.isFunction(f) ? { afterIntegration: f } : {} );

        if (this.canIntegrateOp(op)) {
          if (op.op === "ins") {
            if (this.indexOf(op.wchar.id) != -1) {
              warn("Skipping already integrated char", op.wchar.id);
            } else {
              this.integrateIns(op.wchar, op.wchar.prev, op.wchar.next);
              var ipos = this.visibleIndexOf(op.wchar.id);
              if (op.afterIntegration) op.afterIntegration(ipos,op,this);
              this.tryDequeue();
            }
          }
          else if (op.op == "del") {
            var dpos = this.visibleIndexOf(op.wchar.id);
            var wchar = this.chars[this.indexOf(op.wchar.id)];
            if (wchar.isVisible) {
              this.hide(op.wchar.id);
              if (op.afterIntegration) op.afterIntegration(dpos,op,this);
            }
          }
          else {
            err("Unrecognised op:", op.op, op);
          }
        } else {
          this.queue.push(op); // mutate
        }
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
        trace("Insert ", wchar, " at ", pos);
        this.chars.splice(pos, 0, wchar); // mutate
      },

      hide: function(id) {
        this.chars[this.indexOf(id)].isVisible = false; // mutate
      },

      // (WChar, Id, Id) => Unit
      integrateIns: function(wchar, before, after) {
        var s = this.subseq(before, after);
        if (_.isEmpty(s)) {
          this.ins(wchar, this.indexOf(after));
        }
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

          return { op: op, from: this.site, wchar: _.clone(newChar) };
        }
        else if (op === "del") {
          var existingChar = this.ithVisible(pos);
          existingChar.isVisible = false; // mutate
          return { op: op, from: this.site, wchar: existingChar };
        }
        else {
          err("Unrecognised local operation type", op);
          return {};
        }

      }

    };

    return WString;
  }
);
