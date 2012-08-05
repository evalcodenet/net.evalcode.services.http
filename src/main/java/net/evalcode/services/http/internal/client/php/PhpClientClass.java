package net.evalcode.services.http.internal.client.php;


import java.util.ArrayList;
import java.util.List;
import net.evalcode.services.http.internal.client.WebApplicationClientGeneratorPhp;
import org.apache.commons.lang.StringUtils;


/**
 * PhpClientClass
 *
 * @author carsten.schipke@gmail.com
 */
public class PhpClientClass
{
  // PREDEFINED PROPERTIES
  static final String LINE_INDENTATION=" ";
  static final String LINE_ENDING="\n";
  static final String COMMENT_PREFIX="//";
  static final String PHP_OPEN="<?php";
  static final String PHP_CLOSE="?>";
  static final String BRACKET_OPEN="{";
  static final String BRACKET_CLOSE="}";
  static final String SECTION_ACCESSORS_MUTATORS="ACCESSORS/MUTATORS";
  static final String SECTION_CONSTRUCTION="CONSTRUCTION";
  static final String SECTION_IMPLEMENTATION="IMPLEMENTATION";
  static final String SECTION_PREDEFINED_PROPERTIES="PREDEFINED PROPERTIES";
  static final String SECTION_PROPERTIES="PROPERTIES";
  static final String SECTION_SEPARATOR_CHARACTER="-";
  static final String KEY_CLASS_NAME="%CLASS_NAME%";
  static final String KEY_CLASS_AUTHOR="%CLASS_AUTHOR%";
  static final String KEY_CLASS_PACKAGE="%CLASS_PACKAGE%";
  static final int LINE_INDENTATION_1=2;
  static final int LINE_INDENTATION_2=4;
  static final int SECTION_SEPARATOR_LENGTH=74;
  static final int INITIAL_LINE_SPACING=3;


  // MEMBERS
  protected final String name;
  protected final String fileName;
  protected final Class<?> clazz;
  protected final PhpClientApplication application;
  protected final List<PhpClientClassConstant> constants=new ArrayList<PhpClientClassConstant>();
  protected final List<PhpClientClassProperty> properties=new ArrayList<PhpClientClassProperty>();
  protected final List<PhpClientMethod> methods=new ArrayList<PhpClientMethod>();
  protected final List<String> interfaces=new ArrayList<String>();


  // CONSTRUCTION
  public PhpClientClass(final PhpClientApplication application, final Class<?> clazz,
    final String name, final String fileName)
  {
    this.application=application;

    this.clazz=clazz;
    this.name=name;
    this.fileName=fileName;
  }


  // ACCESSORS/MUTATORS
  public PhpClientApplication getApplication()
  {
    return application;
  }

  public Class<?> getType()
  {
    return clazz;
  }

  public String getName()
  {
    return name;
  }

  public String getFileName()
  {
    return fileName;
  }

  public PhpClientMethod addMethod(final PhpClientMethod method)
  {
    methods.add(method);

    return method;
  }

  public List<PhpClientMethod> getMethods()
  {
    return methods;
  }

  public void addConstant(final PhpClientClassConstant constant)
  {
    constants.add(constant);
  }

  public void addProperty(final PhpClientClassProperty property)
  {
    properties.add(property);
  }

  public void addInterface(final String interfaceClazzName)
  {
    interfaces.add(interfaceClazzName);
  }


  // OVERRIDES/IMPLEMENTS
  @Override
  public String toString()
  {
    final StringBuffer stringBuffer=new StringBuffer();

    stringBuffer.append(PHP_OPEN);
    stringBuffer.append(StringUtils.repeat(LINE_ENDING, INITIAL_LINE_SPACING));
    stringBuffer.append(getPhpDoc());
    stringBuffer.append(getSignature());
    stringBuffer.append(StringUtils.repeat(LINE_INDENTATION, LINE_INDENTATION_1));
    stringBuffer.append(BRACKET_OPEN);
    stringBuffer.append(LINE_ENDING);

    if(0<constants.size())
    {
      stringBuffer.append(getSectionTitle(SECTION_PREDEFINED_PROPERTIES, LINE_INDENTATION_2, true));

      for(final PhpClientClassConstant constant : constants)
        stringBuffer.append(constant.toString());

      stringBuffer.append(getSectionSeparator(LINE_INDENTATION_2, true));
    }

    if(0<properties.size())
    {
      if(0<constants.size())
        stringBuffer.append(StringUtils.repeat(LINE_ENDING, 2));

      stringBuffer.append(getSectionTitle(SECTION_PROPERTIES, LINE_INDENTATION_2, true));

      for(final PhpClientClassProperty property : properties)
        stringBuffer.append(property.toString());

      stringBuffer.append(getSectionSeparator(LINE_INDENTATION_2, true));
    }

    if(0<methods.size())
    {
      if(0<constants.size() || 0<properties.size())
        stringBuffer.append(StringUtils.repeat(LINE_ENDING, 2));

      int i=0;

      if(PhpClientMethod.NAME_CONSTRUCTOR.equals(methods.get(0).getName()))
      {
        stringBuffer.append(getSectionTitle(SECTION_CONSTRUCTION, LINE_INDENTATION_2, true));
        stringBuffer.append(methods.get(i++).toString());
        stringBuffer.append(getSectionSeparator(LINE_INDENTATION_2, true));

        if(1<methods.size())
          stringBuffer.append(StringUtils.repeat(LINE_ENDING, 2));
      }

      stringBuffer.append(getSectionTitle(SECTION_ACCESSORS_MUTATORS, LINE_INDENTATION_2, true));
      for(int j=i; j<methods.size(); j++)
      {
        stringBuffer.append(methods.get(j).toString());

        if((j+1)<methods.size())
          stringBuffer.append(LINE_ENDING);
      }
      stringBuffer.append(getSectionSeparator(LINE_INDENTATION_2, true));
    }

    stringBuffer.append(getMembers());

    stringBuffer.append(StringUtils.repeat(LINE_INDENTATION, LINE_INDENTATION_1));
    stringBuffer.append(BRACKET_CLOSE);
    stringBuffer.append(LINE_ENDING);
    stringBuffer.append(PHP_CLOSE);
    stringBuffer.append(LINE_ENDING);

    return stringBuffer.toString();
  }


  // IMPLEMENTATION
  String getSignature()
  {
    if(0<interfaces.size())
    {
      return String.format("  class %1$s implements %2$s\n",
        getName(), StringUtils.join(interfaces.toArray(), ", ")
      );
    }

    return String.format("  class %1$s\n", getName());
  }

  String getPhpDoc()
  {
    final StringBuffer stringBuffer=new StringBuffer();

    final String applicationName=String.format(
      WebApplicationClientGeneratorPhp.PATTERN_APPLICATION_ROOT_PATH, application.getName()
    );

    stringBuffer.append("  /**\n");
    stringBuffer.append(String.format("   * %1$s\n", getName()));
    stringBuffer.append("   *\n");
    stringBuffer.append(String.format("   * @package %1$s\n", applicationName));
    stringBuffer.append(
      String.format("   * @subpackage %1$s\n", PhpClientApplication.DEFAULT_CLASS_PACKAGE)
    );
    stringBuffer.append("   *\n");
    stringBuffer.append(
      String.format("   * @author %1$s\n", PhpClientApplication.DEFAULT_CLASS_AUTHOR)
    );
    stringBuffer.append("   */\n");

    return stringBuffer.toString();
  }

  String getMembers()
  {
    final StringBuffer stringBuffer=new StringBuffer();

    if(1>methods.size())
      return "";

    stringBuffer.append(StringUtils.repeat(LINE_ENDING, 2));
    stringBuffer.append(getSectionTitle(SECTION_IMPLEMENTATION, LINE_INDENTATION_2, true));

    for(final PhpClientMethod method : methods)
    {
      for(final PhpClientMethodParameter parameter : method.getParameters())
      {
        if(parameter.assignMember())
          stringBuffer.append(String.format("    private $m_%1$s;\n", parameter.getName()));
      }
    }

    stringBuffer.append(getSectionSeparator(LINE_INDENTATION_2, true));

    return stringBuffer.toString();
  }

  String getSectionTitle(final String title, final int indentation, final boolean appendNewLine)
  {
    final StringBuffer stringBuffer=new StringBuffer();

    stringBuffer.append(StringUtils.repeat(LINE_INDENTATION, indentation));
    stringBuffer.append(COMMENT_PREFIX);
    stringBuffer.append(" "+title);

    if(appendNewLine)
      stringBuffer.append(LINE_ENDING);

    return stringBuffer.toString();
  }

  String getSectionSeparator(final int indentation, final boolean appendNewLine)
  {
    final StringBuffer stringBuffer=new StringBuffer();

    stringBuffer.append(StringUtils.repeat(LINE_INDENTATION, indentation));
    stringBuffer.append(COMMENT_PREFIX);
    stringBuffer.append(StringUtils.repeat(SECTION_SEPARATOR_CHARACTER, SECTION_SEPARATOR_LENGTH));

    if(appendNewLine)
      stringBuffer.append(LINE_ENDING);

    return stringBuffer.toString();
  }
}
