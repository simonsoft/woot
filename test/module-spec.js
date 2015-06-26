
var expect = require('chai').expect;

var woot = require('../woot');

describe("woot module", function() {

  it("exports the low level WString", function() {
    expect(woot.WString).to.exist.and.be.a('function');
  });

});
