package org.bananalaba.jdk24;

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

    @NonNull
    private final Logger logger;
    private final GeneratedCodeClassLoader classLoader = new GeneratedCodeClassLoader(getClass().getClassLoader());

    @SneakyThrows
    public <T> T wrap(@NonNull final Class<T> beanType) {
        var proxyType = generateProxyType(beanType);
        return proxyType.getConstructor(Logger.class).newInstance(logger);
    }

    @SneakyThrows
    private <T> Class<T> generateProxyType(final Class<T> type) {
        var file = ClassFile.of();
        var proxyDescriptor = ClassDesc.of(type.getPackageName(), type.getSimpleName() + "$LoggingProxy");
        var byteCode = file.build(proxyDescriptor, builder -> {
            var parentDescriptor = ClassDesc.of(type.getPackageName(), type.getSimpleName());
            builder.withSuperclass(parentDescriptor);

            var loggerTypeDescriptor = ClassDesc.of(Logger.class.getPackageName(), Logger.class.getSimpleName());
            builder.withField("logger", loggerTypeDescriptor, field -> {
                field.withFlags(AccessFlag.PRIVATE, AccessFlag.FINAL);
            });

            var constructorDescriptor = MethodTypeDesc.of(CD_void, loggerTypeDescriptor);
            var stringTypeDescriptor = ClassDesc.of("java.lang", "String");
            builder.withMethod("<init>", constructorDescriptor, ACC_PUBLIC, method -> {
                method.withCode(code -> {
                    var label1 = code.newLabel();

                    code.aload(0);

                    var superConstructorDescriptor = MethodTypeDesc.of(CD_void);
                    code.invokespecial(parentDescriptor, "<init>", superConstructorDescriptor);

                    code.aload(1);

                    var npeTypeDescriptor = ClassDesc.of("java.lang", "NullPointerException");
                    code.branch(Opcode.IFNONNULL, label1);
                    code.new_(npeTypeDescriptor);
                    code.dup();
                    code.ldc("logger is marked non-null but is null");
                    code.invokespecial(npeTypeDescriptor, "<init>", MethodTypeDesc.of(CD_void, stringTypeDescriptor));
                    code.athrow();

                    code.labelBinding(label1);
                    code.aload(0);
                    code.aload(1);
                    code.putfield(proxyDescriptor, "logger", loggerTypeDescriptor);
                    code.return_();
                });
            });

            var addDescriptor = MethodTypeDesc.of(CD_int, CD_int, CD_int);
            builder.withMethod("add", addDescriptor, ACC_PUBLIC, method -> {
               method.withCode(code -> {
                   code.aload(0);
                   code.getfield(proxyDescriptor, "logger", loggerTypeDescriptor);
                   code.ldc("add({}, {})");
                   code.iload(1);
                   code.invokestatic(CD_Integer, "valueOf", MethodTypeDesc.of(CD_Integer, CD_int));
                   code.iload(2);
                   code.invokestatic(CD_Integer, "valueOf", MethodTypeDesc.of(CD_Integer, CD_int));

                   var objectArrayDescriptor = ClassDesc.of("java.lang", "Object");
                   code.invokeinterface(loggerTypeDescriptor, "info", MethodTypeDesc.of(CD_void, stringTypeDescriptor, objectArrayDescriptor, objectArrayDescriptor));

                   code.aload(0);
                   code.iload(1);
                   code.iload(2);
                   code.invokespecial(parentDescriptor, "add", addDescriptor);

                   code.ireturn();
               });
            });
        });

        var fullName = type.getName() + "$LoggingProxy";
        return (Class<T>) classLoader.define(fullName, byteCode);
    }

}
