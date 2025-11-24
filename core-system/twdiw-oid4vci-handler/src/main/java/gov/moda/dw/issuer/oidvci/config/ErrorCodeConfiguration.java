package gov.moda.dw.issuer.oidvci.config;

public class ErrorCodeConfiguration {

public static String getErrorMessage(int err_code){
		
		String error_message = new String();
		
		switch(err_code){
		
		case 10000:
			error_message = "Success";
			break;			
		case 11001:
			error_message = "Invalid or missing input parameter(s)";
			break;
		case 11002:
			error_message = "The user is not authenticated";
			break;
		case 11003:
			error_message = "The format of the id_token is incorrect.";
			break;
		case 11004:
			error_message = "The id_token has expired.";
			break;
		case 11005:
			error_message = "The nonce has already been repeated.";
			break;
		case 11006:
			error_message = "An error occurred while generating the QR code.";
			break;
		case 11007:
			error_message = "Pre-authorized_code not found";
			break;
		case 11008:
			error_message = "Pre-authorized_code has already expired";
			break;
		case 11009:
			error_message = "Pre-authorized_code has already been used";
			break;
		case 11010:
			error_message = "The grant_type is unsupported";
			break;
		case 11011:
			error_message = "The VC type is unsupported";
			break;
		case 11012:
			error_message = "The authorization_details are invalid";
			break;
		case 11013:
			error_message = "An error occurred while generating the access token.";
			break;
		case 11014:
			error_message = "Missing or invalid Authorization header";
			break;
		case 11015:
			error_message = "The access token is missing";
			break;
		case 11016:
			error_message = "The proofs jwt is invalid: ";
			break;
		case 11017:
			error_message = "The access_token not found";
			break;
		case 11018:
			error_message = "The access_token has either expired or has already been used.";
			break;
		case 11019:
			error_message = "The credential_identifier is invalid";
			break;
		case 11020:
			error_message = "VC generation error: ";
			break;
		case 11021:
			error_message = "VC Server error";
			break;
		case 11022:
			error_message = "An exception occurred while connecting to the VC";
			break;
		case 11500:
			error_message = "Interal server error";
			break;
		}
		
		return error_message;
		
	}
}
