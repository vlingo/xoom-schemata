package io.vlingo.xoom.schemata.codegen.backend;

import freemarker.template.*;

import java.io.IOException;
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
}
