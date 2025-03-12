package org.bananalaba.jdk24;

import java.lang.classfile.ClassBuilder;
import java.lang.classfile.ClassFile;
import static java.lang.classfile.ClassFile.ACC_PUBLIC;
import java.lang.classfile.Opcode;
import java.lang.constant.ClassDesc;
import static java.lang.constant.ConstantDescs.CD_Integer;
import static java.lang.constant.ConstantDescs.CD_int;
import static java.lang.constant.ConstantDescs.CD_void;
import java.lang.constant.MethodTypeDesc;
import java.lang.reflect.AccessFlag;
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

    private static final String PROXY_SUFFIX = "$LoggingProxy";
    private static final String CONSTRUCTOR = "<init>";
    private static final String LOGGER = "logger";

    @NonNull
    private final Logger logger;
    private final GeneratedCodeClassLoader classLoader = new GeneratedCodeClassLoader(getClass().getClassLoader());

    @SneakyThrows
    public <T> T wrap(@NonNull final Class<T> beanType) {
        var proxyType = generateProxyType(beanType);
        return proxyType.getConstructor(Logger.class).newInstance(logger);
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    private <T> Class<T> generateProxyType(final Class<T> type) {
        var file = ClassFile.of();
        var proxyDescriptor = ClassDesc.of(type.getPackageName(), type.getSimpleName() + PROXY_SUFFIX);
        var byteCode = file.build(proxyDescriptor, builder -> {
            var parentDescriptor = ClassDesc.of(type.getPackageName(), type.getSimpleName());
            builder.withSuperclass(parentDescriptor);

            defineLoggerField(builder);
            defineConstructor(builder, parentDescriptor, proxyDescriptor);
            proxyMethod(builder, parentDescriptor, proxyDescriptor);
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
                             final ClassDesc proxyDescriptor) {
        var methodDescriptor = MethodTypeDesc.of(CD_int, CD_int, CD_int);
        builder.withMethod("add", methodDescriptor, ACC_PUBLIC, method -> {
            method.withCode(code -> {
                code.aload(0);
                code.getfield(proxyDescriptor, LOGGER, CD_LOGGER);
                code.ldc("add({}, {})");
                code.iload(1);
                code.invokestatic(CD_Integer, "valueOf", MethodTypeDesc.of(CD_Integer, CD_int));
                code.iload(2);
                code.invokestatic(CD_Integer, "valueOf", MethodTypeDesc.of(CD_Integer, CD_int));

                code.invokeinterface(CD_LOGGER, "info", MethodTypeDesc.of(CD_void, CD_STRING, CD_OBJECT, CD_OBJECT));

                code.aload(0);
                code.iload(1);
                code.iload(2);
                code.invokespecial(parentDescriptor, "add", methodDescriptor);

                code.ireturn();
            });
        });
    }

}
