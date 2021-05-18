using System;
using Vlingo.Lattice.Model;
using Vlingo.Xoom.Common.Version;
<#macro printValue value array>
    <@compress single_line=true>
        <#if array>
          {<#list value as v>
            ${v}<#if v?has_next>, </#if>
        </#list>}
        <#else>
            ${value}
        </#if>
    </@compress>
</#macro>
<#macro printTypeName>
    <@compress single_line=true>
        <#if type = 'Command'>
          ${typeName} : Command
        <#elseif type = 'Event'>
          ${typeName} : DomainEvent
        <#else>
          ${typeName}
        </#if>
    </@compress>
</#macro>

namespace ${namespace}
{
  public sealed class <@printTypeName />
  {
    <#list properties as p>
    public <#if !p.value??>readonly </#if>${p.type}<#if p.array>[]</#if> ${p.name}<#if p.value??> = <@printValue p.value p.array /></#if>;
    </#list>

    <#if createDefaultConstructor>
    public ${typeName}() {
    <#list properties as p>
      <#if p.name = 'eventVersion' || p.name = 'semanticVersion'>
      this.${p.name} = SemanticVersion.toValue("${version}");
      <#elseif p.name = 'eventType'>
      this.${p.name} = "${typeName}";
      <#elseif p.name = 'occurredOn'>
      this.${p.name} = DateTimeOffset.UtcNow.ToUnixTimeMilliseconds();
      </#if>
    </#list>
    }
    </#if>
    <#if createRequiredArgsConstructor>
    public ${typeName}(<#list properties as p><#if p.name != 'eventVersion' && p.name != 'semanticVersion' && p.name != 'eventType' && p.name != 'occurredOn'>${p.type}<#if p.array>[]</#if> ${p.name}<#if p?has_next>, </#if></#if></#list>) {
    <#list properties as p>
      <#if p.name = 'eventVersion' || p.name = 'semanticVersion'>
      this.${p.name} = SemanticVersion.toValue("${version}");
      <#elseif p.name = 'eventType'>
      this.${p.name} = "${typeName}";
      <#elseif p.name = 'occurredOn'>
      this.${p.name} = DateTimeOffset.UtcNow.ToUnixTimeMilliseconds();
      <#else>
      this.${p.name} = ${p.name};
      </#if>
    </#list>
    }
    </#if>
  }
}
