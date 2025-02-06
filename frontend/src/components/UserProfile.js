// import React, { useState, useEffect } from 'react';
// import { useNavigate } from 'react-router-dom';
// import { useAuth } from '../contexts/AuthContext';
// import { userService } from '../services/userService';

// const UserProfile = () => {
//   const [userData, setUserData] = useState(null);
//   const [loading, setLoading] = useState(true);
//   const [error, setError] = useState(null);
//   const { logout } = useAuth();
//   const navigate = useNavigate();

//   useEffect(() => {
//     loadUserData();
//   }, []);

//   const loadUserData = async () => {
//     try {
//       const data = await userService.getCurrentUser();
//       setUserData(data);
//     } catch (error) {
//       setError('Failed to load user data');
//       if (error.response?.status === 401) {
//         navigate('/login');
//       }
//     } finally {
//       setLoading(false);
//     }
//   };

//   const handlePreferencesUpdate = async (preferences) => {
//     try {
//       await userService.updateUserPreferences(userData.id, preferences);
//       await loadUserData();
//       alert('Preferences updated successfully!');
//     } catch (error) {
//       setError('Failed to update preferences');
//     }
//   };

//   const handleLogout = () => {
//     logout();
//     navigate('/login');
//   };

//   if (loading) return <div>Loading...</div>;
//   if (error) return <div>Error: {error}</div>;

//   return (
//     <div className="min-h-screen bg-gray-50 py-6 px-4">
//       <div className="max-w-2xl mx-auto bg-white rounded-lg shadow p-8">
//         <div className="flex justify-between items-center mb-6">
//           <h1 className="text-2xl font-bold">Profil użytkownika</h1>
//           <button
//             onClick={handleLogout}
//             className="px-4 py-2 bg-red-500 text-white rounded hover:bg-red-600"
//           >
//             Wyloguj
//           </button>
//         </div>

//         {userData && (
//           <div className="space-y-6">
//             <div>
//               <h2 className="text-gray-500 text-sm">Nazwa użytkownika</h2>
//               <p className="text-lg">{userData.username}</p>
//             </div>
            
//             <div>
//               <h2 className="text-gray-500 text-sm">Email</h2>
//               <p className="text-lg">{userData.email}</p>
//             </div>

//             {userData.preferences && (
//               <div className="border-t pt-6">
//                 <h2 className="text-xl font-semibold mb-4">Preferencje</h2>
                
//                 <div className="space-y-4">
//                   <div>
//                     <label className="block text-gray-500">Preferowana godzina</label>
//                     <input
//                       type="time"
//                       value={userData.preferences.timePreference}
//                       onChange={(e) => handlePreferencesUpdate({
//                         ...userData.preferences,
//                         timePreference: e.target.value
//                       })}
//                       className="mt-1 block w-full rounded-md border-gray-300 shadow-sm"
//                     />
//                   </div>
                  
//                   <div>
//                     <label className="block text-gray-500">Liczba słów</label>
//                     <select
//                       value={userData.preferences.wordCountPreference}
//                       onChange={(e) => handlePreferencesUpdate({
//                         ...userData.preferences,
//                         wordCountPreference: parseInt(e.target.value)
//                       })}
//                       className="mt-1 block w-full rounded-md border-gray-300 shadow-sm"
//                     >
//                       <option value={5}>5 słów</option>
//                       <option value={8}>8 słów</option>
//                       <option value={10}>10 słów</option>
//                     </select>
//                   </div>
//                 </div>
//               </div>
//             )}
//           </div>
//         )}
//       </div>
//     </div>
//   );
// };

// export default UserProfile;

import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { userService } from '../services/userService';
import { authService } from '../services/authService';

const UserProfile = () => {
  const [userData, setUserData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();
  const [generatedWords, setGeneratedWords] = useState(null);
  const [generatingWords, setGeneratingWords] = useState(false);

  useEffect(() => {
    loadUserData();
  }, []);

  const loadUserData = async () => {
    try {
      const data = await userService.getCurrentUser();
      setUserData(data);
    } catch (error) {
      console.error('Error in loadUserData:', error);
      if (error.response?.status === 401) {
        authService.logout();
        navigate('/login');
      } else {
        setError('Nie udało się załadować danych użytkownika');
      }
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = () => {
    authService.logout();
    navigate('/login');
  };

  const handleGenerateWords = async () => {
    try {
      setGeneratingWords(true);
      const words = await userService.generateWords();
      setGeneratedWords(words);
    } catch (error) {
      console.error('Error generating words:', error);
      setError('Nie udało się wygenerować słówek');
    } finally {
      setGeneratingWords(false);
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen flex justify-center items-center">
        <div className="text-xl text-gray-600">Ładowanie...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen flex justify-center items-center">
        <div className="text-red-500 text-center">
          <p className="mb-4">{error}</p>
          <button
            onClick={() => navigate('/login')}
            className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
          >
            Wróć do logowania
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-100 py-6 flex flex-col justify-center sm:py-12">
      <div className="relative py-3 sm:max-w-xl sm:mx-auto">
        <div className="relative px-4 py-10 bg-white mx-8 md:mx-0 shadow rounded-3xl sm:p-10">
          <div className="max-w-md mx-auto">
            <div className="flex items-center justify-between mb-6">
              <h1 className="text-2xl font-semibold text-gray-900">Profil użytkownika</h1>
              <button
                onClick={handleLogout}
                className="px-4 py-2 bg-red-500 text-white rounded hover:bg-red-600"
              >
                Wyloguj
              </button>
            </div>

            {userData && (
              <div className="space-y-6">
                <div>
                  <h2 className="text-sm font-medium text-gray-500">Nazwa użytkownika</h2>
                  <p className="mt-1 text-lg font-medium text-gray-900">{userData.username}</p>
                </div>

                <div>
                  <h2 className="text-sm font-medium text-gray-500">Email</h2>
                  <p className="mt-1 text-lg font-medium text-gray-900">{userData.email}</p>
                </div>

                {userData.preferences && (
                  <div className="pt-6 border-t border-gray-200">
                    <h2 className="text-lg font-medium text-gray-900 mb-4">Preferencje</h2>
                    
                    <div className="space-y-4">
                      <div>
                        <h3 className="text-sm font-medium text-gray-500">Preferowana godzina</h3>
                        <p className="mt-1 text-lg font-medium text-gray-900">
                          {userData.preferences.timePreference}
                        </p>
                      </div>

                      <div>
                        <h3 className="text-sm font-medium text-gray-500">Liczba słów</h3>
                        <p className="mt-1 text-lg font-medium text-gray-900">
                          {userData.preferences.wordCountPreference}
                        </p>
                      </div>
                    </div>
                  </div>
                )}

                <div className="pt-6 border-t border-gray-200">
                  <button
                    onClick={handleGenerateWords}
                    disabled={generatingWords}
                    className={`w-full px-4 py-2 rounded text-white transition-colors ${
                      generatingWords 
                        ? 'bg-gray-400 cursor-not-allowed' 
                        : 'bg-blue-500 hover:bg-blue-600'
                    }`}
                  >
                    {generatingWords ? 'Generowanie słówek...' : 'Wygeneruj słówka'}
                  </button>

                  {generatedWords && (
                    <div className="mt-4 p-4 bg-gray-50 rounded-lg">
                      <h3 className="text-lg font-medium text-gray-900 mb-2">
                        Wygenerowane słówka:
                      </h3>
                      <div className="text-gray-800 whitespace-pre-line">
                        {generatedWords}
                      </div>
                    </div>
                  )}
                </div>

              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default UserProfile;