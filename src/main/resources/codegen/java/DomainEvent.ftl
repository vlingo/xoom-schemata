package ${namespace};

import io.vlingo.xoom.common.version.SemanticVersion;
import io.vlingo.xoom.lattice.model.DomainEvent;

<#macro printValue value array type>
    <@compress single_line=true>
        <#if array>
            new ${type}[] {<#list value as v>
              ${v}<#if v?has_next>, </#if>
            </#list>}
        <#else>
            ${value}
        </#if>
    </@compress>
</#macro>

public final class ${typeName} extends DomainEvent {

  <#list properties as p>
  public <#if !p.value??>final </#if>${p.type}<#if p.array>[]</#if> ${p.name}<#if p.value??> = <@printValue p.value p.array p.type /></#if>;
  </#list>

  <#if createDefaultConstructor>
  public ${typeName}() {
    <#list properties as p>
    <#if p.name = 'eventVersion' || p.name = 'semanticVersion'>
    this.${p.name} = SemanticVersion.toValue("${version}");
    <#elseif p.name = 'eventType'>
    this.${p.name} = "${typeName}";
    <#elseif p.name = 'occurredOn'>
    this.${p.name} = System.currentTimeMillis();
    </#if>
    </#list>
  }
  </#if>
  <#if createRequiredArgsConstructor>
  public ${typeName}(<#list properties as p><#if p.name != 'eventVersion' && p.name != 'semanticVersion' && p.name != 'eventType' && p.name != 'occurredOn'>final ${p.type}<#if p.array>[]</#if> ${p.name}<#if p?has_next>, </#if></#if></#list>) {
    <#list properties as p>
    <#if p.name = 'eventVersion' || p.name = 'semanticVersion'>
    this.${p.name} = SemanticVersion.toValue("${version}");
    <#elseif p.name = 'eventType'>
    this.${p.name} = "${typeName}";
    <#elseif p.name = 'occurredOn'>
    this.${p.name} = System.currentTimeMillis();
    <#else>
    this.${p.name} = ${p.name};
    </#if>
    </#list>
  }
  </#if>
}
