using Vlingo.Lattice.Model;
using Vlingo.Xoom.Common.Version;

namespace ${package}
{
  public sealed class ${typeName} : DomainEvent
  {
    public readonly int semanticVersion;

    <#list children as p>
    public readonly ${p.type.name()} ${p.name};
    </#list>

    public ${typeName}(<#list children as p>${p.type.name()} ${p.name}<#if p?has_next>, </#if></#list>)
    {
      this.semanticVersion = SemanticVersion.toValue("1.0.0");
      <#list children as p>
      this.${p.name} = ${p.name};
      </#list>
    }
  }
}
