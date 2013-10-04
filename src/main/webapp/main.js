define(
  [
    "wootmodel",
    "jquery-1.10.2.min",
    "ace-builds/src-noconflict/ace"
  ],
  function(WString) {

    function existy(x) { return x != null }

    //var trace = function() { console.log.apply(console, arguments); };
    var trace = function() {};

    var onAir = true; // Are we broadcasting changes?

    // Execute a block without broadcasting the change
    function offAir(block) {
      var was = onAir;
      onAir = false;
      block();
      onAir = was;
    }

    var editor = ace.edit("editor");
    editor.setTheme("ace/theme/merbivore");
    editor.getSession().getDocument().setNewLineMode("unix");
    editor.getSession().setMode("ace/mode/markdown");

    // Convert ACE "position" (row/column) to WOOT "index":
    function idx(position) {
      return editor.getSession().getDocument().positionToIndex(position);
    }

    // Covert a WOOT "index" into an ACE "position"
    function pos(idx) {
      return editor.getSession().getDocument().indexToPosition(idx);
    }

    /* -- From server to client functions: remote integration -------------------------------------- */

    // Convert a WOOT operation to an ACE delta object for WOOT index i:
    function asDelta(op, i) {
      //console.log("Delta ",op.wchar.alpha,i,pos(i));
      return {
        action: op.op == "ins" ? "insertText" : "removeText",
        range: {
          start: pos(i),
          end: pos(i + op.wchar.alpha.length)
        },
        text: op.wchar.alpha
      };
    }

    function isDocumentMessage(v) { return existy(v.chars); }

    function isOpToIntegrate(v,model) {
      return v.wchar && v.wchar.id && v.from !== model.site;
    }

    // Just after a remote operation is integrated, this is what we want to happen:
    var afterRemoteIntegration = function(ipos, op) {
      var delta = asDelta(op, ipos);
      offAir(function() {
        editor.getSession().getDocument().applyDeltas([delta]);
      });
    };

    // The handler will first be called with a WString, and from then on just with an operation
    var messageHandler = function(v) {
      trace("Handling: ", v);
      if (isDocumentMessage(v)) { 
        model.init(v.site, v.clockValue, v.chars, v.queue);
        offAir(function() { editor.getSession().getDocument().setValue(model.text()); });
      } 
      else if (isOpToIntegrate(v,model)) model.remoteIntegrate(v, afterRemoteIntegration);
      else trace("Ignoring ", v);
    };

    var shutdownHandler = function() {
      alert("Remote has closed.");
    };


    /* -- From client to server functions: local integration ----------------------------------------- */


    // TODO: would it be better if model was created, somehow, during wootServer.init?
    var model = new WString(1, 1);
    wootServer.init({ docId: "1" }).then(messageHandler).done(shutdownHandler);

    // `text` is of arbitrary size. For now we serialize as individual operations:
    var broadcast = function(op, text, range) {
      var base = idx(range.start), len = (idx(range.end) - base);

      // When removing multiple character, the position for delete does not change:
      function charpos(p) { return op == "ins" ? base+p : base; }

      for(var p=0; p<len; p++)
        broadcast1(op, text[p], charpos(p));
    };

    var broadcast1 = function(opType, ch, pos) {
      trace("Broadcasting: ",opType," on ",ch," @ ",pos);
      var op = model.localIntegrate(opType, ch, pos);
      wootServer.send(op);
    };

    function cat(lines) {
      var nl = editor.getSession().getDocument().getNewLineCharacter();
      var combined =
       (_.chain(lines)
          .map(function(line) {return line+nl; })
          .reduce(function(acc,line) { return acc+line; })
          .value());
      //console.log("Combined", combined);
      return combined;

    }

    var aceCommands = {
      insertText:  function(text,range) { broadcast("ins", text, range); },
      removeText:  function(text,range) { broadcast("del", text, range); },
      insertLines: function(text,range,event) { aceCommands.insertText(cat(event.data.lines), range); },
      removeLines: function(text,range,event) { console.log("TODO: removelines"); }
    };

    function dispatch(f, text, range, event) {
      //console.log(event.data);
      _.isFunction(f) ? f(text, range, event) : trace("Ignoring Command ",event.data.action);
    }

    editor.getSession().on('change', function(e) {
      if (onAir) dispatch(aceCommands[e.data.action], e.data.text, e.data.range, e);
    });


  });