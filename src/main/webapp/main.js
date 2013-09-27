define(
  [
    "wootmodel",
    "jquery-1.10.2.min",
    "ace-builds/src-noconflict/ace"
  ],
  function(WString) {

    function existy(x) { return x != null }

    var silence = function() {};
    var trace = silence;

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
    editor.getSession().setMode("ace/mode/markdown");

    // Convert ACE "position" (row/column) to WOOT "index":
    function idx(position) {
      return editor.getSession().getDocument().positionToIndex(position);
    }

    // Covert a WOOT "index" into an ACE "position"
    function pos(idx) {
      return editor.getSession().getDocument().indexToPosition(idx);
    }

    // Convert a WOOT operation to an ACE delta object for WOOT index i:
    function asDelta(op, i) {
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
      if (existy(delta)) offAir(function() {
        editor.getSession().getDocument().applyDeltas([delta]);
      });
    };

    // The handler will first be called with a WString, and from then on just with an operation
    var messageHandler = function(v) {
      trace("Handling: ", v);
      if (isDocumentMessage(v)) model.init(v.site, v.clockValue, v.chars, v.queue);
      else if (isOpToIntegrate(v,model)) model.remoteIntegrate(v, afterRemoteIntegration);
      else trace("Ignoring ", v);
    };

    var shutdownHandler = function() {
      alert("Remote has closed.");
    };

    // TODO: would it be better if model was created, somehow, during wootServer.init?
    var model = new WString(1, 1);
    wootServer.init({ name: "shared doc 1"}).then(messageHandler).done(shutdownHandler);

    // `text` is of arbitrary size. For now we serialize as individual operations:
    var broadcast = function(op, text, range) {
      var base = idx(range.start), len = (idx(range.end) - base);
      for(var p=0; p<len; p++)
        broadcast1(op, text[p], base+p);
    };

    var broadcast1 = function(op, ch, pos) {
      trace("Broadcasting: ",op," on ",ch," @ ",pos);
      var op = model.localIntegrate(op, ch, pos);
      wootServer.send(op);
    };

    var normalizeOpName = function(aceEventName) {
      return aceEventName === "insertText" ? "ins" : "del" ;
    };

    editor.getSession().on('change', function(e) {
      if (onAir && (e.data.action == "insertText" || e.data.action == "removeText"))
        broadcast(normalizeOpName(e.data.action), e.data.text, e.data.range);
    });


  });