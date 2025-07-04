package net.ice.relic.modding;

import net.ice.relic.annotations.Rewrite;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

@SupportedAnnotationTypes("net.ice.relic.annotations.Rewrite")
public class RewriteProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(Rewrite.class)) {
            Rewrite annotation = element.getAnnotation(Rewrite.class);
            String message = annotation.reason().isEmpty()
                    ? "This class/function is marked to be rewritten. It may stop working in a future release."
                    : annotation.reason();
            processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, message, element);
        }
        return true;
    }
}
