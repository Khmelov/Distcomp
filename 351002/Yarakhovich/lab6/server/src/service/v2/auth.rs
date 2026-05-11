use serde::{Deserialize, Serialize};
use jsonwebtoken::{encode, decode, Header, Algorithm, Validation, EncodingKey, DecodingKey};
use bcrypt::{hash, verify, DEFAULT_COST};
use chrono::{Utc, Duration};

#[derive(Debug, Serialize, Deserialize, Clone)]
pub enum Role {
    ADMIN,
    CUSTOMER,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct Claims {
    pub sub: String,
    pub role: Role,
    pub iat: usize,
    pub exp: usize,
}

pub const JWT_SECRET: &[u8] = b"your_super_secret_key";

pub fn hash_password(password: &str) -> Result<String, std::io::Error> {
    hash(password, DEFAULT_COST)
        .map_err(|_| std::io::Error::new(std::io::ErrorKind::BrokenPipe, "Password hashing failed"))
}

pub fn verify_password(password: &str, hashed: &str) -> bool {
    verify(password, hashed).unwrap_or(false)
}

pub fn create_jwt(login: &str, role: Role) -> Result<String, std::io::Error> {
    let expiration = Utc::now()
        .checked_add_signed(Duration::hours(24))
        .expect("valid timestamp")
        .timestamp();

    let claims = Claims {
        sub: login.to_owned(),
        role,
        iat: Utc::now().timestamp() as usize,
        exp: expiration as usize,
    };

    encode(&Header::default(), &claims, &EncodingKey::from_secret(JWT_SECRET))
        .map_err(|_| std::io::Error::new(std::io::ErrorKind::BrokenPipe, "Token generation failed"))
}