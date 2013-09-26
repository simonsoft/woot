define(
  [
    "wootmodel",
    "jquery-1.10.2.min",
    "ace-builds/src-noconflict/ace"
  ],
  function(WString) {

    var editor = ace.edit("editor");
    editor.setTheme("ace/theme/merbivore");
    editor.getSession().setMode("ace/mode/markdown");

    function isDefined(v) { return !(_.isUndefined(v)); };

    function isDocumentMessage(v) { return isDefined(v.chars); };

    function isOpToIntegrate(v,model) {
      return v.wchar && v.wchar.id && v.wchar.id.site !== model.site;
    };

    // The handler will first be called with a WString, and from then on just with an operation
    var messageHandler = function(v) {
      console.log("HANDING  ", v);
      if (isDocumentMessage(v)) model.init(v.site, v.clockValue, v.chars, v.queue);
      else if (isOpToIntegrate(v,model)) model.remoteIntegrate(v);
      else console.log("Ignoring ", v);
    };

    var shutdownHandler = function() {
      console.log("Remote is done");
    };

    // TODO: would it be better if model was created, somehow, during wootServer.init?

    var model = new WString(1, 1);
    wootServer.init({ name: "shared doc 1"}).then(messageHandler).done(shutdownHandler);

    // Convert ACE "position" (row/column) to WOOT "index":
    var idx = function(position) {
      return editor.getSession().getDocument().positionToIndex(position);
    };

    // `text` is of arbitrary size: for now we serialize to individual operations:
    var broadcast = function(op, text, range) {
      var base = idx(range.start), len = (idx(range.end) - base);
      for(var p=0; p<len; p++)
        broadcast1(op, text[p], base+p);
    };

    var broadcast1 = function(op, ch, pos) {
      console.log("BROADCASTING ",op," on ",ch," @ ",pos);
      var op = model.localIntegrate(op, ch, pos);
      wootServer.send(op);
    };

    var normalizeOpName = function(aceEventName) {
      return aceEventName === "insertText" ? "ins" : "del" ;
    };

    editor.getSession().on('change', function(e) {
      if (e.data.action == "insertText" || e.data.action == "removeText")
        broadcast(normalizeOpName(e.data.action), e.data.text, e.data.range);
    });


  });