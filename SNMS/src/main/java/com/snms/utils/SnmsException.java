package com.snms.utils;

public class SnmsException extends Exception
{

    private static final long serialVersionUID = 1L;

    /** String object for error code to be displayed to user */
    private String            ERROR_CODE       = "";

    /** Object of Throwable to catch nested exception */
    private Throwable         throwEx          = null;

    /** String array of inputs to pass to error message */
    private String[]          params           = null;

    /**
     * Constructor
     * 
     * @param message - String object of custom message
     * @param throwEx - Object of Throwable
     **/
    public SnmsException(String message, Throwable throwEx)
    {
        super(message, throwEx);
        this.throwEx = throwEx;
    }

    ////////////////////////////////////////////
    // GETTER METHODS
    ////////////////////////////////////////////

    public String getERROR_CODE()
    {
        return ERROR_CODE;
    }
    public String getMessage()
    {
        return (throwEx == null) ? super.getMessage() : throwEx.getMessage();
    }
    public String[] getParameter()
    {
        return params;
    }

    ////////////////////////////////////////////
    // SETTER METHODS
    ////////////////////////////////////////////

    public void setERROR_CODE(String eRROR_CODE)
    {
        ERROR_CODE = eRROR_CODE;
    }
    public void setParameter(String[] params)
    {
        this.params = params;
    }
}
