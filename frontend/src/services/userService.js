// import api from './authService';

// class UserService {
//   async getCurrentUser() {
//     const response = await api.get('/users/me');
//     return response.data;
//   }

//   async updateUserPreferences(userId, preferences) {
//     const response = await api.post(`/users/${userId}/preferences`, preferences);
//     return response.data;
//   }
// }

// export const userService = new UserService();


import axios from 'axios';

class UserService {
  constructor() {
    this.api = axios.create({
      baseURL: 'http://localhost:8080',  // bez /api dla endpointów użytkownika
      headers: {
        'Content-Type': 'application/json'
      }
    });

    // Dodajemy interceptor do automatycznego dodawania tokenu
    this.api.interceptors.request.use(
      (config) => {
        console.log('Preparing request:', config.url);
        const token = localStorage.getItem('token');
        if (token) {
          config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
      },
      (error) => {
        console.error('Request interceptor error:', error);
        return Promise.reject(error);
      }
    );

    // Dodajemy interceptor do obsługi odpowiedzi
    this.api.interceptors.response.use(
      (response) => {
        console.log('Response received:', response);
        return response;
      },
      (error) => {
        console.error('Response error:', {
          status: error.response?.status,
          data: error.response?.data,
          message: error.message
        });
        return Promise.reject(error);
      }
    );
  }

  async getCurrentUser() {
    try {
      console.log('Fetching current user data...');
      console.log('Current token:', localStorage.getItem('token'));
      
      const response = await this.api.get('/users/me');
      console.log('User data received:', response.data);
      
      return response.data;
    } catch (error) {
      console.error('Error fetching user data:', {
        status: error.response?.status,
        data: error.response?.data,
        message: error.message
      });
      throw error;
    }
  }
  async generateWords() {
    try {
      console.log('Generating words...');
      const response = await this.api.post('/users/generateWords');
      console.log('Generated words:', response.data);
      return response.data;
    } catch (error) {
      console.error('Error generating words:', {
        status: error.response?.status,
        data: error.response?.data,
        message: error.message
      });
      throw error;
    }
  }
}

export const userService = new UserService();