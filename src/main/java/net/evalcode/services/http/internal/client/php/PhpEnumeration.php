<?php


namespace %NAMESPACE%;


  /**
   * %TYPE%
   *
   * @package %PACKAGE%
   * @subpackage %SUBPACKAGE%
   *
   * @author %AUTHOR%
   *
%DOC%
   */
  class %TYPE% extends Enumeration
  {
    // PREDEFINED PROPERTIES
%PROPERTIES%
    //--------------------------------------------------------------------------


    // STATIC ACCESSORS
    /**
     * @return string[]
     */
    public static function values()
    {
      return array_values(self::$m_values);
    }
    //--------------------------------------------------------------------------


    // IMPLEMENTATION
    /**
     * @var string[]
     */
    private static $m_values=[
%VALUES%
    ];
    //--------------------------------------------------------------------------
  }
?>
