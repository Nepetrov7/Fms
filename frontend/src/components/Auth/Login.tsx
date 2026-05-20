import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { authService } from '../../services/authService';
import styles from './AuthPages.module.scss';

export const Login: React.FC = () => {
  const navigate = useNavigate();
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
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
    setLoading(true);
    try {
      await authService.login(username.trim(), password);
      navigate('/', { replace: true });
    } catch (err: unknown) {
      const msg =
        (err as { response?: { data?: { message?: string } } })?.response?.data?.message ??
        'Неверный логин или пароль';
      setError(typeof msg === 'string' ? msg : 'Ошибка входа');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className={styles.wrap}>
      <div className={styles.card}>
        <h1 className={styles.title}>Вход</h1>
        <p className={styles.sub}>Войдите, чтобы заполнить профиль и открыть дорожную карту</p>

        <form onSubmit={(e) => void handleSubmit(e)} className={styles.form}>
          {error && <div className={styles.error}>{error}</div>}
          <div className={styles.field}>
            <label htmlFor="login">Логин</label>
            <input
              id="login"
              className={styles.input}
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              autoComplete="username"
              required
            />
          </div>
          <div className={styles.field}>
            <label htmlFor="pwd">Пароль</label>
            <input
              id="pwd"
              type="password"
              className={styles.input}
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              autoComplete="current-password"
              required
            />
          </div>
          <button type="submit" className={styles.submit} disabled={loading}>
            {loading ? 'Вход…' : 'Войти'}
          </button>
        </form>

        <p className={styles.footer}>
          Нет аккаунта? <Link to="/register">Регистрация</Link>
        </p>
      </div>
    </div>
  );
};

