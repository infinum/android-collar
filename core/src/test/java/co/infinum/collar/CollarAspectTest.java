package co.infinum.collar;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import co.infinum.collar.annotations.Attribute;
import co.infinum.collar.annotations.ConstantAttribute;
import co.infinum.collar.annotations.ConstantAttributes;
import co.infinum.collar.annotations.ConvertAttribute;
import co.infinum.collar.annotations.ConvertAttributes;
import co.infinum.collar.annotations.TrackAttribute;
import co.infinum.collar.annotations.TrackEvent;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@SuppressWarnings("unused")
public class CollarAspectTest {

    @Mock ProceedingJoinPoint joinPoint;
    @Mock MethodSignature methodSignature;

    private final Map<String, Object> superAttributes = new HashMap<>();

    private CollarAspect aspect;
    private TrackEvent trackEvent;
    private Map<String, Object> attributes;

    @SuppressWarnings("AccessStaticViaInstance")
    @Before
    public void setup() {
        initMocks(this);

        AspectListener aspectListener = new AspectListener() {
            @Override
            public void onAspectEventTriggered(TrackEvent trackEvent, Map<String, Object> attributes) {
                CollarAspectTest.this.trackEvent = trackEvent;
                CollarAspectTest.this.attributes = attributes;
            }

            @Override
            public void onAspectSuperAttributeAdded(String key, Object value) {
                superAttributes.put(key, value);
            }

            @Override
            public void onAspectSuperAttributeRemoved(String key) {
                superAttributes.remove(key);
            }
        };

        aspect = new CollarAspect();
        aspect.observe(aspectListener);

        when(joinPoint.getSignature()).thenReturn(methodSignature);
    }

    private Method invokeMethod(Class<?> klass, String methodName, Class<?>... parameterTypes) throws Throwable {
        Method method = initMethod(klass, methodName, parameterTypes);
        Object instance = new Object();
        when(joinPoint.getThis()).thenReturn(instance);

        aspect.weaveJoinPointTrackEvent(joinPoint);
        return method;
    }

    private Method initMethod(Class<?> klass, String name, Class<?>... parameterTypes) throws Throwable {
        Method method = klass.getMethod(name, parameterTypes);
        when(methodSignature.getMethod()).thenReturn(method);
        return method;
    }

    @Test
    public void trackEventWithoutAttributes() throws Throwable {
        class Foo {

            @TrackEvent("title")
            public void foo() {
                // some code
            }
        }
        invokeMethod(Foo.class, "foo");

        assertTrack()
            .event("title")
            .noFilters()
            .noTags()
            .noAttributes();
    }

    @Test
    public void useReturnValueAsAttribute() throws Throwable {
        class Foo {

            @TrackEvent("title")
            @Attribute("key")
            public String foo() {
                return "test";
            }
        }

        when(joinPoint.proceed()).thenReturn("test");
        invokeMethod(Foo.class, "foo");

        assertTrack()
            .event("title")
            .noTags()
            .noFilters()
            .attribute("key", "test");
    }

    @Test
    public void useReturnValueAndParametersAsAttributes() throws Throwable {
        class Foo {

            @TrackEvent("title")
            @Attribute("key1")
            public String foo(@Attribute("key2") String param) {
                return "test";
            }
        }

        when(joinPoint.proceed()).thenReturn("test");
        when(joinPoint.getArgs()).thenReturn(new Object[] {"param"});
        invokeMethod(Foo.class, "foo", String.class);

        assertTrack()
            .event("title")
            .noFilters()
            .noTags()
            .attribute("key1", "test")
            .attribute("key2", "param");
    }

    @Test
    public void useDefaultValueWhenThereIsNoReturnValue() throws Throwable {
        class Foo {

            @TrackEvent("title")
            @Attribute(value = "key1", defaultValue = "defaultValue")
            public void foo() {
                // some code
            }
        }
        invokeMethod(Foo.class, "foo");

        assertTrack()
            .event("title")
            .noFilters()
            .noTags()
            .attribute("key1", "defaultValue");
    }

    @Test
    public void useReturnValueWhenItIsNotNull() throws Throwable {
        class Foo {

            @TrackEvent("title")
            @Attribute(value = "key1", defaultValue = "defaulValue")
            public String foo() {
                return "returnValue";
            }
        }
        when(joinPoint.proceed()).thenReturn("returnValue");
        invokeMethod(Foo.class, "foo");

        assertTrack()
            .event("title")
            .noFilters()
            .noTags()
            .attribute("key1", "returnValue");
    }

    @Test
    public void useDefaultValueWhenParameterValueIsNull() throws Throwable {
        class Foo {

            @TrackEvent("title")
            public void foo(@Attribute(value = "key1", defaultValue = "default") String val) {
                // some code
            }
        }

        when(joinPoint.getArgs()).thenReturn(new Object[] {null});
        invokeMethod(Foo.class, "foo", String.class);

        assertTrack()
            .event("title")
            .noFilters()
            .noTags()
            .attribute("key1", "default");
    }

    @Test
    public void constantAttributeOnMethodScope() throws Throwable {
        class Foo {

            @TrackEvent("title")
            @ConstantAttribute(key = "key1", value = "value")
            public String foo() {
                return "returnValue";
            }
        }
        invokeMethod(Foo.class, "foo");

        assertTrack()
            .event("title")
            .noFilters()
            .noTags()
            .attribute("key1", "value");
    }

    @Test
    public void constantAttributeOnClassScope() throws Throwable {
        @ConstantAttributes({
            @ConstantAttribute(key = "key1", value = "value1"),
            @ConstantAttribute(key = "key2", value = "value2")
        })
        @ConstantAttribute(key = "key3", value = "value3")
        class Foo {

            @TrackEvent("title")
            @ConstantAttribute(key = "key4", value = "value4")
            public void foo() {
                // some code
            }
        }
        invokeMethod(Foo.class, "foo");

        assertTrack()
            .event("title")
            .noFilters()
            .noTags()
            .attribute("key1", "value1")
            .attribute("key2", "value2")
            .attribute("key3", "value3")
            .attribute("key4", "value4");
    }

    @Test
    public void constantAttributeAndAttributeAtSameTime() throws Throwable {
        class Foo {

            @TrackEvent("title")
            @Attribute("key1")
            @ConstantAttribute(key = "key2", value = "value2")
            public String foo() {
                return "value1";
            }
        }

        when(joinPoint.proceed()).thenReturn("value1");
        invokeMethod(Foo.class, "foo");

        assertTrack()
            .event("title")
            .noFilters()
            .noTags()
            .attribute("key1", "value1")
            .attribute("key2", "value2");
    }

    @Test
    public void constantAttributes() throws Throwable {
        class Foo {

            @TrackEvent("title")
            @ConstantAttributes({
                @ConstantAttribute(key = "key1", value = "value1"),
                @ConstantAttribute(key = "key2", value = "value2")
            })
            @ConstantAttribute(key = "key3", value = "value3")
            public void foo() {
                // some code
            }
        }
        invokeMethod(Foo.class, "foo");

        assertTrack()
            .event("title")
            .noFilters()
            .noTags()
            .attribute("key1", "value1")
            .attribute("key2", "value2")
            .attribute("key3", "value3");
    }

    @Test
    public void superAttribute() throws Throwable {
        class Foo {

            @TrackEvent("title")
            @Attribute(value = "key1", isSuper = true)
            public String foo(@Attribute(value = "key2", isSuper = true) String value) {
                return "value1";
            }
        }

        when(joinPoint.proceed()).thenReturn("value1");
        when(joinPoint.getArgs()).thenReturn(new Object[] {"value2"});

        invokeMethod(Foo.class, "foo", String.class);

        assertThat(superAttributes).containsExactly("key1", "value1", "key2", "value2");
    }

    @Test
    public void superConstantAttribute() throws Throwable {
        class Foo {

            @TrackEvent("title")
            @ConstantAttributes({
                @ConstantAttribute(key = "key1", value = "value1"),
                @ConstantAttribute(key = "key2", value = "value2", isSuper = true)
            })
            @ConstantAttribute(key = "key3", value = "value3", isSuper = true)
            public String foo() {
                return "returnValue";
            }
        }

        when(joinPoint.proceed()).thenReturn("value1");
        invokeMethod(Foo.class, "foo");

        assertThat(superAttributes).containsExactly("key2", "value2", "key3", "value3");
    }

    @Test
    public void superConvertAttribute() throws Throwable {
        class Foo {

            @TrackEvent("event")
            @ConvertAttributes(
                keys = {0, 1},
                values = {"value1", "value2"}
            )
            @ConvertAttribute(value = "key1", isSuper = true)
            public int foo(@ConvertAttribute(value = "key2", isSuper = true) Integer val) {
                return 0;
            }
        }

        when(joinPoint.proceed()).thenReturn(0);
        when(joinPoint.getArgs()).thenReturn(new Object[] {1});
        invokeMethod(Foo.class, "foo", Integer.class);

        assertThat(superAttributes).containsExactly("key1", "value1", "key2", "value2");
    }

    @Test
    public void trackable() throws Throwable {
        class Bar implements Trackable {

            @Override
            public Map<String, Object> trackableAttributes() {
                Map<String, Object> values = new HashMap<>();
                values.put("key1", "value1");
                values.put("key2", "value2");
                return values;
            }
        }

        class Foo {

            @TrackEvent("title")
            public void foo(@TrackAttribute Bar bar) {
                // some code
            }
        }

        when(joinPoint.getArgs()).thenReturn(new Object[] {new Bar()});

        invokeMethod(Foo.class, "foo", Bar.class);

        assertTrack()
            .event("title")
            .noFilters()
            .noTags()
            .attribute("key1", "value1")
            .attribute("key2", "value2");
    }

    @Test
    public void ignoreNullValuesOnTrackable() throws Throwable {
        class Bar implements Trackable {

            @Override
            public Map<String, Object> trackableAttributes() {
                return null;
            }
        }

        class Foo {

            @TrackEvent("title")
            public void foo(@TrackAttribute Bar bar) {
                // some code
            }
        }

        when(joinPoint.getArgs()).thenReturn(new Object[] {new Bar()});

        invokeMethod(Foo.class, "foo", Bar.class);

        assertTrack()
            .event("title")
            .noFilters()
            .noTags()
            .noAttributes();
    }

    @Test
    public void throwExceptionWhenTrackableAnnotationNotMatchWithValue() throws Throwable {

        class Foo {

            @TrackEvent("title")
            public void foo(@TrackAttribute String bar) {
                // some code
            }
        }

        when(joinPoint.getArgs()).thenReturn(new Object[] {"sdfsd"});

        try {
            invokeMethod(Foo.class, "foo", String.class);

            Assert.fail("Should throw exception");
        } catch (Exception e) {
            assertThat(e).hasMessageThat().matches("Trackable interface must be implemented for the parameter type");
        }
    }

    @Test
    public void methodParameterWithoutAnnotation() throws Throwable {
        class Foo {

            @TrackEvent("title")
            public void foo(@Attribute("Key") String bar, String param2) {
                // some code
            }
        }

        when(joinPoint.getArgs()).thenReturn(new Object[] {"sdfsd"});

        invokeMethod(Foo.class, "foo", String.class, String.class);

        try {
            aspect.weaveJoinPointTrackEvent(joinPoint);
        } catch (Exception e) {
            Assert.fail("Method parameters without annotation should be accepted");
        }
    }

    @Test
    public void classWideAttributeInAnonymousClass() throws Throwable {
        @ConstantAttribute(key = "key1", value = "value1")
        class Foo {

            @ConstantAttribute(key = "key2", value = "value2")
            class Inner {

                @TrackEvent("title")
                public void bar() {
                    // some code
                }
            }
        }

        invokeMethod(Foo.Inner.class, "bar");

        assertTrack()
            .event("title")
            .noFilters()
            .noTags()
            .attribute("key1", "value1")
            .attribute("key2", "value2");
    }

    @Test
    public void convertAttributeForParameters() throws Throwable {
        class Foo {

            @TrackEvent("event")
            @ConvertAttributes(
                keys = {0, 1},
                values = {"value1", "value2"}
            )
            public void foo(@ConvertAttribute("key1") Integer type) {
                // some code
            }
        }

        when(joinPoint.getArgs()).thenReturn(new Object[] {0});
        invokeMethod(Foo.class, "foo", Integer.class);

        assertTrack()
            .event("event")
            .noFilters()
            .noTags()
            .attribute("key1", "value1");
    }

    @Test
    public void convertAttributeMapInvalidState() throws Throwable {
        class Foo {

            @TrackEvent("event")
            @ConvertAttributes(
                keys = {0, 1},
                values = {"value1"}
            )
            public void foo(@ConvertAttribute("key1") Integer type) {
                // some code
            }
        }

        when(joinPoint.getArgs()).thenReturn(new Object[] {0});

        try {
            invokeMethod(Foo.class, "foo", Integer.class);
        } catch (Exception e) {
            assertThat(e).hasMessageThat().matches("ConvertAttributeMap keys and values must have same length");
        }
    }

    @Test
    public void convertAttributeWithoutConvertAttributeMap() throws Throwable {
        class Foo {

            @TrackEvent("event")
            public void foo(@ConvertAttribute("key1") Integer type) {
                // some code
            }
        }

        when(joinPoint.getArgs()).thenReturn(new Object[] {0});

        try {
            invokeMethod(Foo.class, "foo", Integer.class);
        } catch (Exception e) {
            assertThat(e).hasMessageThat().matches("Method must have ConvertAttributeMap when ConvertAttribute is used");
        }
    }

    @Test
    public void convertAttributeForReturnValue() throws Throwable {
        class Foo {

            @TrackEvent("event")
            @ConvertAttributes(
                keys = {0, 1},
                values = {"value1", "value2"}
            )
            @ConvertAttribute("key1")
            public int foo() {
                return 1;
            }
        }

        when(joinPoint.proceed()).thenReturn(1);
        invokeMethod(Foo.class, "foo");

        assertTrack()
            .event("event")
            .noFilters()
            .noTags()
            .attribute("key1", "value2");
    }

    @Test
    public void convertAttributeDefaultValue() throws Throwable {
        class Foo {

            @TrackEvent("event")
            @ConvertAttributes(
                keys = {0, 1},
                values = {"value1", "value2"}
            )
            @ConvertAttribute(value = "key1", defaultValue = "default1")
            public String foo(@ConvertAttribute(value = "key2", defaultValue = "default2") Integer val) {
                return null;
            }
        }

        when(joinPoint.getArgs()).thenReturn(new Object[] {null});
        invokeMethod(Foo.class, "foo", Integer.class);

        assertTrack()
            .event("event")
            .noFilters()
            .noTags()
            .attribute("key1", "default1")
            .attribute("key2", "default2");
    }

    @Test
    public void trackableAttributeForCurrentClass() throws Throwable {
        class Foo implements Trackable {

            @Override
            public Map<String, Object> trackableAttributes() {
                Map<String, Object> map = new HashMap<>();
                map.put("key", "value");
                return map;
            }

            @TrackEvent("event")
            @TrackAttribute
            public void foo() {
                // some code
            }
        }

        initMethod(Foo.class, "foo");
        when(joinPoint.getThis()).thenReturn(new Foo());
        aspect.weaveJoinPointTrackEvent(joinPoint);

        assertTrack()
            .event("event")
            .noFilters()
            .noTags()
            .attribute("key", "value");
    }

    @Test
    public void doNotUseTrackableAttributesWhenTrackableAttributeNotExists() throws Throwable {
        class Foo implements Trackable {

            @Override
            public Map<String, Object> trackableAttributes() {
                Map<String, Object> map = new HashMap<>();
                map.put("key", "value");
                return map;
            }

            @TrackEvent("event")
            public void foo() {
                // some code
            }
        }

        when(joinPoint.getThis()).thenReturn(new Foo());
        invokeMethod(Foo.class, "foo");

        assertTrack()
            .event("event")
            .noFilters()
            .noTags()
            .noAttributes();
    }

    @Test
    public void ignoreNullValueOnTrackableAttributeForCurrentClass() throws Throwable {
        class Foo implements Trackable {

            @Override
            public Map<String, Object> trackableAttributes() {
                return null;
            }

            @TrackEvent("event")
            @TrackAttribute
            public void foo() {
                // some code
            }
        }

        initMethod(Foo.class, "foo");
        when(joinPoint.getThis()).thenReturn(new Foo());
        aspect.weaveJoinPointTrackEvent(joinPoint);

        assertTrack()
            .event("event")
            .noFilters()
            .noTags()
            .noAttributes();
    }

    @Test
    public void overrideClassWideAttributeOnMethodWhenAttributesAreSame() throws Throwable {
        @ConstantAttribute(key = "key", value = "class")
        @ConstantAttributes(
            @ConstantAttribute(key = "key1", value = "class1")
        )
        class Foo {

            @TrackEvent("event")
            @ConstantAttribute(key = "key", value = "method")
            @ConstantAttributes(
                @ConstantAttribute(key = "key1", value = "method1")
            )
            public void foo() {
                // some code
            }
        }

        invokeMethod(Foo.class, "foo");

        assertTrack()
            .event("event")
            .noFilters()
            .noTags()
            .attribute("key", "method")
            .attribute("key1", "method1");
    }

    @Test
    public void useThisClassWhenCalledFromSuperClass() throws Throwable {
        @ConstantAttribute(key = "key0", value = "value0")
        class Base {

            @TrackEvent("event")
            public void base() {
                // some code
            }
        }

        @ConstantAttribute(key = "key", value = "value")
        @ConstantAttributes(
            @ConstantAttribute(key = "key2", value = "value2")
        )
        class Foo extends Base {
        }

        initMethod(Foo.class, "base");
        when(joinPoint.getThis()).thenReturn(new Foo());
        aspect.weaveJoinPointTrackEvent(joinPoint);

        assertTrack()
            .event("event")
            .noFilters()
            .noTags()
            .attribute("key0", "value0")
            .attribute("key", "value")
            .attribute("key2", "value2");
    }

    @Test
    public void filters() throws Throwable {
        class Foo {

            @TrackEvent(value = "event", filters = {100, 200})
            public void foo() {
                // some code
            }
        }

        invokeMethod(Foo.class, "foo");

        int[] tags = {100, 200};

        assertTrack()
            .event("event")
            .noTags()
            .filters(tags)
            .noAttributes();
    }

    @Test
    public void tags() throws Throwable {
        class Foo {

            @TrackEvent(value = "event", tags = {"abc", "123"})
            public void foo() {
                // some code
            }
        }

        invokeMethod(Foo.class, "foo");

        String[] tags = {"abc", "123"};

        assertTrack()
            .event("event")
            .noFilters()
            .tags(tags)
            .noAttributes();
    }

    private AssertTracker assertTrack() {
        return new AssertTracker(trackEvent, attributes);
    }
}
