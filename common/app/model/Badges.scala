package model

import conf.{Configuration, Static}
import layout.FaciaContainer

case class Badge(seriesTag: String, imageUrl: String)

object Badges {
  val usElection = Badge("us-news/us-elections-2016", Static("images/USElectionlogooffset.png").path)
  val ausElection = Badge("australia-news/australian-election-2016", Static("images/AUSElectionBadge.png").path)
  // Temporarily Disabled until Editorial confirm
  /* val euElection = Badge("politics/eu-referendum", Static("images/EU_Ref_Logo.svg").path) */
  val euRealityCheck = Badge("politics/series/eu-referendum-reality-check", Static("images/EU_Ref_Logo.svg").path)
  

  val allBadges = Seq(usElection, ausElection, euRealityCheck)

  def badgeFor(c: ContentType) = allBadges.find(badge => c.tags.tags.exists(tag => tag.id == badge.seriesTag))
  def badgeFor(fc: FaciaContainer) = fc.href.flatMap(href => allBadges.find(badge => href == badge.seriesTag))
}
