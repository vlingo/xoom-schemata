package ${package};

<#list imports as import>
import ${import};
</#list>

public final class ${typeName}<#if baseTypeName??> extends ${baseTypeName}</#if> {
<#list fields as field>
  <#if field.defaultValue??>
  public ${field.type} ${field.name}<#if field.defaultValue??> = ${field.defaultValue}</#if>;
  <#else>
  public final ${field.type} ${field.name};
  </#if>

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
