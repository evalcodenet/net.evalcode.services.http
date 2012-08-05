<?php


  /**
   * %CLASS_NAME%
   *
   * @package %APPLICATION_NAME%
   * @subpackage %CLASS_PACKAGE%
   *
   * @author %CLASS_AUTHOR%
   */
  class %CLASS_NAME%
  {
    // PREDEFINED PROPERTIES
    const BASE_URL='%BASE_URL%';
    //--------------------------------------------------------------------------


    // STATIC ACCESSORS
    /**
     * @return Services_Client
     */
    public static function getClient()
    {
      if(null===self::$m_client)
        self::$m_client=new Services_Client(self::BASE_URL);

      return self::$m_client;
    }
    //--------------------------------------------------------------------------


    // IMPLEMENTATION
    /**
     * @var Services_Client
     */
    private static $m_client;
    //--------------------------------------------------------------------------
  }
?>
