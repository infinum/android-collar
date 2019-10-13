package co.infinum.collar;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import co.infinum.collar.annotations.Attribute;
import co.infinum.collar.annotations.ConstantAttribute;
import co.infinum.collar.annotations.ConstantAttributes;
import co.infinum.collar.annotations.TrackEvent;
import co.infinum.collar.annotations.TrackAttribute;
import co.infinum.collar.annotations.ConvertAttribute;
import co.infinum.collar.annotations.ConvertAttributes;

@Aspect
public class CollarAspect {

    @NotNull
    private static AspectListener aspectListener;

    static void observe(@NotNull AspectListener listener) {
        CollarAspect.aspectListener = listener;
    }

    @Pointcut("execution(@co.infinum.collar.annotations.TrackEvent * *(..))")
    public void methodAnnotatedWithTrackEvent() {
        // No implementation is needed
    }

    @Pointcut("execution(@co.infinum.collar.annotations.TrackEvent *.new(..))")
    public void constructorAnnotatedTrackEvent() {
        // No implementation is needed
    }

    @Around("methodAnnotatedWithTrackEvent() || constructorAnnotatedTrackEvent()")
    public Object weaveJoinPointTrackEvent(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();

        // Local attributes
        final Map<String, Object> attributes = new HashMap<>();

        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();

        addClassAttributes(method, joinPoint, attributes);

        Map<Integer, String> convertMap = new HashMap<>();
        addMethodAttributes(method, result, attributes, convertMap);
        addMethodParameterAttributes(method, joinPoint, attributes, convertMap);

        TrackEvent trackEvent = method.getAnnotation(TrackEvent.class);

        pushEvent(trackEvent, attributes);
        return result;
    }

    private void addClassAttributes(Method method, JoinPoint joinPoint, Map<String, Object> attributes) {
        Class<?> declaringClass = method.getDeclaringClass();

        if (method.isAnnotationPresent(TrackAttribute.class) && Trackable.class.isAssignableFrom(declaringClass)) {
            Trackable trackable = (Trackable) joinPoint.getThis();
            if (trackable.trackableAttributes() != null) {
                attributes.putAll(trackable.trackableAttributes());
            }
        }

        while (declaringClass != null) {
            addConstantAttribute(declaringClass.getAnnotation(ConstantAttribute.class), attributes);
            addConstantAttributes(declaringClass.getAnnotation(ConstantAttributes.class), attributes);
            declaringClass = declaringClass.getEnclosingClass();
        }

        declaringClass = joinPoint.getThis().getClass();
        addConstantAttribute(declaringClass.getAnnotation(ConstantAttribute.class), attributes);
        addConstantAttributes(declaringClass.getAnnotation(ConstantAttributes.class), attributes);
    }

    private void addMethodAttributes(Method method, Object returnValue, Map<String, Object> attributes,
        Map<Integer, String> convertMap) {
        Annotation[] annotations = method.getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation instanceof Attribute) {
                addAttribute((Attribute) annotation, returnValue, attributes);
            }
            if (annotation instanceof ConstantAttribute) {
                addConstantAttribute((ConstantAttribute) annotation, attributes);
            }
            if (annotation instanceof ConstantAttributes) {
                addConstantAttributes((ConstantAttributes) annotation, attributes);
            }
            if (annotation instanceof ConvertAttributes) {
                ConvertAttributes convertAttributeMap = (ConvertAttributes) annotation;
                int[] keys = convertAttributeMap.keys();
                String[] values = convertAttributeMap.values();
                if (keys.length != values.length) {
                    throw new IllegalStateException("ConvertAttributeMap keys and values must have same length");
                }
                for (int i = 0; i < keys.length; i++) {
                    convertMap.put(keys[i], values[i]);
                }
            }
            if (annotation instanceof ConvertAttribute) {
                addConvertAttribute((ConvertAttribute) annotation, returnValue, convertMap, attributes);
            }
        }
    }

    private void addMethodParameterAttributes(Method method, JoinPoint joinPoint, Map<String, Object> attributes,
        Map<Integer, String> convertMap) {
        Object[] fields = joinPoint.getArgs();
        Annotation[][] annotations = method.getParameterAnnotations();
        checkParameters(annotations, fields, convertMap, attributes);
    }

    private void addAttribute(Attribute attribute, Object methodResult, Map<String, Object> attributes) {
        if (attribute == null) {
            return;
        }

        Object value = null;
        if (methodResult != null) {
            value = methodResult;
        } else if (attribute.defaultValue().length() != 0) {
            value = attribute.defaultValue();
        }
        attributes.put(attribute.value(), value);
        if (attribute.isSuper()) {
            addSuperAttribute(attribute.value(), value);
        }
    }

    private void addConvertAttribute(ConvertAttribute attribute, Object result, Map<Integer, String> convertMap,
        Map<String, Object> attributes) {
        if (attribute == null) {
            return;
        }

        Object value = null;
        if (result != null) {
            value = convertMap.get(result);
        } else if (attribute.defaultValue().length() != 0) {
            value = attribute.defaultValue();
        }
        attributes.put(attribute.value(), value);
        if (attribute.isSuper()) {
            addSuperAttribute(attribute.value(), value);
        }
    }

    private void addConstantAttributes(ConstantAttributes constantAttributes, Map<String, Object> attributes) {
        if (constantAttributes == null) {
            return;
        }

        ConstantAttribute[] attributeList = constantAttributes.value();
        for (ConstantAttribute attribute : attributeList) {
            attributes.put(attribute.key(), attribute.value());
            if (attribute.isSuper()) {
                addSuperAttribute(attribute.key(), attribute.value());
            }
        }
    }

    private void addConstantAttribute(ConstantAttribute attribute, Map<String, Object> attributes) {
        if (attribute == null) {
            return;
        }
        attributes.put(attribute.key(), attribute.value());
        if (attribute.isSuper()) {
            addSuperAttribute(attribute.key(), attribute.value());
        }
    }

    private void checkParameters(Annotation[][] keys, Object[] values, Map<Integer, String> convertAttributeMap,
        Map<String, Object> attributes) {
        if (keys == null || values == null) {
            return;
        }
        for (int i = 0, size = keys.length; i < size; i++) {
            if (keys[i].length == 0) {
                continue;
            }
            Object value = values[i];
            Annotation annotation = keys[i][0];
            if (annotation instanceof Attribute) {
                Attribute attribute = (Attribute) annotation;
                Object result = null;
                if (value != null) {
                    result = value;
                } else if (attribute.defaultValue().length() != 0) {
                    result = attribute.defaultValue();
                }
                attributes.put(attribute.value(), result);
                if (attribute.isSuper()) {
                    addSuperAttribute(attribute.value(), result);
                }
            }
            if (annotation instanceof TrackAttribute) {
                if (value instanceof Trackable) {
                    Trackable trackable = (Trackable) value;
                    Map<String, Object> trackableValues = trackable.trackableAttributes();
                    if (trackableValues != null) {
                        attributes.putAll(trackable.trackableAttributes());
                    }
                } else {
                    throw new ClassCastException("Trackable interface must be implemented for the parameter type");
                }
            }
            if (annotation instanceof ConvertAttribute) {
                if (convertAttributeMap == null) {
                    throw new IllegalStateException("Method must have ConvertAttributeMap when ConvertAttribute is used");
                }
                ConvertAttribute convertAttribute = (ConvertAttribute) annotation;
                Object result = null;
                if (value != null) {
                    result = convertAttributeMap.get(value);
                } else if (convertAttribute.defaultValue().length() != 0) {
                    result = convertAttribute.defaultValue();
                }

                attributes.put(convertAttribute.value(), result);
                if (convertAttribute.isSuper()) {
                    addSuperAttribute(convertAttribute.value(), result);
                }
            }
        }
    }

    private void pushEvent(TrackEvent trackEvent, Map<String, Object> attributes) {
        if (aspectListener == null) {
            return;
        }

        aspectListener.onAspectEventTriggered(trackEvent, attributes);
    }

    private void addSuperAttribute(String key, Object value) {
        if (aspectListener == null) {
            return;
        }

        aspectListener.onAspectSuperAttributeAdded(key, value);
    }

    private void removeSuperAttribute(String key) {
        if (aspectListener == null) {
            return;
        }

        aspectListener.onAspectSuperAttributeRemoved(key);
    }
}