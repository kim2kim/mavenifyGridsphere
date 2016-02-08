package org.gridsphere.services.core.jcr;

public interface JCRNode {

    public final static String NAMESPACE = "http://www.gridsphere.org/jcr-content/1.0";
    public final static String PREFIX = "gs";

    // Data
    public final static String AUTHOR = PREFIX + ":" + "author";
    public final static String CONTENT = PREFIX + ":" + "content";
    public final static String TITLE = PREFIX + ":" + "title";

    // Metadata
    public final static String GS_ROOT_NODE_NAME = "gridsphere";
    public final static String GS_ROOT_NODE_PATH = "/" + GS_ROOT_NODE_NAME;
    public final static String GS_ROOT_CONTENTDOCUMENT_NAME = "contentdocuments";
    public final static String GS_ROOT_CONTENTDOCUMENT_PATH = "/" + GS_ROOT_NODE_NAME + "/" + GS_ROOT_CONTENTDOCUMENT_NAME;

    public final static String GSID = PREFIX + ":" + "gsid";
    public final static String MODIFIED_DATE = PREFIX + ":" + "modifieddate";
    public final static String CREATED_DATE = PREFIX + ":" + "createddate";
    public final static String MODIFIED_BY = PREFIX + ":" + "modifiedby";
    public final static String RENDERKIT = PREFIX + ":" + "renderkit";
    public final static String TYPE = PREFIX + ":" + "document";

    public final static String RENDERKIT_TEXT = "text/text";
    public final static String RENDERKIT_HTML = "text/html";
    public final static String RENDERKIT_RADEOX = "text/radeox";
    public final static String RENDERKIT_DEFAULT = RENDERKIT_TEXT;

}
