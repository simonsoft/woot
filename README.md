# JavaScript and Scala Implementations of WOOT

### For the impatient

To run the demo:

    $ ./sbt
    > container:start
   
...then  open: [http://127.0.0.1:8080](http://127.0.0.1:8080).  This will give you a shared document, using the [ACE](http://ace.c9.io/) editor.


# What is WOOT?

WOOT is a collaborative text editing algorithm, allowing multiple users ("sites") to insert or delete characters (`WChar`) from a shared document (`WString`). The algorithm preserves the intention of users, and ensures that the text converges to the same state for all users.

Its key properties are simplicity, and avoiding the need for a reliable network or vector clocks (it can be peer-to-peer).

The key references are:

* Oster _et al._ (2005) _Real time group editors without Operational transformation_, report paper 5580, INRIA - [PDF](http://www.loria.fr/~oster/pmwiki/pub/papers/OsterRR05a.pdf)

* Oster _et al._ (2006) _Data Consistency for P2P Collaborative Editing_, CSCW'06 - [PDF](http://hal.archives-ouvertes.fr/docs/00/10/85/23/PDF/OsterCSCW06.pdf)

WOOT stands for With Out [Operational Transforms](https://en.wikipedia.org/wiki/Operational_transform).

-------------------


# JavaScript

The JavaScript implementation is of the _model_: you need to bring your own editor and transport mechanism.

You'll find the code in `src/main/webapp/wootmodel.js` and it uses [Underscore.js](http://underscorejs.org/) and [RequireJS](http://requirejs.org/).

## Example usage

    // Empty model for site A, clock value 0.
    var model = new WString("A", 0)

    // Locally insert "a" at position 0.
    // This returns an operation you can send to your peers.
    var op = model.localIntegrate("ins", "a", 0);

    // If you receive an operation:
    model.remoteIntegrate(op, function(p) {
      if (op.op == "del")
        console.log("Delete the char at pos ", p);
   	  else
   	    console.log("Insert ", op.wchar.alpha, " at ", p);
    });


The `remoteIntegrate` call will update the model (the data-structure is mutable). It may not happen immediately. For example, if the model cannot integrate the remote operation, the operation will go onto a queue and be applied when preconditions are satisfied.

Queued operations are automatically tested on any `remoteIntegrate` call. You don't have to work the WOOT queue yourself.

For a full example of using the library via RequireJS, see `src/main/webapp/index.html` which imports code via `main.js`.


## Data Structures

### ID

      {
       site: "X",
       clock: N
      }
### WCHAR
	
	  {
	   id: ID,
	   alpha: "X",
	   isVisible: true,
	   prev: ID,
	   next: ID
      }

### OPERATION
 
	  {
	   op: "ins" (or "del"),
	   from: "X" (site id),
	   wchar: WCHAR
	  }

## Jasmine unit tests

To run the [Jasmine](https://jasmine.github.io/) tests, open `src/test/javascript/SpecRunner.html` in a browser.

-------------------

# Scala

The Scala version of the model is intended to be a passive peer in a collaborative editing session.  As such the `integrate` method is a remote integrate, and there is no equivalent of the JavaScript `localIntegrate` function. (Only because I don't need it -- it wouldn't be hard to add at all.)

The Scala `WString` data structure is immutable, which is another difference from the JavaScript version.

## Example usage

    import spiralarm.woot._
    val s = WString()
    val c = WChar(CharId("A",1), 'a', Beginning, Ending)
    val s_prime = s.integrate(InsOperation(c, from="A"))
    s_prime.text == "a"

## Documentation

To build the documentation:

    $ ./docco.sh

and then open the files in the _docs_ folder.


## How to run the code

There are unit tests:

    $ sbt test
    
There is a [Lift](http://liftweb.net/) application, which you can start with:

    $ sbt 
    > container:start
    
This is a single document example using the [ACE](http://ace.c9.io/) editor, with Lift providing
 the transport.  Running this example gives you a shared editor at [http://127.0.0.1:8080](http://127.0.0.1:8080).


-------------------

# Debugging in the browser 

If you see either of these errors in your chrome console...

  * GET http://localhost:8080/jquery-1.10.2.min.map 404 (Not Found) 
  * GET http://localhost:8080/underscore-min.map 404 (Not Found) 

view this [Stackoverflow question](http://stackoverflow.com/questions/18365315/jquerys-jquery-1-10-2-min-map-is-triggering-a-404-not-found) for a solution.
