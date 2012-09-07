package server.conf;

public interface Constants {

    public static final String PARAM_ACTION = "action";
    public static final String PARAM_USERNAME = "username";
    public static final String PARAM_PASSWORD = "password";
    public static final String PARAM_TOKENID = "tokenid";
    public static final String PARAM_PATH = "path";
    public static final String PARAM_PATTERN = "pattern";
    public static final String PARAM_FILES = "files";
    public static final String PARAM_FILENAME = "filename";
    public static final String PARAM_PROP_KEY = "prop.key";
    public static final String PARAM_PROP_VALUE = "prop.value";
    public static final String PARAM_ACCESS_KEY = "access.key";
    public static final String PARAM_SECURE_KEY = "secure.key";
    public static final String PARAM_FILEOWNER = "fileowner";
    public static final String PARAM_PATHNAME = "pathname";
    public static final String PARAM_MESSAGE = "message";
    public static final String PARAM_SRC = "src";
    public static final String PARAM_DST = "dst";
    public static final String PARAM_MODE = "mode";
    public static final String PARAM_OFFSET = "offset";
    public static final String PARAM_LENGTH = "length";
    public static final String ATTR_USERNAME = "username";
    public static final String ATTR_PASSWORD = "password";
    public static final String ATTR_TOKENID = "tokenid";
    public static final String DEFAULT_VALUE_OPERATOR = "_DEFAULT";
    public static final String PAIR_KEY_NAME = "name";
    public static final String PAIR_KEY_SIZE = "size";
    public static final String PAIR_KEY_STATUS = "status";
    public static final String PAIR_KEY_BYTES_TRANSFERED = "bytesTransferred";
    public static final String PAIR_KEY_BYTES_TOTAL = "bytesTotal";
    public static final String PAIR_VALUE_SUCCESS = "success";
    public static final String PAIR_VALUE_FAILURE = "failure";
    public static final String PAIR_VALUE_READY = "ready";
    public static final String PAIR_VALUE_PROCESSING = "processing";
    public static final String PAIR_VALUE_PROCESSING_POST = "processing_post";
    // --------------- ACTION --------------
    public static final String ACTION_USER_LOGIN = "user.login";
    public static final String ACTION_USER_GET_INFO = "user.get.info";
    public static final String ACTION_USER_GET_SYSMSG = "user.get.sysmsg";
    public static final String ACTION_USER_PUSH_SYSMSG = "user.push.sysmsg";
    public static final String ACTION_USER_REGISTER = "user.register";
    public static final String ACTION_USER_EXISTED = "user.existed";
    public static final String ACTION_FILE_LIST = "file.list";
    public static final String ACTION_FILE_SEARCH = "file.search";
    public static final String ACTION_FILE_MOVE = "file.move";
    public static final String ACTION_FILE_COPY = "file.copy";
    public static final String ACTION_FILE_DETAIL = "file.detail";
    public static final String ACTION_FILE_LIST_PUBLIC = "file.list.public";
    public static final String ACTION_FILE_ACHIEVE = "file.achieve";
    public static final String ACTION_FILE_DOWNLOAD = "file.download";
    public static final String ACTION_FILE_ACHIEVE_FNS = "file.achieve.fns";
    public static final String ACTION_FILE_UPLOAD = "file.upload";
    public static final String ACTION_FILE_GET_THUMBNAIL = "file.thumbnail";
    public static final String ACTION_FILE_GET_FILEDATA = "file.filedata";
    public static final String ACTION_FILE_SET_PROP = "file.set.prop";
    public static final String ACTION_FILE_UPLOAD_GET_STATUS = "file.upload.get.status"; //[optional] it's a sub operation for getting status of upload
    public static final String ACTION_FILE_GET_EXTERNAL_URL = "file.external";
    public static final String ACTION_FILE_MAKE_DIR = "file.makedir";
    public static final String ACTION_FILE_REMOVE_DIR = "file.removedir";
    public static final String ACTION_FILE_DELETE = "file.delete";
    public static final String ACTION_FILE_RENAME = "file.rename";
    public static final String ACTION_DEBUG_EXEC = "debug.exec";
    // ------------------------------------
    public static final String STATUS_OK = "status.ok";
    public static final String STATUS_ERROR = "status.error";
    public static final String EXCEPTION_TAG_NEGATIVE_REPLY = "FTPReply - negative reply";
    public static final String EXCEPTION_TAG_LOGIN_FAIL = "Login - fail";
    public static final String EXCEPTION_TAG_FILE_NOT_FOUND = "File - not found in file system";
    public static final String EXCEPTION_TAG_EXCEED_MAX_BUFFER = "File - exceed max buffer size";
    public static final String EXCEPTION_TAG_STORE_NOT_COMPLETE = "Store - fail";
    public static final String EXCEPTION_TAG_MKDIR_NOT_COMPLETE = "Mkdir - fail";
    public static final String EXCEPTION_TAG_RMDIR_NOT_COMPLETE = "Rmdir - fail";
    public static final String EXCEPTION_TAG_RMFILE_NOT_COMPLETE = "Rmfile - fail";
    public static final String EXCEPTION_TAG_SET_FILE_TYPE = "FTP - set file type to binary, fialed";
    public static final String EXCEPTION_TAG_RETRIEVE_NOT_COMPLETE = "Retrieve - not complete";

    public static interface FORMAT {

        public static final String DATE = "yyyy-MM-dd HH:mm:ss";
        public static final String DATE2 = "yyyy-MM-dd'T'HH:mm:ss'.000Z'";
        public static final String DATE3 = "E, dd MMM yyyy HH:mm:ss z";
        public static final String FILESIZE = "%1$,1d";
    }

    public static interface SPLITTER {

        public static final String FILE = ":";
    }

    public static interface ENCODING {

        public static final String WEB = "UTF-8";
        public static final String WEB_QS = "ISO-8859-1";
        public static final String LOCAL_FS = "gb2312";
//		public static final String FTP="UTF-8";
    }

    public static interface CACHE {

        public static final String PATH = "./gs_cache/";
        public static final String PATH_THUMBNAIL = "./thumbnail";
    }

    public static interface HEADER {

        public static final String KEY_RESPONSE_PRODUCER = "Response-Producer";
        public static final String KEY_RESPONSE_TYPE = "Response-Type";
        public static final String KEY_RESPONSE_CODE = "Response-Code";
        public static final String VALUE_RESPONSE_PRODUCER = "Zhumulangma, Giant System";
        public static final String VALUE_RESPONSE_TYPE_NORMAL = "NORMAL";
        public static final String VALUE_RESPONSE_TYPE_ERROR = "ERROR";
        public static final int VALUE_RESPONSE_CODE_NORMAL = 0;
    }

    public static interface ERROR {

        public static final int UNSUPPORT = 10000001;
        public static final int BADUSERNAME = 20000001;
        public static final int BADPASSWORD = 20000002;
        public static final int BADSESSION = 20000003;
        public static final int BADLOGIN = 20000004;
        public static final int BADPARAMS = 20000005;
        public static final int BADCONTENT = 20010001;
        public static final int CONNERROR = 40000100;
        public static final int IOERROR = 40000200;
        public static final int IOERROR_CONN_CLOSE = 40000201;
        public static final int MKDIRFAIL = 40000300;
        public static final int ACCESSDENIED = 40000400;
        public static final int NOTFOUND = 40000401;
        public static final int ALREADYEXISTED = 40000402;
        public static final int PARENTINVALID = 40000403;
        public static final int LOGICALERROR = 40000404;
        public static final int ONLYACCEPTDIRCTORY = 40000405;
        public static final int NORECURSIVEMKDIR = 40000406;
        public static final int RENAME_NEWDIR_EXISTED = 40000407;
        public static final int RENAMEFILE_PARENTFolder_NOTEXIST = 40000408;
        public static final int RENAMEFILE_FILE_NOTEXIST = 40000409;
        public static final int MKDIR_OVER_DEPTHLIMIT = 40000410;
        public static final int MKDIR_COTAIN_ILLEGAL_CHR = 40000411;
        public static final int MKDIR_SIZE_TOOLONG = 40000412;
        public static final int MKDIR_DIR_ALREADYEXIST = 40000413;
        public static final int MKDIR_NOT_ALLOW_ROOT = 40000414;
        public static final int MKDIR_BUCKET_EXIST = 40000415;
        public static final int FILEMISSING = 40000500;
        public static final int FILESIZEEXCEED = 40000501;
        public static final int PARENTNOTAVAILABLE = 40000502;
        public static final int BLOCKSIZEEXCEED = 40000503;
        public static final int FILENAME_COTAIN_ILLEGAL_CHR = 40000504;
        public static final int DIR_NOTEXIST = 40000505;
        public static final int STORE_FILE_COTAIN_ILLEGAL_CHR = 40000506;
        public static final int COPY_DEST_NOT_EXIST = 40000520;
        public static final int NEGATIVEREPLY = 40000600;
        public static final int SETFILETYPEFAIL = 40000601;
        public static final int STORENOTCOMPLETE = 40000700;
        public static final int ILLEGALSTATE = 40000800;
        public static final int MKDIRNOTCOMPLETE = 40000900;
        public static final int RMDIRNOTCOMPLETE = 40001000;
        public static final int RETRIEVENOTCOMPLETE = 40001100;
        public static final int RMFILENOTCOMPLETE = 40001200;
        public static final int DELETEDIR_NODIR = 40003000;
        public static final int TASK_LOCKED = 40002001;
        public static final int THUMBNAILSIZEEXCEED = 40003001;
        public static final int REGISTER_FAIL = 50000001;
        public static final int UNKNOWN = 80000001;
        public static final int FILE_DATA_SERVER_ERROR = 5000100;
        public static final int FILE_META_SERVER_ERROR = 5000200;
    }

    public static interface S3ERROR {

        public static final int ACCESSDENIED = 40000400;
        public static final int NOTFOUND = 40000401;
        public static final int USERID_NOT_FOUND = 40000402;
        public static final int NOT_SUCH_KEY = 4000043;
        public static final int OVER_KEY_LENGTH_LIMIT = 40000410;
        // public static final int KEY_COTAIN_ILLEGAL_CHR = 40000411;
        public static final int BUCKETNAME_ILLEGAL_CHR = 40000411;
        public static final int MKDIR_SIZE_TOOLONG = 40000412;
        public static final int KEY_COTAIN_ILLEGAL_CHR = 40000413;
        public static final int FILE_DATA_SERVER_ERROR = 5000100;
        public static final int FILE_META_SERVER_ERROR = 5000200;
        public static final int FILE_UPLOAD_INTERAPT = 5000300;
        public static final int BUCKET_NOTEXIST = 6000100;
        public static final int BUCKET_ALREADYEXIST = 6000200;
        public static final int BUCKET_NOT_EMPTY = 6000300;
        public static final int NO_SUCH_UPLOAD = 7000100;
        public static final int UPLOAD_ETAG_NOT_MATCH = 7000200;
        public static final int USER_ALREADY_ACTIVED = 8000001;
    }

    public static interface S3Contants {

        public static final String RESERVCED_WORDS = "s3portal;AllUsers;BucketService;";
        public static final String READ_FILE_PERMISSION = "READ";
        public static final String WRITE_FILE_PERMISSION = "WRITE";
        public static final String READ_P_PERMISSION = "READ_ACP";
        public static final String WRITE_P_PERMISSION = "WRITE_ACP";
        public static final String FULL_CONTROL = "FULL_CONTROL";
        public static final String ALLUSERS = "AllUsers";
        public static final String REDUCED_REDUNDANCY = "REDUCED_REDUNDANCY";
        public static final String STANDARD_STORAGE_CLASS = "STANDARD";
        public static final String CONTENT_TYPE = "Content-Type";
        public static final String CONTENT_MD5 = "Content-MD5";
        public static final String LastModified = "LastModified";
        public static final String IS_TRUNCATED = "IsTruncated";
        public static final String NextKeyMarker = "NextKeyMarker";
        public static final String NextPartNumberMarker = "NextPartNumberMarker";
        public static final String DEFAULT_REGION = "default";
        public static final int DEFAULT_REPLICAS = 3;
        public static final int REDUCED_REPLICAS = 1;
        // ----
    }

    public static interface THRESHOLD {

        public static final int FTP_TIMEOUT = 15 * 1000; // ms
        public static final int DISPLAY_NAME_LENGTH = 24;
        public static final int THUMBNAIL_MAX_SIZE = 1024 * 1024; // bytes
    }

    public static interface MKDIR {

        public static final int MAX_DEPTH = 100 + 3; //
    }

    public static interface UPLOAD {
//		public static final long MAX_SIZE=100*1024*1024;

        public static final long MAX_SIZE = Long.MAX_VALUE;
        public static final int CACHE_SIZE = 1 * 1024 * 1024;
        public static final int STREAM_BUFFER_FACTOR = 256; //factory * Util.DEFAULT_COPY_BUFFER_SIZE(1024)
        public static final String TEMP_PATH = "gs_upload_temp/";
    }

    public static interface HDFSAdapter {

        public static final String BYTESWRITTEN = "bytesWritten";
        public static final String FILEMD5 = "fileMD5";
        public static final String VERSION = "version";
    }
}
