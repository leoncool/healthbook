package server.conf;

public class ErrorDict {

	
	public static String get(String errorCode) {
		if(null!=errorCode) {
			return get(Integer.parseInt(errorCode));
		}else {
			Utils.log(ErrorDict.class, "Did you send me a valid error code?");
			return "";
		}
	}	
	public static String get(int errorCode) {
		String desc="";
		
		switch(errorCode) {
		case Constants.ERROR.UNSUPPORT:
			desc="Not a supported action";
			break;
			
		case Constants.ERROR.BADUSERNAME:
			desc="Bad Username";
			break;
		case Constants.ERROR.BADPASSWORD:
			desc="Bad Password";
			break;
		case Constants.ERROR.BADSESSION:
			desc="Bad Session";
			break;
		case Constants.ERROR.BADLOGIN:
			desc="Bad Login";
			break;
		case Constants.ERROR.BADPARAMS:
			desc="Bad Parameters";
			break;
			
		case Constants.ERROR.BADCONTENT:
			desc="Bad Contents";
			break;
			
		case Constants.ERROR.CONNERROR:
			desc="Connection Error";
			break;
		case Constants.ERROR.IOERROR:
			desc="IO Error";
			break;
		case Constants.ERROR.IOERROR_CONN_CLOSE:
			desc="Connection Closed";
			break;
		case Constants.ERROR.MKDIRFAIL:
			desc="Dir Create Failed";
			break;
		case Constants.ERROR.ACCESSDENIED:
			desc="Access Denied";
			break;
		case Constants.ERROR.FILEMISSING:
			desc="File Missing";
			break;
		case Constants.ERROR.FILESIZEEXCEED:
			desc="File size exceed Maximum";
			break;
		case Constants.ERROR.PARENTNOTAVAILABLE:
			desc="File's parent directory is not available";
			break;
		case Constants.ERROR.NEGATIVEREPLY:
			desc="Negaive Reply";
			break;
		case Constants.ERROR.SETFILETYPEFAIL:
			desc="Set file type Failed";
			break;
		case Constants.ERROR.STORENOTCOMPLETE:
			desc="Storing file not complete";
			break;
		case Constants.ERROR.ILLEGALSTATE:
			desc="Illegal State";
			break;
		case Constants.ERROR.MKDIRNOTCOMPLETE:
			desc="Bad parameters";
			break;
		case Constants.ERROR.RMDIRNOTCOMPLETE:
			desc="Remove dir not complete";
			break;
		case Constants.ERROR.RETRIEVENOTCOMPLETE:
			desc="Retriving file not complete";
			break;
		case Constants.ERROR.RMFILENOTCOMPLETE:
			desc="Rename file not complete";
			break;
			
			
		case Constants.ERROR.TASK_LOCKED:
			desc="Task has been locked! Please try it again util the current task complete.";
			break;
		
			
		case Constants.ERROR.THUMBNAILSIZEEXCEED:
			desc="Thumbnail file size is too large.";
			break;
			
			
		case Constants.ERROR.UNKNOWN:
			desc="Unknown Error, contacts administrator please.";
			break;
			
		default:
			desc="Undefined Error Code: "+errorCode;
			break;
		}
		
		
		
		return desc;
	}
	
	
}






