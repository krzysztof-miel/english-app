import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json'
  }
});

class AuthService {
  async login(usernameOrEmail, password) {
    try {
      console.log('Attempting login with:', { usernameOrEmail, password });
      
      const response = await axios.post('http://localhost:8080/api/auth/login', 
        { usernameOrEmail, password },
        {
          headers: {
            'Content-Type': 'application/json'
          }
        }
      );

      console.log('Login response:', response);

      if (response.data.accessToken) {
        localStorage.setItem('token', response.data.accessToken);
        return response.data;
      } else {
        console.error('No token in response:', response.data);
        throw new Error('No token received');
      }
    } catch (error) {
      console.error('Login error details:', {
        message: error.message,
        response: error.response?.data,
        status: error.response?.status
      });
      throw error;
    }
  }

  async register(username, email, password) {
    try {
      console.log('Attempting registration with:', { username, email });
      
      const response = await axios.post('http://localhost:8080/api/auth/register',
        { username, email, password },
        {
          headers: {
            'Content-Type': 'application/json'
          }
        }
      );

      console.log('Registration response:', response);
      return response.data;
    } catch (error) {
      console.error('Registration error details:', {
        message: error.message,
        response: error.response?.data,
        status: error.response?.status
      });
      throw error;
    }
  }

  logout() {
    localStorage.removeItem('token');
  }

  isAuthenticated() {
    return !!localStorage.getItem('token');
  }
}

export const authService = new AuthService();
export default api;
