package io.vlingo.xoom.schemata.codegen.template.schematype;

import io.vlingo.xoom.schemata.Schemata;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.joining;

public class SchemaTypePackage {

  private final List<String> packageSegments;
  private final String separator;
  private final Function<String, String> segmentFormatter;

  private SchemaTypePackage(final List<String> packageSegments, final String separator, final Function<String, String> segmentFormatter) {
    this.packageSegments = packageSegments;
    this.separator = separator;
    this.segmentFormatter = segmentFormatter;
  }

  private SchemaTypePackage(final List<String> packageSegments, final String separator) {
    this(packageSegments, separator, (segment) -> segment);
  }

  public static SchemaTypePackage from(final String reference, final String category, final String separator) {
    final String[] referenceParts = reference.split(Schemata.ReferenceSeparator);
    if (referenceParts.length < Schemata.MinReferenceParts) {
      throw new IllegalArgumentException("Invalid fully qualified type name. Valid type names look like this <organization>:<unit>:<context namespace>:<type name>[:<version>].");
    }
    final String namespace = referenceParts[2];
    final String className = referenceParts[3];

    final String basePackage = namespace + separator + category;
    final String localPackage = className.contains(separator) ? className.substring(0, className.lastIndexOf(separator.charAt(0))) : "";
    return new SchemaTypePackage(Arrays.asList((localPackage.length() > 0
            ? basePackage + separator + localPackage
            : basePackage).split(Pattern.quote(separator))), separator);
  }

  public SchemaTypePackage withSegmentFormatter(final Function<String, String> formatter) {
    return new SchemaTypePackage(this.packageSegments, this.separator, formatter);
  }

  public String name() {
    return packageSegments.stream().map(segmentFormatter).collect(joining(separator));
  }
}
