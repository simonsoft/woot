describe("WOOT Model", function() {

  it("should be able to locally insert characters", function() {
    var model = new WOOT.WString(1, 1);
    model.localIntegrate("ins", "A", 0);
    model.localIntegrate("ins", "C", 1);
    model.localIntegrate("ins", "B", 1);
    expect(model.asString()).toEqual("ABC");
  });

  describe("should be able to locate IDs", function() {

    var model = new WOOT.WString(1, 1);
    model.localIntegrate("ins", "A", 0);
    model.localIntegrate("ins", "C", 1);
    model.localIntegrate("ins", "B", 1);

    it("beginning ID", function() {
      expect(model.indexOf({ beginning: true}) ).toEqual(0);
    });

    it("ending ID", function() {
      expect(model.indexOf({ ending: true}) ).toEqual(3);
    });

    it("other IDs", function() {
      // NB: Insert order was ACB, but resulting positions are at ABC
      expect(model.indexOf({ site: 1, clock: 2}) ).toEqual(0);
      expect(model.indexOf({ site: 1, clock: 4}) ).toEqual(1);
      expect(model.indexOf({ site: 1, clock: 3}) ).toEqual(2);
    });

    it("fail to find IDs that don't exist", function() {
      expect(model.indexOf({ site: 10, clock: 2}) ).toEqual(-1);
    });



  });


});