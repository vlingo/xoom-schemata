package ${package};

<#list imports as import>
import ${import};
</#list>

public final class ${typeName} extends ${baseTypeName} {
<#list fields as field>
  ${field.modifiers} ${field.type} ${field.name}<#if field.initializer != ""> = ${field.initializer}</#if>;

</#list>
<#if needsConstructor>
  public ${typeName}(<#list fields?filter(f -> !f.isComputed) as field>final ${field.type} ${field.name}<#sep>, </#list>) {
  <#list fields as field>
    this.${field.name} = ${field.constructorInitializer};
  </#list>
  }
</#if>
<#if needsDefaultConstructor>
  <#if needsConstructor>

  </#if>
  public ${typeName}() {
  <#list computedFields as field>
    this.${field.name} = ${field.constructorInitializer};
  </#list>
  }
</#if>
<#t/>
}
