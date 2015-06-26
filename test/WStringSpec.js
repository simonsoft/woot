
var ns = global;

var test = {
};

ns.define = function(deps, module) {
  console.warn('Ignoring this test:', deps, typeof module);
};

require('../src/test/javascript/WootModelSpec');
