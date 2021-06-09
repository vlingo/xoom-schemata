<#list imports>
<#items as import>
using ${import};
</#items>

</#list>
namespace ${namespace}
{
  public sealed class ${typeName}<#if baseTypeName??> : ${baseTypeName}</#if>
  {
    <#list properties as property>
    public ${property.type} ${property.name} { get; }<#if property.defaultValue??> = ${property.defaultValue};</#if>

    </#list>
    <#if needsConstructor>
    public ${typeName}(<#list properties?filter(f -> !f.isComputed) as property>${property.type} ${property.argumentName}<#sep>, </#list>) {
      <#list properties as property>
      ${property.name} = ${property.constructorInitializer};
      </#list>
    }
    </#if>
    <#if needsDefaultConstructor>
      <#if needsConstructor>

      </#if>
    public ${typeName}() {
    <#list properties?filter(f -> f.isComputed) as property>
      ${property.name} = ${property.constructorInitializer};
    </#list>
    }
    </#if>
  }
}
