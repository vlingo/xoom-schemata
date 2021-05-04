package ${namespace};

import io.vlingo.xoom.common.version.SemanticVersion;
import io.vlingo.xoom.lattice.model.DomainEvent;

public final class ${typeName} extends DomainEvent {

  <#list properties as p>
  public <#if !p.value??>final </#if>${p.type} ${p.name}<#if p.value??> = ${p.value}</#if>;
  </#list>

  public ${typeName}(<#list properties as p><#if p.name != 'eventVersion'>final ${p.type} ${p.name}<#if p?has_next>, </#if></#if></#list>) {
    <#list properties as p>
    <#if p.name = 'eventVersion'>
    this.${p.name} = SemanticVersion.toValue("${version}");
    <#else>
    this.${p.name} = ${p.name};
    </#if>
    </#list>
  }
}
