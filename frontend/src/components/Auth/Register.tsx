import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { authService } from '../../services/authService';
import styles from './AuthPages.module.scss';

export const Register: React.FC = () => {
  const navigate = useNavigate();
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [password2, setPassword2] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (authService.isAuthenticated()) {
      navigate('/', { replace: true });
    }
  }, [navigate]);

  const handleSubmit = async (e: React.FormEvent): Promise<void> => {
    e.preventDefault();
    setError('');
    if (password !== password2) {
      setError('Пароли не совпадают');
      return;
    }
    if (password.length < 6) {
      setError('Пароль не короче 6 символов');
      return;
    }
    setLoading(true);
    try {
      await authService.register(username.trim(), password);
      navigate('/', { replace: true });
    } catch (err: unknown) {
      const data = (err as { response?: { data?: Record<string, string> | { message?: string } } })?.response
        ?.data;
      if (data && typeof data === 'object' && 'message' in data && typeof data.message === 'string') {
        setError(data.message);
      } else if (data && typeof data === 'object' && 'username' in data) {
        setError(String((data as { username?: string }).username));
      } else {
        setError('Не удалось зарегистрироваться');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className={styles.wrap}>
      <div className={styles.card}>
        <h1 className={styles.title}>Регистрация</h1>
        <p className={styles.sub}>
          Укажите логин и пароль. Данные мигранта заполните в профиле после входа.
        </p>

        <form onSubmit={(e) => void handleSubmit(e)} className={styles.form}>
          {error && <div className={styles.error}>{error}</div>}
          <div className={styles.field}>
            <label htmlFor="regUser">Логин</label>
            <input
              id="regUser"
              className={styles.input}
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              autoComplete="username"
              minLength={3}
              required
            />
          </div>
          <div className={styles.field}>
            <label htmlFor="regPwd">Пароль</label>
            <input
              id="regPwd"
              type="password"
              className={styles.input}
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              autoComplete="new-password"
              minLength={6}
              required
            />
          </div>
          <div className={styles.field}>
            <label htmlFor="regPwd2">Пароль ещё раз</label>
            <input
              id="regPwd2"
              type="password"
              className={styles.input}
              value={password2}
              onChange={(e) => setPassword2(e.target.value)}
              autoComplete="new-password"
              required
            />
          </div>
          <button type="submit" className={styles.submit} disabled={loading}>
            {loading ? 'Регистрация…' : 'Зарегистрироваться'}
          </button>
        </form>

        <p className={styles.footer}>
          Уже есть аккаунт? <Link to="/login">Войти</Link>
        </p>
      </div>
    </div>
  );
};

