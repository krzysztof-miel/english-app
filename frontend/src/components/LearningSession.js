import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import { userService } from '../services/userService'; 

const LearningSession = () => {
  const [currentWord, setCurrentWord] = useState('');
  const [translations, setTranslations] = useState([]);
  const [isTranslating, setIsTranslating] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  // Inicjalizacja sesji
  useEffect(() => {
    const initSession = async () => {
      try {
        setIsLoading(true);
        
        // Wypisz token do konsoli w celach diagnostycznych
        const token = localStorage.getItem('token');
        console.log('Current token:', token);
        
        // Używamy bezpośrednio API z userService, które ma już skonfigurowane interceptory
        try {
          // Sprawdzamy status sesji
          const statusResponse = await userService.api.get('/users/learning/status');
          console.log('Session status response:', statusResponse);
          
          if (!statusResponse.data.active) {
            // Jeśli nie ma aktywnej sesji, tworzymy nową
            const startResponse = await userService.api.post('/users/learning/start');
            console.log('Session created:', startResponse);
          }
          
          // Pobieramy aktualne tłumaczenia
          const translationsResponse = await userService.api.get('/users/learning/translations');
          console.log('Translations:', translationsResponse);
          setTranslations(translationsResponse.data);
        } catch (apiError) {
          console.error('API Error:', apiError);
          
          // Spróbujmy użyć pełnych adresów URL jako ostatnią deskę ratunku
          console.log('Trying with full URLs...');
          
          const fullUrlBase = 'http://localhost:8080';
          
          // GET na status z pełnym URL
          const statusResponse = await axios.get(`${fullUrlBase}/users/learning/status`, {
            headers: {
              'Authorization': `Bearer ${token}`
            }
          });
          console.log('Direct status response:', statusResponse);
          
          if (!statusResponse.data.active) {
            // POST na start z pełnym URL
            await axios.post(`${fullUrlBase}/users/learning/start`, {}, {
              headers: {
                'Authorization': `Bearer ${token}`
              }
            });
          }
          
          // GET na translations z pełnym URL
          const translationsResponse = await axios.get(`${fullUrlBase}/users/learning/translations`, {
            headers: {
              'Authorization': `Bearer ${token}`
            }
          });
          setTranslations(translationsResponse.data);
        }
      } catch (error) {
        console.error('Error initializing session:', error);
        console.error('Error response:', error.response);
        setError('Nie udało się rozpocząć sesji nauki. Sprawdź konsolę przeglądarki.');
      } finally {
        setIsLoading(false);
      }
    };

    initSession();
  }, []);

  const handleTranslate = async () => {
    if (!currentWord.trim()) return;
    
    setIsTranslating(true);
    try {
      const token = localStorage.getItem('token');
      const response = await axios.post('http://localhost:8080/users/learning/translate', 
        { prompt: currentWord.trim() },
        {
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        }
      );
      
      console.log('Translation response:', response);
      setTranslations([...translations, response.data]);
      setCurrentWord('');
    } catch (error) {
      console.error('Error translating:', error);
      
      if (error.response?.status === 404) {
        // Brak sesji - tworzymy nową i próbujemy ponownie
        try {
          const token = localStorage.getItem('token');
          await axios.post('http://localhost:8080/users/learning/start', {}, {
            headers: {
              'Authorization': `Bearer ${token}`,
              'Content-Type': 'application/json'
            }
          });
          // Ponowna próba tłumaczenia
          handleTranslate();
        } catch (startError) {
          console.error('Error starting session:', startError);
          setError('Nie udało się rozpocząć sesji');
        }
      } else {
        setError('Wystąpił błąd podczas tłumaczenia');
      }
    } finally {
      setIsTranslating(false);
    }
  };

  const handleExportPdf = async () => {
    if (translations.length === 0) {
      setError('Nie można wygenerować PDF dla pustej sesji');
      return;
    }
    
    try {
      const token = localStorage.getItem('token');
      
      // Pobierz plik PDF jako blob
      const response = await fetch('http://localhost:8080/users/learning/export/pdf', {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      
      if (!response.ok) {
        throw new Error('Nie udało się pobrać pliku PDF');
      }
      
      const blob = await response.blob();
      
      // Utwórz URL dla pobranego blobu
      const url = window.URL.createObjectURL(blob);
      
      // Utwórz tymczasowy link i kliknij go, aby pobrać plik
      const a = document.createElement('a');
      a.href = url;
      a.download = `vocabulary-${new Date().toISOString().slice(0,10)}.pdf`;
      document.body.appendChild(a);
      a.click();
      
      // Posprzątaj
      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);
      
      // Zakończ sesję po pobraniu pliku
      await fetch('http://localhost:8080/users/learning/end', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });
      
      // Przekieruj do profilu
      navigate('/profile');
    } catch (error) {
      console.error('Error exporting PDF:', error);
      setError('Wystąpił błąd podczas generowania PDF: ' + error.message);
    }
  };

  const handleEndSession = async () => {
    try {
      const token = localStorage.getItem('token');
      await axios.post('http://localhost:8080/users/learning/end', {}, {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });
      navigate('/profile');
    } catch (error) {
      console.error('Error ending session:', error);
      setError('Wystąpił błąd podczas kończenia sesji');
    }
  };

  if (isLoading) {
    return (
      <div className="min-h-screen flex justify-center items-center">
        <div className="text-xl text-gray-600">Ładowanie sesji nauki...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen flex justify-center items-center">
        <div className="text-center">
          <div className="text-red-500 mb-4">{error}</div>
          <button
            onClick={() => navigate('/profile')}
            className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
          >
            Wróć do profilu
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
            <h1 className="text-2xl font-semibold text-gray-900 mb-6">
              Sesja nauki angielskiego
            </h1>
            
            <div className="mb-4">
              <label className="block text-sm font-medium text-gray-500">Wpisz słówko po angielsku</label>
              <div className="mt-1 flex rounded-md shadow-sm">
                <input
                  type="text"
                  value={currentWord}
                  onChange={(e) => setCurrentWord(e.target.value)}
                  onKeyPress={(e) => e.key === 'Enter' && handleTranslate()}
                  className="focus:ring-blue-500 focus:border-blue-500 flex-1 block w-full rounded-md sm:text-sm border-gray-300"
                  placeholder="np. apple"
                />
                <button
                  onClick={handleTranslate}
                  disabled={isTranslating || !currentWord.trim()}
                  className={`ml-3 inline-flex justify-center py-2 px-4 border border-transparent shadow-sm text-sm font-medium rounded-md text-white ${
                    isTranslating || !currentWord.trim()
                      ? 'bg-gray-400 cursor-not-allowed'
                      : 'bg-blue-500 hover:bg-blue-600'
                  }`}
                >
                  {isTranslating ? 'Tłumaczenie...' : 'Tłumacz'}
                </button>
              </div>
            </div>

            {translations.length > 0 ? (
              <div className="mt-6">
                <h2 className="text-lg font-medium text-gray-900 mb-3">
                  Przetłumaczone słówka ({translations.length})
                </h2>
                <div className="bg-gray-50 rounded-lg overflow-hidden">
                  <table className="min-w-full divide-y divide-gray-200">
                    <thead className="bg-gray-50">
                      <tr>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                          Angielski
                        </th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                          Polski
                        </th>
                      </tr>
                    </thead>
                    <tbody className="bg-white divide-y divide-gray-200">
                      {translations.map((item, index) => (
                        <tr key={index}>
                          <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                            {item.originalWord}
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                            {item.translatedWord}
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </div>
            ) : (
              <div className="mt-6 text-center text-gray-500">
                Twoja lista słówek jest pusta. Dodaj pierwsze słówko!
              </div>
            )}

            <div className="mt-8 space-y-3">
              <button
                onClick={handleExportPdf}
                disabled={translations.length === 0}
                className={`w-full inline-flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-md text-white ${
                  translations.length === 0
                    ? 'bg-gray-300 cursor-not-allowed'
                    : 'bg-blue-500 hover:bg-blue-600'
                }`}
              >
                Pobierz jako PDF
              </button>
              
              <button
                onClick={handleEndSession}
                className="w-full inline-flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-md text-white bg-red-500 hover:bg-red-600"
              >
                Zakończ sesję bez zapisywania
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default LearningSession;