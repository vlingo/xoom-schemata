package io.vlingo.xoom.schemata.codegen.backend;

import freemarker.template.*;
import io.vlingo.xoom.codegen.CodeGenerationException;
import io.vlingo.xoom.common.Outcome;
import io.vlingo.xoom.common.Success;
import io.vlingo.xoom.schemata.errors.SchemataBusinessException;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Locale;

import static freemarker.template.Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS;

public class CodeGenerator {

    private final Configuration configuration;

    public CodeGenerator() {
        DefaultObjectWrapper objectWrapper = new DefaultObjectWrapper(DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
        objectWrapper.setExposeFields(true);
        this.configuration = new Configuration(DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
        configuration.setClassForTemplateLoading(CodeGenerator.class, "/");
        configuration.setDefaultEncoding("UTF-8");
        configuration.setLocale(Locale.US);
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        configuration.setObjectWrapper(objectWrapper);
    }

    public void generateWith(String language, String templateName, Object templateArgs, Writer output) throws IOException, TemplateException {
        Template template = configuration.getTemplate("/codegen/" + language + "/" + templateName + ".ftl");
        template.process(templateArgs, output);
    }

    public Outcome<SchemataBusinessException, String> generateWith(String language, String templateName, Object templateArgs) {
        StringWriter result = new StringWriter();
        try{
            generateWith(language, templateName, templateArgs, result);
        }catch (IOException | TemplateException e) {
            throw new CodeGenerationException(e);
        }
        return Success.of(result.toString());
    }
}
