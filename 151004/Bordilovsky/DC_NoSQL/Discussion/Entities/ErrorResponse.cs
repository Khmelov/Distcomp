using System.Net;

namespace Discussion.Entities
{
	public class ErrorResponse
	{
		public string ErrorMessage { get; set; }
		public string ErrorCode { get; set; }

		public static ErrorResponse CreateErrorResponse(string errorMessage, HttpStatusCode httpStatusCode)
		{
			int httpStatusCodeValue = (int)httpStatusCode;
			//int errorCodeHash = errorMessage.GetHashCode();
			string errorCode = errorMessage.Substring(0, 2);

			string fullErrorCode = $"{httpStatusCodeValue}{errorCode}";

			var errorResponse = new ErrorResponse
			{
				ErrorMessage = errorMessage,
				ErrorCode = fullErrorCode
			};

			return errorResponse;
		}
	}
}
