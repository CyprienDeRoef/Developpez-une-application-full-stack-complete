export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface JwtResponse {
  token: string;
  type: string;
  id: number;
  email: string;
  name: string;
}

export interface User {
  id: number;
  email: string;
  name: string;
  createdAt?: Date;
  updatedAt?: Date;
}
