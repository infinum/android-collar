package co.infinum.collar.sample;

public class Foo {

    public Foo() {
        Collar.trackEvent(new AnalyticsEvent.Foo());
    }

    public void trackFoo() {
        Collar.trackEvent(new AnalyticsEvent.TrackFoo());
    }
}
