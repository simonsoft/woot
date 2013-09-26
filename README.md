# JavaScript and Scala Implementations of WOOT

WOOT is a collaborative text editing algorithm, allowing multiple users ("sites") to insert or delete characters (`WChar`) from a shared document (`WString`). The algorithm preserves the intention of users, and ensures that the text converges to the same state for all users.

Its key properties are simplicity, and avoiding the need for a reliable network or vector clocks (it can be peer-to-peer).

The key references are:

* Oster _et al._ (2005) _Real time group editors without Operational transformation_, report paper 5580, INRIA - [PDF](http://www.loria.fr/~oster/pmwiki/pub/papers/OsterRR05a.pdf)

* Oster _et al._ (2006) _Data Consistency for P2P Collaborative Editing_, CSCW'06 - [PDF](http://hal.archives-ouvertes.fr/docs/00/10/85/23/PDF/OsterCSCW06.pdf)

WOOT stands for With Out [Operational Transforms](https://en.wikipedia.org/wiki/Operational_transform).

-------------------


# JavaScript

The JavaScript implementation is of the _model_: you need to bring your own editor and transport mechanism.

You'll find the code in `src/main/webapp/wootmodel.js` and it uses UnderscoreJS and RequireJs.

## Example usage

    // Empty model for site 1, clock value 0.
    var model = WOOT.WString(1, 0)

    // Locally insert "A" at position 0.
    // This returns an operation you can send to your peers.
    var op = model.localIntegrate("ins", "A", 0);

    // If you receive an operation:
    model.remoteIntegrate(op);

This will update the model (the data-structure is mutable), possibly adding the operation to a local queue if it cannot be applied yet.

Queue operations are automatically tested on any `remoteIntegrate` call.


## Data Structures

* ID

      {
       site: N,
       clock: N
      }

* WCHAR
	
	  {
	   id: ID,
	   alpha: "X",
	   isVisible: true,
	   prev: ID,
	   next: ID
      }

* OPERATION:
 
	  {
	   op: "ins" (or "del"),
	   wchar: WCHAR
	  }

## Jasmine unit tests

To run the Jasmine unit test, open `src/test/javascript/SpecRunner.html` in a browser.

-------------------

# Scala

## How to run the code

You don't, yet.  There is a unit test:

    $ sbt test
    
There will be a [Lift](http://liftweb.net) application, which you can start with:

    $ sbt 
    $ container:start
    
This includes an editor and a transport, giving you a working shared editor in localhost:8080.


