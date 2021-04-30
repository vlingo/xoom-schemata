package ${package};

import io.vlingo.xoom.common.version.SemanticVersion;
import io.vlingo.xoom.lattice.model.DomainEvent;

public final class ${typeName} extends DomainEvent {

  public final int semanticVersion;

  <#list children as p>
  public final ${p.type.name()} ${p.name};
  </#list>

  public ${typeName}(<#list children as p>final ${p.type.name()} ${p.name}<#if p?has_next>, </#if></#list>) {
    this.semanticVersion = SemanticVersion.toValue("1.0.0");
    <#list children as p>
    this.${p.name} = ${p.name};
    </#list>
  }
}
