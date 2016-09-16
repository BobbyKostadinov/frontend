package staticpages

import model.{GuardianContentTypes, SectionSummary, MetaData, SimplePage}

object StaticPages {
  def simpleSurveyStaticPageForId(id: String): SimplePage = SimplePage(
    MetaData.make(
      id = id,
      section = Option(SectionSummary(id="global", activeBrandings=None)),
      webTitle = "Guardian Survey Page",
      analyticsName = "global",
      contentType = "survey",
      iosType = None,
      shouldGoogleIndex = false))

  def subscriberNumberPage: SimplePage = SimplePage(
    MetaData.make(
      id = "subscriber-number-page",
      section = Option(SectionSummary(id="global", activeBrandings=None)),
      webTitle = "Subscriber number form",
      analyticsName = "subscriber-number-page",
      contentType = GuardianContentTypes.NetworkFront,
      iosType = None,
      shouldGoogleIndex = false))

    def simpleEmailSignupPage(id: String, webTitle: String): SimplePage = SimplePage(
      MetaData.make(
        id = id,
        section = Option(SectionSummary(id="global", activeBrandings=None)),
        webTitle = webTitle,
        analyticsName = "global",
        // a currently running AB test is using `contentType = "survey"` change this to "signup" after 2016-09-13
        contentType = "survey",
        iosType = None,
        shouldGoogleIndex = false))

      def simpleNewslettersPage(id: String): SimplePage = SimplePage(
        MetaData.make(
          id = id,
          section = Option(SectionSummary(id="global", activeBrandings=None)),
          webTitle = "Sign up for Guardian emails",
          analyticsName = "newsletter-signup-page",
          contentType = "signup",
          iosType = None,
          shouldGoogleIndex = false))
}
