package org.bananalaba.jdk24;

import static java.lang.classfile.ClassFile.ACC_PUBLIC;
import static java.lang.constant.ConstantDescs.CD_boolean;
import static java.lang.constant.ConstantDescs.CD_byte;
import static java.lang.constant.ConstantDescs.CD_char;
import static java.lang.constant.ConstantDescs.CD_double;
import static java.lang.constant.ConstantDescs.CD_float;
import static java.lang.constant.ConstantDescs.CD_int;
import static java.lang.constant.ConstantDescs.CD_long;
import static java.lang.constant.ConstantDescs.CD_short;
import static java.lang.constant.ConstantDescs.CD_void;

import java.lang.classfile.ClassBuilder;
import java.lang.classfile.ClassFile;
import java.lang.classfile.CodeBuilder;
import java.lang.classfile.Opcode;
import java.lang.constant.ClassDesc;
import java.lang.constant.MethodTypeDesc;
import java.lang.reflect.AccessFlag;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;

@RequiredArgsConstructor
public class LoggingProxyFactory {

    private static final ClassDesc CD_OBJECT =
        ClassDesc.of("java.lang", "Object");
    private static final ClassDesc CD_NPE =
        ClassDesc.of("java.lang", "NullPointerException");
    private static final ClassDesc CD_STRING =
        ClassDesc.of("java.lang", "String");
    private static final ClassDesc CD_LOGGER =
        ClassDesc.of(Logger.class.getPackageName(), Logger.class.getSimpleName());

    private static final Map<Class<?>, Consumer<CodeBuilder>> RETURN_TYPES = Map.of(
        byte.class, CodeBuilder::ireturn,
        short.class, CodeBuilder::ireturn,
        char.class, CodeBuilder::ireturn,
        int.class, CodeBuilder::ireturn,
        long.class, CodeBuilder::lreturn,
        float.class, CodeBuilder::freturn,
        double.class, CodeBuilder::dreturn,
        boolean.class, CodeBuilder::ireturn,
        void.class, CodeBuilder::return_
    );

    private static final Map<Class<?>, BiConsumer<Integer, CodeBuilder>> PRIMITIVE_TO_STRING = Map.of(
        byte.class, (i, code) -> {
            code.iload(i);
            code.invokestatic(CD_STRING, "valueOf", MethodTypeDesc.of(CD_STRING, CD_byte));
        },
        short.class, (i, code) -> {
            code.iload(i);
            code.invokestatic(CD_STRING, "valueOf", MethodTypeDesc.of(CD_STRING, CD_short));
        },
        char.class, (i, code) -> {
            code.iload(i);
            code.invokestatic(CD_STRING, "valueOf", MethodTypeDesc.of(CD_STRING, CD_char));
        },
        int.class, (i, code) -> {
            code.iload(i);
            code.invokestatic(CD_STRING, "valueOf", MethodTypeDesc.of(CD_STRING, CD_int));
        },
        long.class, (i, code) -> {
            code.lload(i);
            code.invokestatic(CD_STRING, "valueOf", MethodTypeDesc.of(CD_STRING, CD_long));
        },
        float.class, (i, code) -> {
            code.fload(i);
            code.invokestatic(CD_STRING, "valueOf", MethodTypeDesc.of(CD_STRING, CD_float));
        },
        double.class, (i, code) -> {
            code.dload(i);
            code.invokestatic(CD_STRING, "valueOf", MethodTypeDesc.of(CD_STRING, CD_double));
        },
        boolean.class, (i, code) -> {
            code.iload(i);
            code.invokestatic(CD_STRING, "valueOf", MethodTypeDesc.of(CD_STRING, CD_boolean));
        }
    );

    private static final Map<Class<?>, BiConsumer<Integer, CodeBuilder>> PRIMITIVE_LOADERS = Map.of(
        byte.class, (i, code) -> code.iload(i),
        short.class, (i, code) -> code.iload(i),
        char.class, (i, code) -> code.iload(i),
        int.class, (i, code) -> code.iload(i),
        long.class, (i, code) -> code.lload(i),
        float.class, (i, code) -> code.fload(i),
        double.class, (i, code) -> code.dload(i),
        boolean.class, (i, code) -> code.iload(i)
    );

    private static final Map<Class<?>, ClassDesc> PRIMITIVE_CD_MAP = Map.of(
        byte.class, CD_byte,
        short.class, CD_short,
        char.class, CD_char,
        int.class, CD_int,
        long.class, CD_long,
        float.class, CD_float,
        double.class, CD_double,
        boolean.class, CD_boolean
    );

    private static final String PROXY_SUFFIX = "$LoggingProxy";
    private static final String CONSTRUCTOR = "<init>";
    private static final String LOGGER = "logger";

    @NonNull
    private final Logger logger;
    private final GeneratedCodeClassLoader classLoader = new GeneratedCodeClassLoader(getClass().getClassLoader());

    private final Map<Class<?>, Class<?>> proxyCache = new ConcurrentHashMap<>();

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public <T> T wrap(@NonNull final Class<T> beanType) {
        var proxyType = proxyCache.computeIfAbsent(beanType, this::generateProxyType);
        return (T) proxyType.getConstructor(Logger.class).newInstance(logger);
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    private <T> Class<T> generateProxyType(final Class<T> type) {
        if (type.isInterface() || type.isAnnotation() || type.isArray() || type.isPrimitive() || type.isRecord()) {
            throw new IllegalArgumentException("unsupported type: " + type + ", only regular classes are supported");
        }

        var modifiers = type.getModifiers();
        if (Modifier.isAbstract(modifiers) || Modifier.isFinal(modifiers)) {
            throw new IllegalArgumentException("unsupported type: " + type + ", it cannot be abstract nor final");
        }

        try {
            var defaultConstructor = type.getDeclaredConstructor();
            if (Modifier.isPrivate(defaultConstructor.getModifiers())) {
                throw new IllegalArgumentException("unsupported type: " + type + ", default constructor must be public");
            }
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("unsupported type: " + type + ", no default constructor found");
        }

        var file = ClassFile.of();
        var proxyDescriptor = ClassDesc.of(type.getPackageName(), type.getSimpleName() + PROXY_SUFFIX);
        var byteCode = file.build(proxyDescriptor, builder -> {
            var parentDescriptor = ClassDesc.of(type.getPackageName(), type.getSimpleName());
            builder.withSuperclass(parentDescriptor);

            defineLoggerField(builder);
            defineConstructor(builder, parentDescriptor, proxyDescriptor);

            for (var method : type.getDeclaredMethods()) {
                var methodModifiers = method.getModifiers();
                if (Modifier.isFinal(methodModifiers) || Modifier.isStatic(methodModifiers) || Modifier.isNative(methodModifiers)) {
                    continue;
                }

                proxyMethod(
                    builder,
                    parentDescriptor,
                    proxyDescriptor,
                    method.getName(),
                    method.getParameterTypes(),
                    method.getReturnType()
                );
            }
        });

        var fullName = type.getName() + PROXY_SUFFIX;
        return (Class<T>) classLoader.define(fullName, byteCode);
    }

    private void defineLoggerField(final ClassBuilder builder) {
        builder.withField(LOGGER, CD_LOGGER, field -> {
            field.withFlags(AccessFlag.PRIVATE, AccessFlag.FINAL);
        });
    }

    private void defineConstructor(final ClassBuilder builder,
                                   final ClassDesc parentDescriptor,
                                   final ClassDesc proxyDescriptor) {
        var constructorDescriptor = MethodTypeDesc.of(CD_void, CD_LOGGER);
        builder.withMethod(CONSTRUCTOR, constructorDescriptor, ACC_PUBLIC, method -> {
            method.withCode(code -> {
                var label1 = code.newLabel();

                code.aload(0);

                var superConstructorDescriptor = MethodTypeDesc.of(CD_void);
                code.invokespecial(parentDescriptor, CONSTRUCTOR, superConstructorDescriptor);

                code.aload(1);

                code.branch(Opcode.IFNONNULL, label1);
                code.new_(CD_NPE);
                code.dup();
                code.ldc("logger is marked non-null but is null");
                code.invokespecial(CD_NPE, CONSTRUCTOR, MethodTypeDesc.of(CD_void, CD_STRING));
                code.athrow();

                code.labelBinding(label1);
                code.aload(0);
                code.aload(1);
                code.putfield(proxyDescriptor, LOGGER, CD_LOGGER);
                code.return_();
            });
        });
    }

    private void proxyMethod(final ClassBuilder builder,
                             final ClassDesc parentDescriptor,
                             final ClassDesc proxyDescriptor,
                             final String name,
                             final Class<?>[] parameterTypes,
                             final Class<?> returnType) {
        var returnCd = toDescriptor(returnType);
        var parameterCds = Arrays.stream(parameterTypes)
            .map(this::toDescriptor)
            .toArray(ClassDesc[]::new);
        var methodDescriptor = MethodTypeDesc.of(returnCd, parameterCds);

        var numberOfParameters = parameterTypes.length;
        builder.withMethod(name, methodDescriptor, ACC_PUBLIC, method -> {
            method.withCode(code -> {
                code.loadConstant(numberOfParameters);
                code.anewarray(CD_OBJECT);
                code.astore(numberOfParameters + 1);

                for (var i = 1; i <= numberOfParameters; i++) {
                    code.aload(numberOfParameters + 1);
                    code.loadConstant(i - 1);
                    parameterToString(i, parameterTypes[i - 1], code);
                    code.aastore();
                }

                code.aload(0);
                code.getfield(proxyDescriptor, LOGGER, CD_LOGGER);
                code.ldc(name + "({})");
                code.aload(numberOfParameters + 1);
                code.invokeinterface(CD_LOGGER, "info", MethodTypeDesc.of(CD_void, CD_STRING, CD_OBJECT.arrayType()));

                code.aload(0);
                for (var i = 1; i <= numberOfParameters; i++) {
                    load(i, parameterTypes[i - 1], code);
                }
                code.invokespecial(parentDescriptor, name, methodDescriptor);

                addReturn(code, returnType);
            });
        });
    }

    private ClassDesc toDescriptor(final Class<?> type) {
        var primitive = PRIMITIVE_CD_MAP.get(type);
        return (primitive == null) ? ClassDesc.of(type.getPackageName(), type.getSimpleName()) : primitive;
    }

    private void parameterToString(final int parameterSlot, final Class<?> parameterType, final CodeBuilder code) {
        var primitiveToString = PRIMITIVE_TO_STRING.get(parameterType);
        if (primitiveToString == null) {
            code.aload(parameterSlot);
            code.invokestatic(CD_STRING, "valueOf", MethodTypeDesc.of(CD_STRING, CD_OBJECT));
        } else {
            primitiveToString.accept(parameterSlot, code);
        }
    }

    private void load(final int parameterSlot, final Class<?> parameterType, final CodeBuilder code) {
        var primitiveLoad = PRIMITIVE_LOADERS.get(parameterType);
        if (primitiveLoad == null) {
            code.aload(parameterSlot);
        } else {
            primitiveLoad.accept(parameterSlot, code);
        }
    }

    private void addReturn(final CodeBuilder code, final Class<?> type) {
        var primitiveReturnBuilder = RETURN_TYPES.get(type);
        if (primitiveReturnBuilder == null) {
            code.areturn();
        } else {
            primitiveReturnBuilder.accept(code);
        }
    }

}
