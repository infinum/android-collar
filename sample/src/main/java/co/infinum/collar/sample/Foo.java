package co.infinum.collar.sample;

public class Foo {

    public Foo() {
        CollarAnalyticsEvent.trackEvent(new AnalyticsEvent.Foo());
    }

    public void trackFoo() {
        CollarAnalyticsEvent.trackEvent(new AnalyticsEvent.TrackFoo());
    }
}
