using Vlingo.Lattice.Model;
using Vlingo.Xoom.Common.Version;

namespace ${namespace}
{
  public sealed class ${typeName} : DomainEvent
  {
    public readonly int eventVersion;

    <#list properties as p>
    public readonly ${p.type} ${p.name};
    </#list>

    public ${typeName}(<#list properties as p>${p.type} ${p.name}<#if p?has_next>, </#if></#list>)
    {
      this.eventVersion = SemanticVersion.toValue("${version}");
      <#list properties as p>
      this.${p.name} = ${p.name};
      </#list>
    }
  }
}
