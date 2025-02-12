import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { authService } from '../services/authService';

const Auth = () => {
  const [isRegistering, setIsRegistering] = useState(false);
  const [formData, setFormData] = useState({
    username: '',
    email: '',
    password: '',
    usernameOrEmail: ''
  });
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    
    try {
      if (isRegistering) {
        console.log('Registration attempt:', {
          username: formData.username,
          email: formData.email,
          password: formData.password
        });
        
        const response = await authService.register(
          formData.username,
          formData.email,
          formData.password
        );
        
        console.log('Registration successful:', response);
        alert('Rejestracja udana! Możesz się teraz zalogować.');
        setIsRegistering(false);
        setFormData({
          username: '',
          email: '',
          password: '',
          usernameOrEmail: ''
        });
      } else {
        console.log('Login attempt:', {
          usernameOrEmail: formData.usernameOrEmail,
          password: formData.password
        });
        
        const response = await authService.login(
          formData.usernameOrEmail,
          formData.password
        );
        
        console.log('Login successful:', response);
        navigate('/profile');
      }
    } catch (error) {
      console.error(isRegistering ? 'Registration error:' : 'Login error:', error);
      setError(error.response?.data?.message || `Błąd podczas ${isRegistering ? 'rejestracji' : 'logowania'}`);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full space-y-8">
        <div>
          <h2 className="mt-6 text-center text-3xl font-extrabold text-gray-900">
            {isRegistering ? 'Zarejestruj się' : 'Zaloguj się'}
          </h2>
        </div>
        
        {error && (
          <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative">
            {error}
          </div>
        )}

        <form className="mt-8 space-y-6" onSubmit={handleSubmit}>
          <div className="rounded-md shadow-sm space-y-4">
            {isRegistering ? (
              <>
                <div>
                  <input
                    name="username"
                    type="text"
                    required
                    className="appearance-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded focus:outline-none focus:ring-blue-500 focus:border-blue-500 focus:z-10 sm:text-sm"
                    placeholder="Nazwa użytkownika"
                    value={formData.username}
                    onChange={(e) => setFormData({
                      ...formData,
                      username: e.target.value
                    })}
                  />
                </div>
                <div>
                  <input
                    name="email"
                    type="email"
                    required
                    className="appearance-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded focus:outline-none focus:ring-blue-500 focus:border-blue-500 focus:z-10 sm:text-sm"
                    placeholder="Email"
                    value={formData.email}
                    onChange={(e) => setFormData({
                      ...formData,
                      email: e.target.value
                    })}
                  />
                </div>
              </>
            ) : (
              <div>
                <input
                  name="usernameOrEmail"
                  type="text"
                  required
                  className="appearance-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded focus:outline-none focus:ring-blue-500 focus:border-blue-500 focus:z-10 sm:text-sm"
                  placeholder="Email lub nazwa użytkownika"
                  value={formData.usernameOrEmail}
                  onChange={(e) => setFormData({
                    ...formData,
                    usernameOrEmail: e.target.value
                  })}
                />
              </div>
            )}
            
            <div>
              <input
                name="password"
                type="password"
                required
                className="appearance-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded focus:outline-none focus:ring-blue-500 focus:border-blue-500 focus:z-10 sm:text-sm"
                placeholder="Hasło"
                value={formData.password}
                onChange={(e) => setFormData({
                  ...formData,
                  password: e.target.value
                })}
              />
            </div>
          </div>

          <div>
            <button
              type="submit"
              className="group relative w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
            >
              {isRegistering ? 'Zarejestruj się' : 'Zaloguj się'}
            </button>
          </div>

          <div className="text-center">
            <button
              type="button"
              onClick={() => {
                setIsRegistering(!isRegistering);
                setError('');
                setFormData({
                  username: '',
                  email: '',
                  password: '',
                  usernameOrEmail: ''
                });
              }}
              className="text-blue-600 hover:text-blue-500"
            >
              {isRegistering ? 'Masz już konto? Zaloguj się' : 'Nie masz konta? Zarejestruj się'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default Auth;