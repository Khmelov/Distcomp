using System;
using Microsoft.AspNetCore.Http;

namespace Domain.Shared;

public class Error : IEquatable<Error>
{
    public static readonly Error None = new Error(0, 0, 0, "");
    public static readonly Error NullValue = new Error(204, 0, 0, "The specified result value is null");
    public static readonly Error DatabaseError = new Error(503, 0, 1, "Error occurred when querying the database");
    public static readonly Error Unknown = new Error(503, 0, 2, "Unknown error occurred");
    
    private int _httpStatusCode { get; }
    private int _errorTarget { get; }
    private int _errorClass { get; }
    private string _errorMessage { get; }

    public int HttpStatusCode { get => _httpStatusCode; }
    public string Code { get => _httpStatusCode.ToString() + _errorTarget.ToString() + _errorClass.ToString(); }
    public string Message { get => _errorMessage; }

    public Error(int statusCode, int errorTarget, int errorClass, string message) {
        _httpStatusCode = statusCode;
        _errorTarget = errorTarget;
        _errorClass = errorClass;
        _errorMessage = message;
    }

    public virtual bool Equals(Error? other)
    {
        if (other is null) {
            return false; 
        }

        return Code == other.Code && Message == other.Message;
    }

    public override bool Equals(object? obj) => obj is Error err && Equals(err);
    public static bool operator ==(Error? err1, Error? err2){
        if (err1 is null && err2 is null){
            return true;
        }
        if (err1 is null || err2 is null){
            return false;
        }

        return err1.Equals(err2);
    }

    public static bool operator !=(Error? err1, Error? err2) => !(err1==err2);
    public override int GetHashCode() => HashCode.Combine(Code, Message);
    public override string ToString() => Code;
}
