
var ns = global;

var woot = {
};

ns.define = function(deps, module) {
  if (deps.length !== 1 || !/^underscore/.test(deps[0])) {
    throw new Error("Unexpected woot deps");
  }
  ns._ = require('underscore');
  woot.WString = module();
};

require('./src/main/webapp/wootmodel');

module.exports = woot;
