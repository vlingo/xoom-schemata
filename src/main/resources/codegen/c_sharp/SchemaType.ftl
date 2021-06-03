<#list imports as import>
using ${import};
</#list>

namespace ${namespace}
{
  public sealed class ${typeName} : ${baseTypeName}
  {
    <#list properties as property>
    public ${property.type} ${property.name} { get; }

    </#list>
    public ${typeName}(<#list properties?filter(f -> !f.isComputed) as property>${property.type} ${property.argumentName}<#sep>, </#list>) {
      <#list properties as property>
      ${property.name} = ${property.constructorInitializer};
      </#list>
    }
  }
}
