package bootstrap.liftweb

import java.util.Random

import net.liftweb._
import net.liftweb.common._
import net.liftweb.http._
import net.liftweb.sitemap._
import net.liftweb.util._
import net.liftweb.util.Helpers._
import woots.snippet.Broadcaster
import woots.snippet.RemoveSite

/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot {
  def boot {
   
    // where to search snippet
    LiftRules.addToPackages("woots")

    // Build SiteMap
    def sitemap = SiteMap(
      Menu.i("Home") / "index"
      )

    // set the sitemap.  Note if you don't want access control for
    // each page, just comment this line out.
    LiftRules.setSiteMapFunc(() => sitemap)

    //Show the spinny image when an Ajax call starts
    LiftRules.ajaxStart =
      Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)
    
    // Make the spinny image go away when it ends
    LiftRules.ajaxEnd =
      Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

    // Force the request to be UTF-8
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))

    // Use HTML5 for rendering
    LiftRules.htmlProperties.default.set((r: Req) => new Html5Properties(r.userAgent))
   
    //Useful testing session timeouts.
    //LiftRules.sessionInactivityTimeout.default.set(Full(30L*1000))

    //Clean up session related information in the broadcaster.
	LiftSession.onShutdownSession ::= ((s : LiftSession) => { Broadcaster ! RemoveSite( s.uniqueId)})	  
	  
  }
}
