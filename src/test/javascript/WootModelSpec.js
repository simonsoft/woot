// Reference: http://pivotal.github.io/jasmine/

define(
  ['wootmodel'],
  function (WString) {

describe("WOOT Model", function() {

  it("should be able to locally insert characters", function() {
    var model = new WString(1, 1);
    model.localIntegrate("ins", "A", 0);
    model.localIntegrate("ins", "C", 1);
    model.localIntegrate("ins", "B", 1);
    expect(model.text()).toEqual("ABC");
  });

  describe("should be able to locate IDs", function() {

    var model = new WString(1, 1);
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

  describe("can compare IDs via Less Than", function() {

    var model = new WString(1, 1);

    it("Beginning < Ending = true", function() {
      expect(model.idLessThan(model.beginningId, model.endingId)).toBe(true);
    });

    it("Beginning < Beginning = false", function() {
      expect(model.idLessThan(model.beginningId, model.beginningId)).toBe(false);
    });

    it("Ending < Beginning = false", function() {
      expect(model.idLessThan(model.endingId, model.beginningId)).toBe(false);
    });

    it("Ending < Ending = false", function() {
      expect(model.idLessThan(model.endingId, model.endingId)).toBe(false);
    });

    var site1clock1 = { site: 1, clock: 1 };

    it("Beginning < ID = true", function() {
      expect(model.idLessThan(model.beginningId, site1clock1)).toBe(true);
    });

    it("ID < Beginning = false", function() {
      expect(model.idLessThan(site1clock1, model.beginningId)).toBe(false);
    });

    it("ID < Ending = true", function() {
      expect(model.idLessThan(site1clock1, model.endingId)).toBe(true);
    });

    it("Ending < ID = false", function() {
      expect(model.idLessThan(model.endingId,site1clock1)).toBe(false);
    });

  });

  describe("should support example 1 from section 3.5 of WOOT research paper (RR-5580)", function() {

    var site1 = new WString(1, 1);
    var site2 = new WString(2, 1);
    var site3 = new WString(3, 1);

    var op1 = site1.localIntegrate("ins", "1", 0);
    var op2 = site2.localIntegrate("ins", "2", 0);


    site3.remoteIntegrate(op1);
    var op3 = site3.localIntegrate("ins", "3", 0);
    var op4 = site3.localIntegrate("ins", "4", 2);


    it("local integrate should return an OPERATION object", function() {
      expect(op1).toEqual({
        op: 'ins',
        from: 1,
        wchar: {
          alpha: '1',
          id: { site:1, clock:2 },
          prev: { beginning: true },
          next: { ending: true },
          isVisible: true
        }
      });
    });

    it("site3 results in 3124", function() {
      expect(site3.text()).toBe("314");
      expect(site1.idLessThan(op1.wchar.id, op2.wchar.id)).toBe(true);

      site3.remoteIntegrate(op2);
      expect(site3.text()).toBe("3124");
    });

    it("site2 results in 3124", function() {
      site2.remoteIntegrate(op1);
      site2.remoteIntegrate(op3);
      site2.remoteIntegrate(op4);
      expect(site2.text()).toBe("3124");

    });

  });

  describe("Integration should be idempotent", function() {

    it("same char insert integrated twice has no effect", function() {
      var site1 = new WString("site1", 1);
      var op1 = site1.localIntegrate("ins", "1", 0);
      expect(site1.text()).toBe("1");

      site1.remoteIntegrate(op1);
      expect(site1.text()).toBe("1");

      site1.remoteIntegrate(op1);
      expect(site1.text()).toBe("1");
    });

    it("same char deleted twice has no effect", function() {
      var site1 = new WString("site1", 1);
      var op1 = site1.localIntegrate("ins", "1", 0);
      expect(site1.text()).toBe("1");

      var op2 = site1.localIntegrate("del", "1", 0);
      expect(site1.text()).toBe("");

      site1.remoteIntegrate(op2);
      expect(site1.text()).toBe("");

    });


  });

  describe("Deleting", function() {

    var site1 = new WString(1, 1);
    var site2 = new WString(2, 1);

    var op1 = site1.localIntegrate("ins", "A", 0);
    site2.remoteIntegrate(op1);

    var op2 = site1.localIntegrate("del", "A", 0);

    it("should return an OPERATION representation", function() {
      expect(op2).toEqual({
        op: "del",
        from: 1,
        wchar: {
          alpha : 'A',
          id : { site : 1, clock : 2 },
          prev : { beginning : true },
          next : { ending : true },
          isVisible : false }
      });
    });

    it("should make a character invisible", function() {
      expect(site1.text()).toBe("");
      expect(site2.text()).toBe("A");
      site2.remoteIntegrate(op2);
      expect(site2.text()).toBe("");
    });

    it("should queue and later apply operations that cannot immediately complete", function() {

      // Create an insert and delete...
      var site1 = new WString(1, 1);
      var ins = site1.localIntegrate("ins", "A", 0);
      var del = site1.localIntegrate("del", "A", 0);
      expect(site1.text()).toBe("");

      // ...and apply in the wrong order on another site:
      var site2 = new WString(2, 1);
      site2.remoteIntegrate(del);
      site2.remoteIntegrate(ins);
      expect(site2.text()).toBe("");
    });

    it("should trigger queued functions once they are applied", function() {

      var site1 = new WString(1, 1);
      var ins = site1.localIntegrate("ins", "A", 0);
      var del = site1.localIntegrate("del", "A", 0);

      var site2 = new WString(2, 1);

      var fWasRun = false;
      site2.remoteIntegrate(del, function(pos,op) {
        fWasRun = true;
        expect(_.omit(op, 'afterIntegration')).toEqual(del);
        expect(pos).toBe(0);
      });
      expect(fWasRun).toBe(false);

      site2.remoteIntegrate(ins);
      expect(fWasRun).toBe(true);

    });


    it("should be idempotent", function() {

      var site1 = new WString(1, 1);
      var ins = site1.localIntegrate("ins", "A", 0);
      var del = site1.localIntegrate("del", "A", 0);

      var site2 = new WString(2, 1);
      site2.remoteIntegrate(ins);

      var fRunCount = 0;
      site2.remoteIntegrate(del, function(pos,op) {
        fRunCount = fRunCount + 1;
        expect(_.omit(op, 'afterIntegration')).toEqual(del);
        expect(pos).toBe(0);
      });
      expect(fRunCount).toBe(1);

      site2.remoteIntegrate(ins);
      expect(fRunCount).toBe(1);

    });


  });


});

  });