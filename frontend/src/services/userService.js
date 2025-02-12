


import axios from 'axios';

class UserService {
  constructor() {
    this.api = axios.create({
      baseURL: 'http://localhost:8080',  
      headers: {
        'Content-Type': 'application/json'
      }
    });

    
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

  async updatePreferences(preferences) {
    try {
      console.log('Updating preferences:', preferences);
      const response = await this.api.post('/users/preferences', preferences);
      console.log('Preferences updated:', response.data);
      return response.data;
    } catch (error) {
      console.error('Error updating preferences:', error);
      throw error;
    }
  }

}

export const userService = new UserService();